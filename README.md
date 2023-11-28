#### 自建的简易缓存仓库

**支持** ：maven、alpine  
**说明** ：通过缓存形式提高仓库的拉取速度。如果本地不存在对应的缓存文件，则会从`${res.config.*.remote-url}`
指定的仓库获取资源文件进行本地缓存，下载的时候会同时传递给请求方。如果已存在，则直接从缓存文件获取。
服务不设置缓存过期事件，因为文件名称中存在版本信息，因此缓存文件不设置过期机制。

#### 编译

编译结果：target/library-cache-server-0.0.1-SNAPSHOT.jar

```shell
mvn clean package 
```

#### 运行

命令行运行

```shell
# res.config.maven.remote-url 指定Maven阿里云仓库
# res.config.alpine.remote-url 指定Alpine阿里云仓库

# res.config.maven.cache-path 指定Maven缓存目录
# res.config.alpine.cache-path 指定Alpine缓存目录 
java -jar \
  -Dres.config.maven.remote-url=https://maven.aliyun.com/repository/releases \
  -Dres.config.alpine.remote-url=https://mirrors.aliyun.com/alpine \
  library-cache-server-0.0.1-SNAPSHOT.jar
```
或者简单运行
```shell
java -jar library-cache-server-0.0.1-SNAPSHOT.jar
```

#### 前置说明

运行环境  
IP地址：192.168.2.115  
端口：8080  
Context-Path：/res

#### Maven配置文件设置方式

settings.xml 文件内容

```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
    <mirrors>
        <mirror>
            <id>mirror</id>
            <mirrorOf>*</mirrorOf>
            <name>mirror</name>
            <url>http://192.168.2.115:8080/res/maven/agent/cache/</url>
        </mirror>
    </mirrors>
</settings>

```

#### Alpine配置设置方式

指定仓库且安装openjdk8  
**PS** : 注意事项`cat /etc/alpine-release`执行结果为`3.16.1`。  
有些仓库统一使用大版本，例如：`3.16.1`实际为`3.16`。  
因此`cat /etc/alpine-release`组成的参数可能无效。

```shell
echo http://192.168.2.115:8080/res/alpine/agent/cache/v`cat /etc/alpine-release`/main/ > /etc/apk/repositories 
echo http://192.168.2.115:8080/res/alpine/agent/cache/v`cat /etc/alpine-release`/community/ >> /etc/apk/repositories 
apk update && apk upgrade && apk add openjdk8
```