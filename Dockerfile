FROM eclipse-temurin:17-jre
ENV TZ=Asia/Shanghai
WORKDIR /app
RUN groupadd --system --gid 1001 appgroup && \
    useradd --system --uid 1001 --gid appgroup appuser
COPY bootstrap/target/bootstrap-1.0.0-SNAPSHOT.jar /app/app.jar
RUN chown -R appuser:appgroup /app
USER appuser
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
