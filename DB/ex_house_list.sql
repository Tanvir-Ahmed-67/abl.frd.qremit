-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: May 12, 2025 at 08:06 AM
-- Server version: 8.0.42-0ubuntu0.24.04.1
-- PHP Version: 8.3.6

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
  `id` int NOT NULL,
  `exchange_code` varchar(20) COLLATE utf8mb3_unicode_ci NOT NULL,
  `exchange_name` varchar(255) COLLATE utf8mb3_unicode_ci NOT NULL,
  `exchange_short_name` varchar(30) COLLATE utf8mb3_unicode_ci NOT NULL,
  `nrta_code` varchar(10) COLLATE utf8mb3_unicode_ci NOT NULL,
  `country_code` varchar(10) COLLATE utf8mb3_unicode_ci NOT NULL,
  `country_name` varchar(128) COLLATE utf8mb3_unicode_ci NOT NULL,
  `base_table_name` varchar(20) COLLATE utf8mb3_unicode_ci NOT NULL,
  `class_name` varchar(32) COLLATE utf8mb3_unicode_ci NOT NULL,
  `repository_name` varchar(32) COLLATE utf8mb3_unicode_ci NOT NULL,
  `is_settlement` tinyint(1) DEFAULT '0',
  `active_status` tinyint(1) DEFAULT '0',
  `has_settlement_daily` tinyint(1) DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;

--
-- Dumping data for table `ex_house_list`
--

INSERT INTO `ex_house_list` (`id`, `exchange_code`, `exchange_name`, `exchange_short_name`, `nrta_code`, `country_code`, `country_name`, `base_table_name`, `class_name`, `repository_name`, `is_settlement`, `active_status`, `has_settlement_daily`) VALUES
(1, '7010231', 'Al Muzaini Exchange Company Kuwait', 'Al Muzaini', '7038', '414', 'KUWAIT', 'muzaini', 'MuzainiModel', 'muzainiModelRepository', 0, 1, 0),
(2, '7010209', 'Bahrain Exchange Co Kuwait', 'BEC', '7012', '414', 'KUWAIT', 'bec', 'BecModel', 'becModelRepository', 0, 1, 0),
(3, '7010234', 'National Finance and Exch. Co.', 'NAFEX', '7046', '48', 'BAHRAIN', 'nafex', 'NafexEhMstModel', 'nafexModelRepository', 0, 1, 0),
(4, '7010226', 'Agrani Exchange House Singapore', 'Singapore', '7025', '702', 'SINGAPORE', 'singapore', 'AgexSingaporeModel', 'agexSingaporeModelRepository', 1, 1, 1),
(5, '7010299', 'IFast Global Bank LTD (Ezremit)', 'EZ Remit', '7102', '48', 'BAHRAIN', 'ezremit', 'EzRemitModel', 'ezRemitModelRepository', 1, 1, 0),
(6, '7010290', 'Continental Exchange Solution (Ria)', 'RIA', '7081', '458', 'MALAYSIA', 'ria', 'RiaModel', 'riaModelRepository', 1, 1, 0),
(7, '111111', 'API BEFTN', 'API BEFTN', '1000', '', '', 'api_beftn', 'ApiBeftnModel', 'apiBeftnModelRepository', 0, 1, 0),
(8, '222222', 'API T24', 'API T24', '2000', '', '', 'api_t24', 'ApiT24Model', 'apiT24ModelRepository', 1, 1, 1),
(9, '333333', 'COC Paid', 'COC Paid', '3000', '', '', 'coc_paid', 'CocPaidModel', 'cocPaidModelRepository', 1, 1, 1),
(112, '7010291', 'Aftab Currency Exchange Ltd.UK', '', '7100', '826', 'UNITED KINGDOM (UK)', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0),
(113, '7010221', 'Al Ahalia Money Ex. ', '', '7047', '784', 'UNITED ARAB EMIRATES (UAE)', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0),
(114, '7010239', 'Al Ansari Ex. Co, Abu Dhabi, UAE', 'Al Ansari Ex', '7053', '784', 'UNITED ARAB EMIRATES (UAE)', 'alansari', 'AlansariModel', 'alansariModelRepository', 0, 1, 0),
(115, '7010246', 'Al Jadeed Exchange. Oman', 'Al Jadeed', '7058', '784', 'UNITED ARAB EMIRATES (UAE)', 'alzadeed', 'AlzadeedModel', 'alzadeedModelRepository', 0, 1, 0),
(116, '7010232', 'Al Mulla Int. Ex. Co. Kuwait ', '', '7040', '414', 'KUWAIT', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0),
(117, '7010240', 'Al Rostamani Intl Ex. Co. Abu Dhabi, UAE', 'Al Rostamani', '7052', '784', 'UNITED ARAB EMIRATES (UAE)', 'alrostamani', 'AlRostamaniModel', 'alRostamaniModelRepository', 0, 1, 0),
(118, '7010245', 'Al Zaman Ex. , Qatar', 'Al Zaman', '7057', '634', 'QATAR', 'alzaman', 'AlZamanModel', 'alZamanModelRepository', 0, 1, 0),
(119, '7010262', 'ALAWANEH EX. JORDAN', 'ALAWANEH EX.', '7082', '400', 'JORDAN', 'alawneh', 'AlawnehModel', 'alawnehModelRepository', 0, 1, 0),
(120, '7010205', 'Al-Fardan Exchange Co. Doha,Qatar', 'Al Fardan Doha', '7002', '634', 'QATAR', 'alfardan_doha', 'AlFardanDohaModel', 'alFardanDohaModelRepository', 0, 1, 0),
(121, '7010220', 'Al-Fardan Exchange, Abu Dhabi, UAE', 'Al Fardan Abu Dhabi', '7010', '784', 'UNITED ARAB EMIRATES (UAE)', 'alfardan_abudhabi', 'AlFardanAbuDhabiModel', 'alFardanAbuDhabiModelRepository', 0, 1, 0),
(122, '7010233', 'AL-Ghurair Ex. Dubai, UAE', '', '7043', '784', 'UNITED ARAB EMIRATES (UAE)', '', '', '', 0, 0, 0),
(123, '7119', 'Al-Rajhi Banking & Investment ', 'Al Raji', '7009', '682', 'SAUDI ARABIA', 'alraji', 'AlRajiModel', 'alRajiModelRepository', 0, 1, 0),
(124, '7010263', 'Aman Ex- Kuwait', 'Aman Ex', '7084', '414', 'KUWAIT', 'aman', 'AmanModel', 'amanModelRepository', 0, 1, 0),
(125, '7010203', 'Arab National Bank, Riyadh, K.S.A', 'Arab National Bank', '7030', '682', 'SAUDI ARABIA', 'anb', 'AnbModel', 'anbModelRepository', 0, 1, 0),
(126, '7010243', 'Bahrain Financing Co. Bahrain ', '', '7013', '48', 'BAHRAIN', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0),
(127, '7010204', 'Bank Al-Bilad, Riyadh, K.S.A', 'Bank Al Bilad', '7031', '682', 'SAUDI ARABIA', 'albilad', 'AlBiladModel', 'alBiladModelRepository', 0, 1, 0),
(128, '7010279', 'Belhasa Global Ex', 'Belhasa', '7098', '784', 'UNITED ARAB EMIRATES (UAE)', 'belhashaglobal', 'BelhashaGlobalModel', 'belhashaGlobalModelRepository', 0, 1, 0),
(129, '7010278', 'Brac Saajjan Exchange', '', '7091', '826', 'UNITED KINGDOM (UK)', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0),
(130, '7010251', 'Choice Money Ex. , USA', '', '7066', '840', 'UNITED STATES OF AMERICA (USA)', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0),
(131, '7010211', 'City Intl Exchange Co, Kuwait', 'City Intl Exchange', '7016', '414', 'KUWAIT', 'city', 'CityModel', 'cityModelRepository', 0, 1, 0),
(132, '7010288', 'City pay Malysia', '', '7071', '458', 'MALAYSIA', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0),
(133, '7010254', 'Dollarco Exchange Co. Kuwait', '', '7019', '414', 'KUWAIT', '', '', '', 0, 0, 0),
(134, '7010206', 'Eastern Exchange Est. Doha, Qatar', 'Eastern Ex', '7000', '634', 'QATAR', 'eastern', 'EasternModel', 'easternModelRepository', 0, 1, 0),
(135, '7010244', 'Emirates India Int. Ex. , UAE', '', '7056', '784', 'UNITED ARAB EMIRATES (UAE)', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0),
(136, '7010301', 'Finshot Inc. South Korea', '', '7110', '410', 'KOREA, REPUBLIC OF', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0),
(137, '7010269', 'Global Money Exchange,Oman', '', '7089', '512', 'OMAN', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0),
(138, '7010277', 'Global Money Express, South Korea', '', '7095', '410', 'KOREA, REPUBLIC OF', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0),
(139, '7010292', 'GMoney Transfer Co.Ltd.', '', '7103', '410', 'KOREA, REPUBLIC OF', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0),
(140, '7010287', 'Gulf Overseas', '', '7069', '512', 'OMAN', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0),
(141, '7010284', 'Hamdan Exchange', '', '7099', '512', 'OMAN', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0),
(142, '7010223', 'Index Exchange Co. LLC', 'Index Exchange', '7024', '784', 'UNITED ARAB EMIRATES (UAE)', 'index', 'IndexModel', 'indexModelRepository', 0, 1, 0),
(143, '7010260', 'Instant Cash', 'Instant Cash', '7080', '784', 'UNITED ARAB EMIRATES (UAE)', 'instantcash', 'InstantCashModel', 'instantCashModelRepository', 0, 1, 0),
(144, '7010298', 'Japan Remit Finance, Japan', '', '7108', '392', 'JAPAN', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0),
(145, '7010255', 'Joyalukkas Ex Dubai,UAE', '', '7072', '784', 'UNITED ARAB EMIRATES (UAE)', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0),
(146, '7010258', 'Joyalukkas Ex Oman', '', '7078', '512', 'OMAN', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0),
(147, '7010264', 'Joyalukkas Ex WLL,KUWAIT', '', '7085', '414', 'KUWAIT', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0),
(148, '7010271', 'K & H Remittance Services Brunai', 'K & H Brunai', '7092', '96', 'BRUNEI DARUSSALAM', 'kandh', 'KandHModel', 'kandHModelRepository', 0, 1, 0),
(149, '7010207', 'Kuwait Bahrain Intl Ex. Kuwait', '', '7006', '414', 'KUWAIT', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0),
(150, '7010242', 'Lari Exchange \n Abu Dhabi, UAE', 'Lari Exchange', '7054', '784', 'UNITED ARAB EMIRATES (UAE)', 'lari', 'LariModel', 'lariModelRepository', 0, 1, 0),
(151, '7010237', 'Lulu Exchange Co', '', '7050', '512', 'OMAN', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0),
(152, '7010295', 'MaxMoney', '', '7094', '458', 'MALAYSIA', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0),
(153, '7010265', 'Modern Ex. Co., Bahrain', '', '7083', '48', 'BAHRAIN', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0),
(154, '7010250', 'Modern Ex. Co., Oman', '', '7061', '512', 'OMAN', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0),
(155, '7010302', 'Money Match', '', '7109', '458', 'MALAYSIA', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0),
(156, '7010252', 'Multinet  trust Ex. LLC, UAE ', 'Multinet Trust', '7067', '784', 'UNITED ARAB EMIRATES (UAE)', 'multinet', 'MultinetModel', 'multinetModelRepository', 0, 1, 0),
(157, '7010229', 'National Exchange Company, Italy', 'NEC Italy', '7037', '380', 'ITALY', 'necitaly', 'NecItalyModel', 'necItalyModelRepository', 0, 1, 0),
(158, '7010285', 'NBL Money Transfer USA', 'NBL USA', '7077', '840', 'USA', 'nblusa', 'NblUsaModel', 'nblUsaModelRepository', 0, 1, 0),
(159, '7010276', 'NBL Money Transfer, Malaysia', 'NBL Malaysia', '7059', '458', 'MALAYSIA', 'nblmalaysia', 'NblMalaysiaModel', 'nblMalaysiaModelRepository', 0, 1, 0),
(160, '7010241', 'Saudi National Bank (SNB)', '', '7055', '682', 'SAUDI ARABIA', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0),
(161, '7010272', 'NEC Money Transfer, UK', 'NEC UK', '7087', '826', 'UNITED KINGDOM (UK)', 'necuk', 'NecUkModel', 'necUkModelRepository', 0, 1, 0),
(162, '7010208', 'Oman Exchange Co, Kuwait', 'Oman Exchange Kuwait', '7008', '414', 'KUWAIT', 'omankuwait', 'OmanKuwaitModel', 'omanKuwaitModelRepository', 0, 1, 0),
(163, '7010215', 'Oman Intl Exchange Ltd. Oman', 'Oman Exchange Oman', '7021', '512', 'OMAN', 'oman', 'OmanModel', 'omanModelRepository', 0, 1, 0),
(164, '7010286', 'Placid NK orp', '', '7065', '9997', 'OTHERS', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0),
(165, '7010238', 'Prabhu Group Inc. U S A', 'Prabhu USA', '7051', '840', 'UNITED STATES OF AMERICA (USA)', 'prabhu', 'PrabhuModel', 'prabhuModelRepository', 0, 1, 0),
(166, '7010296', 'SG Quick Pay, PTE LTD, SNG', '', '7107', '702', 'Singapore', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0),
(167, '7010256', 'SIGUE Global Sercice ', 'Sigue Global', '7074', '826', 'UNITED KINGDOM (UK)', 'sigue', 'SigueModel', 'sigueModelRepository', 0, 1, 0),
(168, '7010248', 'Standard Express', 'Standard Express', '7060', '840', 'UNITED STATES OF AMERICA (USA)', 'standard', 'StandardModel', 'standardModelRepository', 0, 1, 0),
(169, '7010274', 'Sunman Express 	', 'Sunman', '7096', '840', 'UNITED STATES OF AMERICA (USA)', 'sunman', 'SunmanModel', 'sunmanModelRepository', 0, 1, 0),
(170, '7010293', 'Terra Payment Services Ltd. Malaysia', '', '7101', '9997', 'OTHERS', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0),
(171, '7010266', 'The Saudi Investment Bank (SAIB)', 'SAIB', '7086', '682', 'SAUDI ARABIA', 'saib', 'SaibModel', 'saibModelRepository', 0, 1, 0),
(172, '7010267', 'U REMIT INTERNATIONAL, CANADA', 'U REMIT', '7088', '124', 'CANADA', 'uremit', 'UremitModel', 'uremitModelRepository', 0, 1, 0),
(173, '7010225', 'U.A.E. Exchange LLC, Abu Dhabi', '', '7028', '784', 'UNITED ARAB EMIRATES (UAE)', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0),
(174, '7010257', 'U.A.E. Exchange, Malaysia', '', '7075', '458', 'MALAYSIA', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0),
(175, '7010213', 'UAE Exchange WLL,Kuwait', '', '7027', '414', 'KUWAIT', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0),
(176, '7010216', 'Unimoni Exchange', 'Unimoni Ex', '7034', '512', 'OMAN', 'unimoni', 'UnimoniModel', 'unimoniModelRepository', 0, 1, 0),
(177, '7010268', 'Val You SDN BHD', '', '7090', '458', 'MALAYSIA', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0),
(178, '7010224', 'Wall Street Exchange, Abu Dhabi, UAE', '', '7033', '784', 'UNITED ARAB EMIRATES (UAE)', '', '', '', 0, 0, 0),
(179, '7010303', 'Zamzam Money Exchange', '', '7111', '784', 'UNITED ARAB EMIRATES (UAE)', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0),
(180, '7010219', 'Zenj Exchange Co. Bahrain', '', '7003', '48', 'BAHRAIN', '', '', '', 0, 0, 0),
(183, '7010289', 'FSIE Italy', 'FSIE', '7093', '380', 'ITALY', 'fsie', 'FsieModel', 'fsieModelRepository', 0, 1, 0),
(184, '7010228', 'Agrani Remittance House Sdn. Bhd. Malaysia', 'Agrani Ex. Malaysia', '7035', '458', 'MALAYSIA', 'agranimalaysia', 'AgraniMalaysiaModel', 'agraniMalaysiaModelRepository', 0, 1, 0),
(185, '7010304', 'Progoti Exchange Co.', 'Progoti Ex.', '7113', '784', 'UNITED ARAB EMIRATES (UAE)', 'progoti', 'ProgotiModel', 'progotiModelRepository', 0, 1, 0),
(187, '7010297', 'NBL Money Transfer, Maldives', 'NBL Maldives', '7105', '462', 'Maldives', 'nblmaldives', 'NblMaldivesModel', 'nblMaldivesModelRepository', 0, 1, 0),
(188, '7010300', 'Merchantrade Asia Sdn Bhd', 'Merchantrade', '7049', '458', 'MALAYSIA', 'merchantrade', 'MerchantradeModel', 'merchantradeModelRepository', 0, 1, 0),
(189, '7010294', 'Shah Global', 'Shah Global', '7104', '826', 'UNITED KINGDOM (UK)', 'shah_global', 'ShahGlobalModel', 'shahGlobalModelRepository', 0, 1, 0),
(190, '444444', 'SWIFT Message Extractions', 'SWIFT', '4000', '', '', 'swift', 'SwiftModel', 'swiftModelRepository', 0, 1, 0),
(191, '7010235', 'Xpress Money', '', '7112', '784', 'UNITED ARAB EMIRATES (UAE)', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0),
(192, '7010305', 'Taptap Send UK Limited, UK', '', '7115', '826', 'UNITED KINGDOM (UK)', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0);

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
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=193;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
