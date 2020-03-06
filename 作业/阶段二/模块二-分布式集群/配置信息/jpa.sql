/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80017
 Source Host           : localhost:3306
 Source Schema         : jpa

 Target Server Type    : MySQL
 Target Server Version : 80017
 File Encoding         : 65001

 Date: 04/03/2020 23:56:33
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tb_resume
-- ----------------------------
DROP TABLE IF EXISTS `tb_resume`;
CREATE TABLE `tb_resume` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `address` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of tb_resume
-- ----------------------------
BEGIN;
INSERT INTO `tb_resume` VALUES (1, '北京', '张三', '131000000');
INSERT INTO `tb_resume` VALUES (2, '上海', '李四', '151000000');
INSERT INTO `tb_resume` VALUES (3, '广州', '王五', '153000000');
INSERT INTO `tb_resume` VALUES (4, '上海', '李六', '152000000');
INSERT INTO `tb_resume` VALUES (5, '杭州', '孙七', '171000000');
INSERT INTO `tb_resume` VALUES (6, '北京', '赵八', '132000000');
INSERT INTO `tb_resume` VALUES (7, '重庆', '周九', '172000000');
INSERT INTO `tb_resume` VALUES (28, '广州', '吴二', '176000001');
INSERT INTO `tb_resume` VALUES (32, '杭州', '郑大', '189000000');
INSERT INTO `tb_resume` VALUES (35, 'test', 'test', '111000000');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
