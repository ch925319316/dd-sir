/*
SQLyog Enterprise v12.09 (64 bit)
MySQL - 5.7.25-log : Database - sir
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`sir` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `sir`;

/*Table structure for table `sir_actors` */

DROP TABLE IF EXISTS `sir_actors`;

CREATE TABLE `sir_actors` (
  `id` varchar(20) COLLATE utf8_unicode_ci NOT NULL,
  `chinaName` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL,
  `name` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL,
  `pinyin` varchar(40) COLLATE utf8_unicode_ci DEFAULT NULL,
  `cover` varchar(400) COLLATE utf8_unicode_ci DEFAULT NULL,
  `renqi` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `av_count` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '作品数',
  `created_at` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '出道时间',
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Table structure for table `sir_ts` */

DROP TABLE IF EXISTS `sir_ts`;

CREATE TABLE `sir_ts` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `v_id` varchar(20) DEFAULT NULL COMMENT '对应的v_Id',
  `oss` varchar(400) DEFAULT NULL COMMENT 'oss',
  `count` varchar(10) DEFAULT NULL COMMENT '顺序',
  `create_time` datetime DEFAULT NULL,
  `url` varchar(400) DEFAULT NULL,
  `is_down` char(1) DEFAULT '0' COMMENT '是否已下载',
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2109 DEFAULT CHARSET=utf8;

/*Table structure for table `sir_video` */

DROP TABLE IF EXISTS `sir_video`;

CREATE TABLE `sir_video` (
  `id` varchar(20) NOT NULL,
  `_id` varchar(60) DEFAULT NULL COMMENT '番号',
  `title` varchar(600) DEFAULT NULL COMMENT '名称',
  `cover_full` varchar(600) DEFAULT NULL COMMENT '封面',
  `actors` varchar(200) DEFAULT NULL COMMENT '作者',
  `tags` varchar(400) DEFAULT NULL COMMENT '标签',
  `created_at` varchar(13) DEFAULT NULL COMMENT '发布时间',
  `badges` varchar(400) DEFAULT NULL COMMENT '权限',
  `price` varchar(120) DEFAULT NULL COMMENT '价格',
  `good` varchar(10) DEFAULT NULL COMMENT '评价',
  `img_oss` varchar(400) DEFAULT NULL COMMENT '图片本地地址',
  `rating` varchar(20) DEFAULT NULL COMMENT '等级',
  `duration` varchar(20) DEFAULT NULL COMMENT '持续时长',
  `mv_url` varchar(600) DEFAULT NULL COMMENT 'mv地址',
  `is_down` char(1) DEFAULT '0' COMMENT '是否已下载 0未下载 1已下载 2正在下载 3ts下载失败 4tsurl下载成功 9未找到url',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `oss` varchar(400) DEFAULT NULL COMMENT '本地url',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
