# 🏨 HOTEL MANAGEMENT SYSTEM (GRAND HOTEL PMS)
> Hệ thống quản lý khách sạn chuyên nghiệp dạng Desktop Application xây dựng bằng **Java Swing & MySQL 8.0**.

---

## 🌟 1. Giới thiệu dự án
Dự án được xây dựng theo kiến trúc **2 Không gian làm việc (2-Workspace PMS Architecture)** chuẩn mô hình quản trị khách sạn thực tế:
1. **Màn hình 1 - Vận hành & Lễ tân (Front Desk Dashboard):**
   - Sơ đồ phòng trực quan (Room Matrix Grid) chia theo khu vực tầng.
   - Thẻ phòng màu sắc trực quan (🟢 Trống, 🔴 Đang ở, 🟠 Chờ khách/Đặt cọc, ⚪ Đang dọn/Bảo trì).
   - Click thao tác 1-chạm: Đặt phòng & Check-in, Gọi dịch vụ Minibar, Check-out & Xuất hóa đơn.
   - Bảng thao tác nhanh và thống kê chỉ số trong ngày (Số phòng có khách, khách chuẩn bị đến, doanh thu tạm tính).
2. **Màn hình 2 - Quản trị & Danh mục (Back-office Management):**
   - Menu thanh bên (Sidebar): Quản lý phòng & giá, Hồ sơ khách hàng, Nhân sự & phân quyền, Danh mục dịch vụ, Báo cáo & thống kê.
   - Cơ chế ẩn form nhập liệu: Bảng dữ liệu luôn thông thoáng, nhập liệu/sửa đổi thông qua các hộp thoại Modal Popup tinh tế.

---

## 🛠️ 2. Công nghệ sử dụng
- **Ngôn ngữ:** Java (JDK 17 / JDK 21 LTS).
- **Giao diện:** Java Swing, Java AWT.
- **Hệ quản trị CSDL:** MySQL 8.0+ (InnoDB, UTF-8 MB4 Unicode).
- **Driver kết nối:** `mysql-connector-j-8.3.0.jar` (JDBC).

---

## 📁 3. Cấu trúc thư mục
```
HotelManagement_Java/
│
├── Database/
│   ├── db-hotel.sql                 # Script tạo CSDL HotelManagement & 13 bảng đầy đủ
│   └── CreateDatabase_MySQL.sql      # Bản sao lưu script CSDL
│
├── lib/
│   └── mysql-connector-j-8.3.0.jar   # Thư viện JDBC Driver kết nối MySQL
│
├── src/
│   └── hotel/
│       ├── MainFrame.java           # Cửa sổ chính, Top Navbar & bộ chuyển 2 Workspace
│       ├── VanHanhLeTanPanel.java   # Workspace 1: Sơ đồ phòng, Check-in, Gọi DV, Check-out
│       ├── QuanTriDanhMucPanel.java # Workspace 2: Sidebar Quản trị, Danh mục, Modal Popups
│       ├── DatabaseHelper.java      # Lớp kết nối JDBC & thực thi truy vấn
│       ├── UITheme.java             # Bảng màu, phông chữ và kiểu dáng chuẩn PMS
│       └── TestConnection.java      # Công cụ kiểm tra kết nối CSDL
│
├── config.properties                # Cấu hình IP, Port, User, Password CSDL
├── Bat_Dich_Vu_MySQL.bat            # Script khởi động dịch vụ MySQL80
├── Kiem_Tra_Ket_Noi_MySQL.bat       # Script kiểm tra kết nối CSDL
└── Run_Java_App.bat                 # Script chạy nhanh ứng dụng
```

---

## 🚀 4. Hướng dẫn cài đặt & Khởi chạy

### Bước 1: Nạp Cơ sở dữ liệu MySQL
1. Mở **MySQL Workbench** hoặc **Navicat / phpMyAdmin**.
2. Mở file `Database/db-hotel.sql` và nhấn **Execute (⚡)** để tạo Database `HotelManagement` cùng toàn bộ 13 bảng và dữ liệu mẫu.

### Bước 2: Cấu hình kết nối
Mở file `config.properties` và điền mật khẩu MySQL của bạn:
```properties
db.host=localhost
db.port=3306
db.name=HotelManagement
db.user=root
db.password=MẬT_KHẨU_MYSQL_CỦA_BẠN
```

### Bước 3: Chạy ứng dụng
- Nhấp đúp vào file **`Run_Java_App.bat`** để khởi chạy chương trình.
