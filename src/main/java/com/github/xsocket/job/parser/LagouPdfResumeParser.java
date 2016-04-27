package com.github.xsocket.job.parser;

import java.io.File;
import java.io.FileInputStream;
import java.util.Calendar;

import com.github.xsocket.job.AbstractResumeParser;
import com.github.xsocket.job.Resume;
import com.github.xsocket.job.ResumeParser;
import com.github.xsocket.job.resume.LagouResume;
import com.github.xsocket.job.util.PdfUtils;

/**
 * 拉钩简历解析工具。
 * @author MWQ
 *
 */
public class LagouPdfResumeParser extends AbstractResumeParser implements ResumeParser {
  
  private static final String JOB_COMPANY_SPLIT = "·";
  private static final String BASIC_INFO_SPLIT = "︳";
  
  @Override
  public String getName() {
    return "拉勾PDF";
  }
  
  @Override
  public boolean canParse(File file) {
    if(file == null) {
      return false;
    } else {
      String name = file.getName();
      return name.endsWith(".pdf") || (name.startsWith("拉勾-") && name.endsWith(".pdf"));
    }
  }

  @Override
  public Resume parse(File file) throws Exception {
    
    /*
      解析样例如下的数据：
      
    张波   
  男  ︳   28岁  ︳本科 ︳ 2年工作经验  ︳ 北京    
18513353690 ︳18513353690@163.com   
 
 
 工作经历    
软通动力（外派：百度金融事业部）    
Java工程师   2015.07-至今   
 
北京科蓝软件系统股份有限公司    
java工程师   2014.08-2015.06   
 
 
 教育经历   
 
河北工程大学   
本科 · 地理信息系统  2013年毕业
    
         */
    LagouResume resume = new LagouResume();
    String[] lines = PdfUtils.parsePdf2Text(new FileInputStream(file)).split("\n");
    
    boolean startedBasic = false;
    boolean startEducation = false;
    boolean startWork = false;
    String nextEducation = null;
    String nextWork = null;
    for(String text : lines) {
      if(isNullOrEmpty(text)) {
        continue;
      }
      
      if(!startedBasic) {
        // 第一个还未开始的非空字符串是：姓名
        resume.setName(text.trim());
        startedBasic = true;
      } else if(text.contains("工作经历")) {
        startEducation = false;
        startWork = true;
        nextWork = "上家公司";
      } else if(text.contains("教育经历")) {
        startEducation = true;
        startWork = false;
        nextEducation = "学校";
      } else if(!startEducation && !startWork) {
        if(text.contains(JOB_COMPANY_SPLIT)) {
          parseJobAndCompany(resume, text);
        } else if(text.indexOf(BASIC_INFO_SPLIT) >= 0) {
          // 其他基本信息
          parsePersonalInfo(resume, text);
        }
      } else if(startWork) {
        if("上家公司".equals(nextWork)) {
          resume.setCompany(text);
          nextWork = "上家岗位";
        } else if("上家岗位".equals(nextWork)) {
          int blankIndex = text.indexOf(" ");
          if(blankIndex > 0) {
            resume.setJob(text.substring(0, blankIndex));
          }
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
          
          if(resume.getSchool() != null && !EDUCATIONS.containsKey(resume.getEducation())) {
            // 学历未设置的情况，假设是本科
            resume.setEducation("本科");
          }
          
          if(EDUCATIONS.containsKey(resume.getEducation())) {
            // 解析成功则退出
            int indexN = text.indexOf("年");
            if(indexN > 0) {
              try {
                // 解析毕业年份
                int end = Integer.parseInt(text.substring(indexN - 4, indexN));
                Integer age = Calendar.getInstance().get(Calendar.YEAR) - end + EDUCATIONS.get(resume.getEducation());
                resume.setAge(age.toString());
              } catch(Exception e) {}
            }
            break;
          } 
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
