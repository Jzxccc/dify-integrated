-- Dify Integration Platform - Database Schema

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(255) UNIQUE NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE
);

-- Create conversations table
CREATE TABLE IF NOT EXISTS conversations (
    id BIGSERIAL PRIMARY KEY,
    conversation_id VARCHAR(255) UNIQUE NOT NULL,
    app_id VARCHAR(255) NOT NULL,
    user_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE,
    ended_at TIMESTAMP WITH TIME ZONE,
    metadata JSONB,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Create app_interactions table
CREATE TABLE IF NOT EXISTS app_interactions (
    id BIGSERIAL PRIMARY KEY,
    interaction_id VARCHAR(255) UNIQUE NOT NULL,
    app_id VARCHAR(255) NOT NULL,
    user_id VARCHAR(255),
    conversation_id VARCHAR(255),
    input TEXT NOT NULL,
    output TEXT,
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL,
    metadata JSONB,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE
);

-- Create api_keys table
CREATE TABLE IF NOT EXISTS api_keys (
    id BIGSERIAL PRIMARY KEY,
    api_key_value VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_conversations_user_id ON conversations(user_id);
CREATE INDEX IF NOT EXISTS idx_conversations_app_id ON conversations(app_id);
CREATE INDEX IF NOT EXISTS idx_conversations_status ON conversations(status);
CREATE INDEX IF NOT EXISTS idx_app_interactions_user_id ON app_interactions(user_id);
CREATE INDEX IF NOT EXISTS idx_app_interactions_app_id ON app_interactions(app_id);
CREATE INDEX IF NOT EXISTS idx_app_interactions_conversation_id ON app_interactions(conversation_id);

-- Insert sample data
-- Note: Passwords should be properly hashed in production (using BCrypt or similar)
-- The following user has password 'password' (plain text for demo purposes only)
INSERT INTO users (user_id, username, email, password, active)
SELECT 'user_123456789', 'demo_user', 'demo@example.com', '$2a$10$vDtCxgJ8YqHl2qK8YqHl2O7YqHl2qK8YqHl2qK8YqHl2qK8YqHl2q', true
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'demo_user');

-- Insert sample conversation
INSERT INTO conversations (conversation_id, app_id, user_id, status)
SELECT 'conv_123456789', 'd2a5c47c-5644-49f0-bc20-6a67ac1a7b69', id, 'ACTIVE'
FROM users
WHERE username = 'demo_user'
AND NOT EXISTS (SELECT 1 FROM conversations WHERE conversation_id = 'conv_123456789');

-- Insert sample API key (for demo purposes)
INSERT INTO api_keys (api_key_value)
SELECT 'your-dify-api-key-here'
WHERE NOT EXISTS (SELECT 1 FROM api_keys WHERE api_key_value = 'your-dify-api-key-here');