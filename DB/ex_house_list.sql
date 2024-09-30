-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Sep 30, 2024 at 12:42 PM
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
  `active_status` tinyint(1) DEFAULT 0,
  `base_table_name` varchar(255) NOT NULL,
  `exchange_code` varchar(255) NOT NULL,
  `exchange_name` varchar(255) NOT NULL,
  `exchange_short_name` varchar(255) NOT NULL,
  `is_api` tinyint(1) DEFAULT 0,
  `nrta_code` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `ex_house_list`
--

INSERT INTO `ex_house_list` (`id`, `active_status`, `base_table_name`, `exchange_code`, `exchange_name`, `exchange_short_name`, `is_api`, `nrta_code`) VALUES
(1, 1, 'muzaini', '7010231', 'Al Muzaini Exchange Company K', 'Al Muzaini', 0, '7038'),
(2, 1, 'bec', '7010209', 'Bahrain Exchange Co Kuwait', 'BEC', 0, '7012'),
(3, 1, 'nafex', '7010234', 'National Finance and Exch. Co.', 'NAFEX', 0, '7046'),
(4, 1, 'singapore', '7010226', 'Agrani Exchange House Singapore', 'Singapore', 1, '7025'),
(5, 1, 'ezremit', '7010299', 'Ezremit', 'EZ Remit', 1, '7013'),
(6, 1, 'ria', '7010290', 'Continental Exchange Solution( Ria)', 'RIA', 1, '7081'),
(7, 1, 'api_beftn', '111111', 'API BEFTN', 'API BEFTN', 0, '1000'),
(8, 1, 'api_t24', '222222', 'API T24', 'API T24', 1, '2000'),
(9, 1, 'coc_paid', '333333', 'COC Paid', 'COC Paid', 1, '3000'),
(10, 1, '', '7119', 'al raji', '', 0, '7009'),
(11, 1, '', 'RMO', 'Agrani Exchange House Pte. Ltd', '', 0, '7025'),
(12, 1, '', '7010273', 'Agrani Exchange House Singapore', '', 0, '7025'),
(13, 1, '', 'INF', 'Agrani Remittance House Sdn B', '', 0, '7035'),
(14, 1, '', '7010221', 'Al Ahalia Money Ex. ', '', 0, '7047'),
(15, 1, '', 'M02', 'Al Ansari Ex. Co Abu Dhabi U', '', 0, '7053'),
(16, 1, '', '7010239', 'Al Ansari Ex. Co Abu Dhabi U', '', 0, '7053'),
(17, 1, '', '7010246', 'Al jadeed Exchange', '', 0, '7058'),
(18, 1, '', '7010232', 'Al Mulla Int. Ex. Co. Kuwait ', '', 0, '7040'),
(19, 1, '', 'M21', 'Al Mulla International Exchang', '', 0, '7040'),
(20, 1, '', '7010240', 'Al Rostamani Int\'l Ex. Co. Abu', '', 0, '7052'),
(21, 1, '', '7010245', 'Al Zaman Ex.  Qatar', '', 0, '7057'),
(22, 1, '', '7010262', 'ALAWANEH EX. JORDAN', '', 0, '7082'),
(23, 1, '', '7010205', 'Al-Fardan Exchange  Co. Doha ', '', 0, '7002'),
(24, 1, '', '7010220', 'Al-Fardan Exchange AbuDhabi', '', 0, '7010'),
(25, 1, '', '7010233', 'AL-Ghurair Ex. Dubai UAE', '', 0, '7043'),
(26, 1, '', '7009', 'Al-Rajhi Banking & Investment ', '', 0, '7009'),
(27, 1, '', '7010263', 'Aman Ex- Kuwait', '', 0, '7084'),
(28, 1, '', '7010203', 'Arab National Bank Riyadh K.', '', 0, '7030'),
(29, 1, '', '7010243', 'Bahrain Financing Co. Bahrain ', '', 0, '7013'),
(30, 1, '', '7010204', 'Bank Al-Bilad Riyadh K.S.A', '', 0, '7031'),
(31, 1, '', '7010279', 'Belhasa Global Ex', '', 0, '7098'),
(32, 1, '', 'M24', 'Brac Saajjan Exchange', '', 0, '7091'),
(33, 1, '', '7010270', 'Brac Saajjan Exchange', '', 0, '7091'),
(34, 1, '', '7010278', 'Brac Saajjan Exchange', '', 0, '7091'),
(35, 1, '', '7010251', 'Choice Money Ex.  USA (Small World)', '', 0, '7066'),
(36, 1, '', 'M07', 'City bank limitedMalaysia', '', 0, '7071'),
(37, 1, '', '7010211', 'City Int\'l Exchange Co Kuwait', '', 0, '7016'),
(38, 1, '', '7010254', 'Dollarco Exchange Co. Kuwait', '', 0, '7019'),
(39, 1, '', '7010206', 'Eastern Exchange Est. Doha Qa', '', 0, '7000'),
(40, 1, '', '7010244', 'Emirates India Int. Ex.  UAE', '', 0, '7056'),
(41, 1, '', 'M18', 'First Security Islami Exchange', '', 0, '7093'),
(42, 1, '', '7010269', 'Global Money Exchange', '', 0, '7089'),
(43, 1, '', 'M20', 'Global Money Express S K', '', 0, '7095'),
(44, 1, '', '7010275', 'Global Money Express S K', '', 0, '7095'),
(45, 1, '', '7010277', 'Global Money Express S K', '', 0, '7095'),
(46, 1, '', 'M04', 'Gulf OverseasOman', '', 0, '7069'),
(47, 1, '', 'M36', 'Hamdan Exchange', '', 0, '7099'),
(48, 1, '', 'M22', 'Hello Pasia (PTY) Ltd', '', 0, '7097'),
(49, 1, '', '7010223', 'Index Exchange Co. LLC (Al Falah)', '', 0, '7024'),
(50, 1, '', '7010260', 'Instant Cash', '', 0, '7080'),
(51, 1, '', 'M12', 'Instant Cash FZEUAE', '', 0, '7080'),
(52, 1, '', '7010258', 'Joyalukkas EX. LLC', '', 0, '7078'),
(53, 1, '', '7010255', 'Joyalukkas Exchange ', '', 0, '7072'),
(54, 1, '', '7010264', 'Joyalukkas Exchange ', '', 0, '7085'),
(55, 1, '', 'M17', 'K & H Remittance Services Brun', '', 0, '7092'),
(56, 1, '', '7010271', 'K & H Remittance Services Brun', '', 0, '7092'),
(57, 1, '', 'M26', 'Kuwait Bahrain Int. Ex. Co', '', 0, '7006'),
(58, 1, '', '7010207', 'Kuwait Bahrain Int\'l Ex. Kuwai', '', 0, '7006'),
(59, 1, '', '7010242', 'Lari Exchange  (Abu Dhabi UAE', '', 0, '7054'),
(60, 1, '', '7010237', 'Lulu Exchange Co', '', 0, '7050'),
(61, 1, '', 'M27', 'Lulu Exchange Co. ', '', 0, '7050'),
(62, 1, '', 'MRC', 'Marchantrade Asia Sdn.Bhd. Mal', '', 0, '7049'),
(63, 1, '', 'M19', 'Max Money Sdn Bhd', '', 0, '7094'),
(64, 1, '', 'M25', 'Al-Rajhi Banking & Investment ', '', 0, '7083'),
(65, 1, '', '7010265', 'Modern Ex. Co. Bahrain', '', 0, '7083'),
(66, 1, '', 'M23', 'Modern Ex. Co. Oman', '', 0, '7061'),
(67, 1, '', '7010250', 'Modern Ex. Co. Oman', '', 0, '7061'),
(68, 1, '', 'MGM', 'MoneyGram Int. Ex. ', '', 0, '7041'),
(69, 1, '', '7010252', 'Multinet  trust Ex. LLC UAE ', '', 0, '7067'),
(70, 1, '', 'M05', 'National Exchange Company Ita', '', 0, '7037'),
(71, 1, '', '7010229', 'National Exchange Italy', '', 0, '7037'),
(72, 1, '', 'M10', 'NBL Money Transfer ', '', 0, '7076'),
(73, 1, '', 'NBL', 'NBL Money TransferMalaysia', '', 0, '7059'),
(74, 1, '', '7010276', 'NBL Money TransferMalaysia', '', 0, '7059'),
(75, 1, '', '7010241', 'NCBJ Jeddah KSA (SNB)', '', 0, '7055'),
(76, 1, '', 'M15', 'NEC Money Transfer  U.K.', '', 0, '7087'),
(77, 1, '', '7010272', 'NEC Money Transfer UK', '', 0, '7087'),
(78, 1, '', '7010208', 'Oman Exchange Co Kuwait', '', 0, '7008'),
(79, 1, '', '7010215', 'Oman Int\'l Exchange Ltd. Oman ', '', 0, '7021'),
(80, 1, '', 'M14', 'PLACID NK CORPORATION', '', 0, '7065'),
(81, 1, '', 'PRB', 'Prabhu Group Inc.', '', 0, '7051'),
(82, 1, '', '7010238', 'Prabhu Group Inc. U S A', '', 0, '7051'),
(83, 1, '', '7010256', 'SIGUE Global Sercice ', '', 0, '7074'),
(84, 1, '', '7010248', 'Standard Express', '', 0, '7060'),
(85, 1, '', '7010274', 'Sunman Express ', '', 0, '7096'),
(86, 1, '', '7010266', 'The Saudi Investment Bank (SAIB)', '', 0, '7086'),
(87, 1, '', 'TFT', 'Trans Fast Remittance USA', '', 0, '7044'),
(88, 1, '', 'M03', 'Turbo Cash', '', 0, '7003'),
(89, 1, '', 'M16', 'U REMIT INTERNATIONAL CANADA', '', 0, '7088'),
(90, 1, '', '7010267', 'U REMIT INTERNATIONAL CANADA', '', 0, '7088'),
(91, 1, '', '7010225', 'U.A.E. Exchange LLC Abu Dhabi', '', 0, '7028'),
(92, 1, '', '7010257', 'U.A.E. Exchange Malaysia', '', 0, '7075'),
(93, 1, '', '7010213', 'UAE Exchange LLC ', '', 0, '7027'),
(94, 1, '', '7010216', 'Unimoni Exchange', '', 0, '7034'),
(95, 1, '', '7010268', 'Val You SDN BHD', '', 0, '7090'),
(96, 1, '', '7010224', 'Wall Street Exchange Abu Dhab', '', 0, '7033'),
(97, 1, '', 'M01', 'Western Union', '', 0, '7063'),
(98, 1, '', '7010219', 'Zenj Exchange Co. Bahrain', '', 0, '7003'),
(99, 1, '', 'M28', 'Global Money Exchange Oman', '', 0, '7089'),
(100, 1, '', 'M29', 'Gmoney Transact.co Ltd Korea ', '', 0, '7081'),
(101, 1, '', 'M30', 'U.A.E Ex WLL Kuait', '', 0, '7027'),
(102, 1, '', 'M31', 'Joyalukkas Exchange Oman', '', 0, '7078'),
(103, 1, '', 'M32', 'Joyalukkas EX Co kuwait', '', 0, '7085'),
(104, 1, '', 'M33', 'Joyalukkas EX Co UAE', '', 0, '7072'),
(105, 1, '', 'M08', 'SHA Global', '', 0, '7104'),
(106, 1, '', '7010297', 'NBl Maldives', '', 0, '7105'),
(107, 1, '', 'M34', 'NBL Maldives', '', 0, '7105'),
(108, 1, '', 'M35', 'SG Quick Pay', '', 0, '7107'),
(109, 1, '', '7010284', 'Hamdan ExchangeOman', '', 0, '7099'),
(110, 1, '', 'M37', 'Japan Remit Finance', '', 0, '7108'),
(111, 1, '', 'M38', 'Saudi National Bank (SNB)  Je', '', 0, '7055');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `ex_house_list`
--
ALTER TABLE `ex_house_list`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_is_api` (`is_api`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `ex_house_list`
--
ALTER TABLE `ex_house_list`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=112;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
