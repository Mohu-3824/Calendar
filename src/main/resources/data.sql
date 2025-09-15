/* usersテーブル */
INSERT IGNORE INTO users (id, name, furigana, phone_number, email, password, enabled) VALUES 
(1, '山田　太郎', 'ヤマダ　タロウ','090-1234-5678', 'taro.yamada@example.com', 'password', true),
(2, '岡本　花子', 'オカモト　ハナコ','090-1234-5678', 'hanako.okamoto@example.com', '$2a$10$2JNjTwZBwo7fprL2X4sv.OEKqxnVtsVQvuXDkI8xVGix.U3W5B7CO', true),
(3, '伊藤　英二', 'イトウ　エイジ','090-1234-5678', 'eiji.ito@example.com', '$2a$10$2JNjTwZBwo7fprL2X4sv.OEKqxnVtsVQvuXDkI8xVGix.U3W5B7CO', true),
(4, '佐藤　信子', 'サトウ　ノブコ','090-1234-5678', 'nobuko.sato@example.com', '$2a$10$2JNjTwZBwo7fprL2X4sv.OEKqxnVtsVQvuXDkI8xVGix.U3W5B7CO', true),
(5, '木下　純一', 'キノシタ　ジュンイチ','090-1234-5678', 'junichi.kinoshita@example.com', '$2a$10$2JNjTwZBwo7fprL2X4sv.OEKqxnVtsVQvuXDkI8xVGix.U3W5B7CO', true);

/* task_masterテーブル */
INSERT IGNORE INTO task_master 
(user_id, title, category_code, repeat_type, repeat_frequency, repeat_weekdays, repeat_month_day, repeat_end_date) 
VALUES
-- 🏋️‍♂️ ユーザー1: 週3回の筋トレ（無期限）
(1, '筋トレ', 'exercise', 'repeat', 'weekly', 'Mon,Wed,Fri', NULL, NULL),

-- 📚 ユーザー1: 毎月15日読書（2025年12月まで）
(1, '読書', 'hobby', 'repeat', 'monthly', NULL, 15, '2025-12-31'),

-- 🧠 ユーザー1: 英語学習（繰り返しなし）
(1, '英語学習', 'study', 'none', NULL, NULL, NULL, NULL),

-- 🏃‍♀️ ユーザー2: 毎日ランニング（2025年9月末まで）
(2, 'ランニング', 'exercise', 'repeat', 'daily', NULL, NULL, '2025-09-30'),

-- 🧹 ユーザー2: 毎週土曜の掃除（無期限）
(2, '部屋の掃除', 'lifestyle', 'repeat', 'weekly', 'Sat', NULL, NULL);

/* task_logテーブル */
INSERT IGNORE INTO task_log 
(task_id, user_id, log_date, done, current_streak, max_streak) 
VALUES
-- ユーザー1: 筋トレ（前日失敗→今日成功）
(1, 1, '2025-09-13', FALSE, 0, 3),
(1, 1, '2025-09-14', TRUE, 1, 3),

-- ユーザー1: 読書（当日成功）
(2, 1, '2025-09-14', TRUE, 4, 4),

-- ユーザー1: 英語学習（未達成）
(3, 1, '2025-09-14', FALSE, 0, 2),

-- ユーザー2: ランニング（連続達成中）
(4, 2, '2025-09-13', TRUE, 6, 6),
(4, 2, '2025-09-14', TRUE, 7, 7),

-- ユーザー2: 掃除（今週の土曜日）
(5, 2, '2025-09-13', TRUE, 2, 3);