# Wang AI Agent - 智能学习与工作效率助手

## 项目简介

Wang AI Agent 是一个基于 Spring Boot 3.4.8 和 Spring AI 框架构建的智能学习与工作效率助手系统。该系统集成了多种AI能力，包括学习规划、智能工具调用、RAG检索增强生成等功能，旨在为用户提供个性化的学习路径规划和高效的工作自动化解决方案。

## 🚀 核心功能

### 1. 学习规划大师 (StudyApp)
- **个性化学习路径生成**：基于用户目标和时间制定系统化学习计划
- **RAG检索增强**：集成向量数据库，提供智能知识检索和问答
- **流式响应**：支持SSE实时流式输出，提供即时反馈
- **会话记忆管理**：自动维护对话上下文，支持多轮交互

### 2. 超级智能体 (WangManus)
- **多工具协作**：集成多种实用工具，支持复杂任务处理
- **智能工具选择**：基于任务需求自动选择最合适的工具组合
- **任务分解执行**：将复杂任务拆分为多个步骤逐步完成
- **实时状态反馈**：提供详细的执行过程和结果反馈

## 🛠️ 技术架构

### 后端技术栈
- **框架**：Spring Boot 3.4.8 + Spring AI 1.0.0-M6
- **语言**：Java 21
- **AI模型**：阿里云百炼/灵积大模型 (DashScope)
- **向量数据库**：PostgreSQL + PGVector / 内存向量存储
- **工具集成**：MCP (Model Context Protocol) 客户端以及自定义工具类
- **API文档**：Knife4j OpenAPI 3

### 前端技术栈
- **框架**：Vue 3.4 + Vue Router 4.2
- **构建工具**：Vite 4.5
- **HTTP客户端**：Axios 1.6
- **部署**：支持 Vercel 部署以及微信云托管

### 核心组件

#### 智能体架构
- **BaseAgent**：抽象基础代理类，管理代理状态和执行流程
- **ToolCallAgent**：工具调用智能体，支持多工具协作
- **ReActAgent**：ReAct模式智能体，支持推理和行动
- **WangManus**：超级智能体，集成多种工具能力

#### 工具集成
- **GoogleWebSearchTool**：基于SerpApi的Google搜索
- **FileOperationTool**：文件操作工具
- **PDFGenerationTool**：PDF文档生成
- **TerminalOperationTool**：终端命令执行
- **ResourceDownloadTool**：资源下载工具
- **TerminateTool**：任务终止工具

#### RAG检索增强
- **StudyAppDocumentLoader**：文档加载器
- **MyTokenTextSplitter**：自定义文本分割器
- **MyKeywordEnricher**：关键词增强器
- **QueryRewriter**：查询重写器
- **向量存储**：支持PGVector和内存向量存储

## 📁 项目结构

```
wang-ai-agent/
├── src/main/java/com/wangdazhi/wangaiagent/
│   ├── agent/                 # 智能体核心模块
│   │   ├── BaseAgent.java     # 基础代理类
│   │   ├── ToolCallAgent.java # 工具调用代理
│   │   ├── WangManus.java     # 超级智能体
│   │   └── model/             # 智能体模型
│   ├── tools/                 # 工具集成模块
│   │   ├── GoogleWebSearchTool.java
│   │   ├── FileOperationTool.java
│   │   ├── PDFGenerationTool.java
│   │   └── ToolRegistration.java
│   ├── rag/                   # RAG检索增强模块
│   │   ├── StudyAppDocumentLoader.java
│   │   ├── StudyAppVectorStoreConfig.java
│   │   └── QueryRewriter.java
│   ├── controller/            # API控制器
│   │   ├── AiController.java  # AI接口控制器
│   │   └── HealthController.java
│   ├── config/                # 配置模块
│   ├── constant/              # 常量定义
│   └── chatmemory/            # 会话记忆管理
├── wang-ai-agent-frontend/    # Vue前端应用
│   ├── src/
│   │   ├── pages/             # 页面组件
│   │   ├── components/        # 通用组件
│   │   └── api/               # API接口
│   └── package.json
├── wang-image-search-mcp-server/ # MCP图像搜索服务
└── Dockerfile                 # Docker部署配置
```

## 🚀 快速开始

### 环境要求
- Java 21+
- Maven 3.9+
- Node.js 16+ (前端开发)
- PostgreSQL (可选，用于向量存储)

### 后端启动
```bash
# 克隆项目
git clone <repository-url>
cd wang-ai-agent

# 配置环境变量
# 在 application.yml 中配置以下参数：
# - spring.ai.dashscope.api-key: 阿里云灵积API密钥
# - search-api.api-key: SerpApi搜索密钥

# 编译运行
mvn clean package
java -jar target/wang-ai-agent-0.0.1-SNAPSHOT.jar
```

### 前端启动
```bash
cd wang-ai-agent-frontend

# 安装依赖
npm install

# 开发模式
npm run dev

# 构建生产版本
npm run build
```

### Docker部署
```bash
# 构建镜像
docker build -t wang-ai-agent .

# 运行容器
docker run -p 8123:8123 wang-ai-agent
```

## 📡 API接口

### 学习规划接口
- `GET /ai/studyApp/chat/sync` - 同步聊天接口
- `GET /ai/studyApp/chat/sse` - SSE流式聊天接口
- `GET /ai/studyApp/chat/ServerSentEvent` - Server-Sent Events接口
- `GET /ai/studyApp/chat/SSEEmiter` - SSE Emitter接口

### 超级智能体接口
- `GET /ai/manus/chat` - WangManus智能体聊天接口

### 健康检查
- `GET /health` - 服务健康状态检查

## 🔧 配置说明

### 核心配置项
```yaml
spring:
  ai:
    dashscope:
      api-key: ${DASHSCOPE_API_KEY}  # 阿里云灵积API密钥
    
search-api:
  api-key: ${SERPAPI_KEY}  # SerpApi搜索密钥

server:
  port: 8123  # 服务端口
```

### 向量数据库配置
- **内存向量存储**：默认配置，适合开发和测试
- **PGVector存储**：生产环境推荐，支持持久化存储

## 🎯 使用场景

### 学习规划场景
1. **目标设定**：用户设定学习目标和时间框架
2. **路径生成**：系统生成个性化学习路径和每日任务
3. **进度跟踪**：实时跟踪学习进度和里程碑
4. **知识检索**：基于RAG技术提供相关知识问答

### 智能体工作场景
1. **信息调研**：使用Google搜索工具获取最新信息
2. **文档处理**：PDF生成、文件操作等自动化任务
3. **资源下载**：自动下载和管理相关资源
4. **任务执行**：通过终端工具执行系统命令

## 🔒 安全特性

- **违禁词拦截**：集成敏感词过滤，确保内容安全
- **API密钥管理**：环境变量配置，避免密钥泄露
- **输入验证**：严格的输入参数验证和异常处理
- **会话隔离**：独立的会话记忆管理，保护用户隐私

## 📈 性能优化

- **流式响应**：SSE技术实现实时流式输出
- **向量缓存**：内存向量存储提升检索性能
- **异步处理**：支持异步任务执行和回调
- **资源管理**：自动清理临时文件和会话资源

## 🤝 贡献指南

1. Fork 项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 📞 联系方式

- 项目维护者：王得志
- 项目地址：[GitHub Repository]
- 问题反馈：[Issues]

---

**让AI成为你的学习伙伴和工作助手！** 🚀
