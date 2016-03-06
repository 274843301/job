package com.github.xsocket.job.parser;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.github.xsocket.job.AbstractResumeParser;
import com.github.xsocket.job.Resume;
import com.github.xsocket.job.ResumeParser;
import com.github.xsocket.job.resume.ZhilianResume;

public class ZhilianResumeParser extends AbstractResumeParser implements ResumeParser {

  @Override
  public boolean canParse(File file) {
    return file == null ? false : file.getName().startsWith("智联招聘");
  }

  @Override
  public Resume parse(File file) throws Exception {
    
    Document doc = parse2Html(file);
    
    String filename = file.getName();
    String[] params = filename.split("_");
    ZhilianResume resume = new ZhilianResume();
    resume.setName(params[1]);
    resume.setJob(params[2]);
    
    // 解析其他数据
    Elements trs = doc.select("table").get(1).select("tr");
    for(Element tr : trs) {
      String text = tr.text();
      if(text.contains("工作经验")) {
        // 男    38岁(1978年12月)    13年工作经验    本科 现居住地：北京 | 户口：保定 
        parseBasicInfo(resume, text);
      } else if(text.startsWith("手机：")) {
        // 手机：18600904162 E-mail：18600904162@163.com
        parseContactMethod(resume, text);
      } 
      // 其他信息忽略
    }
    
    return resume;
  }
  
  // 男    38岁(1978年12月)    13年工作经验    本科 现居住地：北京 | 户口：保定 
  protected void parseBasicInfo(ZhilianResume resume, String basicInfo) {
    final String seprator = "    ";
    String[] msg = basicInfo.split(seprator);
    for(String info : msg) {
      if(info.contains("男")) {
        resume.setSex("男");
      } else if(info.contains("女")) {
        resume.setSex("女");
      } else if(info.contains("岁")) {
        int index = info.indexOf("岁");
        resume.setAge(info.substring(0, index));
        resume.setBirthday(trim(info.substring(index + 1), "(",")","（","）"));
      } else if(info.contains("工作经验")) {
        resume.setWorkDuration(info);     
      } else if(info.contains("现居住地：")) {
        resume.setEducation(intercept(info, null, "现居住地："));
        resume.setCity(intercept(info, "现居住地：", "|"));
      }
    } 
  }
  
  //  手机：18600904162 E-mail：18600904162@163.com
  protected void parseContactMethod(ZhilianResume resume, String contact) {
    resume.setPhone(intercept(contact, "手机：", "E-mail："));
    resume.setMail(intercept(contact, "E-mail：", null));
  }
  
  protected Document parse2Html(File file) throws Exception {
    String html = FileUtils.readFileToString(file, "UTF-8");
    return Jsoup.parse(html);
  }

}
