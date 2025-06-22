FROM eclipse-temurin:17-jre-jammy
ENV ARTIFACT_NAME=katan.jar
ENV HOME=/usr/kuken

WORKDIR $HOME
RUN mkdir resources
COPY application/build/libs/ .
COPY application/build/resources/main/ ./resources
ENTRYPOINT exec java -jar $HOME/${ARTIFACT_NAME}