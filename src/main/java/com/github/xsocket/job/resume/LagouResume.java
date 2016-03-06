package com.github.xsocket.job.resume;

import com.github.xsocket.job.AbstractResume;
import com.github.xsocket.job.Resume;

/**
 * 拉钩简历。
 * 
 * @author MWQ
 *
 */
public class LagouResume extends AbstractResume implements Resume {

  private static final long serialVersionUID = 2093235400790350336L;

  protected String name;

  protected String job;

  protected String company;

  protected String sex;

  protected String education;

  protected String workDuration;

  protected String city;

  protected String phone;

  protected String mail;
  

  @Override
  public String getSource() {
    return "拉钩";
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

  public String getCompany() {
    return company;
  }

  public void setCompany(String company) {
    this.company = company;
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
