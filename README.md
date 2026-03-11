# PocketPOS App 📱

**PocketPOS** là một ứng dụng quản lý bán hàng, được xây dựng trên nền tảng Android. Ứng dụng giúp đơn giản hóa việc quản lý sản phẩm, đơn hàng và quét mã vạch trực tiếp trên thiết bị di động.

---

## 🚀 Công nghệ sử dụng

Dự án áp dụng kiến trúc hiện đại và các thư viện phổ biến nhất hiện nay:

- **Ngôn ngữ:** [Kotlin](https://kotlinlang.org/)
- **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose) (Khai báo giao diện hiện đại)
- **Dependency Injection:** [Hilt](https://developer.android.com/training/dependency-injection/hilt-android) 
- **Database:** [Room](https://developer.android.com/training/data-storage/room) (Lưu trữ dữ liệu cục bộ)
- **Networking:** [Retrofit 3](https://square.github.io/retrofit/) & [OkHttp](https://square.github.io/okhttp/) (Kết nối API)
- **JSON Parsing:** [Moshi](https://github.com/square/moshi) (với KSP codegen)
- **Asynchronous:** [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & [Flow](https://kotlinlang.org/docs/flow.html)
- **Image Loading:** [Coil 3](https://coil-kt.github.io/coil/)
- **Navigation:** [Jetpack Navigation Compose](https://developer.android.com/jetpack/compose/navigation)
- **Local Storage:** [DataStore Preferences](https://developer.android.com/topic/libraries/architecture/datastore)
- **Paging:** [Paging 3](https://developer.android.com/topic/libraries/architecture/paging/v3-paged-data) (Xử lý danh sách lớn)
- **Camera & Barcode:** [CameraX](https://developer.android.com/training/camerax) & [ML Kit Barcode Scanning](https://developers.google.com/ml-kit/vision/barcode-scanning)
- **Logging:** [Timber](https://github.com/JakeWharton/timber)
- **Memory Leak Detection:** [LeakCanary](https://square.github.io/leakcanary/) (chế độ debug)

---

## 🏗️ Kiến trúc dự án (Architecture)

Ứng dụng tuân thủ nguyên tắc **Clean Architecture** và mô hình **MVVM (Model-View-ViewModel)**:

- **UI Layer:** Jetpack Compose Screens & ViewModels.
- **Domain Layer:** Chứa các Business Logic (nếu cần mở rộng).
- **Data Layer:** Repository Pattern xử lý dữ liệu từ Remote (Retrofit) và Local (Room/DataStore).

---

## 🛠️ Yêu cầu hệ thống

- **Android Studio:** Ladybug trở lên.
- **JDK:** 11+
- **Minimum SDK:** API 24 (Android 7.0)
- **Target SDK:** API 36

---

## ✨ Tính năng chính

- [x] Quản lý danh mục và sản phẩm.
- [x] Quét mã vạch sản phẩm bằng Camera (ML Kit).
- [x] Lưu trữ dữ liệu offline với Room.
- [x] Phân trang danh sách sản phẩm.
- [x] Cấu hình cài đặt ứng dụng.

---

## 📄 Liên hệ

- **Tác giả:** duongnd
- **Dự án:** Cá nhân (PocketPOS)
- **Email:** ducduong.contact@gmail.com
