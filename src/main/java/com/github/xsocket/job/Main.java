package com.github.xsocket.job;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import com.github.xsocket.job.parser.Job51ResumeParser;
import com.github.xsocket.job.parser.LagouPdfResumeParser;
import com.github.xsocket.job.parser.LagouResumeParser;
import com.github.xsocket.job.parser.LiepinResumeParser;
import com.github.xsocket.job.parser.ZhilianEmailResumeParser;
import com.github.xsocket.job.parser.ZhilianResumeParser;
import com.github.xsocket.job.util.ExcelUtils;

/**
 * 启动类
 * @author MWQ
 *
 */
public class Main {
  
  private static Set<ResumeParser> PARSERS = new HashSet<ResumeParser>();
  private static ResumeParser LAGOU_PARSER = new LagouResumeParser();
  private static ResumeParser ZL_PARSER = new ZhilianEmailResumeParser();
  static {
    PARSERS.add(LAGOU_PARSER);
    PARSERS.add(new Job51ResumeParser());
    PARSERS.add(new LiepinResumeParser());
    PARSERS.add(new ZhilianResumeParser());
    PARSERS.add(ZL_PARSER);
    PARSERS.add(new LagouPdfResumeParser());
  }
  
  public static void main(String[] args) {
    String date = new SimpleDateFormat("yyyy年MM月dd日HH点mm分ss秒").format(new Date());
    System.err.println();
    System.err.println(date);
        
    // 简历所在目录
    File resumeDir = getResumeFolder();
    if(!resumeDir.exists()) {
      System.err.println("指定的简历所在目录：【" + resumeDir.getAbsolutePath() + "】 不存在！");
      System.exit(-1);
    } else if(!resumeDir.isDirectory()) {
      System.err.println("指定的简历所在目录：【" + resumeDir.getAbsolutePath() + "】 是一个文件！");
      System.exit(-1);
    } 
    System.out.println("简历所在目录：" + resumeDir.getAbsolutePath());
    boolean showDetail = "TRUE".equalsIgnoreCase(System.getProperty("job.detail"));
    
    // 简历转移配置
    boolean transfer = !"FALSE".equalsIgnoreCase(System.getProperty("job.output"));
    File outputDir = getOutputFolder();
    if(transfer) {
      if(!outputDir.exists()) {
        System.err.println("指定的简历输出目录：【" + outputDir.getAbsolutePath() + "】 不存在！");
        System.exit(-1);
      } else if(!outputDir.isDirectory()) {
        System.err.println("指定的简历输出目录：【" + outputDir.getAbsolutePath() + "】 是一个文件！");
        System.exit(-1);
      }
      System.out.println("简历文件处理：解析完毕后简历文件会剪切到 " + outputDir.getAbsolutePath() + " 并按格式重新命名");
    } else {
      System.out.println("简历文件处理：解析完毕后不做任何处理");
    }
    
    // Excel 输出位置
    File excelDir = getExcelFolder();
    if(!excelDir.exists()) {
      System.err.println("指定的Excel结果报告存放目录：【" + excelDir.getAbsolutePath() + "】 不存在！");
      System.exit(-1);
    } else if(!excelDir.isDirectory()) {
      System.err.println("指定的Excel结果报告存放目录：【" + excelDir.getAbsolutePath() + "】 是一个文件！");
      System.exit(-1);
    } 
    System.out.println("Excel结果报告存放目录：" + excelDir.getAbsolutePath());
    
    // 开始处理简历文件
    System.out.println("");
    System.out.println("============================= 开始处理简历 ==============================");
    File[] resumesFiles = resumeDir.listFiles();
    Map<File, Resume> cache = new HashMap<File, Resume>();
    for(File file : resumesFiles) {
      Resume resume = processResumeFile(file);
      if(resume != null) {
        cache.put(file, resume);
        System.out.println("成功解析简历：《" + file.getName() + "》");
        if(showDetail) {
          printResume(resume);
        }
      }
    }
    System.out.println("============================= 完成简历处理 ==============================");
    System.out.println("");
    System.out.println("========================= 开始将结果写入Excel ===========================");   
    
    if(cache.isEmpty()) {
      System.out.println("未处理任何简历...");
      System.exit(0);
    }
    
    String excelName = date + " " + cache.size() + "份简历.xls";
    
    try {
      File excel = new File(getExcelFolder(), excelName);
      ExcelUtils.outputResumeProcessResult(cache, excel);
      System.out.println("成功将结果写入Excel：《" + excel.getName() + "》");
      System.out.println("========================= 完成Excel的写入 ==========================="); 
    } catch (IOException e) {
      System.err.println("未能正确将结果写入Excel");
      e.printStackTrace(System.err);
    }
    
    if(transfer) {
      System.out.println("");
      System.out.println("========================= 开始将处理后的简历进行转移 ==========================");
      for(Map.Entry<File, Resume> entry : cache.entrySet()) {
        Resume resume = entry.getValue();
        File file = entry.getKey();
        String fileName = file.getName();
        StringBuilder sb = new StringBuilder();
        sb.append(resume.getSource()).append("-")
          .append(resume.getName()).append("-")
          .append(resume.getPhone()).append("-")
          .append(resume.getJob())
          .append(".").append(FilenameUtils.getExtension(fileName));
        String outputName = sb.toString().replace("/", "").replace("\\", "");
        try {
          FileUtils.copyFile(file, new File(outputDir, outputName));
          System.out.println("《" + fileName + "》   ===>   《" + outputName + "》" );
          FileUtils.deleteQuietly(file);
        } catch (IOException e) {
          System.err.println("未能转移简历文件：《" + fileName + "》" );
          e.printStackTrace(System.err);
        }
      }    
      System.out.println("========================= 处理后的简历已成功结转备份 ==========================="); 
    }
  }
  
  private static Resume processResumeFile(File file) {
    if(file.isDirectory()) {
      return null;
    }
    try {
      for(ResumeParser parser : PARSERS) {
        if(parser.canParse(file)) {
          return parser.parse(file);
        }
      }
    } catch(Exception e) {
      System.err.println("未能正确解析简历：《" + file.getName() + "》");
      e.printStackTrace(System.err);
    }
    
    String filename = file.getName();
    System.out.println("暂时无法解析简历：《" + filename + "》，接下来尝试所有可能的解析方法。");
    
    // 未正确解析，尝试遍历所有的解析方式
    for(ResumeParser parser : PARSERS) {
      String parserName = parser.getName();
      System.out.println("尝试以" + parserName + "简历去解析...");
      try {
        Resume resume = parser.parse(file);
        if(resume != null && resume.getName() != null && resume.getPhone() != null) {
          System.out.println("简历解析取得成功！");
          return resume;
        } else {
          System.out.println("解析失败，该简历不是" + parserName + "简历...");
        }
      } catch (Exception e) {
        System.out.println("解析失败，无法正确获取简历内容！");        
      }
    }
    
    return null;
  }
  
  /** 获取简历所在目录 */
  private static File getResumeFolder() {
    String dir = System.getProperty("job.resume.dir");
    return mkdir(new File(dir == null || dir.length() == 0 ? "未处理简历" : dir));
  }
  
  /** 获取简历转移目录 */
  private static File getOutputFolder() {
    String dir = System.getProperty("job.output.dir");
    return mkdir(new File(dir == null || dir.length() == 0 ? "已处理简历" : dir));
  }
  
  /** 获取结果输出Excel所在目录 */
  private static File getExcelFolder() {
    String dir = System.getProperty("job.excel.dir");
    return mkdir(new File(dir == null || dir.length() == 0 ? "处理结果" : dir));
  }
  
  private static File mkdir(File dir) {
    dir.mkdirs();
    return dir;
  }
  
  private static void printResume(Resume resume) {
    StringBuilder sb = new StringBuilder("\t");
    sb.append("姓名：").append(resume.getName()).append("\n\t");
    sb.append("性别：").append(resume.getSex()).append("\n\t");
    sb.append("年龄：").append(resume.getAge()).append("\n\t");
    sb.append("生日：").append(resume.getBirthday()).append("\n\t");
    sb.append("电话：").append(resume.getPhone()).append("\n\t");
    sb.append("邮箱：").append(resume.getMail()).append("\n\t");
    
    sb.append("城市：").append(resume.getCity()).append("\n\t");
    sb.append("地址：").append(resume.getAddress()).append("\n\t");
    sb.append("学历：").append(resume.getEducation()).append("\n\t");
    sb.append("学校：").append(resume.getSchool()).append("\n\t");
    sb.append("经验：").append(resume.getWorkDuration()).append("\n\t");
    
    sb.append("工作：").append(resume.getJob()).append("\n\t");
    sb.append("公司：").append(resume.getCompany());
    System.out.println(sb.toString());
  }

}
