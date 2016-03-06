package com.github.xsocket.job.resume;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.github.xsocket.job.AbstractResume;
import com.github.xsocket.job.Resume;

/**
 * 猎聘简历。
 * @author MWQ
 *
 */
public class LiepinResume extends AbstractResume implements Resume {

  private static final long serialVersionUID = 7062333547931765074L;
  
  public static final Set<String> KEY_WORDS = new HashSet<String>();
  
  public static final String KEY_JOB = "主动应聘职位：";
  public static final String KEY_NAME = "姓名：";
  public static final String KEY_WORK_DURATION = "工作年限：";
  public static final String KEY_SEX = "性别：";
  public static final String KEY_AGE = "年龄：";
  public static final String KEY_CITY = "所在地：";
  public static final String KEY_PHONE = "手机号码：";
  public static final String KEY_MAIL = "电子邮件：";
  public static final String KEY_EDUCATION = "教育程度：";
  
  static {
    KEY_WORDS.add(KEY_NAME);
    KEY_WORDS.add(KEY_WORK_DURATION);
    KEY_WORDS.add(KEY_SEX);
    KEY_WORDS.add(KEY_AGE);
    KEY_WORDS.add(KEY_PHONE);
    KEY_WORDS.add(KEY_CITY);
    KEY_WORDS.add(KEY_MAIL);
    KEY_WORDS.add(KEY_EDUCATION);
  }
  
  protected Map<String, String> cache = new HashMap<String, String>();
  
  @Override
  public String getSource() {
    return "猎聘网";
  }

  @Override
  public String getName() {
    return cache.get(KEY_NAME);
  }

  @Override
  public String getJob() {
    return cache.get(KEY_JOB);
  }

  @Override
  public String getSex() {
    return cache.get(KEY_SEX);
  }

  @Override
  public String getEducation() {
    return cache.get(KEY_EDUCATION);
  }

  @Override
  public String getWorkDuration() {
    return cache.get(KEY_WORK_DURATION);
  }

  @Override
  public String getCity() {
    return cache.get(KEY_CITY);
  }

  @Override
  public String getPhone() {
    return cache.get(KEY_PHONE);
  }

  @Override
  public String getMail() {
    return cache.get(KEY_MAIL);
  }
  
  @Override
  public String getAge() {
    return cache.get(KEY_AGE);
  }
  
  public void set(String key, String value) {
    cache.put(key, value);
  }

}
