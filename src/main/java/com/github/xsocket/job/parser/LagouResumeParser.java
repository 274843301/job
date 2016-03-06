package com.github.xsocket.job.parser;

import java.io.File;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.github.xsocket.job.Resume;
import com.github.xsocket.job.ResumeParser;
import com.github.xsocket.job.resume.LagouResume;
import com.github.xsocket.job.util.JsoupUtils;
import com.github.xsocket.job.util.WordUtils;

/**
 * 拉钩简历解析工具。
 * @author MWQ
 *
 */
public class LagouResumeParser implements ResumeParser {
  
  private static final String JOB_COMPANY_SPLIT = "·";

  @Override
  public boolean canParse(File file) {
    return file == null ? false : file.getName().endsWith("的简历.doc");
  }

  @Override
  public Resume parse(File file) throws Exception {
    String html = WordUtils.parseDoc2Html(file);
    Document doc = Jsoup.parse(html);

    LagouResume resume = new LagouResume();
    resume.setName(JsoupUtils.getText(doc, "p.p3>span.s1"));
    resume.setPhone(JsoupUtils.getText(doc, "p.p4>span.s6"));
    resume.setMail(JsoupUtils.getText(doc, "p.p4>a>span.s9"));
    
    Elements elems = doc.select("p.p5>span.s6");
    resume.setSex(JsoupUtils.getText(elems, 1));
    resume.setEducation(JsoupUtils.getText(elems, 2));
    resume.setWorkDuration(JsoupUtils.getText(elems, 3));
    resume.setCity(JsoupUtils.getText(elems, 4));
    
    String jobAndCompany = JsoupUtils.getText(elems, 0).replaceAll(" ", "");
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
    
    return resume;
  }

}
