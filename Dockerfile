# 最小编译方式，通过alpine基础镜像进行编译docker 服务镜像。
FROM alpine:3.15
RUN echo http://192.168.2.29:8080/res/alpine/agent/cache/v`cat /etc/alpine-release`/main/ > /etc/apk/repositories && \
            echo http://192.168.2.29:8080/res/alpine/agent/cache/v`cat /etc/alpine-release`/community/ >> /etc/apk/repositories && \
        apk update && apk upgrade && apk add openjdk8
ADD target/library-cache-server-0.0.1-SNAPSHOT.jar /home/app.jar
WORKDIR /home
EXPOSE 8080
CMD ["java","-jar","app.jar"]