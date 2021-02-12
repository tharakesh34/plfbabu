# Dockerfile
FROM tomcat_8.5.12
MAINTAINER  Pennant Infra-Team <itsupport@pennanttech.com>
RUN sed -i '103i export JAVA_HOME=/opt/jdk1.8.0_121\n export PATH=$JAVA_HOME/bin:$PATH\n export PATH=$JAVA_HOME/jre/lib:$PATH\n export APP_ROOT_PATH=/opt\n export APP_ENCRYPTION_PASSWORD=mysalt123\n export PFF_HOME=/opt/PFF/BASE\n export PFF_PASSWORD=mysalt123\n export DESIGNER_HOME=/opt/PFF/BASE\n JAVA_OPTS="$JAVA_OPTS -Xms512m -Xmx1024m -XX:+UseG1GC -XX:+UseStringDeduplication" \n systemctl start filebeat' /opt/tomcat/bin/catalina.sh
EXPOSE 8080
RUN sftp -rp -o StrictHostKeyChecking=no  root@sftpserver:/Pennant/PLF/Core/QC3/Docker-config/context.xml /opt/tomcat/conf/
RUN sftp -rp -o StrictHostKeyChecking=no  root@sftpserver:/Pennant/PLF/Core/QC3/PFF /opt/
RUN sftp -rp -o StrictHostKeyChecking=no  root@sftpserver:/Pennant/common-files/ojdbc8.jar /opt/tomcat/lib/
RUN sftp -rp -o StrictHostKeyChecking=no  root@sftpserver:/Pennant/PLF/Core/QC3/Docker-config/process-designer.war /opt/tomcat/webapps/
RUN sftp -rp -o StrictHostKeyChecking=no  root@sftpserver:/Pennant/PLF/Core/QC3/Docker-config/filebeat.yml /etc/filebeat/
RUN chmod 755 /etc/filebeat/filebeat.yml
COPY PFSWeb.war /opt/tomcat/webapps/
COPY pff-api.war /opt/tomcat/webapps/
CMD ["/opt/tomcat/bin/catalina.sh", "run"]