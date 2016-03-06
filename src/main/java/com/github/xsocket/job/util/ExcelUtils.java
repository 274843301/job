package com.github.xsocket.job.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.github.xsocket.job.Resume;

public class ExcelUtils {
  
  private static final String[] HEADERS = new String[]{
      "序号", "渠道", "姓名", "性别", "年龄", "生日", "电话", "邮箱",
      "居住城市", "地址", "学历", "工作经验", "申请职位", "现工作单位" 
  };

  public static void outputResumeProcessResult(Map<File, Resume> map, File outFile) throws IOException {
    //工作簿
    HSSFWorkbook excel = new HSSFWorkbook();
    FileOutputStream fileoutputstream = new FileOutputStream(outFile);
    
    try {
      HSSFSheet sheet = excel.createSheet("简历处理结果");
      
      // 写表头
      HSSFRow headerRow = sheet.createRow(0);
      for(int i = 0; i < HEADERS.length; i++) {
        headerRow.createCell(i).setCellValue(HEADERS[i]);
      }
      
      // 写内容
      int rowNum = 1;
      for(Map.Entry<File, Resume> entry : map.entrySet()) {
        Resume resume = entry.getValue();
        HSSFRow row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue(rowNum);                    // 序号
        row.createCell(1).setCellValue(resume.getSource());        // 简历来源
        row.createCell(2).setCellValue(resume.getName());          // 姓名
        row.createCell(3).setCellValue(resume.getSex());           // 性别
        row.createCell(4).setCellValue(resume.getAge());           // 年龄
        row.createCell(5).setCellValue(resume.getBirthday());      // 生日
        row.createCell(6).setCellValue(resume.getPhone());         // 电话
        row.createCell(7).setCellValue(resume.getMail());          // 邮箱
        row.createCell(8).setCellValue(resume.getCity());          // 居住城市
        row.createCell(9).setCellValue(resume.getAddress());       // 地址
        row.createCell(10).setCellValue(resume.getEducation());    // 学历
        row.createCell(11).setCellValue(resume.getWorkDuration()); // 工作经验
        row.createCell(12).setCellValue(resume.getJob());          // 申请职位
        row.createCell(13).setCellValue(resume.getCompany());      // 现工作单位
        rowNum++;
      }
      excel.write(fileoutputstream);
    } finally {
      fileoutputstream.close();
      excel.close();
    }
  }
  
}
