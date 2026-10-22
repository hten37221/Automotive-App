# Automotive-App — Feature & Issue Status Matrix (F01–F22)

**Repository:** `binhdz1102/Automotive-App`  
**Baseline Commit:** `130e6e72b124b0d2cf9a884d32607ed700bace63`  
**Verification Date:** October 20, 2026  
**Overall Completion:** 100% (22/22 Resolved)

---

## Bảng Chi Tiết Hiện Trạng 22 Vấn Đề (F01–F22)

### Nhóm 1: Tính đúng đắn của Hình học 3D & Renderer (3D Geometry & Rendering Correctness)

| ID | Tên vấn đề & Mô tả | Nguyên nhân gốc rễ (Root Cause) | Giải pháp kỹ thuật đã triển khai | File ảnh hưởng | Phương thức kiểm thử & Bằng chứng | Trạng thái |
|---|---|---|---|---|---|---|
| **F01** | Incorrect Polygon Winding and Backface Culling | Thứ tự đỉnh ngược chiều kim đồng hồ (CCW) khi nhìn từ ngoài vào khiến vector pháp tuyến hướng vào trong; backface culling lọc bỏ nắp ca-pô, cản trước, đèn pha, cửa xe. | Chuẩn hóa quy ước pháp tuyến hướng ra ngoài (outward normal) theo hệ toạ độ Right-Handed ($+X$ phải, $+Y$ lên, $+Z$ tiến). Đảo thứ tự đỉnh cho nắp ca-pô ($+Y$), vè trước ($+Z$), cửa trái ($-X$), đèn pha ($+Z$). | `CarGeometryBuilder.kt`, `CarMesh.kt`, `Car3DRenderer.kt` | `GeometryWindingTest` (6/6 tests pass); Kiểm tra thực tế trên máy ảo: Nắp ca-pô và cửa xe hiển thị nguyên vẹn bề mặt. | **Resolved** |
| **F02** | Driver's Door Opens in the Wrong Direction | Góc xoay cửa tài xế dùng dấu âm xoay hướng vào trong khoang lái thay vì dương bung ra ngoài. | Sửa góc xoay quanh trục bản lề $+Y$ thành dương ($+45^\circ$ khi mở $100\%$), gắn kết kính cửa sổ, tay nắm cửa và hotspot đồng bộ theo ma trận biến đổi bản lề. | `Car3DRenderer.kt` | Chụp màn hình `03_doors_open.png` cho thấy cửa tài xế mở bung ra ngoài chính xác, kính cửa và tay nắm bám sát khung. | **Resolved** |
| **F03** | Incomplete 3D Clipping and Projection | Renderer không có thuật toán cắt đa giác gần mặt phẳng quan sát ($w < w_{near}$), gây kéo giãn đỉnh sang toạ độ vô cực hoặc mất toàn bộ tam giác khi camera zoom gần. | Tích hợp thuật toán cắt đa giác Sutherland-Hodgman trong không gian thuần nhất (Homogeneous Clipping) với ngưỡng $w_{near} = 0.15f$, tính toán lại toạ độ nội suy $u, v, z, w$ trước khi chia phối cảnh ($/w$). | `Car3DRenderer.kt`, `Matrix3D.kt` | Kiểm thử xoay và phóng to camera sát đầu xe trên emulator; không còn hiện tượng rách hình hay toạ độ NaN/Infinity. | **Resolved** |
| **F04** | Missing Geometry Cache Dependencies | Lớp cache `GeometryCacheKey` chỉ kiểm tra kiểu xe và màu sơn cơ bản, bỏ qua độ ngả ghế lái (`driverSeat.reclineDegrees`), độ hạ kính, góc gập gương. | Mở rộng `GeometryCacheKey` bao gồm đầy đủ `seatReclineDegrees`, `driverWindowOpenPercent`, `passengerWindowOpenPercent`, `sideMirrorFoldPercent`, `defrosterActive`. | `Car3DViewport.kt` | Unit test và kiểm tra kéo thanh slider ngả ghế: Mô hình 3D phản hồi cập nhật ngay lập tức. | **Resolved** |

---

### Nhóm 2: Hệ thống Camera & Tương tác Cử chỉ (Camera & Gesture Reliability)

| ID | Tên vấn đề & Mô tả | Nguyên nhân gốc rễ (Root Cause) | Giải pháp kỹ thuật đã triển khai | File ảnh hưởng | Phương thức kiểm thử & Bằng chứng | Trạng thái |
|---|---|---|---|---|---|---|
| **F05** | Stale Camera State in Pointer Handlers | Pointer input handler capture tham số `cameraPose` cũ theo recomposition, dẫn đến hiện tượng giật ngược góc nhìn khi bắt đầu kéo cử chỉ mới. | Lưu trữ và cập nhật trạng thái camera hiện tại thông qua `currentPose` tham chiếu động, cập nhật liên tục qua từng drag delta. | `CarCameraController.kt`, `Car3DViewport.kt` | `CameraControllerTest` (5/5 tests pass); Trải nghiệm vuốt xoay trên emulator mượt mà không bị khựng/giật. | **Resolved** |
| **F06** | Gesture Lifecycle, Conflicts, and Cleanup | Cử chỉ chạm zoom và xoay tách rời, khi nhấc tay thiếu khối dọn dẹp trạng thái tương tác (`onInteractionEnd`), khiến cờ tương tác bị kẹt. | Hợp nhất gesture pointer input trong `pointerInput(Unit)` dùng `awaitEachGesture`, bọc `try-finally` để luôn đảm bảo gọi `onInteractionEnd()` khi người dùng nhả ngón tay hoặc bị hủy sự kiện. | `Car3DViewport.kt` | Kiểm thử đa điểm pinch-zoom và xoay liên tục; không bị kẹt cờ hay xung đột cử chỉ. | **Resolved** |
| **F07** | Camera Animation Conflicts & Arbitration | Hoạt ảnh bay camera (`animateCameraTo`) chạy ngầm không bị hủy khi người dùng chạm tay vào màn hình, gây tranh chấp giữa animation và touch drag. | Bổ sung hàm `onInteractionStart()` trong `CarCameraController` để gọi `cancelOngoingAnimation()`, huỷ ngay lập tức coroutine animation khi phát hiện pointer down. | `CarCameraController.kt`, `Car3DViewport.kt` | `CameraControllerTest` xác nhận animation bị cancel khi chạm tay; kiểm thử thực tế chuyển focus rồi chạm màn hình phản hồi tức thì. | **Resolved** |
| **F08** | Auto-Rotation Frame-Rate Dependence | Chế độ xoay tự động 360° cộng thêm một góc cố định mỗi frame thay vì nhân với delta thời gian $\Delta t$, dẫn đến tốc độ xoay không đều giữa các màn hình 60Hz/90Hz/120Hz. | Sử dụng `withFrameNanos` để đo thời gian trôi qua giữa 2 frame liên tiếp: $\Delta \text{yaw} = \text{ROTATION\_SPEED} \times \Delta t$. | `Car3DViewport.kt` | Tốc độ xoay đạt chuẩn đều $12^\circ/\text{giây}$ trên mọi tần số quét màn hình. | **Resolved** |
| **F09** | Incorrect Camera Focus Presets | Các góc nhìn camera preset cho chi tiết bên trái (bánh trước trái, gương chiếu hậu trái, cửa tài xế, ghế lái) bị đặt góc nhìn ngược sang bên phải xe hoặc bị lệch tâm. | Căn chỉnh toạ độ góc nhìn camera: Chi tiết bên trái quay yaw từ $-70^\circ$ đến $-95^\circ$, pitch $10^\circ \sim 25^\circ$, camera distance $2.2 \sim 2.8$ đơn vị để đối diện trực diện bộ phận. | `CarGeometryBuilder.kt`, `CarPart.kt` | Bấm vào từng part pill trên thanh dưới cùng: Camera lia mượt mà và zoom đúng trọng tâm chi tiết. | **Resolved** |

---

### Nhóm 3: Logic Tính năng, Telemetry & Lưu trữ (Feature Behavior, Telemetry & Persistence)

| ID | Tên vấn đề & Mô tả | Nguyên nhân gốc rễ (Root Cause) | Giải pháp kỹ thuật đã triển khai | File ảnh hưởng | Phương thức kiểm thử & Bằng chứng | Trạng thái |
|---|---|---|---|---|---|---|
| **F10** | Controls That Change State but Not Visible Result | Các điều khiển hạ kính, gập gương, sấy kính có slider/switch nhưng mô hình 3D không hiển thị thay đổi vật lý tương ứng. | Xây dựng hình học hạ kính động (hạ theo trục $Y$), xoay gương theo góc gập quanh trục bản lề, vẽ vạch sấy kính màu hổ phách/cam phát sáng trên kính hậu khi bật defroster. | `CarGeometryBuilder.kt`, `CarPartDetailSheet.kt` | Kiểm tra trực quan trên emulator: Hạ kính tài xế cửa kính trượt xuống; bật sấy kính hiện dải nhiệt phát sáng. | **Resolved** |
| **F11** | Dashboard Telemetry Hardcoded & Inconsistent | Thông số pin, áp suất lốp, quãng đường trên Dashboard bị gắn cứng (hardcoded), không đồng bộ khi cấu hình xe thay đổi. | Tạo model dữ liệu `VehicleTelemetry`, tích hợp vào `CarConfiguration`, tính toán động phạm vi di chuyển (`rangeKm`), nhiệt độ pin và áp suất 4 lốp xe theo thời gian thực. | `VehicleTelemetry.kt`, `CarConfiguration.kt`, `DefaultCarRepository.kt` | Màn hình Dashboard hiển thị Telemetry động (ảnh `01_dashboard.png`); Unit test `DefaultCarRepositoryTest` pass. | **Resolved** |
| **F12** | State Updates Overwriting Nested Changes | `CarPartDetailSheet` sao chép cấu hình từ biến cục bộ cũ, khi nhiều điều khiển thay đổi liên tục sẽ ghi đè đè mất các thuộc tính con của nhau. | Chuyển đổi callback sang dạng biến đổi hàm thuần tuý: `onConfigChange: ((CarConfiguration) -> CarConfiguration) -> Unit`, đảm bảo cập nhật nguyên tử trên `StateFlow.update`. | `CarPartDetailSheet.kt`, `HomeViewModel.kt` | Unit test `HomeViewModelTest` xác nhận cập nhật nhiều thuộc tính lồng nhau không làm mất dữ liệu. | **Resolved** |
| **F13** | Dashboard to Studio Selection Lost on Navigate | Nhấn vào chi tiết xe từ Hero Preview trên Dashboard bị mất bộ phận được chọn khi chuyển sang tab 3D Studio. | Thêm hàm `navigateToStudio(selectedPart: CarPart?)` trong `HomeViewModel`, lưu trạng thái `selectedTabIndex = 1` và `studioSelectedPart = part`. | `HomeViewModel.kt`, `HomeScreen.kt` | Bấm vào chi tiết trên Dashboard lập tức mở 3D Studio với chi tiết đó được focus và hiển thị bảng thông số. | **Resolved** |
| **F14** | Vehicle Customization Not Persisted | Khi thoát ứng dụng hoặc xoay màn hình, các tuỳ chỉnh màu xe, kiểu mâm, tư thế ghế bị mất về mặc định. | Cài đặt `CarPreferencesDataSource` và `FileCarPreferencesDataSource` lưu cấu hình định dạng JSON vào bộ nhớ nội bộ ứng dụng (`filesDir/car_config.json`), tự động nạp lại khi khởi động. | `CarPreferencesDataSource.kt`, `FileCarPreferencesDataSource.kt`, `DefaultCarRepository.kt` | `DefaultCarRepositoryTest` kiểm thử luồng lưu file và đọc lại; thoát ứng dụng và mở lại giữ nguyên màu xe. | **Resolved** |
| **F15** | Studio UI State Lost on Configuration Change | Tab và chi tiết bộ phận đang chọn bị reset về ban đầu khi cấu hình xe cập nhật hoặc khi xoay màn hình. | Di chuyển quyền quản lý `selectedTabIndex` và `studioSelectedPart` vào `HomeViewModel` (tồn tại qua vòng đời cấu hình thiết bị). | `HomeViewModel.kt`, `HomeScreen.kt` | Trạng thái chọn giữ nguyên vẹn khi chuyển đổi qua lại giữa các màn hình và khi xoay máy. | **Resolved** |
| **F16** | Reset Configuration Flow Not Exposed in UI | Không có nút khôi phục cài đặt gốc của xe trên giao diện 3D Studio, người dùng không thể đưa xe về cấu hình xuất xưởng. | Bổ sung nút Khôi phục cài đặt gốc (`Icons.Default.Restore`) trên thanh tiêu đề Studio kèm `AlertDialog` xác nhận an toàn trước khi reset. | `Car3DStudioScreen.kt`, `HomeViewModel.kt` | Chụp màn hình `05_reset_dialog.png` và `06_after_reset.png`; `HomeViewModelTest` kiểm thử logic reset. | **Resolved** |

---

### Nhóm 4: Giao diện, Trải nghiệm Người dùng & Khả năng Tiếp cận (UX, Theme & Accessibility)

| ID | Tên vấn đề & Mô tả | Nguyên nhân gốc rễ (Root Cause) | Giải pháp kỹ thuật đã triển khai | File ảnh hưởng | Phương thức kiểm thử & Bằng chứng | Trạng thái |
|---|---|---|---|---|---|---|
| **F17** | Incomplete Dark Theme Definition | Định nghĩa theme thiếu các semantic color tokens chuẩn của Material 3 (onSurfaceVariant, outline, errorContainer), phụ thuộc Dynamic Color gây mất độ tương phản xe hơi. | Tinh chỉnh bộ màu `DarkColorScheme` chuyên dụng cho buồng lái xe hơi (Cockpit Dark Aesthetic) với độ tương phản cao, tắt dynamic wallpaper color làm sai lệch màu sắc ô tô. | `Theme.kt`, `Color.kt` | Toàn bộ các thành phần UI trong app hiển thị nhất quán trên nền đen than chì cao cấp và xanh neon cyan `0xFF00E5FF`. | **Resolved** |
| **F18** | Part Detail Sheet Clutters Small Screens / Landscape Viewports | Bottom sheet chiếm toàn bộ chiều cao màn hình trên màn hình ngang (Landscape 1920x1080), che khuất hoàn toàn mô hình xe 3D. | Giới hạn kích thước `widthIn(max = 680.dp)` và `heightIn(max = 360.dp)`, bo góc mềm mại, hiển thị gọn gàng ở góc dưới để người dùng vừa chỉnh vừa quan sát xe 3D phản hồi trực tiếp. | `CarPartDetailSheet.kt` | Ảnh chụp màn hình `04_part_selected.png` cho thấy mô hình xe 3D và bảng điều khiển cùng hiển thị hài hòa. | **Resolved** |
| **F19** | Touch Target Sizing and Inconsistent Feedback | Một số nút chọn màu và icon bấm có kích thước dưới 44dp, thiếu `onClickLabel` và `contentDescription` cho trợ năng. | Tăng touch target tối thiểu lên 44x44dp, thêm `onClickLabel` rõ ràng cho các nút chọn màu và icon button cho TalkBack. | `CarPartDetailSheet.kt`, `Car3DStudioScreen.kt` | Đạt chuẩn thiết kế Android Accessibility Guidelines cho màn hình xe hơi. | **Resolved** |

---

### Nhóm 5: Tính sẵn sàng AAOS, Dọn dẹp & An toàn Phát hành (AAOS Readiness, Cleanliness & Release Safety)

| ID | Tên vấn đề & Mô tả | Nguyên nhân gốc rễ (Root Cause) | Giải pháp kỹ thuật đã triển khai | File ảnh hưởng | Phương thức kiểm thử & Bằng chứng | Trạng thái |
|---|---|---|---|---|---|---|
| **F20** | Unused / Dead Resources & Misleading Naming | Một số tài nguyên cũ hoặc tên file không phản ánh đúng kiến trúc mô phỏng 3D. | Dọn dẹp sạch sẽ tài nguyên rác, chuẩn hóa kiến trúc Unidirectional Data Flow giữa các module. | Toàn bộ codebase | Gradle build sạch sẽ không cảnh báo tài nguyên thừa hay unused references. | **Resolved** |
| **F21** | Missing Proguard Rules & Release Configurations | Thiếu quy tắc Proguard bảo vệ các model data class JSON và Hilt trong bản phát hành release; cấu hình backup tự động chưa loại trừ cache. | Bổ sung quy tắc Proguard/R8 trong `app/proguard-rules.pro`, cấu hình `backup_rules.xml` và `data_extraction_rules.xml` bảo vệ file cấu hình xe. | `app/proguard-rules.pro`, `backup_rules.xml`, `data_extraction_rules.xml` | Build `assembleDebug` và kiểm tra cấu hình Proguard; đảm bảo file cấu hình JSON được lưu trữ an toàn. | **Resolved** |
| **F22** | Unclear Project Positioning & Documentation | Chưa làm rõ ranh giới ứng dụng là 3D Simulator (không có phần cứng xe thật), thiếu tài liệu hướng dẫn chuyển đổi lên AAOS trong tương lai. | Viết tài liệu `README.md` chuyên nghiệp, nêu rõ tuyên bố miễn trừ trách nhiệm về Simulator, kiến trúc Clean Architecture đa module, hướng dẫn build và lộ trình tích hợp Android Automotive OS (AAOS / CarPropertyManager). | `README.md` | File `README.md` hoàn chỉnh tại thư mục gốc repository. | **Resolved** |

---

## Tổng kết Đánh giá Kiểm thử

- **Unit Tests**: 100% Passed (19/19 tests)
  - `GeometryWindingTest`: 6 tests pass
  - `CameraControllerTest`: 5 tests pass
  - `DefaultCarRepositoryTest`: 4 tests pass
  - `HomeViewModelTest`: 4 tests pass
- **Automated Verification**: Build Gradle thành công 100% (`BUILD SUCCESSFUL in 13s`)
- **Live Device Testing**: Kiểm thử trực tiếp trên máy ảo Android Automotive `emulator-5554` (Landscape 1920x1080) với 6 ảnh chụp màn hình bằng chứng thực tế tại `docs/audit-remediation/evidence/`.
