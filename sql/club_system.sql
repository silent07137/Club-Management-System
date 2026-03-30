/*
 Navicat Premium Dump SQL

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80045 (8.0.45)
 Source Host           : localhost:3306
 Source Schema         : club_system

 Target Server Type    : MySQL
 Target Server Version : 80045 (8.0.45)
 File Encoding         : 65001

 Date: 30/03/2026 23:17:25
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for activity
-- ----------------------------
DROP TABLE IF EXISTS `activity`;
CREATE TABLE `activity`  (
  `activity_id` bigint NOT NULL AUTO_INCREMENT COMMENT '活动ID',
  `club_id` bigint NOT NULL COMMENT '所属社团ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '活动标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '活动详情',
  `location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '活动地点',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `max_participants` int NULL DEFAULT 0 COMMENT '人数上限(0为不限)',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态: 0未开始, 1进行中, 2已结束',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  PRIMARY KEY (`activity_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '活动表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for club
-- ----------------------------
DROP TABLE IF EXISTS `club`;
CREATE TABLE `club`  (
  `club_id` bigint NOT NULL AUTO_INCREMENT COMMENT '社团ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '社团名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '社团简介',
  `leader_id` bigint NOT NULL COMMENT '社长ID',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态: 0待审核, 1已通过, 2已拒绝',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`club_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '社团表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for club_member
-- ----------------------------
DROP TABLE IF EXISTS `club_member`;
CREATE TABLE `club_member`  (
  `member_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `club_id` bigint NOT NULL COMMENT '所属社团ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_type` tinyint NOT NULL DEFAULT 3 COMMENT '角色类型: 1社长, 2管理组(干事), 3普通成员',
  `join_status` tinyint NULL DEFAULT 1 COMMENT '加入状态: 0申请中, 1已加入, 2已拒绝',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入/申请时间',
  PRIMARY KEY (`member_id`) USING BTREE,
  UNIQUE INDEX `uk_club_user`(`club_id` ASC, `user_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '社团成员关系表(用户组)' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for notification
-- ----------------------------
DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification`  (
  `notify_id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '接收用户ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '消息内容',
  `type` tinyint NULL DEFAULT 1 COMMENT '类型: 1系统消息, 2活动提醒, 3积分变动',
  `is_read` tinyint NULL DEFAULT 0 COMMENT '0未读, 1已读',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`notify_id`) USING BTREE,
  INDEX `idx_user_read`(`user_id` ASC, `is_read` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统通知表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for point_record
-- ----------------------------
DROP TABLE IF EXISTS `point_record`;
CREATE TABLE `point_record`  (
  `record_id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `points_change` int NOT NULL COMMENT '积分变动(正负)',
  `reason` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '变动原因(如:报名活动/签到)',
  `related_id` bigint NULL DEFAULT NULL COMMENT '关联业务ID(如活动ID)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`record_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '积分流水表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for registration
-- ----------------------------
DROP TABLE IF EXISTS `registration`;
CREATE TABLE `registration`  (
  `reg_id` bigint NOT NULL AUTO_INCREMENT COMMENT '报名ID',
  `activity_id` bigint NOT NULL COMMENT '活动ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态: 1已报名, 2已取消, 3已签到',
  `sign_time` datetime NULL DEFAULT NULL COMMENT '签到时间',
  `reg_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '报名时间',
  PRIMARY KEY (`reg_id`) USING BTREE,
  UNIQUE INDEX `uk_act_user`(`activity_id` ASC, `user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '报名记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `student_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学号',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '姓名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码(加密后)',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像URL',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'student' COMMENT '角色: student/leader/admin',
  `global_role` tinyint NULL DEFAULT 1 COMMENT '系统角色: 0超级管理员, 1普通学生',
  `points` int NULL DEFAULT 0 COMMENT '积分',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `uk_student_id`(`student_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
