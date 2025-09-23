/* usersテーブル */
INSERT IGNORE INTO users (id, name, furigana, phone_number, email, password, enabled) VALUES 
(1, '山田　太郎', 'ヤマダ　タロウ','090-1234-5678', 'taro.yamada@example.com', 'password', true),
(2, '岡本　花子', 'オカモト　ハナコ','090-1234-5678', 'hanako.okamoto@example.com', 'password', true),
(3, '伊藤　英二', 'イトウ　エイジ','090-1234-5678', 'eiji.ito@example.com', 'password', true),
(4, '佐藤　信子', 'サトウ　ノブコ','090-1234-5678', 'nobuko.sato@example.com', 'password', true),
(5, '木下　純一', 'キノシタ　ジュンイチ','090-1234-5678', 'junichi.kinoshita@example.com', 'password', true);

/* categoriesテーブル */
INSERT IGNORE INTO categories(id, user_id, category_name, icon_image, color_code) VALUES
(1, 1, '運動', NULL, '#ffff00'),
(2, 1, '勉強', NULL, '#ffc0cb'),
(3, 1, '趣味', NULL, '#90ee90'),
(4, 1, '生活', NULL, '#ffa500'),
(5, 1, '仕事', NULL, '#87cefa'),
(6, 1, '休養', NULL, '#ee82ee');


/* tasksテーブル */
INSERT IGNORE INTO tasks(user_id, category_id, title, log_date, status) VALUES
-- 🏋️‍♂️ ユーザー1: 筋トレ, 2025/9/20（未完了）
(1, 1, '筋トレ', '2025-09-20', false),

-- 📚 ユーザー1: 読書, 2025/9/20（未完了）
(1, 3, '読書', '2025-09-20', false),

-- 🧠 ユーザー1: 英語学習, 2025/9/20（完了）
(1, 2, '英語学習', '2025-09-20', true),

-- 🏃‍♀️ ユーザー2: ランニング, 2025/9/19（完了）
(2, 1, 'ランニング', '2025-09-19', true),

-- 🧹 ユーザー2: 部屋の掃除, 2025/9/20（完了）
(2, 4, '部屋の掃除', '2025-09-20', true);
