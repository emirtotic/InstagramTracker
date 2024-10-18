# 1. Step: Use JDK based image
FROM amazoncorretto:17-alpine

# 2. Step: Define working directory in container
WORKDIR /app

# 3. Step: Copy maven build output (jar file) in container
COPY target/InstagramTracker-0.0.1-SNAPSHOT.jar app.jar

# 4. Step: Expose port
EXPOSE 8080

# 5. Step: Define the command for starting the app
ENTRYPOINT ["java", "-jar", "app.jar"]
