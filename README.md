# 🛒 Spring Boot E-commerce Backend

## 📌 Giới thiệu

Đây là một dự án **Spring Boot API** cho hệ thống **quản lý bán hàng trực tuyến** (E-commerce Platform). Ứng dụng hỗ trợ các chức năng cơ bản như:

- Đăng ký / đăng nhập người dùng (JWT Authentication)
- Quản lý sản phẩm, danh mục
- Giỏ hàng và chi tiết giỏ hàng
- Đặt hàng và quản lý đơn hàng
- Gửi email xác thực / quên mật khẩu
- Quản lý token, blacklist JWT
- Lưu lịch sử Flyway (migrations)

Ứng dụng được xây dựng theo kiến trúc **RESTful API** với phân tách rõ ràng các lớp `Controller`, `Service`, `Repository`, `Entity`, và có áp dụng **DTO pattern**, **JWT**, **Spring Security**, **Flyway** và **MySQL** làm CSDL.

## 🗂️ Cấu trúc thư mục chính:

### 1. Cấu trúc dự án
![image](https://github.com/user-attachments/assets/361db810-0df4-468e-bda7-fdd0125fc491)

### 2. Lược đồ ERD
![image](https://github.com/user-attachments/assets/e7ba189f-83b9-4132-a338-2fc49a2b5b0b)


## 🧑‍💻 Công nghệ sử dụng

- Java 17+
- Spring Boot
- Spring Data JPA
- Spring Security + JWT
- MySQL
- Flyway
- Lombok
- Mail (SMTP)

---

## 🚀 Hướng dẫn cài đặt và chạy

### 1. Clone dự án

git clone [https://github.com/hoangdinhdung05/<repo-name>.git](https://github.com/hoangdinhdung05/projectDemo.git)
cd <repo-name>

### 2. Cấu hình database, flyway quản lí migration, cấu hình jwt

spring.datasource.url=jdbc:mysql://localhost:3306/your_db_name
spring.datasource.username=your_username
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration

jwt.secret=your_jwt_secret_key

### 3. Chạy ứng dụng

./mvnw spring-boot:run

✅ Gợi ý phát triển thêm
Trang admin quản lý người dùng, sản phẩm, đơn hàng (với phân quyền)

Tích hợp Swagger UI

Cổng thanh toán (VNPAY, Momo...)

Tìm kiếm nâng cao, phân trang

Thống kê doanh thu theo ngày/tháng/năm

Đánh giá, yêu thích sản phẩm

CORS, CSRF, Refresh Token Rotation

👤 Tác giả

Hoàng Đình Dũng

Sinh viên năm 2 - Đại học Công Nghệ Giao Thông Vận Tải

🌐 Facebook: https://www.facebook.com/hoangdinhdung2208

💻 Github: https://github.com/hoangdinhdung05

