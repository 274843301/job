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

public class ZhilianParserTest {
  
  private static final Set<String> KEY_WORDS = new HashSet<String>();
  static {
    KEY_WORDS.add("应聘职位：");
    KEY_WORDS.add("期望工作地区：");
    KEY_WORDS.add("期望月薪：");
    KEY_WORDS.add("手机号码：");
    KEY_WORDS.add("年龄：");
    KEY_WORDS.add("电子邮件：");
    KEY_WORDS.add("教育程度：");
    KEY_WORDS.add("工作年限：");
    KEY_WORDS.add("所在地：");
  }
  
  @Test
  public void testZhilianParser() throws Exception {
    
    Document doc = parseZhilianResume();
    
    Elements trs = doc.select("table").get(1).select("tr");
    for(Element elem : trs) {
      System.out.println(elem.text());
    }
    
    String[] list = "男    38岁(1978年12月)    13年工作经验    本科 现居住地：北京 | 户口：保定 ".split("    ");
    for(String str : list) {
      System.err.println(str);
    }
    
    
    String currentKeyWord = null;
    Elements elems = doc.select("table td");
    for(Element elem : elems) {
      String text = elem.text();
      //System.out.println(text);
      if(KEY_WORDS.contains(text)) {
        currentKeyWord = text;
      } else if(currentKeyWord != null) {
        System.out.println(currentKeyWord + text);
        currentKeyWord = null;
      }
    }
    
  }
  
  
  private Document parseZhilianResume() throws Exception {
    URL url = ZhilianParserTest.class.getResource("/zhilian.doc");
    
    
    // 解析成功, 开始进行数据读取
    String html = IOUtils.toString(url.openStream(), "UTF-8");
   // System.err.println(html);
    return Jsoup.parse(html);
  }
  
  
}
