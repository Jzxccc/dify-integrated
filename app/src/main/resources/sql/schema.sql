-- 创建 AppInteraction 表用于存储与特定 Dify 应用的交互数据
CREATE TABLE app_interactions (
    id BIGSERIAL PRIMARY KEY,
    interaction_id VARCHAR(255) NOT NULL UNIQUE,
    app_id VARCHAR(255) NOT NULL,
    user_id VARCHAR(255),
    input TEXT NOT NULL,
    output TEXT,
    timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    metadata JSONB,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 为 app_id 创建索引以优化按应用 ID 查询的性能
CREATE INDEX idx_app_interactions_app_id ON app_interactions(app_id);

-- 为 user_id 创建索引以优化按用户 ID 查询的性能
CREATE INDEX idx_app_interactions_user_id ON app_interactions(user_id);

-- 为 timestamp 创建索引以优化按时间范围查询的性能
CREATE INDEX idx_app_interactions_timestamp ON app_interactions(timestamp);

-- 创建更新时间戳的触发器函数
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- 为 app_interactions 表创建更新时间戳的触发器
CREATE TRIGGER update_app_interactions_updated_at 
    BEFORE UPDATE ON app_interactions 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

-- 示例插入数据（可选，仅作演示）
-- INSERT INTO app_interactions (interaction_id, app_id, user_id, input, output, metadata) 
-- VALUES ('int_001', 'd2a5c47c-5644-49f0-bc20-6a67ac1a7b69', 'user_123', 'Hello, how are you?', 'I am fine, thank you!', '{"session_id": "sess_456"}');