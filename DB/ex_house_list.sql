-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Nov 11, 2024 at 12:39 PM
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
  `exchange_code` varchar(20) NOT NULL,
  `exchange_name` varchar(255) NOT NULL,
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

INSERT INTO `ex_house_list` (`id`, `exchange_code`, `exchange_name`, `exchange_short_name`, `nrta_code`, `base_table_name`, `class_name`, `repository_name`, `is_settlement`, `active_status`) VALUES
(1, '7010231', 'Al Muzaini Exchange Company K', 'Al Muzaini', '7038', 'muzaini', 'MuzainiModel', 'muzainiModelRepository', 0, 1),
(2, '7010209', 'Bahrain Exchange Co Kuwait', 'BEC', '7012', 'bec', 'BecModel', 'becModelRepository', 0, 1),
(3, '7010234', 'National Finance and Exch. Co.', 'NAFEX', '7046', 'nafex', 'NafexEhMstModel', 'nafexModelRepository', 0, 1),
(4, '7010226', 'Agrani Exchange House Singapore', 'Singapore', '7025', 'singapore', 'AgexSingaporeModel', 'agexSingaporeModelRepository', 1, 1),
(5, '7010299', 'Ezremit', 'EZ Remit', '7102', 'ezremit', 'EzRemitModel', 'ezRemitModelRepository', 1, 1),
(6, '7010290', 'Continental Exchange Solution( Ria)', 'RIA', '7081', 'ria', 'RiaModel', 'riaModelRepository', 1, 1),
(7, '111111', 'API BEFTN', 'API BEFTN', '1000', 'api_beftn', '', '', 0, 1),
(8, '222222', 'API T24', 'API T24', '2000', 'api_t24', '', '', 1, 1),
(9, '333333', 'COC Paid', 'COC Paid', '3000', 'coc_paid', '', '', 1, 1),
(112, '7010291', 'Aftab Currency Exchange Ltd.UK', '', '7100', 'generic', 'GenericModel', 'genericModelRepository', 0, 1),
(113, '7010221', 'Al Ahalia Money Ex. ', '', '7047', 'generic', 'GenericModel', 'genericModelRepository', 0, 1),
(114, '7010239', 'Al Ansari Ex. Co, Abu Dhabi, UAE', 'Al Ansari Ex', '7053', 'alansari', 'AlansariModel', 'alansariModelRepository', 0, 1),
(115, '7010246', 'Al Jadeed Exchange', 'Al Jadeed', '7058', 'alzadeed', 'AlzadeedModel', 'alzadeedModelRepository', 0, 1),
(116, '7010232', 'Al Mulla Int. Ex. Co. Kuwait ', '', '7040', 'generic', 'GenericModel', 'genericModelRepository', 0, 1),
(117, '7010240', 'Al Rostamani Intl Ex. Co. Abu', 'Al Rostamani', '7052', 'alrostamani', 'AlRostamaniModel', 'alRostamaniModelRepository', 0, 1),
(118, '7010245', 'Al Zaman Ex. , Qatar', 'Al Zaman', '7057', 'alzaman', 'AlZamanModel', 'alZamanModelRepository', 0, 1),
(119, '7010262', 'ALAWANEH EX. JORDAN', 'ALAWANEH EX.', '7082', 'alawneh', 'AlawnehModel', 'alawnehModelRepository', 0, 1),
(120, '7010205', 'Al-Fardan Exchange  Co. Doha, ', '', '7002', '', '', '', 0, 1),
(121, '7010220', 'Al-Fardan Exchange, Abu Dhabi, UAE', '', '7010', '', '', '', 0, 1),
(122, '7010233', 'AL-Ghurair Ex. Dubai, UAE', '', '7043', '', '', '', 0, 1),
(123, '7119', 'Al-Rajhi Banking & Investment ', 'Al Raji', '7009', 'alraji', 'AlRajiModel', 'alRajiModelRepository', 0, 1),
(124, '7010263', 'Aman Ex- Kuwait', 'Aman Ex', '7084', 'aman', 'AmanModel', 'amanModelRepository', 0, 1),
(125, '7010203', 'Arab National Bank, Riyadh, K.', 'Arab National Bank', '7030', 'anb', 'AnbModel', 'anbModelRepository', 0, 1),
(126, '7010243', 'Bahrain Financing Co. Bahrain ', '', '7013', 'generic', 'GenericModel', 'genericModelRepository', 0, 1),
(127, '7010204', 'Bank Al-Bilad, Riyadh, K.S.A', 'Bank Al Bilad', '7031', 'albilad', 'AlBiladModel', 'alBiladModelRepository', 0, 1),
(128, '7010279', 'Belhasa Global Ex', 'Belhasa', '7098', 'belhashaglobal', 'BelhashaGlobalModel', 'belhashaGlobalModelRepository', 0, 1),
(129, '7010278', 'Brac Saajjan Exchange', '', '7091', 'generic', 'GenericModel', 'genericModelRepository', 0, 1),
(130, '7010251', 'Choice Money Ex. , USA', '', '7066', 'generic', 'GenericModel', 'genericModelRepository', 0, 1),
(131, '7010211', 'City Intl Exchange Co, Kuwait', '', '7016', '', '', '', 0, 1),
(132, '7010288', 'City pay Malysia', '', '7071', 'generic', 'GenericModel', 'genericModelRepository', 0, 1),
(133, '7010254', 'Dollarco Exchange Co. Kuwait', '', '7019', '', '', '', 0, 1),
(134, '7010206', 'Eastern Exchange Est. Doha, Qatar', 'Eastern Ex', '7000', 'eastern', 'EasternModel', 'easternModelRepository', 0, 1),
(135, '7010244', 'Emirates India Int. Ex. , UAE', '', '7056', 'generic', 'GenericModel', 'genericModelRepository', 0, 1),
(136, '7010301', 'Finshot Inc. South Korea', '', '7110', 'generic', 'GenericModel', 'genericModelRepository', 0, 1),
(137, '7010269', 'Global Money Exchange,Oman', '', '7089', 'generic', 'GenericModel', 'genericModelRepository', 0, 1),
(138, '7010277', 'Global Money Express, S K', '', '7095', 'generic', 'GenericModel', 'genericModelRepository', 0, 1),
(139, '7010292', 'GMoney Transfer Co.Ltd.', '', '7103', 'generic', 'GenericModel', 'genericModelRepository', 0, 1),
(140, '7010287', 'Gulf Overseas', '', '7069', 'generic', 'GenericModel', 'genericModelRepository', 0, 1),
(141, '7010284', 'Hamdan Exchange', '', '7099', 'generic', 'GenericModel', 'genericModelRepository', 0, 1),
(142, '7010223', 'Index Exchange Co. LLC', 'Index Exchange', '7024', 'index', 'IndexModel', 'indexModelRepository', 0, 1),
(143, '7010260', 'Instant Cash', 'Instant Cash', '7080', 'instantcash', 'InstantCashModel', 'instantCashModelRepository', 0, 1),
(144, '7010298', 'Japan Remit Finance, Japan', '', '7108', 'generic', 'GenericModel', 'genericModelRepository', 0, 1),
(145, '7010255', 'Joyalukkas Ex Dubai,UAE', '', '7072', 'generic', 'GenericModel', 'genericModelRepository', 0, 1),
(146, '7010258', 'Joyalukkas Ex Oman', '', '7078', 'generic', 'GenericModel', 'genericModelRepository', 0, 1),
(147, '7010264', 'Joyalukkas Ex WLL,KUWAIT', '', '7085', 'generic', 'GenericModel', 'genericModelRepository', 0, 1),
(148, '7010271', 'K & H Remittance Services Brunai', '', '7092', '', '', '', 0, 1),
(149, '7010207', 'Kuwait Bahrain Intl Ex. Kuwai', '', '7006', 'generic', 'GenericModel', 'genericModelRepository', 0, 1),
(150, '7010242', 'Lari Exchange  (Abu Dhabi, UAE', 'Lari Exchange', '7054', 'lari', 'LariModel', 'lariModelRepository', 0, 1),
(151, '7010237', 'Lulu Exchange Co', '', '7050', 'generic', 'GenericModel', 'genericModelRepository', 0, 1),
(152, '7010295', 'MaxMoney', '', '7094', 'generic', 'GenericModel', 'genericModelRepository', 0, 1),
(153, '7010265', 'Modern Ex. Co., Bahrain', '', '7083', 'generic', 'GenericModel', 'genericModelRepository', 0, 1),
(154, '7010250', 'Modern Ex. Co., Oman', '', '7061', 'generic', 'GenericModel', 'genericModelRepository', 0, 1),
(155, '7010302', 'Money Match', '', '7109', 'generic', 'GenericModel', 'genericModelRepository', 0, 1),
(156, '7010252', 'Multinet  trust Ex. LLC, UAE ', 'Multinet Trust', '7067', 'multinet', 'MultinetModel', 'multinetModelRepository', 0, 1),
(157, '7010229', 'National Exchange Company, Italy', 'NEC Italy', '7037', 'necitaly', 'NecItalyModel', 'necItalyModelRepository', 0, 1),
(158, '7010285', 'NBL Money Transfer USA', 'NBL USA', '7077', 'nblusa', 'NblUsaModel', 'nblUsaModelRepository', 0, 1),
(159, '7010276', 'NBL Money Transfer, Malaysia', 'NBL Malaysia', '7059', 'nblmalaysia', 'NblMalaysiaModel', 'nblMalaysiaModelRepository', 0, 1),
(160, '7010241', 'Saudi National Bank (SNB)', '', '7055', 'generic', 'GenericModel', 'genericModelRepository', 0, 1),
(161, '7010272', 'NEC Money Transfer, UK', 'NEC UK', '7087', 'necuk', 'NecUkModel', 'necUkModelRepository', 0, 1),
(162, '7010208', 'Oman Exchange Co, Kuwait', 'Oman Exchange Kuwait', '7008', 'omankuwait', 'OmanKuwaitModel', 'omanKuwaitModelRepository', 0, 1),
(163, '7010215', 'Oman Intl Exchange Ltd. Oman', 'Oman Exchange Oman', '7021', 'oman', 'OmanModel', 'omanModelRepository', 0, 1),
(164, '7010286', 'Placid NK orp', '', '7065', 'generic', 'GenericModel', 'genericModelRepository', 0, 1),
(165, '7010238', 'Prabhu Group Inc. U S A', 'Prabhu USA', '7051', 'prabhu', 'PrabhuModel', 'prabhuModelRepository', 0, 1),
(166, '7010296', 'SG Quick Pay, PTE LTD, SNG', '', '7107', 'generic', 'GenericModel', 'genericModelRepository', 0, 1),
(167, '7010256', 'SIGUE Global Sercice ', 'Sigue Global', '7074', 'sigue', 'SigueModel', 'sigueModelRepository', 0, 1),
(168, '7010248', 'Standard Express', 'Standard Express', '7060', 'standard', 'StandardModel', 'standardModelRepository', 0, 1),
(169, '7010274', 'Sunman Express 	', 'Sunman', '7096', 'sunman', 'SunmanModel', 'sunmanModelRepository', 0, 1),
(170, '7010293', 'Terra Payment Services Ltd. Ma', '', '7101', 'generic', 'GenericModel', 'genericModelRepository', 0, 1),
(171, '7010266', 'The Saudi Investment Bank (SAIB)', 'SAIB', '7086', 'saib', 'SaibModel', 'saibModelRepository', 0, 1),
(172, '7010267', 'U REMIT INTERNATIONAL, CANADA', 'U REMIT', '7088', 'uremit', 'UremitModel', 'uremitModelRepository', 0, 1),
(173, '7010225', 'U.A.E. Exchange LLC, Abu Dhabi', '', '7028', 'generic', 'GenericModel', 'genericModelRepository', 0, 1),
(174, '7010257', 'U.A.E. Exchange, Malaysia', '', '7075', 'generic', 'GenericModel', 'genericModelRepository', 0, 1),
(175, '7010213', 'UAE Exchange WLL,Kuwait', '', '7027', '', '', '', 0, 1),
(176, '7010216', 'Unimoni Exchange', 'Unimoni Ex', '7034', 'unimoni', 'UnimoniModel', 'unimoniModelRepository', 0, 1),
(177, '7010268', 'Val You SDN BHD', '', '7090', 'generic', 'GenericModel', 'genericModelRepository', 0, 1),
(178, '7010224', 'Wall Street Exchange, Abu Dhabi, UAE', '', '7033', '', '', '', 0, 0),
(179, '7010303', 'Zamzam Money Exchange', '', '7111', 'generic', 'GenericModel', 'genericModelRepository', 0, 1),
(180, '7010219', 'Zenj Exchange Co. Bahrain', '', '7003', '', '', '', 0, 1),
(183, '7010289', 'FSIE Italy', 'FSIE', '7093', 'fsie', 'FsieModel', 'fsieModelRepository', 0, 1),
(184, '7010228', 'Agrani Remittance House Sdn. Bhd. Malaysia', 'Agrani Ex. Malaysia', '7035', 'agranimalaysia', 'AgraniMalaysiaModel', 'agraniMalaysiaModelRepository', 0, 1),
(185, '7010304', 'Progoti Exchange Co.', 'Progoti Ex.', '7113', 'progoti', 'ProgotiModel', 'progotiModelRepository', 0, 1),
(186, '', '', '', '', '', '', '', 0, 0),
(187, '7010294', 'NBL Money Transfer, Maldives', 'NBL Maldives', '7105', 'nblmaldives', 'NblMaldivesModel', 'nblMaldivesModelRepository', 0, 1),
(188, '7010300', 'Merchantrade Asia Sdn Bhd', 'Merchantrade', '7049', 'merchantrade', 'MerchantradeModel', 'merchantradeModelRepository', 0, 1);

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
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=189;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
