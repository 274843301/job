package com.github.xsocket.job.parser;

import java.io.File;

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
  public boolean canParse(File file) {
    if(file == null) {
      return false;
    } else {
      String name = file.getName();
      return name.endsWith("的简历.doc") || (name.startsWith("拉钩-") && name.endsWith(".doc"));
    }
  }

  @Override
  public Resume parse(File file) throws Exception {
    String html = WordUtils.parseDoc2Html(file);
    Document doc = Jsoup.parse(html);
    
    /*
      解析样例如下的数据：
      
    骆波涛
    高级软件工程师（架构师） · 北京嗨购电子商务科技有限公司
    男 ︳硕士 ︳ 6年工作经验 ︳ 北京
    18612119582 ︳botaoluo@gmail.com
    
         */
    LagouResume resume = new LagouResume();
    Elements sections = doc.getElementsByTag("p");
    boolean started = false;
    
    for(Element section : sections) {
      String text = section.text();
      // 空字符串的话判断是否结束
      if(isNullOrEmpty(text)) {
        if(started) {
          break;
        } else {
          continue;
        }
      }
      
      if(!started) {
        // 第一个还未开始的非空字符串是：姓名
        resume.setName(text);
        started = true;
      } else {
        if(text.contains(JOB_COMPANY_SPLIT)) {
          parseJobAndCompany(resume, text);
        } else if(text.indexOf(BASIC_INFO_SPLIT) > 0) {
          // 其他基本信息
          parsePersonalInfo(resume, text);
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
        resume.setWorkDuration(str);  
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
