/*
SQLyog Ultimate v10.51 
MySQL - 5.0.45-community-nt-log : Database - finder
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`finder` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `finder`;

/*Table structure for table `comment` */

DROP TABLE IF EXISTS `comment`;

CREATE TABLE `comment` (
  `id` int(11) NOT NULL auto_increment,
  `info_id` int(11) default NULL COMMENT '信息id',
  `sender_id` int(11) default NULL COMMENT '发送用户id',
  `reciver_id` int(11) default NULL COMMENT '接收用户id',
  `msg` varchar(200) default NULL COMMENT '评论内容',
  `send_time` datetime default NULL COMMENT '发送时间',
  `status` int(1) default NULL COMMENT '评论状态:0未读,1已读',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='评论表';

/*Table structure for table `info` */

DROP TABLE IF EXISTS `info`;

CREATE TABLE `info` (
  `id` int(11) NOT NULL auto_increment,
  `begin_time` date default NULL COMMENT '开始日期',
  `end_time` date default NULL COMMENT '结束日期',
  `province` varchar(20) default NULL COMMENT '省/市',
  `city` varchar(30) default NULL COMMENT '市/区',
  `keywords` varchar(50) default NULL COMMENT '关键词,逗号分隔',
  `content` varchar(200) default NULL COMMENT '内容描述',
  `pic` varchar(255) default NULL COMMENT '图片路径,逗号分隔',
  `status` int(1) default NULL COMMENT '信息状态:1活动,2关闭',
  `type` int(1) default NULL COMMENT '信息类型:1丢失,2捡拾',
  `create_time` datetime default NULL COMMENT '创建时间',
  `update_time` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP COMMENT '更新时间',
  `user_id` int(11) default NULL COMMENT '用户编号',
  `close_time` datetime default NULL COMMENT '关闭时间',
  `close_reason` varchar(50) default NULL COMMENT '关闭原因',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='信息表(丢失/捡拾信息表)';

/*Table structure for table `user` */

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `id` int(11) NOT NULL auto_increment,
  `name` varchar(50) default NULL COMMENT '用户名',
  `password` varchar(50) default NULL COMMENT '密码',
  `email` varchar(50) default NULL COMMENT '邮箱',
  `nickname` varchar(50) default NULL COMMENT '昵称',
  `avatar` varchar(50) default NULL COMMENT '头像图片地址',
  `sex` int(1) default NULL COMMENT '性别:0,保密,1男,2女',
  `cellphone` varchar(11) default NULL COMMENT '手机号',
  `register_time` datetime default NULL COMMENT '注册时间',
  `user_token` varchar(32) default NULL COMMENT '系统内部用户令牌',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='注册用户表';

/*Table structure for table `user_collect` */

DROP TABLE IF EXISTS `user_collect`;

CREATE TABLE `user_collect` (
  `id` int(11) NOT NULL auto_increment,
  `user_id` int(11) default NULL COMMENT '用户id',
  `info_id` int(11) default NULL COMMENT '信息id',
  `create_time` datetime default NULL COMMENT '创建时间',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户收藏信息表';

/*Table structure for table `user_log` */

DROP TABLE IF EXISTS `user_log`;

CREATE TABLE `user_log` (
  `id` int(11) NOT NULL auto_increment,
  `user_id` int(11) default NULL COMMENT '用户id',
  `create_time` datetime default NULL COMMENT '操作时间',
  `operate` int(1) default NULL COMMENT '操作类型:',
  `remark` varchar(50) default NULL COMMENT '备注',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户操作日志表';

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
