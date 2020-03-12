FROM salpadding/server_jre8:alpine
RUN mkdir /app/
RUN mkdir /genesis
COPY build/*.json /genesis/
COPY build/*.jar /app/lib/
ENV CLASSPATH .:$JAVA_HOME/lib
