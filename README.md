# 📝 Smart Notes & AI Task Planner

Ứng dụng ghi chú thông minh và lên kế hoạch tự động tích hợp trí tuệ nhân tạo (AI Gemini) và dịch vụ đám mây Firebase, phát triển theo mô hình Client - Server.

Dự án được triển khai theo phương pháp phát triển Full-stack độc lập theo từng tính năng, giúp tối ưu hóa tiến độ và làm rõ vai trò đóng góp của từng thành viên.

---

## 🛠️ Thông số Kỹ thuật & Môi trường Hệ thống

Để đảm bảo dự án chạy đồng bộ trên máy của tất cả các thành viên, toàn nhóm thống nhất cấu hình môi trường theo các thông số sau:

### 1. Hệ thống Frontend (Android Mobile App)
* **IDE:** Android Studio (Khuyến nghị phiên bản Ladybug 2024.2.1 hoặc mới hơn)
* **Ngôn ngữ lập trình:** Java (JDK 21)
* **Kiến trúc:** MVVM (Model - View - ViewModel)
* **Minimum SDK:** API 24 (Android 7.0 Nougat) — *Tối ưu hóa độ tương thích thiết bị*
* **Target SDK:** API 34 hoặc API 35
* **Thư viện chính:** Retrofit 2 (Kết nối API), Gson (Parse JSON), SharedPreferences (Lưu token).

### 2. Hệ thống Backend (API Server)
* **Framework:** Spring Boot 3.x.x (Tương thích hoàn toàn với Java 21)
* **Ngôn ngữ lập trình:** Java 21
* **Quản lý dự án:** Maven
* **Kiến trúc:** Layered Architecture (Controller - Service - Repository)
* **Thư viện chính:** Spring Web, Spring Data JPA, Lombok, PostgreSQL Driver.

### 3. Cơ sở dữ liệu & Dịch vụ tích hợp
* **Database:** PostgreSQL 15.x / 16.x (Quản lý trực quan bằng pgAdmin hoặc DBeaver)
* **AI:** Google Gemini API (Model: `gemini-1.5-flash`)
* **Cloud Platform:** Firebase (Sử dụng Firebase Cloud Messaging - FCM để gửi thông báo đẩy).

---

## 👥 Phân chia Công việc (Full-stack Team 5 Members)

Mỗi thành viên chịu trách nhiệm **toàn diện** cho mô-đun tính năng của mình: từ thiết kế bảng Database PostgreSQL, viết API Spring Boot, cho đến thiết kế giao diện và xử lý logic kết nối trên Android Studio.

| Thành viên | Tính năng phụ trách (Full-stack) | Chi tiết công việc (DB -> Backend -> Frontend) |
| :--- | :--- | :--- |
| **Huỳnh Văn Nghĩa**<br>*(Leader)* | **Hệ thống Xác thực & Git Master** | - **Quản lý:** Khởi tạo project, cấu hình Git, `.gitignore` và duyệt các Pull Request.<br>- **Backend:** Thiết kế bảng `User`. Viết API Đăng ký, Đăng nhập, Quên mật khẩu, tạo mã Token.<br>- **Frontend:** Code trọn bộ giao diện và logic luồng Đăng nhập/Đăng ký/Quên mật khẩu, xử lý lưu Token an toàn bằng `SharedPreferences`. |
| **Trần Minh Khang** | **Quản lý Ghi chú cốt lõi (Core Notes CRUD)** | - **Backend:** Thiết kế bảng `Note` (liên kết khóa ngoại với `User`). Viết các API: Thêm mới, Chỉnh sửa, Xóa, Lấy danh sách ghi chú.<br>- **Frontend:** Thiết kế màn hình Danh sách ghi chú (`RecyclerView` bo góc), màn hình Thêm/Sửa ghi chú chi tiết và kết nối các API tương ứng từ Server. |
| **Nguyễn Khánh Duy** | **Phân loại Ghi chú & Tìm kiếm (Categories & Search)** | - **Backend:** Thiết kế bảng `Category`/`Tag`. Viết API tạo danh mục, gắn tag vào từng ghi chú và API tìm kiếm ghi chú theo từ khóa/phân loại.<br>- **Frontend:** Thiết kế giao diện bộ lọc danh mục, thanh tìm kiếm (Search Bar) thông minh và quản lý màu sắc tag trên giao diện điện thoại. |
| **Nguyễn Trần Thanh Phong** | **Tích hợp Trí tuệ nhân tạo (AI Gemini Integration)** | - **Backend:** Viết Service kết nối với Google Gemini API. Xử lý prompt ngầm từ hệ thống để: Tóm tắt nội dung ghi chú dài và Tự động bóc tách ghi chú thành To-do list (mục tiêu nhỏ).<br>- **Frontend:** Thiết kế các nút bấm tính năng AI trên màn hình ghi chú, xử lý hiệu ứng đợi phản hồi (Loading) và hiển thị kết quả AI trả về. |
| **Trần Minh Quân** | **Hẹn giờ & Thông báo nhắc nhở (Reminders & FCM)** | - **Backend:** Thiết kế bảng lưu thời gian hẹn giờ nhắc nhở. Tích hợp Firebase FCM Server SDK, sử dụng `Spring Scheduler` để tự động quét database và đẩy thông báo xuống điện thoại khi đến giờ hẹn.<br>- **Frontend:** Cấu hình Firebase FCM Client trên Android để tiếp nhận thông báo đẩy ngầm, thiết kế giao diện chọn ngày/giờ nhắc nhở (`DateTimePicker`) trong ghi chú. |

---

## 📁 Cấu trúc Thư mục Dự án

```text
smart-notes-app/
├── backend-springboot/      # Mã nguồn và cấu hình Spring Boot Server (Java 21)
│   ├── src/main/java/.../   # Package chứa Controller, Service, Repository, Entity theo từng tính năng
│   └── resources/
│       ├── application.properties.example  # File cấu hình mẫu cho các thành viên
│       └── application.properties          # File cấu hình thật chứa pass DB (ĐÃ CHẶN TRÊN GIT)
├── frontend-android/       # Mã nguồn và cấu hình Android Studio (Java 21)