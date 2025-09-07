/* usersテーブル */
INSERT IGNORE INTO users (id, name, furigana, phone_number, email, password, enabled) VALUES 
(1, '山田　太郎', 'ヤマダ　タロウ','090-1234-5678', 'taro.yamada@example.com', '$2a$10$2JNjTwZBwo7fprL2X4sv.OEKqxnVtsVQvuXDkI8xVGix.U3W5B7CO', true),
(2, '岡本　花子', 'オカモト　ハナコ','090-1234-5678', 'hanako.okamoto@example.com', '$2a$10$2JNjTwZBwo7fprL2X4sv.OEKqxnVtsVQvuXDkI8xVGix.U3W5B7CO', true),
(3, '伊藤　英二', 'イトウ　エイジ','090-1234-5678', 'eiji.ito@example.com', '$2a$10$2JNjTwZBwo7fprL2X4sv.OEKqxnVtsVQvuXDkI8xVGix.U3W5B7CO', true),
(4, '佐藤　信子', 'サトウ　ノブコ','090-1234-5678', 'nobuko.sato@example.com', '$2a$10$2JNjTwZBwo7fprL2X4sv.OEKqxnVtsVQvuXDkI8xVGix.U3W5B7CO', true),
(5, '木下　純一', 'キノシタ　ジュンイチ','090-1234-5678', 'junichi.kinoshita@example.com', '$2a$10$2JNjTwZBwo7fprL2X4sv.OEKqxnVtsVQvuXDkI8xVGix.U3W5B7CO', true);