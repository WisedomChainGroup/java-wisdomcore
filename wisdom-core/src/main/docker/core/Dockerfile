FROM wisdomchain/server_jre8
RUN mkdir /app/
RUN apt update
RUN apt install -y netcat
COPY ./wisdom-core-1.13.0-RELEASE.jar /app/app.jar
COPY lib/ /app/lib/
COPY run_wdc_core.sh /run_wdc_core.sh
COPY version.json /version.json
ENV CLASSPATH .:$JAVA_HOME/lib
ENTRYPOINT ["/run_wdc_core.sh" ]
