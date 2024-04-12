# syntax=docker/dockerfile:1.4
FROM node:18-alpine3.19 as builder-web
WORKDIR /build
COPY . /build
# RUN cd web_src && rm package-lock.json && npm install -verbose && npm run build -verbose
# 需要启用Docker的BuildKit功能
RUN --mount=type=cache,id=wvp-pro-web-node_modules,sharing=locked,target=/build/web_src/node_modules \
    cd web_src && \
    rm -f package-lock.json && \
    npm install -verbose && \
    npm run build -verbose
FROM maven:3.6.3-amazoncorretto-11 as builder
COPY --from=builder-web /build /build
WORKDIR /build
# RUN mvn clean package -X -D skipTests -s settings.xml
# 需要启用Docker的BuildKit功能
RUN --mount=type=cache,id=m2_cache,sharing=locked,target=/root/.m2 mvn clean package -X -D skipTests -s custom.settings.xml
FROM amazoncorretto:11-alpine3.19
# RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.aliyun.com/g' /etc/apk/repositories && apk add --no-cache fontconfig ttf-freefont ffmpeg
RUN adduser -S -u 9528 app
USER app
WORKDIR /app
COPY --from=builder --chown=app:app /build/target/*.jar ./app.jar
ENTRYPOINT ["java", "-jar", "app.jar", "-server", "-Djava.awt.headless=true", "-Djava.security.egd=file:/dev/./urandom", "-Dfile.encoding=UTF-8", "-Duser.timezone=Asia/Shanghai"]