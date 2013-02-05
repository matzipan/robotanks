CREATE DATABASE "robotanks";

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `robotanks`
--

-- --------------------------------------------------------

--
-- Table structure for table `battle`
--

DROP TABLE IF EXISTS `battle`;
CREATE TABLE IF NOT EXISTS `battle` (
  `battle_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `competition_id` int(11) NOT NULL,
  `battle_date` int(11) NOT NULL,
  PRIMARY KEY (`battle_id`),
  KEY `competition_id` (`competition_id`),
  KEY `battle_date` (`battle_date`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=2 ;

--
-- Dumping data for table `battle`
--


-- --------------------------------------------------------

--
-- Table structure for table `competitions`
--

DROP TABLE IF EXISTS `competitions`;
CREATE TABLE IF NOT EXISTS `competitions` (
  `competition_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `competition_name` varchar(50) NOT NULL,
  `competition_owner` int(11) NOT NULL,
  `competition_start` int(11) NOT NULL,
  `competition_end` int(11) NOT NULL,
  `competition_official` tinyint(1) NOT NULL COMMENT 'if competition official or  not',
  PRIMARY KEY (`competition_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

--
-- Dumping data for table `competitions`
--


-- --------------------------------------------------------

--
-- Table structure for table `robots`
--

DROP TABLE IF EXISTS `robots`;
CREATE TABLE IF NOT EXISTS `robots` (
  `robot_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `robot_name` varchar(30) NOT NULL,
  PRIMARY KEY (`robot_id`),
  KEY `user_id` (`user_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

--
-- Dumping data for table `robots`
--


-- --------------------------------------------------------

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `user_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_name` text NOT NULL,
  `user_password` varchar(200) NOT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `auth` (`user_name`(50),`user_password`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=3 ;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`user_id`, `user_name`, `user_password`) VALUES
(1, 'matzipan', '1002912b251887b4fe0e5fa1efdbfa06'),
(2, 'redddragon', '741147');
