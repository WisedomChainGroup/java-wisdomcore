FROM wisdomchain/jdk11
RUN mkdir /app/
RUN mkdir /genesis
COPY build/*.json /genesis/
COPY build/*.jar /app/lib/
ENV CLASSPATH .:$JAVA_HOME/lib
