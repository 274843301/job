package com.github.xsocket.job;

import java.io.File;

/**
 * 简历解析工具。
 * 
 * @author MWQ
 *
 */
public interface ResumeParser {

  boolean canParse(File file);
  
  Resume parse(File file) throws Exception;
}
