package com.github.xsocket.job.parser;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

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
    
    Document doc = Jsoup.parse(html);
    Elements trs = doc.select("table table table table tr");
    for(Element elem : trs) {
      System.out.println(elem.text());
    }
    
    
    for(String edu : EDUCATIONS.keySet()) {
      System.err.println(edu);
    }
    
    for(String str : "2014/02 - 至今 辽河油田泰利达有限公司 （1年3个月）".split(" ")) {
      System.err.println(str);
    }
    
  }
}
