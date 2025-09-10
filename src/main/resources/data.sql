-- 關閉外來鍵檢查
SET FOREIGN_KEY_CHECKS = 0;

-- 清空相關表格，以避免重複插入
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

--
-- 產生 users 表格的假資料 (已修正 points_balance)
--
-- 1. 一般會員 (共8位)
INSERT INTO `users` (`email`, `password`, `account_status`, `forgot_password_url`, `name`, `nick_name`, `phone`, `gender`, `height_cm`, `weight_kg`, `bmi`, `points_balance`) VALUES
('user01@example.com', 'user123456', 1, 'https://example.com/reset/01', '王小明', '小明', '0911222333', 1, 175.5, 70.0, 22.86, 19),
('user02@example.com', 'user123456', 1, 'https://example.com/reset/02', '陳大華', '大華', '0922333444', 1, 168.0, 65.5, 23.2, 8),
('user03@example.com', 'user123456', 1, 'https://example.com/reset/03', '林美君', '美美', '0933444555', 2, 162.0, 55.0, 20.96, 6),
('user04@example.com', 'user123456', 1, 'https://example.com/reset/04', '張志遠', '小張', '0944555666', 1, 180.5, 80.2, 24.58, 9),
('user05@example.com', 'user123456', 1, 'https://example.com/reset/05', '黃玉珍', '小黃', '0955666777', 2, 158.0, 52.3, 20.97, 4),
('user06@example.com', 'user123456', 1, 'https://example.com/reset/06', '劉文傑', '文傑', '0966777888', 1, 170.0, 72.8, 25.19, 10),
('user07@example.com', 'user123456', 1, 'https://example.com/reset/07', '徐麗娟', '麗麗', '0977888999', 2, 165.0, 60.0, 22.04, 8),
('user08@example.com', 'user123456', 1, 'https://example.com/reset/08', '趙偉宏', '阿宏', '0988999000', 1, 183.0, 85.0, 25.4, 0);

-- 2. 作為管理員的會員 (共2位)
INSERT INTO `users` (`email`, `password`, `account_status`, `forgot_password_url`, `name`, `nick_name`, `phone`, `gender`, `height_cm`, `weight_kg`, `bmi`, `points_balance`) VALUES
('admin01@example.com', 'user123456', 1, 'https://example.com/reset/admin', '系統管理員', 'Admin', '0900111222', 0, NULL, NULL, NULL, 0);
SET @admin_user_id = LAST_INSERT_ID();

INSERT INTO `users` (`email`, `password`, `account_status`, `forgot_password_url`, `name`, `nick_name`, `phone`, `gender`, `height_cm`, `weight_kg`, `bmi`, `points_balance`) VALUES
('admin02@example.com', 'user123456', 1, 'https://example.com/reset/staff', '李志強', '小李', '0933444555', 1, 178.0, 75.0, 23.67, 0);
SET @staff_user_id = LAST_INSERT_ID();


--
-- 產生 admins 表格的假資料 (共2位)
--
INSERT INTO `admins` (`user_id`, `last_login_at`, `account`, `password`) VALUES
(@admin_user_id, NOW(), 'system_admin01', 'admin123456');

INSERT INTO `admins` (`user_id`, `last_login_at`, `account`, `password`) VALUES
(@staff_user_id, '2025-08-18 10:00:00', 'system_admin02', 'admin123456');




--
-- 產生 product 表格的假資料
--
INSERT INTO `product`
(`product_type`, `product_name`, `product_description`, `product_price`, `stock_quantity`, `product_picture`, `product_status`, `product_code`)
VALUES
-- ========= 裝備：衣服 =========
(0, 'TibaFit 衣服 S號', '吸濕排汗材質，尺寸：S', 700, 50, 'images/equipment/clothes-s.jpg', 1, 'EQ-C-S'),
(0, 'TibaFit 衣服 M號', '吸濕排汗材質，尺寸：M', 700, 80, 'images/equipment/clothes-m.jpg', 1, 'EQ-C-M'),
(0, 'TibaFit 衣服 L號', '吸濕排汗材質，尺寸：L', 700, 70, 'images/equipment/clothes-l.jpg', 1, 'EQ-C-L'),
(0, 'TibaFit 衣服 XL號', '吸濕排汗材質，尺寸：XL', 1200, 40, 'images/equipment/clothes-xl.jpg', 1, 'EQ-C-XL'),

-- ========= 裝備：褲子 =========
(0, 'TibaFit 褲子 S號', '高彈性面料，尺寸：S', 700, 40, 'images/equipment/pants-s.jpg', 1, 'EQ-P-S'),
(0, 'TibaFit 褲子 M號', '高彈性面料，尺寸：M', 700, 60, 'images/equipment/pants-m.jpg', 1, 'EQ-P-M'),
(0, 'TibaFit 褲子 L號', '高彈性面料，尺寸：L', 700, 50, 'images/equipment/pants-l.jpg', 1, 'EQ-P-L'),
(0, 'TibaFit 褲子 XL號', '高彈性面料，尺寸：XL', 900, 30, 'images/equipment/pants-xl.jpg', 1, 'EQ-P-XL'),

-- ========= 裝備：運動手套 (不分尺寸) =========
(0, 'TibaFit 運動手套', '止滑耐磨，均碼', 500, 200, 'images/equipment/gloves.jpg', 1, 'EQ-G-01'),

-- ========= 裝備：運動腰帶 (不分尺寸) =========
(0, 'TibaFit 運動腰帶', '核心支撐，均碼', 1200, 100, 'images/equipment/waist-belt.jpg', 1, 'EQ-WB-01'),

-- ========= 裝備：運動護膝 (不分尺寸) =========
(0, 'TibaFit 運動護膝', '支撐膝關節，均碼', 800, 100, 'images/equipment/knee-brace.jpg', 1, 'EQ-KB-01'),

-- ========= 配件：搖搖杯 =========
(0, 'TibaFit 搖搖杯', '容量：500 ml', 200, 120, 'images/accessories/shaker-500.jpg', 1, 'ACC-S-500'),
(0, 'TibaFit 搖搖杯', '容量：700 ml', 400, 100, 'images/accessories/shaker-700.jpg', 1, 'ACC-S-700'),

-- ========= 補充劑 =========
(1, 'TibaFit 肌酸', '重量：500g', 650, 150, 'images/supplements/creatine-500.jpg', 1, 'SUP-C-500'),
(1, 'TibaFit 肌酸', '重量：1kg',  1300, 100, 'images/supplements/creatine-1000.jpg', 1, 'SUP-C-1000'),
(1, 'TibaFit 乳清蛋白', '重量：500g', 700, 200, 'images/supplements/whey-500.jpg', 1, 'SUP-W-500'),
(1, 'TibaFit 乳清蛋白', '重量：1kg',  1400, 150, 'images/supplements/whey-1000.jpg', 1, 'SUP-W-1000'),
(1, 'TibaFit BCAA', '重量：500g', 650, 200, 'images/supplements/bcaa-500.jpg', 1, 'SUP-B-500'),
(1, 'TibaFit BCAA', '重量：1kg',  1300, 150, 'images/supplements/bcaa-1000.jpg', 1, 'SUP-B-1000');


--
-- 產生 cart_item 表格的假資料
-- 假設 user01 (user_id=1) 和 user02 (user_id=2) 有購物車
--
INSERT INTO `cart_item`
(`product_id`, `user_id`, `cart_item_quantity`)
VALUES
(1, 1, 1),   -- user01 購物車：TibaFit 衣服 S號 (product_id=1)
(2, 1, 2),   -- user01 購物車：TibaFit 衣服 M號 (product_id=2)
(14, 2, 3),  -- user02 購物車：TibaFit 肌酸 500g (product_id=14)
(16, 2, 1);  -- user02 購物車：TibaFit 乳清蛋白 500g (product_id=16)

--
-- 
INSERT INTO `orders`
(`user_id`, `order_date`, `order_status`, `recipient_name`, `recipient_phone`, `recipient_address`, `used_points_amount`, `total_price`, `payment_time`, `payment_status`, `order_code`)
VALUES
-- 第一筆訂單 (原為 1250，修正為 1200)
(3, '2025-08-15 10:00:00', 2, '林美君', '0933444555', '台北市中山區南京東路三段219號', 6, 1200, '2025-08-15 10:05:00', 1, 'ORD-20250815-001'),
-- 第二筆訂單 (正確，無需修改)
(4, '2025-08-16 14:30:00', 0, '張志遠', '0944555666', '台中市西屯區台灣大道三段301號', 0, 800, '2025-08-16 14:35:00', 1, 'ORD-20250816-002'),
-- 第三筆訂單 (原為 1380，修正為 1500)
(5, '2025-08-17 11:00:00', 1, '黃玉珍', '0955666777', '高雄市苓雅區四維三路2號', 4, 1500, '2025-08-17 11:05:00', 1, 'ORD-20250817-003'),
-- 第四筆訂單 (金額正確，但明細有誤，一併修正)
(6, '2025-08-18 09:15:00', 0, '劉文傑', '0966777888', '桃園市中壢區中正路100號', 0, 1400, '2025-08-18 09:20:00', 1, 'ORD-20250818-004'),
-- 第五筆訂單 (金額正確，但明細有誤，一併修正)
(7, '2025-08-19 16:45:00', 0, '徐麗娟', '0977888999', '新北市板橋區縣民大道一段1號', 0, 850, '2025-08-19 16:50:00', 1, 'ORD-20250819-005');

--
-- 產生 order_item 表格的假資料 (已修正價格與總計)
-- 假設 AUTO_INCREMENT 從 1 開始
--
SET @order_id_1 = 1;
SET @order_id_2 = 2;
SET @order_id_3 = 3;
SET @order_id_4 = 4;
SET @order_id_5 = 5;

INSERT INTO `order_item`
(`order_id`, `product_id`, `order_item_idquantity`, `buy_price`, `item_total_price`, `order_item_code`)
VALUES
-- 第一筆訂單明細 (總計: 700 + 500 = 1200)
(@order_id_1, 1, 1, 700, 700, 'OIT-001'),
(@order_id_1, 9, 1, 500, 500, 'OIT-002'),
-- 第二筆訂單明細 (總計: 800)
(@order_id_2, 11, 1, 800, 800, 'OIT-003'),
-- 第三筆訂單明細 (總計: 1300 + 200 = 1500)
(@order_id_3, 14, 2, 650, 1300, 'OIT-004'),
(@order_id_3, 12, 1, 200, 200, 'OIT-005'),
-- 第四筆訂單明細 (原 product_id=16, buy_price=1400 價格錯誤，修正為 product_id=17，總計: 1400)
(@order_id_4, 17, 1, 1400, 1400, 'OIT-006'),
-- 第五筆訂單明細 (原 product_id=17, buy_price=650 價格錯誤，修正為 product_id=14，總計: 650 + 200 = 850)
(@order_id_5, 14, 1, 650, 650, 'OIT-007'),
(@order_id_5, 12, 1, 200, 200, 'OIT-008');
--

-- 清空相關表格，以避免重複插入
--

-- 重新開啟外來鍵檢查
--
SET FOREIGN_KEY_CHECKS = 1;

-- ========================
-- ========================
-- 任務類型表：task_type (3 筆)
-- ========================
INSERT INTO `task_type` (`task_type_id`, `task_type_name`, `create_time`, `update_time`)
VALUES
(1, '消耗卡路里', NOW(), NOW()),
(2, '運動次數', NOW(), NOW()),
(3, '運動時長', NOW(), NOW());

-- ========================
-- 任務表：task (4 筆)
-- ========================
INSERT INTO `task`
(`task_type_id`, `task_name`, `target_value`, `unit`, `start_time`, `end_time`, `points`, `task_icon`, `admin_id`)
VALUES
(1, '燃燒 500 大卡', 500, '大卡', '2025-08-01', '2025-08-31', 10, 'https://example.com/icon1.png', 1),
(3, '游泳 30 分鐘', 30, '分鐘', '2025-08-01', '2025-08-31', 8, 'https://example.com/icon2.png', 2),
(3, '跑步 30 分鐘', 30, '分鐘', '2025-08-01', '2025-08-31', 12, 'https://example.com/icon3.png', 1),
(2, '深蹲 40 下', 40, '次', '2025-08-01', '2025-08-31', 9, 'https://example.com/icon4.png', 2);

-- ========================
-- 狀態碼表：task_record_status_code (2 筆)
-- ========================
INSERT INTO `task_record_status_code` (`task_record_status`, `status_name`)
VALUES
(0, '未完成'),
(1, '已完成');

-- ========================
-- 使用者任務紀錄表：task_record (10 筆)
-- ========================
INSERT INTO `task_record`
(`user_id`, `task_id`, `task_record_status`, `user_start_time`, `user_end_time`)
VALUES
(1, 1, 1, '2025-08-01 07:00:00', '2025-08-01 07:40:00'),   -- user01 完成 500大卡
(2, 2, 0, '2025-08-02 20:00:00', NULL),                     -- user02 開始游泳 30 分鐘未完成
(3, 3, 1, '2025-08-03 18:00:00', '2025-08-03 18:30:00'),   -- user03 完成 跑步 30 分鐘
(4, 4, 1, '2025-08-04 06:30:00', '2025-08-04 06:50:00'),   -- user04 完成 深蹲40下
(5, 1, 0, '2025-08-05 07:10:00', NULL),                     -- user05 開始500大卡但未完成
(6, 1, 1, '2025-08-01 08:00:00', '2025-08-01 08:45:00'),   -- user06 完成 500大卡
(7, 2, 1, '2025-08-02 19:00:00', '2025-08-02 19:20:00'),   -- user07 完成游泳 30 分鐘
(8, 3, 0, '2025-08-03 17:30:00', NULL),                     -- user08 開始跑步30分鐘但未完成
(1, 4, 1, '2025-08-04 06:00:00', '2025-08-04 06:25:00'),   -- user01 完成 深蹲40下
(2, 4, 1, '2025-08-05 07:00:00', '2025-08-05 07:55:00');   -- user02 完成 深蹲40下


--
-- 產生 points_log 表格的假資料
-- 假設 user01 到 user08 的 user_id 為 1 到 8
-- 假設 task_id 為 1 到 4
--
INSERT INTO `points_log`
(`user_id`, `transaction_type`, `points_amount`, `task_id`, `order_id`, `transaction_time`)
VALUES
-- 獲得點數（來自任務）
(1, 0, 10, 1, NULL, '2025-08-1 07:45:00'),
(3, 0, 12, 3, NULL, '2025-08-03 18:35:00'),
(4, 0, 9, 4, NULL, '2025-08-04 06:55:00'),
(6, 0, 10, 1, NULL, '2025-08-01 08:50:00'),
(7, 0, 8, 2, NULL, '2025-08-02 19:25:00'),
(1, 0, 9, 4, NULL, '2025-08-04 06:30:00'),
(2, 0, 8, 2, NULL, '2025-08-05 08:00:00'),

-- 使用點數（來自訂單）
(3, 1, 6, NULL, 1, '2025-08-15 10:05:00'),
(5, 1, 4, NULL, 3, '2025-08-17 11:05:00');


--
-- 產生 forum_type 表格的假資料 (6 筆)
-- 新增了「體育新聞」及其他建議分類
--
INSERT INTO `forum_type` (`forum_type_id`, `forum_type_name`)
VALUES
(1, '健身知識分享'),
(2, '日常心得交流'),
(3, '體育新聞'),
(4, '健康飲食專區'),
(5, '器材與裝備評測'),
(6, '新手入門區');


--
-- 產生 article 表格的假資料 (10 筆)
-- 假設 user01 到 user08 和 admin01, admin02 為作者
--
INSERT INTO `article`
(`user_id`, `forum_type_id`, `title`, `content`, `cover_image_url`, `article_attribute`, `is_pinned`, `is_deleted`)
VALUES
(1, 1, '新手必看！三大健身基本原則', '這篇文章將介紹適合新手的三大健身原則...', 'images/article/cover1.jpg', '一般文章', 0, 0),
(2, 1, '如何選擇適合自己的乳清蛋白？', '乳清蛋白的種類繁多，本文教你如何挑選...', 'images/article/cover2.jpg', '一般文章', 0, 0),
(3, 2, '健身餐分享：簡單又美味的雞胸肉做法', '分享一個我常做的雞胸肉食譜，讓你的健身餐不再單調！', 'images/article/cover3.jpg', '一般文章', 0, 0),
(4, 1, '深蹲技巧大公開，避免膝蓋受傷', '深蹲是健身之王，但姿勢錯誤容易受傷...', 'images/article/cover4.jpg', '一般文章', 0, 0),
(5, 2, '運動後恢復的重要性', '運動後千萬別忽略了恢復，這篇教你如何快速恢復...', 'images/article/cover5.jpg', '一般文章', 0, 0),
(6, 1, '增肌減脂的飲食策略', '想增肌又減脂？你需要掌握正確的飲食策略...', 'images/article/cover6.jpg', '一般文章', 0, 0),
(7, 3, '最新體育新聞：奧運冠軍的訓練秘訣', '奧運金牌得主在賽後分享了他們的訓練秘訣，值得參考！', 'images/article/news1.jpg', '一般文章', 0, 0),
(8, 1, '在家也能做的核心訓練', '沒有器材也能練核心，五個動作讓你練出馬甲線！', 'images/article/cover8.jpg', '一般文章', 0, 0),
(9, 1, '網站公告：論壇新功能上線', '親愛的會員，論壇已新增...。', 'images/article/announcement.jpg', '公告', 1, 0),
(10, 2, '管理員的心得分享：堅持就是勝利！', '作為管理員，我也和大家一樣...', 'images/article/staff_share.jpg', '一般文章', 0, 0);


--
-- 產生 article_collection 表格的假資料 (10 筆)
--
INSERT INTO `article_collection` (`user_id`, `article_id`, `collect_time`, `collection_status`)
VALUES
(1, 2, NOW(), 1),
(2, 1, NOW(), 1),
(3, 4, NOW(), 1),
(4, 3, NOW(), 1),
(5, 6, NOW(), 1),
(6, 5, NOW(), 1),
(7, 8, NOW(), 1),
(8, 7, NOW(), 1),
(1, 5, NOW(), 1),
(2, 6, NOW(), 1);


--
-- 產生 report_type 表格的假資料 (3 筆)
--
INSERT INTO `report_type` (`report_type_name`)
VALUES
('色情內容'),
('暴力血腥'),
('人身攻擊');


--
-- 產生 report_status 表格的假資料 (4 筆)
--
INSERT INTO `report_status` (`report_status`, `status_name`)
VALUES
(0, '待處理'),
(1, '已處理'),
(2, '已駁回'),
(3, '無效檢舉');


--
-- 產生 article_report 表格的假資料 (5 筆)
-- 假設 user01 (user_id=1) 和 user02 (user_id=2) 進行檢舉
--
INSERT INTO `article_report`
(`user_id`, `article_id`, `report_type_id`, `reason`, `report_time`, `report_status`)
VALUES
(1, 2, 3, '留言有不雅字眼', NOW(), 0),
(2, 4, 1, '圖片不適合', NOW(), 1),
(1, 6, 2, '內容血腥暴力', NOW(), 0),
(2, 8, 3, '對作者人身攻擊', NOW(), 0),
(3, 1, 1, '內容不符分類', NOW(), 2);







-- sport表假資料 (需在 admis表假資料 建立後執行)
INSERT INTO `sport` (
    sport_name,
    sport_description,
    sport_mets,
    sport_estimated_calories,
    sport_level,
    sport_pic,
    sport_data_status,
    admin_id
)
VALUES
-- 健走類
('健走(初階)', '慢速散步 約 1 公里/小時，輕鬆散步，可正常交談', 2.00, 150, 'junior', NULL, 1, 1),
('健走(進階)', '中速散步 約 3 公里/小時，稍快步伐，呼吸略快', 4.00, 250, 'senior', NULL, 1, 1),
('健走(高階)', '快速健走 約 5 公里/小時，快步行走，呼吸明顯加快', 6.50, 350, 'advanced', NULL, 1, 1),

-- 跑步類
('跑步(初階)', '慢跑 約 5 公里/小時，平地輕鬆跑，可邊聊天', 2.50, 250, 'junior', NULL, 1, 1),
('跑步(進階)', '中速跑 約 8 公里/小時，微坡地中等強度', 5.50, 500, 'senior', NULL, 1, 1),
('跑步(高階)', '間歇衝刺 約 12 公里/小時，跑/走交替，短時間高強度', 8.00, 700, 'advanced', NULL, 1, 1),
('跑步(超高階)', '越野跑 約 10 公里/小時，崎嶇地形，增加核心穩定性', 7.00, 650, 'advanced', NULL, 1, 1),

-- 重訓類
('舉重', '輕量訓練，啞鈴或槓鈴，主要鍛鍊上肢肌力', 2.50, 200, 'junior', NULL, 1, 1),
('核心訓練', '平板支撐、卷腹，基礎腹肌訓練，每分鐘約 15 次', 3.50, 250, 'senior', NULL, 1, 1),
('深蹲', '自由重量深蹲，鍛鍊腿部與臀部肌群，中等強度', 5.50, 400, 'senior', NULL, 1, 1),
('臥推', '槓鈴或啞鈴臥推，高重量訓練胸肌', 7.50, 600, 'advanced', NULL, 1, 1),
('彈力帶訓練', '彈力帶上肢肌力訓練，進階強度', 6.50, 500, 'advanced', NULL, 1, 1);






-- custom_sport表假資料 (需在 admis表假資料 建立後執行)
INSERT INTO `custom_sport` (
    sport_name,
    sport_description,
    sport_estimated_calories,
    sport_pic,
    sport_data_status,
    user_id
)
VALUES
('跳繩', '居家有氧運動，適合燃脂', 180, NULL, 1, 1),
('爬樓梯', '日常鍛鍊腿部肌力', 150, NULL, 1, 1),
('瑜珈伸展', '柔軟度訓練，放鬆全身肌肉', 100, NULL, 1, 1),
('居家有氧操', '中等強度有氧操，燃脂效果佳', 200, NULL, 1, 1),
('彈力帶訓練', '使用彈力帶進行上肢力量訓練', 160, NULL, 1, 1);





-- sport_type表假資料
INSERT INTO `sport_type` (
    sport_type_name,
    sport_type_pic
)
VALUES
    ('重訓', NULL),
    ('有氧', NULL);
    
    
-- sport_type_item表假資料
INSERT INTO `sport_type_item` (
    sport_type_id,
    sport_id
)
VALUES
-- 有氧類 (sport_type_id = 2)
(2, 1),  -- 健走 1km/h
(2, 2),  -- 健走 3km/h
(2, 3),  -- 健走 5km/h
(2, 4),  -- 跑步 慢跑
(2, 5),  -- 跑步 中速跑
(2, 6),  -- 跑步 間歇衝刺
(2, 7),  -- 跑步 越野跑

-- 重訓類 (sport_type_id = 1)
(1, 8),  -- 舉重
(1, 9), -- 核心訓練
(1, 10), -- 深蹲
(1, 11), -- 臥推
(1, 12); -- 彈力帶訓練





-- workout_plan表假資料
INSERT INTO `workout_plan` (
    user_id,
    sport_from,
    sport_id,
    custom_sport_id,
    workout_plan_status,
    workout_plan_date,
    workout_plan_notify_time,
    workout_plan_expected_duration,
    actual_total_count,
    actual_total_duration,
    actual_total_calories,
    workout_plan_data_status,
    workout_plan_update_datetime,
    task_record_id
)
VALUES
-- 健走計畫
(1, 'system', 1, NULL, 0, '2025-08-21', NULL, 30, 0, 0, 0, 1, '2025-08-20 21:00:00', NULL),
(1, 'system', 2, NULL, 1, '2025-08-22', NULL, 40, 1, 40, 188, 1, '2025-08-20 21:00:00', NULL),
(1, 'system', 3, NULL, 1, '2025-08-21', NULL, 50, 1, 50, 292, 1, '2025-08-20 21:00:00', NULL),

-- 跑步計畫
(1, 'system', 4, NULL, 1, '2025-08-23', NULL, 30, 1, 30, 125, 1, '2025-08-20 21:00:00', NULL),
(1, 'system', 5, NULL, 1, '2025-08-24', NULL, 40, 1, 40, 333, 1, '2025-08-20 21:00:00', NULL),
(1, 'system', 6, NULL, 0, '2025-08-23', NULL, 50, 0, 0, 0, 1, '2025-08-20 21:00:00', NULL),
(1, 'system', 7, NULL, 0, '2025-08-28', NULL, 60, 0, 0, 0, 1, '2025-08-20 21:00:00', NULL),
-- 重訓計畫
(1, 'system', 8, NULL, 0, '2025-08-25', NULL, 60, 0, 0, 0, 1, '2025-08-20 21:00:00', NULL),
(1, 'system', 9, NULL, 1, '2025-08-25', NULL, 45, 1, 60, 250, 1, '2025-08-20 21:00:00', NULL),
(1, 'system', 10, NULL, 1, '2025-08-26', NULL, 50, 1, 45, 300, 1, '2025-08-20 21:00:00', NULL),

-- 自訂義運動計畫
(1, 'custom', NULL, 1, 0, '2025-08-29', NULL, 30, 0, 0, 0, 1, '2025-08-20 21:00:00', NULL),
(1, 'custom', NULL, 2, 1, '2025-08-30', NULL, 15, 3, 30, 75, 1, '2025-08-20 21:00:00', NULL),
(1, 'custom', NULL, 3, 0, '2025-08-31', NULL, 10, 0, 0, 0, 1, '2025-08-20 21:00:00', NULL);





-- workout_plan_record表假資料
INSERT INTO `workout_plan_record` (
    workout_plan_id,
    sport_from,
    sport_id,
    custom_sport_id,
    actual_calories,
    actual_start_time,
    actual_end_time,
    actual_duration,
    actual_record_datetime,
    workout_plan_record_data_status
)
VALUES
-- 健走計畫紀錄
(2, 'system', 2, NULL, 188, '2025-08-22 08:30:00', '2025-08-22 09:10:00', 40, '2025-08-22 09:10:00', 1),
(3, 'system', 3, NULL, 292, '2025-08-21 09:00:00', '2025-08-21 09:50:00', 50, '2025-08-21 09:50:00', 1),

-- 跑步計畫紀錄
(4, 'system', 4, NULL, 125, '2025-08-23 07:00:00', '2025-08-23 07:30:00', 30, '2025-08-23 07:30:00', 1),
(5, 'system', 5, NULL, 333, '2025-08-24 07:30:00', '2025-08-24 08:10:00', 40, '2025-08-24 08:10:00', 1),

-- 重訓計畫紀錄
(9, 'system', 9, NULL, 250, '2025-08-25 19:00:00', '2025-08-25 20:00:00', 60, '2025-08-25 20:00:00', 1),
(10, 'system', 10, NULL, 300, '2025-08-25 20:00:00', '2025-08-25 20:45:00', 45, '2025-08-25 20:45:00', 1),

-- 自訂義運動計畫紀錄
(12, 'custom', NULL, 2, 25, '2025-08-26 07:00:00', '2025-08-26 07:10:00', 10, '2025-08-26 07:15:00', 1),
(12, 'custom', NULL, 2, 25, '2025-08-26 12:30:00', '2025-08-26 12:40:00', 10, '2025-08-26 13:00:00', 1),
(12, 'custom', NULL, 2, 25, '2025-08-26 18:30:00', '2025-08-26 18:40:00', 10, '2025-08-26 21:40:00', 1);



-- 重新開啟外來鍵檢查
SET FOREIGN_KEY_CHECKS = 1;
