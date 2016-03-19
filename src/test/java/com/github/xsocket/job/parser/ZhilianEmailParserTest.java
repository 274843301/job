package com.github.xsocket.job.parser;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.apache.http.client.fluent.Request;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

public class ZhilianEmailParserTest {
  
  protected static Map<String, Integer> EDUCATIONS = new LinkedHashMap<String, Integer>();
  static {
    EDUCATIONS.put("博士", new Integer(30));
    EDUCATIONS.put("硕士", new Integer(26));
    EDUCATIONS.put("本科", new Integer(23));
    EDUCATIONS.put("大专", new Integer(22));
    EDUCATIONS.put("高中", new Integer(19));
  }

  @Test
  public void testZhaopinEml() throws Exception {

    URL url = LiepinParserTest.class.getResource("/zhaopin3.eml");

    Session mailSession = Session.getDefaultInstance(System.getProperties(), null);

    MimeMessage msg = new MimeMessage(mailSession, url.openStream());
    
    Multipart part = (Multipart) msg.getContent();
    String html = "";
    for(int i = 0; i < part.getCount(); i++) {
      BodyPart body = part.getBodyPart(i);
      if(body.getContentType().startsWith("text/html")) {
        html = body.getContent().toString();
        break;
      }
    }
    
    System.err.println(html);
    
    Document doc = Jsoup.parse(html);
    Elements trs = doc.select("table table table table tr");
    for(Element elem : trs) {
      System.out.println(elem.text());
    }
    
    // 联系方式访问连接
    Elements as = doc.select("table table table table tr td a"); 
    for(Element elem : as) {
      System.err.println(elem.attr("href"));
    }
    
    String href = as.get(0).attr("href");
    final String SPLIT = "url=";
    String addr = href.substring(href.lastIndexOf(SPLIT) + SPLIT.length(), href.length());
    
    String content = Request.Get(addr).execute().returnContent().asString();
    Document doc2 = Jsoup.parse(content);
    Elements infos = doc2.select("div.login_content>p");
    for(Element elem : infos) {
      System.out.println(elem.text());
    }
    
  }
}
