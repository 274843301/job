package com.github.xsocket.job.parser;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

public class LiepinParserTest {
  
  private static final Set<String> KEY_WORDS = new HashSet<String>();
  static {
    // 主动应聘职位： 市场推广经理 | 2016-02-29 12:24:27 简历编号信息： 00901573 简历更新时间： 2016-03-01 08:54:35
    //KEY_WORDS.add("主动应聘职位：");
    KEY_WORDS.add("姓名：");
    KEY_WORDS.add("性别：");
    KEY_WORDS.add("手机号码：");
    KEY_WORDS.add("年龄：");
    KEY_WORDS.add("电子邮件：");
    KEY_WORDS.add("教育程度：");
    KEY_WORDS.add("工作年限：");
    KEY_WORDS.add("所在地：");
  }
  
  @Test
  public void testLiepinParser() throws Exception {
    
    Document doc = parseLiepinResume();
    
    String currentKeyWord = null;
    Elements elems = doc.select("table td");
    for(Element elem : elems) {
      String text = elem.text();
      if(KEY_WORDS.contains(text)) {
        currentKeyWord = text;
      } else if(currentKeyWord != null) {
        System.err.println(currentKeyWord + text);
        currentKeyWord = null;
      }
    }
    
    System.out.println(intercept(
        "主动应聘职位： 市场推广经理 | 2016-02-29 12:24:27 简历编号信息： 00901573 简历更新时间： 2016-03-01 08:54:35", "主动应聘职位：", "|"));
    
  }
  
  
  private Document parseLiepinResume() throws Exception {
    URL url = LiepinParserTest.class.getResource("/liepin.doc");
    
    
    // 解析成功, 开始进行数据读取
    String html = IOUtils.toString(url.openStream(), "unicode");
    //System.err.println(html);
    return Jsoup.parse(html);
  }
  
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
  
}
