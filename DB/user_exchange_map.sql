-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Oct 24, 2024 at 06:07 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

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
  `id` int(11) NOT NULL,
  `exchange_code` varchar(20) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `published` tinyint(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `user_exchange_map`
--

INSERT INTO `user_exchange_map` (`id`, `exchange_code`, `user_id`, `published`) VALUES
(1, '7010231', 9, 1),
(2, '7010209', 9, 1),
(3, '7010234', 9, 1),
(4, '7010226', 9, 1),
(5, '7010299', 9, 1),
(6, '7010290', 9, 1),
(7, '111111', 9, 1),
(8, '222222', 9, 1),
(9, '333333', 9, 1),
(10, '7010239', 9, 1),
(11, '7010246', 9, 1),
(12, '7010240', 9, 1),
(13, '7010245', 9, 1),
(14, '7010262', 9, 1),
(15, '7010263', 9, 1),
(16, '7010203', 9, 1),
(17, '7010279', 9, 1),
(18, '7010206', 9, 1),
(19, '7010223', 9, 1),
(20, '7010260', 9, 1),
(21, '7010242', 9, 1),
(22, '7010229', 9, 1),
(23, '7010276', 9, 1),
(24, '7010272', 9, 1),
(25, '7010256', 9, 1),
(26, '7010248', 9, 1),
(27, '7010266', 9, 1),
(28, '7010216', 9, 1),
(29, '7010289', 9, 1);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `user_exchange_map`
--
ALTER TABLE `user_exchange_map`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_exchange_code` (`exchange_code`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `user_exchange_map`
--
ALTER TABLE `user_exchange_map`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=30;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
