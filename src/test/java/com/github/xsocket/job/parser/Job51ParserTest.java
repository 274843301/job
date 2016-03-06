package com.github.xsocket.job.parser;

import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Test;

public class Job51ParserTest {
  
  private static final Set<String> KEY_WORDS = new HashSet<String>();
  static {
    KEY_WORDS.add("居住地：");
    KEY_WORDS.add("户　口：");
    KEY_WORDS.add("地　址：");
    KEY_WORDS.add("电　话：");
    KEY_WORDS.add("E-mail：");
    KEY_WORDS.add("公　司：");
    KEY_WORDS.add("行　业：");
    KEY_WORDS.add("职　位：");
    KEY_WORDS.add("学　历：");
    KEY_WORDS.add("专　业：");
    KEY_WORDS.add("学　校：");
  }
  
  @Test
  public void temp51Job() throws Exception {
    
    Document doc = parse51JobResume();
    
    String name = doc.select("table table table tr span").first().text();
    System.out.println(name);
    
    // 应聘职位：架构师（北京） 应聘公司：北京云途数字营销顾问有限公司 投递时间：2016-02-27 更新时间：2016-03-01
    String start = "应聘职位：";
    String end = "应聘公司：";
    String title = doc.select("table>tbody>tr>td").first().text();
    System.out.println(title.substring(start.length(), title.indexOf(end)).trim());
    
    String detail = doc.select("table table table table span>b").first().text();
    String[] ds = detail.replaceAll("\\|", " ~!~ ").split("~!~");
    for(String str : ds) {
      System.out.println(str.trim());
    }
    
    String currentKeyWord = null;
    Elements elems = doc.select("table table table td");
    for(Element elem : elems) {
      String text = elem.text();
      if(KEY_WORDS.contains(text)) {
        currentKeyWord = text;
      } else if(currentKeyWord != null) {
        System.err.println(currentKeyWord + text);
        currentKeyWord = null;
      }
    }
  }
  
  
  private Document parse51JobResume() throws Exception {
    URL url = Job51ParserTest.class.getResource("/job51.doc");
    List<String> lines = IOUtils.readLines(url.openStream());
    //10年以上工作经验 | 男 |  42岁（1974年1月6日）
    //1年工作经验 | 女 | 24岁(1991年11月 28日 )
    String state = "";
    String charset = null;
    StringBuilder sb = new StringBuilder();
    for(String line : lines) {
      if(line.startsWith("Content-Type:text/html;charset=")) {
        charset = line.substring("Content-Type:text/html;charset=".length()).replaceAll("\"", "");
        state = "ready";
      } else if(line.startsWith("Content-Type: text/html;charset=")) {
        charset = line.substring("Content-Type: text/html;charset=".length()).replaceAll("\"", "");
        state = "ready";
      } else if(state.equals("ready") && line.length() == 76) {
        state = "go";
        sb.append(line);
      } else if(state.equals("go") && line.length() > 0) {
        sb.append(line);
      } else if(state.equals("go") && line.length() == 0) {
        state = "over";
        break;
      }
    }
    
    // 未成功解析则返回null
    if(!"over".equals(state)) {
      Assert.fail("未成功解析51job简历文件");
    }
    
    // 解析成功, 开始进行数据读取
    String html = new String(Base64.decodeBase64(sb.toString()), charset);
    return Jsoup.parse(html);
  }
  
  
}
