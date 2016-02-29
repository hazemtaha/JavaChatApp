-- phpMyAdmin SQL Dump
-- version 4.0.10deb1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Feb 29, 2016 at 10:00 AM
-- Server version: 5.5.47-0ubuntu0.14.04.1
-- PHP Version: 5.5.9-1ubuntu4.14

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `chatApp`
--

-- --------------------------------------------------------

--
-- Table structure for table `contact_list`
--

CREATE TABLE IF NOT EXISTS `contact_list` (
  `u_id` int(11) NOT NULL,
  `friend_id` int(11) NOT NULL,
  PRIMARY KEY (`u_id`,`friend_id`),
  KEY `friend_id` (`friend_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `contact_list`
--

INSERT INTO `contact_list` (`u_id`, `friend_id`) VALUES
(2, 1),
(3, 1),
(1, 2),
(3, 2),
(1, 3),
(2, 3),
(1, 4),
(1, 5),
(2, 5),
(2, 6),
(2, 11),
(2, 13);

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE IF NOT EXISTS `users` (
  `u_id` int(11) NOT NULL AUTO_INCREMENT,
  `firstName` varchar(25) NOT NULL,
  `lastName` varchar(25) NOT NULL,
  `age` tinyint(10) NOT NULL,
  `email` varchar(35) NOT NULL,
  `password` char(32) NOT NULL,
  `status` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`u_id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=14 ;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`u_id`, `firstName`, `lastName`, `age`, `email`, `password`, `status`) VALUES
(1, 'Hazem', 'Taha', 0, 'hazem@mail.com', '123', 0),
(2, 'Ahmed', 'Mohamed', 0, 'ahmed@mail.com', '123', 0),
(3, 'Mostaf', 'Ashour', 0, 'sasa@mail.com', '123', 0),
(4, 'Mahmoud', 'Elsherief', 0, 'mahmoud@mail.com', '123', 0),
(5, 'Amr', 'Elmarooty', 0, 'amr@mail.com', '123', 0),
(6, 'Karem', 'Akram', 0, 'karem@mail.com', '123', 0),
(7, 'Andro', 'George', 0, 'andro@mail.com', '123', 0),
(8, 'Asmaa', 'Gaafar', 0, 'asmaa@mail.com', '123', 0),
(9, 'Amira', 'Ismail', 0, 'amira@mail.com', '123', 0),
(10, 'Zahra', 'Mosaab', 0, 'zahra@mail.com', '123', 0),
(11, 'Amer', 'Ahmed', 0, 'amir@mail.com', '123', 0),
(12, 'Serag', 'Karam', 0, 'serag@mail.com', '123', 0),
(13, 'Mohab', 'Amgad', 0, 'mohab@mail.com', '123', 0);

--
-- Constraints for dumped tables
--

--
-- Constraints for table `contact_list`
--
ALTER TABLE `contact_list`
  ADD CONSTRAINT `contact_list_ibfk_1` FOREIGN KEY (`u_id`) REFERENCES `users` (`u_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `contact_list_ibfk_2` FOREIGN KEY (`friend_id`) REFERENCES `users` (`u_id`) ON DELETE CASCADE;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
