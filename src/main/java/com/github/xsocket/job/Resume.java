package com.github.xsocket.job;

import java.io.Serializable;

/**
 * 简历数据。
 * 
 * @author MWQ
 *
 */
public interface Resume extends Serializable {
  
  /** 简历来源渠道 */
  String getSource();

  /** 姓名 */
  String getName();
  /** 年龄 */
  String getAge();
  /** 生日 */
  String getBirthday();
  /** 性别 */
  String getSex();
  /** 职位 */
  String getJob();
  /** 所在公司 */
  String getCompany();
  /** 学历 */
  String getEducation();
  /** 毕业院校 */
  String getSchool();
  /** 工作时间 */
  String getWorkDuration();
  /** 所在城市 */
  String getCity();  
  /** 现住址 */
  String getAddress();
  /** 联系电话 */
  String getPhone();
  /** 电子邮箱 */
  String getMail();
}
