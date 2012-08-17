-- phpMyAdmin SQL Dump
-- version 3.4.5deb1
-- http://www.phpmyadmin.net
--
-- 主机: localhost
-- 生成日期: 2012 年 03 月 01 日 16:05
-- 服务器版本: 5.1.58
-- PHP 版本: 5.3.6-13ubuntu3.6

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- 数据库: `friendbook`
--
CREATE DATABASE `friendbook` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;
USE `friendbook`;

-- --------------------------------------------------------

--
-- 表的结构 `checkin`
--

CREATE TABLE IF NOT EXISTS `checkin` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `hwid` varchar(64) NOT NULL,
  `timestamp` varchar(48) NOT NULL,
  `path` varchar(128) NOT NULL,
  `status` tinyint(1) NOT NULL,
  PRIMARY KEY (`path`,`hwid`),
  KEY `id` (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1725 ;

-- --------------------------------------------------------

--
-- 表的结构 `corpus`
--

CREATE TABLE IF NOT EXISTS `corpus` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `hwid` varchar(64) NOT NULL,
  `timestamp` bigint(20) NOT NULL,
  `w1` double NOT NULL,
  `w2` double NOT NULL,
  `w3` double NOT NULL,
  `w4` double NOT NULL,
  `w5` double NOT NULL,
  `w6` double NOT NULL,
  `w7` double NOT NULL,
  `w8` double NOT NULL,
  `w9` double NOT NULL,
  `w10` double NOT NULL,
  `w11` double NOT NULL,
  `w12` double NOT NULL,
  `w13` double NOT NULL,
  `w14` double NOT NULL,
  `w15` double NOT NULL,
  `w16` double NOT NULL,
  `w17` double NOT NULL,
  `w18` double NOT NULL,
  `w19` double NOT NULL,
  `w20` double NOT NULL,
  PRIMARY KEY (`hwid`),
  KEY `id` (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=9 ;

-- --------------------------------------------------------

--
-- 表的结构 `graph`
--

CREATE TABLE IF NOT EXISTS `graph` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `hwid` varchar(64) NOT NULL,
  `timestamp` bigint(20) NOT NULL,
  `neighbor` longtext NOT NULL,
  PRIMARY KEY (`hwid`),
  KEY `id` (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=9 ;

-- --------------------------------------------------------

--
-- 表的结构 `index`
--

CREATE TABLE IF NOT EXISTS `index` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `timestamp` bigint(20) NOT NULL,
  `topic` bigint(20) NOT NULL,
  `doclist` longtext NOT NULL,
  PRIMARY KEY (`topic`),
  KEY `id` (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=11 ;

-- --------------------------------------------------------

--
-- 表的结构 `ranks`
--

CREATE TABLE IF NOT EXISTS `ranks` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `hwid` varchar(64) NOT NULL,
  `timestamp` bigint(20) NOT NULL,
  `rank` double NOT NULL,
  PRIMARY KEY (`hwid`),
  KEY `id` (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=9 ;

-- --------------------------------------------------------

--
-- 表的结构 `rawdata`
--

CREATE TABLE IF NOT EXISTS `rawdata` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `hwid` varchar(64) NOT NULL,
  `timestamp` bigint(20) NOT NULL,
  `hrs` int(10) unsigned NOT NULL,
  `acc-x` double NOT NULL,
  `acc-y` double NOT NULL,
  `acc-z` double NOT NULL,
  `gyr-x` double NOT NULL,
  `gyr-y` double NOT NULL,
  `gyr-z` double NOT NULL,
  `gps-lat` double NOT NULL,
  `gps-lng` double NOT NULL,
  `gps-sp` double NOT NULL,
  `gps-acc` double NOT NULL,
  PRIMARY KEY (`hwid`,`timestamp`),
  KEY `id` (`id`,`hwid`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=32588220 ;

-- --------------------------------------------------------

--
-- 表的结构 `rawdoc`
--

CREATE TABLE IF NOT EXISTS `rawdoc` (
  `docid` bigint(20) NOT NULL AUTO_INCREMENT,
  `hwid` varchar(64) NOT NULL,
  `timestamp` bigint(20) NOT NULL,
  `entity` longtext NOT NULL,
  PRIMARY KEY (`hwid`,`timestamp`),
  KEY `docid` (`docid`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=33 ;

-- --------------------------------------------------------

--
-- 表的结构 `topic`
--

CREATE TABLE IF NOT EXISTS `topic` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `hwid` varchar(64) NOT NULL,
  `timestamp` bigint(20) NOT NULL,
  `topic` longtext NOT NULL,
  PRIMARY KEY (`hwid`),
  KEY `id` (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=9 ;

-- --------------------------------------------------------

--
-- 表的结构 `users`
--

CREATE TABLE IF NOT EXISTS `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `hwid` varchar(64) NOT NULL,
  `uip` varchar(48) NOT NULL,
  `checkin` varchar(48) NOT NULL,
  PRIMARY KEY (`hwid`),
  KEY `id` (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=7 ;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
