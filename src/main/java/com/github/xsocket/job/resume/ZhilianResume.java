package com.github.xsocket.job.resume;

import com.github.xsocket.job.AbstractResume;
import com.github.xsocket.job.Resume;

/**
 * 智联简历。
 * 
 * @author MWQ
 *
 */
public class ZhilianResume extends AbstractResume implements Resume {

  private static final long serialVersionUID = 509611509419279515L;

  protected String name;

  protected String job;

  protected String sex;

  protected String education;

  protected String workDuration;

  protected String city;

  protected String phone;

  protected String mail;
  
  protected String age;
  
  protected String birthday;

  @Override
  public String getSource() {
    return "智联招聘";
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getJob() {
    return job;
  }

  public void setJob(String job) {
    this.job = job;
  }

  public String getAge() {
    return age;
  }

  public void setAge(String age) {
    this.age = age;
  }
  
  public String getBirthday() {
    return birthday;
  }

  public void setBirthday(String birthday) {
    this.birthday = birthday;
  }

  public String getSex() {
    return sex;
  }

  public void setSex(String sex) {
    this.sex = sex;
  }

  public String getEducation() {
    return education;
  }

  public void setEducation(String education) {
    this.education = education;
  }

  public String getWorkDuration() {
    return workDuration;
  }

  public void setWorkDuration(String workDuration) {
    this.workDuration = workDuration;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getMail() {
    return mail;
  }

  public void setMail(String mail) {
    this.mail = mail;
  }
  
  
}
