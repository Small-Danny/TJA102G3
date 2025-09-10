-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: tja102g3
-- ------------------------------------------------------
-- Server version	8.0.36
DROP DATABASE IF EXISTS `tja102g3`;

--
-- 建立資料庫: 'tja102g3'
--
CREATE DATABASE IF NOT EXISTS `tja102g3` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `tja102g3`;


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` int NOT NULL AUTO_INCREMENT COMMENT '流水號PK',
  `email` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '帳號',
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密碼',
  `account_status` int NOT NULL COMMENT '帳號狀態',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '帳號建立時間',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新時間',
  `forgot_password_url` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '忘記密碼URL',
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '姓名',
  `nick_name` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '暱稱',
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '電話',
  `profile_picture` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '頭像',
  `gender` int DEFAULT NULL COMMENT '性別',
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
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admins` (
  `admin_id` int NOT NULL AUTO_INCREMENT COMMENT '管理員流水號',
  `user_id` int NOT NULL COMMENT '流水號PK',
  `last_login_at` datetime DEFAULT NULL COMMENT '最後登入時間',
  `admins_created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '帳號建立時間',
  `account` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '帳號',
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密碼',
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
/*!40101 SET @saved_cs_client     = @@character_set_client */;
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
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `article` (
  `article_id` int NOT NULL AUTO_INCREMENT COMMENT '流水號PK',
  `user_id` int NOT NULL COMMENT '會員ID',
  `forum_type_id` int NOT NULL COMMENT '分類ID',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '標題',
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文章內容',
  `cover_image_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '封面圖片URL',
  `article_attribute` enum('一般文章','公告') COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文章屬性',
  `is_pinned` tinyint NOT NULL DEFAULT '0' COMMENT '是否置頂',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '是否已刪除',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '建立時間',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新時間',
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
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `article_collection` (
  `collection_id` int NOT NULL AUTO_INCREMENT COMMENT '流水號PK',
  `user_id` int NOT NULL COMMENT '收藏人會員ID',
  `article_id` int NOT NULL COMMENT '被收藏文章ID',
  `collect_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏時間',
  `collection_status` int NOT NULL DEFAULT '1' COMMENT '收藏狀態',
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
/*!40101 SET @saved_cs_client     = @@character_set_client */;
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
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `report_status` (
  `report_status` int NOT NULL COMMENT '處理狀態',
  `status_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '狀態名稱',
  PRIMARY KEY (`report_status`),
  UNIQUE KEY `status_name` (`status_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='檢舉處理狀態表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `article_report`
--

DROP TABLE IF EXISTS `article_report`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
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
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product` (
  `product_id` int NOT NULL AUTO_INCREMENT COMMENT '商品編號',
  `product_type` tinyint NOT NULL COMMENT '商品類型(0: 裝備，1:補充劑)',
  `product_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商品名稱',
  `product_description` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商品敘述 (含規格)',
  `product_price` int NOT NULL COMMENT '價格',
  `stock_quantity` int NOT NULL COMMENT '庫存',
  `product_picture` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商品圖片',
  `product_status` tinyint NOT NULL COMMENT '商品狀態(0: 下架，1:上架)',
  `product_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商品編號或SKU',
  PRIMARY KEY (`product_id`),
  UNIQUE KEY `product_code` (`product_code`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cart_item`
--

DROP TABLE IF EXISTS `cart_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
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
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `order_id` int NOT NULL AUTO_INCREMENT COMMENT '訂單流水號',
  `user_id` int NOT NULL COMMENT '使用者流水號',
  `order_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下訂日期',
  `order_status` tinyint NOT NULL DEFAULT '0' COMMENT '出貨狀態',
  `recipient_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '收貨人姓名',
  `recipient_phone` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '收貨人電話',
  `recipient_address` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '收貨人地址',
  `used_points_amount` int DEFAULT NULL COMMENT '使用點數',
  `total_price` int NOT NULL COMMENT '總價',
  `payment_time` timestamp NOT NULL COMMENT '付款時間',
  `payment_status` tinyint NOT NULL COMMENT '付款狀態',
  `order_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '訂單代碼',
  PRIMARY KEY (`order_id`),
  UNIQUE KEY `order_code` (`order_code`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='訂單表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `order_item`
--

DROP TABLE IF EXISTS `order_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_item` (
  `order_item_id` int NOT NULL AUTO_INCREMENT COMMENT '訂單明細ID',
  `order_id` int NOT NULL COMMENT '訂單ID',
  `product_id` int NOT NULL COMMENT '商品ID',
  `order_item_idquantity` int DEFAULT NULL COMMENT '商品數量',
  `buy_price` int NOT NULL COMMENT '購買價格',
  `item_total_price` int NOT NULL COMMENT '總價',
  `order_item_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '訂單明細代碼',
  PRIMARY KEY (`order_item_id`),
  UNIQUE KEY `order_item_code` (`order_item_code`),
  KEY `order_id` (`order_id`),
  KEY `product_id` (`product_id`),
  CONSTRAINT `order_item_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `order_item_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='訂單明細表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `task_type`
--

DROP TABLE IF EXISTS `task_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
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
/*!40101 SET @saved_cs_client     = @@character_set_client */;
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
/*!40101 SET @saved_cs_client     = @@character_set_client */;
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
/*!40101 SET @saved_cs_client     = @@character_set_client */;
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
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `points_log` (
  `log_id` int NOT NULL AUTO_INCREMENT COMMENT '流水號PK',
  `user_id` int NOT NULL COMMENT '使用者ID',
  `transaction_type` tinyint NOT NULL COMMENT '交易類型',
  `points_amount` int NOT NULL COMMENT '點數數量',
  `task_id` int DEFAULT NULL COMMENT '關聯任務ID',
  `order_id` int DEFAULT NULL COMMENT '關聯訂單ID',
  `transaction_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '交易時間',
  PRIMARY KEY (`log_id`),
  KEY `user_id` (`user_id`),
  KEY `task_id` (`task_id`),
  KEY `order_id` (`order_id`),
  CONSTRAINT `points_log_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `points_log_ibfk_2` FOREIGN KEY (`task_id`) REFERENCES `task` (`task_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `points_log_ibfk_3` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='點數交易紀錄表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sport_type`
--

DROP TABLE IF EXISTS `sport_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
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
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sport` (
  `sport_id` int NOT NULL AUTO_INCREMENT COMMENT '系統運動項目ID',
  `sport_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '運動名稱',
  `sport_description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '運動描述',
  `sport_mets` decimal(4,2) NOT NULL COMMENT '運動強度',
  `sport_estimated_calories` int unsigned NOT NULL COMMENT '預估運動消耗熱量',
  `sport_level` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '運動等級',
  `sport_pic` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '運動圖片',
  `sport_data_status` tinyint NOT NULL COMMENT '運動資料狀態',
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
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sport_type_item` (
  `sport_type_item_id` int NOT NULL AUTO_INCREMENT COMMENT '運動分類明細ID',
  `sport_type_id` int NOT NULL COMMENT '運動分類ID',
  `sport_id` int NOT NULL COMMENT '系統運動項目ID',
  PRIMARY KEY (`sport_type_item_id`),
  KEY `fk_sport_type_id` (`sport_type_id`),
  KEY `fk_sport_type_item_sport_id` (`sport_id`),
  CONSTRAINT `fk_sport_type_id` FOREIGN KEY (`sport_type_id`) REFERENCES `sport_type` (`sport_type_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_sport_type_item_sport_id` FOREIGN KEY (`sport_id`) REFERENCES `sport` (`sport_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='運動分類明細表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `custom_sport`
--

DROP TABLE IF EXISTS `custom_sport`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `custom_sport` (
  `custom_sport_id` int NOT NULL AUTO_INCREMENT COMMENT '自訂義運動ID',
  `sport_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '運動名稱',
  `sport_description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '運動描述',
  `sport_estimated_calories` int unsigned NOT NULL COMMENT '預估運動消耗熱量',
  `sport_pic` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '運動圖片',
  `sport_data_status` tinyint NOT NULL COMMENT '運動資料狀態',
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
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `workout_plan` (
  `workout_plan_id` int NOT NULL AUTO_INCREMENT COMMENT '運動計畫ID',
  `user_id` int NOT NULL COMMENT '使用者ID',
  `sport_from` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '運動項目來源',
  `sport_id` int DEFAULT NULL COMMENT '系統運動項目ID',
  `custom_sport_id` int DEFAULT NULL COMMENT '自訂義運動項目ID',
  `workout_plan_status` tinyint NOT NULL COMMENT '計畫狀態',
  `workout_plan_date` date NOT NULL COMMENT '計畫安排日期',
  `workout_plan_notify_time` time DEFAULT NULL COMMENT '計畫提醒通知時間',
  `workout_plan_expected_duration` int unsigned NOT NULL COMMENT '計畫預期執行總時長(單次)',
  `actual_total_count` int unsigned NOT NULL DEFAULT '0' COMMENT '實際執行總次數',
  `actual_total_duration` int unsigned NOT NULL DEFAULT '0' COMMENT '實際執行總時長',
  `actual_total_calories` int unsigned NOT NULL DEFAULT '0' COMMENT '實際執行總消耗卡路里',
  `workout_plan_data_status` tinyint NOT NULL DEFAULT '1' COMMENT '計畫資料狀態',
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
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `workout_plan_record` (
  `workout_plan_record_id` int NOT NULL AUTO_INCREMENT COMMENT '實際執行紀錄ID',
  `workout_plan_id` int NOT NULL COMMENT '運動計畫ID',
  `sport_from` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '運動項目來源',
  `sport_id` int DEFAULT NULL COMMENT '系統運動項目ID',
  `custom_sport_id` int DEFAULT NULL COMMENT '自訂義運動項目ID',
  `actual_calories` int unsigned NOT NULL COMMENT '實際消耗卡路里',
  `actual_start_time` datetime NOT NULL COMMENT '實際執行開始時間',
  `actual_end_time` datetime NOT NULL COMMENT '實際執行結束時間',
  `actual_duration` int unsigned NOT NULL COMMENT '實際執行時長',
  `actual_record_datetime` datetime NOT NULL COMMENT '實際做紀錄的日期時間',
  `workout_plan_record_data_status` tinyint NOT NULL COMMENT '紀錄資料狀態',
  PRIMARY KEY (`workout_plan_record_id`),
  KEY `fk_workout_plan_record_workout_plan_id` (`workout_plan_id`),
  KEY `fk_workout_plan_record_sport_id` (`sport_id`),
  KEY `fk_workout_plan_record_custom_sport_id` (`custom_sport_id`),
  CONSTRAINT `fk_workout_plan_record_custom_sport_id` FOREIGN KEY (`custom_sport_id`) REFERENCES `custom_sport` (`custom_sport_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_workout_plan_record_sport_id` FOREIGN KEY (`sport_id`) REFERENCES `sport` (`sport_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_workout_plan_record_workout_plan_id` FOREIGN KEY (`workout_plan_id`) REFERENCES `workout_plan` (`workout_plan_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='運動計畫實際執行紀錄表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-08-21 16:45:25