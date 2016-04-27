package com.github.xsocket.job.parser;

import java.net.URL;

import org.junit.Test;

import com.github.xsocket.job.util.PdfUtils;

public class LagouPdfParserTest {

  @Test
  public void testLagou() throws Exception {
    URL url = LagouPdfParserTest.class.getResource("/lagou.pdf");
  //  String[] strs = "男 ︳本科 ︳  ︳ 北京".split("︳");
  //  for(String str : strs) {
    //  System.out.println(str);
  //  }

    String text = PdfUtils.parsePdf2Text(url.openStream());
    String[] lines = text.split("\n");
    for(int i = 1; i <= lines.length; i++) {
      System.out.println(i + ": " + lines[i-1]);
    }
  }
  
}
