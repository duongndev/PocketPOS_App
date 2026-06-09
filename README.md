# 📱 PocketPOS App

**PocketPOS** là giải pháp quản lý bán hàng hiện đại dành cho thiết bị di động Android. Ứng dụng giúp đơn giản hóa quy trình bán hàng, từ quét mã vạch sản phẩm đến thanh toán nhanh qua mã QR tự động.

---

## ✨ Tính năng nổi bật

- [x] **Quản lý sản phẩm & Danh mục:** Quản lý kho hàng thông minh, hỗ trợ biến thể sản phẩm.
- [x] **Quét mã vạch siêu tốc:** Tích hợp CameraX và ML Kit Barcode Scanning để quét sản phẩm tức thì.
- [x] **Thanh toán QR Tự động:** Tích hợp VietQR/SePay, tự động tạo mã QR kèm số tiền và nội dung chuyển khoản.
- [x] **Cập nhật thời gian thực (Real-time):** Sử dụng **Socket.io** để lắng nghe tín hiệu thanh toán thành công và tự động cập nhật UI ngay khi khách chuyển tiền.
- [x] **Quản lý đơn hàng:** Theo dõi lịch sử giao dịch, trạng thái thanh toán và chi tiết đơn hàng.
- [x] **Báo cáo & Thống kê:** Biểu đồ doanh thu trực quan sử dụng thư viện Vico Chart.

---

## 🚀 Công nghệ sử dụng

Dự án áp dụng kiến trúc **Clean Architecture** kết hợp với **MVVM** và các công nghệ Android mới nhất:

| Thành phần | Công nghệ |
| :--- | :--- |
| **Giao diện** | [Jetpack Compose](https://developer.android.com/jetpack/compose) (Khai báo giao diện hiện đại) |
| **Dependency Injection** | [Hilt](https://developer.android.com/training/dependency-injection/hilt-android) |
| **Networking** | [Retrofit 2](https://square.github.io/retrofit/) & [OkHttp](https://square.github.io/okhttp/) |
| **Real-time Communication** | [Socket.io Client](https://socket.io/docs/v4/client-api/) |
| **Xử lý bất đồng bộ** | [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & [Flow](https://kotlinlang.org/docs/flow.html) |
| **Hình ảnh** | [Coil 3](https://coil-kt.github.io/coil/) |
| **Điều hướng** | [Navigation Compose](https://developer.android.com/jetpack/compose/navigation) |
| **Thị giác máy tính** | [ML Kit Barcode Scanning](https://developers.google.com/ml-kit/vision/barcode-scanning) |
| **Biểu đồ** | [Vico Chart](https://github.com/patrykandpatrick/vico) |

---

## 🏗️ Kiến trúc dự án

Dự án được tổ chức theo cấu trúc module hóa, tuân thủ nguyên tắc Clean Architecture:
- **`core/`**: Chứa các thành phần dùng chung (DI, Navigation, Utils, Theme).
- **`data/`**: Xử lý dữ liệu (API, Repository Implementation, DTO, Room Entities/DAO).
- **`domain/`**: Chứa Business Logic và Repository Interfaces.
- **`feature/`**: Chứa các màn hình và ViewModel theo tính năng (Scanner, Checkout, Statistics, v.v.).

---

## 🛠️ Cài đặt

1. **Yêu cầu hệ thống:**
   - Android Studio Ladybug trở lên.
   - JDK 11+.
   - Thiết bị chạy Android 7.0 (API 24) trở lên.

2. **Các bước thực hiện:**
   ```bash
   # Clone project
   git clone https://github.com/duongnd/PocketPOS_App.git

   # Mở dự án trong Android Studio và Sync Gradle
   ```

3. **Cấu hình API:**
   Thay đổi URL API và Socket trong `NetworkModule.kt` và `CheckoutViewModel.kt` để trỏ về server của bạn.

---

## 📄 Thông tin tác giả

- **Tác giả:** duongnd
- **Email:** [ducduong.contact@gmail.com](mailto:ducduong.contact@gmail.com)
- **Dự án:** Cá nhân (PocketPOS)

---
*Phát triển với ❤️ bởi duongnd.*
