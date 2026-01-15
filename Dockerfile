# 1. Select Java 21 (Alpine Ultralight Edition) as the Base Image.
FROM eclipse-temurin:21-jdk-alpine

#2. Maintenance Technician Information (Optional)
LABEL maintainer="Tien Dat Nguyen"

#3. Create a cache for the application.
VOLUME /tmp

# 4. Copy file thực thi (.jar) từ thư mục target vào trong Docker
# Lưu ý: *.jar để tự động bắt đúng tên file dù version thay đổi
COPY target/*.jar app.jar

# 5. Open port 8088 (the port your project is running on).
EXPOSE 8088

# 6. Command to run the application when the container starts.
ENTRYPOINT ["java","-jar","/app.jar"]