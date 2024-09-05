-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jul 15, 2024 at 10:07 AM
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
-- Table structure for table `ex_house_list_backup`
--

CREATE TABLE `ex_house_list_backup` (
  `row_id` int(11) NOT NULL,
  `active_status` bit(1) DEFAULT NULL,
  `exchange_code` varchar(255) NOT NULL,
  `exchange_name` varchar(255) NOT NULL,
  `exchange_short_name` varchar(255) NOT NULL,
  `nrta_code` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `ex_house_list_backup`
--

INSERT INTO `ex_house_list_backup` (`row_id`, `active_status`, `exchange_code`, `exchange_name`, `exchange_short_name`, `nrta_code`) VALUES
(1, b'1', '7119', 'al raji', '', '7009'),
(2, b'1', 'RMO', 'Agrani Exchange House Pte. Ltd', '', '7025'),
(3, b'1', '7010226', 'Agrani Exchange House Singapore', '', '7025'),
(4, b'1', '7010273', 'Agrani Exchange House Singapore', '', '7025'),
(5, b'1', 'INF', 'Agrani Remittance House Sdn B', '', '7035'),
(6, b'1', '7010221', 'Al Ahalia Money Ex. ', '', '7047'),
(7, b'1', 'M02', 'Al Ansari Ex. Co Abu Dhabi U', '', '7053'),
(8, b'1', '7010239', 'Al Ansari Ex. Co Abu Dhabi U', '', '7053'),
(9, b'1', '7010246', 'Al jadeed Exchange', '', '7058'),
(10, b'1', '7010232', 'Al Mulla Int. Ex. Co. Kuwait ', '', '7040'),
(11, b'1', 'M21', 'Al Mulla International Exchang', '', '7040'),
(12, b'1', '7010231', 'Al Muzaini Exchange Company K', 'Al Muzaini', '7038'),
(13, b'1', '7010240', 'Al Rostamani Int\'l Ex. Co. Abu', '', '7052'),
(14, b'1', '7010245', 'Al Zaman Ex.  Qatar', '', '7057'),
(15, b'1', '7010262', 'ALAWANEH EX. JORDAN', '', '7082'),
(16, b'1', '7010205', 'Al-Fardan Exchange  Co. Doha ', '', '7002'),
(17, b'1', '7010220', 'Al-Fardan Exchange AbuDhabi', '', '7010'),
(18, b'1', '7010233', 'AL-Ghurair Ex. Dubai UAE', '', '7043'),
(19, b'1', '7009', 'Al-Rajhi Banking & Investment ', '', '7009'),
(20, b'1', '7010263', 'Aman Ex- Kuwait', '', '7084'),
(21, b'1', '7010203', 'Arab National Bank Riyadh K.', '', '7030'),
(22, b'1', '7010209', 'Bahrain Exchange Co Kuwait', 'BEC', '7012'),
(23, b'1', '7010243', 'Bahrain Financing Co. Bahrain ', '', '7013'),
(24, b'1', '7010204', 'Bank Al-Bilad Riyadh K.S.A', '', '7031'),
(25, b'1', '7010279', 'Belhasa Global Ex', '', '7098'),
(26, b'1', 'M24', 'Brac Saajjan Exchange', '', '7091'),
(27, b'1', '7010270', 'Brac Saajjan Exchange', '', '7091'),
(28, b'1', '7010278', 'Brac Saajjan Exchange', '', '7091'),
(29, b'1', '7010251', 'Choice Money Ex.  USA (Small World)', '', '7066'),
(30, b'1', 'M07', 'City bank limitedMalaysia', '', '7071'),
(31, b'1', '7010211', 'City Int\'l Exchange Co Kuwait', '', '7016'),
(32, b'1', '7010290', 'Continental Exchange Solution( Ria)', 'RIA', '7081'),
(33, b'1', '7010254', 'Dollarco Exchange Co. Kuwait', '', '7019'),
(34, b'1', '7010206', 'Eastern Exchange Est. Doha Qa', '', '7000'),
(35, b'1', '7010244', 'Emirates India Int. Ex.  UAE', '', '7056'),
(36, b'1', '7010299', 'Ezremit', 'ezremit', '7013'),
(37, b'1', 'M18', 'First Security Islami Exchange', '', '7093'),
(38, b'1', '7010269', 'Global Money Exchange', '', '7089'),
(39, b'1', 'M20', 'Global Money Express S K', '', '7095'),
(40, b'1', '7010275', 'Global Money Express S K', '', '7095'),
(41, b'1', '7010277', 'Global Money Express S K', '', '7095'),
(42, b'1', 'M04', 'Gulf OverseasOman', '', '7069'),
(43, b'1', 'M36', 'Hamdan Exchange', '', '7099'),
(44, b'1', 'M22', 'Hello Pasia (PTY) Ltd', '', '7097'),
(45, b'1', '7010223', 'Index Exchange Co. LLC (Al Falah)', '', '7024'),
(46, b'1', '7010260', 'Instant Cash', '', '7080'),
(47, b'1', 'M12', 'Instant Cash FZEUAE', '', '7080'),
(48, b'1', '7010258', 'Joyalukkas EX. LLC', '', '7078'),
(49, b'1', '7010255', 'Joyalukkas Exchange ', '', '7072'),
(50, b'1', '7010264', 'Joyalukkas Exchange ', '', '7085'),
(51, b'1', 'M17', 'K & H Remittance Services Brun', '', '7092'),
(52, b'1', '7010271', 'K & H Remittance Services Brun', '', '7092'),
(53, b'1', 'M26', 'Kuwait Bahrain Int. Ex. Co', '', '7006'),
(54, b'1', '7010207', 'Kuwait Bahrain Int\'l Ex. Kuwai', '', '7006'),
(55, b'1', '7010242', 'Lari Exchange  (Abu Dhabi UAE', '', '7054'),
(56, b'1', '7010237', 'Lulu Exchange Co', '', '7050'),
(57, b'1', 'M27', 'Lulu Exchange Co. ', '', '7050'),
(58, b'1', 'MRC', 'Marchantrade Asia Sdn.Bhd. Mal', '', '7049'),
(59, b'1', 'M19', 'Max Money Sdn Bhd', '', '7094'),
(60, b'1', 'M25', 'Al-Rajhi Banking & Investment ', '', '7083'),
(61, b'1', '7010265', 'Modern Ex. Co. Bahrain', '', '7083'),
(62, b'1', 'M23', 'Modern Ex. Co. Oman', '', '7061'),
(63, b'1', '7010250', 'Modern Ex. Co. Oman', '', '7061'),
(64, b'1', 'MGM', 'MoneyGram Int. Ex. ', '', '7041'),
(65, b'1', '7010252', 'Multinet  trust Ex. LLC UAE ', '', '7067'),
(66, b'1', 'M05', 'National Exchange Company Ita', '', '7037'),
(67, b'1', '7010229', 'National Exchange Italy', '', '7037'),
(68, b'1', '7010234', 'National Finance and Exch. Co.', 'NAFEX', '7046'),
(69, b'1', 'M10', 'NBL Money Transfer ', '', '7076'),
(70, b'1', 'NBL', 'NBL Money TransferMalaysia', '', '7059'),
(71, b'1', '7010276', 'NBL Money TransferMalaysia', '', '7059'),
(72, b'1', '7010241', 'NCBJ Jeddah KSA (SNB)', '', '7055'),
(73, b'1', 'M15', 'NEC Money Transfer  U.K.', '', '7087'),
(74, b'1', '7010272', 'NEC Money Transfer UK', '', '7087'),
(75, b'1', '7010208', 'Oman Exchange Co Kuwait', '', '7008'),
(76, b'1', '7010215', 'Oman Int\'l Exchange Ltd. Oman ', '', '7021'),
(77, b'1', 'M14', 'PLACID NK CORPORATION', '', '7065'),
(78, b'1', 'PRB', 'Prabhu Group Inc.', '', '7051'),
(79, b'1', '7010238', 'Prabhu Group Inc. U S A', '', '7051'),
(80, b'1', '7010256', 'SIGUE Global Sercice ', '', '7074'),
(81, b'1', '7010248', 'Standard Express', '', '7060'),
(82, b'1', '7010274', 'Sunman Express ', '', '7096'),
(83, b'1', '7010266', 'The Saudi Investment Bank (SAIB)', '', '7086'),
(84, b'1', 'TFT', 'Trans Fast Remittance USA', '', '7044'),
(85, b'1', 'M03', 'Turbo Cash', '', '7003'),
(86, b'1', 'M16', 'U REMIT INTERNATIONAL CANADA', '', '7088'),
(87, b'1', '7010267', 'U REMIT INTERNATIONAL CANADA', '', '7088'),
(88, b'1', '7010225', 'U.A.E. Exchange LLC Abu Dhabi', '', '7028'),
(89, b'1', '7010257', 'U.A.E. Exchange Malaysia', '', '7075'),
(90, b'1', '7010213', 'UAE Exchange LLC ', '', '7027'),
(91, b'1', '7010216', 'Unimoni Exchange', '', '7034'),
(92, b'1', '7010268', 'Val You SDN BHD', '', '7090'),
(93, b'1', '7010224', 'Wall Street Exchange Abu Dhab', '', '7033'),
(94, b'1', 'M01', 'Western Union', '', '7063'),
(95, b'1', '7010219', 'Zenj Exchange Co. Bahrain', '', '7003'),
(96, b'1', 'M28', 'Global Money Exchange Oman', '', '7089'),
(97, b'1', 'M29', 'Gmoney Transact.co Ltd Korea ', '', '7081'),
(98, b'1', 'M30', 'U.A.E Ex WLL Kuait', '', '7027'),
(99, b'1', 'M31', 'Joyalukkas Exchange Oman', '', '7078'),
(100, b'1', 'M32', 'Joyalukkas EX Co kuwait', '', '7085'),
(101, b'1', 'M33', 'Joyalukkas EX Co UAE', '', '7072'),
(102, b'1', 'M08', 'SHA Global', '', '7104'),
(103, b'1', '7010297', 'NBl Maldives', '', '7105'),
(104, b'1', 'M34', 'NBL Maldives', '', '7105'),
(105, b'1', 'M35', 'SG Quick Pay', '', '7107'),
(106, b'1', '7010284', 'Hamdan ExchangeOman', '', '7099'),
(107, b'1', 'M37', 'Japan Remit Finance', '', '7108'),
(108, b'1', 'M38', 'Saudi National Bank (SNB)  Je', '', '7055');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `ex_house_list_backup`
--
ALTER TABLE `ex_house_list_backup`
  ADD PRIMARY KEY (`row_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `ex_house_list_backup`
--
ALTER TABLE `ex_house_list_backup`
  MODIFY `row_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=109;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
