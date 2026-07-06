-- phpMyAdmin SQL Dump
-- version 5.2.2
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Jul 06, 2026 at 04:58 AM
-- Server version: 8.0.46-0ubuntu0.24.04.3
-- PHP Version: 8.3.6

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `misc_frd_task`
--

-- --------------------------------------------------------

--
-- Table structure for table `ex_house_list`
--

CREATE TABLE `ex_house_list` (
  `id` int NOT NULL,
  `exchange_code` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `exchange_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `exchange_short_name` varchar(30) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `nrta_code` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `exchange_code_sc` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL,
  `country_code` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `country_name` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `base_table_name` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `class_name` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `repository_name` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `is_settlement` tinyint(1) DEFAULT '0',
  `active_status` tinyint(1) DEFAULT '0',
  `has_settlement_daily` tinyint(1) DEFAULT '0',
  `exchange_short_name_sc` varchar(30) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `bb_ex_code` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  `bb_ex_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;

--
-- Dumping data for table `ex_house_list`
--

INSERT INTO `ex_house_list` (`id`, `exchange_code`, `exchange_name`, `exchange_short_name`, `nrta_code`, `exchange_code_sc`, `country_code`, `country_name`, `base_table_name`, `class_name`, `repository_name`, `is_settlement`, `active_status`, `has_settlement_daily`, `exchange_short_name_sc`, `bb_ex_code`, `bb_ex_name`) VALUES
(1, '7010231', 'Al Muzaini Exchange Company Kuwait', 'Al Muzaini', '7038', NULL, '414', 'KUWAIT', 'muzaini', 'MuzainiModel', 'muzainiModelRepository', 0, 1, 0, '', '414003', 'AL MUZAINI EXCHANGE COMPANY, KUWAIT'),
(2, '7010209', 'Bahrain Exchange Co Kuwait', 'BEC', '7012', NULL, '414', 'KUWAIT', '', 'BecModel', 'becModelRepository', 0, 1, 0, '', '414005', 'BAHRAIN EXCHANGE CO, KUWAIT'),
(3, '7010234', 'National Finance and Exch. Co.', 'NAFEX', '7046', NULL, '48', 'BAHRAIN', 'nafex', 'NafexEhMstModel', 'nafexModelRepository', 0, 1, 0, '', '048008', 'NATIONAL FINANCING AND EXCHANGE CO. BAHRAIN '),
(4, '7010226', 'Agrani Exchange House Singapore', 'Singapore', '7025', 'rmo', '702', 'SINGAPORE', 'singapore', 'AgexSingaporeModel', 'agexSingaporeModelRepository', 1, 1, 1, 'Agrani Singapore', '702001', 'AGRANI EXCHANGE HOUSE PTE. LTD., SINGAPORE'),
(5, '7010299', 'IFast Global Bank LTD (Ezremit)', 'EZ Remit', '7102', 'm06', '48', 'BAHRAIN', 'ezremit', 'EzRemitModel', 'ezRemitModelRepository', 1, 1, 0, 'EZ Remit', '048004', 'BFC EZ REMIT'),
(6, '7010290', 'Continental Exchange Solution (Ria)', 'RIA', '7081', 'm13', '458', 'MALAYSIA', 'ria', 'RiaModel', 'riaModelRepository', 1, 1, 0, 'RIA', '840004', 'CONTINENTAL EX. SOLUTIONS, INC DBA RIA FINANCIAL SERIVICES'),
(7, '111111', 'API BEFTN', 'API BEFTN', '1000', NULL, '', '', 'api_beftn', 'ApiBeftnModel', 'apiBeftnModelRepository', 0, 1, 0, '', '', ''),
(8, '222222', 'API T24', 'API T24', '2000', NULL, '', '', 'api_t24', 'ApiT24Model', 'apiT24ModelRepository', 1, 1, 1, '', '', ''),
(9, '333333', 'COC Paid', 'COC Paid', '3000', NULL, '', '', 'coc_paid', 'CocPaidModel', 'cocPaidModelRepository', 1, 1, 1, '', '', ''),
(112, '7010291', 'ACE Money Transfer Ltd. (Aftab Currency)', '', '7100', 'm09', '826', 'UNITED KINGDOM (UK)', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0, 'Aftab', '826001', 'AFTAB CURRENCY EXCHANGE LTD'),
(113, '7010221', 'Al Ahalia Money Ex. ', 'Al Ahalia', '7047', NULL, '784', 'UNITED ARAB EMIRATES (UAE)', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0, '', '784003', 'AL AHALIA MONEY EXCHANGE BUREAU'),
(114, '7010239', 'Al Ansari Ex. Co, Abu Dhabi, UAE', 'Al Ansari Ex', '7053', 'm02', '784', 'UNITED ARAB EMIRATES (UAE)', '', 'AlansariModel', 'alansariModelRepository', 0, 1, 0, 'Al Ansari Ex', '784004', 'AL ANSARI EXCHANGE LLC'),
(115, '7010246', 'Al Jadeed Exchange. Oman', 'Al Jadeed', '7058', NULL, '784', 'UNITED ARAB EMIRATES (UAE)', 'alzadeed', 'AlzadeedModel', 'alzadeedModelRepository', 0, 1, 0, '', '512002', 'AL JADEED EXCHANGE LLC.'),
(116, '7010232', 'Al Mulla Int. Ex. Co. Kuwait ', '', '7040', 'm21', '414', 'KUWAIT', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0, '', '414002', 'AL MULLA INTERNATIONAL EXCHANGE CO. WLL'),
(117, '7010240', 'Al Rostamani Intl Ex. Co. Abu Dhabi, UAE', 'Al Rostamani', '7052', NULL, '784', 'UNITED ARAB EMIRATES (UAE)', 'alrostamani', 'AlRostamaniModel', 'alRostamaniModelRepository', 0, 1, 0, '', '784014', 'AL ROSTAMANI INT.EXCHNAGE'),
(118, '7010245', 'Al Zaman Ex. , Qatar', 'Al Zaman', '7057', NULL, '634', 'QATAR', 'alzaman', 'AlZamanModel', 'alZamanModelRepository', 0, 1, 0, '', '634003', 'AL ZAMAN EXCHANGE W.L.L'),
(119, '7010262', 'ALAWANEH EX. JORDAN', 'ALAWANEH EX.', '7082', NULL, '400', 'JORDAN', 'alawneh', 'AlawnehModel', 'alawnehModelRepository', 0, 1, 0, '', '400005', 'ALAWANEH EX. JORDAN'),
(120, '7010205', 'Al-Fardan Exchange Co. Doha,Qatar', 'Al Fardan Doha', '7002', NULL, '634', 'QATAR', 'alfardan_doha', 'AlFardanDohaModel', 'alFardanDohaModelRepository', 0, 1, 0, '', '634004', 'AL-FARDAN EXCHANGE CO. DOHA, QATAR'),
(121, '7010220', 'Al-Fardan Exchange, Abu Dhabi, UAE', 'Al Fardan Abu Dhabi', '7010', 'm39', '784', 'UNITED ARAB EMIRATES (UAE)', 'alfardan_abudhabi', 'AlFardanAbuDhabiModel', 'alFardanAbuDhabiModelRepository', 0, 1, 0, '', '784017', 'AL-FARDAN EXCHANGE, ABUDHABI'),
(122, '7010233', 'AL-Ghurair Ex. Dubai, UAE', '', '7043', NULL, '784', 'UNITED ARAB EMIRATES (UAE)', '', '', '', 0, 0, 0, '', '784018', 'AL-GHURAIR EX. DUBAI, UAE'),
(123, '7119', 'Al-Rajhi Banking & Investment ', 'Al Raji', '7009', 'm25', '682', 'SAUDI ARABIA', 'alraji', 'AlRajiModel', 'alRajiModelRepository', 0, 1, 0, '', '682003', 'AL RAJHI  BANK'),
(124, '7010263', 'Aman Ex- Kuwait', 'Aman Ex', '7084', NULL, '414', 'KUWAIT', 'aman', 'AmanModel', 'amanModelRepository', 0, 1, 0, '', '414004', 'AMAN EXCHANGE COMPANY W.L.L'),
(125, '7010203', 'Arab National Bank, Riyadh, K.S.A', 'Arab National Bank', '7030', NULL, '682', 'SAUDI ARABIA', 'anb', 'AnbModel', 'anbModelRepository', 0, 1, 0, '', '682007', 'ARAB NATIONAL BANK, RIYADH, K.S.A.'),
(126, '7010243', 'Bahrain Financing Co. Bahrain ', '', '7013', NULL, '48', 'BAHRAIN', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0, '', '048001', 'BAHRAIN FINANCING CO. BAHRAIN'),
(127, '7010204', 'Bank Al-Bilad, Riyadh, K.S.A', 'Bank Al Bilad', '7031', NULL, '682', 'SAUDI ARABIA', 'albilad', 'AlBiladModel', 'alBiladModelRepository', 0, 1, 0, '', '682008', 'BANK AL BILAD'),
(128, '7010279', 'Belhasa Global Ex', 'Belhasa', '7098', NULL, '784', 'UNITED ARAB EMIRATES (UAE)', 'belhashaglobal', 'BelhashaGlobalModel', 'belhashaGlobalModelRepository', 0, 1, 0, '', '784020', 'BELHASA GLOBAL EXCHANGE'),
(129, '7010278', 'Brac Saajjan Exchange', '', '7091', 'm24', '826', 'UNITED KINGDOM (UK)', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0, '', '826006', 'BRAC SAAJAN EXCHANGE CO.LTD.'),
(130, '7010251', 'Small World Inc, USA (Ex- Choice Money, USA)', '', '7066', 'm11', '840', 'UNITED STATES OF AMERICA (USA)', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0, '', '840003', 'CHOICE MONEY EX. , USA'),
(131, '7010211', 'City Intl Exchange Co, Kuwait', 'City Intl Exchange', '7016', NULL, '414', 'KUWAIT', 'city', 'CityModel', 'cityModelRepository', 0, 1, 0, '', '414007', 'CITY INT\'L EXCHANGE CO, KUWAIT'),
(132, '7010288', 'City pay Malaysia', '', '7071', 'm07', '458', 'MALAYSIA', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0, 'CBL Malaysia', '458003', 'CBL MONEY EXCHANGE, MALAYSIA'),
(133, '7010254', 'Dollarco Exchange Co. Kuwait', '', '7019', NULL, '414', 'KUWAIT', '', '', '', 0, 0, 0, '', '414009', 'DOLLARCO EXCHANGE CO. KUWAIT'),
(134, '7010206', 'Eastern Exchange Est. Doha, Qatar', 'Eastern Ex', '7000', 'm99', '634', 'QATAR', 'eastern', 'EasternModel', 'easternModelRepository', 0, 1, 0, '', '634011', 'EASTERN EXCHANGE EST. DOHA, QATAR'),
(135, '7010244', 'Emirates India Int. Ex. , UAE', '', '7056', NULL, '784', 'UNITED ARAB EMIRATES (UAE)', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0, '', '784026', 'EMIRATES INDIA INTERNATIONAL EX.'),
(136, '7010301', 'Finshot Inc. South Korea', '', '7110', NULL, '410', 'KOREA, REPUBLIC OF', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0, '', '997000', 'OTHERS'),
(137, '7010269', 'Global Money Exchange,Oman', '', '7089', 'm28', '512', 'OMAN', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0, '', '512006', 'GLOBAL MONEY EXCHANGE CO. LLC, OMAN'),
(138, '7010277', 'Global Money Express, South Korea', '', '7095', 'm20', '410', 'KOREA, REPUBLIC OF', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0, '', '410001', 'GLOBAL MONEY EXPRESS (GME) CO LTD, KOREA'),
(139, '7010292', 'GMoney Transfer Co.Ltd.', '', '7103', 'm29', '410', 'KOREA, REPUBLIC OF', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0, '', '410002', 'GMONEY TRANS CO., LTD.'),
(140, '7010287', 'Gulf Overseas', '', '7069', 'm04', '512', 'OMAN', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0, '', '512007', 'GULF EXCHANGE, OMAN'),
(141, '7010284', 'Hamdan Exchange', '', '7099', 'm36', '512', 'OMAN', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0, '', '512009', 'HAMDAN EXCHANGE,OMAN'),
(142, '7010223', 'Index Exchange Co. LLC', 'Index Exchange', '7024', NULL, '784', 'UNITED ARAB EMIRATES (UAE)', 'index', 'IndexModel', 'indexModelRepository', 0, 1, 0, '', '784033', 'INDEX EXCHANGE L.L.C'),
(143, '7010260', 'Instant Cash', 'Instant Cash', '7080', 'm12', '784', 'UNITED ARAB EMIRATES (UAE)', 'instantcash', 'InstantCashModel', 'instantCashModelRepository', 1, 1, 0, 'Instant Cash', '826017', 'INSTANT CASH, UK'),
(144, '7010298', 'Japan Remit Finance, Japan', '', '7108', 'm37', '392', 'JAPAN', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0, '', '392001', 'JAPAN REMIT FINANCE CO. LIMITED'),
(145, '7010255', 'Joyalukkas Ex Dubai,UAE', '', '7072', 'm33', '784', 'UNITED ARAB EMIRATES (UAE)', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0, '', '784039', 'JOYALUKKAS EXCHANGE, UAE'),
(146, '7010258', 'Joyalukkas Ex Oman', '', '7078', 'm31', '512', 'OMAN', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0, '', '512010', 'JOYALUKKAS EXCHANGE LLC, OMAN'),
(147, '7010264', 'Joyalukkas Ex WLL,KUWAIT', '', '7085', 'm32', '414', 'KUWAIT', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0, '', '414011', 'JOYALUKKAS EXCHANGE COMPANY,KUWAIT'),
(148, '7010271', 'K & H Remittance Services Brunai', 'K & H Brunai', '7092', 'm17', '96', 'BRUNEI DARUSSALAM', 'kandh', 'KandHModel', 'kandHModelRepository', 0, 1, 0, '', '096002', 'K & H REMITTANCE SERVICES, BRUNEI'),
(149, '7010207', 'Kuwait Bahrain Intl Ex. Kuwait', '', '7006', 'm26', '414', 'KUWAIT', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0, '', '414013', 'KUWAIT BAHRAIN INT\'L EX. CO.'),
(150, '7010242', 'Lari Exchange Abu Dhabi, UAE', 'Lari Exchange', '7054', NULL, '784', 'UNITED ARAB EMIRATES (UAE)', 'lari', 'LariModel', 'lariModelRepository', 0, 1, 0, '', '784040', 'LARI EXCHANGE ESTABLISHMENT, UAE'),
(151, '7010237', 'Lulu Exchange Co', '', '7050', 'm27', '512', 'OMAN', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0, '', '512012', 'LULU EXCHANGE COMPANY LLC, OMAN'),
(152, '7010295', 'Max Money Sdn Bhd', '', '7094', 'm19', '458', 'MALAYSIA', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0, '', '458009', 'MAX MONEY SDN. BHD., MALAYSIA'),
(153, '7010265', 'Modern Ex. Co., Bahrain', '', '7083', NULL, '48', 'BAHRAIN', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0, '', '048007', 'MODERN EX. CO., BAHRAIN'),
(154, '7010250', 'Modern Ex. Co., Oman', '', '7061', 'm23', '512', 'OMAN', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0, '', '512015', 'MODERN EX. CO., OMAN'),
(155, '7010302', 'Money Match', '', '7109', NULL, '458', 'MALAYSIA', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0, '', '997000', 'OTHERS'),
(156, '7010252', 'Multinet  trust Ex. LLC, UAE ', 'Multinet Trust', '7067', NULL, '784', 'UNITED ARAB EMIRATES (UAE)', 'multinet', 'MultinetModel', 'multinetModelRepository', 0, 1, 0, '', '784046', 'MULTINET TRUST EX. LLC, UAE '),
(157, '7010229', 'National Exchange Company, Italy', 'NEC Italy', '7037', 'm05', '380', 'ITALY', 'necitaly', 'NecItalyModel', 'necItalyModelRepository', 0, 1, 0, 'NEC Italy', '380003', 'NATIONAL EXCHANGE COMPANY, ITALY'),
(158, '7010285', 'NBL Money Transfer USA', 'NBL USA', '7077', NULL, '840', 'USA', 'nblusa', 'NblUsaModel', 'nblUsaModelRepository', 0, 0, 0, '', '840009', 'NBL MONEY TRANSFER INC.'),
(159, '7010276', 'NBL Money Transfer, Malaysia', 'NBL Malaysia', '7059', 'nbl', '458', 'MALAYSIA', 'nblmalaysia', 'NblMalaysiaModel', 'nblMalaysiaModelRepository', 0, 1, 0, '', '458013', 'NBL MONEY TRANSFER SDN BHD'),
(160, '7010241', 'Saudi National Bank (SNB)', '', '7055', 'm38', '682', 'SAUDI ARABIA', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0, '', '682013', 'NCBJ, JEDDAH, KSA'),
(161, '7010272', 'NEC Money Transfer, UK', 'NEC UK', '7087', 'm15', '826', 'UNITED KINGDOM (UK)', 'necuk', 'NecUkModel', 'necUkModelRepository', 0, 1, 0, 'NEC UK', '826031', 'NEC MONEY TRANSFER LIMITED, UK'),
(162, '7010208', 'Al Ansari Exchange Co. W.L.L, Kuwait', '', '7008', NULL, '414', 'KUWAIT', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0, '', '414019', 'OMAN EXCHANGE CO. LTD., KUWAIT'),
(163, '7010215', 'Oman Intl Exchange Ltd. Oman', 'Oman Exchange Oman', '7021', NULL, '512', 'OMAN', 'oman', 'OmanModel', 'omanModelRepository', 0, 1, 0, '', '512020', 'OMAN INT\'L EXCHANGE LTD. OMAN '),
(164, '7010286', 'Placid NK Corporation', '', '7065', 'm14', '826', 'UNITED KINGDOM (UK)', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0, 'Placid', '826034', 'PLACID NK CORPORATION'),
(165, '7010238', 'Prabhu Group Inc. U S A', 'Prabhu USA', '7051', 'prb', '840', 'UNITED STATES OF AMERICA (USA)', 'prabhu', 'PrabhuModel', 'prabhuModelRepository', 0, 1, 0, 'Prabhu', '840013', 'PRABHU GROUP INC.(PRABHU MONEY TRANSFER)'),
(166, '7010296', 'SG Quick Pay, PTE LTD, SNG', '', '7107', 'm35', '702', 'Singapore', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0, '', '997000', 'OTHERS'),
(167, '7010256', 'SIGUE Global Sercice ', 'Sigue Global', '7074', NULL, '826', 'UNITED KINGDOM (UK)', 'sigue', 'SigueModel', 'sigueModelRepository', 0, 1, 0, '', '826039', 'SIGUE GLOBAL SERVICES LIMITED'),
(168, '7010248', 'Standard Express', 'Standard Express', '7060', NULL, '840', 'UNITED STATES OF AMERICA (USA)', 'standard', 'StandardModel', 'standardModelRepository', 0, 1, 0, '', '840018', 'STANDARD EXPRESS CO. (USA)'),
(169, '7010274', 'Sunman Express 	', 'Sunman', '7096', NULL, '840', 'UNITED STATES OF AMERICA (USA)', 'sunman', 'SunmanModel', 'sunmanModelRepository', 0, 1, 0, '', '840019', 'SUNMAN GLOBAL EXPRESS CORP. '),
(170, '7010293', 'Terra Payment Services Ltd. Malaysia', '', '7101', NULL, '9997', 'OTHERS', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0, '', '480002', 'TERRA PAYMENT SERVICE'),
(171, '7010266', 'The Saudi Investment Bank (SAIB)', 'SAIB', '7086', NULL, '682', 'SAUDI ARABIA', 'saib', 'SaibModel', 'saibModelRepository', 0, 1, 0, '', '682016', 'THE SAUDI INVESTMENT BANK(SAIB)'),
(172, '7010267', 'U REMIT INTERNATIONAL, CANADA', 'U REMIT', '7088', 'm16', '124', 'CANADA', 'uremit', 'UremitModel', 'uremitModelRepository', 0, 1, 0, 'U Remit', '124009', 'U REMIT INTERNATIONAL CORPORATION, CANADA'),
(173, '7010225', 'U.A.E. Exchange LLC, Abu Dhabi', '', '7028', NULL, '784', 'UNITED ARAB EMIRATES (UAE)', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0, '', '784057', 'UAE EXCHANGE CENTRE, L.L.C. UAE'),
(174, '7010257', 'U.A.E. Exchange, Malaysia', '', '7075', NULL, '458', 'MALAYSIA', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0, '', '458018', 'UAE EXCHANGE MALAYSIA SDN BHD'),
(175, '7010213', 'UAE Exchange WLL,Kuwait', 'UAE Kuwait', '7027', 'm30', '414', 'KUWAIT', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0, '', '414023', 'UAE EXCHANGE CENTRE W.L.L. KUWAIT'),
(176, '7010216', 'Unimoni Exchange', 'Unimoni Ex', '7034', NULL, '512', 'OMAN', 'unimoni', 'UnimoniModel', 'unimoniModelRepository', 0, 1, 0, '', '512023', 'UNIMONI EXCHANGE LLC'),
(177, '7010268', 'Val You SDN BHD', '', '7090', NULL, '458', 'MALAYSIA', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0, '', '458019', 'VAL YOU SDN BHD'),
(178, '7010224', 'Wall Street Exchange, Abu Dhabi, UAE', '', '7033', NULL, '784', 'UNITED ARAB EMIRATES (UAE)', '', '', '', 0, 0, 0, '', '784059', 'WALL STREET EX. CENTRE L.L.C. UAE'),
(179, '7010303', 'Zamzam Money Exchange', '', '7111', NULL, '784', 'UNITED ARAB EMIRATES (UAE)', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0, '', '997000', 'OTHERS'),
(180, '7010219', 'Zenj Exchange Co. Bahrain', '', '7003', 'm03', '48', 'BAHRAIN', '', '', '', 0, 0, 0, '', '048012', 'ZENJ EXCHANGE CO. BAHRAIN'),
(183, '7010289', 'FSIE Italy', 'FSIE', '7093', 'm18', '380', 'ITALY', 'fsie', 'FsieModel', 'fsieModelRepository', 0, 1, 0, '', '380002', 'FIRST SECURITY ISLAMI EXCHANGE ITALY S.R.L.'),
(184, '7010228', 'Agrani Remittance House Sdn. Bhd. Malaysia', 'ARH Malaysia', '7035', 'inf', '458', 'MALAYSIA', 'agranimalaysia', 'ArhMalaysiaModel', 'arhMalaysiaModelRepository', 1, 1, 1, 'ARH Malaysia', '458001', 'AGRANI REMITTANCE HOUSE SDN. BHD.'),
(185, '7010304', 'Progoti Exchange Co.', 'Progoti Ex.', '7113', NULL, '784', 'UNITED ARAB EMIRATES (UAE)', 'progoti', 'ProgotiModel', 'progotiModelRepository', 0, 1, 0, '', '784052', 'PROGOTI EXCHANGE, UAE'),
(187, '7010297', 'NBL Money Transfer, Maldives', 'NBL Maldives', '7105', 'm34', '462', 'Maldives', 'nblmaldives', 'NblMaldivesModel', 'nblMaldivesModelRepository', 0, 1, 0, '', '462002', 'NBL MONEY TRANSFER (MALDIVES) PVT LTD'),
(188, '7010300', 'Merchantrade Asia Sdn Bhd', 'Merchantrade', '7049', 'mrc', '458', 'MALAYSIA', 'merchantrade', 'MerchantradeModel', 'merchantradeModelRepository', 0, 1, 0, 'Merchantrade', '458010', 'MERCHANTRADE ASIA SDN. BHD.'),
(189, '7010294', 'Worldwide W & E Services Ltd (Ex Shah Global)', 'Shah Global', '7104', 'm08', '826', 'UNITED KINGDOM (UK)', 'shah_global', 'ShahGlobalModel', 'shahGlobalModelRepository', 0, 1, 0, '', '826049', 'WORLDWIDE WEST 2 EAST (T/A SHAH GLOBAL)'),
(190, '444444', 'SWIFT Message Extractions', 'SWIFT', '4000', NULL, '', '', 'swift', 'SwiftModel', 'swiftModelRepository', 0, 1, 0, '', '', ''),
(191, '7010235', 'Xpress Money', '', '', NULL, '784', 'UNITED ARAB EMIRATES (UAE)', 'generic', 'GenericModel', 'genericModelRepository', 0, 0, 0, '', '784060', 'X PRESS MONEY (UAE EXCHANGE) LLC, ABU DHABI'),
(192, '7010305', 'Taptap Send UK Limited, UK', '', '7115', 'm40', '826', 'UNITED KINGDOM (UK)', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0, '', '826052', 'TAPTAP SEND UK LTD.'),
(193, '555555', 'NPSB', 'NPSB', '1000', NULL, '', '', 'npsb_mfs', 'NpsbMfsModel', 'npsbMfsRepository', 1, 1, 0, '', '', ''),
(194, '666666', 'BEFTN Return', 'Beftn Return', '1000', NULL, '', '', 'beftn_return', '', '', 0, 1, 0, '', '', ''),
(195, '7010306', 'MASTERCARD TRANSACTION SERVICES', '', '7044', 'tft', '840', 'UNITED STATES OF AMERICA (USA)', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0, 'Transfast', '840020', 'TRANS-FAST REMITTANCE LLC'),
(196, '7010307', 'Western Union', '', '7063', 'm01', '840', 'UNITED STATES OF AMERICA (USA)', '', '', '', 0, 1, 0, 'Western Union', '840023', 'WESTERN UNION MONEY TRANSFER SERVICES'),
(197, '7010308', 'MoneyGram Int. Exchange', '', '7041', 'mgm', '840', 'UNITED STATES OF AMERICA (USA)', '', '', '', 0, 1, 0, 'MoneyGram', '999003', 'MONEYGRAM PAYMENT SYSTEM INC.'),
(198, '7010309', 'WIZZ CROSS BORDER SERVICE LTD.', '', '7112', 'xpm', '784', 'UNITED ARAB EMIRATES (UAE)', '', '', '', 0, 1, 0, 'XPress Money', '784060', 'X PRESS MONEY (UAE EXCHANGE) LLC, ABU DHABI'),
(199, '7010310', 'NBL Money Transfer Payment Foundation SA, Greece', '', '7076', 'm10', '300', 'GREECE', '', '', '', 0, 1, 0, '', '300001', 'NBL MONEY TRANSFER PAYMENT FOUNDATION S.A'),
(200, '7010311', 'Hello Pasia (PTY) Ltd', '', '7097', 'm22', '710', 'SOUTH AFRICA', '', '', '', 0, 1, 0, 'Hello Paisa', '710001', 'HELLO PAISA (PTY) LIMITED'),
(201, '7010312', 'IFIC Money Transfer (UK) Limited', '', '7116', '', '826', 'UNITED KINGDOM (UK)', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0, '', '826016', 'IFIC MONEY TRANSFER (UK) LTD.'),
(202, '777777', 'API Spot Cash', '', '2000', NULL, '', '', '', '', '', 0, 1, 0, 'API Spot Cash', '', ''),
(203, '888888', 'NBL API Data', '', '2000', NULL, '', '', '', '', '', 0, 1, 0, 'NBL API ', '', ''),
(204, '7010247', 'IME Malaysia', '', '7042', 'ime', '', '', '', '', '', 0, 0, 0, '', '', ''),
(205, '999999', 'Q Remit Incentive', 'Q Remit Incentive', '2000', NULL, '', '', 'converted_data_online', '', '', 0, 1, 0, '', '', ''),
(206, '7010313', 'Horizon Remittance Limited', 'Horizon', '7119', '', '826', 'UNITED KINGDOM (UK)', 'generic', 'GenericModel', 'genericModelRepository', 1, 1, 0, '', '784032', 'HORIZON EXCHANGE CENTRE LLC, DUBAI'),
(207, '7010314', 'Simpaisa CA Limited., Canada', '', '7117', '', '124', 'CANADA', 'generic', 'GenericModel', 'genericModelRepository', 0, 1, 0, '', '997000', 'OTHERS');

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
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=208;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
