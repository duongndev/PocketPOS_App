# PocketPOS App - Hệ thống Quản lý Bán hàng Thông minh (Android POS)

[![Android](https://img.shields.io/badge/Platform-Android-3DDC84?style=flat&logo=android&logoColor=white)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0%2B-7F52FF?style=flat&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-Material_3-4285F4?style=flat&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Hilt](https://img.shields.io/badge/DI-Hilt-00599C?style=flat)]()
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

**PocketPOS** là giải pháp phần mềm quản lý bán hàng (Point of Sale) hiện đại dành cho thiết bị di động Android. Ứng dụng được thiết kế tối ưu cho các cửa hàng bán lẻ, quán cà phê, và doanh nghiệp F&B, giúp tự động hóa quy trình bán hàng, quét mã vạch sản phẩm, quản lý kho hàng và thanh toán không tiền mặt một cách nhanh chóng, chính xác.

---

## Các tính năng trong ứng dụng

### 1. Xác thực & Phân quyền (Authentication)
- Đăng nhập, Đăng ký tài khoản quản lý / thu ngân an toàn.
- Quản lý phiên làm việc với Token-based Authentication (JWT qua OkHttp Interceptor).

### 2. Quản lý Sản phẩm & Danh mục (Product & Category Management)
- Quản lý danh mục sản phẩm: Thêm, sửa, xóa danh mục sản phẩm.
- Quản lý sản phẩm chi tiết: Thêm, sửa, xóa sản phẩm.
- Hỗ trợ phân trang (Paging 3) danh sách sản phẩm và danh mục.

### 3. Quét mã vạch (Barcode Scanner)
- Tích hợp **CameraX** và **Google ML Kit Barcode Scanning**.
- Nhận diện mã vạch sản phẩm tức thì ngay trên camera preview để thêm nhanh vào giỏ hàng.

### 4. Thanh toán QR tự động & Real-time (Checkout & Socket.io)
- Tạo mã VietQR/SePay tự động kèm theo số tiền và nội dung chuyển khoản theo đơn hàng.
- Lắng nghe trạng thái thanh toán thành công theo thời gian thực (Real-time) thông qua **Socket.io** — tự động chuyển màn hình khi khách hàng chuyển khoản thành công.

### 5. Quản lý Đơn hàng (Order Management)
- Theo dõi toàn bộ lịch sử giao dịch và trạng thái đơn hàng.
- Xem chi tiết từng đơn hàng, mặt hàng đã mua và thông tin thanh toán.

### 6. Báo cáo & Thống kê doanh thu (Statistics)
- Biểu đồ doanh thu trực quan, sinh động sử dụng thư viện **Vico Chart**.
- Thống kê tổng quan doanh thu, sản phẩm bán chạy và hiệu suất bán hàng.

### 7. Cài đặt cửa hàng(Settings)
- Quản lý thông tin cửa hàng, tài khoản ngân hàng nhận tiền.

---

## Công nghệ & Thư viện sử dụng

| Lĩnh vực | Thư viện / Công nghệ | Mô tả                                        |
| :--- | :--- |:---------------------------------------------|
| **Ngôn ngữ** | [Kotlin](https://kotlinlang.org) |  Kotlin                                      |
| **Giao diện** | [Jetpack Compose](https://developer.android.com/jetpack/compose) & Material 3 | Giao diện khai báo hiện đại                  |
| **Dependency Injection** | [Dagger Hilt](https://developer.android.com/training/dependency-injection/hilt-android) | Quản lý phụ thuộc biên dịch                  |
| **Bất đồng bộ** | [Coroutines & Flow](https://kotlinlang.org/docs/coroutines-overview.html) | Xử lý luồng dữ liệu bất đồng bộ phản ứng     |
| **Điều hướng** | [Navigation Compose](https://developer.android.com/jetpack/compose/navigation) | Định tuyến màn hình Type-safe                |
| **Networking** | [Retrofit 2](https://square.github.io/retrofit/) & [OkHttp 5](https://square.github.io/okhttp/) | HTTP Client & REST API                       |
| **Serialization** | [Moshi](https://github.com/square/moshi) | JSON Parsing & Code Generation               |
| **Cơ sở dữ liệu** | [Room Database](https://developer.android.com/training/data-storage/room) & DataStore | Lưu trữ cục bộ & Preferences                 |
| **Phân trang** | [Paging 3](https://developer.android.com/topic/libraries/architecture/paging/v3-overview) | Tải dữ liệu phân trang hiệu quả              |
| **Hình ảnh** | [Coil 3](https://coil-kt.github.io/coil/) | Tải ảnh bất đồng bộ tối ưu cho Compose       |
| **Camera & QR** | [CameraX](https://developer.android.com/training/camerax) & [ML Kit](https://developers.google.com/ml-kit/vision/barcode-scanning) | Quét mã vạch & mã QR sản phẩm                |
| **Real-time** | [Socket.io Client](https://socket.io/docs/v4/client-api/) | Đồng bộ trạng thái thanh toán thời gian thực |
| **Biểu đồ** | [Vico Chart](https://github.com/patrykandpatrick/vico) | Vẽ biểu đồ thống kê doanh thu                |
| **Logging & Debug** | [Timber](https://github.com/JakeWharton/timber) & [LeakCanary](https://github.com/square/leakcanary) | Ghi log hệ thống & Phát hiện memory leak     |

---

## Hướng dẫn Cài đặt & Chạy dự án

### 1. Yêu cầu hệ thống
- **Android Studio:** Ladybug (2024.2.1) hoặc phiên bản mới hơn.
- **JDK:** Java 11 trở lên.
- **Thiết bị/Máy ảo:** Android SDK 24 (Android 7.0) trở lên. Khuyến nghị Android 14+ (API 34/35/36).

### 2. Các bước thiết lập
1. **Clone repository:**
   ```bash
   git clone https://github.com/duongnd/PocketPOS_App.git
   ```
2. **Mở dự án:**
   Mở Android Studio, chọn **Open** và dẫn tới thư mục vừa clone.
3. **Cấu hình biến môi trường / API Endpoint:**
   Trong file `gradle.properties` (hoặc tạo mới nếu chưa có), cấu hình địa chỉ Backend API:
   ```properties
   BASE_URL="https://your-api-server.com/api/"
   ```
4. **Sync Gradle & Build:**
   Nhấn **Sync Now** trên Android Studio để tải các dependencies và chạy ứng dụng trên Emulator hoặc thiết bị thật.

---

## Thông tin Tác giả & Giấy phép

- **Tác giả:** duongnd (Alex)
- **Email:** [ducduong.contact@gmail.com](mailto:ducduong.contact@gmail.com)


