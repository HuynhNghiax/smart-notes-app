# 📝 Smart Notes & AI Task Planner

Ứng dụng ghi chú thông minh và lên kế hoạch tự động tích hợp trí tuệ nhân tạo (AI Gemini) và dịch vụ đám mây Firebase, phát triển theo mô hình Client - Server.

---

## 🛠️ Thông số Kỹ thuật & Môi trường Hệ thống

Để đảm bảo dự án chạy đồng bộ trên máy của tất cả các thành viên, toàn nhóm thống nhất cấu hình môi trường theo các thông số sau:

### 1. Hệ thống Frontend (Android Mobile App)
*   **IDE:** Android Studio (Khuyến nghị phiên bản Ladybug 2024.2.1 hoặc mới hơn)
*   **Ngôn ngữ lập trình:** Java (JDK 21)
*   **Kiến trúc:** MVVM (Model - View - ViewModel)
*   **Minimum SDK:** API 24 (Android 7.0 Nougat) — *Tối ưu hóa độ tương thích thiết bị*
*   **Target SDK:** API 34 hoặc API 35
*   **Thư viện chính:** Retrofit 2 (Kết nối API), Gson (Parse JSON), Glide (Tải ảnh).

### 2. Hệ thống Backend (API Server)
*   **Framework:** Spring Boot 3.x.x (Tương thích hoàn toàn với Java 21)
*   **Ngôn ngữ lập trình:** Java 21
*   **Quản lý dự án:** Maven
*   **Kiến trúc:** Layered Architecture (Controller - Service - Repository)
*   **Thư viện chính:** Spring Web, Spring Data JPA, Spring Boot Starter Mail, Lombok, PostgreSQL Driver.

### 3. Cơ sở dữ liệu & Dịch vụ tích hợp
*   **Database:** PostgreSQL 15.x / 16.x (Quản lý trực quan bằng pgAdmin hoặc DBeaver)
*   **AI:** Google Gemini API (Model: `gemini-1.5-flash`)
*   **Cloud Platform:** Firebase (Sử dụng Firebase Cloud Messaging - FCM để gửi thông báo đẩy).

---

## 👥 Phân chia Công việc (Team 5 Members)

Dự án được phân chia tối ưu cấu trúc nhân sự gồm: **3 thành viên tập trung Frontend (Android)** và **2 thành viên tập trung Backend (Spring Boot)** để đảm bảo tiến độ song song.

| Thành viên | Vai trò chính | Nhiệm vụ chi tiết |
| :--- | :--- | :--- |
| **Huỳnh Văn Nghĩa**<br>*(Leader)* | **Frontend Lead & Git Master** | - Khởi tạo project, cấu hình Git, `.gitignore` toàn dự án và quản lý nhánh.<br>- Thiết kế cấu trúc MVVM, cấu hình mạng (Retrofit Client) hỗ trợ Java 21.<br>- Code giao diện và logic cụm màn hình Xác thực: Đăng nhập, Đăng ký, Quên mật khẩu. |
| **Thành viên 2** | **Frontend UI/UX (Notes)** | - Thiết kế giao diện danh sách ghi chú sử dụng `RecyclerView` (CardView bo góc hiện đại).<br>- Code giao diện màn hình thêm/sửa/xem chi tiết ghi chú.<br>- Kết nối các API CRUD ghi chú từ Backend về hiển thị lên giao diện Android. |
| **Thành viên 3** | **Frontend Tích hợp (FCM & AI)** | - Tích hợp Firebase FCM SDK vào Android để tiếp nhận thông báo nhắc nhở từ Server.<br>- Thiết kế các nút bấm tính năng AI (Tóm tắt, Lên kế hoạch) và xử lý hiệu ứng hiển thị kết quả trả về từ AI.<br>- Xử lý bắt lỗi dữ liệu đầu vào (Validation) cho toàn bộ các form nhập liệu. |
| **Thành viên 4** | **Backend Core & Database** | - Khởi tạo project Spring Boot với Java 21, cấu hình kết nối database PostgreSQL.<br>- Thiết kế cơ sở dữ liệu và tạo các bảng (User, Note, OtpToken).<br>- Viết các API xác thực (Đăng ký, Đăng nhập, Quản lý Token/Session) và API CRUD cho Ghi chú. |
| **Thành viên 5** | **Backend AI & Services** | - Tích hợp Java Mail để viết logic tạo và gửi mã OTP khôi phục mật khẩu qua Email.<br>- Kết nối API Gemini để xử lý logic thông minh: Tóm tắt văn bản và Tự động phân tách mục tiêu thành To-do list.<br>- Viết tính năng hẹn giờ (Spring Scheduler) tự động gọi sang Firebase FCM để đẩy thông báo xuống điện thoại. |

---

## 📁 Cấu trúc Thư mục Dự án

```text
smart-notes-app/
├── backend-springboot/      # Mã nguồn và cấu hình Spring Boot Server
│   ├── src/main/java/.../   # Package chứa Controller, Service, Repository, Entity
│   └── resources/
│       ├── application.properties.example  # File cấu hình mẫu cho các thành viên (PostgreSQL)
│       └── application.properties          # File cấu hình thật (ĐÃ CHẶN TRÊN GIT)
├── frontend-android/       # Mã nguồn và cấu hình Android Studio
│   ├── app/src/main/java/...# Package chứa UI (Auth, Note), Data, Utils
│   └── app/src/main/res/    # Thư mục chứa Layout XML, Drawable, Values
└── README.md                # File hướng dẫn dự án (File này)