package com.github.xsocket.job.parser;

import java.io.File;
import java.util.Calendar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.github.xsocket.job.AbstractResumeParser;
import com.github.xsocket.job.Resume;
import com.github.xsocket.job.ResumeParser;
import com.github.xsocket.job.resume.LagouResume;
import com.github.xsocket.job.util.WordUtils;

/**
 * 拉钩简历解析工具。
 * @author MWQ
 *
 */
public class LagouResumeParser extends AbstractResumeParser implements ResumeParser {
  
  private static final String JOB_COMPANY_SPLIT = "·";
  private static final String BASIC_INFO_SPLIT = "︳";
  
  @Override
  public String getName() {
    return "拉勾";
  }
  
  @Override
  public boolean canParse(File file) {
    if(file == null) {
      return false;
    } else {
      String name = file.getName();
      return name.endsWith("的简历.doc") || (name.startsWith("拉勾-") && name.endsWith(".doc"));
    }
  }

  @Override
  public Resume parse(File file) throws Exception {
    String os = System.getProperty("os.name");
    
    String html = WordUtils.parseDoc2Html(file, os.startsWith("Windows") ? "GBK" : "UTF-8");
    //String html = WordUtils.parseDoc2Html(file);
    
    Document doc = Jsoup.parse(html);
    
    /*
      解析样例如下的数据：
      
    骆波涛
    高级软件工程师（架构师） · 北京嗨购电子商务科技有限公司
    男 ︳硕士 ︳ 6年工作经验 ︳ 北京
    18612119582 ︳botaoluo@gmail.com
    
    
    教育经历
    中软培训中心 
    
    其他  ·   java培训
    2009年毕业 
    
    
    辽宁工程技术大学
    
    本科 · 计算机科学与技术
    2005年毕业 
    
         */
    LagouResume resume = new LagouResume();
    Elements sections = doc.getElementsByTag("p");
    boolean startedBasic = false;
    boolean startEducation = false;
    String nextEducation = null;
    for(Element section : sections) {
      String text = section.text(); 
      if(isNullOrEmpty(text)) {
        continue;
      }
      
      if(!startedBasic) {
        // 第一个还未开始的非空字符串是：姓名
        resume.setName(text);
        startedBasic = true;
      } else if("教育经历".equals(text)) {
        startEducation = true;
        nextEducation = "学校";
      } else if(!startEducation) {
        if(text.contains(JOB_COMPANY_SPLIT)) {
          parseJobAndCompany(resume, text);
        } else if(text.indexOf(BASIC_INFO_SPLIT) >= 0) {
          // 其他基本信息
          parsePersonalInfo(resume, text);
        }
      } else if(startEducation) {
        // 解析教育经历
        if("学校".equals(nextEducation)) {
          resume.setSchool(text);
          nextEducation = "学历";
        } else if("学历".equals(nextEducation)) {
          int index = text.indexOf(JOB_COMPANY_SPLIT);
          if(index > 0) {
            String edu = text.substring(0, index).trim();
            for(String e : EDUCATIONS.keySet()) {
              if(edu.startsWith(e)) {
                edu = e;
                break;
              }
            }
            resume.setEducation(edu);
          } else {
            resume.setEducation("");
          }
          nextEducation = "毕业时间";
        } else if("毕业时间".equals(nextEducation)) {
          if(resume.getSchool() != null && !EDUCATIONS.containsKey(resume.getEducation())) {
            // 学历未设置的情况，假设是本科
            resume.setEducation("本科");
          }
          
          if(EDUCATIONS.containsKey(resume.getEducation())) {
            // 解析成功则退出
            int index = text.indexOf("年");
            if(index > 0) {
              try {
                // 解析毕业年份
                int end = Integer.parseInt(text.substring(0, index));
                Integer age = Calendar.getInstance().get(Calendar.YEAR) - end + EDUCATIONS.get(resume.getEducation());
                resume.setAge(age.toString());
              } catch(Exception e) {}
            }
            break;
          } 
          
          resume.setSchool("");
          resume.setEducation("");
          resume.setAge("");
          nextEducation = "学校";
        }
      }
    }
    
    return resume;
  }
  
  protected void parseJobAndCompany(LagouResume resume, String jobAndCompany) {
    int index = jobAndCompany.indexOf(JOB_COMPANY_SPLIT);
    int length = jobAndCompany.length();
    if(index < 0) {
      resume.setJob(jobAndCompany);
    } else if(index == 0 && length >= 2) {
      resume.setCompany(jobAndCompany.substring(1));
    } else if(index + 1 == length) {
      resume.setJob(jobAndCompany.substring(0, index));
    } else {
      resume.setJob(jobAndCompany.substring(0, index));
      resume.setCompany(jobAndCompany.substring(index + 1));
    }
  }

  protected void parsePersonalInfo(LagouResume resume, String text) {
    String[] values = text.split(BASIC_INFO_SPLIT);
    
    String next = null;
    
    for(String value : values) {
      String str = value.trim();
      if(str.contains("工作经验")) {
        int index = str.indexOf("年");
        if(index > 0) {
          resume.setWorkDuration(str.substring(0, index));
        } else {
          resume.setWorkDuration(str.substring(0, str.indexOf("工作经验")));
        }
        next = "城市";
      } else if(str.contains("应届")) {
        resume.setWorkDuration("0");
        next = "城市";
      } else if(str.contains("男")) {
        resume.setSex("男");
        next = "学历";
      } else if(str.contains("女")) {
        resume.setSex("女");
        next = "学历";
      } else if(str.contains("@")) {
        resume.setMail(str);
        next = null;
      } else if(str.startsWith("1") && str.length() >= 11) {
        // TODO 对电话的解析值得商榷
        resume.setPhone(str);
        next = null;
      } else if("城市".equals(next)){
        resume.setCity(str);
        next = null;
      } else if("学历".equals(next)){
        resume.setEducation(str);
        next = null;
      }
    }
  }
}
