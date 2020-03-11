FROM ubuntu
# Install Oracle Server JRE 8u212
ADD server-jre-8u212-linux-x64.tar.gz /tmp/server-jre-8u212-linux-x64.tar.gz
RUN apt update
RUN apt upgrade -y
RUN apt install -y apt-utils
RUN apt install -y curl
RUN cd /tmp && \
    curl -L -O -k "https://github.com/mawenpeng/server_jre8/raw/8u212-b10-jdk-down/server-jre-8u212-linux-x64.tar.gz" && \
    tar xf server-jre-8u212-linux-x64.tar.gz -C /srv/ && \
    rm -f server-jre-8u212-linux-x64.tar.gz && \
    ln -s /srv/jdk1.8* /srv/jdk && \
    ln -s /srv/jdk/bin/* /usr/local/bin
    
ENV JAVA_HOME /srv/jdk
