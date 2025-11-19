<p align="center">
  <img 
    src="https://pub-217c759e94154463a5870a703a1743c4.r2.dev/readme/Artboard%204%20copy%208.png" 
    width="120" 
    alt="SpotiXe Logo"
  />
</p>
<h1 align="center">SpotiXe</h1>

<p align="center">
  <img src="https://img.shields.io/badge/status-active-brightgreen" />
  <img src="https://img.shields.io/badge/platform-Android%20%7C%20Web%20%7C%20API-blue" />
  <img src="https://img.shields.io/badge/tech-.NET%208%20%7C%20React%2018%20%7C%20Kotlin-orange" />
  <img src="https://img.shields.io/badge/license-MIT-lightgrey" />
</p>

<p align="center">
  Nền tảng streaming nhạc gồm Mobile App Kotlin, Web Admin React và Web API .NET.
</p>

SpotiXe là bộ sản phẩm nghe nhạc full-stack bao gồm **mobile app Kotlin/Jetpack Compose**, **web admin React**, **Web API ASP.NET Core** và **cơ sở dữ liệu MySQL**. Repository này gom toàn bộ mã nguồn và kịch bản triển khai để bạn có thể tự dựng môi trường phát triển hoặc demo nhanh.

## Kiến trúc tổng quát

| Thành phần           | Công nghệ chính                                | Mục đích                                        |
| -------------------- | ---------------------------------------------- | ----------------------------------------------- |
| `web-api/SpotiXeApi` | ASP.NET Core 8, JWT, MySQL                     | Cung cấp REST API cho mobile & admin            |
| `web-admin`          | React 18 + Vite + Tailwind CSS                 | Dashboard quản trị người dùng, nghệ sĩ, bài hát |
| `main-app`           | Kotlin, Jetpack Compose, Media3, Firebase Auth | Ứng dụng nghe nhạc Android                      |
| `db`                 | MySQL scripts                                  | Khởi tạo schema & dữ liệu mẫu                   |

```
SpotiXe/
├── db/                     # Các script tạo database tuần tự 00_..07_
├── main-app/               # Mã nguồn Android app
├── web-admin/              # Mã nguồn admin React
├── web-api/SpotiXeApi/     # API ASP.NET Core
├── mysql_schema.sql        # Placeholder (không sử dụng)
├── context/                # Mô tả schema bổ sung
└── web-site/               # (Tùy chọn) landing page
```

## Demo Screenshot / Preview

## 🌐 Landing Page Preview

<p align="center">
  <img src="https://pub-217c759e94154463a5870a703a1743c4.r2.dev/readme/site.png" width="800"/>
</p>

<p align="center">
  <img src="https://pub-217c759e94154463a5870a703a1743c4.r2.dev/readme/site2.png" width="800"/>
</p>
<p align="center"><i>
Trang giới thiệu SpotiXe, hiển thị thông tin nền tảng và liên kết tải ứng dụng.
</i></p>

## Admin Dashboard

<p align="center">
  <img src="https://pub-217c759e94154463a5870a703a1743c4.r2.dev/readme/admin-dashboard.png" width="430"/>
  <img src="https://pub-217c759e94154463a5870a703a1743c4.r2.dev/readme/admin-dashboard2.png" width="430"/>
</p>
<p align="center"><i>Dashboard tổng quan số liệu người dùng, doanh thu và lượt phát theo thời gian thực (dữ liệu tĩnh) - *ảnh minh họa.</i></p>

## API – Swagger UI

<p align="center">
  <img src="https://pub-217c759e94154463a5870a703a1743c4.r2.dev/readme/web-api1.png" width="850"/>
</p>

<p align="center">
  <img src="https://pub-217c759e94154463a5870a703a1743c4.r2.dev/readme/web-api2.png" width="850"/>
</p>
<p align="center"><i>
Giao diện tài liệu API của SpotiXe, cho phép xem và thử các endpoint trực tiếp.
</i></p>

## ⭐ Product Showcase

<p align="center">
  <img src="https://pub-217c759e94154463a5870a703a1743c4.r2.dev/readme/product-showcase/1.png" width="280"/>
  <img src="https://pub-217c759e94154463a5870a703a1743c4.r2.dev/readme/product-showcase/2.png" width="280"/>
  <img src="https://pub-217c759e94154463a5870a703a1743c4.r2.dev/readme/product-showcase/3.png" width="280"/>
</p>

<p align="center">
  <img src="https://pub-217c759e94154463a5870a703a1743c4.r2.dev/readme/product-showcase/4.png" width="280"/>
  <img src="https://pub-217c759e94154463a5870a703a1743c4.r2.dev/readme/product-showcase/5.png" width="280"/>
  <img src="https://pub-217c759e94154463a5870a703a1743c4.r2.dev/readme/product-showcase/6.png" width="280"/>
</p>

## Hướng phát triển

### Đã làm được

- [x] Thiết kế kiến trúc hệ thống (API – Admin – Mobile – DB)
- [x] Database schema
- [x] Web API nền tảng (Songs, Artists, Auth JWT)
- [x] Web Admin (React + Firebase Auth + API C#)
- [x] Landing Page
- [x] Mobile App:
  - [x] Đăng nhập Firebase
  - [x] UI với Jetpack Compose
  - [x] Phát nhạc cơ bản (play/pause/mini player bar) với ExoPlayer
  - [x] Tìm kiếm bài hát cơ bản
- [x] Docker cho MySQL & Web API

### Đang thực hiện

- Hoàn thiện tính năng nghe nhạc (Media3): Next, Previous, Queue List
- Upload audio & ảnh bìa cho Admin Dashboard
- Tối ưu API response, thêm phân trang và caching
- Tính năng thêm album và playlist
- Mobile app nghe nhạc theo playlist, album.
- Tính năng quản lý playlist (CRUD + reorder)
- Tối ưu UI/UX cho mobile app

### Kế hoạch sắp tới (Nếu team còn chơi với nhau)

- Realtime analytics cho Admin (SignalR/WebSockets)
- Đề xuất bài hát (Recommendation Engine)
- Gói Premium + thanh toán (Stripe/Momo/ZaloPay)
- Hệ thống phân quyền: Admin / Artist / User
- API cho Artist tự upload & quản lý bài hát

## Yêu cầu hệ thống

- **MySQL 8.0+**
- **.NET SDK 8.0** để build Web API.
- **Node.js 18+** và npm (Vite + Tailwind cần Node 18 trở lên). 【F:web-admin/package.json†L1-L38】
- **Android Studio Koala hoặc mới hơn** (JDK 17 được bunded) + Android SDK 24-36 cho mobile app.
- **Firebase project** nếu dùng đăng nhập Google trên mobile & web admin.
- (Tuỳ chọn) **Docker** để đóng gói API.

## API Overview

| Endpoint          | Method | Mô tả                 |
| ----------------- | ------ | --------------------- |
| `/api/auth/login` | POST   | Đăng nhập nhận JWT    |
| `/api/songs`      | GET    | Lấy danh sách bài hát |
| `/api/playlists`  | POST   | Tạo Playlist          |

## Cấu trúc thư mục

```
main-app/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/spotixe/
│   │   │   │   ├── api/              # API Client & Network Layer
│   │   │   │   ├── auth/             # Authentication Module
│   │   │   │   │   ├── data/
│   │   │   │   │   │   ├── api/     # Auth API Services
│   │   │   │   │   │   └── repository/
│   │   │   │   │   └── viewmodel/   # Auth ViewModels
│   │   │   │   ├── components/      # Reusable UI Components
│   │   │   │   ├── Data/            # Data Models & Entities
│   │   │   │   ├── Pages/           # UI Screens/Pages
│   │   │   │   │   ├── AppMainPages/    # Main App Screens
│   │   │   │   │   ├── SignInPages/     # Sign In Flow
│   │   │   │   │   ├── SignUpPages/     # Sign Up Flow
│   │   │   │   │   └── StartPages/      # Onboarding/Splash
│   │   │   │   ├── player/          # Music Player Module
│   │   │   │   ├── services/        # Background Services
│   │   │   │   ├── ui/              # UI Components & Theme
│   │   │   │   ├── ViewModel/       # ViewModels (MVVM)
│   │   │   │   ├── MainActivity.kt
│   │   │   │   └── Routes.kt        # Navigation Routes
│   │   │   └── res/                 # Resources (layouts, drawables, etc.)
│   │   ├── androidTest/             # Instrumented Tests
│   │   └── test/                    # Unit Tests
│   ├── build.gradle.kts             # App-level Gradle config
│   ├── google-services.json         # Firebase configuration
│   └── proguard-rules.pro           # ProGuard rules
├── gradle/
│   ├── libs.versions.toml           # Version catalog
│   └── wrapper/
│       └── gradle-wrapper.properties
├── build.gradle.kts                 # Project-level Gradle config
├── settings.gradle.kts              # Gradle settings
├── gradlew                          # Gradle wrapper (Unix)
├── gradlew.bat                      # Gradle wrapper (Windows)
├── gradle.properties                # Gradle properties
└── local.properties                 # Local SDK paths
```

```
web-admin/
├── public/                          # Static assets
├── src/
│   ├── components/                  # React Components
│   │   ├── common/                  # Common/Shared components
│   │   ├── dashboard/               # Dashboard-specific components
│   │   ├── layout/                  # Layout components
│   │   ├── routes/                  # Route components
│   │   └── ui/                      # UI library components
│   ├── config/
│   │   └── api.js                   # API configuration
│   ├── context/
│   │   ├── AuthContext.jsx          # Authentication context
│   │   └── NavigationGuardContext.jsx # Navigation guard
│   ├── hooks/                       # Custom React hooks
│   │   ├── useAlbumDetail.js
│   │   ├── useAlbums.js
│   │   ├── useArtistDetail.js
│   │   ├── useArtists.js
│   │   ├── useClickOutside.js
│   │   ├── useConfirmDialog.js
│   │   ├── useSongDetail.js
│   │   ├── useSongs.js
│   │   ├── useTheme.js
│   │   └── useUnsavedChanges.js
│   ├── lib/
│   │   └── formatters.js            # Utility formatters
│   ├── pages/                       # Page components
│   │   ├── albums/
│   │   │   ├── AlbumCreatePage.jsx
│   │   │   ├── AlbumDetailPage.jsx
│   │   │   └── AlbumEditPage.jsx
│   │   ├── artists/
│   │   │   ├── ArtistCreatePage.jsx
│   │   │   ├── ArtistDetailPage.jsx
│   │   │   └── ArtistEditPage.jsx
│   │   ├── songs/
│   │   │   ├── SongCreatePage.jsx
│   │   │   ├── SongDetailPage.jsx
│   │   │   └── SongEditPage.jsx
│   │   ├── AlbumsManagement.jsx
│   │   ├── Analytics.jsx
│   │   ├── ArtistsManagement.jsx
│   │   ├── Dashboard.jsx
│   │   ├── LoginPage.jsx
│   │   ├── MusicManagement.jsx
│   │   ├── NotFound.jsx
│   │   ├── Playlists.jsx
│   │   ├── PremiumPlans.jsx
│   │   ├── Reports.jsx
│   │   ├── Revenue.jsx
│   │   ├── Settings.jsx
│   │   └── UsersManagement.jsx
│   ├── services/                    # API services
│   │   ├── api/
│   │   │   ├── albums.js
│   │   │   ├── artists.js
│   │   │   ├── auth.js
│   │   │   ├── playlists.js
│   │   │   ├── songs.js
│   │   │   └── users.js
│   │   ├── api.js                   # Main API service
│   │   ├── firebase.js              # Firebase service
│   │   └── mockApi.js               # Mock API (dev)
│   ├── stores/
│   │   └── sidebarStore.js          # Zustand store for sidebar
│   ├── utils/                       # Utility functions
│   │   ├── audioMetadata.js
│   │   ├── csvHelper.js
│   │   └── helpers.js
│   ├── App.jsx                      # Root component
│   ├── main.jsx                     # Entry point
│   └── index.css                    # Global styles
├── firebase.json                    # Firebase hosting config
├── index.html                       # HTML template
├── package.json                     # Dependencies
├── postcss.config.js                # PostCSS config
├── tailwind.config.js               # Tailwind CSS config
├── vite.config.js                   # Vite config
└── project-structure.txt            # Project structure doc

```

```
web-site/
├── public/
│   ├── qr-code-dynamic.svg          # QR code để tải app
│   └── ... (other static assets)
├── src/
│   ├── components/
│   │   ├── FeaturesSection.jsx      # Features showcase
│   │   ├── Footer.jsx               # Footer component
│   │   ├── Header.jsx               # Header/Navbar
│   │   ├── HeroSection.jsx          # Hero banner
│   │   ├── PricingSection.jsx       # Pricing plans
│   │   └── QRSection.jsx            # QR download section
│   ├── img/                         # Images
│   ├── App.jsx                      # Root component
│   ├── main.jsx                     # Entry point
│   └── index.css                    # Global styles
├── firebase.json                    # Firebase hosting config
├── index.html                       # HTML template
├── package.json                     # Dependencies
├── postcss.config.js                # PostCSS config
├── tailwind.config.js               # Tailwind CSS config
└── vite.config.js                   # Vite config
```

```
web-api/
├── SpotiXeApi/
│   ├── Context/
│   │   └── SpotiXeDbContext.cs      # EF Core DbContext
│   ├── Controllers/                 # API Controllers
│   │   ├── AlbumsController.cs      # Album endpoints
│   │   ├── ArtistsController.cs     # Artist endpoints
│   │   ├── AuthController.cs        # Authentication endpoints
│   │   ├── PlaylistsController.cs   # Playlist endpoints
│   │   ├── SongsController.cs       # Song endpoints
│   │   └── UsersController.cs       # User endpoints
│   ├── DTOs/                        # Data Transfer Objects
│   │   ├── AlbumsDtos.cs
│   │   ├── ArtistsDtos.cs
│   │   ├── AuthDtos.cs
│   │   ├── PlaylistsDtos.cs
│   │   ├── SongsDtos.cs
│   │   └── UsersDtos.cs
│   ├── Entities/                    # Database Models
│   │   ├── Album.cs
│   │   ├── Artist.cs
│   │   ├── Playlist.cs
│   │   ├── PlaylistSong.cs
│   │   ├── Song.cs
│   │   ├── User.cs
│   │   └── UserFollowedPlaylist.cs
│   ├── Repositories/                # Repository pattern
│   │   └── ... (Repository classes)
│   ├── Services/                    # Business logic services
│   │   └── ... (Service classes)
│   ├── Properties/
│   │   └── launchSettings.json      # Launch profiles
│   ├── appsettings.json             # Configuration
│   ├── Program.cs                   # Application entry point
│   └── SpotiXeApi.csproj            # Project file
├── Dockerfile                       # Docker configuration
└── web-api.sln                      # Solution file
```

## Bắt đầu

```bash
git clone https://github.com/hientran-dotnet/SpotiXe.git
cd SpotiXe
```

### 1. Khởi tạo MySQL

1.1. Tạo database trống `spotixe`.

1.2. Chạy script trong thư mục `db/` để tạo schema.

1.3. Tạo tài khoản MySQL và cập nhật chuỗi kết nối trong file cấu hình API.

### 2. Cấu hình và chạy Web API (ASP.NET Core)

2.1. Sao chép `web-api/SpotiXeApi/appsettings.json` thành `appsettings.Development.json` (hoặc giữ nguyên) và sửa:

```json
{
  "ConnectionStrings": {
    "DefaultConnection": "server=127.0.0.1;port=3306;database=spotixe;user=spotixe;password=secret"
  },
  "Jwt": {
    "Key": "<tối thiểu 32 ký tự>",
    "Issuer": "spotixe",
    "Audience": "spotixe_users",
    "ExpireMinutes": 60
  }
}
```

Bạn có thể override bằng biến môi trường chuẩn của ASP.NET: `ConnectionStrings__DefaultConnection`, `Jwt__Key`, ...

2.2 Restore & chạy:

```bash
cd web-api
dotnet restore
dotnet build
dotnet run --project SpotiXeApi/SpotiXeApi.csproj
```

API mặc định chạy ở `https://localhost:5000` (hoặc `http://localhost:5001`).

2.3. Docker build (tuỳ chọn):

```bash
docker build -t spotixe-api -f web-api/Dockerfile web-api
docker run -p 8080:8080 --env ConnectionStrings__DefaultConnection="..." spotixe-api
```

### 3. Web Admin React

3.1. Tạo file `web-admin/.env.local`:

```bash
VITE_API_BASE_URL=http://localhost:5000/api
VITE_API_URL=http://localhost:5000/api
VITE_ALLOWED_DOMAIN=admin.spotixe.local
VITE_FIREBASE_API_KEY=<firebase-key>
VITE_FIREBASE_AUTH_DOMAIN=<firebase-auth-domain>
VITE_FIREBASE_PROJECT_ID=<firebase-project>
VITE_FIREBASE_STORAGE_BUCKET=<firebase-storage>
VITE_FIREBASE_MESSAGING_SENDER_ID=<sender>
VITE_FIREBASE_APP_ID=<firebase-app-id>
VITE_FIREBASE_MEASUREMENT_ID=<measurement-id>
```

Các biến này được dùng trong `src/config/api.js`, `src/context/AuthContext.jsx` và `src/services/firebase.js`.

3.2. Cài dependency & chạy dev server:

```bash
cd web-admin
npm install
npm run dev
```

Vite sẽ chạy ở `http://localhost:3000`.

3.3. Build production:

```bash
npm run build
npm run preview
```

#### (Tuỳ chọn) chạy MySQL bằng Docker + volume dữ liệu

```bash
docker volume create spotixe-mysql-data
docker run -d \
  --name spotixe-mysql \
  -e MYSQL_ROOT_PASSWORD=secret \
  -e MYSQL_DATABASE=spotixe \
  -e MYSQL_USER=spotixe \
  -e MYSQL_PASSWORD=secret \
  -p 3306:3306 \
  -v spotixe-mysql-data:/var/lib/mysql \
  -v $(pwd)/db:/docker-entrypoint-initdb.d:ro \
  mysql:8.0
```

- Volume tên `spotixe-mysql-data` giữ lại dữ liệu giữa các lần khởi động.
- Mount thư mục `db/` vào `/docker-entrypoint-initdb.d` giúp container auto chạy các script tạo schema ngay lần đầu.
- Bạn có thể thay biến môi trường để phù hợp với mật khẩu/tài khoản riêng.

### Cấu hình và chạy Web API (ASP.NET Core)

1. Sao chép `web-api/SpotiXeApi/appsettings.json` thành `appsettings.Development.json` (hoặc giữ nguyên) và sửa:
   ```json
   {
     "ConnectionStrings": {
       "DefaultConnection": "server=127.0.0.1;port=3306;database=spotixe;user=spotixe;password=secret"
     },
     "Jwt": {
       "Key": "<tối thiểu 32 ký tự>",
       "Issuer": "spotixe",
       "Audience": "spotixe_users",
       "ExpireMinutes": 60
     }
   }
   ```
   Bạn có thể override bằng biến môi trường chuẩn của ASP.NET: `ConnectionStrings__DefaultConnection`, `Jwt__Key`, ...
2. Restore & chạy:
   ```bash
   cd web-api
   dotnet restore
   dotnet build
   dotnet run --project SpotiXeApi/SpotiXeApi.csproj
   ```
   API mặc định chạy ở `https://localhost:5001` (hoặc `http://localhost:5000`).
3. Docker build (tuỳ chọn):
   ```bash
   docker build -t spotixe-api -f web-api/Dockerfile web-api
   docker run -p 8080:8080 --env ConnectionStrings__DefaultConnection="..." spotixe-api
   ```

#### Build & chạy Web API trực tiếp bằng Docker

1. **Build image** (chạy tại thư mục gốc repo):
   ```bash
   docker build -t registry.local/spotixe-api:latest -f web-api/Dockerfile web-api
   ```
   Dockerfile đã bao gồm restore, build và publish self-contained app.
2. **Kết nối với MySQL container**: nếu bạn dùng container `spotixe-mysql` ở trên, tạo network riêng để API truy cập DB dễ hơn:
   ```bash
   docker network create spotixe-net
   docker network connect spotixe-net spotixe-mysql
   ```
3. **Chạy API** kèm biến môi trường kết nối DB & cấu hình JWT:
   ```bash
   docker run -d \
     --name spotixe-api \
     --network spotixe-net \
     -p 8080:8080 \
     -e ConnectionStrings__DefaultConnection="server=spotixe-mysql;port=3306;database=spotixe;user=spotixe;password=secret" \
     -e Jwt__Key="<chuoi_32_ky_tu>" \
     -e Jwt__Issuer=spotixe \
     -e Jwt__Audience=spotixe_users \
     registry.local/spotixe-api:latest
   ```
   Container sẽ expose API tại `http://localhost:8080` (HTTPS nếu bạn tự cấu hình reverse proxy). Bạn cũng có thể mount `appsettings.Production.json` nếu muốn tách cấu hình ra file.

### 4. Ứng dụng Android (Kotlin + Jetpack Compose)

4.1. Mở thư mục `main-app/` bằng Android Studio.

4.2. Đồng bộ Gradle (`./gradlew tasks` nếu muốn kiểm tra CLI).

4.3. Đặt file `app/google-services.json` tương ứng Firebase project của bạn.

4.4. Nếu muốn đổi API base URL, cập nhật hằng `BASE_URL` trong `auth/data/api/RetrofitClient.kt`. 【F:main-app/app/src/main/java/com/example/spotixe/auth/data/api/RetrofitClient.kt†L5-L64】

4.5. Build & chạy:

```bash
./gradlew assembleDebug
```

hoặc deploy trực tiếp từ Android Studio lên thiết bị / emulator API 24+.

## Luồng chạy gợi ý

1. Khởi động MySQL và chắc chắn schema + dữ liệu đã tạo.
2. Chạy Web API → xác minh `GET /health` (nếu có) hoặc `GET /api/songs` trả dữ liệu.
3. Chạy web admin (`npm run dev`) để đăng nhập/seed nội dung.
4. Mở mobile app, cấu hình tài khoản Firebase/Google, đăng nhập và kiểm tra phát nhạc.

## Troubleshooting

| Vấn đề                            | Gợi ý xử lý                                                                                                                                       |
| --------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------- |
| API không kết nối được DB         | Kiểm tra port MySQL, user/password và quyền truy cập trong `appsettings.*`. Đảm bảo MySQL chấp nhận kết nối từ `localhost` nếu chạy trong Docker. |
| Web Admin báo `VITE_* undefined`  | Chắc chắn `.env.local` tồn tại và chạy lại `npm run dev`. Với production build cần set biến môi trường trước khi `npm run build`.                 |
| Mobile app không đăng nhập Google | Kiểm tra `google-services.json` và SHA-1 trong Firebase console; bật API Google Sign-In.                                                          |
| Mobile app gọi sai API URL        | Sửa `BASE_URL` và rebuild ứng dụng.                                                                                                               |
| CORS                              | Thêm domain client vào `Program.cs` của API nếu cần cấu hình CORS policy.                                                                         |

## Đóng góp & giấy phép

- PR/Issue luôn được hoan nghênh, vui lòng mô tả rõ thành phần bị ảnh hưởng (API / Admin / Mobile / DB).
- Mã nguồn tuân theo giấy phép MIT trong `LICENSE`.
