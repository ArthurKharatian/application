FROM openjdk:11-jre-slim

VOLUME /tmp

ADD /target/user-registration-spring-boot.jar user-registration-spring-boot.jar

ENTRYPOINT ["java",  "-jar", "/user-registration-spring-boot.jar"]