FROM openjdk:8-jdk-stretch

RUN apt-get update --fix-missing
RUN apt-get install -y vim
COPY ./ /var/www
WORKDIR /var/www
EXPOSE 8080
