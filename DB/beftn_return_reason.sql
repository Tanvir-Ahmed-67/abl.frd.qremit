-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Aug 12, 2025 at 01:50 PM
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
-- Table structure for table `beftn_return_reason`
--

CREATE TABLE `beftn_return_reason` (
  `id` int(11) NOT NULL,
  `return_code` varchar(10) NOT NULL,
  `return_name` varchar(255) NOT NULL,
  `is_active` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `beftn_return_reason`
--

INSERT INTO `beftn_return_reason` (`id`, `return_code`, `return_name`, `is_active`) VALUES
(1, 'R01', 'Insufficient Funds', 1),
(2, 'R02', 'Account Closed', 1),
(3, 'R04', 'Invalid Account Number', 1),
(4, 'R05', 'Unauthorized Debit to Consumer Account Using Corporate SEC Code', 1),
(5, 'R06', 'Returned per OB\'s Request', 1),
(6, 'R10', 'Customer Advises Not Authorized', 1),
(7, 'R14', 'Representative Payee Deceased or Unable to Continue in that Capacity', 1),
(8, 'R15', 'Beneficiary or Account Holder (Other than a Representative Payee) Deceased', 1),
(9, 'R16', 'Account Frozen', 1),
(10, 'R20', 'Non-Transaction Account', 1),
(11, 'R23', 'Credit Entry Refused by Receiver', 1),
(12, 'R29', 'Corporate Customer Advises Not Authorized', 1),
(13, 'R03', 'NO ACCOUNT/UNABLE TO LOCATE ACCOUNT', 1),
(14, 'R08', 'PAYMENT STOPPED', 1);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `beftn_return_reason`
--
ALTER TABLE `beftn_return_reason`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `return_code` (`return_code`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `beftn_return_reason`
--
ALTER TABLE `beftn_return_reason`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
