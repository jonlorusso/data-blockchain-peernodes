FROM openjdk:8-jre
MAINTAINER jon@swatt.exchange

ARG INSTALL_DIR
WORKDIR ${INSTALL_DIR}

ENTRYPOINT ["/usr/bin/java", "-jar", "blockchain-java.jar" ]

# Add Maven dependencies (not shaded into the artifact; Docker-cached)
ADD target/lib ${INSTALL_DIR}/lib

# Add the service itself
ARG JAR_FILE
ADD target/${JAR_FILE} ${INSTALL_DIR}/blockchain-java.jar
