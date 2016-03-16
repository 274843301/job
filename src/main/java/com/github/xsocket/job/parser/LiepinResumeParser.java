package com.github.xsocket.job.parser;

import static com.github.xsocket.job.resume.LiepinResume.KEY_JOB;
import static com.github.xsocket.job.resume.LiepinResume.KEY_WORDS;
import static com.github.xsocket.job.resume.LiepinResume.KEY_WORK_DURATION;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.github.xsocket.job.AbstractResumeParser;
import com.github.xsocket.job.Resume;
import com.github.xsocket.job.ResumeParser;
import com.github.xsocket.job.resume.LiepinResume;

public class LiepinResumeParser extends AbstractResumeParser implements ResumeParser {

  @Override
  public boolean canParse(File file) {
    return file == null ? false : file.getName().contains("猎聘网") ;
  }

  @Override
  public Resume parse(File file) throws Exception {
    
    Document doc = parse2Html(file);
    
    LiepinResume resume = new LiepinResume();
    
    String currentKeyWord = null;
    Elements elems = doc.select("table td");
    for(Element elem : elems) {
      String text = elem.text();
      
      if(KEY_WORDS.contains(text)) {
        currentKeyWord = text;
      } else if(currentKeyWord != null) {
        resume.set(currentKeyWord, text);
        currentKeyWord = null;
      } else if(text.startsWith(KEY_JOB)) {
        resume.set(KEY_JOB, intercept(text, KEY_JOB, " | "));
      }
    }
    
    // 后续处理
    String workDuration = resume.getWorkDuration();
    if(workDuration != null && workDuration.contains("年")) {
      resume.set(KEY_WORK_DURATION, workDuration.substring(0, workDuration.indexOf("年")).trim());
    }
    
    return resume;
  }
  
  protected Document parse2Html(File file) throws Exception {
    String html = FileUtils.readFileToString(file, "unicode");
    return Jsoup.parse(html);
  }

}
