FROM openjdk:8-jdk-alpine
MAINTAINER jon@swatt.exchange

WORKDIR /root

COPY target/lib /root/lib

ARG JAR_FILE
COPY target/${JAR_FILE} /root/${JAR_FILE}

ENV JAR_FILE ${JAR_FILE}
ENTRYPOINT [ "sh", "-c", "/usr/bin/java -jar $JAR_FILE" ]
