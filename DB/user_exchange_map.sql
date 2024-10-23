-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Oct 23, 2024 at 03:58 PM
-- Server version: 8.0.39-0ubuntu0.20.04.1
-- PHP Version: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `frd_qremit_converter`
--

-- --------------------------------------------------------

--
-- Table structure for table `user_exchange_map`
--

CREATE TABLE `user_exchange_map` (
  `id` int NOT NULL,
  `exchange_code` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  `published` tinyint(1) DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `user_exchange_map`
--

INSERT INTO `user_exchange_map` (`id`, `exchange_code`, `user_id`, `published`) VALUES
(1, '7010231', 4, 1),
(2, '7010209', 4, 1),
(3, '7010234', 4, 1),
(4, '7010226', 4, 1),
(5, '7010299', 4, 1),
(6, '7010290', 4, 1),
(7, '111111', 4, 1),
(8, '222222', 4, 1),
(9, '333333', 4, 1),
(10, '7010239', 4, 1),
(11, '7010246', 4, 1),
(12, '7010240', 4, 1),
(13, '7010245', 4, 1),
(14, '7010262', 4, 1),
(15, '7010263', 4, 1),
(16, '7010203', 4, 1),
(17, '7010279', 4, 1),
(18, '7010206', 4, 1),
(19, '7010223', 4, 1),
(20, '7010260', 4, 1),
(21, '7010242', 4, 1),
(22, '7010229', 4, 1),
(23, '7010276', 4, 1),
(24, '7010272', 4, 1),
(25, '7010256', 4, 1),
(26, '7010248', 4, 1),
(27, '7010266', 4, 1),
(28, '7010216', 4, 1),
(29, '7010289', 4, 1);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `user_exchange_map`
--
ALTER TABLE `user_exchange_map`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `user_exchange_map`
--
ALTER TABLE `user_exchange_map`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=32;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
