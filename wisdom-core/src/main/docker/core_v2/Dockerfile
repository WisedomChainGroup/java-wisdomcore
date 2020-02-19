FROM wisdomchain/server_jre8_with_dependencies
RUN apt update
RUN apt install -y netcat
COPY build/wisdom*.jar /app/app.jar
COPY run_wdc_core.sh /run_wdc_core.sh
COPY version.json /version.json
ENV CLASSPATH .:$JAVA_HOME/lib

RUN wget http://download-keycdn.ej-technologies.com/jprofiler/jprofiler_linux_9_2.tar.gz -P /tmp/ &&\
 tar -xzf /tmp/jprofiler_linux_9_2.tar.gz -C /usr/local &&\
 rm /tmp/jprofiler_linux_9_2.tar.gz

ENV JPAGENT_PATH="-agentpath:/usr/local/jprofiler9/bin/linux-x64/libjprofilerti.so=nowait"
EXPOSE 8849

ENTRYPOINT ["usr/bin/env", "bash", "/run_wdc_core.sh" ]
