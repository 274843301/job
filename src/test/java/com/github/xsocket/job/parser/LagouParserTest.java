package com.github.xsocket.job.parser;

import java.net.URL;
import java.util.Iterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import com.github.xsocket.job.util.WordUtils;

public class LagouParserTest {

  @Test
  public void testLagou() throws Exception {
    URL url = LagouParserTest.class.getResource("/lagou.doc");

    String html = WordUtils.parseDoc2Html(url.openStream());
    
    Document doc = Jsoup.parse(html);
    
    String name = doc.select("p.p3>span.s1").text();
    System.out.println(name);
    
    Elements elems = doc.select("p.p5>span.s6");
    Iterator<Element> iter = elems.iterator();
    while(iter.hasNext()) {
      String text = iter.next().text();
      if(text.contains("·")) {
        System.out.println(text.split("·")[0]);
        System.out.println(text.split("·")[1]);
      } else {
        System.out.println(text);
      }
    }
    
    System.out.println(doc.select("p.p4>span.s6").first().text());
    System.out.println(doc.select("p.p4>a>span.s9").first().text());
    
    Elements spans = doc.select("p span");
    for(Element span : spans) {
      System.err.println(span.text());
    }
    
  }
  
}
