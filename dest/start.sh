#!/bin/sh

#############  简单参数设置说明  ############# 
## 
##    RESUME_DIR： 表示原始未处理的简历所在目录；
##    EXCEL_DIR：  表示解析结果Excel文件存放的目录；
##    OUTPUT_DIR： 表示已成功处理的简历被转移到的目录；
##    OUTPUT：     表示是否将成功处理的简历进行转移，默认为:TRUE；
##    SHOW_DETAIL：表示是否在终端显示更详细的处理日志，默认为:FALSE；
##
##    注意：如果目录路径中包含空格，请在参数设置时用双引号包裹参数值，如 "C:/Program Files/Resume"
##
############################################ 

RESUME_DIR="未处理简历";
EXCEL_DIR="处理结果";
OUTPUT_DIR="已处理简历";
OUTPUT="TRUE";
SHOW_DETAIL="FALSE";

java -jar job-1.0.0.jar -Djob.resume.dir=$RESUME_DIR -Djob.excel.dir=$EXCEL_DIR -Djob.output.dir=$OUTPUT_DIR -Djob.output=$OUTPUT -Djob.detail=$SHOW_DETAIL 2>>logs/error.log