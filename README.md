a# Aplikasi Manajemen Bengkel Lathifah

Aplikasi desktop berbasis Java Swing untuk mengelola operasional bengkel otomotif. Sistem ini dirancang untuk memudahkan pengelolaan data pelanggan, produk, transaksi, laporan, dan statistik bengkel.

## Fitur Utama

### Manajemen Pelanggan
- Tambah, edit, hapus data pelanggan
- Pencarian cepat berdasarkan nama atau nomor plat
- Integrasi langsung dengan transaksi

### Manajemen Barang & Jasa
- Kategori barang terorganisir
- Manajemen produk dan jasa
- Stok dan harga dinamis

### Sistem Transaksi
- Pencatatan transaksi lengkap
- Detail transaksi otomatis
- Riwayat transaksi dengan pencarian

### Laporan & Statistik
- Laporan keuangan harian/mingguan/bulanan
- Export laporan ke CSV (kompatibel dengan Excel)
- Dashboard statistik real-time
- 5 transaksi terakhir di dashboard

### Sistem Login
- Autentikasi pengguna (Admin/Kasir)
- Kontrol akses berdasarkan role

## Persyaratan Sistem

- Java Development Kit (JDK): Versi 8 atau lebih tinggi
- Database: MySQL atau MariaDB
- Sistem Operasi: Windows, macOS, atau Linux

## Setup dan Instalasi

### 1. Clone atau Download Proyek
```bash
git clone <repository-url>
cd "App Bengkel Lathifah"
```

### 2. Setup Database
- Import file `bengkel_lathifah.sql` ke MySQL/MariaDB
- Pastikan database server berjalan

### 3. Konfigurasi Database
Koneksi database menggunakan file properties:
- `src/config/db.properties` (di-load sebagai resource `"/config/db.properties"`)

Isi contoh `db.properties`:
```properties
db.host=localhost
db.port=3306
db.name=bengkel_lathifah
db.user=USERNAME_MYSQL
db.password=PASSWORD_MYSQL
```


## Cara Menjalankan Aplikasi

### Metode 1: Jalankan dari Main.java (Direkomendasikan)
1. Pastikan JDK terinstall dan PATH sudah dikonfigurasi
2. Buka terminal/command prompt
3. Navigasi ke folder proyek:
   ```bash
   cd "c:\KULIAH\App Bengkel Lathifah"
   ```
4. Jalankan aplikasi:
   ```bash
   java -cp "bin;lib/*" Main
   ```

### Metode 2: Compile dan Jalankan Manual
```bash
# Compile semua file Java (termasuk subfolder package)
# Pastikan folder bin/ sudah ada (kalau tidak, buat manual)
javac -cp "lib/*" -d bin $(dir /b /s src\*.java)

# Jika command $(...) tidak didukung di shell kamu, gunakan alternatif:
# 1) Compile dengan IDE (Run/Build) atau
# 2) Copy semua *.java ke satu folder sementara.


# Jalankan aplikasi
java -cp "bin;lib/*" Main
```


## Akun Login untuk Testing

| Username | Password | Role       |
|----------|----------|------------|
| admin    | admin123 | Administrator |
| kasir    | kasir123 | Kasir       |

## Preview Aplikasi

- Login Screen: Autentikasi pengguna
- Dashboard: Statistik dan transaksi terbaru
- Pelanggan: Manajemen data pelanggan dengan pencarian
- Transaksi: Pencatatan transaksi dengan auto-select pelanggan
- Laporan: Filter dan export laporan ke CSV

## Struktur Proyek

```
App Bengkel Lathifah/
├── src/
│   ├── Main.java                 # Entry point aplikasi
│   ├── config/
│   │   └── DatabaseConnection.java
│   ├── dao/                      # Data Access Objects
│   ├── gui/                      # Graphical User Interface
│   ├── lib/                      # Library dependencies
│   └── model/                    # Model classes
├── bin/                          # Compiled classes
├── lib/                          # External libraries
├── bengkel_lathifah.sql           # Database schema

└── README.md                     # Dokumentasi ini
```

## Troubleshooting

### Error: "java command not found"
- Pastikan JDK terinstall dan PATH sudah dikonfigurasi
- Cek versi Java: `java -version`

### Error: "Class not found"
- Pastikan semua file .class ada di folder bin
- Cek classpath: `-cp "bin;lib/*"`

### Error: Database connection failed
- Pastikan MySQL/MariaDB server berjalan
- Cek konfigurasi di `DatabaseConnection.java`
- Pastikan database sudah di-import

## Catatan Pengembang

- Aplikasi menggunakan Java Swing untuk UI
- Database menggunakan JDBC untuk koneksi MySQL
- Arsitektur MVC (Model-View-Controller) sederhana
- Dependencies eksternal disimpan di folder `lib/`

## Kontribusi

Untuk berkontribusi pada proyek ini:
1. Fork repository
2. Buat branch fitur baru
3. Commit perubahan
4. Push ke branch
5. Buat Pull Request

## Lisensi

Proyek ini dibuat untuk keperluan edukasi dan tugas proyek RPL

---

Dibuat oleh Kelompok 3 Tekom D 24
