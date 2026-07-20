FROM registry.cn-hangzhou.aliyuncs.com/dockerhub_mirror/java:21-anolis

LABEL maintainer="ringstory <ringstory@example.com>"

ENV TZ=Asia/Shanghai
ENV LANG C.UTF-8
ENV JAVA_OPTS="-Xms256m -Xmx512m -Djava.security.egd=file:/dev/./urandom"

RUN ln -sf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

RUN mkdir -p /ringstory-gateway

WORKDIR /ringstory-gateway

EXPOSE 8080

ADD ./target/ringstory-gateway.jar ./

CMD java $JAVA_OPTS -jar ringstory-gateway.jar
