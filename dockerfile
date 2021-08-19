FROM balenalib/armv7hf-debian-openjdk:latest

COPY ./target /usr/src/myapp
WORKDIR /usr/src/myapp

CMD ["java","-jar","EsperPrototype-1.0-SNAPSHOT.jar"]
