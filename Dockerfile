FROM openjdk:17
WORKDIR /
COPY ./build/libs/app.jar app.jar

RUN useradd -u 1234 app
USER app

ENTRYPOINT ["java", "-jar", "app.jar"]