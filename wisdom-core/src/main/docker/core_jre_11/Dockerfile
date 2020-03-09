FROM wisdomchain/server_jdk11_with_dependencies
COPY build/wisdom*.jar /app/app.jar
COPY run_wdc_core.sh /run_wdc_core.sh
COPY version.json /version.json
ENV CLASSPATH .:$JAVA_HOME/lib

ENTRYPOINT ["usr/bin/env", "java", "/run_wdc_core.sh" ]
