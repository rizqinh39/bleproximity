# 🛰️ BLE Proximity Tracker (Android Case Study)

**BLE Proximity Tracker** (`com.rizqinh.bleproximity`) is a production-ready Android application built for a **Mobile Engineer Technical Case Study**. The application provides real-time Bluetooth Low Energy (BLE) peripheral scanning, distance estimation, interactive concentric radar tracking, and offline persistence.

Built with **100% Kotlin**, **Jetpack Compose (Material 3)**, and **MVVM Clean Architecture**.

---

## 📸 Core Features & Screens

### 1. Dashboard Utama (Scanner Screen)
- **Real-Time BLE Scanning**: Scans BLE advertising signals using `BluetoothLeScanner` in `SCAN_MODE_LOW_LATENCY`.
- **Automatic Signal Sorting**: Devices are dynamically auto-sorted by strongest signal (highest RSSI first, e.g. -30 dBm before -80 dBm).
- **Search & Threshold Filtering**:
  - Search bar filtering by **Device Name** or **MAC Address**.
  - Interactive **Slider** for minimum RSSI threshold (-100 dBm to -30 dBm).
- **Manual Toggle & Banner Warning**: Manual Start/Stop scan toggle in TopAppBar, plus interactive warning banner when Bluetooth is turned off or runtime permissions are missing.

### 2. Detail Pelacakan (Radar View Screen)
- **Concentric Circles Radar Canvas**: Custom 2D Concentric Circles built with Jetpack Compose `Canvas` (`drawCircle`) and smooth `animateFloatAsState` / `animateColorAsState` ring pulse scaling.
- **Dynamic 6 Proximity Zone Colors**: Radar ring colors and active radius scale dynamically based on current RSSI category.
- **Signal Stability Metrics**: Real-time stats card displaying **Min RSSI**, **Average RSSI**, **Max RSSI**, **Total Packet Counter**, and **Estimated Distance**.
- **5-Second Watchdog Timer**: Automatically transitions a target device to **`< -90 dBm (Sinyal Hilang / Lost)`** if no advertising packets are captured for > 5 seconds.

### 3. Riwayat Perangkat (History Log Screen)
- **Local Persistence**: Powered by **Room Database** (`table_device_history`).
- **Automatic Upsert**: Every detected BLE device is automatically saved with its MAC address, name, last RSSI, category, and timestamp.
- **Sorted Log View**: Displays history logs sorted by `lastSeenTimestamp DESC` with a **"Clear All History"** button.

---

## 🎨 6 Proximity Signal Zones (Semantic Design System)

| Zone | RSSI Range | Label (Indonesian) | Distance Range | Color Token | Hex |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Zone 1** | `-10 to -30 dBm` | Sangat Kuat (Sangat Dekat) | `< 1 meter` | Emerald | `#10B981` |
| **Zone 2** | `-30 to -50 dBm` | Kuat (Dekat) | `1-3 meter` | Lime | `#84CC16` |
| **Zone 3** | `-50 to -70 dBm` | Cukup / Baik | `3-10 meter` | Amber | `#EAB308` |
| **Zone 4** | `-70 to -80 dBm` | Lemah | `10-20 meter` | Orange | `#F97316` |
| **Zone 5** | `-80 to -90 dBm` | Sangat Lemah / Putus-putus | `> 20 meter` | Red | `#EF4444` |
| **Zone 6** | `< -90 dBm` / >5s | Sinyal Hilang (Lost) | Terputus / Di luar jangkauan | Gray | `#94A3B8` |

---

## 📐 Architecture & Tech Stack

- **Pattern**: MVVM + Clean Architecture (Domain, Data, Presentation, Core).
- **UI Framework**: Jetpack Compose with Material 3.
- **Dependency Injection**: Hilt (`@HiltAndroidApp`, `@HiltViewModel`).
- **Local Database**: Room Database (`table_device_history`, `DeviceHistoryDao`).
- **Asynchronous / Reactive**: Kotlin Coroutines & `StateFlow` / `callbackFlow`.
- **Hardware & Permissions**: Bluetooth Low Energy API (`BluetoothLeScanner`), Android 12+ API 31+ permissions (`BLUETOOTH_SCAN`, `BLUETOOTH_CONNECT`), BroadcastReceiver (`BluetoothAdapter.ACTION_STATE_CHANGED`).

## 🛠️ Instruksi Kompilasi & Build

### Prasarat (Prerequisites)
- **JDK**: Java 17 (Rekomendasi: Temurin/OpenJDK 17)
- **Android Studio**: Jellyfish (2023.3.1) atau versi lebih baru
- **Min SDK**: 24 (Android 7.0) | **Target SDK**: 34 (Android 14)

### 1. Menjalankan Unit Test
```bash
./gradlew test
```

### 2. Build Debug APK
```bash
./gradlew assembleDebug
```
*Hasil APK:* `app/build/outputs/apk/debug/app-debug.apk`

### 3. Build Release APK
```bash
./gradlew assembleRelease
```
*Hasil APK:* `app/build/outputs/apk/release/app-release.apk`

---

## 🤖 AI Usage Disclosure
Sesuai dengan panduan pengerjaan, proyek ini dikembangkan dengan bantuan **Generative AI Agent** (sebagai Senior Android Architect & Mentor) untuk:
- Membantu merancang struktur proyek MVVM + Clean Architecture & Hilt DI module.
- Menyusun logika matematika estimasi jarak RSSI (`RssiDistanceCalculator`) dan algoritma *Watchdog Timer*.
- Mengatur konfigurasi otomatisasi CI/CD pada GitHub Actions (`android-release.yml`).
- Seluruh logika bisnis, penanganan izin runtime, dan komponen UI Jetpack Compose telah diverifikasi serta diuji melalui unit test.
