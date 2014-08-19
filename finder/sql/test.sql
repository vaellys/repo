/**
 * 测试表user
 */
CREATE TABLE `user` (
  `id` varchar(50) NOT NULL,
  `username` varchar(18) default NULL,
  `password` varchar(18) default NULL,
  `email` varchar(50) default NULL,
  `name` varchar(18) default NULL,
  `sex` varchar(2) default NULL,
  `birthday` varchar(50) default NULL,
  `address` varchar(500) default NULL,
  `tel` varchar(18) default NULL,
  `qq` varchar(18) default NULL,
  `image` varchar(50) default NULL,
  `sfjh` varchar(1) default NULL,
  `sfzx` varchar(1) default NULL,
  `sfhf` varchar(1) default NULL,
  `sfpl` varchar(1) default NULL,
  `sffx` varchar(1) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;