package com.github.xsocket.job;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractResumeParser implements ResumeParser {
  
  // 通过教育经历来判断年龄
  protected static Map<String, Integer> EDUCATIONS = new HashMap<String, Integer>();
  static {
    EDUCATIONS.put("本科", new Integer(23));
    EDUCATIONS.put("硕士", new Integer(26));
    EDUCATIONS.put("博士", new Integer(30));
    EDUCATIONS.put("大专", new Integer(22));
    EDUCATIONS.put("高中", new Integer(19));
  }

  /**
   * 从 text 中截取 start和end 字符之间的字符串。
   * @param text
   * @param start
   * @param end
   * @return
   */
  protected String intercept(String text, String start, String end) {
    int head = start == null ? -1 : text.indexOf(start);
    int tail = end == null ? -1 : text.indexOf(end);
    int begin = head == -1 ? 0 : head + start.length();
    if(tail == -1) {
      return text.substring(begin).trim();
    } else {
      return text.substring(begin, tail).trim();
    }
  }
  
  protected String trim(String text, String... strs) {
    String result = text.trim();
    for(String str : strs) {
      if(result.startsWith(str)) {
        result = result.substring(str.length());
      } else if(result.endsWith(str)) {
        result = result.substring(0, result.length() - str.length());
      }
    }
    return result.trim();
  }
  
  protected boolean isNullOrEmpty(String text) {
    return text == null || text.length() == 0; // string.isEmpty() in Java 6
  }

}
