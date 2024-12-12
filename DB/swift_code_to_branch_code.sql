-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Dec 11, 2024 at 10:36 AM
-- Server version: 10.4.27-MariaDB
-- PHP Version: 7.4.33

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
-- Table structure for table `swift_code_to_branch_code`
--

CREATE TABLE `swift_code_to_branch_code` (
  `id` int(255) NOT NULL,
  `branch_name` varchar(255) NOT NULL,
  `swift_code` varchar(255) NOT NULL,
  `branch_code` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

--
-- Dumping data for table `swift_code_to_branch_code`
--

INSERT INTO `swift_code_to_branch_code` (`id`, `branch_name`, `swift_code`, `branch_code`) VALUES
(1, 'Agrani Bank Ltd.Head Office', 'AGBKBDDH000', '-4006'),
(2, 'Principal.Br.Motijheel,Dhaka', 'AGBKBDDH001', '4006'),
(3, 'BangaBandhu Avenue Br. Dhaka', 'AGBKBDDH002', '1170'),
(4, 'Moulvi Bazar Br.Dhaka', 'AGBKBDDH003', '2507'),
(5, 'Amin Court Br. Dhaka', 'AGBKBDDH004', '1242'),
(6, 'Ramna Br. Dhaka', 'AGBKBDDH005', '2531'),
(7, 'Foreign Exchange Br.', 'AGBKBDDH006', '6009'),
(8, 'Sadarghat Br. Dhaka', 'AGBKBDDH007', '2542'),
(9, 'Banani Br. Dhaka', 'AGBKBDDH008', '8686'),
(10, 'BangaBandhu Road Br. Narayangonj', 'AGBKBDDH009', '1268'),
(11, 'Court Road Br.Narayangonj', 'AGBKBDDH010', '3322'),
(12, 'Faridpur Chawk Bazar Br.', 'AGBKBDDH011', '3096'),
(13, 'WASA Br.Kawran Bazar', 'AGBKBDDH012', '4786'),
(14, 'Tejgaon I/A Br. Dhaka', 'AGBKBDDH013', '2550'),
(15, 'Nawabpur Road Br. Dhaka', 'AGBKBDDH014', '2515'),
(16, 'Agrabad C/A.Br. Chittagong', 'AGBKBDDH015', '2844'),
(17, 'Asadgonj Br. Chittagong', 'AGBKBDDH016', '2783'),
(18, 'Laldighi East Br. Chittagong', 'AGBKBDDH017', '2454'),
(19, 'Agrabad Jahan Bldg.Br.Ctg', 'AGBKBDDH018', '1016'),
(20, 'Rajgonj Br. Comilla', 'AGBKBDDH020', '1085'),
(21, 'Laldighirpar Br. Sylhet', 'AGBKBDDH021', '2680'),
(22, 'Choumuhoni Br. Noakhali', 'AGBKBDDH022', '2481'),
(23, 'Sir Iqbal Rd.Br. Khulna', 'AGBKBDDH023', '1223'),
(24, 'Jessore Br. Jessore', 'AGBKBDDH024', '1162'),
(25, 'Shaheb Bazar, Rajshahi', 'AGBKBDDH027', '2645'),
(26, 'Thana Road Br. Bogra', 'AGBKBDDH029', '2438'),
(27, 'Maldahpatty Br. Dinajpur', 'AGBKBDDH030', '2568'),
(28, 'Hakimpur Br. Dinajpur', 'AGBKBDDH031', '6710'),
(29, 'Clay Road Br. Khulna', 'AGBKBDDH032', '2584'),
(30, 'B.WAPDA Br. Dhaka', 'AGBKBDDH033', '2791'),
(31, 'Purana Paltan Br. Dhaka', 'AGBKBDDH034', '6423'),
(32, 'EPZ Corporate Br.Ctg/Bay Shopping', 'AGBKBDDH035', '9864'),
(33, 'Green Road Br. Dhaka', 'AGBKBDDH036', '3929'),
(34, 'New Market Br. Chittagong', 'AGBKBDDH039', '3287'),
(35, 'Hotel Sheraton Corp. Branch, Dhaka', 'AGBKBDDH040', '3945'),
(36, 'Strand Road Br. Chittagong', 'AGBKBDDH041', '2462'),
(37, 'Gulshan Corp.Br.Dhaka', 'AGBKBDDH042', '9787'),
(38, 'Gazipur Corp. Br., Gazipur', 'AGBKBDDH038', '9787'),
(39, 'Rangpur Branch, Rangpur', 'AGBKBDDH028', '2661'),
(40, 'Kushtia Br., Kushtia', 'AGBKBDDH026', '2600');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `swift_code_to_branch_code`
--
ALTER TABLE `swift_code_to_branch_code`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `swift_code_to_branch_code`
--
ALTER TABLE `swift_code_to_branch_code`
  MODIFY `id` int(255) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=41;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
