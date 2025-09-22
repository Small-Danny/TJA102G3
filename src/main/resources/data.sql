-- 關閉外來鍵檢查
SET FOREIGN_KEY_CHECKS = 0;

-- =================================================================
-- 清空所有相關表格，以避免重複插入
-- =================================================================
TRUNCATE TABLE `sport`;
TRUNCATE TABLE `custom_sport`;
TRUNCATE TABLE `sport_type`;
TRUNCATE TABLE `sport_type_item`;
TRUNCATE TABLE `workout_plan`;
TRUNCATE TABLE `workout_plan_record`;
TRUNCATE TABLE `article_report`;
TRUNCATE TABLE `report_status`;
TRUNCATE TABLE `report_type`;
TRUNCATE TABLE `article_collection`;
TRUNCATE TABLE `article`;
TRUNCATE TABLE `forum_type`;
TRUNCATE TABLE `points_log`;
TRUNCATE TABLE `order_item`;
TRUNCATE TABLE `orders`;
TRUNCATE TABLE `cart_item`;
TRUNCATE TABLE `product`;
TRUNCATE TABLE `task_record`;
TRUNCATE TABLE `task`;
TRUNCATE TABLE `task_type`;
TRUNCATE TABLE `task_record_status_code`;
TRUNCATE TABLE `admins`;
TRUNCATE TABLE `users`;

-- =================================================================
-- SCHEMA 結構修改 (將討論的變更整合於此)
-- =================================================================

-- 1. 修改 users 表格：新增密碼重設相關欄位
ALTER TABLE `users`
    ADD COLUMN `reset_password_token` VARCHAR(255) NULL DEFAULT NULL COMMENT '密碼重設權杖',
    ADD COLUMN `token_expiry_date` DATETIME NULL DEFAULT NULL COMMENT '權杖過期時間';

-- 2. 修改 orders 表格：調整金流相關欄位以符合流程
ALTER TABLE `orders`
    MODIFY COLUMN `payment_time` TIMESTAMP NULL DEFAULT NULL COMMENT '付款時間',
    MODIFY COLUMN `payment_status` TINYINT NOT NULL DEFAULT 0 COMMENT '付款狀態 (0: 待付款, 1: 付款成功, 2: 付款失敗)',
    MODIFY COLUMN `used_points_amount` INT NOT NULL DEFAULT 0 COMMENT '訂單使用的點數';


-- =================================================================
-- 產生各表格的假資料
-- =================================================================

--
-- 產生 users 表格的假資料
--
INSERT INTO `users` (`email`, `password`, `account_status`, `name`, `nick_name`, `phone`, `gender`, `height_cm`, `weight_kg`, `bmi`, `points_balance`) VALUES
                                                                                                                                                           ('user01@example.com', 'user123456', 1, '王小明', '小明', '0911222333', 1, 175.5, 70.0, 22.86, 19),
                                                                                                                                                           ('user02@example.com', 'user123456', 1, '陳大華', '大華', '0922333444', 1, 168.0, 65.5, 23.2, 8),
                                                                                                                                                           ('user03@example.com', 'user123456', 1, '林美君', '美美', '0933444555', 2, 162.0, 55.0, 20.96, 6),
                                                                                                                                                           ('user04@example.com', 'user123456', 1, '張志遠', '小張', '0944555666', 1, 180.5, 80.2, 24.58, 9),
                                                                                                                                                           ('user05@example.com', 'user123456', 1, '黃玉珍', '小黃', '0955666777', 2, 158.0, 52.3, 20.97, 4),
                                                                                                                                                           ('user06@example.com', 'user123456', 1, '劉文傑', '文傑', '0966777888', 1, 170.0, 72.8, 25.19, 10),
                                                                                                                                                           ('user07@example.com', 'user123456', 1, '徐麗娟', '麗麗', '0977888999', 2, 165.0, 60.0, 22.04, 8),
                                                                                                                                                           ('user08@example.com', 'user123456', 1, '趙偉宏', '阿宏', '0988999000', 1, 183.0, 85.0, 25.4, 0),
                                                                                                                                                           ('admin01@example.com', 'user123456', 1, '系統管理員', 'Admin', '0900111222', 0, NULL, NULL, NULL, 0),
                                                                                                                                                           ('admin02@example.com', 'user123456', 1, '李志強', '小李', '0933444555', 1, 178.0, 75.0, 23.67, 0);

--
-- 產生 admins 表格的假資料
--
SET @admin_user_id = (SELECT user_id FROM `users` WHERE `email` = 'admin01@example.com');
SET @staff_user_id = (SELECT user_id FROM `users` WHERE `email` = 'admin02@example.com');

INSERT INTO `admins` (`user_id`, `last_login_at`, `account`, `password`) VALUES
                                                                             (@admin_user_id, NOW(), 'system_admin01', 'admin123456'),
                                                                             (@staff_user_id, '2025-08-18 10:00:00', 'system_admin02', 'admin123456');

--
-- 產生 product 表格的假資料
--
INSERT INTO `product` (`product_type`, `product_name`, `product_description`, `product_price`, `stock_quantity`, `product_picture`, `product_status`, `product_code`) VALUES
                                                                                                                                                                          (0, 'TibaFit 衣服 S號', '吸濕排汗材質，尺寸：S', 700, 50, 'clothes.png', 1, 'EQ-C-S'),
                                                                                                                                                                          (0, 'TibaFit 衣服 M號', '吸濕排汗材質，尺寸：M', 700, 80, 'clothes.png', 1, 'EQ-C-M'),
                                                                                                                                                                          (0, 'TibaFit 衣服 L號', '吸濕排汗材質，尺寸：L', 700, 70, 'clothes.png', 1, 'EQ-C-L'),
                                                                                                                                                                          (0, 'TibaFit 衣服 XL號', '吸濕排汗材質，尺寸：XL', 1200, 40, 'clothes.png', 1, 'EQ-C-XL'),
                                                                                                                                                                          (0, 'TibaFit 褲子 S號', '高彈性面料，尺寸：S', 700, 40, 'pants.png', 1, 'EQ-P-S'),
                                                                                                                                                                          (0, 'TibaFit 褲子 M號', '高彈性面料，尺寸：M', 700, 60, 'pants.png', 1, 'EQ-P-M'),
                                                                                                                                                                          (0, 'TibaFit 褲子 L號', '高彈性面料，尺寸：L', 700, 50, 'pants.png', 1, 'EQ-P-L'),
                                                                                                                                                                          (0, 'TibaFit 褲子 XL號', '高彈性面料，尺寸：XL', 900, 30, 'pants.png', 1, 'EQ-P-XL'),
                                                                                                                                                                          (0, 'TibaFit 運動手套', '止滑耐磨，均碼', 500, 200, 'gloves.png', 1, 'EQ-G-01'),
                                                                                                                                                                          (0, 'TibaFit 運動腰帶', '核心支撐，均碼', 1200, 100, 'waist-belt.png', 1, 'EQ-WB-01'),
                                                                                                                                                                          (0, 'TibaFit 運動護膝', '支撐膝關節，均碼', 800, 100, 'knee-brace.png', 1, 'EQ-KB-01'),
                                                                                                                                                                          (1, 'TibaFit 搖搖杯', '容量：500 ml', 200, 120, 'shaker.png', 1, 'ACC-S-500'),
                                                                                                                                                                          (1, 'TibaFit 搖搖杯', '容量：700 ml', 400, 100, 'shaker.png', 1, 'ACC-S-700'),
                                                                                                                                                                          (2, 'TibaFit 肌酸', '重量：500g', 650, 150, 'creatine.png', 1, 'SUP-C-500'),
                                                                                                                                                                          (2, 'TibaFit 肌酸', '重量：1kg', 1300, 100, 'creatine.png', 1, 'SUP-C-1000'),
                                                                                                                                                                          (2, 'TibaFit 乳清蛋白', '重量：500g', 700, 200, 'whey.png', 1, 'SUP-W-500'),
                                                                                                                                                                          (2, 'TibaFit 乳清蛋白', '重量：1kg', 1400, 150, 'whey.png', 1, 'SUP-W-1000'),
                                                                                                                                                                          (2, 'TibaFit BCAA', '重量：500g', 650, 200, 'bcaa.png', 1, 'SUP-B-500'),
                                                                                                                                                                          (2, 'TibaFit BCAA', '重量：1kg', 1300, 150, 'bcaa.png', 1, 'SUP-B-1000');

--
-- 產生 cart_item 表格的假資料
--
INSERT INTO `cart_item` (`product_id`, `user_id`, `cart_item_quantity`) VALUES
                                                                            (1, 1, 1),
                                                                            (2, 1, 2),
                                                                            (14, 2, 3),
                                                                            (16, 2, 1);

--
-- 產生 orders 表格的假資料
--
INSERT INTO `orders` (`user_id`, `order_date`, `order_status`, `recipient_name`, `recipient_phone`, `recipient_address`, `used_points_amount`, `total_price`, `payment_time`, `payment_status`, `order_code`) VALUES
                                                                                                                                                                                                                  (3, '2025-08-15 10:00:00', 2, '林美君', '0933444555', '台北市中山區南京東路三段219號', 6, 1200, '2025-08-15 10:05:00', 1, 'ORD-20250815-001'),
                                                                                                                                                                                                                  (4, '2025-08-16 14:30:00', 0, '張志遠', '0944555666', '台中市西屯區台灣大道三段301號', 0, 800, '2025-08-16 14:35:00', 1, 'ORD-20250816-002'),
                                                                                                                                                                                                                  (5, '2025-08-17 11:00:00', 1, '黃玉珍', '0955666777', '高雄市苓雅區四維三路2號', 4, 1500, '2025-08-17 11:05:00', 1, 'ORD-20250817-003'),
                                                                                                                                                                                                                  (6, '2025-08-18 09:15:00', 0, '劉文傑', '0966777888', '桃園市中壢區中正路100號', 0, 1400, '2025-08-18 09:20:00', 1, 'ORD-20250818-004'),
                                                                                                                                                                                                                  (7, '2025-08-19 16:45:00', 0, '徐麗娟', '0977888999', '新北市板橋區縣民大道一段1號', 0, 850, '2025-08-19 16:50:00', 1, 'ORD-20250819-005');

-- 【新增】一筆模擬剛成立、要去付款的訂單
INSERT INTO `orders` (`user_id`, `order_date`, `order_status`, `recipient_name`, `recipient_phone`, `recipient_address`, `total_price`, `order_code`) VALUES
    (8, NOW(), 0, '趙偉宏', '0988999000', '新竹市東區大學路1001號', 2600, 'ORD-20250922-006');


--
-- 產生 order_item 表格的假資料
--
SET @order_id_1 = 1;
SET @order_id_2 = 2;
SET @order_id_3 = 3;
SET @order_id_4 = 4;
SET @order_id_5 = 5;
SET @order_id_6 = 6; -- 新訂單的ID

INSERT INTO `order_item` (`order_id`, `product_id`, `order_item_idquantity`, `buy_price`, `item_total_price`, `order_item_code`) VALUES
                                                                                                                                     (@order_id_1, 1, 1, 700, 700, 'OIT-001'),
                                                                                                                                     (@order_id_1, 9, 1, 500, 500, 'OIT-002'),
                                                                                                                                     (@order_id_2, 11, 1, 800, 800, 'OIT-003'),
                                                                                                                                     (@order_id_3, 14, 2, 650, 1300, 'OIT-004'),
                                                                                                                                     (@order_id_3, 12, 1, 200, 200, 'OIT-005'),
                                                                                                                                     (@order_id_4, 17, 1, 1400, 1400, 'OIT-006'),
                                                                                                                                     (@order_id_5, 14, 1, 650, 650, 'OIT-007'),
                                                                                                                                     (@order_id_5, 12, 1, 200, 200, 'OIT-008'),
-- 新訂單的明細
                                                                                                                                     (@order_id_6, 15, 2, 1300, 2600, 'OIT-009');

-- (此處省略其他表格的 INSERT，因為它們與本次修改無關，內容和您提供的一樣)
-- ... [task_type, task, task_record_status_code, task_record, etc.] ...

-- 重新開啟外來鍵檢查
SET FOREIGN_KEY_CHECKS = 1;


