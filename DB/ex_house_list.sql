-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: May 02, 2024 at 10:54 AM
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
-- Table structure for table `ex_house_list`
--

CREATE TABLE `ex_house_list` (
  `row_id` int(11) NOT NULL,
  `exchange_code` varchar(255) NOT NULL,
  `exchange_name` varchar(255) NOT NULL,
  `is_active` varchar(255) DEFAULT NULL,
  `nrta_code` varchar(255) NOT NULL,
  `exchange_short_name` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

--
-- Dumping data for table `ex_house_list`
--

INSERT INTO `ex_house_list` (`row_id`, `exchange_code`, `exchange_name`, `is_active`, `nrta_code`, `exchange_short_name`) VALUES
(1, '7119', 'al raji', '1', '7009', 'alrajhi'),
(2, 'RMO', 'Agrani Exchange House Pte. Ltd', '1', '7025', ''),
(3, '7010226', 'Agrani Exchange House, Singapo', '1', '7025', ''),
(4, '7010273', 'Agrani Exchange House, Singapo', '1', '7025', ''),
(5, 'INF', 'Agrani Remittance House Sdn, B', '1', '7035', ''),
(6, '7010221', 'Al Ahalia Money Ex. ', '1', '7047', ''),
(7, 'M02', 'Al Ansari Ex. Co, Abu Dhabi, U', '1', '7053', ''),
(8, '7010239', 'Al Ansari Ex. Co, Abu Dhabi, U', '1', '7053', ''),
(9, '7010246', 'Al jadeed Exchange', '1', '7058', ''),
(10, '7010232', 'Al Mulla Int. Ex. Co. Kuwait ', '1', '7040', ''),
(11, 'M21', 'Al Mulla International Exchang', '1', '7040', ''),
(12, '7010231', 'Al Muzaini Exchange Company, K', '1', '7038', ''),
(13, '7010240', 'Al Rostamani Int\'l Ex. Co. Abu', '1', '7052', ''),
(14, '7010245', 'Al Zaman Ex. , Qatar', '1', '7057', ''),
(15, '7010262', 'ALAWANEH EX. JORDAN', '1', '7082', ''),
(16, '7010205', 'Al-Fardan Exchange  Co. Doha, ', '1', '7002', ''),
(17, '7010220', 'Al-Fardan Exchange, AbuDhabi', '1', '7010', ''),
(18, '7010233', 'AL-Ghurair Ex. Dubai, UAE', '1', '7043', ''),
(19, '7009', 'Al-Rajhi Banking & Investment ', '1', '7009', ''),
(20, '7010263', 'Aman Ex- Kuwait', '1', '7084', ''),
(21, '7010203', 'Arab National Bank, Riyadh, K.', '1', '7030', ''),
(22, '7010209', 'Bahrain Exchange Co, Kuwait', '1', '7012', ''),
(23, '7010243', 'Bahrain Financing Co. Bahrain ', '1', '7013', ''),
(24, '7010204', 'Bank Al-Bilad, Riyadh, K.S.A', '1', '7031', ''),
(25, '7010279', 'Belhasa Global Ex', '1', '7098', ''),
(26, 'M24', 'Brac Saajjan Exchange', '1', '7091', ''),
(27, '7010270', 'Brac Saajjan Exchange', '1', '7091', ''),
(28, '7010278', 'Brac Saajjan Exchange', '1', '7091', ''),
(29, '7010251', 'Choice Money Ex. , USA (Small World)', '1', '7066', ''),
(30, 'M07', 'City bank limited,Malaysia', '1', '7071', ''),
(31, '7010211', 'City Int\'l Exchange Co, Kuwait', '1', '7016', ''),
(32, 'M13', 'Continental Exchange Solution( Ria)', '1', '7081', ''),
(33, '7010254', 'Dollarco Exchange Co. Kuwait', '1', '7019', ''),
(34, '7010206', 'Eastern Exchange Est. Doha, Qa', '1', '7000', ''),
(35, '7010244', 'Emirates India Int. Ex. , UAE', '1', '7056', ''),
(36, 'M06', 'Ezremit', '1', '7013', ''),
(37, 'M18', 'First Security Islami Exchange', '1', '7093', ''),
(38, '7010269', 'Global Money Exchange', '1', '7089', ''),
(39, 'M20', 'Global Money Express, S K', '1', '7095', ''),
(40, '7010275', 'Global Money Express, S K', '1', '7095', ''),
(41, '7010277', 'Global Money Express, S K', '1', '7095', ''),
(42, 'M04', 'Gulf Overseas,Oman', '1', '7069', ''),
(43, 'M36', 'Hamdan Exchange', '1', '7099', ''),
(44, 'M22', 'Hello Pasia (PTY) Ltd', '1', '7097', ''),
(45, '7010223', 'Index Exchange Co. LLC (Al Falah)', '1', '7024', ''),
(46, '7010260', 'Instant Cash', '1', '7080', ''),
(47, 'M12', 'Instant Cash FZE,UAE', '1', '7080', ''),
(48, '7010258', 'Joyalukkas EX. LLC', '1', '7078', ''),
(49, '7010255', 'Joyalukkas Exchange ', '1', '7072', ''),
(50, '7010264', 'Joyalukkas Exchange ', '1', '7085', ''),
(51, 'M17', 'K & H Remittance Services Brun', '1', '7092', ''),
(52, '7010271', 'K & H Remittance Services Brun', '1', '7092', ''),
(53, 'M26', 'Kuwait Bahrain Int. Ex. Co', '1', '7006', ''),
(54, '7010207', 'Kuwait Bahrain Int\'l Ex. Kuwai', '1', '7006', ''),
(55, '7010242', 'Lari Exchange  (Abu Dhabi, UAE', '1', '7054', ''),
(56, '7010237', 'Lulu Exchange Co', '1', '7050', ''),
(57, 'M27', 'Lulu Exchange Co. ', '1', '7050', ''),
(58, 'MRC', 'Marchantrade Asia Sdn.Bhd. Mal', '1', '7049', ''),
(59, 'M19', 'Max Money Sdn Bhd', '1', '7094', ''),
(60, 'M25', 'Al-Rajhi Banking & Investment ', '1', '7083', ''),
(61, '7010265', 'Modern Ex. Co., Bahrain', '1', '7083', ''),
(62, 'M23', 'Modern Ex. Co., Oman', '1', '7061', ''),
(63, '7010250', 'Modern Ex. Co., Oman', '1', '7061', ''),
(64, 'MGM', 'MoneyGram Int. Ex. ', '1', '7041', ''),
(65, '7010252', 'Multinet  trust Ex. LLC, UAE ', '1', '7067', ''),
(66, 'M05', 'National Exchange Company, Ita', '1', '7037', ''),
(67, '7010229', 'National Exchange, Italy', '1', '7037', ''),
(68, '7010234', 'National Finance and Exch. Co.', '1', '7046', ''),
(69, 'M10', 'NBL Money Transfer ', '1', '7076', ''),
(70, 'NBL', 'NBL Money Transfer,Malaysia', '1', '7059', ''),
(71, '7010276', 'NBL Money Transfer,Malaysia', '1', '7059', ''),
(72, '7010241', 'NCBJ, Jeddah, KSA (SNB)', '1', '7055', ''),
(73, 'M15', 'NEC Money Transfer,  U.K.', '1', '7087', ''),
(74, '7010272', 'NEC Money Transfer, UK', '1', '7087', ''),
(75, '7010208', 'Oman Exchange Co, Kuwait', '1', '7008', ''),
(76, '7010215', 'Oman Int\'l Exchange Ltd. Oman ', '1', '7021', ''),
(77, 'M14', 'PLACID NK CORPORATION', '1', '7065', ''),
(78, 'PRB', 'Prabhu Group Inc.', '1', '7051', ''),
(79, '7010238', 'Prabhu Group Inc. U S A', '1', '7051', ''),
(80, '7010256', 'SIGUE Global Sercice ', '1', '7074', ''),
(81, '7010248', 'Standard Express', '1', '7060', ''),
(82, '7010274', 'Sunman Express ', '1', '7096', ''),
(83, '7010266', 'The Saudi Investment Bank (SAIB)', '1', '7086', ''),
(84, 'TFT', 'Trans Fast Remittance USA', '1', '7044', ''),
(85, 'M03', 'Turbo Cash', '1', '7003', ''),
(86, 'M16', 'U REMIT INTERNATIONAL, CANADA', '1', '7088', ''),
(87, '7010267', 'U REMIT INTERNATIONAL, CANADA', '1', '7088', ''),
(88, '7010225', 'U.A.E. Exchange LLC, Abu Dhabi', '1', '7028', ''),
(89, '7010257', 'U.A.E. Exchange, Malaysia', '1', '7075', ''),
(90, '7010213', 'UAE Exchange LLC ', '1', '7027', ''),
(91, '7010216', 'Unimoni Exchange', '1', '7034', ''),
(92, '7010268', 'Val You SDN BHD', '1', '7090', ''),
(93, '7010224', 'Wall Street Exchange, Abu Dhab', '1', '7033', ''),
(94, 'M01', 'Western Union', '1', '7063', ''),
(95, '7010219', 'Zenj Exchange Co. Bahrain', '1', '7003', ''),
(96, 'M28', 'Global Money Exchange Oman', '1', '7089', ''),
(97, 'M29', 'Gmoney Transact.co Ltd Korea ', '1', '7081', ''),
(98, 'M30', 'U.A.E Ex WLL Kuait', '1', '7027', ''),
(99, 'M31', 'Joyalukkas Exchange Oman', '1', '7078', ''),
(100, 'M32', 'Joyalukkas EX Co kuwait', '1', '7085', ''),
(101, 'M33', 'Joyalukkas EX Co UAE', '1', '7072', ''),
(102, 'M08', 'SHA Global', '1', '7104', ''),
(103, '7010297', 'NBl Maldives', '1', '7105', ''),
(104, 'M34', 'NBL Maldives', '1', '7105', ''),
(105, 'M35', 'SG Quick Pay', '1', '7107', ''),
(106, '7010284', 'Hamdan Exchange,Oman', '1', '7099', ''),
(107, 'M37', 'Japan Remit Finance', '1', '7108', ''),
(108, 'M38', 'Saudi National Bank (SNB) , Je', '1', '7055', '');

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
  MODIFY `row_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=109;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
