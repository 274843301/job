package com.github.xsocket.job.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Calendar;

import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.apache.http.client.fluent.Request;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.github.xsocket.job.AbstractResumeParser;
import com.github.xsocket.job.Resume;
import com.github.xsocket.job.ResumeParser;
import com.github.xsocket.job.resume.ZhilianResume;

/**
 * 智联邮件简历解析工具。
 * @author MWQ
 *
 */
public class ZhilianEmailResumeParser extends AbstractResumeParser implements ResumeParser {
  
  private static final String COMPANY_SPLIT = " ";
  private static final String BASIC_INFO_SPLIT = "\\|";

  @Override
  public boolean canParse(File file) {
    if(file == null) {
      return false;
    } else {
      String name = file.getName();
      return (name.contains("Zhaopin.com") || name.startsWith("智联招聘")) && name.endsWith(".eml");
    }
  }

  @Override
  public Resume parse(File file) throws Exception {
    ZhilianResume resume = new ZhilianResume();
    String fileName = file.getName();
    
    if(fileName.contains("Zhaopin.com")) {
      resume.setJob(intercept(fileName, "应聘 ", "-"));
    } else {
      // 解析已经解析完成的文件
      int start = fileName.lastIndexOf("-") + 1;
      int end = fileName.lastIndexOf(".eml");
      if(start > 0 && end > start) {
        resume.setJob(fileName.substring(start, end));
      }
    }
    
    
    Document doc = parse2Html(file);        
    
    /*
      解析样例如下的数据：
      
    董文朋
男|4年工作经验|1992年5月|未婚 现居住于北京-通州区|本科|湖北-孝感户口
..... 其他信息
2014/02 - 2015/05 辽河油田泰利达有限公司 （1年3个月）
。。。。。。 其他信息
2008/09 - 2012/07 北京国际经贸研修学院  信息管理与信息系统  统招  本科
    
         */
    
    Elements sections = doc.select("table table table table tr");
    // 下一行要解析的数据类型
    String nextSection = "姓名";
    for(Element section : sections) {
      String text = section.text(); 
      if(isNullOrEmpty(text)) {
        continue;
      }
      
      if("姓名".equals(nextSection)) {
        resume.setName(text.trim());
        nextSection = "综合";
      } else if("综合".equals(nextSection)) {
        parseBasicInfo(resume, text);
        nextSection = "上家公司";
      } else if("上家公司".equals(nextSection)) {
        // 判断是否是真是的公司信息
        if(text.length() < 20) {
          continue;
        } else if(text.charAt(4) != '/') {
          continue;
        } else if(text.charAt(8) != '-') {
          continue;
        }        
        
        String[] infos = text.split(COMPANY_SPLIT);
        if(infos.length>=4) {
          resume.setCompany(infos[3]);
        }
        nextSection = "毕业院校";
      } else if("毕业院校".equals(nextSection)) {
        for(String edu : EDUCATIONS.keySet()) {
          if(text.contains(edu)) {
            resume.setEducation(edu);
            String[] infos = text.split(COMPANY_SPLIT);
            if(infos.length>=4) {
              resume.setSchool(infos[3]);
            }
            nextSection = "结束";
            break;
          }
        }
        if("结束".equals(nextSection)) {
          break;
        }
      }
    }
    
    tryFetchContact(resume, doc);
    
    return resume;
  }
  
  protected void tryFetchContact(ZhilianResume resume, Document doc) {
    final String SPLIT1 = "url=";
    final String SPLIT2 = "ldparam=";
    Elements as = doc.select("table table table table tr td a"); 
    for(Element elem : as) {
      String href = elem.attr("href");
      if(href.contains(SPLIT2) && href.contains(SPLIT1)) {
        String url = href.substring(href.lastIndexOf(SPLIT1) + SPLIT1.length(), href.length());
        String content;
        try {
          content = Request.Get(url).execute().returnContent().asString();
          Document doc2 = Jsoup.parse(content);
          Elements infos = doc2.select("div.login_content p a");
          resume.setName(infos.get(0).text());
          resume.setPhone(infos.get(1).text());
          resume.setMail(infos.get(2).text());
        } catch (Exception e) {
          e.printStackTrace(System.err);
        } 
        
        return;
      }
    }
  }
  
  protected void parseBasicInfo(ZhilianResume resume, String text) {
    String[] values = text.split(BASIC_INFO_SPLIT);
    
    String next = null;
    
    for(String value : values) {
      String str = value.trim();
      if(str.contains("男")) {
        resume.setSex("男");
        next = "工作经验";
      } else if(str.contains("女")) {
        resume.setSex("女");
        next = "工作经验";
      } else if(str.contains("工作经验")) {
        int index = str.indexOf("年");
        if(index > 0) {
          resume.setWorkDuration(str.substring(0, index));
        } else {
          resume.setWorkDuration(str.substring(0, str.indexOf("工作经验")));
        }
        next = "生日";
      } else if("生日".equals(next)){
        int index = str.indexOf("年");
        if(index > 0) {
          resume.setBirthday(str);
          try {
            int birthYear = Integer.parseInt(str.substring(0, index));
            Integer age = Calendar.getInstance().get(Calendar.YEAR) - birthYear;
            resume.setAge(age.toString());
          } catch(Exception e) {
            // 解析出生年份失败，无法计算年龄
          }
        }
        next = "城市";
      } else if("城市".equals(next)){
        int index = str.indexOf("现居住于");
        if(index > 0) {
          resume.setCity(str.substring(index + 1, str.length()));
        }
        next = "学历";
      } else if("学历".equals(next)) {
        if(EDUCATIONS.containsKey(str)) {
          resume.setEducation(str);
        }
      }
    }
  }
  
  protected Document parse2Html(File file) throws Exception {
    InputStream in = new FileInputStream(file);

    Session mailSession = Session.getDefaultInstance(System.getProperties(), null);

    MimeMessage msg = new MimeMessage(mailSession, in);
    
    Multipart part = (Multipart) msg.getContent();
    String html = "";
    for(int i = 0; i < part.getCount(); i++) {
      BodyPart body = part.getBodyPart(i);
      if(body.getContentType().startsWith("text/html")) {
        html = body.getContent().toString();
        break;
      }
    }
    
    return Jsoup.parse(html);
  }
}
