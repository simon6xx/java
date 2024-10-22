# 使用 OpenJDK 作为基础镜像
FROM openjdk:17-jdk-slim

# 将项目构建的 JAR 文件复制到容器中（这里不是宿主机的绝对路径，而是相对于Dockerfile目录下的相对路径）
COPY springboot-docker-compose-1.0-SNAPSHOT.jar /app/demo.jar

# 暴露应用运行的端口
EXPOSE 8080

# 运行应用程序
ENTRYPOINT ["java", "-jar", "/app/demo.jar"]
