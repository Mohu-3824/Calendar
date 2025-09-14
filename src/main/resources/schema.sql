DELETE FROM task_log;
CREATE TABLE IF NOT EXISTS users (
	id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(50) NOT NULL,
	furigana VARCHAR(50) NOT NULL,
	phone_number VARCHAR(50) NOT NULL,
	email VARCHAR(255) NOT NULL UNIQUE,
	password VARCHAR(255) NOT NULL,
	enabled BOOLEAN NOT NULL,
	created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP	
);

CREATE TABLE IF NOT EXISTS task_master (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    category_code VARCHAR(50) NOT NULL,    -- study / beauty / exercise etc.
    repeat_type VARCHAR(10) DEFAULT 'none', -- none / repeat
    repeat_frequency VARCHAR(10) DEFAULT NULL, -- daily / weekly / monthly
    repeat_weekdays VARCHAR(50) DEFAULT NULL,  -- 'Mon,Wed,Fri'
    repeat_month_day INT DEFAULT NULL,         -- 1〜31
    repeat_end_date DATE DEFAULT NULL,         -- 繰り返し終了日
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS task_log (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    task_id INT NOT NULL,                  -- マスターのID
    user_id INT NOT NULL,
    log_date DATE NOT NULL,                -- 実際の日付
    done BOOLEAN NOT NULL DEFAULT FALSE,   -- 達成/未達成
    current_streak INT NOT NULL DEFAULT 0, -- その時点の連続日数
    max_streak INT NOT NULL DEFAULT 0,     -- 履歴上の最大連続日数
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (task_id) REFERENCES task_master(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT uq_task_log UNIQUE (task_id, user_id, log_date)
);

	