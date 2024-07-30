-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jul 15, 2024 at 08:48 AM
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
-- Table structure for table `ex_house_list`
--

CREATE TABLE `ex_house_list` (
  `row_id` int(11) NOT NULL,
  `active_status` bit(1) DEFAULT NULL,
  `exchange_code` varchar(255) NOT NULL,
  `exchange_name` varchar(255) NOT NULL,
  `exchange_short_name` varchar(255) NOT NULL,
  `nrta_code` varchar(255) NOT NULL,
  `base_table_name` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `ex_house_list`
--

INSERT INTO `ex_house_list` (`row_id`, `active_status`, `exchange_code`, `exchange_name`, `exchange_short_name`, `nrta_code`, `base_table_name`) VALUES
(12, b'1', '7010231', 'Al Muzaini Exchange Company K', 'Al Muzaini', '7038', 'muzaini'),
(22, b'1', '7010209', 'Bahrain Exchange Co Kuwait', 'BEC', '7012', 'bec'),
(68, b'1', '7010234', 'National Finance and Exch. Co.', 'NAFEX', '7046', 'nafex'),
(109, b'1', '7010226', 'Agrani Exchange House Singapore', 'Singapore', '7025', 'singapore'),
(110, b'1', '7010299', 'Ezremit', 'EZ Remit', '7013', 'ezremit'),
(111, b'1', '7010290', 'Continental Exchange Solution( Ria)', 'RIA', '7081', 'ria');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `ex_house_list`
--
ALTER TABLE `ex_house_list`
  ADD PRIMARY KEY (`row_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `ex_house_list`
--
ALTER TABLE `ex_house_list`
  MODIFY `row_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=112;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
