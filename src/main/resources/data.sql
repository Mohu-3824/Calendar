/* usersテーブル */
INSERT IGNORE INTO users (id, name, furigana, phone_number, email, password, enabled) VALUES 
(1, '山田　太郎', 'ヤマダ　タロウ','090-1234-5678', 'taro.yamada@example.com', 'password', true),
(2, '岡本　花子', 'オカモト　ハナコ','090-1234-5678', 'hanako.okamoto@example.com', '$2a$10$2JNjTwZBwo7fprL2X4sv.OEKqxnVtsVQvuXDkI8xVGix.U3W5B7CO', true),
(3, '伊藤　英二', 'イトウ　エイジ','090-1234-5678', 'eiji.ito@example.com', '$2a$10$2JNjTwZBwo7fprL2X4sv.OEKqxnVtsVQvuXDkI8xVGix.U3W5B7CO', true),
(4, '佐藤　信子', 'サトウ　ノブコ','090-1234-5678', 'nobuko.sato@example.com', '$2a$10$2JNjTwZBwo7fprL2X4sv.OEKqxnVtsVQvuXDkI8xVGix.U3W5B7CO', true),
(5, '木下　純一', 'キノシタ　ジュンイチ','090-1234-5678', 'junichi.kinoshita@example.com', '$2a$10$2JNjTwZBwo7fprL2X4sv.OEKqxnVtsVQvuXDkI8xVGix.U3W5B7CO', true);

/* tasksテーブル */
INSERT IGNORE INTO tasks (id, mydate, title, category_code, done, current_streak, max_streak, repeat_type, repeat_frequency, repeat_weekdays, repeat_month_day) VALUES 
-- 毎週（月・水・金）の筋トレ
(1, '2025-09-14', '筋トレ', 'exercise', true, 0, 0,
 'repeat', 'weekly', 'Mon,Wed,Fri', NULL),
-- 毎月15日の読書
(2, '2025-09-15', '読書', 'hobby', true, 0, 0,
 'repeat', 'monthly', NULL, 15),
-- 繰り返しなしの勉強
(3, '2025-09-16', '勉強', 'study', false, 0, 0,
 'none', NULL, NULL, NULL);