-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Oct 23, 2024 at 11:05 AM
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
  `nrta_code` varchar(255) NOT NULL,
  `class_name` varchar(32) NOT NULL,
  `repository_name` varchar(32) NOT NULL,
  `is_settlement` tinyint(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `ex_house_list`
--

INSERT INTO `ex_house_list` (`id`, `active_status`, `base_table_name`, `exchange_code`, `exchange_name`, `exchange_short_name`, `nrta_code`, `class_name`, `repository_name`, `is_settlement`) VALUES
(1, 1, 'muzaini', '7010231', 'Al Muzaini Exchange Company K', 'Al Muzaini', '7038', 'MuzainiModel', 'muzainiModelRepository', 0),
(2, 1, 'bec', '7010209', 'Bahrain Exchange Co Kuwait', 'BEC', '7012', 'BecModel', 'becModelRepository', 0),
(3, 1, 'nafex', '7010234', 'National Finance and Exch. Co.', 'NAFEX', '7046', 'NafexEhMstModel', 'nafexModelRepository', 0),
(4, 1, 'singapore', '7010226', 'Agrani Exchange House Singapore', 'Singapore', '7025', 'AgexSingaporeModel', 'agexSingaporeModelRepository', 1),
(5, 1, 'ezremit', '7010299', 'Ezremit', 'EZ Remit', '7102', 'EzRemitModel', 'ezRemitModelRepository', 1),
(6, 1, 'ria', '7010290', 'Continental Exchange Solution( Ria)', 'RIA', '7081', 'RiaModel', 'riaModelRepository', 1),
(7, 1, 'api_beftn', '111111', 'API BEFTN', 'API BEFTN', '1000', '', '', 0),
(8, 1, 'api_t24', '222222', 'API T24', 'API T24', '2000', '', '', 1),
(9, 1, 'coc_paid', '333333', 'COC Paid', 'COC Paid', '3000', '', '', 1),
(112, 1, '', '7010291', 'Aftab Currency Exchange Ltd.UK', '', '7100', '', '', 0),
(113, 1, '', '7010221', 'Al Ahalia Money Ex. ', '', '7047', '', '', 0),
(114, 1, 'alansari', '7010239', 'Al Ansari Ex. Co, Abu Dhabi, UAE', 'Al Ansari Ex', '7053', 'AlansariModel', 'alansariModelRepository', 0),
(115, 1, 'alzadeed', '7010246', 'Al Jadeed Exchange', 'Al Jadeed', '7058', 'AlzadeedModel', 'alzadeedModelRepository', 0),
(116, 1, '', '7010232', 'Al Mulla Int. Ex. Co. Kuwait ', '', '7040', '', '', 0),
(117, 1, 'alrostamani', '7010240', 'Al Rostamani Intl Ex. Co. Abu', 'Al Rostamani', '7052', 'AlRostamaniModel', 'alRostamaniModelRepository', 0),
(118, 1, 'alzaman', '7010245', 'Al Zaman Ex. , Qatar', 'Al Zaman', '7057', 'AlZamanModel', 'alZamanModelRepository', 0),
(119, 1, 'alawneh', '7010262', 'ALAWANEH EX. JORDAN', 'ALAWANEH EX.', '7082', 'AlawnehModel', 'alawnehModelRepository', 0),
(120, 1, '', '7010205', 'Al-Fardan Exchange  Co. Doha, ', '', '7002', '', '', 0),
(121, 1, '', '7010220', 'Al-Fardan Exchange, Abu Dhabi', '', '7010', '', '', 0),
(122, 1, '', '7010233', 'AL-Ghurair Ex. Dubai, UAE', '', '7043', '', '', 0),
(123, 1, '', '7119', 'Al-Rajhi Banking & Investment ', '', '7009', '', '', 0),
(124, 1, 'aman', '7010263', 'Aman Ex- Kuwait', 'Aman Ex', '7084', 'AmanModel', 'amanModelRepository', 0),
(125, 1, 'anb', '7010203', 'Arab National Bank, Riyadh, K.', 'Arab National Bank', '7030', 'AnbModel', 'anbModelRepository', 0),
(126, 1, '', '7010243', 'Bahrain Financing Co. Bahrain ', '', '7013', '', '', 0),
(127, 1, '', '7010204', 'Bank Al-Bilad, Riyadh, K.S.A', '', '7031', '', '', 0),
(128, 1, 'belhashaglobal', '7010279', 'Belhasa Global Ex', 'Belhasa', '7098', 'BelhashaGlobalModel', 'belhashaGlobalModelRepository', 0),
(129, 1, '', '7010278', 'Brac Saajjan Exchange', '', '7091', '', '', 0),
(130, 1, '', '7010251', 'Choice Money Ex. , USA', '', '7066', '', '', 0),
(131, 1, '', '7010211', 'City Intl Exchange Co, Kuwait', '', '7016', '', '', 0),
(132, 1, '', '7010288', 'City pay Malysia', '', '7071', '', '', 0),
(133, 1, '', '7010254', 'Dollarco Exchange Co. Kuwait', '', '7019', '', '', 0),
(134, 1, 'eastern', '7010206', 'Eastern Exchange Est. Doha, Qa', 'Eastern Ex', '7000', 'EasternModel', 'easternModelRepository', 0),
(135, 1, '', '7010244', 'Emirates India Int. Ex. , UAE', '', '7056', '', '', 0),
(136, 1, '', '7010301', 'Finshot Inc. South Korea', '', '7110', '', '', 0),
(137, 1, '', '7010269', 'Global Money Exchange,Oman', '', '7089', '', '', 0),
(138, 1, '', '7010277', 'Global Money Express, S K', '', '7095', '', '', 0),
(139, 1, '', '7010292', 'GMoney Transfer Co.Ltd.', '', '7103', '', '', 0),
(140, 1, '', '7010287', 'Gulf Overseas', '', '7069', '', '', 0),
(141, 1, '', '7010284', 'Hamdan Exchange', '', '7099', '', '', 0),
(142, 1, '', '7010223', 'Index Exchange Co. LLC', '', '7024', '', '', 0),
(143, 1, 'instantcash', '7010260', 'Instant Cash', 'Instant Cash', '7080', 'InstantCashModel', 'instantCashModelRepository', 0),
(144, 1, '', '7010298', 'Japan Remit Finance, Japan', '', '7108', '', '', 0),
(145, 1, '', '7010255', 'Joyalukkas Ex Dubai,UAE', '', '7072', '', '', 0),
(146, 1, '', '7010258', 'Joyalukkas Ex Oman', '', '7078', '', '', 0),
(147, 1, '', '7010264', 'Joyalukkas Ex WLL,KUWAIT', '', '7085', '', '', 0),
(148, 1, '', '7010271', 'K & H Remittance Services Brun', '', '7092', '', '', 0),
(149, 1, '', '7010207', 'Kuwait Bahrain Intl Ex. Kuwai', '', '7006', '', '', 0),
(150, 1, '', '7010242', 'Lari Exchange  (Abu Dhabi, UAE', '', '7054', '', '', 0),
(151, 1, '', '7010237', 'Lulu Exchange Co', '', '7050', '', '', 0),
(152, 1, '', '7010295', 'MaxMoney', '', '7094', '', '', 0),
(153, 1, '', '7010265', 'Modern Ex. Co., Bahrain', '', '7083', '', '', 0),
(154, 1, '', '7010250', 'Modern Ex. Co., Oman', '', '7061', '', '', 0),
(155, 1, '', '7010302', 'Money Match', '', '7109', '', '', 0),
(156, 1, '', '7010252', 'Multinet  trust Ex. LLC, UAE ', '', '7067', '', '', 0),
(157, 1, 'necitaly', '7010229', 'National Exchange Company, Italy', 'NEC Italy', '7037', 'NecItalyModel', 'necItalyModelRepository', 0),
(158, 1, '', '7010285', 'NBL Money Transfer USA', '', '7077', '', '', 0),
(159, 1, 'nblmalyasia', '7010276', 'NBL Money Transfer, Malaysia', 'NBL Malaysia', '7059', 'NblMalyasiaModel', 'nblMalyasiaModelRepository', 0),
(160, 1, '', '7010241', 'NCBJ, Jeddah, KSA', '', '7055', '', '', 0),
(161, 1, 'necuk', '7010272', 'NEC Money Transfer, UK', 'NEC UK', '7087', 'NecUkModel', 'necUkModelRepository', 0),
(162, 1, '', '7010208', 'Oman Exchange Co, Kuwait', '', '7008', '', '', 0),
(163, 1, '', '7010215', 'Oman Intl Exchange Ltd. Oman', '', '7021', '', '', 0),
(164, 1, '', '7010286', 'Placid NK orp', '', '7065', '', '', 0),
(165, 1, '', '7010238', 'Prabhu Group Inc. U S A', '', '7051', '', '', 0),
(166, 1, '', '7010296', 'SG Quick Pay, PTE LTD, SNG', '', '7107', '', '', 0),
(167, 1, 'sigue', '7010256', 'SIGUE Global Sercice ', 'Sigue Global', '7074', 'SigueModel', 'sigueModelRepository', 0),
(168, 1, 'standard', '7010248', 'Standard Express', 'Standard Express', '7060', 'StandardModel', 'standardModelRepository', 0),
(169, 1, '', '7010274', 'Sunman Express 	', '', '7096', '', '', 0),
(170, 1, '', '7010293', 'Terra Payment Services Ltd. Ma', '', '7101', '', '', 0),
(171, 1, 'saib', '7010266', 'The Saudi Investment Bank (SAI', 'SAIB', '7086', 'SaibModel', 'saibModelRepository', 0),
(172, 1, '', '7010267', 'U REMIT INTERNATIONAL, CANADA', '', '7088', '', '', 0),
(173, 1, '', '7010225', 'U.A.E. Exchange LLC, Abu Dhabi', '', '7028', '', '', 0),
(174, 1, '', '7010257', 'U.A.E. Exchange, Malaysia', '', '7075', '', '', 0),
(175, 1, '', '7010213', 'UAE Exchange WLL,Kuwait', '', '7027', '', '', 0),
(176, 1, 'unimoni', '7010216', 'Unimoni Exchange', 'Unimoni Ex', '7034', 'UnimoniModel', 'unimoniModelRepository', 0),
(177, 1, '', '7010268', 'Val You SDN BHD', '', '7090', '', '', 0),
(178, 1, '', '7010224', 'Wall Street Exchange, Abu Dhab', '', '7033', '', '', 0),
(179, 1, '', '7010303', 'Zamzam Money Exchange', '', '7111', '', '', 0),
(180, 1, '', '7010219', 'Zenj Exchange Co. Bahrain', '', '7003', '', '', 0),
(183, 1, 'fsie', '7010289', 'FSIE Italy', 'FSIE', '7093', 'FsieModel', 'fsieModelRepository', 1);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `ex_house_list`
--
ALTER TABLE `ex_house_list`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_is_settlement` (`is_settlement`);

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
