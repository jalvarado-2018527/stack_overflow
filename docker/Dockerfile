FROM ${DOCKER_IMAGE}

WORKDIR application
#RUN apk add --update tzdata fontconfig ttf-dejavu
RUN apt update && apt install -y tzdata fontconfig ttf-dejavu

COPY target/dependencies/ ./
COPY target/spring-boot-loader/ ./
COPY target/snapshot-dependencies/ ./
COPY target/application/ ./

ENV TZ=America/Guatemala
ENV LANG es_GT.UTF-8
ENV LANGUAGE es_GT.UTF-8
ENV LC_ALL es_GT.UTF-8
ENV JVM_ARGS="${JVM_ARGS_DEFAULT}"
ENV SPRING_PROFILES_ACTIVE="${SPRING_PROFILES_ACTIVE_DEFAULT}"
RUN echo ${TZ} > /etc/timezone

ENTRYPOINT ["sh", "-c", "java ${JVM_ARGS} org.springframework.boot.loader.JarLauncher"]
