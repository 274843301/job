package com.github.xsocket.job.parser;

import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import com.github.xsocket.job.util.WordUtils;

public class LagouParserTest {

  @Test
  public void testLagou() throws Exception {
    URL url = LagouParserTest.class.getResource("/lagou5.doc");
    
    System.out.println("男 ︳硕士 ︳ 6年工作经验 ︳ 北京".indexOf("︳"));

    String html = WordUtils.parseDoc2Html(url.openStream());
    //System.out.println(html);
    
    Document doc = Jsoup.parse(html);
    /*
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
    */
    
    /*
骆波涛
高级软件工程师（架构师） · 北京嗨购电子商务科技有限公司
男 ︳硕士 ︳ 6年工作经验 ︳ 北京
18612119582 ︳botaoluo@gmail.com
     */
    Elements spans = doc.select("p");
    for(Element span : spans) {
      System.err.println(span.text());
    }
    
  }
  
}
