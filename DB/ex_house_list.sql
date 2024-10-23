-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Oct 23, 2024 at 12:06 PM
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
  `exchange_name` varchar(255) NOT NULL,
  `exchange_code` varchar(20) NOT NULL,
  `exchange_short_name` varchar(30) NOT NULL,
  `nrta_code` varchar(10) NOT NULL,
  `base_table_name` varchar(20) NOT NULL,
  `class_name` varchar(32) NOT NULL,
  `repository_name` varchar(32) NOT NULL,
  `is_settlement` tinyint(1) DEFAULT 0,
  `active_status` tinyint(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `ex_house_list`
--

INSERT INTO `ex_house_list` (`id`, `exchange_name`, `exchange_code`, `exchange_short_name`, `nrta_code`, `base_table_name`, `class_name`, `repository_name`, `is_settlement`, `active_status`) VALUES
(1, 'Al Muzaini Exchange Company K', '7010231', 'Al Muzaini', '7038', 'muzaini', 'MuzainiModel', 'muzainiModelRepository', 0, 1),
(2, 'Bahrain Exchange Co Kuwait', '7010209', 'BEC', '7012', 'bec', 'BecModel', 'becModelRepository', 0, 1),
(3, 'National Finance and Exch. Co.', '7010234', 'NAFEX', '7046', 'nafex', 'NafexEhMstModel', 'nafexModelRepository', 0, 1),
(4, 'Agrani Exchange House Singapore', '7010226', 'Singapore', '7025', 'singapore', 'AgexSingaporeModel', 'agexSingaporeModelRepository', 1, 1),
(5, 'Ezremit', '7010299', 'EZ Remit', '7102', 'ezremit', 'EzRemitModel', 'ezRemitModelRepository', 1, 1),
(6, 'Continental Exchange Solution( Ria)', '7010290', 'RIA', '7081', 'ria', 'RiaModel', 'riaModelRepository', 1, 1),
(7, 'API BEFTN', '111111', 'API BEFTN', '1000', 'api_beftn', '', '', 0, 1),
(8, 'API T24', '222222', 'API T24', '2000', 'api_t24', '', '', 1, 1),
(9, 'COC Paid', '333333', 'COC Paid', '3000', 'coc_paid', '', '', 1, 1),
(112, 'Aftab Currency Exchange Ltd.UK', '7010291', '', '7100', '', '', '', 0, 1),
(113, 'Al Ahalia Money Ex. ', '7010221', '', '7047', '', '', '', 0, 1),
(114, 'Al Ansari Ex. Co, Abu Dhabi, UAE', '7010239', 'Al Ansari Ex', '7053', 'alansari', 'AlansariModel', 'alansariModelRepository', 0, 1),
(115, 'Al Jadeed Exchange', '7010246', 'Al Jadeed', '7058', 'alzadeed', 'AlzadeedModel', 'alzadeedModelRepository', 0, 1),
(116, 'Al Mulla Int. Ex. Co. Kuwait ', '7010232', '', '7040', '', '', '', 0, 1),
(117, 'Al Rostamani Intl Ex. Co. Abu', '7010240', 'Al Rostamani', '7052', 'alrostamani', 'AlRostamaniModel', 'alRostamaniModelRepository', 0, 1),
(118, 'Al Zaman Ex. , Qatar', '7010245', 'Al Zaman', '7057', 'alzaman', 'AlZamanModel', 'alZamanModelRepository', 0, 1),
(119, 'ALAWANEH EX. JORDAN', '7010262', 'ALAWANEH EX.', '7082', 'alawneh', 'AlawnehModel', 'alawnehModelRepository', 0, 1),
(120, 'Al-Fardan Exchange  Co. Doha, ', '7010205', '', '7002', '', '', '', 0, 1),
(121, 'Al-Fardan Exchange, Abu Dhabi', '7010220', '', '7010', '', '', '', 0, 1),
(122, 'AL-Ghurair Ex. Dubai, UAE', '7010233', '', '7043', '', '', '', 0, 1),
(123, 'Al-Rajhi Banking & Investment ', '7119', '', '7009', '', '', '', 0, 1),
(124, 'Aman Ex- Kuwait', '7010263', 'Aman Ex', '7084', 'aman', 'AmanModel', 'amanModelRepository', 0, 1),
(125, 'Arab National Bank, Riyadh, K.', '7010203', 'Arab National Bank', '7030', 'anb', 'AnbModel', 'anbModelRepository', 0, 1),
(126, 'Bahrain Financing Co. Bahrain ', '7010243', '', '7013', '', '', '', 0, 1),
(127, 'Bank Al-Bilad, Riyadh, K.S.A', '7010204', '', '7031', '', '', '', 0, 1),
(128, 'Belhasa Global Ex', '7010279', 'Belhasa', '7098', 'belhashaglobal', 'BelhashaGlobalModel', 'belhashaGlobalModelRepository', 0, 1),
(129, 'Brac Saajjan Exchange', '7010278', '', '7091', '', '', '', 0, 1),
(130, 'Choice Money Ex. , USA', '7010251', '', '7066', '', '', '', 0, 1),
(131, 'City Intl Exchange Co, Kuwait', '7010211', '', '7016', '', '', '', 0, 1),
(132, 'City pay Malysia', '7010288', '', '7071', '', '', '', 0, 1),
(133, 'Dollarco Exchange Co. Kuwait', '7010254', '', '7019', '', '', '', 0, 1),
(134, 'Eastern Exchange Est. Doha, Qa', '7010206', 'Eastern Ex', '7000', 'eastern', 'EasternModel', 'easternModelRepository', 0, 1),
(135, 'Emirates India Int. Ex. , UAE', '7010244', '', '7056', '', '', '', 0, 1),
(136, 'Finshot Inc. South Korea', '7010301', '', '7110', '', '', '', 0, 1),
(137, 'Global Money Exchange,Oman', '7010269', '', '7089', '', '', '', 0, 1),
(138, 'Global Money Express, S K', '7010277', '', '7095', '', '', '', 0, 1),
(139, 'GMoney Transfer Co.Ltd.', '7010292', '', '7103', '', '', '', 0, 1),
(140, 'Gulf Overseas', '7010287', '', '7069', '', '', '', 0, 1),
(141, 'Hamdan Exchange', '7010284', '', '7099', '', '', '', 0, 1),
(142, 'Index Exchange Co. LLC', '7010223', 'Index Exchange', '7024', 'index', 'IndexModel', 'indexModelRepository', 0, 1),
(143, 'Instant Cash', '7010260', 'Instant Cash', '7080', 'instantcash', 'InstantCashModel', 'instantCashModelRepository', 0, 1),
(144, 'Japan Remit Finance, Japan', '7010298', '', '7108', '', '', '', 0, 1),
(145, 'Joyalukkas Ex Dubai,UAE', '7010255', '', '7072', '', '', '', 0, 1),
(146, 'Joyalukkas Ex Oman', '7010258', '', '7078', '', '', '', 0, 1),
(147, 'Joyalukkas Ex WLL,KUWAIT', '7010264', '', '7085', '', '', '', 0, 1),
(148, 'K & H Remittance Services Brun', '7010271', '', '7092', '', '', '', 0, 1),
(149, 'Kuwait Bahrain Intl Ex. Kuwai', '7010207', '', '7006', '', '', '', 0, 1),
(150, 'Lari Exchange  (Abu Dhabi, UAE', '7010242', '', '7054', '', '', '', 0, 1),
(151, 'Lulu Exchange Co', '7010237', '', '7050', '', '', '', 0, 1),
(152, 'MaxMoney', '7010295', '', '7094', '', '', '', 0, 1),
(153, 'Modern Ex. Co., Bahrain', '7010265', '', '7083', '', '', '', 0, 1),
(154, 'Modern Ex. Co., Oman', '7010250', '', '7061', '', '', '', 0, 1),
(155, 'Money Match', '7010302', '', '7109', '', '', '', 0, 1),
(156, 'Multinet  trust Ex. LLC, UAE ', '7010252', '', '7067', '', '', '', 0, 1),
(157, 'National Exchange Company, Italy', '7010229', 'NEC Italy', '7037', 'necitaly', 'NecItalyModel', 'necItalyModelRepository', 0, 1),
(158, 'NBL Money Transfer USA', '7010285', '', '7077', '', '', '', 0, 1),
(159, 'NBL Money Transfer, Malaysia', '7010276', 'NBL Malaysia', '7059', 'nblmalyasia', 'NblMalyasiaModel', 'nblMalyasiaModelRepository', 0, 1),
(160, 'NCBJ, Jeddah, KSA', '7010241', '', '7055', '', '', '', 0, 1),
(161, 'NEC Money Transfer, UK', '7010272', 'NEC UK', '7087', 'necuk', 'NecUkModel', 'necUkModelRepository', 0, 1),
(162, 'Oman Exchange Co, Kuwait', '7010208', '', '7008', '', '', '', 0, 1),
(163, 'Oman Intl Exchange Ltd. Oman', '7010215', '', '7021', '', '', '', 0, 1),
(164, 'Placid NK orp', '7010286', '', '7065', '', '', '', 0, 1),
(165, 'Prabhu Group Inc. U S A', '7010238', '', '7051', '', '', '', 0, 1),
(166, 'SG Quick Pay, PTE LTD, SNG', '7010296', '', '7107', '', '', '', 0, 1),
(167, 'SIGUE Global Sercice ', '7010256', 'Sigue Global', '7074', 'sigue', 'SigueModel', 'sigueModelRepository', 0, 1),
(168, 'Standard Express', '7010248', 'Standard Express', '7060', 'standard', 'StandardModel', 'standardModelRepository', 0, 1),
(169, 'Sunman Express 	', '7010274', '', '7096', '', '', '', 0, 1),
(170, 'Terra Payment Services Ltd. Ma', '7010293', '', '7101', '', '', '', 0, 1),
(171, 'The Saudi Investment Bank (SAI', '7010266', 'SAIB', '7086', 'saib', 'SaibModel', 'saibModelRepository', 0, 1),
(172, 'U REMIT INTERNATIONAL, CANADA', '7010267', '', '7088', '', '', '', 0, 1),
(173, 'U.A.E. Exchange LLC, Abu Dhabi', '7010225', '', '7028', '', '', '', 0, 1),
(174, 'U.A.E. Exchange, Malaysia', '7010257', '', '7075', '', '', '', 0, 1),
(175, 'UAE Exchange WLL,Kuwait', '7010213', '', '7027', '', '', '', 0, 1),
(176, 'Unimoni Exchange', '7010216', 'Unimoni Ex', '7034', 'unimoni', 'UnimoniModel', 'unimoniModelRepository', 0, 1),
(177, 'Val You SDN BHD', '7010268', '', '7090', '', '', '', 0, 1),
(178, 'Wall Street Exchange, Abu Dhab', '7010224', '', '7033', '', '', '', 0, 1),
(179, 'Zamzam Money Exchange', '7010303', '', '7111', '', '', '', 0, 1),
(180, 'Zenj Exchange Co. Bahrain', '7010219', '', '7003', '', '', '', 0, 1),
(183, 'FSIE Italy', '7010289', 'FSIE', '7093', 'fsie', 'FsieModel', 'fsieModelRepository', 1, 1);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `ex_house_list`
--
ALTER TABLE `ex_house_list`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_is_settlement` (`is_settlement`),
  ADD KEY `idx_active_status` (`active_status`),
  ADD KEY `idx_exchange_code` (`exchange_code`),
  ADD KEY `idx_nrta_code` (`nrta_code`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `ex_house_list`
--
ALTER TABLE `ex_house_list`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=184;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
