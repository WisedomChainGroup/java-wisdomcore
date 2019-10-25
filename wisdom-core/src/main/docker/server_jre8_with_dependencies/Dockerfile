FROM wisdomchain/server_jre8
RUN mkdir /app/
RUN mkdir /genesis
COPY genesis/wisdom-genesis-generator.json /genesis/
COPY lib/ /app/lib/
ENV CLASSPATH .:$JAVA_HOME/lib
