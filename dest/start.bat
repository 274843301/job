:: ##########  �򵥲�������˵��  ############# 
:: 
::    RESUME_DIR�� ��ʾԭʼδ����ļ�������Ŀ¼��
::    EXCEL_DIR��  ��ʾ�������Excel�ļ���ŵ�Ŀ¼��
::    OUTPUT_DIR�� ��ʾ�ѳɹ�����ļ�����ת�Ƶ���Ŀ¼��
::    OUTPUT��     ��ʾ�Ƿ񽫳ɹ�����ļ�������ת�ƣ�Ĭ��Ϊ:TRUE��
::    SHOW_DETAIL����ʾ�Ƿ����ն���ʾ����ϸ�Ĵ�����־��Ĭ��Ϊ:FALSE��
::
::    ע�⣺���Ŀ¼·���а����ո����ڲ�������ʱ��˫���Ű�������ֵ���� "C:/Program Files/Resume"
::
:: ######################################### 

set RESUME_DIR="δ�������";
set EXCEL_DIR="������";
set OUTPUT_DIR="�Ѵ������";
set OUTPUT="TRUE";
set SHOW_DETAIL="FALSE";

java -jar job-1.0.0.jar -Djob.resume.dir=%RESUME_DIR% -Djob.excel.dir=%EXCEL_DIR% -Djob.output.dir=%OUTPUT_DIR% -Djob.output=%OUTPUT% -Djob.detail=%SHOW_DETAIL% 2>>logs/error.log

pause