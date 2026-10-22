# Automotive-App — Android Automotive 3D Interactive Simulator

Ứng dụng mô phỏng HMI (Human-Machine Interface) tương tác 3D thế hệ mới cho xe ô tô điện thể thao cao cấp (Sports EV Sedan), được xây dựng hoàn toàn bằng Kotlin, Jetpack Compose, Material Design 3 và Clean Architecture đa module.

---

### Yêu cầu môi trường:
- Android Studio Ladybug hoặc mới hơn.
- JDK 17 / JDK 21 daemon toolchain.
- Android SDK Platform 35+ (Tương thích Android 10+ và Android Automotive OS).

### Lệnh:
```bash
# Kiểm tra và chạy unit tests
./gradlew testDebugUnitTest

# Biên dịch gói APK Debug
./gradlew assembleDebug

# Cài đặt và khởi chạy trên thiết bị / Máy ảo Automotive đang kết nối
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.android.car.automotiveapp/.MainActivity
```
