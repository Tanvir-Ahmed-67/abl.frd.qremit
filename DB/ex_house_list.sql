-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Oct 02, 2024 at 11:12 AM
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
(5, 1, 'ezremit', '7010299', 'Ezremit', 'EZ Remit', 1, '7102'),
(6, 1, 'ria', '7010290', 'Continental Exchange Solution( Ria)', 'RIA', 1, '7081'),
(7, 1, 'api_beftn', '111111', 'API BEFTN', 'API BEFTN', 0, '1000'),
(8, 1, 'api_t24', '222222', 'API T24', 'API T24', 1, '2000'),
(9, 1, 'coc_paid', '333333', 'COC Paid', 'COC Paid', 1, '3000'),
(112, 1, '', '7010291', 'Aftab Currency Exchange Ltd.UK', '', 0, '7100'),
(113, 1, '', '7010221', 'Al Ahalia Money Ex. ', '', 0, '7047'),
(114, 1, '', '7010239', 'Al Ansari Ex. Co, Abu Dhabi, UAE', '', 0, '7053'),
(115, 1, '', '7010246', 'Al jadeed Exchange', '', 0, '7058'),
(116, 1, '', '7010232', 'Al Mulla Int. Ex. Co. Kuwait ', '', 0, '7040'),
(117, 1, '', '7010240', "Al Rostamani Int'l Ex. Co. Abu", '', 0, '7052'),
(118, 1, '', '7010245', 'Al Zaman Ex. , Qatar', '', 0, '7057'),
(119, 1, '', '7010262', 'ALAWANEH EX. JORDAN', '', 0, '7082'),
(120, 1, '', '7010205', 'Al-Fardan Exchange  Co. Doha, ', '', 0, '7002'),
(121, 1, '', '7010220', 'Al-Fardan Exchange, Abu Dhabi', '', 0, '7010'),
(122, 1, '', '7010233', 'AL-Ghurair Ex. Dubai, UAE', '', 0, '7043'),
(123, 1, '', '7119', 'Al-Rajhi Banking & Investment ', '', 0, '7009'),
(124, 1, '', '7010263', 'Aman Ex- Kuwait', '', 0, '7084'),
(125, 1, '', '7010203', 'Arab National Bank, Riyadh, K.', '', 0, '7030'),
(126, 1, '', '7010243', 'Bahrain Financing Co. Bahrain ', '', 0, '7013'),
(127, 1, '', '7010204', 'Bank Al-Bilad, Riyadh, K.S.A', '', 0, '7031'),
(128, 1, '', '7010279', 'Belhasa Global Ex', '', 0, '7098'),
(129, 1, '', '7010278', 'Brac Saajjan Exchange', '', 0, '7091'),
(130, 1, '', '7010251', 'Choice Money Ex. , USA', '', 0, '7066'),
(131, 1, '', '7010211', "City Int'l Exchange Co, Kuwait", '', 0, '7016'),
(132, 1, '', '7010288', 'City pay Malysia', '', 0, '7071'),
(133, 1, '', '7010254', 'Dollarco Exchange Co. Kuwait', '', 0, '7019'),
(134, 1, '', '7010206', 'Eastern Exchange Est. Doha, Qa', '', 0, '7000'),
(135, 1, '', '7010244', 'Emirates India Int. Ex. , UAE', '', 0, '7056'),
(136, 1, '', '7010301', 'Finshot Inc. South Korea', '', 0, '7110'),
(137, 1, '', '7010269', 'Global Money Exchange,Oman', '', 0, '7089'),
(138, 1, '', '7010277', 'Global Money Express, S K', '', 0, '7095'),
(139, 1, '', '7010292', 'GMoney Transfer Co.Ltd.', '', 0, '7103'),
(140, 1, '', '7010287', 'Gulf Overseas', '', 0, '7069'),
(141, 1, '', '7010284', 'Hamdan Exchange', '', 0, '7099'),
(142, 1, '', '7010223', 'Index Exchange Co. LLC', '', 0, '7024'),
(143, 1, '', '7010260', 'Instant Cash', '', 0, '7080'),
(144, 1, '', '7010298', 'Japan Remit Finance, Japan', '', 0, '7108'),
(145, 1, '', '7010255', 'Joyalukkas Ex Dubai,UAE', '', 0, '7072'),
(146, 1, '', '7010258', 'Joyalukkas Ex Oman', '', 0, '7078'),
(147, 1, '', '7010264', 'Joyalukkas Ex WLL,KUWAIT', '', 0, '7085'),
(148, 1, '', '7010271', 'K & H Remittance Services Brun', '', 0, '7092'),
(149, 1, '', '7010207', "Kuwait Bahrain Int'l Ex. Kuwai", '', 0, '7006'),
(150, 1, '', '7010242', 'Lari Exchange  (Abu Dhabi, UAE', '', 0, '7054'),
(151, 1, '', '7010237', 'Lulu Exchange Co', '', 0, '7050'),
(152, 1, '', '7010295', 'MaxMoney', '', 0, '7094'),
(153, 1, '', '7010265', 'Modern Ex. Co., Bahrain', '', 0, '7083'),
(154, 1, '', '7010250', 'Modern Ex. Co., Oman', '', 0, '7061'),
(155, 1, '', '7010302', 'Money Match', '', 0, '7109'),
(156, 1, '', '7010252', 'Multinet  trust Ex. LLC, UAE ', '', 0, '7067'),
(157, 1, '', '7010229', 'National Exchange, Italy', '', 0, '7037'),
(158, 1, '', '7010285', 'NBL Money Transfer USA', '', 0, '7077'),
(159, 1, '', '7010276', 'NBL Money Transfer,Malaysia', '', 0, '7059'),
(160, 1, '', '7010241', 'NCBJ, Jeddah, KSA', '', 0, '7055'),
(161, 1, '', '7010272', 'NEC Money Transfer, UK', '', 0, '7087'),
(162, 1, '', '7010208', 'Oman Exchange Co, Kuwait', '', 0, '7008'),
(163, 1, '', '7010215', "Oman Int'l Exchange Ltd. Oman", '', 0, '7021'),
(164, 1, '', '7010286', 'Placid NK orp', '', 0, '7065'),
(165, 1, '', '7010238', 'Prabhu Group Inc. U S A', '', 0, '7051'),
(166, 1, '', '7010296', 'SG Quick Pay, PTE LTD, SNG', '', 0, '7107'),
(167, 1, '', '7010256', 'SIGUE Global Sercice ', '', 0, '7074'),
(168, 1, '', '7010248', 'Standard Express', '', 0, '7060'),
(169, 1, '', '7010274', 'Sunman Express 	', '', 0, '7096'),
(170, 1, '', '7010293', 'Terra Payment Services Ltd. Ma', '', 0, '7101'),
(171, 1, '', '7010266', 'The Saudi Investment Bank (SAI', '', 0, '7086'),
(172, 1, '', '7010267', 'U REMIT INTERNATIONAL, CANADA', '', 0, '7088'),
(173, 1, '', '7010225', 'U.A.E. Exchange LLC, Abu Dhabi', '', 0, '7028'),
(174, 1, '', '7010257', 'U.A.E. Exchange, Malaysia', '', 0, '7075'),
(175, 1, '', '7010213', 'UAE Exchange WLL,Kuwait', '', 0, '7027'),
(176, 1, '', '7010216', 'Unimoni Exchange', '', 0, '7034'),
(177, 1, '', '7010268', 'Val You SDN BHD', '', 0, '7090'),
(178, 1, '', '7010224', 'Wall Street Exchange, Abu Dhab', '', 0, '7033'),
(179, 1, '', '7010303', 'Zamzam Money Exchange', '', 0, '7111'),
(180, 1, '', '7010219', 'Zenj Exchange Co. Bahrain', '', 0, '7003');

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
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=181;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
