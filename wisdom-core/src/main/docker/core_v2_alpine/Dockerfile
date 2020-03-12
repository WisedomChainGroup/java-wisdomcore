FROM wisdomchain/server_fat_jre8:alpine
RUN apt update
RUN apt install -y netcat
RUN apt install wget
COPY build/wisdom*.jar /app/app.jar
COPY run_wdc_core.sh /run_wdc_core.sh
COPY version.json /version.json
ENV CLASSPATH .:$JAVA_HOME/lib


EXPOSE 8849

ENTRYPOINT ["usr/bin/env", "bash", "/run_wdc_core.sh" ]
