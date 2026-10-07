# Game Vault - Aplikasi Katalog dan Eksplorasi Video Game 🎮

Aplikasi mobile Android berbasis **Jetpack Compose** dan **Material Design 3** untuk mencari, mengeksplorasi, dan melihat informasi video game secara dinamis menggunakan REST API dari **RAWG**.

Dibuat untuk memenuhi seluruh spesifikasi pada modul:
**Responsi Mobile Programming - Aplikasi Katalog dan Eksplorasi Video Game**

---

## 📋 Daftar Isi
1. [Fitur Utama](#-fitur-utama)
2. [Persyaratan Teknis & Arsitektur (MVVM)](#-persyaratan-teknis--arsitektur-mvvm)
3. [Teknologi & Library](#-teknologi--library)
4. [Struktur Proyek](#-struktur-proyek)
5. [Integrasi REST API (RAWG)](#-integrasi-rest-api-rawg)
6. [Tampilan Antarmuka (Screens)](#-tampilan-antarmuka-screens)
7. [Petunjuk Menjalankan Aplikasi](#-petunjuk-menjalankan-aplikasi)

---

## 🌟 Fitur Utama

- **Katalog Video Game (Home Screen)**:
  - Menampilkan kumpulan game dalam layout modern responsif menggunakan `LazyVerticalGrid`.
  - Dilengkapi thumbnail cover art beresolusi tinggi, judul game, badge rating angka bintang, dan tanggal rilis format ISO 8601 (`YYYY-MM-DD`).
  - Badge Metacritic score untuk game populer.
- **Pencarian Real-Time (Search Functionality)**:
  - Search bar interaktif dengan teknik debounce coroutine (400ms) untuk optimasi panggilan API tanpa lag.
  - Pencarian mendukung nama judul game, genre, maupun platform.
- **Halaman Detail Game (Game Detail Screen)**:
  - Hero image banner dinamis dengan gradient overlay.
  - Informasi judul lengkap, rating angka (misal `4.7 / 5.0`), tanggal rilis ISO 8601, chip genre interaktif, label platform yang didukung, dan studio pengembang (developer).
  - Deskripsi lengkap game dengan pembersihan tag HTML otomatis (*clean description*).
  - Tombol navigasi kembali (*Back Navigation*) ke halaman utama.
- **State-Driven UI & Penanganan Recomposition**:
  - `UiState.Loading`: Animasi CircularProgressIndicator saat memuat data.
  - `UiState.Success`: Render daftar game atau tampilan detail.
  - `UiState.Error`: Tampilan error informatif dengan tombol *Coba Lagi* (*Retry*).
- **Konfigurasi Fleksibel API Key & Fallback Cerdas**:
  - Modal konfigurasi API Key langsung pada header aplikasi.
  - Mekanisme *Graceful Fallback* ke database lokal jika API key habis limit / offline / tidak disetel, menjamin aplikasi selalu dapat didemonstrasikan dengan lancar.

---

## 🏛 Persyaratan Teknis & Arsitektur (MVVM)

Aplikasi dibangun menggunakan pola arsitektur **MVVM (Model - View - ViewModel)** sesuai standar industri Android modern:

```
┌────────────────────────────────────────────────────────┐
│                   UI LAYER (View)                      │
│   HomeScreen, DetailScreen, GameCard, AppNavGraph      │
└───────────────────────────▲────────────────────────────┘
                            │ StateFlow / UiState
┌───────────────────────────┴────────────────────────────┐
│                  VIEWMODEL LAYER                       │
│             HomeViewModel, DetailViewModel             │
└───────────────────────────▲────────────────────────────┘
                            │ Coroutines / suspend
┌───────────────────────────┴────────────────────────────┐
│                  REPOSITORY LAYER                      │
│                   GameRepository                       │
└───────────────────────────▲────────────────────────────┘
                            │
              ┌─────────────┴─────────────┐
              ▼                           ▼
     [ RAWG REST API ]            [ Fallback Dataset ]
   (Retrofit & OkHttp)          (Curated Game Catalog)
```

1. **Model Layer (`data/model`)**:
   - `GameResponse.kt`: DTO untuk pemetaan JSON response list RAWG API.
   - `GameDetailResponse.kt`: DTO untuk detail game.
   - `GameItem.kt`: Domain model bersih yang memanfaatkan fitur Kotlin seperti `data class`, `null safety`, default parameters, dan computed properties.
2. **Network Layer (`data/api`)**:
   - `ApiService.kt`: Interface Retrofit mendefinisikan endpoint `@GET("games")` dan `@GET("games/{id}")`.
   - `ApiClient.kt`: Singleton Retrofit dengan OkHttp logging interceptor, timeout configuration, dan Gson converter.
3. **Repository Layer (`data/repository`)**:
   - `GameRepository.kt`: Mengelola abstraksi sumber data, menangani pemanggilan jaringan secara asynchronous menggunakan coroutines (`Dispatchers.IO`), serta menyediakan fallback data.
4. **ViewModel Layer (`ui/viewmodel`)**:
   - `HomeViewModel.kt`: Mengelola query pencarian, debounce flow, filter game, dan state UI (`UiState<List<GameItem>>`).
   - `DetailViewModel.kt`: Mengelola pemuatan detail game spesifik berdasarkan ID.
5. **View / UI Layer (`ui/screen`, `ui/navigation`, `ui/theme`)**:
   - Dibuat secara deklaratif penuh dengan Jetpack Compose & Material 3.

---

## 🛠 Teknologi & Library

- **Bahasa Pemrograman**: Kotlin 2.2+ (Data classes, Null Safety, Coroutine Flows, Extension functions, Lambdas).
- **UI Framework**: Android Jetpack Compose BOM 2026.02.01.
- **Design System**: Material Design 3 (`androidx.compose.material3`).
- **Networking**: Square Retrofit 2.11.0 + Gson Converter 2.11.0.
- **HTTP Client**: Square OkHttp 4.12.0 + Logging Interceptor.
- **Asynchronous**: Kotlin Coroutines & StateFlow.
- **Image Loading**: Coil Compose 2.7.0.
- **Navigasi**: Jetpack Navigation Compose 2.8.8.
- **Lifecycle & Architecture**: ViewModel Compose 2.8.7.

---

## 📁 Struktur Proyek

```
app/src/main/java/com/example/responsipraktikum/
├── MainActivity.kt                  # Entry point aplikasi & inisialisasi NavController
├── data/
│   ├── api/
│   │   ├── ApiClient.kt             # Singleton Retrofit & OkHttpClient
│   │   └── ApiService.kt            # Definisi endpoint REST API RAWG
│   ├── model/
│   │   ├── GameItem.kt              # Domain model video game untuk UI
│   │   ├── GameResponse.kt          # DTO Response daftar game
│   │   └── GameDetailResponse.kt    # DTO Response rincian game
│   └── repository/
│       └── GameRepository.kt        # Repository pengelola data API & fallback
└── ui/
    ├── navigation/
    │   ├── Screen.kt                # Sealed class rute navigasi (Home & Detail)
    │   └── NavGraph.kt              # Setup Compose NavHost
    ├── screen/
    │   ├── HomeScreen.kt            # Layar katalog: Search bar, Grid list, Rating badge
    │   └── DetailScreen.kt          # Layar detail: Hero image, Rating, Rilis, Deskripsi
    ├── state/
    │   └── UiState.kt               # Sealed interface state (Loading, Success, Error)
    ├── theme/
    │   ├── Color.kt                 # Cyber gaming color palette
    │   ├── Theme.kt                 # Material 3 theme configuration
    │   └── Type.kt                  # Typography
    └── viewmodel/
        ├── HomeViewModel.kt         # State holder & business logic katalog game
        └── DetailViewModel.kt       # State holder & business logic detail game
```

---

## 🌐 Integrasi REST API (RAWG)

- **Base URL**: `https://api.rawg.io/api/`
- **Endpoints yang digunakan**:
  1. `GET /api/games?key={API_KEY}&search={query}&ordering=-rating`
     - Mengambil daftar video game terpopuler atau hasil pencarian.
  2. `GET /api/games/{id}?key={API_KEY}`
     - Mengambil rincian lengkap spesifik suatu video game.
- **Data yang ditampilkan**:
  1. Nama game (`name`)
  2. Rating dalam angka (`rating`, misal: `4.5`)
  3. Tanggal rilis format ISO 8601 (`released`, contoh: `YYYY-MM-DD` / `2026-12-31`)
  4. Deskripsi game (`description` / `description_raw`)
  5. Fitur tambahan: Gambar sampul (`background_image`), Skor Metacritic, Daftar Genre, Daftar Platform, dan Pengembang.

---

## 📱 Tampilan Antarmuka (Screens)

1. **Home Screen**:
   - Header aplikasi dengan ikon joystick dan judul *Game Vault*.
   - Tombol pengaturan kunci API (*Key Icon*) pada TopAppBar.
   - Kolom pencarian dinamis (*Search Bar*) dengan tombol hapus cepat (*Clear Button*).
   - Kartu Game Grid (`LazyVerticalGrid`) dengan sudut membulat (*Rounded Corner*), efek elevasi, rating bintang, dan tag tanggal rilis ISO 8601.
2. **Game Detail Screen**:
   - Gambar sampul heroik (*Hero Cover Image*) dengan efek gradien transisi halus ke latar belakang.
   - Judul game tipografi tebal (*Extra Bold*).
   - Baris badge metrik: Rating bintang emas, Tanggal Rilis format ISO 8601, dan Skor Metacritic hijau.
   - Chip genre bernuansa Cyber Violet.
   - Tag platform yang kompatibel.
   - Bagian Deskripsi Game dengan format teks bersih dan nyaman dibaca.

---

## 🚀 Petunjuk Menjalankan Aplikasi

1. **Buka Proyek di Android Studio**:
   - Buka Android Studio, pilih **Open** dan arahkan ke direktori proyek ini.
2. **Sinkronisasi Gradle**:
   - Android Studio akan secara otomatis mengunduh dependensi Gradle.
3. **Konfigurasi API Key (Opsional)**:
   - Dapatkan API Key gratis di [RAWG API Docs](https://rawg.io/apidocs).
   - Masukkan API Key pada `ApiClient.kt` atau langsung melalui tombol ikon kunci pada tampilan Home Screen aplikasi.
   - *(Catatan: Aplikasi telah dilengkapi katalog fallback otomatis sehingga tetap berfungsi penuh meskipun tanpa API key).*
4. **Jalankan Aplikasi**:
   - Pilih emulator atau perangkat fisik Android (Min SDK: 29 / Android 10+).
   - Klik tombol **Run 'app'** (Shift + F10).
