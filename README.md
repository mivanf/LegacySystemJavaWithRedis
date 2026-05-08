# QRIS Payment System

## Deskripsi Sistem
Aplikasi ini adalah sebuah sistem *backend* pemrosesan pembayaran QRIS (Quick Response Code Indonesian Standard). Sistem ini dirancang untuk memfasilitasi transaksi digital secara aman, cepat, dan handal dengan dukungan pembacaan kode QR (berbasis gambar maupun teks). Sistem ini dibangun dengan pendekatan *Clean Architecture*, memastikan setiap lapisan (Adapter, Application, dan Domain/Infrastructure) terpisah secara logis, sehingga memudahkan dalam perawatan (maintenance) dan skalabilitas di masa mendatang.

## Tech Stack
- **Bahasa Pemrograman**: Java 17+
- **Backend Framework**: Spring Boot 3
- **Architecture**: Clean Architecture
- **Database Utama**: PostgreSQL 15
- **Caching & Async Processing**: Redis 7
- **Keamanan (Security)**: Spring Security & JWT (JSON Web Tokens)
- **Dokumentasi API**: Swagger / OpenAPI 3
- **Containerization**: Docker & Docker Compose

## Alur Kerja Sistem
1. **Pendaftaran / Otentikasi**: 
   - Pengguna (User) mendaftar ke dalam sistem.
   - Setelah registrasi, pengguna login untuk mendapatkan token JWT sebagai syarat mengakses fitur utama.
2. **Inquiry QRIS (Pengecekan Kode)**:
   - Pengguna melakukan *scan* atau mengunggah gambar QRIS (via API `multipart/form-data`).
   - Sistem akan mengekstraksi dan membaca data dari QRIS (nama merchant, ID, dsb.) untuk memastikan kode valid.
3. **Proses Pembayaran (Payment)**:
   - Setelah mendapatkan informasi dari QRIS, pengguna memproses pembayaran dengan memasukkan token, PIN, dan nominal.
   - Sistem akan memvalidasi kecukupan saldo dan memproses transaksi tersebut.
4. **Cek Status & Rekap (Output)**:
   - Pengguna dapat mengecek status transaksi spesifik apakah berhasil, ditunda (*pending*), atau gagal.
   - Admin dapat melihat riwayat semua transaksi yang ada di dalam sistem.

## Fitur yang Tersedia
- **Otentikasi**: Registrasi dan Login pengguna menggunakan JWT.
- **Ekstraksi QRIS**: Membaca payload QRIS dari *string* maupun langsung dari file gambar (QR Code image).
- **Manajemen Merchant**: Pendaftaran (atau reaktivasi) merchant baru dari gambar QRIS.
- **Transaksi**: Pembuatan pembayaran (*payment creation*) dan pengecekan status transaksi.
- **Admin Dashboard API**: Melihat seluruh riwayat transaksi dan mengelola pendaftaran *API Clients*.

## Cara Menjalankan di Docker
Pastikan **Docker** dan **Docker Compose** telah ter-install di sistem Anda.

1. Buka terminal (command line) Anda.
2. Arahkan *working directory* ke root folder proyek ini (di mana file `docker-compose.yml` berada).
3. Jalankan perintah berikut untuk mem-*build* image aplikasi dan menjalankan seluruh layanan (database, redis, dan aplikasi) di *background*:
   ```bash
   docker-compose up -d --build
   ```
4. Tunggu beberapa detik hingga proses inisialisasi selesai (terutama PostgreSQL dan Redis hingga berstatus *healthy*). Anda dapat mengecek status container dengan perintah `docker-compose ps`.
5. Untuk mematikan dan menghapus container yang sedang berjalan, gunakan perintah:
   ```bash
   docker-compose down
   ```

## Cara Mengaksesnya
Setelah sistem berjalan dengan sukses di Docker, Anda dapat mengakses beberapa komponen berikut:

- **Base URL API**: `http://localhost:8081`
- **Swagger UI (Dokumentasi & Testing API)**: Buka browser Anda dan kunjungi `http://localhost:8081/swagger-ui.html`

**Akses Container untuk Debugging:**
- **Database PostgreSQL**: 
  - Host: `localhost`
  - Port: `5432`
  - Database: `qris_payment`
  - Username: `qris_user`
  - Password: `qris_secret_2024`
- **Redis Cache**: 
  - Host: `localhost`
  - Port: `6379`
