FROM wisdomchain/server_jre8_with_dependencies
RUN apt update
RUN apt install -y netcat
COPY build/wisdom*.jar /app/app.jar
COPY run_wdc_core.sh /run_wdc_core.sh
COPY version.json /version.json
ENV CLASSPATH .:$JAVA_HOME/lib
ENTRYPOINT ["usr/bin/env", "bash", "/run_wdc_core.sh" ]
