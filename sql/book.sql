/*
SQLyog Enterprise v12.09 (64 bit)
MySQL - 5.7.25-log : Database - book
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`book` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `book`;

/*Table structure for table `t_book` */

DROP TABLE IF EXISTS `t_book`;

CREATE TABLE `t_book` (
  `id` char(32) NOT NULL COMMENT 'java中的UUID去掉-',
  `name` varchar(200) DEFAULT NULL COMMENT '书名',
  `create_time` datetime DEFAULT NULL COMMENT '生成时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `author` varchar(100) DEFAULT NULL COMMENT '作者',
  `introduction` varchar(500) DEFAULT NULL COMMENT '简介',
  `type` char(1) DEFAULT NULL COMMENT '是否完结，1：完结，0：连载中',
  `url` varchar(400) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `t_chater` */

DROP TABLE IF EXISTS `t_chater`;

CREATE TABLE `t_chater` (
  `id` char(32) NOT NULL COMMENT 'java的UUID去掉-',
  `book_id` char(32) NOT NULL COMMENT '所属书的id',
  `name` varchar(254) DEFAULT NULL COMMENT '章节名称',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `file_path` varchar(100) DEFAULT NULL COMMENT '正文储存的路径',
  `url` varchar(400) DEFAULT NULL,
  `count` int(11) DEFAULT NULL COMMENT '第几章',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `t_crawl_config` */

DROP TABLE IF EXISTS `t_crawl_config`;

CREATE TABLE `t_crawl_config` (
  `id` char(32) NOT NULL,
  `url` varchar(600) DEFAULT NULL COMMENT '网站入口',
  `website_name` varchar(200) DEFAULT NULL COMMENT '网站名称',
  `method` varchar(10) DEFAULT NULL COMMENT '请求方式',
  `search_result` varchar(400) DEFAULT NULL COMMENT '搜索结果匹配规则',
  `book_name` varchar(400) DEFAULT NULL COMMENT '书名',
  `author` varchar(400) DEFAULT NULL COMMENT '作者',
  `introduction` varchar(400) DEFAULT NULL COMMENT '简介',
  `type` varchar(400) DEFAULT NULL COMMENT '是否完结',
  `list_url` varchar(400) DEFAULT NULL COMMENT '列表所有url',
  `list_title` varchar(400) DEFAULT NULL COMMENT '列表所有title',
  `content` varchar(400) DEFAULT NULL COMMENT '正文',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `test_v` */

DROP TABLE IF EXISTS `test_v`;

CREATE TABLE `test_v` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `count` bigint(20) DEFAULT NULL,
  `name` varchar(200) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `v_name` varchar(200) DEFAULT NULL,
  `v_url` varchar(400) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
