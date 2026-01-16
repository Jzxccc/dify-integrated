# 系统接入规范

## 概述
本文档描述了外部系统如何接入Dify智能体集成平台。通过标准化的API接口和认证机制，外部系统可以安全地使用平台的核心功能。

## 接入方式

### 1. API接入
外部系统可以通过RESTful API与平台进行交互。

#### 认证方式
- 使用JWT Token进行认证
- 首次接入需要通过用户名/密码获取Token
- Token有效期为24小时，需要定期刷新

#### 基础URL
```
https://your-domain.com/api
```

### 2. SDK接入（未来计划）
提供多种语言的SDK简化接入过程。

## API端点

### 认证相关
- `POST /api/auth/login` - 用户登录，获取JWT Token
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/logout` - 用户登出

### 会话管理
- `POST /api/conversations` - 创建新会话
- `GET /api/conversations` - 获取用户的所有会话
- `GET /api/conversations/{conversationId}` - 获取特定会话详情
- `PUT /api/conversations/{conversationId}/end` - 结束会话

### 应用交互（需认证）
- `POST /api/authenticated/app/{appId}/chat` - 发送消息到指定应用
- `GET /api/authenticated/app/{appId}/history` - 获取会话历史
- `GET /api/authenticated/app/{appId}/conversations` - 获取用户在此应用的会话列表

## 接入流程

### 1. 注册应用
外部系统需要先在平台注册，获取应用ID和密钥。

### 2. 用户认证
外部系统需要引导用户完成认证流程：
1. 用户通过外部系统界面输入平台账号密码
2. 外部系统调用`/api/auth/login`获取JWT Token
3. 将Token存储在外部系统中（注意安全存储）

### 3. 使用服务
获得Token后，外部系统可以代表用户调用受保护的API端点。

## 安全考虑

### 认证和授权
- 所有敏感操作都需要有效的JWT Token
- Token具有用户作用域，用户只能访问自己的数据
- 实施速率限制防止滥用

### 数据安全
- 所有API通信必须使用HTTPS
- 敏感数据在传输和存储时加密
- 实施最小权限原则

## 错误处理

API将返回标准HTTP状态码：
- `200 OK` - 请求成功
- `400 Bad Request` - 请求参数错误
- `401 Unauthorized` - 认证失败
- `403 Forbidden` - 权限不足
- `404 Not Found` - 资源不存在
- `500 Internal Server Error` - 服务器错误

## 示例

### 使用curl接入
```bash
# 1. 用户登录获取Token
TOKEN=$(curl -X POST https://your-domain.com/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"your_username", "password":"your_password"}' \
  | jq -r ".token")

# 2. 创建新会话
CONVERSATION_ID=$(curl -X POST https://your-domain.com/api/conversations \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"appId":"your_app_id"}' \
  | jq -r ".conversationId")

# 3. 发送消息
RESPONSE=$(curl -X POST https://your-domain.com/api/authenticated/app/your_app_id/chat \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{\"query\":\"Hello\", \"conversationId\":\"$CONVERSATION_ID\"}")
```

### 使用Python接入
```python
import requests

class DifyIntegrationClient:
    def __init__(self, base_url):
        self.base_url = base_url
        self.token = None
    
    def login(self, username, password):
        response = requests.post(
            f"{self.base_url}/api/auth/login",
            json={"username": username, "password": password}
        )
        if response.status_code == 200:
            self.token = response.json()["token"]
            return True
        return False
    
    def create_conversation(self, app_id):
        headers = {"Authorization": f"Bearer {self.token}"}
        response = requests.post(
            f"{self.base_url}/api/conversations",
            headers=headers,
            json={"appId": app_id}
        )
        return response.json()
    
    def send_message(self, app_id, message, conversation_id=None):
        headers = {"Authorization": f"Bearer {self.token}"}
        payload = {"query": message}
        if conversation_id:
            payload["conversationId"] = conversation_id
            
        response = requests.post(
            f"{self.base_url}/api/authenticated/app/{app_id}/chat",
            headers=headers,
            json=payload
        )
        return response.json()

# 使用示例
client = DifyIntegrationClient("https://your-domain.com")
if client.login("username", "password"):
    conv = client.create_conversation("your_app_id")
    response = client.send_message("your_app_id", "Hello", conv["conversationId"])
    print(response)
```

## 最佳实践

1. **Token管理**: 安全存储JWT Token，实现自动刷新机制
2. **错误处理**: 实现重试机制和优雅的错误处理
3. **速率限制**: 遵循API速率限制，避免对服务器造成压力
4. **日志记录**: 记录API调用以便调试和监控
5. **数据验证**: 验证从API返回的数据完整性

## 支持和文档

- API文档: https://your-domain.com/docs/api
- SDK文档: https://your-domain.com/docs/sdk
- 社区支持: https://your-domain.com/support
- 问题报告: https://github.com/your-org/issues