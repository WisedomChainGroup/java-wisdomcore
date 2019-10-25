FROM wisdomchain/server_jre8
RUN mkdir /app/
RUN mkdir /genesis
COPY build/wisdom-genesis-generator.json /genesis/
COPY build/*.jar /app/lib/
ENV CLASSPATH .:$JAVA_HOME/lib
