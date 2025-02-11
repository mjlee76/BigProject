# Amazon Corretto 21을 기반 이미지로 사용
FROM bellsoft/liberica-openjdk-alpine:17 AS builder

WORKDIR /tellMe

COPY . .

RUN ./gradlew clean build -x test
# 빌드된 JAR 파일을 컨테이너로 복사

FROM bellsoft/liberica-openjdk-alpine:17

WORKDIR /tellMe

COPY --from=builder /tellMe/build/libs/*.jar app.jar

# 포트 노출
EXPOSE 8080

# 'dev','dockerdb' 프로파일을 활성화하여 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "/tellMe/app.jar"]