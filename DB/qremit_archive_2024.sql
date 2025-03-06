-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Mar 06, 2025 at 10:24 AM
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
-- Table structure for table `qremit_archive_2024`
--

CREATE TABLE `qremit_archive_2024` (
  `id` int(11) NOT NULL,
  `transaction_no` varchar(30) NOT NULL,
  `status` varchar(20) NOT NULL,
  `entered_date` varchar(30) NOT NULL,
  `currency` varchar(10) NOT NULL,
  `amount` varchar(15) NOT NULL,
  `remitter_name` varchar(255) NOT NULL,
  `exchange_code` varchar(15) NOT NULL,
  `bank_name` varchar(255) NOT NULL,
  `branch_name` varchar(255) NOT NULL,
  `district_name` varchar(255) NOT NULL,
  `beneficiary_account` varchar(30) NOT NULL,
  `beneficiary_name` varchar(255) NOT NULL,
  `t1` varchar(255) NOT NULL,
  `purpose_of_remittance` varchar(255) NOT NULL,
  `branch_code` varchar(10) NOT NULL,
  `abl_branch_name` varchar(255) NOT NULL,
  `zone_name` varchar(255) NOT NULL,
  `zone_code` varchar(255) NOT NULL,
  `t6` varchar(255) NOT NULL,
  `govt_incentive` varchar(15) NOT NULL,
  `type` varchar(10) NOT NULL,
  `processed_date` varchar(30) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `qremit_archive_2024`
--
ALTER TABLE `qremit_archive_2024`
  ADD PRIMARY KEY (`id`),
  ADD KEY `key` (`transaction_no`,`exchange_code`,`amount`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `qremit_archive_2024`
--
ALTER TABLE `qremit_archive_2024`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
