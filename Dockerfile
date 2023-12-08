# 最小编译方式，通过alpine基础镜像进行编译docker 服务镜像。
# docker build -t trevet/library-cache-server:0.0.2 .
FROM alpine:3.16
RUN echo https://mirrors.aliyun.com/alpine/v3.10/main/ > /etc/apk/repositories && \
        echo https://mirrors.aliyun.com/alpine/v3.10/community/ >> /etc/apk/repositories && \
    apk update && apk upgrade && apk add openjdk8
#RUN echo http://192.168.2.2/res/alpine/agent/cache/v`cat /etc/alpine-release`/main/ > /etc/apk/repositories && \
#            echo http://192.168.2.2/res/alpine/agent/cache/v`cat /etc/alpine-release`/community/ >> /etc/apk/repositories && \
#        apk update && apk upgrade && apk add openjdk8
ADD target/library-cache-server-0.0.1-SNAPSHOT.jar /home/app.jar
WORKDIR /home
EXPOSE 81
CMD ["java","-jar","app.jar"]