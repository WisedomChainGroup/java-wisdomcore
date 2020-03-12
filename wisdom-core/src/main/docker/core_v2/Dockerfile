FROM wisdomchain/server_jre8_with_dependencies

COPY build/wisdom*.jar /app/app.jar
COPY run_wdc_core.sh /run_wdc_core.sh
COPY version.json /version.json
ENV CLASSPATH .:$JAVA_HOME/lib


EXPOSE 8849

ENTRYPOINT ["usr/bin/env", "bash", "/run_wdc_core.sh" ]
