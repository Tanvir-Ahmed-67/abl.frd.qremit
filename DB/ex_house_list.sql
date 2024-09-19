-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Sep 11, 2024 at 08:40 AM
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
  `id` int(11) NOT NULL,
  `active_status` tinyint(1) NOT NULL DEFAULT 0,
  `base_table_name` varchar(255) NOT NULL,
  `exchange_code` varchar(255) NOT NULL,
  `exchange_name` varchar(255) NOT NULL,
  `exchange_short_name` varchar(255) NOT NULL,
  `nrta_code` varchar(255) NOT NULL,
  `is_api` tinyint(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `ex_house_list`
--

INSERT INTO `ex_house_list` (`id`, `active_status`, `base_table_name`, `exchange_code`, `exchange_name`, `exchange_short_name`, `nrta_code`, `is_api`) VALUES
(12, 1, 'muzaini', '7010231', 'Al Muzaini Exchange Company K', 'Al Muzaini', '7038', 0),
(22, 1, 'bec', '7010209', 'Bahrain Exchange Co Kuwait', 'BEC', '7012', 0),
(68, 1, 'nafex', '7010234', 'National Finance and Exch. Co.', 'NAFEX', '7046', 0),
(109, 1, 'singapore', '7010226', 'Agrani Exchange House Singapore', 'Singapore', '7025', 1),
(110, 1, 'ezremit', '7010299', 'Ezremit', 'EZ Remit', '7013', 1),
(111, 1, 'ria', '7010290', 'Continental Exchange Solution( Ria)', 'RIA', '7081', 1),
(112, 1, 'apibeftn', '111111', 'API BEFTN', 'API BEFTN', '1000', 0),
(113, 1, 'apit24', '222222', 'API T24', 'API T24', '2000', 1);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `ex_house_list`
--
ALTER TABLE `ex_house_list`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `ex_house_list`
--
ALTER TABLE `ex_house_list`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=114;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
