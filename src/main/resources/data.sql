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
(1, 0, 'TibaFit 衣服 S號', '吸濕排汗機能布料，親膚透氣、快乾耐洗，訓練與日常皆宜。', 700, 50, 'clothes.png', 1, 'EQ-C-S'),
(2, 0, 'TibaFit 衣服 M號', '吸濕排汗機能布料，親膚透氣、快乾耐洗，訓練與日常皆宜。', 700, 80, 'clothes.png', 1, 'EQ-C-M'),
(3, 0, 'TibaFit 衣服 L號', '吸濕排汗機能布料，親膚透氣、快乾耐洗，訓練與日常皆宜。', 700, 70, 'clothes.png', 1, 'EQ-C-L'),
(4, 0, 'TibaFit 衣服 XL號', '吸濕排汗機能布料，親膚透氣、快乾耐洗，訓練與日常皆宜。', 1200, 40, 'clothes.png', 1, 'EQ-C-XL'),
(5, 0, 'TibaFit 褲子 S號', '高彈性耐磨面料，貼身不緊繃，支撐下肢動作更穩定。', 700, 40, 'pants.png', 1, 'EQ-P-S'),
(6, 0, 'TibaFit 褲子 M號', '高彈性耐磨面料，貼身不緊繃，支撐下肢動作更穩定。', 700, 60, 'pants.png', 1, 'EQ-P-M'),
(7, 0, 'TibaFit 褲子 L號', '高彈性耐磨面料，貼身不緊繃，支撐下肢動作更穩定。', 700, 50, 'pants.png', 1, 'EQ-P-L'),
(8, 0, 'TibaFit 褲子 XL號', '高彈性耐磨面料，貼身不緊繃，支撐下肢動作更穩定。', 900, 30, 'pants.png', 1, 'EQ-P-XL'),
(9, 0, 'TibaFit 運動手套', '止滑耐磨掌心，透氣不悶熱，抓握更穩、訓練更安心', 500, 200, 'gloves.png', 1, 'EQ-G-01'),
(10, 0, 'TibaFit 運動腰帶', '核心穩定支撐設計，重訓深蹲/硬舉更安心。', 1200, 100, 'waist_belt.png', 1, 'EQ-WB-01'),
(11, 0, 'TibaFit 運動護膝', '高彈性支撐材，貼合膝關節，運動更安心。', 800, 100, 'knee_brace.png', 1, 'EQ-KB-01'),
(12, 1, 'TibaFit 搖搖杯', '防漏旋蓋、好清洗的搖搖杯，外出補給更輕鬆。', 200, 120, 'shaker.png', 1, 'ACC-S-500'),
(13, 1, 'TibaFit 搖搖杯', '防漏旋蓋、好清洗的搖搖杯，外出補給更輕鬆。', 400, 100, 'shaker.png', 1, 'ACC-S-700'),
(14, 2, 'TibaFit 肌酸', '高純度肌酸粉，溶解度佳，助力爆發與肌力表現。', 650, 150, 'creatine.png', 1, 'SUP-C-500'),
(15, 2, 'TibaFit 肌酸', '高純度肌酸粉，溶解度佳，助力爆發與肌力表現。', 1300, 100, 'creatine.png', 1, 'SUP-C-1000'),
(16, 2, 'TibaFit 乳清蛋白', '優質乳清蛋白，好喝易沖泡，訓練後快速補給。', 700, 200, 'whey.png', 1, 'SUP-W-500'),
(17, 2, 'TibaFit 乳清蛋白', '優質乳清蛋白，好喝易沖泡，訓練後快速補給。', 1400, 150, 'whey.png', 1, 'SUP-W-1000'),
(18, 2, 'TibaFit BCAA', 'BCAA 支鏈胺基酸，訓練前後補給好選擇。', 650, 200, 'bcaa.png', 1, 'SUP-B-500'),
(19, 2, 'TibaFit BCAA', 'BCAA 支鏈胺基酸，訓練前後補給好選擇。', 1300, 150, 'bcaa.png', 1, 'SUP-B-1000');

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
(5, 7, '2025-08-19 16:45:00', 0, '徐麗娟', '0977888999', '新北市板橋區縣民大道一段1號', 0, 850, '2025-08-19 16:50:00', 1, 'ORD-20250819-005'),
(6, 8, NOW(), 0, '趙偉宏', '0988999000', '新竹市東區大學路1001號', 2600, 2600, NULL, 0, 'ORD-20250922-006');

--
-- Dumping data for table `order_item`
--
INSERT INTO `order_item` (`order_item_id`, `order_id`, `product_id`, `order_item_quantity`, `buy_price`, `item_total_price`, `order_item_code`) VALUES
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
-- 關閉外來鍵檢查
--
SET FOREIGN_KEY_CHECKS = 0;
--
-- 清空相關表格，以避免重複插入
--
TRUNCATE TABLE `article_report`;
TRUNCATE TABLE `report_status`;
TRUNCATE TABLE `report_type`;
TRUNCATE TABLE `article_collection`;
TRUNCATE TABLE `article`;
TRUNCATE TABLE `forum_type`;
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
(1, 1, '新手必看！三大健身基本原則', '這篇文章將介紹適合新手的三大健身原則...', 'https://res.cloudinary.com/doxg5hdim/image/upload/v1759231412/forum/content/vvgcth1c5y0o8rwvubr7.jpg', '一般文章', 0, 0),
(2, 1, '如何選擇適合自己的乳清蛋白？', '乳清蛋白的種類繁多，本文教你如何挑選...', 'https://res.cloudinary.com/doxg5hdim/image/upload/v1759231409/forum/content/zk7r0c1sgxgwr5vduqlj.jpg', '一般文章', 0, 0),
(3, 2, '健身餐分享：簡單又美味的雞胸肉做法', '分享一個我常做的雞胸肉食譜，讓你的健身餐不再單調！', 'https://res.cloudinary.com/doxg5hdim/image/upload/v1759231410/forum/content/ld3frx7zunvamjlx0uig.jpg', '一般文章', 0, 0),
(4, 1, '深蹲技巧大公開，避免膝蓋受傷', '深蹲是健身之王，但姿勢錯誤容易受傷...', 'https://res.cloudinary.com/doxg5hdim/image/upload/v1759231411/forum/content/nzzpdhcffpvmncaks5fy.jpg', '一般文章', 0, 0),
(5, 2, '運動後恢復的重要性', '運動後千萬別忽略了恢復，這篇教你如何快速恢復...', 'https://res.cloudinary.com/doxg5hdim/image/upload/v1759231413/forum/content/o2glelz2gzmlrbqhc3fm.jpg', '一般文章', 0, 0),
(6, 1, '增肌減脂的飲食策略', '想增肌又減脂？你需要掌握正確的飲食策略...', 'https://res.cloudinary.com/doxg5hdim/image/upload/v1759231771/forum/content/v8biiecporyx0abzw3si.jpg', '一般文章', 0, 0),
(7, 3, '最新體育新聞：奧運冠軍的訓練秘訣', '奧運金牌得主在賽後分享了他們的訓練秘訣，值得參考！', 'https://res.cloudinary.com/doxg5hdim/image/upload/v1759231769/forum/content/irwokacozop9cbt7etkt.jpg', '一般文章', 0, 0),
(8, 1, '在家也能做的核心訓練', '沒有器材也能練核心，五個動作讓你練出馬甲線！', 'https://res.cloudinary.com/doxg5hdim/image/upload/v1759231772/forum/content/iigdnkbgdwkbsld57rq1.jpg', '一般文章', 0, 0),
(9, 1, '網站公告：論壇新功能上線', '親愛的會員，論壇已新增...。','https://res.cloudinary.com/doxg5hdim/image/upload/v1759231774/forum/content/ntihxl4pwjel87jyjfg6.png', '公告', 1, 0),
(10, 2, '管理員的心得分享：堅持就是勝利！', '作為管理員，我也和大家一樣...', 'https://res.cloudinary.com/doxg5hdim/image/upload/v1759231773/forum/content/tpil2nirr1ro1b2f94vn.jpg', '一般文章', 0, 0);
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
('人身攻擊'),
('仇恨或惡意內容'),
('有害或危險舉動'),
('騷擾或霸凌內容'),
('自殺,自傷或飲食失調'),
('錯誤資訊'),
('垃圾內容或誤導性內容'),
('法律問題'),
('其他')
;
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
--
-- 重新開啟外來鍵檢查
--
SET FOREIGN_KEY_CHECKS = 1;


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
(1, 1, '燃燒 500 大卡', 500, '大卡', '2025-08-01', '2025-08-31', 10, NULL, 1),
(2, 3, '游泳 30 分鐘', 30, '分鐘', '2025-08-01', '2025-08-31', 8, NULL, 2),
(3, 3, '跑步 30 分鐘', 30, '分鐘', '2025-08-01', '2025-08-31', 12, NULL, 1),
(4, 2, '深蹲 40 下', 40, '次', '2025-08-01', '2025-08-31', 9, NULL, 2);

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
INSERT INTO `sport_type` (sport_type_id, sport_type_name, sport_type_pic, sport_type_data_status, create_datetime, update_datetime) VALUES
(1, '重訓', NULL, 1, '2025-08-20 20:00:00', '2025-08-20 21:00:00'),
(2, '有氧', NULL, 1, '2025-08-20 20:00:00', '2025-08-20 21:00:00'),
(3, '自訂義', NULL, 1, '2025-08-20 20:00:00', '2025-08-20 21:00:00');

--
-- Dumping data for table `sport`
--
INSERT INTO `sport` (sport_id, sport_name, sport_description, sport_mets, sport_estimated_calories, sport_level, sport_pic, sport_data_status, admin_id, create_datetime, update_datetime) VALUES
(1, '健走(初階)', '慢速散步 約 1 公里/小時，輕鬆散步，可正常交談', 2.00, 150, 'junior', NULL, 1, 1, '2025-08-20 20:00:00', '2025-08-20 21:00:00'),
(2, '健走(進階)', '中速散步 約 3 公里/小時，稍快步伐，呼吸略快', 4.00, 250, 'senior', NULL, 1, 1, '2025-08-20 20:00:00', '2025-08-20 21:00:00'),
(3, '健走(高階)', '快速健走 約 5 公里/小時，快步行走，呼吸明顯加快', 6.50, 350, 'advanced', NULL, 1, 1, '2025-08-20 20:00:00', '2025-08-20 21:00:00'),
(4, '跑步(初階)', '慢跑 約 5 公里/小時，平地輕鬆跑，可邊聊天', 2.50, 250, 'junior', NULL, 1, 1, '2025-08-20 20:00:00', '2025-08-20 21:00:00'),
(5, '跑步(進階)', '中速跑 約 8 公里/小時，微坡地中等強度', 5.50, 500, 'senior', NULL, 1, 1, '2025-08-20 20:00:00', '2025-08-20 21:00:00'),
(6, '跑步(高階)', '間歇衝刺 約 12 公里/小時，跑/走交替，短時間高強度', 8.00, 700, 'advanced', NULL, 1, 1, '2025-08-20 20:00:00', '2025-08-20 21:00:00'),
(7, '跑步(超高階)', '越野跑 約 10 公里/小時，崎嶇地形，增加核心穩定性', 7.00, 650, 'advanced', NULL, 1, 1, '2025-08-20 20:00:00', '2025-08-20 21:00:00'),
(8, '舉重', '輕量訓練，啞鈴或槓鈴，主要鍛鍊上肢肌力', 2.50, 200, 'junior', NULL, 1, 1, '2025-08-20 20:00:00', '2025-08-20 21:00:00'),
(9, '核心訓練', '平板支撐、卷腹，基礎腹肌訓練，每分鐘約 15 次', 3.50, 250, 'senior', NULL, 1, 1, '2025-08-20 20:00:00', '2025-08-20 21:00:00'),
(10, '深蹲', '自由重量深蹲，鍛鍊腿部與臀部肌群，中等強度', 5.50, 400, 'senior', NULL, 1, 1, '2025-08-20 20:00:00', '2025-08-20 21:00:00'),
(11, '臥推', '槓鈴或啞鈴臥推，高重量訓練胸肌', 7.50, 600, 'advanced', NULL, 1, 1, '2025-08-20 20:00:00', '2025-08-20 21:00:00'),
(12, '彈力帶訓練', '彈力帶上肢肌力訓練，進階強度', 6.50, 500, 'advanced', NULL, 1, 1, '2025-08-20 20:00:00', '2025-08-20 21:00:00');

--
-- Dumping data for table `sport_type_item`
--
INSERT INTO `sport_type_item` (sport_type_item_id, sport_type_id, sport_id, create_datetime, update_datetime) VALUES
(1, 2, 1, '2025-08-20 20:00:00', '2025-08-20 21:00:00'),
(2, 2, 2, '2025-08-20 20:00:00', '2025-08-20 21:00:00'),
(3, 2, 3, '2025-08-20 20:00:00', '2025-08-20 21:00:00'),
(4, 2, 4, '2025-08-20 20:00:00', '2025-08-20 21:00:00'),
(5, 2, 5, '2025-08-20 20:00:00', '2025-08-20 21:00:00'),
(6, 2, 6, '2025-08-20 20:00:00', '2025-08-20 21:00:00'),
(7, 2, 7, '2025-08-20 20:00:00', '2025-08-20 21:00:00'),
(8, 1, 8, '2025-08-20 20:00:00', '2025-08-20 21:00:00'),
(9, 1, 9, '2025-08-20 20:00:00', '2025-08-20 21:00:00'),
(10, 1, 10, '2025-08-20 20:00:00', '2025-08-20 21:00:00'),
(11, 1, 11, '2025-08-20 20:00:00', '2025-08-20 21:00:00'),
(12, 1, 12, '2025-08-20 20:00:00', '2025-08-20 21:00:00');

--
-- Dumping data for table `custom_sport`
--
INSERT INTO `custom_sport` (custom_sport_id, sport_name, sport_description, sport_estimated_calories, sport_pic, sport_data_status, user_id, create_datetime, update_datetime) VALUES
(1, '跳繩', '居家有氧運動，適合燃脂', 180, NULL, 1, 1, '2025-08-20 20:00:00', '2025-08-20 21:00:00'),
(2, '爬樓梯', '日常鍛鍊腿部肌力', 150, NULL, 1, 1, '2025-08-20 20:00:00', '2025-08-20 21:00:00'),
(3, '瑜珈伸展', '柔軟度訓練，放鬆全身肌肉', 100, NULL, 1, 1, '2025-08-20 20:00:00', '2025-08-20 21:00:00'),
(4, '居家有氧操', '中等強度有氧操，燃脂效果佳', 200, NULL, 1, 1, '2025-08-20 20:00:00', '2025-08-20 21:00:00'),
(5, '彈力帶訓練', '使用彈力帶進行上肢力量訓練', 160, NULL, 1, 1, '2025-08-20 20:00:00', '2025-08-20 21:00:00');

--
-- Dumping data for table `workout_plan`
--
INSERT INTO `workout_plan` (workout_plan_id, workout_plan_name, user_id, sport_from, sport_id, custom_sport_id, workout_plan_status, workout_plan_date, workout_plan_time, workout_plan_is_notify, workout_plan_expected_duration, actual_total_count, actual_total_duration, actual_total_calories, workout_plan_data_status, task_record_id, create_datetime, update_datetime) VALUES
(1, '計畫01', 1, 'system', 1, NULL, 0, '2025-08-21', NULL, 0, 30, 0, 0, 0, 1, NULL, '2025-08-20 20:00:00', '2025-08-20 21:00:00'),
(2, '計畫02', 1, 'system', 2, NULL, 1, '2025-08-22', NULL, 0, 40, 1, 40, 188, 1, NULL, '2025-08-20 20:00:00', '2025-08-20 21:00:00'),
(3, '計畫03', 1, 'system', 3, NULL, 1, '2025-08-21', NULL, 0, 50, 1, 50, 292, 1, NULL, '2025-08-20 20:00:00', '2025-08-20 21:00:00'),
(4, '計畫04', 1, 'system', 4, NULL, 1, '2025-08-23', NULL, 0, 30, 1, 30, 125, 1, NULL, '2025-08-20 20:00:00', '2025-08-20 21:00:00'),
(5, '計畫05', 1, 'system', 5, NULL, 1, '2025-08-24', NULL, 0, 40, 1, 40, 333, 1, NULL, '2025-08-20 20:00:00', '2025-08-20 21:00:00'),
(6, '計畫06', 1, 'system', 6, NULL, 0, '2025-08-23', NULL, 0, 50, 0, 0, 0, 1, NULL, '2025-08-20 20:00:00', '2025-08-20 21:00:00'),
(7, '計畫07', 1, 'system', 7, NULL, 0, '2025-08-28', NULL, 0, 60, 0, 0, 0, 1, NULL, '2025-08-20 20:00:00', '2025-08-20 21:00:00'),
(8, '計畫08', 1, 'system', 8, NULL, 0, '2025-08-25', NULL, 0, 60, 0, 0, 0, 1, NULL, '2025-08-20 20:00:00', '2025-08-20 21:00:00'),
(9, '計畫09', 1, 'system', 9, NULL, 1, '2025-08-25', NULL, 0, 45, 1, 60, 250, 1, NULL, '2025-08-20 20:00:00', '2025-08-20 21:00:00'),
(10, '計畫10', 1, 'system', 10, NULL, 1, '2025-08-26', NULL, 0, 50, 1, 45, 300, 1, NULL, '2025-08-20 20:00:00', '2025-08-20 21:00:00'),
(11, '計畫11', 1, 'custom', NULL, 1, 0, '2025-08-29', NULL, 0, 30, 0, 0, 0, 1, NULL, '2025-08-20 20:00:00', '2025-08-20 21:00:00'),
(12, '計畫12', 1, 'custom', NULL, 2, 1, '2025-08-30', NULL, 0, 15, 3, 30, 75, 1, NULL, '2025-08-20 20:00:00', '2025-08-20 21:00:00'),
(13, '計畫13', 1, 'custom', NULL, 3, 0, '2025-08-31', NULL, 0, 10, 0, 0, 0, 1, NULL, '2025-08-20 20:00:00', '2025-08-20 21:00:00');

--
-- Dumping data for table `workout_plan_record`
--
INSERT INTO `workout_plan_record` (workout_plan_record_id, workout_plan_id, sport_from, sport_id, custom_sport_id, actual_calories, calorie_count_method, actual_start_time, actual_end_time, actual_duration, actual_record_datetime, workout_plan_record_data_status, create_datetime, update_datetime) VALUES
(1, 2, 'system', 2, NULL, 188, 1, '2025-08-22 08:30:00', '2025-08-22 09:10:00', 40, '2025-08-22 09:10:00', 1, '2025-08-20 20:00:00', '2025-08-20 21:00:00'),
(2, 3, 'system', 3, NULL, 292, 1, '2025-08-21 09:00:00', '2025-08-21 09:50:00', 50, '2025-08-21 09:50:00', 1, '2025-08-20 20:00:00', '2025-08-20 21:00:00'),
(3, 4, 'system', 4, NULL, 125, 1, '2025-08-23 07:00:00', '2025-08-23 07:30:00', 30, '2025-08-23 07:30:00', 1, '2025-08-20 20:00:00', '2025-08-20 21:00:00'),
(4, 5, 'system', 5, NULL, 333, 1, '2025-08-24 07:30:00', '2025-08-24 08:10:00', 40, '2025-08-24 08:10:00', 1, '2025-08-20 20:00:00', '2025-08-20 21:00:00'),
(5, 9, 'system', 9, NULL, 250, 1, '2025-08-25 19:00:00', '2025-08-25 20:00:00', 60, '2025-08-25 20:00:00', 1, '2025-08-20 20:00:00', '2025-08-20 21:00:00'),
(6, 10, 'system', 10, NULL, 300, 1, '2025-08-25 20:00:00', '2025-08-25 20:45:00', 45, '2025-08-25 20:45:00', 1, '2025-08-20 20:00:00', '2025-08-20 21:00:00'),
(7, 12, 'custom', NULL, 2, 25, 3, '2025-08-26 07:00:00', '2025-08-26 07:10:00', 10, '2025-08-26 07:15:00', 1, '2025-08-20 20:00:00', '2025-08-20 21:00:00'),
(8, 12, 'custom', NULL, 2, 25, 3, '2025-08-26 12:30:00', '2025-08-26 12:40:00', 10, '2025-08-26 13:00:00', 1, '2025-08-20 20:00:00', '2025-08-20 21:00:00'),
(9, 12, 'custom', NULL, 2, 25, 3, '2025-08-26 18:30:00', '2025-08-26 18:40:00', 10, '2025-08-26 21:40:00', 1, '2025-08-20 20:00:00', '2025-08-20 21:00:00');
