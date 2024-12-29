-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Dec 29, 2024 at 08:00 AM
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
-- Table structure for table `analytics_abl_current_year_target_achievement`
--

CREATE TABLE `analytics_abl_current_year_target_achievement` (
  `id` int(255) NOT NULL,
  `month_name` varchar(255) DEFAULT NULL,
  `target` varchar(255) NOT NULL,
  `achievement` varchar(255) NOT NULL,
  `percentage` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

--
-- Dumping data for table `analytics_abl_current_year_target_achievement`
--

INSERT INTO `analytics_abl_current_year_target_achievement` (`id`, `month_name`, `target`, `achievement`, `percentage`) VALUES
(1, 'Jan', '1375', '735.02', '53.46'),
(2, ' Feb', '2750', '1887.11', '68.62'),
(3, 'Mar', '4125', '2467.54', '59.82'),
(4, 'Apr', '5500', '2900.79', '52.74'),
(5, 'May', '6875', '3614.59', '52.58'),
(6, 'June', '8250', '5854.87', '70.97'),
(7, 'July', '9625', '6489.19', '67.42'),
(8, 'Aug', '11000', '7631.04', '69.37'),
(9, 'Sep', '12375', '11384.70', '92.00'),
(10, 'Oct', '13750', '14296.63', '103.98'),
(11, 'Nov', '15125', '17766.70', '117.47'),
(12, 'Dec', '16500', '0.00', '0');

-- --------------------------------------------------------

--
-- Table structure for table `analytics_abl_growth`
--

CREATE TABLE `analytics_abl_growth` (
  `id` int(255) NOT NULL,
  `month_name` varchar(255) NOT NULL,
  `year` int(255) NOT NULL,
  `national_amount` varchar(255) NOT NULL,
  `abl_amount` varchar(255) NOT NULL,
  `abl_share` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

--
-- Dumping data for table `analytics_abl_growth`
--

INSERT INTO `analytics_abl_growth` (`id`, `month_name`, `year`, `national_amount`, `abl_amount`, `abl_share`) VALUES
(1, 'January', 2008, '710.74', '65.62', '9.23%'),
(2, 'February', 2008, '689.26', '61.26', '8.89%'),
(3, 'March', 2008, '808.72', '65.12', '8.05%'),
(4, 'April', 2008, '781.71', '72.05', '9.22%'),
(5, 'May', 2008, '730.26', '64.38', '8.82%'),
(6, 'June', 2008, '753.58', '69.12', '9.17%'),
(7, 'July', 2008, '820.71', '67.21', '8.19%'),
(8, 'August', 2008, '721.92', '59.06', '8.18%'),
(9, 'September', 2008, '794.18', '65.78', '8.28%'),
(10, 'October', 2008, '648.51', '61.26', '9.45%'),
(11, 'November', 2008, '761.38', '60.77', '7.98%'),
(12, 'December', 2008, '758.03', '58.3', '7.69%'),
(13, 'January', 2009, '865.25', '60.55', '7.00%'),
(14, 'February', 2009, '784.47', '63.45', '8.09%'),
(15, 'March', 2009, '881.31', '68.25', '7.74%'),
(16, 'April', 2009, '857.03', '68.04', '7.94%'),
(17, 'May', 2009, '890.05', '60.49', '6.80%'),
(18, 'June', 2009, '911.62', '68.31', '7.49%'),
(19, 'July', 2009, '886.4', '67.76', '7.64%'),
(20, 'August', 2009, '937.91', '64.12', '6.84%'),
(21, 'September', 2009, '887.92', '68.39', '7.70%'),
(22, 'October', 2009, '911.2', '76.45', '8.39%'),
(23, 'November', 2009, '1053.54', '78.27', '7.43%'),
(24, 'December', 2009, '876.33', '77.92', '8.89%'),
(25, 'January', 2010, '950.92', '77.32', '8.13%'),
(26, 'February', 2010, '844.07', '74.58', '8.84%'),
(27, 'March', 2010, '941.31', '82.61', '8.78%'),
(28, 'April', 2010, '921.24', '90.47', '9.82%'),
(29, 'May', 2010, '885.12', '77.87', '8.80%'),
(30, 'June', 2010, '726.8', '67.53', '9.29%'),
(31, 'July', 2010, '847.64', '77.4', '9.13%'),
(32, 'August', 2010, '957.93', '87.28', '9.11%'),
(33, 'September', 2010, '838.17', '75.46', '9.00%'),
(34, 'October', 2010, '917.23', '82.74', '9.02%'),
(35, 'November', 2010, '945.99', '93.26', '9.86%'),
(36, 'December', 2010, '963.53', '93.48', '9.70%'),
(37, 'January', 2011, '960.09', '88.49', '9.22%'),
(38, 'February', 2011, '414.12', '44.87', '10.84%'),
(39, 'March', 2011, '974.46', '93.96', '9.64%'),
(40, 'April', 2011, '1090.49', '105.69', '9.69%'),
(41, 'May', 2011, '976.14', '93.8', '9.61%'),
(42, 'June', 2011, '1038.13', '99.57', '9.59%'),
(43, 'July', 2011, '1028.14', '96.28', '9.36%'),
(44, 'August', 2011, '1078.15', '108.14', '10.03%'),
(45, 'September', 2011, '843.32', '84.99', '10.08%'),
(46, 'October', 2011, '653.29', '73.42', '11.24%'),
(47, 'November', 2011, '908.79', '79.93', '8.80%'),
(48, 'December', 2011, '1144.38', '109.99', '9.61%'),
(49, 'January', 2012, '1215.8', '112.07', '9.22%'),
(50, 'February', 2012, '1130.9', '114.02', '10.08%'),
(51, 'March', 2012, '1107.49', '106.83', '9.65%'),
(52, 'April', 2012, '1151.17', '113.35', '9.85%'),
(53, 'May', 2012, '1151.17', '113.35', '9.85%'),
(54, 'June', 2012, '1073.48', '107.54', '10.02%'),
(55, 'July', 2012, '1193.77', '115.81', '9.70%'),
(56, 'August', 2012, '1167.84', '117', '10.02%'),
(57, 'September', 2012, '1171.9', '111.59', '9.52%'),
(58, 'October', 2012, '1453.6', '148.44', '10.21%'),
(59, 'November', 2012, '1102.15', '118.62', '10.76%'),
(60, 'December', 2012, '1282.47', '147.71', '11.52%'),
(61, 'January', 2013, '1314.74', '156.04', '11.87%'),
(62, 'February', 2013, '1155.63', '140.01', '12.12%'),
(63, 'March', 2013, '1224.04', '149.05', '12.18%'),
(64, 'April', 2013, '1182.33', '147.53', '12.48%'),
(65, 'May', 2013, '1079.39', '126.91', '11.76%'),
(66, 'June', 2013, '1057.63', '128.71', '12.17%'),
(67, 'July', 2013, '1230.46', '144.5', '11.74%'),
(68, 'August', 2013, '1008.2', '116.69', '11.57%'),
(69, 'September', 2013, '1026.09', '114.74', '11.18%'),
(70, 'October', 2013, '1230.68', '136.02', '11.05%'),
(71, 'November', 2013, '1051.1', '125.46', '11.94%'),
(72, 'December', 2013, '1210.21', '134.64', '11.13%'),
(73, 'January', 2014, '750.79', '144.61', '19.26%'),
(74, 'February', 2014, '1164.03', '128.67', '11.05%'),
(75, 'March', 2014, '1273.32', '139.02', '10.92%'),
(76, 'April', 2014, '1232.41', '136.45', '11.07%'),
(77, 'May', 2014, '1202.18', '128.28', '10.67%'),
(78, 'June', 2014, '1286.37', '149.53', '11.62%'),
(79, 'July', 2014, '1482.39', '161.52', '10.90%'),
(80, 'August', 2014, '1160.06', '132.71', '11.44%'),
(81, 'September', 2014, '1319.34', '157.68', '11.95%'),
(82, 'October', 2014, '1010.72', '119', '11.77%'),
(83, 'November', 2014, '1168.85', '136.19', '11.65%'),
(84, 'December', 2014, '1258.16', '136.84', '10.88%'),
(85, 'January', 2015, '1243.25', '140.58', '11.31%'),
(86, 'February', 2015, '1189.6', '138.71', '11.66%'),
(87, 'March', 2015, '1338.31', '156.31', '11.68%'),
(88, 'April', 2015, '1297.49', '148.94', '11.48%'),
(89, 'May', 2015, '1321.77', '148.03', '11.20%'),
(90, 'June', 2015, '1439.34', '164.44', '11.42%'),
(91, 'July', 2015, '1389.56', '148.89', '10.71%'),
(92, 'August', 2015, '1195.02', '127.42', '10.66%'),
(93, 'September', 2015, '1349.06', '144.83', '10.74%'),
(94, 'October', 2015, '1098.46', '117.82', '10.73%'),
(95, 'November', 2015, '1142.49', '120.31', '10.53%'),
(96, 'December', 2015, '1312.6', '139.1', '10.60%'),
(97, 'January', 2016, '1150.64', '125.1', '10.87%'),
(98, 'February', 2016, '1136.26', '128.24', '11.29%'),
(99, 'March', 2016, '1285.59', '147.22', '11.45%'),
(100, 'April', 2016, '1191.15', '131.39', '11.03%'),
(101, 'May', 2016, '1214.48', '138.88', '11.44%'),
(102, 'June', 2016, '1465.87', '163.75', '11.17%'),
(103, 'July', 2016, '1005.5', '118.68', '11.80%'),
(104, 'August', 2016, '1183.61', '137.23', '11.59%'),
(105, 'September', 2016, '1056.64', '118.62', '11.23%'),
(106, 'October', 2016, '1010.99', '119.44', '11.81%'),
(107, 'November', 2016, '951.37', '105.41', '11.08%'),
(108, 'December', 2016, '958.73', '100.71', '10.50%'),
(109, 'January', 2017, '1009.47', '110.58', '10.95%'),
(110, 'February', 2017, '940.75', '95.48', '10.15%'),
(111, 'March', 2017, '1077.52', '105.6', '9.80%'),
(112, 'April', 2017, '1092.64', '102.86', '9.41%'),
(113, 'May', 2017, '1267.61', '131.75', '10.39%'),
(114, 'June', 2017, '1214.61', '122.67', '10.10%'),
(115, 'July', 2017, '1115.57', '107.8', '9.66%'),
(116, 'August', 2017, '1418.58', '133.35', '9.40%'),
(117, 'September', 2017, '856.87', '83.92', '9.79%'),
(118, 'October', 2017, '1162.77', '112.45', '9.67%'),
(119, 'November', 2017, '1214.75', '103.97', '8.56%'),
(120, 'December', 2017, '1163.82', '108.17', '9.29%'),
(121, 'January', 2018, '1379.79', '129.65', '9.40%'),
(122, 'February', 2018, '1149.08', '115.51', '10.05%'),
(123, 'March', 2018, '1299.77', '125.55', '9.66%'),
(124, 'April', 2018, '1331.33', '130.36', '9.79%'),
(125, 'May', 2018, '1504.98', '143.23', '9.52%'),
(126, 'June', 2018, '1384.38', '135.32', '9.77%'),
(127, 'July', 2018, '1318.18', '130.76', '9.92%'),
(128, 'August', 2018, '1411.05', '150.79', '10.69%'),
(129, 'September', 2018, '1139.66', '113.18', '9.93%'),
(130, 'October', 2018, '1239.11', '122.56', '9.89%'),
(131, 'November', 2018, '1180.44', '107.5', '9.11%'),
(132, 'December', 2018, '1206.91', '110.66', '9.17%'),
(133, 'January', 2019, '1597.21', '141.9', '8.88%'),
(134, 'February', 2019, '1317.73', '113.77', '8.63%'),
(135, 'March', 2019, '1458.68', '134.34', '9.21%'),
(136, 'April', 2019, '1434.3', '143.44', '10.00%'),
(137, 'May', 2019, '1748.16', '175.84', '10.06%'),
(138, 'June', 2019, '1368.2', '143.54', '10.49%'),
(139, 'July', 2019, '1597.66', '167.13', '10.46%'),
(140, 'August', 2019, '1444.75', '156.3', '10.82%'),
(141, 'September', 2019, '1472.16', '146.18', '9.93%'),
(142, 'October', 2019, '1639.62', '147.12', '8.97%'),
(143, 'November', 2019, '1555.22', '136.62', '8.78%'),
(144, 'December', 2019, '1687.15', '159.67', '9.46%'),
(145, 'January', 2020, '1638.53', '175.38', '10.70%'),
(146, 'February', 2020, '1452.2', '147.91', '10.19%'),
(147, 'March', 2020, '1276.26', '156.34', '12.25%'),
(148, 'April', 2020, '1081.46', '90.14', '8.34%'),
(149, 'May', 2020, '1503.4', '119.68', '7.96%'),
(150, 'June', 2020, '1832.56', '160.26', '8.75%'),
(151, 'July', 2020, '2599.56', '423.09', '16.28%'),
(152, 'August', 2020, '1963.94', '327.69', '16.69%'),
(153, 'September', 2020, '2151.05', '202.45', '9.41%'),
(154, 'October', 2020, '2112.44', '201.92', '9.56%'),
(155, 'November', 2020, '2078.74', '220.16', '10.59%'),
(156, 'December', 2020, '2050.65', '252.33', '12.30%'),
(157, 'January', 2021, '1961.91', '203.65', '10.38%'),
(158, 'February', 2021, '1780.59', '170.13', '9.55%'),
(159, 'March', 2021, '1910.98', '197.11', '10.31%'),
(160, 'April', 2021, '2067.64', '215.09', '10.40%'),
(161, 'May', 2021, '2171.03', '240.8', '11.09%'),
(162, 'June', 2021, '1940.81', '168.85', '8.70%'),
(163, 'July', 2021, '1871.49', '194.8', '10.41%'),
(164, 'August', 2021, '1810.1', '162.56', '8.98%'),
(165, 'September', 2021, '1726.71', '148.79', '8.62%'),
(166, 'October', 2021, '1646.87', '131.81', '8.00%'),
(167, 'November', 2021, '1553.7', '126.9', '8.17%'),
(168, 'December', 2021, '1629.04', '132.79', '8.15%'),
(169, 'January', 2022, '1704.45', '124.87', '7.33%'),
(170, 'February', 2022, '1496.09', '113.63', '7.60%'),
(171, 'March', 2022, '1859.97', '121.59', '6.54%'),
(172, 'April', 2022, '2009.49', '125.73', '6.26%'),
(173, 'May', 2022, '1885.34', '125.89', '6.68%'),
(174, 'June', 2022, '1837.27', '114.6', '6.24%'),
(175, 'July', 2022, '2096.91', '139.98', '6.68%'),
(176, 'August', 2022, '2037.8', '132.25', '6.49%'),
(177, 'September', 2022, '1539.49', '95.58', '6.21%'),
(178, 'October', 2022, '1525.43', '106.41', '6.98%'),
(179, 'November', 2022, '1594.73', '105.71', '6.63%'),
(180, 'December', 2022, '1699.69', '99.09', '5.83%'),
(181, 'January', 2023, '1958.86', '104.24', '5.32%'),
(182, 'February', 2023, '1561.26', '145.55', '9.32%'),
(183, 'March', 2023, '2017.68', '94.01', '4.66%'),
(184, 'April', 2023, '1683.47', '98.31', '5.84%'),
(185, 'May', 2023, '1691.63', '116.97', '6.91%'),
(186, 'June', 2023, '2199.01', '132.4', '6.02%'),
(187, 'July', 2023, '1973.15', '123.31', '6.25%'),
(188, 'August', 2023, '1599.45', '61.64', '3.85%'),
(189, 'September', 2023, '1343.66', '45.02', '3.35%'),
(190, 'October', 2023, '1977.56', '50.96', '2.58%'),
(191, 'November', 2023, '1930.04', '63.09', '3.27%'),
(192, 'December', 2023, '1989.87', '67.82', '3.41%'),
(193, 'January', 2024, '2100.95', '67.13', '3.20%'),
(194, 'February', 2024, '2166.04', '105.21', '4.86%'),
(195, 'March', 2024, '1996.85', '53.01', '2.65%'),
(196, 'April', 2024, '2043.06', '39.57', '1.94%'),
(197, 'May', 2024, '2253.88', '62.01', '2.75%'),
(198, 'June', 2024, '2541.65', '190.1', '7.48%'),
(199, 'July', 2024, '1913.58', '53.82', '2.81%'),
(200, 'August', 2024, '2221.32', '96.89', '4.36%'),
(201, 'September', 2024, '2404.79', '322.12', '13.39%'),
(202, 'October', 2024, '2395.08', '262.47', '10.96%'),
(203, 'November', 2024, '2199.51', '288.24', '13.10%'),
(204, 'December', 2024, '', '', '');

-- --------------------------------------------------------

--
-- Table structure for table `analytics_abl_target_achievement`
--

CREATE TABLE `analytics_abl_target_achievement` (
  `id` int(255) NOT NULL,
  `year` varchar(255) NOT NULL,
  `target` varchar(255) NOT NULL,
  `achievement` varchar(255) NOT NULL,
  `percentage` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

--
-- Dumping data for table `analytics_abl_target_achievement`
--

INSERT INTO `analytics_abl_target_achievement` (`id`, `year`, `target`, `achievement`, `percentage`) VALUES
(1, '2021', '26000', '17961.01', '69.08'),
(2, '2022', '26000', '13246.81', '50.95'),
(3, '2023', '16500', '11934.41', '72.33'),
(4, '2024', '16500', '17766.70', '107.68');

-- --------------------------------------------------------

--
-- Table structure for table `analytics_all_bank_remittance`
--

CREATE TABLE `analytics_all_bank_remittance` (
  `id` int(255) NOT NULL,
  `bank_name` varchar(255) NOT NULL,
  `year` int(255) NOT NULL,
  `amount` varchar(255) NOT NULL,
  `share` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

--
-- Dumping data for table `analytics_all_bank_remittance`
--

INSERT INTO `analytics_all_bank_remittance` (`id`, `bank_name`, `year`, `amount`, `share`) VALUES
(1, 'Agrani Bank', 2020, '2477.35', '11.68%'),
(2, 'Janata Bank', 2020, '935.2', '4.41%'),
(3, 'Rupali Bank', 2020, '656.1', '3.09%'),
(4, 'Sonali Bank', 2020, '1447.07', '6.82%'),
(5, 'BASIC Bank', 2020, '1.11', '0.01%'),
(6, 'BDBL', 2020, '0', '0.00%'),
(7, 'Bangladesh Krishi Bank', 2020, '360.92', '1.70%'),
(8, 'RAKUB.', 2020, '0', '0.00%'),
(9, 'AB Bank Ltd.', 2020, '126.88', '0.60%'),
(10, 'Al-Arafah Islami Bank Ltd.', 2020, '453.2', '2.14%'),
(11, 'Bangladesh Commerce Bank Ltd.', 2020, '10.19', '0.05%'),
(12, 'Bank Asia Ltd.', 2020, '745.19', '3.51%'),
(13, 'BRAC Bank Ltd.', 2020, '370.96', '1.75%'),
(14, 'Dhaka Bank Ltd.', 2020, '49.94', '0.24%'),
(15, 'Community Bank Bangladesh Ltd.', 2020, '0', '0.00%'),
(16, 'Dutch Bangla Bank Ltd.', 2020, '2016.22', '9.50%'),
(17, 'Eastern Bank Ltd.', 2020, '103.02', '0.49%'),
(18, 'EXIM Bank Ltd.', 2020, '41.96', '0.20%'),
(19, 'First Security Islami Bank Ltd.', 2020, '136.18', '0.64%'),
(20, 'Global Islami Bank Ltd.', 2020, '0.65', '0.00%'),
(21, 'ICB Islamic Bank', 2020, '24.15', '0.11%'),
(22, 'IFIC Bank Ltd.', 2020, '24.15', '0.11%'),
(23, 'Islami Bank Bangladesh Ltd.', 2020, '6342.83', '29.89%'),
(24, 'Jamuna Bank Ltd.', 2020, '14.7', '0.07%'),
(25, 'Meghna Bank Ltd.', 2020, '247.24', '1.17%'),
(26, 'Mercantile Bank Ltd.', 2020, '6.68', '0.03%'),
(27, 'Midland Bank Ltd.', 2020, '7.22', '0.03%'),
(28, 'Modhumoti Bank Ltd', 2020, '403.78', '1.90%'),
(29, 'Mutual Trust Bank Ltd.', 2020, '354.6', '1.67%'),
(30, 'National Bank Ltd.', 2020, '481.44', '2.27%'),
(31, 'NCC Bank Ltd.', 2020, '481.44', '2.27%'),
(32, 'NRB Bank Ltd.', 2020, '9.14', '0.04%'),
(33, 'NRB Commercial Bank Ltd.', 2020, '7.98', '0.04%'),
(34, 'One Bank Ltd.', 2020, '41.48', '0.20%'),
(35, 'Padma Bank Ltd.', 2020, '2.73', '0.01%'),
(36, 'Premier Bank Ltd.', 2020, '99.88', '0.47%'),
(37, 'Prime Bank Ltd.', 2020, '314.43', '1.48%'),
(38, 'Pubali Bank Ltd.', 2020, '528.19', '2.49%'),
(39, 'Shahjalal Islami Bank Ltd.', 2020, '43.18', '0.20%'),
(40, 'Shimanto Bank Ltd.', 2020, '0.46', '0.00%'),
(41, 'Social Islami Bank Ltd.', 2020, '144.22', '0.68%'),
(42, 'SBAC Bank Ltd.', 2020, '43.84', '0.21%'),
(43, 'Southeast Bank Ltd.', 2020, '424.64', '2.00%'),
(44, 'Standard Bank Ltd.', 2020, '87.69', '0.41%'),
(45, 'The City Bank Ltd.', 2020, '381.69', '1.80%'),
(46, 'Trust Bank Ltd.', 2020, '102.86', '0.48%'),
(47, 'Union Bank Ltd.', 2020, '28.52', '0.13%'),
(48, 'United Commercial Bank Ltd.', 2020, '247.54', '1.17%'),
(49, 'Uttara Bank Ltd.', 2020, '270.08', '1.27%'),
(50, 'Bank Al-Falah', 2020, '0.11', '0.00%'),
(51, 'CITI Bank NA', 2020, '0.73', '0.00%'),
(52, 'Commercial Bank of Ceylon', 2020, '4.57', '0.02%'),
(53, 'Habib Bank Ltd.', 2020, '0', '0.00%'),
(54, 'HSBC', 2020, '4.72', '0.02%'),
(55, 'National Bank of Pakistan', 2020, '0', '0.00%'),
(56, 'Standard Chartered Bank', 2020, '54.89', '0.26%'),
(57, 'State Bank of India', 2020, '0.21', '0.00%'),
(58, 'Woori Bank Ltd.', 2020, '53.5', '0.25%'),
(59, 'Bengal Commercial Bank Ltd', 2020, '-0', '-0'),
(60, 'Citizens Bank Plc.', 2020, '-0', '-0'),
(61, 'Agrani Bank', 2021, '2093.28', '9.55%'),
(62, 'Janata Bank', 2021, '800.43', '3.65%'),
(63, 'Rupali Bank', 2021, '531.49', '2.42%'),
(64, 'Sonali Bank', 2021, '1400.47', '6.39%'),
(65, 'BASIC Bank', 2021, '2.21', '0.01%'),
(66, 'BDBL', 2021, '0', '0.00%'),
(67, 'Bangladesh Krishi Bank', 2021, '374.38', '1.71%'),
(68, 'RAKUB.', 2021, '0', '0.00%'),
(69, 'AB Bank Ltd.', 2021, '186.68', '0.85%'),
(70, 'Al-Arafah Islami Bank Ltd.', 2021, '563.07', '2.57%'),
(71, 'Bangladesh Commerce Bank Ltd.', 2021, '13.12', '0.06%'),
(72, 'Bank Asia Ltd.', 2021, '910.97', '4.15%'),
(73, 'BRAC Bank Ltd.', 2021, '360.88', '1.65%'),
(74, 'Dhaka Bank Ltd.', 2021, '35.68', '0.16%'),
(75, 'Community Bank Bangladesh Ltd.', 2021, '0', '0.00%'),
(76, 'Dutch Bangla Bank Ltd.', 2021, '2546.59', '11.61%'),
(77, 'Eastern Bank Ltd.', 2021, '49.45', '0.23%'),
(78, 'EXIM Bank Ltd.', 2021, '42.04', '0.19%'),
(79, 'First Security Islami Bank Ltd.', 2021, '136.72', '0.62%'),
(80, 'Global Islami Bank Ltd.', 2021, '38.88', '0.18%'),
(81, 'ICB Islamic Bank', 2021, '4.21', '0.02%'),
(82, 'IFIC Bank Ltd.', 2021, '595.85', '2.72%'),
(83, 'Islami Bank Bangladesh Ltd.', 2021, '6104.19', '27.84%'),
(84, 'Jamuna Bank Ltd.', 2021, '240.47', '1.10%'),
(85, 'Meghna Bank Ltd.', 2021, '63.18', '0.29%'),
(86, 'Mercantile Bank Ltd.', 2021, '317.38', '1.45%'),
(87, 'Midland Bank Ltd.', 2021, '6.54', '0.03%'),
(88, 'Modhumoti Bank Ltd', 2021, '48.18', '0.22%'),
(89, 'Mutual Trust Bank Ltd.', 2021, '480.15', '2.19%'),
(90, 'National Bank Ltd.', 2021, '357.72', '1.63%'),
(91, 'NCC Bank Ltd.', 2021, '465.37', '2.12%'),
(92, 'NRB Bank Ltd.', 2021, '4.57', '0.02%'),
(93, 'NRB Commercial Bank Ltd.', 2021, '21.89', '0.10%'),
(94, 'One Bank Ltd.', 2021, '47.07', '0.21%'),
(95, 'Padma Bank Ltd.', 2021, '4.94', '0.02%'),
(96, 'Premier Bank Ltd.', 2021, '216.41', '0.99%'),
(97, 'Prime Bank Ltd.', 2021, '215.42', '0.98%'),
(98, 'Pubali Bank Ltd.', 2021, '659.38', '3.01%'),
(99, 'Shahjalal Islami Bank Ltd.', 2021, '50.89', '0.23%'),
(100, 'Shimanto Bank Ltd.', 2021, '0.39', '0.00%'),
(101, 'Social Islami Bank Ltd.', 2021, '179.19', '0.82%'),
(102, 'SBAC Bank Ltd.', 2021, '24.71', '0.11%'),
(103, 'Southeast Bank Ltd.', 2021, '469.39', '2.14%'),
(104, 'Standard Bank Ltd.', 2021, '138.51', '0.63%'),
(105, 'The City Bank Ltd.', 2021, '432.84', '1.97%'),
(106, 'Trust Bank Ltd.', 2021, '115.89', '0.53%'),
(107, 'Union Bank Ltd.', 2021, '33.43', '0.15%'),
(108, 'United Commercial Bank Ltd.', 2021, '186.23', '0.85%'),
(109, 'Uttara Bank Ltd.', 2021, '267.46', '1.22%'),
(110, 'Bank Al-Falah', 2021, '0.04', '0.00%'),
(111, 'CITI Bank NA', 2021, '0.76', '0.00%'),
(112, 'Commercial Bank of Ceylon', 2021, '7.81', '0.04%'),
(113, 'Habib Bank Ltd.', 2021, '0', '0.00%'),
(114, 'HSBC', 2021, '5.7', '0.03%'),
(115, 'National Bank of Pakistan', 2021, '0', '0.00%'),
(116, 'Standard Chartered Bank', 2021, '55.72', '0.25%'),
(117, 'State Bank of India', 2021, '0.12', '0.00%'),
(118, 'Woori Bank Ltd.', 2021, '17.9', '0.08%'),
(119, 'Bengal Commercial Bank Ltd', 2021, '-0', '-0'),
(120, 'Citizens Bank Plc.', 2021, '-0', '-0'),
(121, 'Agrani Bank', 2022, '1405.33', '6.60%'),
(122, 'Janata Bank', 2022, '601.97', '2.83%'),
(123, 'Rupali Bank', 2022, '566.92', '2.66%'),
(124, 'Sonali Bank', 2022, '1070.16', '5.03%'),
(125, 'BASIC Bank', 2022, '3.72', '0.02%'),
(126, 'BDBL', 2022, '0', '0.00%'),
(127, 'Bangladesh Krishi Bank', 2022, '350.66', '1.65%'),
(128, 'RAKUB.', 2022, '0', '0.00%'),
(129, 'AB Bank Ltd.', 2022, '110.39', '0.52%'),
(130, 'Al-Arafah Islami Bank Ltd.', 2022, '837.75', '3.94%'),
(131, 'Bangladesh Commerce Bank Ltd.', 2022, '10.64', '0.05%'),
(132, 'Bank Asia Ltd.', 2022, '822.17', '3.86%'),
(133, 'BRAC Bank Ltd.', 2022, '339.15', '1.59%'),
(134, 'Dhaka Bank Ltd.', 2022, '170.63', '0.80%'),
(135, 'Community Bank Bangladesh Ltd.', 2022, '0', '0.00%'),
(136, 'Dutch Bangla Bank Ltd.', 2022, '1941.56', '9.12%'),
(137, 'Eastern Bank Ltd.', 2022, '72.15', '0.34%'),
(138, 'EXIM Bank Ltd.', 2022, '44.5', '0.21%'),
(139, 'First Security Islami Bank Ltd.', 2022, '142.14', '0.67%'),
(140, 'Global Islami Bank Ltd.', 2022, '26.75', '0.13%'),
(141, 'ICB Islamic Bank', 2022, '0.97', '0.00%'),
(142, 'IFIC Bank Ltd.', 2022, '64.81', '0.30%'),
(143, 'Islami Bank Bangladesh Ltd.', 2022, '4654.27', '21.87%'),
(144, 'Jamuna Bank Ltd.', 2022, '460.62', '2.16%'),
(145, 'Meghna Bank Ltd.', 2022, '11.88', '0.06%'),
(146, 'Mercantile Bank Ltd.', 2022, '607.16', '2.85%'),
(147, 'Midland Bank Ltd.', 2022, '6.92', '0.03%'),
(148, 'Modhumoti Bank Ltd', 2022, '29.47', '0.14%'),
(149, 'Mutual Trust Bank Ltd.', 2022, '855.56', '4.02%'),
(150, 'National Bank Ltd.', 2022, '334.97', '1.57%'),
(151, 'NCC Bank Ltd.', 2022, '576.34', '2.71%'),
(152, 'NRB Bank Ltd.', 2022, '22.82', '0.11%'),
(153, 'NRB Commercial Bank Ltd.', 2022, '26.64', '0.13%'),
(154, 'One Bank Ltd.', 2022, '25.01', '0.12%'),
(155, 'Padma Bank Ltd.', 2022, '1.17', '0.01%'),
(156, 'Premier Bank Ltd.', 2022, '290.57', '1.37%'),
(157, 'Prime Bank Ltd.', 2022, '168.06', '0.79%'),
(158, 'Pubali Bank Ltd.', 2022, '837.04', '3.93%'),
(159, 'Shahjalal Islami Bank Ltd.', 2022, '146.56', '0.69%'),
(160, 'Shimanto Bank Ltd.', 2022, '0.57', '0.00%'),
(161, 'Social Islami Bank Ltd.', 2022, '351.32', '1.65%'),
(162, 'SBAC Bank Ltd.', 2022, '91.99', '0.43%'),
(163, 'Southeast Bank Ltd.', 2022, '800.92', '3.76%'),
(164, 'Standard Bank Ltd.', 2022, '130.68', '0.61%'),
(165, 'The City Bank Ltd.', 2022, '868.76', '4.08%'),
(166, 'Trust Bank Ltd.', 2022, '602.51', '2.83%'),
(167, 'Union Bank Ltd.', 2022, '38.83', '0.18%'),
(168, 'United Commercial Bank Ltd.', 2022, '468.15', '2.20%'),
(169, 'Uttara Bank Ltd.', 2022, '206.39', '0.97%'),
(170, 'Bank Al-Falah', 2022, '0.51', '0.00%'),
(171, 'CITI Bank NA', 2022, '0.85', '0.00%'),
(172, 'Commercial Bank of Ceylon', 2022, '4.91', '0.02%'),
(173, 'Habib Bank Ltd.', 2022, '0', '0.00%'),
(174, 'HSBC', 2022, '9.16', '0.04%'),
(175, 'National Bank of Pakistan', 2022, '0', '0.00%'),
(176, 'Standard Chartered Bank', 2022, '60.25', '0.28%'),
(177, 'State Bank of India', 2022, '0.01', '0.00%'),
(178, 'Woori Bank Ltd.', 2022, '12.53', '0.06%'),
(179, 'Bengal Commercial Bank Ltd', 2022, '0.02', '0.00%'),
(180, 'Citizens Bank Plc.', 2022, '-0', '-0'),
(181, 'Agrani Bank', 2023, '1103.29', '5.03%'),
(182, 'Janata Bank', 2023, '763.13', '3.48%'),
(183, 'Rupali Bank', 2023, '215.71', '0.98%'),
(184, 'Sonali Bank', 2023, '592.15', '2.70%'),
(185, 'BASIC Bank', 2023, '1.74', '0.01%'),
(186, 'BDBL', 2023, '0', '0.00%'),
(187, 'Bangladesh Krishi Bank', 2023, '649.18', '2.96%'),
(188, 'RAKUB.', 2023, '0', '0.00%'),
(189, 'AB Bank Ltd.', 2023, '152.63', '0.70%'),
(190, 'Al-Arafah Islami Bank Ltd.', 2023, '970.59', '4.43%'),
(191, 'Bangladesh Commerce Bank Ltd.', 2023, '7.23', '0.03%'),
(192, 'Bank Asia Ltd.', 2023, '424.82', '1.94%'),
(193, 'BRAC Bank Ltd.', 2023, '817.2', '3.73%'),
(194, 'Dhaka Bank Ltd.', 2023, '476.98', '2.18%'),
(195, 'Community Bank Bangladesh Ltd.', 2023, '0', '0.00%'),
(196, 'Dutch Bangla Bank Ltd.', 2023, '581.91', '2.65%'),
(197, 'Eastern Bank Ltd.', 2023, '296.16', '1.35%'),
(198, 'EXIM Bank Ltd.', 2023, '43.48', '0.20%'),
(199, 'First Security Islami Bank Ltd.', 2023, '199.15', '0.91%'),
(200, 'Global Islami Bank Ltd.', 2023, '19.65', '0.09%'),
(201, 'ICB Islamic Bank', 2023, '0.37', '0.00%'),
(202, 'IFIC Bank Ltd.', 2023, '68.78', '0.31%'),
(203, 'Islami Bank Bangladesh Ltd.', 2023, '5054.96', '23.06%'),
(204, 'Jamuna Bank Ltd.', 2023, '514.13', '2.34%'),
(205, 'Meghna Bank Ltd.', 2023, '96.01', '0.44%'),
(206, 'Mercantile Bank Ltd.', 2023, '372.43', '1.70%'),
(207, 'Midland Bank Ltd.', 2023, '19.44', '0.09%'),
(208, 'Modhumoti Bank Ltd', 2023, '193.7', '0.88%'),
(209, 'Mutual Trust Bank Ltd.', 2023, '761.35', '3.47%'),
(210, 'National Bank Ltd.', 2023, '488.84', '2.23%'),
(211, 'NCC Bank Ltd.', 2023, '665.26', '3.03%'),
(212, 'NRB Bank Ltd.', 2023, '230.39', '1.05%'),
(213, 'NRB Commercial Bank Ltd.', 2023, '10.9', '0.05%'),
(214, 'One Bank Ltd.', 2023, '59.62', '0.27%'),
(215, 'Padma Bank Ltd.', 2023, '0.68', '0.00%'),
(216, 'Premier Bank Ltd.', 2023, '530.37', '2.42%'),
(217, 'Prime Bank Ltd.', 2023, '127.36', '0.58%'),
(218, 'Pubali Bank Ltd.', 2023, '1011.63', '4.61%'),
(219, 'Shahjalal Islami Bank Ltd.', 2023, '403.51', '1.84%'),
(220, 'Shimanto Bank Ltd.', 2023, '0.27', '0.00%'),
(221, 'Social Islami Bank Ltd.', 2023, '1060.23', '4.84%'),
(222, 'SBAC Bank Ltd.', 2023, '164.87', '0.75%'),
(223, 'Southeast Bank Ltd.', 2023, '294.89', '1.34%'),
(224, 'Standard Bank Ltd.', 2023, '380.53', '1.74%'),
(225, 'The City Bank Ltd.', 2023, '641.35', '2.93%'),
(226, 'Trust Bank Ltd.', 2023, '800.42', '3.65%'),
(227, 'Union Bank Ltd.', 2023, '15.32', '0.07%'),
(228, 'United Commercial Bank Ltd.', 2023, '494.84', '2.26%'),
(229, 'Uttara Bank Ltd.', 2023, '61.87', '0.28%'),
(230, 'Bank Al-Falah', 2023, '2.28', '0.01%'),
(231, 'CITI Bank NA', 2023, '0.57', '0.00%'),
(232, 'Commercial Bank of Ceylon', 2023, '3.98', '0.02%'),
(233, 'Habib Bank Ltd.', 2023, '0', '0.00%'),
(234, 'HSBC', 2023, '7.51', '0.03%'),
(235, 'National Bank of Pakistan', 2023, '0', '0.00%'),
(236, 'Standard Chartered Bank', 2023, '55.38', '0.25%'),
(237, 'State Bank of India', 2023, '0', '0.00%'),
(238, 'Woori Bank Ltd.', 2023, '3.01', '0.01%'),
(239, 'Bengal Commercial Bank Ltd', 2023, '13.59', '0.06%'),
(240, 'Citizens Bank Plc.', 2023, '0', '0.00%');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `analytics_abl_current_year_target_achievement`
--
ALTER TABLE `analytics_abl_current_year_target_achievement`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `analytics_abl_growth`
--
ALTER TABLE `analytics_abl_growth`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `analytics_abl_target_achievement`
--
ALTER TABLE `analytics_abl_target_achievement`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `analytics_all_bank_remittance`
--
ALTER TABLE `analytics_all_bank_remittance`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `analytics_abl_current_year_target_achievement`
--
ALTER TABLE `analytics_abl_current_year_target_achievement`
  MODIFY `id` int(255) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT for table `analytics_abl_growth`
--
ALTER TABLE `analytics_abl_growth`
  MODIFY `id` int(255) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=205;

--
-- AUTO_INCREMENT for table `analytics_abl_target_achievement`
--
ALTER TABLE `analytics_abl_target_achievement`
  MODIFY `id` int(255) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `analytics_all_bank_remittance`
--
ALTER TABLE `analytics_all_bank_remittance`
  MODIFY `id` int(255) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=241;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
