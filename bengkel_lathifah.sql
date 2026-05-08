-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Waktu pembuatan: 30 Mar 2026 pada 03.37
-- Versi server: 10.4.32-MariaDB
-- Versi PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `bengkel_lathifah`
--

-- --------------------------------------------------------

--
-- Struktur dari tabel `barang`
--

CREATE TABLE `barang` (
  `id` int(11) NOT NULL,
  `kategori_id` int(11) NOT NULL,
  `deskripsi` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `barang`
--

INSERT INTO `barang` (`id`, `kategori_id`, `deskripsi`) VALUES
(1, 1, 'Oli mineral berkualitas tinggi untuk mesin harian'),
(2, 1, 'Oli synthetic untuk performa tinggi'),
(3, 2, 'Oli transmisi otomatis original'),
(4, 4, 'Filter udara standar replacement'),
(5, 5, 'Filter oli untuk mobil sedan'),
(6, 6, 'Filter solar/diesel'),
(7, 7, 'Kampas rem asli Toyota OEM'),
(8, 7, 'Kampas rem aftermarket murah'),
(9, 8, 'Kaliper rem depan kanan'),
(10, 10, 'Ban standar untuk city car'),
(11, 11, 'Ban motor sport'),
(12, 14, 'Aki maintenance free'),
(13, 15, 'Lampu halogen depan'),
(14, 18, 'Busi standar NGK'),
(15, 19, 'Set kabel busi lengkap'),
(16, 20, 'Radiator pendingin standar'),
(17, 22, 'Piston ring set'),
(18, 23, 'Shock depan KYB premium'),
(19, 24, 'Plat kopling single'),
(20, 21, 'Karbu racing PWK'),
(21, 9, 'Master cylinder belakang'),
(22, 12, 'Velg alloy 17\"'),
(23, 1, 'Pompa oli mesin 150cc'),
(24, 22, 'Sensor posisi throttle'),
(25, 20, 'Selang radiator karet'),
(26, 7, 'Cakram rem depan'),
(27, 23, 'Tie rod end kanan'),
(28, 23, 'Ball joint bawah'),
(29, 25, 'CV joint inner'),
(30, 2, 'Gear oil manual 75W90'),
(31, 4, 'Filter udara racing K&N'),
(32, 7, 'Kampas rem Exide premium'),
(33, 14, 'Aki 70AH heavy duty'),
(34, 15, 'Lampu LED projector'),
(35, 18, 'Busi iridium NGK');

-- --------------------------------------------------------

--
-- Struktur dari tabel `detail_transaksi`
--

CREATE TABLE `detail_transaksi` (
  `id` int(11) NOT NULL,
  `transaksi_id` int(11) NOT NULL,
  `produk_id` int(11) NOT NULL,
  `qty` int(11) NOT NULL,
  `harga_satuan` decimal(12,2) NOT NULL,
  `subtotal` decimal(14,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `detail_transaksi`
--

INSERT INTO `detail_transaksi` (`id`, `transaksi_id`, `produk_id`, `qty`, `harga_satuan`, `subtotal`) VALUES
(1, 1, 1, 1, 55000.00, 55000.00),
(2, 1, 36, 1, 150000.00, 150000.00),
(3, 1, 37, 1, 80000.00, 80000.00),
(4, 1, 2, 1, 450000.00, 450000.00),
(5, 2, 7, 2, 250000.00, 500000.00),
(6, 2, 54, 1, 200000.00, 200000.00),
(7, 2, 38, 1, 70000.00, 70000.00),
(8, 2, 39, 1, 450000.00, 450000.00),
(13, 4, 12, 1, 650000.00, 650000.00),
(14, 4, 33, 1, 1200000.00, 1200000.00),
(15, 4, 42, 1, 250000.00, 250000.00),
(16, 5, 18, 2, 1250000.00, 2500000.00),
(17, 5, 46, 1, 600000.00, 600000.00),
(18, 5, 47, 1, 350000.00, 350000.00),
(19, 6, 4, 3, 85000.00, 255000.00),
(20, 6, 37, 1, 80000.00, 80000.00),
(21, 6, 14, 4, 45000.00, 180000.00),
(22, 6, 44, 1, 120000.00, 120000.00),
(23, 7, 9, 1, 750000.00, 750000.00),
(24, 7, 49, 1, 400000.00, 400000.00),
(25, 7, 26, 1, 650000.00, 650000.00),
(26, 8, 3, 2, 120000.00, 240000.00),
(27, 8, 30, 1, 95000.00, 95000.00),
(28, 8, 52, 1, 750000.00, 750000.00),
(29, 9, 22, 1, 1800000.00, 1800000.00),
(30, 9, 11, 2, 350000.00, 700000.00),
(31, 9, 40, 1, 200000.00, 200000.00),
(32, 10, 16, 1, 950000.00, 950000.00),
(33, 10, 43, 1, 180000.00, 180000.00),
(34, 10, 25, 2, 85000.00, 170000.00),
(35, 11, 29, 1, 550000.00, 550000.00),
(36, 11, 51, 1, 550000.00, 550000.00),
(37, 11, 27, 2, 180000.00, 360000.00),
(38, 12, 34, 1, 450000.00, 450000.00),
(39, 12, 13, 2, 150000.00, 300000.00),
(40, 12, 6, 1, 65000.00, 65000.00),
(41, 13, 19, 1, 420000.00, 420000.00),
(42, 13, 45, 1, 800000.00, 800000.00),
(43, 14, 17, 3, 280000.00, 840000.00),
(44, 14, 20, 1, 850000.00, 850000.00),
(45, 14, 48, 1, 250000.00, 250000.00),
(46, 15, 31, 1, 120000.00, 120000.00),
(47, 15, 32, 2, 300000.00, 600000.00),
(48, 15, 23, 1, 450000.00, 450000.00),
(49, 16, 28, 4, 220000.00, 880000.00),
(50, 16, 21, 1, 320000.00, 320000.00),
(51, 17, 8, 2, 180000.00, 360000.00),
(52, 17, 35, 4, 120000.00, 480000.00),
(53, 17, 53, 1, 50000.00, 50000.00),
(54, 18, 24, 1, 250000.00, 250000.00),
(55, 18, 15, 3, 120000.00, 360000.00),
(56, 19, 1, 2, 55000.00, 110000.00),
(57, 19, 36, 1, 150000.00, 150000.00),
(58, 19, 38, 1, 70000.00, 70000.00),
(59, 19, 39, 1, 450000.00, 450000.00),
(60, 20, 7, 1, 250000.00, 250000.00),
(61, 20, 54, 2, 200000.00, 400000.00),
(62, 20, 55, 1, 100000.00, 100000.00),
(63, 21, 10, 1, 850000.00, 850000.00),
(64, 21, 40, 1, 200000.00, 200000.00),
(65, 21, 12, 1, 650000.00, 650000.00),
(66, 22, 4, 2, 85000.00, 170000.00),
(67, 22, 37, 1, 80000.00, 80000.00),
(68, 23, 18, 1, 1250000.00, 1250000.00),
(69, 23, 46, 1, 600000.00, 600000.00),
(70, 24, 9, 1, 750000.00, 750000.00),
(71, 24, 49, 1, 400000.00, 400000.00),
(72, 25, 22, 1, 1800000.00, 1800000.00),
(73, 25, 11, 1, 350000.00, 350000.00),
(74, 26, 16, 1, 950000.00, 950000.00),
(75, 26, 43, 1, 180000.00, 180000.00),
(76, 27, 29, 1, 550000.00, 550000.00),
(77, 27, 51, 1, 550000.00, 550000.00),
(78, 28, 34, 1, 450000.00, 450000.00),
(79, 28, 13, 1, 150000.00, 150000.00),
(80, 29, 1, 1, 55000.00, 55000.00),
(81, 29, 36, 1, 150000.00, 150000.00),
(82, 29, 39, 1, 450000.00, 450000.00),
(83, 30, 7, 2, 250000.00, 500000.00),
(84, 30, 54, 1, 200000.00, 200000.00),
(85, 30, 45, 1, 800000.00, 800000.00),
(86, 31, 21, 1, 320000.00, 320000.00),
(87, 31, 5, 1, 75000.00, 75000.00),
(88, 32, 51, 1, 550000.00, 550000.00),
(89, 33, 1, 1, 55000.00, 55000.00),
(90, 34, 2, 1, 450000.00, 450000.00);

-- --------------------------------------------------------

--
-- Struktur dari tabel `jasa`
--

CREATE TABLE `jasa` (
  `id` int(11) NOT NULL,
  `deskripsi` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `jasa`
--

INSERT INTO `jasa` (`id`, `deskripsi`) VALUES
(36, 'Servis rutin ganti oli + cek mesin'),
(37, 'Pembersihan dan penggantian filter udara'),
(38, 'Ganti filter oli + oli baru'),
(39, 'Perbaikan sistem rem lengkap'),
(40, 'Pasang ban baru + balancing'),
(41, 'Test aki, charge, dan terminal'),
(42, 'Setting karburator standar'),
(43, 'Pembersihan sistem pendingin'),
(44, 'Ganti 4 busi + cek pengapian'),
(45, 'Bongkar pasang kopling'),
(46, 'Ganti shockbreaker depan/belakang'),
(47, 'Set klep intake/exhaust'),
(48, 'Aligment roda depan'),
(49, 'Overhaul kaliper rem'),
(50, 'Inspeksi lengkap suspensi'),
(51, 'Ganti CV joint + grease'),
(52, 'Servis gearbox manual'),
(53, 'Cuci steam motor'),
(54, 'Ganti kampas depan/belakang'),
(55, 'Scan OBD2 + diagnosa error');

-- --------------------------------------------------------

--
-- Struktur dari tabel `kategori_barang`
--

CREATE TABLE `kategori_barang` (
  `id` int(11) NOT NULL,
  `nama_kategori` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `kategori_barang`
--

INSERT INTO `kategori_barang` (`id`, `nama_kategori`) VALUES
(1, 'Oli Mesin'),
(2, 'Oli Transmisi'),
(3, 'Oli Rem'),
(4, 'Filter Udara'),
(5, 'Filter Oli'),
(6, 'Filter Bahan Bakar'),
(7, 'Sistem Rem - Kampas'),
(8, 'Sistem Rem - Kaliper'),
(9, 'Sistem Rem - Master Silinder'),
(10, 'Ban Depan'),
(11, 'Ban Belakang'),
(12, 'Velg'),
(13, 'Aki'),
(14, 'Lampu Depan'),
(15, 'Lampu Belakang'),
(16, 'Klakson'),
(17, 'Busi'),
(18, 'Kabel Busi'),
(19, 'Radiator'),
(20, 'Pompa Bensin'),
(21, 'Sparepart Mesin'),
(22, 'Sparepart Suspensi'),
(23, 'Sparepart Kopling'),
(24, 'Sparepart Akselerator'),
(25, 'Lain-lain');

-- --------------------------------------------------------

--
-- Struktur dari tabel `laporan`
--

CREATE TABLE `laporan` (
  `id` int(11) NOT NULL,
  `periode_awal` date NOT NULL,
  `periode_akhir` date NOT NULL,
  `jenis_laporan` varchar(50) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struktur dari tabel `pelanggan`
--

CREATE TABLE `pelanggan` (
  `id` int(11) NOT NULL,
  `nama` varchar(100) NOT NULL,
  `no_telp` varchar(20) DEFAULT NULL,
  `alamat` text DEFAULT NULL,
  `no_plat` varchar(20) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `pelanggan`
--

INSERT INTO `pelanggan` (`id`, `nama`, `no_telp`, `alamat`, `no_plat`, `created_at`) VALUES
(1, 'Ahmad Santoso', '081234567890', 'Jl. Merdeka 12, Jakarta', 'B 1234 ABC', '2026-03-18 09:53:33'),
(2, 'Siti Nurhaliza', '081234567891', 'Jl. Sudirman 45, Bandung', 'D 5678 XYZ', '2026-03-18 09:53:33'),
(3, 'Budi Hartono', '081234567892', 'Jl. Gatot Subroto 78, Surabaya', 'G 9012 DEF', '2026-03-18 09:53:33'),
(4, 'Dewi Sartika', '081234567893', 'Jl. Thamrin 23, Medan', 'N 3456 GHI', '2026-03-18 09:53:33'),
(5, 'Eko Prasetyo', '081234567894', 'Jl. Ahmad Yani 56, Semarang', 'T 7890 JKL', '2026-03-18 09:53:33'),
(6, 'Fitri Ani', '081234567895', 'Jl. Malioboro 11, Yogyakarta', 'Z 2345 MNO', '2026-03-18 09:53:33'),
(7, 'Gatot Suryo', '081234567896', 'Jl. Imam Bonjol 34, Makassar', 'AA 6789 PQR', '2026-03-18 09:53:33'),
(8, 'Heni Wulandari', '081234567897', 'Jl. Hasanuddin 67, Palembang', 'BB 0123 STU', '2026-03-18 09:53:33'),
(9, 'Indra Wijaya', '081234567898', 'Jl. Gajah Mada 90, Pekanbaru', 'CC 4567 VWX', '2026-03-18 09:53:33'),
(10, 'Joko Widodo', '081234567899', 'Jl. Raya Bogor 12, Depok', 'DD 8901 YZA', '2026-03-18 09:53:33'),
(11, 'Kartika Sari', '081234567900', 'Jl. Siliwangi 45, Cimahi', 'EE 2345 BCD', '2026-03-18 09:53:33'),
(12, 'Larasati Putri', '081234567901', 'Jl. Panglima Sudirman 78, Jambi', 'FF 6789 EFG', '2026-03-18 09:53:33'),
(13, 'Muhammad Ali', '081234567902', 'Jl. Veteran 23, Bengkulu', 'GG 0123 HIJ', '2026-03-18 09:53:33'),
(14, 'Nita Rahmawati', '081234567903', 'Jl. Sudanco 56, Pontianak', 'HH 4567 KLM', '2026-03-18 09:53:33'),
(15, 'Oscar Nugroho', '081234567904', 'Jl. Jend. Sudirman 89, Banjarmasin', 'II 8901 NOP', '2026-03-18 09:53:33'),
(16, 'Purnama Sari', '081234567905', 'Jl. A. Yani 34, Manado', 'JJ 2345 QRS', '2026-03-18 09:53:33'),
(17, 'Qori Alamsyah', '081234567906', 'Jl. R.A. Kartini 67, Ambon', 'KK 6789 TUV', '2026-03-18 09:53:33'),
(18, 'Rina Melinda', '081234567907', 'Jl. Diponegoro 90, Jayapura', 'LL 0123 WXY', '2026-03-18 09:53:33'),
(19, 'Slamet Raharjo', '081234567908', 'Jl. Pangeran 12, Sorong', 'MM 4567 ZAB', '2026-03-18 09:53:33'),
(20, 'Tina Permata', '081234567909', 'Jl. Kyai Mojo 45, Kediri', 'NN 8901 CDE', '2026-03-18 09:53:33'),
(21, 'Umar Faruq', '081234567910', 'Jl. Dr. Wahidin 78, Malang', 'OO 2345 FGH', '2026-03-18 09:53:33'),
(22, 'Vina Citra', '081234567911', 'Jl. Bromo 23, Malang', 'PP 6789 IJK', '2026-03-18 09:53:33'),
(23, 'Wawan Setiawan', '081234567912', 'Jl. Semeru 56, Batu', 'QQ 0123 LMN', '2026-03-18 09:53:33'),
(24, 'Xena Lestari', '081234567913', 'Jl. Arjuno 89, Blitar', 'RR 4567 OPQ', '2026-03-18 09:53:33'),
(25, 'Yudi Kurniawan', '081234567914', 'Jl. Ijen 34, Lumajang', 'SS 8901 RST', '2026-03-18 09:53:33');

-- --------------------------------------------------------

--
-- Struktur dari tabel `pengguna`
--

CREATE TABLE `pengguna` (
  `id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `nama_lengkap` varchar(100) NOT NULL,
  `role` varchar(20) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `pengguna`
--

INSERT INTO `pengguna` (`id`, `username`, `password`, `nama_lengkap`, `role`, `created_at`) VALUES
(1, 'admin', 'admin123', 'Marwan Aidit', 'Admin', '2026-02-24 11:07:13'),
(2, 'karyawan', '123', 'Nur ilhamsyah. B', 'User', '2026-02-24 12:19:15');

-- --------------------------------------------------------

--
-- Struktur dari tabel `produk`
--

CREATE TABLE `produk` (
  `id` int(11) NOT NULL,
  `kode` varchar(50) NOT NULL,
  `nama` varchar(100) NOT NULL,
  `harga` decimal(12,2) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `produk`
--

INSERT INTO `produk` (`id`, `kode`, `nama`, `harga`, `created_at`) VALUES
(1, 'BRG001', 'Oli Mesin SAE 20W50 1L', 55000.00, '2026-03-18 09:53:33'),
(2, 'BRG002', 'Oli Mesin Full Synthetic 5W30 4L', 450000.00, '2026-03-18 09:53:33'),
(3, 'BRG003', 'Oli Transmisi ATF 1L', 120000.00, '2026-03-18 09:53:33'),
(4, 'BRG004', 'Filter Udara K series', 85000.00, '2026-03-18 09:53:33'),
(5, 'BRG005', 'Filter Oli Hundai Avante', 75000.00, '2026-03-18 09:53:33'),
(6, 'BRG006', 'Filter BBM Standar', 65000.00, '2026-03-18 09:53:33'),
(7, 'BRG007', 'Kampas Rem Depan Toyota', 250000.00, '2026-03-18 09:53:33'),
(8, 'BRG008', 'Kampas Rem Belakang Honda', 180000.00, '2026-03-18 09:53:33'),
(9, 'BRG009', 'Kaliper Rem Rembrand', 750000.00, '2026-03-18 09:53:33'),
(10, 'BRG010', 'Ban FDR Genzi 195/65 R15', 850000.00, '2026-03-18 09:53:33'),
(11, 'BRG011', 'Ban Swallow 80/90-17', 350000.00, '2026-03-18 09:53:33'),
(12, 'BRG012', 'Aki GS 45AH', 650000.00, '2026-03-18 09:53:33'),
(13, 'BRG013', 'Lampu Depan Philips 9005', 150000.00, '2026-03-18 09:53:33'),
(14, 'BRG014', 'Busi NGK CPR6EA', 45000.00, '2026-03-18 09:53:33'),
(15, 'BRG015', 'Kabel Busi Standar', 120000.00, '2026-03-18 09:53:33'),
(16, 'BRG016', 'Radiator Kenceng', 950000.00, '2026-03-18 09:53:33'),
(17, 'BRG017', 'Piston Ring Honda', 280000.00, '2026-03-18 09:53:33'),
(18, 'BRG018', 'Shock Breaker KYB', 1250000.00, '2026-03-18 09:53:33'),
(19, 'BRG019', 'Plat Kopling Exide', 420000.00, '2026-03-18 09:53:33'),
(20, 'BRG020', 'Karburetor PWK 28mm', 850000.00, '2026-03-18 09:53:33'),
(21, 'BRG021', 'Master Rem Belakang', 320000.00, '2026-03-18 09:53:33'),
(22, 'BRG022', 'Velg Harian 17 inch', 1800000.00, '2026-03-18 09:53:33'),
(23, 'BRG023', 'Pompa Oli Mesin', 450000.00, '2026-03-18 09:53:33'),
(24, 'BRG024', 'Sensor Throttle', 250000.00, '2026-03-18 09:53:33'),
(25, 'BRG025', 'Hose Radiator', 85000.00, '2026-03-18 09:53:33'),
(26, 'BRG026', 'Disc Brake Depan', 650000.00, '2026-03-18 09:53:33'),
(27, 'BRG027', 'Tie Rod End', 180000.00, '2026-03-18 09:53:33'),
(28, 'BRG028', 'Ball Joint', 220000.00, '2026-03-18 09:53:33'),
(29, 'BRG029', 'CV Joint', 550000.00, '2026-03-18 09:53:33'),
(30, 'BRG030', 'Gear Box Oil 1L', 95000.00, '2026-03-18 09:53:33'),
(31, 'BRG031', 'Air Filter Racing', 120000.00, '2026-03-18 09:53:33'),
(32, 'BRG032', 'Brake Pad Exide', 300000.00, '2026-03-18 09:53:33'),
(33, 'BRG033', 'Battery 70AH', 1200000.00, '2026-03-18 09:53:33'),
(34, 'BRG034', 'Headlamp LED', 450000.00, '2026-03-18 09:53:33'),
(35, 'BRG035', 'Spark Plug Iridium', 120000.00, '2026-03-18 09:53:33'),
(36, 'JAS001', 'Ganti Oli Mesin', 150000.00, '2026-03-18 09:53:33'),
(37, 'JAS002', 'Ganti Filter Udara', 80000.00, '2026-03-18 09:53:33'),
(38, 'JAS003', 'Ganti Filter Oli', 70000.00, '2026-03-18 09:53:33'),
(39, 'JAS004', 'Servis Rem Komplit', 450000.00, '2026-03-18 09:53:33'),
(40, 'JAS005', 'Ganti Ban + Balance', 200000.00, '2026-03-18 09:53:33'),
(41, 'JAS006', 'Cek Aki & Charging', 100000.00, '2026-03-18 09:53:33'),
(42, 'JAS007', 'Tune Up Karburator', 250000.00, '2026-03-18 09:53:33'),
(43, 'JAS008', 'Flush Radiator', 180000.00, '2026-03-18 09:53:33'),
(44, 'JAS009', 'Ganti Busi', 120000.00, '2026-03-18 09:53:33'),
(45, 'JAS010', 'Overhaul Kopling', 800000.00, '2026-03-18 09:53:33'),
(46, 'JAS011', 'Ganti Shockbreaker', 600000.00, '2026-03-18 09:53:33'),
(47, 'JAS012', 'Setting Valve', 350000.00, '2026-03-18 09:53:33'),
(48, 'JAS013', 'Spooring & Nooting', 250000.00, '2026-03-18 09:53:33'),
(49, 'JAS014', 'Ganti Kaliper Rem', 400000.00, '2026-03-18 09:53:33'),
(50, 'JAS015', 'Cek Suspensi', 150000.00, '2026-03-18 09:53:33'),
(51, 'JAS016', 'Ganti CV Joint', 550000.00, '2026-03-18 09:53:33'),
(52, 'JAS017', 'Servis Transmisi', 750000.00, '2026-03-18 09:53:33'),
(53, 'JAS018', 'Cuci Motor Komplit', 50000.00, '2026-03-18 09:53:33'),
(54, 'JAS019', 'Ganti Kampas Rem', 200000.00, '2026-03-18 09:53:33'),
(55, 'JAS020', 'Diagnosa Komputer', 100000.00, '2026-03-18 09:53:33');

-- --------------------------------------------------------

--
-- Struktur dari tabel `statistik`
--

CREATE TABLE `statistik` (
  `id` int(11) NOT NULL,
  `jenis_statistik` varchar(50) DEFAULT NULL,
  `periode_awal` date DEFAULT NULL,
  `periode_akhir` date DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struktur dari tabel `transaksi`
--

CREATE TABLE `transaksi` (
  `id` int(11) NOT NULL,
  `no_transaksi` varchar(50) NOT NULL,
  `tanggal` date NOT NULL,
  `tipe_transaksi` varchar(20) NOT NULL,
  `total_harga` decimal(14,2) DEFAULT 0.00,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `pelanggan_id` int(11) DEFAULT NULL,
  `pengguna_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `transaksi`
--

INSERT INTO `transaksi` (`id`, `no_transaksi`, `tanggal`, `tipe_transaksi`, `total_harga`, `created_at`, `pelanggan_id`, `pengguna_id`) VALUES
(1, 'TRX-20241001-001', '2026-01-03', 'Campuran', 735000.00, '2026-03-18 09:53:33', 1, 1),
(2, 'TRX-20241002-002', '2026-01-05', 'Campuran', 1220000.00, '2026-03-18 09:53:33', 2, 1),
(4, 'TRX-20241005-004', '2026-01-10', 'Barang', 2100000.00, '2026-03-18 09:53:33', 4, 1),
(5, 'TRX-20241007-005', '2026-01-12', 'Barang', 3450000.00, '2026-03-18 09:53:33', 5, 1),
(6, 'TRX-20241010-006', '2026-01-15', 'Campuran', 635000.00, '2026-03-18 09:53:33', 6, 1),
(7, 'TRX-20241012-007', '2026-01-18', 'Campuran', 1800000.00, '2026-03-18 09:53:33', 7, 1),
(8, 'TRX-20241015-008', '2026-01-20', 'Barang', 1085000.00, '2026-03-18 09:53:33', 8, 1),
(9, 'TRX-20241018-009', '2026-01-22', 'Campuran', 2700000.00, '2026-03-18 09:53:33', 9, 1),
(10, 'TRX-20241020-010', '2026-01-25', 'Barang', 1300000.00, '2026-03-18 09:53:33', 10, 1),
(11, 'TRX-20241022-011', '2026-02-02', 'Campuran', 1460000.00, '2026-03-18 09:53:33', 11, 1),
(12, 'TRX-20241025-012', '2026-02-04', 'Barang', 815000.00, '2026-03-18 09:53:33', 12, 1),
(13, 'TRX-20241028-013', '2026-02-06', 'Campuran', 1220000.00, '2026-03-18 09:53:33', 13, 1),
(14, 'TRX-20241030-014', '2026-02-09', 'Campuran', 1940000.00, '2026-03-18 09:53:33', 14, 1),
(15, 'TRX-20241101-015', '2026-02-12', 'Barang', 1170000.00, '2026-03-18 09:53:33', 15, 1),
(16, 'TRX-20241103-016', '2026-02-15', 'Barang', 1200000.00, '2026-03-18 09:53:33', 16, 1),
(17, 'TRX-20241105-017', '2026-02-18', 'Campuran', 890000.00, '2026-03-18 09:53:33', 17, 1),
(18, 'TRX-20241108-018', '2026-02-20', 'Barang', 610000.00, '2026-03-18 09:53:33', 18, 1),
(19, 'TRX-20241110-019', '2026-02-22', 'Campuran', 780000.00, '2026-03-18 09:53:33', 19, 1),
(20, 'TRX-20241112-020', '2026-02-25', 'Campuran', 750000.00, '2026-03-18 09:53:33', 20, 1),
(21, 'TRX-20241115-021', '2026-03-02', 'Campuran', 1700000.00, '2026-03-18 09:53:33', 21, 1),
(22, 'TRX-20241118-022', '2026-03-04', 'Barang', 250000.00, '2026-03-18 09:53:33', 22, 1),
(23, 'TRX-20241120-023', '2026-03-06', 'Barang', 1850000.00, '2026-03-18 09:53:33', 23, 1),
(24, 'TRX-20241122-024', '2026-03-08', 'Barang', 1150000.00, '2026-03-18 09:53:33', 24, 1),
(25, 'TRX-20241125-025', '2026-03-10', 'Barang', 2150000.00, '2026-03-18 09:53:33', 25, 1),
(26, 'TRX-20241127-026', '2026-03-12', 'Barang', 1130000.00, '2026-03-18 09:53:33', 1, 1),
(27, 'TRX-20241128-027', '2026-03-14', 'Barang', 1100000.00, '2026-03-18 09:53:33', 2, 1),
(28, 'TRX-20241129-028', '2026-03-15', 'Barang', 600000.00, '2026-03-18 09:53:33', 3, 1),
(29, 'TRX-20241130-029', '2026-03-16', 'Campuran', 655000.00, '2026-03-18 09:53:33', 4, 1),
(30, 'TRX-20241201-030', '2026-03-17', 'Campuran', 1500000.00, '2026-03-18 09:53:33', 5, 1),
(31, 'TRX-C-00001', '2026-03-25', 'Campuran', 395000.00, '2026-03-25 12:32:23', 23, 1),
(32, 'TRX-J-00001', '2026-03-25', 'Jasa', 550000.00, '2026-03-25 13:06:53', 3, 1),
(33, 'TRX-C-00002', '2026-03-25', 'Campuran', 55000.00, '2026-03-25 13:17:25', 2, 1),
(34, 'TRX-C-00003', '2026-03-25', 'Campuran', 450000.00, '2026-03-25 14:54:54', 1, 2);

--
-- Indexes for dumped tables
--

--
-- Indeks untuk tabel `barang`
--
ALTER TABLE `barang`
  ADD PRIMARY KEY (`id`),
  ADD KEY `kategori_id` (`kategori_id`);

--
-- Indeks untuk tabel `detail_transaksi`
--
ALTER TABLE `detail_transaksi`
  ADD PRIMARY KEY (`id`),
  ADD KEY `transaksi_id` (`transaksi_id`),
  ADD KEY `produk_id` (`produk_id`);

--
-- Indeks untuk tabel `jasa`
--
ALTER TABLE `jasa`
  ADD PRIMARY KEY (`id`);

--
-- Indeks untuk tabel `kategori_barang`
--
ALTER TABLE `kategori_barang`
  ADD PRIMARY KEY (`id`);

--
-- Indeks untuk tabel `laporan`
--
ALTER TABLE `laporan`
  ADD PRIMARY KEY (`id`);

--
-- Indeks untuk tabel `pelanggan`
--
ALTER TABLE `pelanggan`
  ADD PRIMARY KEY (`id`);

--
-- Indeks untuk tabel `pengguna`
--
ALTER TABLE `pengguna`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- Indeks untuk tabel `produk`
--
ALTER TABLE `produk`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `kode` (`kode`);

--
-- Indeks untuk tabel `statistik`
--
ALTER TABLE `statistik`
  ADD PRIMARY KEY (`id`);

--
-- Indeks untuk tabel `transaksi`
--
ALTER TABLE `transaksi`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `no_transaksi` (`no_transaksi`),
  ADD KEY `pelanggan_id` (`pelanggan_id`),
  ADD KEY `pengguna_id` (`pengguna_id`);

--
-- AUTO_INCREMENT untuk tabel yang dibuang
--

--
-- AUTO_INCREMENT untuk tabel `detail_transaksi`
--
ALTER TABLE `detail_transaksi`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=91;

--
-- AUTO_INCREMENT untuk tabel `kategori_barang`
--
ALTER TABLE `kategori_barang`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=26;

--
-- AUTO_INCREMENT untuk tabel `laporan`
--
ALTER TABLE `laporan`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT untuk tabel `pelanggan`
--
ALTER TABLE `pelanggan`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=28;

--
-- AUTO_INCREMENT untuk tabel `pengguna`
--
ALTER TABLE `pengguna`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT untuk tabel `produk`
--
ALTER TABLE `produk`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=60;

--
-- AUTO_INCREMENT untuk tabel `statistik`
--
ALTER TABLE `statistik`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT untuk tabel `transaksi`
--
ALTER TABLE `transaksi`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=35;

--
-- Ketidakleluasaan untuk tabel pelimpahan (Dumped Tables)
--

--
-- Ketidakleluasaan untuk tabel `barang`
--
ALTER TABLE `barang`
  ADD CONSTRAINT `barang_ibfk_1` FOREIGN KEY (`id`) REFERENCES `produk` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `barang_ibfk_2` FOREIGN KEY (`kategori_id`) REFERENCES `kategori_barang` (`id`);

--
-- Ketidakleluasaan untuk tabel `detail_transaksi`
--
ALTER TABLE `detail_transaksi`
  ADD CONSTRAINT `detail_transaksi_ibfk_1` FOREIGN KEY (`transaksi_id`) REFERENCES `transaksi` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `detail_transaksi_ibfk_2` FOREIGN KEY (`produk_id`) REFERENCES `produk` (`id`);

--
-- Ketidakleluasaan untuk tabel `jasa`
--
ALTER TABLE `jasa`
  ADD CONSTRAINT `jasa_ibfk_1` FOREIGN KEY (`id`) REFERENCES `produk` (`id`) ON DELETE CASCADE;

--
-- Ketidakleluasaan untuk tabel `transaksi`
--
ALTER TABLE `transaksi`
  ADD CONSTRAINT `transaksi_ibfk_1` FOREIGN KEY (`pelanggan_id`) REFERENCES `pelanggan` (`id`),
  ADD CONSTRAINT `transaksi_ibfk_2` FOREIGN KEY (`pengguna_id`) REFERENCES `pengguna` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
