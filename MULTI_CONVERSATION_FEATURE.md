# Multi-Conversation Feature Guide

This document explains how to use the multi-conversation feature with user authentication in the Dify Integration application.

## Overview

The multi-conversation feature allows users to:
- Create and manage multiple conversation sessions
- Maintain conversation context across multiple messages
- Authenticate users to ensure session privacy
- Access conversation history

## API Endpoints

### Authentication Endpoints

- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration
- `POST /api/auth/logout` - User logout

### Conversation Endpoints

- `POST /api/conversations` - Create a new conversation
- `GET /api/conversations/{conversationId}` - Get specific conversation details
- `PUT /api/conversations/{conversationId}/end` - End a conversation
- `GET /api/conversations` - Get all conversations for the authenticated user

### App Interaction Endpoints (Authenticated)

- `POST /api/authenticated/app/{appId}/chat` - Send a message in a conversation
- `POST /api/authenticated/app/{appId}/chat-stream` - Send a message with streaming response
- `GET /api/authenticated/app/{appId}/history` - Get conversation history
- `GET /api/authenticated/app/{appId}/conversations` - Get user's conversations for an app

## Using the Multi-Conversation Feature

### 1. User Registration and Login

First, users need to register and authenticate:

```bash
# Register a new user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "your_username",
    "email": "your_email@example.com",
    "password": "your_password"
  }'

# Login to get JWT token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "your_username",
    "password": "your_password"
  }'
```

### 2. Creating a New Conversation

After authentication, create a new conversation:

```bash
curl -X POST http://localhost:8080/api/conversations \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "appId": "your_app_id"
  }'
```

### 3. Sending Messages in a Conversation

Send messages while maintaining conversation context:

```bash
curl -X POST http://localhost:8080/api/authenticated/app/your_app_id/chat \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "query": "Your message here",
    "conversationId": "conversation_id_from_step_2"
  }'
```

### 4. Getting Conversation History

Retrieve the history of a specific conversation:

```bash
curl -X GET "http://localhost:8080/api/authenticated/app/your_app_id/history?conversationId=conversation_id" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 5. Getting All User Conversations

Get all conversations for the authenticated user:

```bash
curl -X GET http://localhost:8080/api/authenticated/app/your_app_id/conversations \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Frontend Usage

The application provides a web interface at `http://localhost:8080` with:

1. Login/Registration pages
2. Chat interface with session management
3. Ability to create new sessions
4. Ability to load and switch between existing sessions
5. Real-time streaming responses

## Security Features

- JWT-based authentication for all conversation-related endpoints
- Session ownership verification to ensure users can only access their own conversations
- Conversation ID validation to prevent unauthorized access
- User-specific conversation filtering

## Configuration

Make sure to configure the following in `application.properties`:

```properties
# Dify API Configuration
dify.api.base-url=https://api.dify.ai/v1
dify.api.default-api-key=your_dify_api_key_here

# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/dify_integration
spring.datasource.username=your_db_username
spring.datasource.password=your_db_password

# JWT Configuration
jwt.secret=your_secure_jwt_secret
jwt.expiration=86400000  # 24 hours in milliseconds
```

## Database Schema

The application uses the following key tables:

- `users`: Stores user information
- `conversations`: Stores conversation metadata
- `app_interactions`: Stores individual message exchanges

## Error Handling

Common error responses:

- `401 Unauthorized`: Invalid or missing JWT token
- `403 Forbidden`: Attempting to access another user's conversation
- `404 Not Found`: Conversation does not exist