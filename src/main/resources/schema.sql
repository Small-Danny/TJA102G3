-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: tja102g3
-- ------------------------------------------------------
-- Server version   8.0.36
--
-- Final Corrected Version by Gemini @ 2025-09-22
-- Combines the complete schema with all necessary fixes and sample data.

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+08:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- 建立資料庫: 'tja102g3'
--
DROP DATABASE IF EXISTS `tja102g3`;
CREATE DATABASE IF NOT EXISTS `tja102g3` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `tja102g3`;

--
-- Table structure for table `users`
--
DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client       = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
                         `user_id` int NOT NULL AUTO_INCREMENT COMMENT '流水號PK',
                         `email` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '帳號',
                         `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密碼',
                         `account_status` tinyint NOT NULL COMMENT '帳號狀態 (0:停用, 1:啟用)',
                         `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '帳號建立時間',
                         `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新時間',
                         `reset_password_token` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '重設密碼權杖',
                         `token_expiry_date` datetime DEFAULT NULL COMMENT '權杖到期時間',
                         `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '姓名',
                         `nick_name` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '暱稱',
                         `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '電話',
                         `profile_picture` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '頭像',
                         `gender` int DEFAULT NULL COMMENT '性別 (0:不提供, 1:男, 2:女)',
                         `height_cm` decimal(5,2) DEFAULT NULL COMMENT '身高(公分)',
                         `weight_kg` decimal(5,2) DEFAULT NULL COMMENT '體重(公斤)',
                         `bmi` decimal(5,2) DEFAULT NULL COMMENT 'BMI',
                         `points_balance` int NOT NULL DEFAULT '0' COMMENT '點數餘額',
                         PRIMARY KEY (`user_id`),
                         UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='使用者主資料表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `admins`
--
DROP TABLE IF EXISTS `admins`;
/*!40101 SET @saved_cs_client       = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admins` (
                          `admin_id` int NOT NULL AUTO_INCREMENT COMMENT '管理員流水號',
                          `user_id` int NOT NULL COMMENT '對應的使用者ID',
                          `last_login_at` datetime DEFAULT NULL COMMENT '最後登入時間',
                          `admins_created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '帳號建立時間',
                          `account` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '管理員專用帳號',
                          `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '管理員專用密碼',
                          PRIMARY KEY (`admin_id`),
                          UNIQUE KEY `user_id` (`user_id`),
                          UNIQUE KEY `account` (`account`),
                          CONSTRAINT `admins_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理員';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `forum_type`
--
DROP TABLE IF EXISTS `forum_type`;
/*!40101 SET @saved_cs_client       = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `forum_type` (
                              `forum_type_id` int NOT NULL AUTO_INCREMENT COMMENT '流水號PK',
                              `forum_type_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分類名稱',
                              PRIMARY KEY (`forum_type_id`),
                              UNIQUE KEY `forum_type_name` (`forum_type_name`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='討論區分類';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `article`
--
DROP TABLE IF EXISTS `article`;
/*!40101 SET @saved_cs_client       = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `article` (
                           `article_id` int NOT NULL AUTO_INCREMENT COMMENT '流水號PK',
                           `user_id` int NOT NULL COMMENT '會員ID',
                           `forum_type_id` int NOT NULL COMMENT '分類ID',
                           `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '標題',
                           `content` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文章內容',
                           `cover_image_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '封面圖片URL',
                           `article_attribute` enum('一般文章','公告') COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文章屬性',
                           `is_pinned` tinyint NOT NULL DEFAULT '0' COMMENT '是否置頂 (0:否, 1:是)',
                           `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '是否已刪除 (0:否, 1:是)',
                           `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '建立時間',
                           `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新時間',
                           `views` int NOT NULL DEFAULT 0 COMMENT '瀏覽'
                           PRIMARY KEY (`article_id`),
                           KEY `user_id` (`user_id`),
                           KEY `forum_type_id` (`forum_type_id`),
                           CONSTRAINT `article_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
                           CONSTRAINT `article_ibfk_2` FOREIGN KEY (`forum_type_id`) REFERENCES `forum_type` (`forum_type_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `article_collection`
--
DROP TABLE IF EXISTS `article_collection`;
/*!40101 SET @saved_cs_client       = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `article_collection` (
                                      `collection_id` int NOT NULL AUTO_INCREMENT COMMENT '流水號PK',
                                      `user_id` int NOT NULL COMMENT '收藏人會員ID',
                                      `article_id` int NOT NULL COMMENT '被收藏文章ID',
                                      `collect_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏時間',
                                      `collection_status` int NOT NULL DEFAULT '1' COMMENT '收藏狀態 (0:取消收藏, 1:已收藏)',
                                      PRIMARY KEY (`collection_id`),
                                      UNIQUE KEY `user_article` (`user_id`,`article_id`),
                                      KEY `article_id` (`article_id`),
                                      CONSTRAINT `article_collection_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
                                      CONSTRAINT `article_collection_ibfk_2` FOREIGN KEY (`article_id`) REFERENCES `article` (`article_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章收藏表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `report_type`
--
DROP TABLE IF EXISTS `report_type`;
/*!40101 SET @saved_cs_client       = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `report_type` (
                               `report_type_id` int NOT NULL AUTO_INCREMENT COMMENT '流水號PK',
                               `report_type_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '類型名稱',
                               PRIMARY KEY (`report_type_id`),
                               UNIQUE KEY `report_type_name` (`report_type_name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='檢舉類型表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `report_status`
--
DROP TABLE IF EXISTS `report_status`;
/*!40101 SET @saved_cs_client       = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `report_status` (
                                 `report_status` int NOT NULL COMMENT '處理狀態ID',
                                 `status_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '狀態名稱',
                                 PRIMARY KEY (`report_status`),
                                 UNIQUE KEY `status_name` (`status_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='檢舉處理狀態表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `article_report`
--
DROP TABLE IF EXISTS `article_report`;
/*!40101 SET @saved_cs_client       = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `article_report` (
                                  `report_id` int NOT NULL AUTO_INCREMENT COMMENT '流水號PK',
                                  `user_id` int NOT NULL COMMENT '檢舉人會員ID',
                                  `article_id` int NOT NULL COMMENT '被檢舉文章ID',
                                  `report_type_id` int NOT NULL COMMENT '檢舉類型ID',
                                  `reason` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '檢舉原因補充',
                                  `report_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '檢舉時間',
                                  `report_status` int NOT NULL DEFAULT '0' COMMENT '處理狀態',
                                  PRIMARY KEY (`report_id`),
                                  KEY `user_id` (`user_id`),
                                  KEY `article_id` (`article_id`),
                                  KEY `report_type_id` (`report_type_id`),
                                  KEY `report_status` (`report_status`),
                                  CONSTRAINT `article_report_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
                                  CONSTRAINT `article_report_ibfk_2` FOREIGN KEY (`article_id`) REFERENCES `article` (`article_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
                                  CONSTRAINT `article_report_ibfk_3` FOREIGN KEY (`report_type_id`) REFERENCES `report_type` (`report_type_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
                                  CONSTRAINT `article_report_ibfk_4` FOREIGN KEY (`report_status`) REFERENCES `report_status` (`report_status`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='檢舉處理表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `product`
--
DROP TABLE IF EXISTS `product`;
/*!40101 SET @saved_cs_client       = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product` (
                           `product_id` int NOT NULL AUTO_INCREMENT COMMENT '商品編號',
                           `product_type` tinyint NOT NULL COMMENT '商品類型(0: 裝備, 1:配件, 2:補充劑)',
                           `product_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商品名稱',
                           `product_description` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商品敘述 (含規格)',
                           `product_price` int NOT NULL COMMENT '價格',
                           `stock_quantity` int NOT NULL COMMENT '庫存',
                           `product_picture` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商品圖片',
                           `product_status` tinyint NOT NULL COMMENT '商品狀態(0: 下架, 1:上架)',
                           `product_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商品編號或SKU',
                           PRIMARY KEY (`product_id`),
                           UNIQUE KEY `product_code` (`product_code`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cart_item`
--
DROP TABLE IF EXISTS `cart_item`;
/*!40101 SET @saved_cs_client       = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart_item` (
                             `cart_item_id` int NOT NULL AUTO_INCREMENT COMMENT '購物車明細流水號',
                             `product_id` int DEFAULT NULL COMMENT '商品編號',
                             `user_id` int NOT NULL COMMENT '使用者流水號',
                             `cart_item_quantity` int NOT NULL COMMENT '數量',
                             PRIMARY KEY (`cart_item_id`),
                             KEY `product_id` (`product_id`),
                             KEY `user_id` (`user_id`),
                             CONSTRAINT `cart_item_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`) ON DELETE SET NULL ON UPDATE CASCADE,
                             CONSTRAINT `cart_item_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='購物車明細';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `orders`
--
DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client       = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
                          `order_id` int NOT NULL AUTO_INCREMENT COMMENT '訂單流水號',
                          `user_id` int NOT NULL COMMENT '使用者流水號',
                          `order_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下訂日期',
                          `order_status` tinyint NOT NULL DEFAULT '0' COMMENT '出貨狀態(0:待出貨, 1:待收貨, 2:已收貨)',
                          `recipient_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '收貨人姓名',
                          `recipient_phone` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '收貨人電話',
                          `recipient_address` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '收貨人地址',
                          `used_points_amount` int NOT NULL DEFAULT '0' COMMENT '訂單使用的點數',
                          `total_price` int NOT NULL COMMENT '總價',
                          `payment_time` timestamp NULL DEFAULT NULL COMMENT '付款時間',
                          `payment_status` tinyint NOT NULL DEFAULT '0' COMMENT '付款狀態 (0: 待付款, 1: 付款成功, 2: 付款失敗)',
                          `order_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '訂單代碼',
                          PRIMARY KEY (`order_id`),
                          UNIQUE KEY `order_code` (`order_code`),
                          KEY `user_id` (`user_id`),
                          CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='訂單表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `order_item`
--
DROP TABLE IF EXISTS `order_item`;
/*!40101 SET @saved_cs_client       = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_item` (
                              `order_item_id` int NOT NULL AUTO_INCREMENT COMMENT '訂單明細ID',
                              `order_id` int NOT NULL COMMENT '訂單ID',
                              `product_id` int NOT NULL COMMENT '商品ID',
                              `quantity` int NOT NULL COMMENT '商品數量',
                              `buy_price` int NOT NULL COMMENT '購買時單價',
                              `item_total_price` int NOT NULL COMMENT '此項商品總價',
                              `order_item_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '訂單明細代碼',
                              PRIMARY KEY (`order_item_id`),
                              UNIQUE KEY `order_item_code` (`order_item_code`),
                              KEY `order_id` (`order_id`),
                              KEY `product_id` (`product_id`),
                              CONSTRAINT `order_item_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
                              CONSTRAINT `order_item_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='訂單明細表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `task_type`
--
DROP TABLE IF EXISTS `task_type`;
/*!40101 SET @saved_cs_client       = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `task_type` (
                             `task_type_id` int NOT NULL AUTO_INCREMENT COMMENT '任務類型 ID',
                             `task_type_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任務類型名稱',
                             `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '建立時間',
                             `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新時間',
                             PRIMARY KEY (`task_type_id`),
                             UNIQUE KEY `task_type_name` (`task_type_name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任務類型表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `task`
--
DROP TABLE IF EXISTS `task`;
/*!40101 SET @saved_cs_client       = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `task` (
                        `task_id` int NOT NULL AUTO_INCREMENT COMMENT '任務流水號',
                        `task_type_id` int NOT NULL COMMENT '對應任務類型',
                        `task_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任務名稱',
                        `target_value` int NOT NULL COMMENT '任務目標值（如：8000 卡、3 次）',
                        `unit` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '單位（如次、分鐘、大卡）',
                        `start_time` date NOT NULL COMMENT '任務開始日',
                        `end_time` date NOT NULL COMMENT '任務結束日',
                        `points` tinyint NOT NULL DEFAULT '0' COMMENT '點數',
                        `task_icon` varchar(2083) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任務圖騰（URL）',
                        `admin_id` int NOT NULL COMMENT '任務建立人員 ID',
                        PRIMARY KEY (`task_id`),
                        KEY `idx_task_type_id` (`task_type_id`),
                        KEY `idx_admin_id` (`admin_id`),
                        KEY `idx_start_end` (`start_time`,`end_time`),
                        CONSTRAINT `fk_task_admins` FOREIGN KEY (`admin_id`) REFERENCES `admins` (`admin_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
                        CONSTRAINT `fk_task_task_type` FOREIGN KEY (`task_type_id`) REFERENCES `task_type` (`task_type_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任務表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `task_record_status_code`
--
DROP TABLE IF EXISTS `task_record_status_code`;
/*!40101 SET @saved_cs_client       = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `task_record_status_code` (
                                           `task_record_status` int NOT NULL COMMENT '任務狀態代碼',
                                           `status_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任務狀態名稱',
                                           PRIMARY KEY (`task_record_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='使用者任務狀態代碼表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `task_record`
--
DROP TABLE IF EXISTS `task_record`;
/*!40101 SET @saved_cs_client       = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `task_record` (
                               `task_record_id` int NOT NULL AUTO_INCREMENT COMMENT '任務參與紀錄 ID',
                               `user_id` int NOT NULL COMMENT '使用者流水號',
                               `task_id` int NOT NULL COMMENT '任務流水號',
                               `task_record_status` int NOT NULL COMMENT '任務狀態',
                               `user_start_time` datetime NOT NULL COMMENT '使用者開始時間',
                               `user_end_time` datetime DEFAULT NULL COMMENT '使用者結束時間（若未完成可為 NULL）',
                               PRIMARY KEY (`task_record_id`),
                               KEY `idx_user_id` (`user_id`),
                               KEY `idx_task_id` (`task_id`),
                               KEY `idx_task_record_status` (`task_record_status`),
                               CONSTRAINT `fk_task_record_status` FOREIGN KEY (`task_record_status`) REFERENCES `task_record_status_code` (`task_record_status`) ON DELETE RESTRICT ON UPDATE CASCADE,
                               CONSTRAINT `fk_task_record_task` FOREIGN KEY (`task_id`) REFERENCES `task` (`task_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
                               CONSTRAINT `fk_task_record_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='使用者任務紀錄表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `points_log`
--
DROP TABLE IF EXISTS `points_log`;
/*!40101 SET @saved_cs_client       = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `points_log` (
                              `log_id` int NOT NULL AUTO_INCREMENT COMMENT '流水號PK',
                              `user_id` int NOT NULL COMMENT '使用者ID',
                              `transaction_type` tinyint NOT NULL COMMENT '交易類型 (0:獲得, 1:使用)',
                              `points_amount` int NOT NULL COMMENT '點數數量',
                              `task_id` int DEFAULT NULL COMMENT '關聯任務ID',
                              `order_id` int DEFAULT NULL COMMENT '關聯訂單ID',
                              `transaction_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '交易時間',
                              PRIMARY KEY (`log_id`),
                              KEY `user_id` (`user_id`),
                              KEY `task_id` (`task_id`),
                              KEY `order_id` (`order_id`),
                              CONSTRAINT `points_log_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
                              CONSTRAINT `points_log_ibfk_2` FOREIGN KEY (`task_id`) REFERENCES `task` (`task_id`) ON DELETE SET NULL ON UPDATE CASCADE,
                              CONSTRAINT `points_log_ibfk_3` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='點數交易紀錄表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sport_type`
--
DROP TABLE IF EXISTS `sport_type`;
/*!40101 SET @saved_cs_client       = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sport_type` (
                              `sport_type_id` int NOT NULL AUTO_INCREMENT COMMENT '運動分類ID',
                              `sport_type_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '運動分類名稱',
                              `sport_type_pic` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '運動分類圖片',
                              PRIMARY KEY (`sport_type_id`),
                              UNIQUE KEY `sport_type_name` (`sport_type_name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='運動分類表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sport`
--
DROP TABLE IF EXISTS `sport`;
/*!40101 SET @saved_cs_client       = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sport` (
                         `sport_id` int NOT NULL AUTO_INCREMENT COMMENT '系統運動項目ID',
                         `sport_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '運動名稱',
                         `sport_description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '運動描述',
                         `sport_mets` decimal(4,2) NOT NULL COMMENT 'METs運動強度',
                         `sport_estimated_calories` int unsigned NOT NULL COMMENT '預估運動消耗熱量(30分鐘)',
                         `sport_level` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '運動等級',
                         `sport_pic` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '運動圖片',
                         `sport_data_status` tinyint NOT NULL COMMENT '運動資料狀態(0:停用, 1:啟用)',
                         `admin_id` int NOT NULL COMMENT '管理員ID',
                         PRIMARY KEY (`sport_id`),
                         UNIQUE KEY `sport_name` (`sport_name`),
                         KEY `fk_sport_admin_id` (`admin_id`),
                         CONSTRAINT `fk_sport_admin_id` FOREIGN KEY (`admin_id`) REFERENCES `admins` (`admin_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系統運動項目表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sport_type_item`
--
DROP TABLE IF EXISTS `sport_type_item`;
/*!40101 SET @saved_cs_client       = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sport_type_item` (
                                   `sport_type_item_id` int NOT NULL AUTO_INCREMENT COMMENT '運動分類明細ID',
                                   `sport_type_id` int NOT NULL COMMENT '運動分類ID',
                                   `sport_id` int NOT NULL COMMENT '系統運動項目ID',
                                   PRIMARY KEY (`sport_type_item_id`),
                                   UNIQUE KEY `sport_type_sport` (`sport_type_id`,`sport_id`),
                                   KEY `fk_sport_type_item_sport_id` (`sport_id`),
                                   CONSTRAINT `fk_sport_type_id` FOREIGN KEY (`sport_type_id`) REFERENCES `sport_type` (`sport_type_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
                                   CONSTRAINT `fk_sport_type_item_sport_id` FOREIGN KEY (`sport_id`) REFERENCES `sport` (`sport_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='運動分類明細表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `custom_sport`
--
DROP TABLE IF EXISTS `custom_sport`;
/*!40101 SET @saved_cs_client       = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `custom_sport` (
                                `custom_sport_id` int NOT NULL AUTO_INCREMENT COMMENT '自訂義運動ID',
                                `sport_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '運動名稱',
                                `sport_description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '運動描述',
                                `sport_estimated_calories` int unsigned NOT NULL COMMENT '預估運動消耗熱量(30分鐘)',
                                `sport_pic` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '運動圖片',
                                `sport_data_status` tinyint NOT NULL COMMENT '運動資料狀態 (0:刪除, 1:正常)',
                                `user_id` int NOT NULL COMMENT '會員ID',
                                PRIMARY KEY (`custom_sport_id`),
                                KEY `fk_custom_sport_user_id` (`user_id`),
                                CONSTRAINT `fk_custom_sport_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='使用者自訂義運動項目表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `workout_plan`
--
DROP TABLE IF EXISTS `workout_plan`;
/*!40101 SET @saved_cs_client       = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `workout_plan` (
                                `workout_plan_id` int NOT NULL AUTO_INCREMENT COMMENT '運動計畫ID',
                                `user_id` int NOT NULL COMMENT '使用者ID',
                                `sport_from` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '運動項目來源(system, custom)',
                                `sport_id` int DEFAULT NULL COMMENT '系統運動項目ID',
                                `custom_sport_id` int DEFAULT NULL COMMENT '自訂義運動項目ID',
                                `workout_plan_status` tinyint NOT NULL COMMENT '計畫狀態(0:未完成, 1:已完成)',
                                `workout_plan_date` date NOT NULL COMMENT '計畫安排日期',
                                `workout_plan_notify_time` time DEFAULT NULL COMMENT '計畫提醒通知時間',
                                `workout_plan_expected_duration` int unsigned NOT NULL COMMENT '計畫預期執行總時長(分鐘)',
                                `actual_total_count` int unsigned NOT NULL DEFAULT '0' COMMENT '實際執行總次數',
                                `actual_total_duration` int unsigned NOT NULL DEFAULT '0' COMMENT '實際執行總時長(分鐘)',
                                `actual_total_calories` int unsigned NOT NULL DEFAULT '0' COMMENT '實際執行總消耗卡路里',
                                `workout_plan_data_status` tinyint NOT NULL DEFAULT '1' COMMENT '計畫資料狀態(0:刪除, 1:正常)',
                                `workout_plan_update_datetime` datetime DEFAULT NULL COMMENT '計畫最近一次更新日期時間',
                                `task_record_id` int DEFAULT NULL COMMENT '任務參與紀錄ID',
                                PRIMARY KEY (`workout_plan_id`),
                                KEY `fk_workout_plan_user_id` (`user_id`),
                                KEY `fk_workout_plan_sport_id` (`sport_id`),
                                KEY `fk_workout_plan_custom_sport_id` (`custom_sport_id`),
                                KEY `fk_workout_plan_task_record_id` (`task_record_id`),
                                CONSTRAINT `fk_workout_plan_custom_sport_id` FOREIGN KEY (`custom_sport_id`) REFERENCES `custom_sport` (`custom_sport_id`) ON DELETE SET NULL ON UPDATE CASCADE,
                                CONSTRAINT `fk_workout_plan_sport_id` FOREIGN KEY (`sport_id`) REFERENCES `sport` (`sport_id`) ON DELETE SET NULL ON UPDATE CASCADE,
                                CONSTRAINT `fk_workout_plan_task_record_id` FOREIGN KEY (`task_record_id`) REFERENCES `task_record` (`task_record_id`) ON DELETE SET NULL ON UPDATE CASCADE,
                                CONSTRAINT `fk_workout_plan_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='運動計畫表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `workout_plan_record`
--
DROP TABLE IF EXISTS `workout_plan_record`;
/*!40101 SET @saved_cs_client       = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `workout_plan_record` (
                                       `workout_plan_record_id` int NOT NULL AUTO_INCREMENT COMMENT '實際執行紀錄ID',
                                       `workout_plan_id` int NOT NULL COMMENT '運動計畫ID',
                                       `sport_from` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '運動項目來源(system, custom)',
                                       `sport_id` int DEFAULT NULL COMMENT '系統運動項目ID',
                                       `custom_sport_id` int DEFAULT NULL COMMENT '自訂義運動項目ID',
                                       `actual_calories` int unsigned NOT NULL COMMENT '實際消耗卡路里',
                                       `actual_start_time` datetime NOT NULL COMMENT '實際執行開始時間',
                                       `actual_end_time` datetime NOT NULL COMMENT '實際執行結束時間',
                                       `actual_duration` int unsigned NOT NULL COMMENT '實際執行時長(分鐘)',
                                       `actual_record_datetime` datetime NOT NULL COMMENT '實際做紀錄的日期時間',
                                       `workout_plan_record_data_status` tinyint NOT NULL COMMENT '紀錄資料狀態(0:刪除, 1:正常)',
                                       PRIMARY KEY (`workout_plan_record_id`),
                                       KEY `fk_workout_plan_record_workout_plan_id` (`workout_plan_id`),
                                       KEY `fk_workout_plan_record_sport_id` (`sport_id`),
                                       KEY `fk_workout_plan_record_custom_sport_id` (`custom_sport_id`),
                                       CONSTRAINT `fk_workout_plan_record_custom_sport_id` FOREIGN KEY (`custom_sport_id`) REFERENCES `custom_sport` (`custom_sport_id`) ON DELETE SET NULL ON UPDATE CASCADE,
                                       CONSTRAINT `fk_workout_plan_record_sport_id` FOREIGN KEY (`sport_id`) REFERENCES `sport` (`sport_id`) ON DELETE SET NULL ON UPDATE CASCADE,
                                       CONSTRAINT `fk_workout_plan_record_workout_plan_id` FOREIGN KEY (`workout_plan_id`) REFERENCES `workout_plan` (`workout_plan_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='運動計畫實際執行紀錄表';
/*!40101 SET character_set_client = @saved_cs_client */;


-- =================================================================
-- 假資料 (Sample Data)
-- =================================================================

--
-- Dumping data for table `users`
--
INSERT INTO `users` (`user_id`, `email`, `password`, `account_status`, `name`, `nick_name`, `phone`, `gender`, `height_cm`, `weight_kg`, `bmi`, `points_balance`) VALUES
                                                                                                                                                                      (1, 'user01@example.com', 'user123456', 1, '王小明', '小明', '0911222333', 1, 175.50, 70.00, 22.86, 19),
                                                                                                                                                                      (2, 'user02@example.com', 'user123456', 1, '陳大華', '大華', '0922333444', 1, 168.00, 65.50, 23.20, 8),
                                                                                                                                                                      (3, 'user03@example.com', 'user123456', 1, '林美君', '美美', '0933444555', 2, 162.00, 55.00, 20.96, 6),
                                                                                                                                                                      (4, 'user04@example.com', 'user123456', 1, '張志遠', '小張', '0944555666', 1, 180.50, 80.20, 24.58, 9),
                                                                                                                                                                      (5, 'user05@example.com', 'user123456', 1, '黃玉珍', '小黃', '0955666777', 2, 158.00, 52.30, 20.97, 4),
                                                                                                                                                                      (6, 'user06@example.com', 'user123456', 1, '劉文傑', '文傑', '0966777888', 1, 170.00, 72.80, 25.19, 10),
                                                                                                                                                                      (7, 'user07@example.com', 'user123456', 1, '徐麗娟', '麗麗', '0977888999', 2, 165.00, 60.00, 22.04, 8),
                                                                                                                                                                      (8, 'user08@example.com', 'user123456', 1, '趙偉宏', '阿宏', '0988999000', 1, 183.00, 85.00, 25.40, 0),
                                                                                                                                                                      (9, 'admin01@example.com', 'user123456', 1, '系統管理員', 'Admin', '0900111222', 0, NULL, NULL, NULL, 0),
                                                                                                                                                                      (10, 'admin02@example.com', 'user123456', 1, '李志強', '小李', '0933444555', 1, 178.00, 75.00, 23.67, 0);

--
-- Dumping data for table `admins`
--
INSERT INTO `admins` (`admin_id`, `user_id`, `last_login_at`, `account`, `password`) VALUES
                                                                                         (1, 9, NOW(), 'system_admin01', 'admin123456'),
                                                                                         (2, 10, '2025-08-18 10:00:00', 'system_admin02', 'admin123456');

--
-- Dumping data for table `product`
--
INSERT INTO `product` (`product_id`, `product_type`, `product_name`, `product_description`, `product_price`, `stock_quantity`, `product_picture`, `product_status`, `product_code`) VALUES
                                                                                                                                                                                        (1, 0, 'TibaFit 衣服 S號', '吸濕排汗材質，尺寸：S', 700, 50, 'clothes.png', 1, 'EQ-C-S'),
                                                                                                                                                                                        (2, 0, 'TibaFit 衣服 M號', '吸濕排汗材質，尺寸：M', 700, 80, 'clothes.png', 1, 'EQ-C-M'),
                                                                                                                                                                                        (3, 0, 'TibaFit 衣服 L號', '吸濕排汗材質，尺寸：L', 700, 70, 'clothes.png', 1, 'EQ-C-L'),
                                                                                                                                                                                        (4, 0, 'TibaFit 衣服 XL號', '吸濕排汗材質，尺寸：XL', 1200, 40, 'clothes.png', 1, 'EQ-C-XL'),
                                                                                                                                                                                        (5, 0, 'TibaFit 褲子 S號', '高彈性面料，尺寸：S', 700, 40, 'pants.png', 1, 'EQ-P-S'),
                                                                                                                                                                                        (6, 0, 'TibaFit 褲子 M號', '高彈性面料，尺寸：M', 700, 60, 'pants.png', 1, 'EQ-P-M'),
                                                                                                                                                                                        (7, 0, 'TibaFit 褲子 L號', '高彈性面料，尺寸：L', 700, 50, 'pants.png', 1, 'EQ-P-L'),
                                                                                                                                                                                        (8, 0, 'TibaFit 褲子 XL號', '高彈性面料，尺寸：XL', 900, 30, 'pants.png', 1, 'EQ-P-XL'),
                                                                                                                                                                                        (9, 0, 'TibaFit 運動手套', '止滑耐磨，均碼', 500, 200, 'gloves.png', 1, 'EQ-G-01'),
                                                                                                                                                                                        (10, 0, 'TibaFit 運動腰帶', '核心支撐，均碼', 1200, 100, 'waist-belt.png', 1, 'EQ-WB-01'),
                                                                                                                                                                                        (11, 0, 'TibaFit 運動護膝', '支撐膝關節，均碼', 800, 100, 'knee-brace.png', 1, 'EQ-KB-01'),
                                                                                                                                                                                        (12, 1, 'TibaFit 搖搖杯', '容量：500 ml', 200, 120, 'shaker.png', 1, 'ACC-S-500'),
                                                                                                                                                                                        (13, 1, 'TibaFit 搖搖杯', '容量：700 ml', 400, 100, 'shaker.png', 1, 'ACC-S-700'),
                                                                                                                                                                                        (14, 2, 'TibaFit 肌酸', '重量：500g', 650, 150, 'creatine.png', 1, 'SUP-C-500'),
                                                                                                                                                                                        (15, 2, 'TibaFit 肌酸', '重量：1kg', 1300, 100, 'creatine.png', 1, 'SUP-C-1000'),
                                                                                                                                                                                        (16, 2, 'TibaFit 乳清蛋白', '重量：500g', 700, 200, 'whey.png', 1, 'SUP-W-500'),
                                                                                                                                                                                        (17, 2, 'TibaFit 乳清蛋白', '重量：1kg', 1400, 150, 'whey.png', 1, 'SUP-W-1000'),
                                                                                                                                                                                        (18, 2, 'TibaFit BCAA', '重量：500g', 650, 200, 'bcaa.png', 1, 'SUP-B-500'),
                                                                                                                                                                                        (19, 2, 'TibaFit BCAA', '重量：1kg', 1300, 150, 'bcaa.png', 1, 'SUP-B-1000');

--
-- Dumping data for table `cart_item`
--
INSERT INTO `cart_item` (`cart_item_id`, `product_id`, `user_id`, `cart_item_quantity`) VALUES
                                                                                            (1, 1, 1, 1),
                                                                                            (2, 2, 1, 2),
                                                                                            (3, 14, 2, 3),
                                                                                            (4, 16, 2, 1);

--
-- Dumping data for table `orders`
--
INSERT INTO `orders` (`order_id`, `user_id`, `order_date`, `order_status`, `recipient_name`, `recipient_phone`, `recipient_address`, `used_points_amount`, `total_price`, `payment_time`, `payment_status`, `order_code`) VALUES
                                                                                                                                                                                                                              (1, 3, '2025-08-15 10:00:00', 2, '林美君', '0933444555', '台北市中山區南京東路三段219號', 6, 1200, '2025-08-15 10:05:00', 1, 'ORD-20250815-001'),
                                                                                                                                                                                                                              (2, 4, '2025-08-16 14:30:00', 0, '張志遠', '0944555666', '台中市西屯區台灣大道三段301號', 0, 800, '2025-08-16 14:35:00', 1, 'ORD-20250816-002'),
                                                                                                                                                                                                                              (3, 5, '2025-08-17 11:00:00', 1, '黃玉珍', '0955666777', '高雄市苓雅區四維三路2號', 4, 1500, '2025-08-17 11:05:00', 1, 'ORD-20250817-003'),
                                                                                                                                                                                                                              (4, 6, '2025-08-18 09:15:00', 0, '劉文傑', '0966777888', '桃園市中壢區中正路100號', 0, 1400, '2025-08-18 09:20:00', 1, 'ORD-20250818-004'),
                                                                                                                                                                                                                              (5, 7, '2025-08-19 16:45:00', 0, '徐麗娟', '0977888999', '新北市板橋區縣民大道一段1號', 0, 850, '2025-08-19 16:50:00', 1, 'ORD-20250819-005');

-- 【新增】一筆模擬剛成立、要去付款的訂單。注意此筆 INSERT 沒有 `payment_time`, `payment_status`, `used_points_amount` 欄位，它們會自動填上 NULL 和 0
INSERT INTO `orders` (`user_id`, `order_date`, `order_status`, `recipient_name`, `recipient_phone`, `recipient_address`, `total_price`, `order_code`) VALUES
    (8, NOW(), 0, '趙偉宏', '0988999000', '新竹市東區大學路1001號', 2600, 'ORD-20250922-006');

--
-- Dumping data for table `order_item`
--
INSERT INTO `order_item` (`order_item_id`, `order_id`, `product_id`, `quantity`, `buy_price`, `item_total_price`, `order_item_code`) VALUES
                                                                                                                                         (1, 1, 1, 1, 700, 700, 'OIT-001'),
                                                                                                                                         (2, 1, 9, 1, 500, 500, 'OIT-002'),
                                                                                                                                         (3, 2, 11, 1, 800, 800, 'OIT-003'),
                                                                                                                                         (4, 3, 14, 2, 650, 1300, 'OIT-004'),
                                                                                                                                         (5, 3, 12, 1, 200, 200, 'OIT-005'),
                                                                                                                                         (6, 4, 17, 1, 1400, 1400, 'OIT-006'),
                                                                                                                                         (7, 5, 14, 1, 650, 650, 'OIT-007'),
                                                                                                                                         (8, 5, 12, 1, 200, 200, 'OIT-008'),
                                                                                                                                         (9, 6, 15, 2, 1300, 2600, 'OIT-009');

--
-- Dumping data for table `forum_type`
--
INSERT INTO `forum_type` (`forum_type_id`, `forum_type_name`) VALUES
                                                                  (1, '健身知識分享'),
                                                                  (2, '日常心得交流'),
                                                                  (3, '體育新聞'),
                                                                  (4, '健康飲食專區'),
                                                                  (5, '器材與裝備評測'),
                                                                  (6, '新手入門區');

--
-- Dumping data for table `article`
--
INSERT INTO `article` (`article_id`, `user_id`, `forum_type_id`, `title`, `content`, `cover_image_url`, `article_attribute`, `is_pinned`, `is_deleted`) VALUES
                                                                                                                                                            (1, 1, 1, '新手必看！三大健身基本原則', '這篇文章將介紹適合新手的三大健身原則...', 'images/article/cover1.jpg', '一般文章', 0, 0),
                                                                                                                                                            (2, 2, 1, '如何選擇適合自己的乳清蛋白？', '乳清蛋白的種類繁多，本文教你如何挑選...', 'images/article/cover2.jpg', '一般文章', 0, 0),
                                                                                                                                                            (3, 3, 2, '健身餐分享：簡單又美味的雞胸肉做法', '分享一個我常做的雞胸肉食譜，讓你的健身餐不再單調！', 'images/article/cover3.jpg', '一般文章', 0, 0),
                                                                                                                                                            (4, 4, 1, '深蹲技巧大公開，避免膝蓋受傷', '深蹲是健身之王，但姿勢錯誤容易受傷...', 'images/article/cover4.jpg', '一般文章', 0, 0),
                                                                                                                                                            (5, 5, 2, '運動後恢復的重要性', '運動後千萬別忽略了恢復，這篇教你如何快速恢復...', 'images/article/cover5.jpg', '一般文章', 0, 0),
                                                                                                                                                            (6, 6, 1, '增肌減脂的飲食策略', '想增肌又減脂？你需要掌握正確的飲食策略...', 'images/article/cover6.jpg', '一般文章', 0, 0),
                                                                                                                                                            (7, 7, 3, '最新體育新聞：奧運冠軍的訓練秘訣', '奧運金牌得主在賽後分享了他們的訓練秘訣，值得參考！', 'images/article/news1.jpg', '一般文章', 0, 0),
                                                                                                                                                            (8, 8, 1, '在家也能做的核心訓練', '沒有器材也能練核心，五個動作讓你練出馬甲線！', 'images/article/cover8.jpg', '一般文章', 0, 0),
                                                                                                                                                            (9, 9, 1, '網站公告：論壇新功能上線', '親愛的會員，論壇已新增...。', 'images/article/announcement.jpg', '公告', 1, 0),
                                                                                                                                                            (10, 10, 2, '管理員的心得分享：堅持就是勝利！', '作為管理員，我也和大家一樣...', 'images/article/staff_share.jpg', '一般文章', 0, 0);

--
-- Dumping data for table `article_collection`
--
INSERT INTO `article_collection` (`collection_id`, `user_id`, `article_id`, `collect_time`, `collection_status`) VALUES
                                                                                                                     (1, 1, 2, NOW(), 1),
                                                                                                                     (2, 2, 1, NOW(), 1),
                                                                                                                     (3, 3, 4, NOW(), 1),
                                                                                                                     (4, 4, 3, NOW(), 1),
                                                                                                                     (5, 5, 6, NOW(), 1),
                                                                                                                     (6, 6, 5, NOW(), 1),
                                                                                                                     (7, 7, 8, NOW(), 1),
                                                                                                                     (8, 8, 7, NOW(), 1),
                                                                                                                     (9, 1, 5, NOW(), 1),
                                                                                                                     (10, 2, 6, NOW(), 1);

--
-- Dumping data for table `report_type`
--
INSERT INTO `report_type` (`report_type_id`, `report_type_name`) VALUES
                                                                     (1, '色情內容'),
                                                                     (2, '暴力血腥'),
                                                                     (3, '人身攻擊');

--
-- Dumping data for table `report_status`
--
INSERT INTO `report_status` (`report_status`, `status_name`) VALUES
                                                                 (0, '待處理'),
                                                                 (1, '已處理'),
                                                                 (2, '已駁回'),
                                                                 (3, '無效檢舉');

--
-- Dumping data for table `article_report`
--
INSERT INTO `article_report` (`report_id`, `user_id`, `article_id`, `report_type_id`, `reason`, `report_time`, `report_status`) VALUES
                                                                                                                                    (1, 1, 2, 3, '留言有不雅字眼', NOW(), 0),
                                                                                                                                    (2, 2, 4, 1, '圖片不適合', NOW(), 1),
                                                                                                                                    (3, 1, 6, 2, '內容血腥暴力', NOW(), 0),
                                                                                                                                    (4, 2, 8, 3, '對作者人身攻擊', NOW(), 0),
                                                                                                                                    (5, 3, 1, 1, '內容不符分類', NOW(), 2);

--
-- Dumping data for table `task_type`
--
INSERT INTO `task_type` (`task_type_id`, `task_type_name`) VALUES
                                                               (1, '消耗卡路里'),
                                                               (2, '運動次數'),
                                                               (3, '運動時長');

--
-- Dumping data for table `task`
--
INSERT INTO `task` (`task_id`, `task_type_id`, `task_name`, `target_value`, `unit`, `start_time`, `end_time`, `points`, `task_icon`, `admin_id`) VALUES
                                                                                                                                                     (1, 1, '燃燒 500 大卡', 500, '大卡', '2025-08-01', '2025-08-31', 10, 'https://example.com/icon1.png', 1),
                                                                                                                                                     (2, 3, '游泳 30 分鐘', 30, '分鐘', '2025-08-01', '2025-08-31', 8, 'https://example.com/icon2.png', 2),
                                                                                                                                                     (3, 3, '跑步 30 分鐘', 30, '分鐘', '2025-08-01', '2025-08-31', 12, 'https://example.com/icon3.png', 1),
                                                                                                                                                     (4, 2, '深蹲 40 下', 40, '次', '2025-08-01', '2025-08-31', 9, 'https://example.com/icon4.png', 2);

--
-- Dumping data for table `task_record_status_code`
--
INSERT INTO `task_record_status_code` (`task_record_status`, `status_name`) VALUES
                                                                                (0, '未完成'),
                                                                                (1, '已完成');

--
-- Dumping data for table `task_record`
--
INSERT INTO `task_record` (`task_record_id`, `user_id`, `task_id`, `task_record_status`, `user_start_time`, `user_end_time`) VALUES
                                                                                                                                 (1, 1, 1, 1, '2025-08-01 07:00:00', '2025-08-01 07:40:00'),
                                                                                                                                 (2, 2, 2, 0, '2025-08-02 20:00:00', NULL),
                                                                                                                                 (3, 3, 3, 1, '2025-08-03 18:00:00', '2025-08-03 18:30:00'),
                                                                                                                                 (4, 4, 4, 1, '2025-08-04 06:30:00', '2025-08-04 06:50:00'),
                                                                                                                                 (5, 5, 1, 0, '2025-08-05 07:10:00', NULL),
                                                                                                                                 (6, 6, 1, 1, '2025-08-01 08:00:00', '2025-08-01 08:45:00'),
                                                                                                                                 (7, 7, 2, 1, '2025-08-02 19:00:00', '2025-08-02 19:20:00'),
                                                                                                                                 (8, 8, 3, 0, '2025-08-03 17:30:00', NULL),
                                                                                                                                 (9, 1, 4, 1, '2025-08-04 06:00:00', '2025-08-04 06:25:00'),
                                                                                                                                 (10, 2, 4, 1, '2025-08-05 07:00:00', '2025-08-05 07:55:00');

--
-- Dumping data for table `points_log`
--
INSERT INTO `points_log` (`log_id`, `user_id`, `transaction_type`, `points_amount`, `task_id`, `order_id`) VALUES
                                                                                                               (1, 1, 0, 10, 1, NULL),
                                                                                                               (2, 3, 0, 12, 3, NULL),
                                                                                                               (3, 4, 0, 9, 4, NULL),
                                                                                                               (4, 6, 0, 10, 1, NULL),
                                                                                                               (5, 7, 0, 8, 2, NULL),
                                                                                                               (6, 1, 0, 9, 4, NULL),
                                                                                                               (7, 2, 0, 8, 2, NULL),
                                                                                                               (8, 3, 1, 6, NULL, 1),
                                                                                                               (9, 5, 1, 4, NULL, 3);

--
-- Dumping data for table `sport_type`
--
INSERT INTO `sport_type` (`sport_type_id`, `sport_type_name`) VALUES
                                                                  (2, '有氧'),
                                                                  (1, '重訓');

--
-- Dumping data for table `sport`
--
INSERT INTO `sport` (`sport_id`, `sport_name`, `sport_description`, `sport_mets`, `sport_estimated_calories`, `sport_level`, `sport_pic`, `sport_data_status`, `admin_id`) VALUES
                                                                                                                                                                               (1, '健走(初階)', '慢速散步 約 1 公里/小時，輕鬆散步，可正常交談', 2.00, 150, 'junior', NULL, 1, 1),
                                                                                                                                                                               (2, '健走(進階)', '中速散步 約 3 公里/小時，稍快步伐，呼吸略快', 4.00, 250, 'senior', NULL, 1, 1),
                                                                                                                                                                               (3, '健走(高階)', '快速健走 約 5 公里/小時，快步行走，呼吸明顯加快', 6.50, 350, 'advanced', NULL, 1, 1),
                                                                                                                                                                               (4, '跑步(初階)', '慢跑 約 5 公里/小時，平地輕鬆跑，可邊聊天', 2.50, 250, 'junior', NULL, 1, 1),
                                                                                                                                                                               (5, '跑步(進階)', '中速跑 約 8 公里/小時，微坡地中等強度', 5.50, 500, 'senior', NULL, 1, 1),
                                                                                                                                                                               (6, '跑步(高階)', '間歇衝刺 約 12 公里/小時，跑/走交替，短時間高強度', 8.00, 700, 'advanced', NULL, 1, 1),
                                                                                                                                                                               (7, '跑步(超高階)', '越野跑 約 10 公里/小時，崎嶇地形，增加核心穩定性', 7.00, 650, 'advanced', NULL, 1, 1),
                                                                                                                                                                               (8, '舉重', '輕量訓練，啞鈴或槓鈴，主要鍛鍊上肢肌力', 2.50, 200, 'junior', NULL, 1, 1),
                                                                                                                                                                               (9, '核心訓練', '平板支撐、卷腹，基礎腹肌訓練，每分鐘約 15 次', 3.50, 250, 'senior', NULL, 1, 1),
                                                                                                                                                                               (10, '深蹲', '自由重量深蹲，鍛鍊腿部與臀部肌群，中等強度', 5.50, 400, 'senior', NULL, 1, 1),
                                                                                                                                                                               (11, '臥推', '槓鈴或啞鈴臥推，高重量訓練胸肌', 7.50, 600, 'advanced', NULL, 1, 1),
                                                                                                                                                                               (12, '彈力帶訓練', '彈力帶上肢肌力訓練，進階強度', 6.50, 500, 'advanced', NULL, 1, 1);

--
-- Dumping data for table `sport_type_item`
--
INSERT INTO `sport_type_item` (`sport_type_item_id`, `sport_type_id`, `sport_id`) VALUES
                                                                                      (8, 1, 8),
                                                                                      (9, 1, 9),
                                                                                      (10, 1, 10),
                                                                                      (11, 1, 11),
                                                                                      (12, 1, 12),
                                                                                      (1, 2, 1),
                                                                                      (2, 2, 2),
                                                                                      (3, 2, 3),
                                                                                      (4, 2, 4),
                                                                                      (5, 2, 5),
                                                                                      (6, 2, 6),
                                                                                      (7, 2, 7);

--
-- Dumping data for table `custom_sport`
--
INSERT INTO `custom_sport` (`custom_sport_id`, `sport_name`, `sport_description`, `sport_estimated_calories`, `sport_pic`, `sport_data_status`, `user_id`) VALUES
                                                                                                                                                               (1, '跳繩', '居家有氧運動，適合燃脂', 180, NULL, 1, 1),
                                                                                                                                                               (2, '爬樓梯', '日常鍛鍊腿部肌力', 150, NULL, 1, 1),
                                                                                                                                                               (3, '瑜珈伸展', '柔軟度訓練，放鬆全身肌肉', 100, NULL, 1, 1),
                                                                                                                                                               (4, '居家有氧操', '中等強度有氧操，燃脂效果佳', 200, NULL, 1, 1),
                                                                                                                                                               (5, '彈力帶訓練(自訂)', '使用彈力帶進行上肢力量訓練', 160, NULL, 1, 1);

--
-- Dumping data for table `workout_plan`
--
INSERT INTO `workout_plan` (`workout_plan_id`, `user_id`, `sport_from`, `sport_id`, `custom_sport_id`, `workout_plan_status`, `workout_plan_date`, `workout_plan_notify_time`, `workout_plan_expected_duration`, `actual_total_count`, `actual_total_duration`, `actual_total_calories`, `workout_plan_data_status`, `workout_plan_update_datetime`, `task_record_id`) VALUES
                                                                                                                                                                                                                                                                                                                                                                           (1, 1, 'system', 1, NULL, 0, '2025-08-21', NULL, 30, 0, 0, 0, 1, '2025-08-20 21:00:00', NULL),
                                                                                                                                                                                                                                                                                                                                                                           (2, 1, 'system', 2, NULL, 1, '2025-08-22', NULL, 40, 1, 40, 188, 1, '2025-08-20 21:00:00', NULL),
                                                                                                                                                                                                                                                                                                                                                                           (3, 1, 'system', 3, NULL, 1, '2025-08-21', NULL, 50, 1, 50, 292, 1, '2025-08-20 21:00:00', NULL),
                                                                                                                                                                                                                                                                                                                                                                           (4, 1, 'system', 4, NULL, 1, '2025-08-23', NULL, 30, 1, 30, 125, 1, '2025-08-20 21:00:00', NULL),
                                                                                                                                                                                                                                                                                                                                                                           (5, 1, 'system', 5, NULL, 1, '2025-08-24', NULL, 40, 1, 40, 333, 1, '2025-08-20 21:00:00', NULL),
                                                                                                                                                                                                                                                                                                                                                                           (6, 1, 'system', 6, NULL, 0, '2025-08-23', NULL, 50, 0, 0, 0, 1, '2025-08-20 21:00:00', NULL),
                                                                                                                                                                                                                                                                                                                                                                           (7, 1, 'system', 7, NULL, 0, '2025-08-28', NULL, 60, 0, 0, 0, 1, '2025-08-20 21:00:00', NULL),
                                                                                                                                                                                                                                                                                                                                                                           (8, 1, 'system', 8, NULL, 0, '2025-08-25', NULL, 60, 0, 0, 0, 1, '2025-08-20 21:00:00', NULL),
                                                                                                                                                                                                                                                                                                                                                                           (9, 1, 'system', 9, NULL, 1, '2025-08-25', NULL, 45, 1, 60, 250, 1, '2025-08-20 21:00:00', NULL),
                                                                                                                                                                                                                                                                                                                                                                           (10, 1, 'system', 10, NULL, 1, '2025-08-26', NULL, 50, 1, 45, 300, 1, '2025-08-20 21:00:00', NULL),
                                                                                                                                                                                                                                                                                                                                                                           (11, 1, 'custom', NULL, 1, 0, '2025-08-29', NULL, 30, 0, 0, 0, 1, '2025-08-20 21:00:00', NULL),
                                                                                                                                                                                                                                                                                                                                                                           (12, 1, 'custom', NULL, 2, 1, '2025-08-30', NULL, 15, 3, 30, 75, 1, '2025-08-20 21:00:00', NULL),
                                                                                                                                                                                                                                                                                                                                                                           (13, 1, 'custom', NULL, 3, 0, '2025-08-31', NULL, 10, 0, 0, 0, 1, '2025-08-20 21:00:00', NULL);

--
-- Dumping data for table `workout_plan_record`
--
INSERT INTO `workout_plan_record` (`workout_plan_record_id`, `workout_plan_id`, `sport_from`, `sport_id`, `custom_sport_id`, `actual_calories`, `actual_start_time`, `actual_end_time`, `actual_duration`, `actual_record_datetime`, `workout_plan_record_data_status`) VALUES
                                                                                                                                                                                                                                                                            (1, 2, 'system', 2, NULL, 188, '2025-08-22 08:30:00', '2025-08-22 09:10:00', 40, '2025-08-22 09:10:00', 1),
                                                                                                                                                                                                                                                                            (2, 3, 'system', 3, NULL, 292, '2025-08-21 09:00:00', '2025-08-21 09:50:00', 50, '2025-08-21 09:50:00', 1),
                                                                                                                                                                                                                                                                            (3, 4, 'system', 4, NULL, 125, '2025-08-23 07:00:00', '2025-08-23 07:30:00', 30, '2025-08-23 07:30:00', 1),
                                                                                                                                                                                                                                                                            (4, 5, 'system', 5, NULL, 333, '2025-08-24 07:30:00', '2025-08-24 08:10:00', 40, '2025-08-24 08:10:00', 1),
                                                                                                                                                                                                                                                                            (5, 9, 'system', 9, NULL, 250, '2025-08-25 19:00:00', '2025-08-25 20:00:00', 60, '2025-08-25 20:00:00', 1),
                                                                                                                                                                                                                                                                            (6, 10, 'system', 10, NULL, 300, '2025-08-25 20:00:00', '2025-08-25 20:45:00', 45, '2025-08-25 20:45:00', 1),
                                                                                                                                                                                                                                                                            (7, 12, 'custom', NULL, 2, 25, '2025-08-26 07:00:00', '2025-08-26 07:10:00', 10, '2025-08-26 07:15:00', 1),
                                                                                                                                                                                                                                                                            (8, 12, 'custom', NULL, 2, 25, '2025-08-26 12:30:00', '2025-08-26 12:40:00', 10, '2025-08-26 13:00:00', 1),
                                                                                                                                                                                                                                                                            (9, 12, 'custom', NULL, 2, 25, '2025-08-26 18:30:00', '2025-08-26 18:40:00', 10, '2025-08-26 21:40:00', 1);


/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-09-22 10:16:00