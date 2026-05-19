# Gonio 项目完整Wiki

> **Gonio** - 面向生产环境的 Golang 后端脚手架。基于 Gin + GORM + Redis + MySQL，内置限流、JWT、Clean Architecture、消息队列等核心能力。

## 目录
1. [项目概述](#项目概述)
2. [环境搭建](#环境搭建)
3. [启动流程](#启动流程)
4. [目录结构](#目录结构)
5. [核心模块说明](#核心模块说明)
6. [模块分工与协作](#模块分工与协作)
7. [部署运维](#部署运维)

---

## 项目概述

### 项目定位

**Gonio** 是一个面向生产环境的 **Go API 后端脚手架**，设计目标：
- 开箱即用：无需复杂配置，快速搭建 API 服务
- 生产就绪：内置高并发、限流、认证、日志、监控等企业级能力
- 清晰分层：遵循 Clean Architecture 设计，易于扩展和维护
- 可观测性强：完善的日志、链路追踪、健康检查

### 技术栈

| 组件 | 版本/库 | 用途 |
|------|--------|------|
| **Web 框架** | Gin 1.12.0 | REST API 路由、中间件处理 |
| **ORM** | GORM 1.31.1 | MySQL 数据库操作 |
| **MySQL** | go-sql-driver/mysql | MySQL 数据库驱动 |
| **缓存** | Redis 9.18.0 | 缓存、限流、分布式锁、消息队列 |
| **消息队列** | Watermill 1.5.1 | 事件驱动、异步处理 |
| **日志** | Zap 1.27.1 + Lumberjack | 结构化日志、文件轮转 |
| **认证** | JWT (golang-jwt/jwt/v5) | Token 认证、权限验证 |
| **验证** | validator/v10 | 结构体字段验证 |
| **加密** | golang.org/x/crypto | 密码哈希、加密解密 |
| **配置管理** | Viper 1.21.0 | YAML 配置、环境变量覆盖 |

### 核心特性

✅ **Gin Web API 框架** - 轻量、快速的 HTTP 服务  
✅ **GORM + MySQL** - ORM 操作数据库，支持软删除、钩子等  
✅ **Redis 缓存与限流** - 基于 Redis Lua 的 IP + 路由限流（原子执行）  
✅ **JWT 认证** - 面向 app/admin 的 Token 认证  
✅ **消息队列** - 基于 Watermill 的事件驱动（Redis Streams / MySQL）  
✅ **多语言验证** - 国际化错误消息、自定义验证规则  
✅ **优雅停机** - Signal 处理、资源清理、连接释放  
✅ **结构化日志** - Zap 高性能日志、磁盘轮转、链路追踪  

---

## 环境搭建

### 系统要求

| 项 | 版本 |
|-----|------|
| **Go** | 1.25+ |
| **MySQL** | 8.0+ |
| **Redis** | 6.0+ |
| **操作系统** | Linux / macOS / Windows |

### 依赖安装

#### 1. 克隆项目

```bash
git clone https://github.com/your-username/gonio.git
cd gonio
```

#### 2. 下载 Go 依赖

```bash
go mod tidy
go mod download
```

#### 3. 安装 MySQL

**Docker 方式**（推荐）：
```bash
docker run --name mysql8 \
  -e MYSQL_ROOT_PASSWORD=123456 \
  -e MYSQL_DATABASE=silk_route \
  -p 3306:3306 \
  -d mysql:8.0
```

**本地安装**：
- macOS: `brew install mysql@8.0`
- Ubuntu: `sudo apt-get install mysql-server`
- Windows: 下载 [MySQL 官方安装器](https://dev.mysql.com/downloads/mysql/)

#### 4. 安装 Redis

**Docker 方式**（推荐）：
```bash
docker run --name redis \
  -p 6379:6379 \
  -d redis:7-alpine
```

**本地安装**：
- macOS: `brew install redis`
- Ubuntu: `sudo apt-get install redis-server`
- Windows: 下载 [Windows Redis](https://github.com/microsoftarchive/redis/releases)

### 配置文件说明

#### 配置文件位置
```
project/
└── config/
    ├── config.yaml           # 基础配置（所有环境）
    ├── config.dev.yaml       # 开发环境覆盖配置
    ├── config.prod.yaml      # 生产环境覆盖配置
    └── config.test.yaml      # 测试环境覆盖配置
```

#### 配置文件结构

**config.yaml** - 基础配置示例：

```yaml
server:
  port: 8080                 # HTTP 服务端口
  mode: release              # release / debug
  read_timeout: 10           # 请求读超时（秒）
  write_timeout: 10          # 响应写超时（秒）
  shutdown_timeout: 10       # 优雅停机超时（秒）
  auto_migrate: false        # 启动时是否自动迁移数据库
  cache_expire: 3600         # 商品缓存过期时间（秒）
  trusted_proxies: []        # 信任的代理 IP
  cors_origins: []           # CORS 允许源，空则允许所有

mysql:
  host: 127.0.0.1
  port: 3306
  username: root
  password: "123456"
  database: silk_route
  max_idle_conns: 10         # 最大空闲连接数
  max_open_conns: 100        # 最大开启连接数
  max_lifetime: 3600         # 连接最大存活时间（秒）
  conn_max_idle_time: 600    # 空闲连接最大存活时间（秒）
  dial_timeout: 5            # 连接超时（秒）
  read_timeout: 10           # 读超时（秒）
  write_timeout: 10          # 写超时（秒）
  ping_timeout: 3            # 探活超时（秒）
  prepare_stmt: true         # 缓存预编译语句（性能优化）
  skip_default_transaction: true  # 单条操作跳过事务包裹（性能优化）

redis:
  addr: 127.0.0.1:6379
  password: ""               # 无密码则为空
  db: 2                      # Redis 数据库号
  pool_size: 100             # 连接池大小
  min_idle_conns: 10         # 最小空闲连接
  max_idle_conns: 50         # 最大空闲连接
  pool_timeout: 5            # 获取连接超时（秒）
  dial_timeout: 5            # 连接超时（秒）
  read_timeout: 3            # 读超时（秒）
  write_timeout: 3           # 写超时（秒）
  conn_max_idle_time: 300    # 空闲连接最大存活时间（秒）
  conn_max_lifetime: 3600    # 连接最大存活时间（秒）
  ping_timeout: 3            # 探活超时（秒）
  max_retries: 3             # 命令失败最大重试次数
  min_retry_backoff: 8       # 最小重试退避时间（毫秒）
  max_retry_backoff: 512     # 最大重试退避时间（毫秒）

jwt:
  secret: "your-secret-key-change-me"  # JWT 签名密钥（必须修改！）
  expire: 7200               # Token 过期时间（秒）

mq:
  driver: redis              # redis 或 mysql（消息队列驱动）
  consumer_group: gonio-group  # 消费者组名
  topic_concurrency:         # 按 topic 配置并发消费者数
    email: 3
    sms: 1
    stats: 2
  default_max_len: 20        # stream 默认最大长度
  topic_max_len:             # 按 topic 单独设置最大长度
    email: 10
    sms: 2000
    stats: 10000
  trim_interval: 3600        # 定期 XTRIM 间隔（秒），0 为不启用

log:
  mode: prod                 # dev / prod
  level: info                # debug / info / warn / error
  sql_level: warn            # SQL 日志级别
  log_dir: logs              # 日志文件目录（空则仅输出控制台）
  max_size: 100              # 单文件最大 MB
  max_backups: 7             # 保留旧文件数
  max_age: 30                # 保留天数
  compress: true             # 是否压缩归档
```

#### 环境变量覆盖

所有配置都支持环境变量覆盖，格式：`APP_<CONFIG_PATH>`（`.` 替换为 `_`）

```bash
# 覆盖 server.port = 9000
export APP_SERVER_PORT=9000

# 覆盖 mysql.host = db.example.com
export APP_MYSQL_HOST=db.example.com

# 覆盖 jwt.secret
export APP_JWT_SECRET=my-production-secret-key
```

#### 环境切换

通过 `APP_ENV` 环境变量切换配置：
```bash
# 使用 config.dev.yaml（如果存在，自动合并到 config.yaml）
export APP_ENV=dev

# 使用 config.prod.yaml
export APP_ENV=prod

# 默认为 dev
export APP_ENV=dev
```

---

## 启动流程

### 启动入口

**`cmd/server/main.go`** - 应用程序入口

```go
func main() {
    if err := run(); err != nil {
        logger.Log.Errorw("server exited with error", "error", err)
    }
    logger.Log.Info("server exited")
}

func run() error {
    // 13 步完整启动流程
}
```

### 详细启动步骤

```
main() 
  └─> run()
      │
      ├─ 1️⃣  加载配置 (config.Load)
      │        └─> 读取 YAML + 环境变量覆盖 → Config 对象
      │
      ├─ 2️⃣  初始化日志 (logger.Init)
      │        └─> Zap + Lumberjack 配置 → 全局 logger.Log
      │
      ├─ 3️⃣  设置 Gin 模式 (gin.SetMode)
      │        └─> release / debug 模式
      │
      ├─ 4️⃣  初始化 MySQL (database.InitMySQL)
      │        ├─> DSN 构建 (含连接/超时参数)
      │        ├─> GORM 驱动打开
      │        ├─> 连接池配置
      │        └─> *gorm.DB → 全局变量
      │
      ├─ 5️⃣  初始化 Redis (database.InitRedis)
      │        ├─> 连接参数设置
      │        ├─> 连接池配置
      │        ├─> 自动重试策略
      │        └─> *redis.Client → 全局变量
      │
      ├─ 6️⃣  数据库迁移 (migration.AutoMigrate)
      │        └─> 如 config.server.auto_migrate = true，执行建表
      │
      ├─ 7️⃣  初始化 JWT (middleware.NewAuthMiddleware)
      │        └─> JWT secret 配置 → 认证中间件
      │
      ├─ 8️⃣  初始化验证器中文翻译 (validator.Init)
      │        └─> 为字段验证提供中文错误消息
      │
      ├─ 9️⃣  初始化多语言翻译 (i18n.Init)
      │        └─> 国际化错误消息
      │
      ├─ 🔟  初始化 MQ Publisher (mq.NewPublisher)
      │        ├─> 获取 sql.DB (从 gorm.DB)
      │        ├─> 根据 driver (redis/mysql) 创建 Publisher
      │        └─> *mq.Publisher → 全局变量
      │
      ├─ 1️⃣1️⃣  初始化 ServiceContext (svc.NewServiceContext)
      │        ├─> 创建 Repository 层 (productRepo, userRepo, adminRepo)
      │        ├─> 创建 Cache 层 (RedisCache)
      │        ├─> 创建 Service 层 (ProductSvc, UserSvc, AdminSvc)
      │        ├─> 创建限流器 (RateLimiter)
      │        └─> *ServiceContext → 依赖注入容器
      │
      ├─ 1️⃣2️⃣  初始化路由 (router.Setup)
      │        ├─> Gin Engine 创建
      │        ├─> 全局中间件注册
      │        │   ├─ RequestID (链路追踪)
      │        │   ├─ I18n (国际化)
      │        │   ├─ ReqRespLogger (请求响应日志)
      │        │   ├─ Recovery (异常恢复)
      │        │   ├─ CORS (跨域)
      │        │   └─ MaxBodySize (请求体限制)
      │        ├─> 健康检查路由
      │        ├─> App API 路由 (/app/v1/*)
      │        ├─> Admin API 路由 (/admin/v1/*)
      │        └─> *gin.Engine → HTTP Handler
      │
      ├─ 1️⃣3️⃣  初始化 MQ Router (mq.NewRouter)
      │        ├─> Watermill 路由器创建
      │        ├─> 重试中间件 (最多 3 次)
      │        ├─> 死信队列中间件
      │        ├─ 注册 Topic Handler
      │        │  ├─ mq.email → HandleEmail
      │        │  ├─ mq.sms → HandleSMS
      │        │  └─ mq.stats → HandleStats
      │        └─> *mq.Router → 异步处理
      │
      ├─ 1️⃣4️⃣  创建 App (NewApp)
      │        └─> 包装 HTTP Server + MQ Router + Redis
      │
      ├─ 1️⃣5️⃣  处理系统信号 (signal.NotifyContext)
      │        └─> 监听 SIGINT / SIGTERM → 优雅停机
      │
      └─ 1️⃣6️⃣  运行应用 (app.Run)
           ├─> 启动 HTTP 服务 (ListenAndServe)
           ├─> 启动 MQ 消费者
           ├─> 启动流 TRIM 定时器 (TTL 清理)
           ├─> 等待错误或信号
           └─> 优雅关闭资源
```

### 启动命令

```bash
# 开发运行（监听文件变化重启）
make run

# 编译构建
make build

# 运行二进制
./bin/go-yzy

# 使用 go run 直接运行
go run cmd/server/main.go

# 指定环境
APP_ENV=prod go run cmd/server/main.go
```

### 验证启动成功

```bash
# 健康检查（检测 MySQL 和 Redis 连通性）
curl http://localhost:8080/health

# 预期响应
{
  "code": 0,
  "msg": "ok",
  "data": null
}
```

---

## 目录结构

### 完整目录树

```
gonio/
├── cmd/                          # 应用程序入口
│   └── server/
│       ├── main.go              # 程序入口，启动流程
│       └── app.go               # App 结构体，HTTP/MQ 服务管理
│
├── config/                       # 配置文件（需要创建）
│   ├── config.yaml              # 基础配置
│   ├── config.dev.yaml          # 开发环境配置
│   ├── config.prod.yaml         # 生产环境配置
│   └── config.test.yaml         # 测试配置
│
├── internal/                     # 内部包（不对外暴露）
│   │
│   ├── config/                  # ⚙️ 配置管理
│   │   └── config.go            # 配置结构定义、加载、验证
│   │
│   ├── database/                # 🗄️ 数据库层
│   │   ├── mysql.go             # MySQL 初始化、连接池配置
│   │   ├── redis.go             # Redis 初始化、连接池配置
│   │   ├── gorm_logger.go       # GORM 日志适配器（Zap）
│   │   └── redis_hook.go        # Redis 命令日志钩子
│   │
│   ├── model/                   # 📊 数据模型
│   │   ├── base.go              # 基础模型（ID、CreatedAt、UpdatedAt）
│   │   ├── user.go              # 用户模型
│   │   ├── product.go           # 商品模型
│   │   └── admin.go             # 管理员模型
│   │
│   ├── repository/              # 📦 数据访问层 (DAO)
│   │   ├── user_repo.go         # 用户 Repository
│   │   ├── product_repo.go      # 商品 Repository
│   │   └── admin_repo.go        # 管理员 Repository
│   │
│   ├── service/                 # 🎯 业务逻辑层
│   │   ├── user_svc.go          # 用户服务
│   │   ├── user_svc_test.go     # 用户服务测试
│   │   ├── product_svc.go       # 商品服务（含缓存）
│   │   ├── product_svc_test.go  # 商品服务测试
│   │   └── admin_svc.go         # 管理员服务
│   │
│   ├── handler/                 # 🔗 HTTP 处理器层
│   │   ├── user_handler.go      # 用户 Handler
│   │   ├── product_handler.go   # 商品 Handler
│   │   ├── admin_handler.go     # 管理员 Handler
│   │   └── errors.go            # 错误处理辅助函数
│   │
│   ├── router/                  # 🛣️ 路由层
│   │   ├── router.go            # 主路由设置、全局中间件
│   │   ├── app/                 # App 端 API 路由
│   │   │   ├── user.go          # 用户相关路由
│   │   │   └── product.go       # 商品相关路由
│   │   └── admin/               # Admin 端 API 路由
│   │       ├── auth.go          # 登录/认证路由
│   │       └── product.go       # 后台商品管理路由
│   │
│   ├── middleware/              # 🎭 HTTP 中间件
│   │   ├── logger.go            # 日志中间件（Zap Logger）
│   │   ├── jwt.go               # JWT 认证中间件
│   │   ├── auth.go              # 权限检查中间件
│   │   ├── cors.go              # 跨域资源共享
│   │   ├── recovery.go          # 异常恢复（避免宕机）
│   │   ├── ratelimit.go         # IP + 路由限流
│   │   ├── maxbody.go           # 请求体大小限制
│   │   ├── requestid.go         # 请求链路追踪 ID
│   │   ├── i18n.go              # 国际化中间件
│   │   └── reqresp_logger.go    # 请求响应日志
│   │
│   ├── mq/                      # 📨 消息队列（事件驱动）
│   │   ├── publisher.go         # MQ 发布者
│   │   ├── router.go            # MQ 消费者路由
│   │   ├── handlers.go          # MQ 消息处理器
│   │   ├── payload.go           # MQ 消息负载定义
│   │   ├── topics.go            # Topic 常量定义
│   │   ├── logger.go            # Watermill 日志适配器
│   │   └── trimmer.go           # Redis Stream 定时清理
│   │
│   ├── svc/                     # 🔧 服务上下文（依赖注入）
│   │   └── service_context.go   # ServiceContext - 统一 DI 容器
│   │
│   └── pkg/                     # 📚 公共包
│       ├── logger/              # 日志工具
│       │   └── logger.go        # Zap 日志初始化、工具函数
│       ├── auth/                # 认证工具
│       │   └── jwt.go           # JWT 工具函数
│       ├── response/            # 响应工具
│       │   ├── response.go      # 标准响应结构体
│       │   └── login.go         # 登录响应
│       ├── errcode/             # 错误码定义
│       │   └── errcode.go       # 统一错误码常量
│       ├── i18n/                # 国际化
│       │   ├── i18n.go          # 翻译初始化
│       │   └── messages.go      # 错误信息翻译
│       ├── validator/           # 数据验证
│       │   └── validator.go     # 自定义验证规则 + 中文翻译
│       ├── req/                 # 请求参数定义
│       │   ├── user.go          # 用户相关请求参数
│       │   ├── product.go       # 商品相关请求参数
│       │   ├── admin.go         # 管理员相关请求参数
│       │   └── page.go          # 分页参数
│       ├── ratelimit/           # 限流
│       │   └── ratelimit.go     # Redis Lua 限流实现
│       └── cache/               # 缓存
│           ├── cache.go         # 缓存接口
│           └── redis.go         # Redis 缓存实现
│
├── migration/                   # 📌 数据库迁移脚本
│   └── migration.go             # 表结构初始化
│
├── test/                        # ✅ 测试文件
│   └── test_helper.go
│
├── learn/                       # 📖 学习参考
│   └── examples/                # 示例代码
│
├── go.mod                       # Go 模块定义（依赖版本）
├── go.sum                       # 依赖校验文件
├── Dockerfile                   # Docker 构建文件
├── Makefile                     # 构建脚本
├── .golangci.yml               # Lint 规则配置
├── .config-demo.yaml           # 配置文件示例
├── README.md                   # 项目说明（中文）
├── README.en.md                # 项目说明（英文）
└── WIKI.md                     # 项目文档（本文件）
```

### 目录分工说明

| 目录 | 职责 | 说明 |
|-----|------|------|
| `cmd/` | 程序入口 | 包含 main.go，负责启动流程、资源初始化 |
| `config/` | 配置文件 | YAML 配置、环境变量覆盖、配置加载 |
| `internal/config` | 配置管理 | 配置结构定义、验证、默认值 |
| `internal/database` | 数据库 | MySQL/Redis 初始化、连接池、日志钩子 |
| `internal/model` | 数据模型 | GORM 模型定义、表关系、字段约束 |
| `internal/repository` | 数据访问层 | CRUD 操作、复杂查询、SQL 抽象 |
| `internal/service` | 业务逻辑层 | 核心业务逻辑、事务处理、缓存、MQ 发布 |
| `internal/handler` | HTTP 处理 | 接收请求、调用 Service、返回响应 |
| `internal/router` | 路由定义 | API 端点注册、中间件配置、路由分组 |
| `internal/middleware` | 中间件 | 认证、限流、日志、CORS、异常处理 |
| `internal/mq` | 消息队列 | 事件发布、消费、处理器、重试机制 |
| `internal/svc` | 依赖注入 | ServiceContext 容器、单例管理 |
| `internal/pkg` | 公共工具 | logger、validator、response、errcode、cache 等 |
| `migration/` | 数据库迁移 | 表结构定义、初始数据 |
| `test/` | 测试文件 | 单元测试、集成测试 |

---

## 核心模块说明

### 1. HTTP 服务模块

**职责**：Web API 框架、路由、中间件、请求响应

**核心文件**：
- `internal/router/router.go` - 路由设置、全局中间件
- `internal/handler/*.go` - HTTP 处理器
- `internal/middleware/*.go` - 中间件

**架构流程**：
```
HTTP Request
    ↓
RequestID 中间件 (链路追踪)
    ↓
I18n 中间件 (国际化)
    ↓
ReqRespLogger 中间件 (请求日志)
    ↓
Recovery 中间件 (异常恢复)
    ↓
CORS 中间件 (跨域)
    ↓
MaxBodySize 中间件 (4MB 限制)
    ↓
路由匹配
    ├─ /health → 健康检查 (DB + Redis)
    ├─ /app/v1/* → App API
    │   ├─ JWT 认证
    │   ├─ Rate Limit 限流
    │   └─ Handler → Service → Repository → DB
    └─ /admin/v1/* → Admin API
        ├─ JWT 认证 (Admin)
        ├─ Rate Limit 限流
        └─ Handler → Service → Repository → DB
    ↓
Response 序列化
    ↓
HTTP Response
```

**关键中间件**：

| 中间件 | 功能 | 配置项 |
|--------|------|--------|
| RequestID | 链路追踪，为每个请求分配唯一 ID | — |
| I18n | 国际化，根据 Accept-Language 设置语言 | — |
| ReqRespLogger | 记录请求 URL、方法、响应码、耗时 | log.level |
| Recovery | 捕获 panic，避免服务宕机 | — |
| CORS | 跨域资源共享 | server.cors_origins |
| MaxBodySize | 限制请求体大小 (4MB) | — |
| JWT | Token 认证（app/admin） | jwt.secret, jwt.expire |
| RateLimit | IP + 路由 + 方法限流 | —（在路由处注册） |

**示例：注册 API 路由**

```go
// internal/router/app/product.go - App 端产品路由
func Routes(r *gin.RouterGroup, svcCtx *svc.ServiceContext) {
    g := r.Group("/products")
    
    // 列表接口：1 request / 1 second
    g.GET("", 
        middleware.RateLimit(svcCtx.RateLimiter, 1, 1),
        handler.ListProducts,
    )
    
    // 详情接口：无限流
    g.GET("/:id", handler.GetProduct)
}
```

### 2. 数据库模块（MySQL + GORM）

**职责**：数据持久化、连接池、事务、ORM 操作

**核心文件**：
- `internal/database/mysql.go` - MySQL 初始化
- `internal/database/gorm_logger.go` - SQL 日志
- `internal/model/*.go` - 数据模型
- `internal/repository/*.go` - 数据访问

**初始化流程**：

```go
func InitMySQL(cfg *config.MySQLConfig, logCfg *config.LogConfig) (*gorm.DB, error) {
    // 1. 超时参数设置
    dialTimeout := 5s   // 连接超时
    readTimeout := 10s  // 读超时
    writeTimeout := 10s // 写超时
    
    // 2. DSN 构建
    dsn := "user:password@tcp(host:3306)/database?charset=utf8mb4&parseTime=True..."
    
    // 3. GORM 配置（性能优化）
    &gorm.Config{
        PrepareStmt: true,            // 缓存预编译语句（减少 SQL 解析）
        SkipDefaultTransaction: true, // 单条操作跳过事务（提升 30% 性能）
    }
    
    // 4. 连接池配置
    sqlDB.SetMaxIdleConns(10)           // 最大空闲连接
    sqlDB.SetMaxOpenConns(100)          // 最大开启连接
    sqlDB.SetConnMaxLifetime(3600s)     // 连接最大存活时间
    sqlDB.SetConnMaxIdleTime(600s)      // 空闲连接最大存活时间
    
    return db, nil
}
```

**模型定义示例**：

```go
// internal/model/product.go
type Product struct {
    BaseModel
    Name        string  `gorm:"column:name;type:varchar(255);not null"`
    Description string  `gorm:"column:description;type:text"`
    Price       float64 `gorm:"column:price;type:decimal(10,2)"`
    Stock       int     `gorm:"column:stock;type:int"`
}

func (Product) TableName() string {
    return "products"
}
```

**Repository 层示例**：

```go
// internal/repository/product_repo.go
type ProductRepository struct {
    db *gorm.DB
}

func (r *ProductRepository) FindByID(ctx context.Context, id uint) (*model.Product, error) {
    var product model.Product
    if err := r.db.WithContext(ctx).First(&product, id).Error; err != nil {
        return nil, err
    }
    return &product, nil
}

func (r *ProductRepository) List(ctx context.Context, offset, limit int) ([]*model.Product, error) {
    var products []*model.Product
    if err := r.db.WithContext(ctx).Offset(offset).Limit(limit).Find(&products).Error; err != nil {
        return nil, err
    }
    return products, nil
}
```

**性能优化**：

| 项 | 优化策略 | 效果 |
|----|--------|------|
| 预编译语句 | `PrepareStmt: true` | 减少 SQL 解析开销 ~10% |
| 事务优化 | `SkipDefaultTransaction: true` | 单条操作性能 +30% |
| 连接池 | MaxOpenConns=100, MaxIdleConns=10 | 避免连接耗尽 |
| 查询优化 | 索引、Select 字段、避免 N+1 | 减少网络往返 |

### 3. Redis 缓存与限流模块

**职责**：缓存、分布式限流、消息队列后端、分布式锁

**核心文件**：
- `internal/database/redis.go` - Redis 初始化
- `internal/pkg/cache/redis.go` - 缓存实现
- `internal/pkg/ratelimit/ratelimit.go` - 限流实现

#### 3.1 缓存模块

**缓存接口**：

```go
// internal/pkg/cache/cache.go
type Cache interface {
    Get(ctx context.Context, key string) (string, error)
    Set(ctx context.Context, key string, val string, ttl time.Duration) error
    Delete(ctx context.Context, key string) error
}
```

**使用示例**：

```go
// 注入到 Service
type ProductService struct {
    repo  repository.ProductRepository
    cache cache.Cache
    ttl   int
}

// 缓存读写
func (s *ProductService) GetProductDetail(ctx context.Context, id uint) (*model.Product, error) {
    cacheKey := fmt.Sprintf("product:%d", id)
    
    // 1. 先查缓存
    if cached, err := s.cache.Get(ctx, cacheKey); err == nil {
        // 缓存命中，反序列化
        var product model.Product
        json.Unmarshal([]byte(cached), &product)
        return &product, nil
    }
    
    // 2. 缓存未命中，查数据库
    product, err := s.repo.FindByID(ctx, id)
    if err != nil {
        return nil, err
    }
    
    // 3. 写入缓存
    data, _ := json.Marshal(product)
    s.cache.Set(ctx, cacheKey, string(data), time.Duration(s.ttl)*time.Second)
    
    return product, nil
}
```

#### 3.2 限流模块（IP + 路由 + 方法）

**限流原理**：使用 Redis Lua 脚本，原子执行滑动窗口算法

```go
// internal/pkg/ratelimit/ratelimit.go
type RateLimiter struct {
    rdb *redis.Client
}

// 限流 key = "ratelimit:{ip}:{method}:{path}"
// value = [timestamp1, timestamp2, ...] (最近 N 次请求时间戳)
func (rl *RateLimiter) Allow(ctx context.Context, 
    ip string, method string, path string, 
    maxRequests int, windowSeconds int) bool {
    
    key := fmt.Sprintf("ratelimit:%s:%s:%s", ip, method, path)
    
    // Lua 脚本：原子执行
    // 1. 删除过期请求记录
    // 2. 检查当前请求数是否超限
    // 3. 添加当前请求时间戳
    
    return rl.rdb.Eval(ctx, luaScript, []string{key}, 
        maxRequests, windowSeconds).Val().(int64) == 1
}
```

**在路由中使用**：

```go
// internal/middleware/ratelimit.go
func RateLimit(rl *ratelimit.RateLimiter, maxReqs, windowSecs int) gin.HandlerFunc {
    return func(c *gin.Context) {
        if !rl.Allow(c.Request.Context(), 
            c.ClientIP(), c.Request.Method, c.Request.URL.Path,
            maxReqs, windowSecs) {
            c.JSON(429, gin.H{
                "code": errcode.TooManyRequests,
                "msg": "Too many requests",
            })
            c.Abort()
            return
        }
        c.Next()
    }
}
```

**限流配置示例**：

```yaml
# config.yaml 中可配置按路由单独设置
# (目前代码支持全局限流，可扩展为按路由配置)

# 应用到路由
g.GET("/products", 
    middleware.RateLimit(svcCtx.RateLimiter, 1, 1),  // 1 req / 1 sec
    handler.ListProducts,
)
```

### 4. 消息队列（Watermill + Redis/MySQL）

**职责**：异步任务、事件驱动、解耦服务

**核心文件**：
- `internal/mq/publisher.go` - 消息发布
- `internal/mq/router.go` - 消息消费路由
- `internal/mq/handlers.go` - 消息处理器
- `internal/mq/topics.go` - Topic 常量
- `internal/mq/trimmer.go` - Redis Stream 清理

#### 4.1 消息发布

**发布消息**：

```go
// internal/mq/publisher.go
type Publisher struct {
    publisher message.Publisher
}

// 发送消息
func (p *Publisher) PublishEmail(ctx context.Context, 
    to string, subject string, body string) error {
    
    payload := &EmailPayload{
        To:      to,
        Subject: subject,
        Body:    body,
    }
    
    data, _ := json.Marshal(payload)
    msg := &message.Message{
        UUID:    shortuuid.New(),
        Payload: data,
    }
    
    // 发送到 mq.email Topic
    return p.publisher.Publish(ctx, TopicEmail, msg)
}
```

**在 Service 中使用**：

```go
// internal/service/user_svc.go
type UserService struct {
    repo      repository.UserRepository
    mqPub     *mq.Publisher
}

func (s *UserService) Register(ctx context.Context, req *request.RegisterReq) error {
    // 1. 创建用户
    user := &model.User{...}
    s.repo.Create(ctx, user)
    
    // 2. 发送欢迎邮件（异步）
    s.mqPub.PublishEmail(ctx, user.Email, 
        "Welcome", "Welcome to our service!")
    
    return nil
}
```

#### 4.2 消息消费

**注册 Handler**：

```go
// internal/mq/router.go
func NewRouter(cfg *config.MQConfig, 
    rdb *redis.Client, sqlDB *sql.DB) (*Router, error) {
    
    r, _ := message.NewRouter(message.RouterConfig{}, log)
    
    // 重试中间件：失败后最多重试 3 次
    r.AddMiddleware(middleware.Retry{
        MaxRetries: 3,
        Logger:     log,
    }.Middleware)
    
    // 死信队列中间件：失败转入 <topic>.poison
    poisonQueue, _ := middleware.PoisonQueue(poisonPub, "mq.poison")
    r.AddMiddleware(poisonQueue)
    
    // 注册 Topic Handler
    r.AddHandler(
        "email_handler",           // handler 名
        TopicEmail,                // 订阅 topic
        redisstream.NewSubscriber, // 订阅方式（Redis Streams）
        TopicEmail,                // 发布 topic（失败重试）
        handlers.HandleEmail,      // 处理函数
    )
    
    return &Router{router: r}, nil
}
```

**消息处理**：

```go
// internal/mq/handlers.go
func HandleEmail(msg *message.Message) error {
    var payload EmailPayload
    json.Unmarshal(msg.Payload, &payload)
    
    // TODO: 调用 SMTP 或 SendGrid 发送邮件
    logger.Log.Infow("[mq] email sent", 
        "to", payload.To, 
        "subject", payload.Subject)
    
    return nil
}
```

#### 4.3 消息流配置

**config.yaml 配置**：

```yaml
mq:
  driver: redis                    # redis 或 mysql
  consumer_group: gonio-group      # Redis Streams 消费者组
  
  topic_concurrency:               # 按 Topic 配置并发消费者数
    email: 3                       # 邮件处理：3 个并发消费者
    sms: 1                         # 短信处理：1 个消费者
    stats: 2                       # 统计处理：2 个消费者
  
  default_max_len: 20              # Stream 默认最大长度（保留最近 20 条）
  
  topic_max_len:                   # 按 Topic 单独设置最大长度
    email: 10                      # 邮件 Stream：最多保留 10 条
    sms: 2000                      # 短信 Stream：最多保留 2000 条
  
  trim_interval: 3600              # 定期 XTRIM 间隔（秒），0 为不启用
```

**工作流**：

```
Service 发布事件
    ↓
mq.Publisher.PublishEmail(ctx, payload)
    ↓
Redis Stream: mq.email
    └─> [msg1, msg2, msg3, ...]
    ↓
Consumer Group: gonio-group
    └─> 3 个消费者并发处理
    ↓
Handler: HandleEmail(msg)
    ├─ 成功 → ACK (消息从 Stream 删除)
    ├─ 失败 → 重试 3 次
    └─ 失败 (重试完) → 转入死信队列 mq.email.poison
    ↓
定时 TRIMMER (每 3600s)
    └─> XTRIM Stream 至配置的最大长度
```

### 5. 日志模块（Zap + Lumberjack）

**职责**：结构化日志、文件轮转、链路追踪

**核心文件**：
- `internal/pkg/logger/logger.go` - 日志初始化
- `internal/database/gorm_logger.go` - GORM SQL 日志
- `internal/database/redis_hook.go` - Redis 命令日志

#### 5.1 日志初始化

```go
// internal/pkg/logger/logger.go
var (
    Log    *zap.SugaredLogger  // 推荐使用：便于开发
    RawLog *zap.Logger         // 高性能场景：减少字符串拼接
)

func Init(cfg *config.LogConfig) {
    // 1. 编码器配置（JSON 格式）
    encoderCfg := zap.NewProductionEncoderConfig()
    encoderCfg.TimeKey = "ts"
    encoderCfg.EncodeTime = zapcore.ISO8601TimeEncoder
    
    // 2. 日志级别
    level := parseLogLevel(cfg.Level) // debug/info/warn/error
    
    // 3. 输出核心 (Sink)
    cores := []zapcore.Core{
        zapcore.NewCore(encoder, os.Stdout, level), // 控制台输出
    }
    
    if cfg.LogDir != "" {
        // 文件输出（warn 和 error 单独文件）
        cores = append(cores, 
            zapcore.NewCore(encoder, warnWriter, zap.WarnLevel),
            zapcore.NewCore(encoder, errorWriter, zap.ErrorLevel),
        )
    }
    
    // 4. 创建 Logger
    RawLog = zap.New(zapcore.NewTee(cores...))
    Log = RawLog.Sugar()
}
```

#### 5.2 日志输出示例

```go
// 开发场景：使用 SugaredLogger
logger.Log.Infow("user registered",
    "user_id", user.ID,
    "email", user.Email,
    "request_id", c.GetString("request_id"),
)

// 高性能场景：使用 RawLog
logger.RawLog.Info("product cache hit",
    zap.Uint("product_id", id),
    zap.String("cache_key", key),
)

// 错误日志（自动包含 Stack Trace）
logger.Log.Errorw("database query failed",
    "sql", "SELECT * FROM users",
    "error", err,
)
```

#### 5.3 链路追踪

每个 HTTP 请求都被分配唯一的 RequestID，在日志中关联：

```go
// middleware/requestid.go
func RequestID() gin.HandlerFunc {
    return func(c *gin.Context) {
        rid := c.GetString("X-Request-ID")
        if rid == "" {
            rid = uuid.New().String()
        }
        c.Set("request_id", rid)
        c.Header("X-Request-ID", rid)
        c.Next()
    }
}

// 所有日志都包含 request_id
logger.Log.Infow("action performed",
    "request_id", c.GetString("request_id"),
    ...
)
```

#### 5.4 文件轮转配置

```yaml
log:
  mode: prod              # dev / prod
  level: info             # debug / info / warn / error
  sql_level: warn         # SQL 日志级别
  log_dir: logs           # 日志文件目录
  max_size: 100           # 单文件最大 100 MB
  max_backups: 7          # 保留 7 个旧文件
  max_age: 30             # 保留 30 天
  compress: true          # 压缩归档（.gz）
```

输出文件：
```
logs/
├── warn.log            # WARN 及以上日志
├── warn.log.1          # 日期轮转文件
└── error.log           # ERROR 日志
```

---

## 模块分工与协作

### 整体架构图

```
┌─────────────────────────────────────────────────────────────┐
│                    HTTP 请求入口                             │
│                  (Gin HTTP Server)                          │
└──────────────────────────┬──────────────────────────────────┘
                           │
                ┌──────────┴──────────┐
                │ Global Middleware  │
                │                    │
                ├─ RequestID         │ ─→ 链路追踪
                ├─ I18n              │ ─→ 国际化
                ├─ Logger            │ ─→ 请求日志
                ├─ Recovery          │ ─→ 异常恢复
                ├─ CORS              │ ─→ 跨域
                └─ MaxBodySize       │ ─→ 请求限制
                         │
                         ▼
         ┌───────────────────────────┐
         │    Route Matching         │
         │                           │
         ├─ /health                  │
         ├─ /app/v1/*                │
         │  ├─ JWT Auth              │
         │  ├─ RateLimit             │
         │  └─ Handler               │
         └─ /admin/v1/*              │
            ├─ JWT Auth              │
            ├─ RateLimit             │
            └─ Handler
                         │
                         ▼
      ┌──────────────────────────────┐
      │  Handler (HTTP Layer)        │
      │                              │
      │ - 解析请求参数              │
      │ - 参数验证                   │
      │ - 调用 Service               │
      │ - 构造响应                   │
      └────────────┬─────────────────┘
                   │
                   ▼
      ┌──────────────────────────────┐
      │  Service (Business Logic)    │
      │                              │
      │ - 业务逻辑处理              │
      │ - 事务管理                   │
      │ - 缓存操作                   │
      │ - MQ 发布                    │
      │ - Repository 调用            │
      └────────────┬─────────────────┘
                   │
                   ▼
      ┌──────────────────────────────┐
      │  Repository (Data Access)    │
      │                              │
      │ - CRUD 操作                  │
      │ - 复杂查询                   │
      │ - SQL 抽象                   │
      └────────────┬─────────────────┘
                   │
        ┌──────────┴──────────┐
        │                     │
        ▼                     ▼
    ┌─────────┐          ┌────────────┐
    │ MySQL   │          │   Redis    │
    │ (数据)  │          │ (缓存/限流)│
    └─────────┘          └────────────┘
                              │
            ┌─────────────────┴─────────────────┐
            │                                   │
            ▼                                   ▼
    ┌──────────────────┐          ┌──────────────────────┐
    │  Cache Module    │          │  Rate Limiter Module │
    │                  │          │                      │
    │ - Get/Set/Del    │          │ - Allow/Check        │
    │ - TTL 管理       │          │ - Lua 脚本执行       │
    └──────────────────┘          └──────────────────────┘
```

### 跨层协作示例：用户注册

```
1. HTTP 请求
   POST /app/v1/auth/register
   {
     "email": "user@example.com",
     "password": "123456"
   }
   
   ↓
   
2. Handler 层 (user_handler.go)
   - 解析请求参数 → RegisterReq
   - 参数验证 → validator.Validate(req)
   - 调用 Service
   ↓
   
3. Service 层 (user_svc.go)
   - 密码哈希 → bcrypt.HashPassword(password)
   - 检查邮箱重复 → userRepo.FindByEmail()
   - 创建用户 → userRepo.Create()
   - 发送欢迎邮件（异步）→ mqPublisher.PublishEmail()
   ↓
   
4. Repository 层 (user_repo.go)
   - 执行 SQL INSERT 语句
   - 返回新用户
   ↓
   
5. MQ 系统 (publisher.go)
   - 消息发布到 Redis Stream: mq.email
   ↓
   
6. MQ Consumer (router.go + handlers.go)
   - 消费消息：HandleEmail()
   - 调用 SMTP 服务发送邮件
   
   ↓
   
7. 返回响应
   {
     "code": 0,
     "msg": "success",
     "data": {
       "user_id": 1,
       "email": "user@example.com"
     }
   }
```

### 依赖注入流程 (ServiceContext)

```
初始化阶段 (main.go)

1. 创建 Database 连接
   db := database.InitMySQL(cfg)
   rdb := database.InitRedis(cfg)
   
   ↓
   
2. 创建 Publisher
   pub := mq.NewPublisher(cfg, rdb, sqlDB)
   
   ↓
   
3. 创建 ServiceContext（一站式 DI）
   svcCtx := svc.NewServiceContext(cfg, db, rdb, pub)
   
   ↓
   
4. ServiceContext 内部依赖注入链
   
   Repository 层创建
   ├─ productRepo := repository.NewProductRepo(db)
   ├─ userRepo := repository.NewUserRepo(db)
   └─ adminRepo := repository.NewAdminRepo(db)
   
   Cache 层创建
   └─ cache := cache.NewRedisCache(rdb)
   
   Service 层创建（Repository → Service）
   ├─ productSvc := service.NewProductService(productRepo, cache, cfg)
   ├─ userSvc := service.NewUserService(userRepo, cfg)
   └─ adminSvc := service.NewAdminService(adminRepo, cfg)
   
   RateLimiter 创建
   └─ rateLimiter := ratelimit.NewRateLimiter(rdb)
   
   ↓
   
5. 返回完整初始化的 ServiceContext
   {
     Config: cfg,
     ProductSvc: productSvc,
     UserSvc: userSvc,
     AdminSvc: adminSvc,
     RateLimiter: rateLimiter,
     MQPublisher: pub,
     db: db,
     redis: rdb,
   }
   
   ↓
   
6. 传给 router.Setup()
   r := router.Setup(svcCtx, authMiddleware)
   
   ↓
   
7. Handler 使用 ServiceContext
   func ListProducts(c *gin.Context) {
     svcCtx := c.MustGet("service_context").(*svc.ServiceContext)
     products := svcCtx.ProductSvc.List(c.Request.Context(), offset, limit)
   }
```

### 请求-响应流程（详细）

```
HTTP Request Arrives
   │
   ├─ 1. RequestID Middleware
   │      生成或提取 X-Request-ID
   │      c.Set("request_id", rid)
   │      │
   ├─ 2. I18n Middleware
   │      根据 Accept-Language 设置 i18n.Lang
   │      │
   ├─ 3. ReqRespLogger Middleware
   │      记录请求方法、URL、参数
   │      │
   ├─ 4. Recovery Middleware
   │      捕获 panic，避免宕机
   │      │
   ├─ 5. CORS Middleware
   │      检查来源，设置 CORS 头
   │      │
   ├─ 6. MaxBodySize Middleware
   │      检查请求体大小（<4MB）
   │      │
   ├─ 7. Route Match
   │      /app/v1/products → ProductHandler.ListProducts
   │      │
   ├─ 8. JWT Auth (if 需要)
   │      验证 Authorization 头中的 Token
   │      c.Set("user", claims)
   │      │
   ├─ 9. RateLimit (if 需要)
   │      检查 IP + Method + Path 的请求频率
   │      Allow(ip, method, path, maxReqs, windowSecs)?
   │      │
   ├─ 10. Handler (ProductHandler)
   │       func ListProducts(c *gin.Context) {
   │           // 1. 解析查询参数
   │           var req request.ListProductsReq
   │           c.BindQuery(&req)
   │           
   │           // 2. 参数验证
   │           validator.Validate(req)
   │           
   │           // 3. 调用 Service
   │           svcCtx := c.MustGet("service_context")
   │           products := svcCtx.ProductSvc.List(ctx, req.Offset, req.Limit)
   │           
   │           // 4. 构造响应
   │           response.Success(c, products)
   │       }
   │       │
   ├─ 11. Service (ProductService)
   │       func (s *ProductService) List(ctx, offset, limit) {
   │           // 1. 检查缓存
   │           cached := s.cache.Get(ctx, "products_list")
   │           if cached != "" {
   │               return parseFromCache(cached)
   │           }
   │           
   │           // 2. 数据库查询
   │           products := s.repo.List(ctx, offset, limit)
   │           
   │           // 3. 写入缓存
   │           s.cache.Set(ctx, "products_list", toJSON(products), s.ttl)
   │           
   │           return products
   │       }
   │       │
   ├─ 12. Repository (ProductRepository)
   │       func (r *ProductRepository) List(ctx, offset, limit) {
   │           // GORM 数据库查询
   │           var products []*model.Product
   │           r.db.WithContext(ctx).Offset(offset).Limit(limit).Find(&products)
   │           return products
   │       }
   │       │
   ├─ 13. MySQL 执行查询
   │       SELECT * FROM products LIMIT ?, ?
   │       │
   ├─ 14. 返回 Response
   │       {
   │         "code": 0,
   │         "msg": "success",
   │         "data": [
   │           {"id": 1, "name": "product1", ...},
   │           {"id": 2, "name": "product2", ...}
   │         ]
   │       }
   │       │
   └─ 15. ReqRespLogger Middleware (响应日志)
          记录响应状态码、耗时、大小
```

---

## 部署运维

### Docker 构建与部署

#### Dockerfile 说明

```dockerfile
# Stage 1: Build (编译阶段)
FROM golang:1.25-alpine AS builder

RUN apk add --no-cache git

WORKDIR /app

# 先拷贝依赖文件，利用 Docker 层缓存
COPY go.mod go.sum ./
RUN go mod download

COPY . .

# 静态编译：不依赖系统库，可在任意环境运行
RUN CGO_ENABLED=0 GOOS=linux go build -ldflags="-s -w" -o /app/bin/gonio cmd/server/main.go

# Stage 2: Runtime (运行阶段)
FROM alpine:3.21

# 只复制必要的运行时依赖
RUN apk add --no-cache ca-certificates tzdata

WORKDIR /app

# 从编译阶段复制二进制
COPY --from=builder /app/bin/gonio /app/gonio

# 暴露端口
EXPOSE 8080

# 启动应用
ENTRYPOINT ["/app/gonio"]
```

#### 构建 Docker 镜像

```bash
# 构建镜像
make docker

# 或手动构建
docker build -t gonio:latest .

# 查看镜像
docker images | grep gonio
```

#### 运行 Docker 容器

```bash
# 1. 准备配置文件
mkdir -p /app/config
cp config.yaml /app/config/

# 2. 运行容器（挂载配置文件）
docker run -d \
  --name gonio \
  -p 8080:8080 \
  -v /app/config:/app/config \
  -e APP_MYSQL_HOST=db.example.com \
  -e APP_MYSQL_PASSWORD=secretpassword \
  -e APP_REDIS_ADDR=redis.example.com:6379 \
  -e APP_JWT_SECRET=production-secret-key \
  gonio:latest

# 3. 查看日志
docker logs -f gonio

# 4. 健康检查
curl http://localhost:8080/health

# 5. 停止容器
docker stop gonio
docker rm gonio
```

#### Docker Compose 编排

```yaml
# docker-compose.yml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: gonio_mysql
    environment:
      MYSQL_ROOT_PASSWORD: "123456"
      MYSQL_DATABASE: silk_route
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
    networks:
      - gonio_net

  redis:
    image: redis:7-alpine
    container_name: gonio_redis
    ports:
      - "6379:6379"
    networks:
      - gonio_net

  gonio:
    build:
      context: .
      dockerfile: Dockerfile
    container_name: gonio_app
    ports:
      - "8080:8080"
    environment:
      APP_ENV: prod
      APP_MYSQL_HOST: mysql
      APP_MYSQL_PASSWORD: "123456"
      APP_REDIS_ADDR: redis:6379
      APP_JWT_SECRET: production-secret-key
    volumes:
      - ./config:/app/config
      - ./logs:/app/logs
    depends_on:
      - mysql
      - redis
    networks:
      - gonio_net
    command: /app/gonio

volumes:
  mysql_data:

networks:
  gonio_net:
    driver: bridge
```

**启动完整服务**：

```bash
docker-compose up -d
docker-compose logs -f gonio
```

### Kubernetes 部署

#### 创建 Deployment

```yaml
# k8s/deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: gonio
  namespace: default
spec:
  replicas: 3
  selector:
    matchLabels:
      app: gonio
  template:
    metadata:
      labels:
        app: gonio
    spec:
      containers:
      - name: gonio
        image: gonio:latest
        imagePullPolicy: IfNotPresent
        ports:
        - containerPort: 8080
        env:
        - name: APP_ENV
          value: prod
        - name: APP_MYSQL_HOST
          valueFrom:
            configMapKeyRef:
              name: gonio-config
              key: mysql_host
        - name: APP_MYSQL_PASSWORD
          valueFrom:
            secretKeyRef:
              name: gonio-secrets
              key: mysql_password
        - name: APP_REDIS_ADDR
          valueFrom:
            configMapKeyRef:
              name: gonio-config
              key: redis_addr
        - name: APP_JWT_SECRET
          valueFrom:
            secretKeyRef:
              name: gonio-secrets
              key: jwt_secret
        livenessProbe:
          httpGet:
            path: /health
            port: 8080
          initialDelaySeconds: 10
          periodSeconds: 5
        readinessProbe:
          httpGet:
            path: /health
            port: 8080
          initialDelaySeconds: 5
          periodSeconds: 3
        resources:
          requests:
            memory: "256Mi"
            cpu: "250m"
          limits:
            memory: "512Mi"
            cpu: "500m"
```

#### 创建 Service

```yaml
# k8s/service.yaml
apiVersion: v1
kind: Service
metadata:
  name: gonio-service
  namespace: default
spec:
  type: LoadBalancer
  selector:
    app: gonio
  ports:
  - protocol: TCP
    port: 80
    targetPort: 8080
```

#### 部署到 K8s

```bash
# 创建 ConfigMap
kubectl create configmap gonio-config \
  --from-literal=mysql_host=mysql.default.svc.cluster.local \
  --from-literal=redis_addr=redis.default.svc.cluster.local:6379

# 创建 Secret
kubectl create secret generic gonio-secrets \
  --from-literal=mysql_password=secretpass \
  --from-literal=jwt_secret=production-secret-key

# 部署应用
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml

# 查看部署
kubectl get pods
kubectl logs -f deployment/gonio
```

### 常见问题排查

#### 1. 连接超时错误

```
error: dial tcp: i/o timeout
```

**排查步骤**：

```bash
# 1. 检查网络连接
ping mysql.host
telnet mysql.host 3306

# 2. 查看配置中的超时参数
# config.yaml 中的 dial_timeout, read_timeout, write_timeout

# 3. 检查防火墙
firewall-cmd --list-ports
ufw status

# 4. 增加超时时间
mysql:
  dial_timeout: 10  # 从 5 增加到 10
  read_timeout: 20  # 从 10 增加到 20
```

#### 2. JWT Token 验证失败

```
error: invalid token
```

**排查步骤**：

```bash
# 1. 检查 JWT secret 配置
# 确保 jwt.secret 在所有环境一致

# 2. 检查 Token 过期时间
# curl 请求时包含有效 Token

curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/app/v1/products

# 3. 查看日志输出
docker logs gonio | grep -i "jwt\|token"

# 4. 验证 Token 内容
# 使用 jwt.io 在线解析 token
```

#### 3. 数据库迁移失败

```
error: create table failed: table already exists
```

**排查步骤**：

```bash
# 1. 检查自动迁移配置
# config.yaml 中的 auto_migrate
server:
  auto_migrate: false  # 改为 false，避免重复迁移

# 2. 手动执行迁移
# 删除存量表后重试，或使用数据库版本管理工具（如 Flyway）

mysql> DROP TABLE IF EXISTS users, products, admins;

# 3. 检查迁移脚本
# internal/migration/migration.go 中的建表 SQL
```

#### 4. Redis 连接失败

```
error: redis: connection refused
```

**排查步骤**：

```bash
# 1. 检查 Redis 是否运行
redis-cli ping
# 返回 PONG 表示正常

# 2. 检查配置中的 Redis 地址
redis:
  addr: 127.0.0.1:6379  # 确认 IP 和端口

# 3. 检查 Redis 密码
redis-cli -h 127.0.0.1 -p 6379 -a password ping

# 4. 查看 Redis 连接日志
docker logs redis_container_name
```

#### 5. 限流异常（所有请求被限）

```
error: Too many requests (429)
```

**排查步骤**：

```bash
# 1. 检查限流配置
# 查看 ratelimit 中的 maxReqs 和 windowSecs

# 2. 查看 Redis 中的限流计数
redis-cli
> KEYS "ratelimit:*"
> GET "ratelimit:127.0.0.1:GET:/app/v1/products"

# 3. 清除过期的限流记录
redis-cli
> FLUSHDB  # 清空整个 Redis 数据库（谨慎！）

# 4. 调整限流参数
# internal/router/app/product.go
g.GET("", 
    middleware.RateLimit(svcCtx.RateLimiter, 10, 1),  // 改为 10 req/sec
    handler.ListProducts,
)
```

#### 6. 消息队列消费失败

```
error: mq router failed: consumer group error
```

**排查步骤**：

```bash
# 1. 检查 Consumer Group 是否存在
redis-cli
> XINFO GROUPS mq.email

# 2. 查看 Stream 消息
redis-cli
> XLEN mq.email  # 查看 Stream 长度
> XRANGE mq.email - +  # 查看 Stream 内容

# 3. 检查死信队列
redis-cli
> XLEN mq.email.poison

# 4. 手动消费（调试）
redis-cli
> XREAD COUNT 1 STREAMS mq.email 0

# 5. 重置 Consumer Group 偏移量
redis-cli
> XGROUP SETID mq.email gonio-group $
```

#### 7. 内存泄漏（内存持续增长）

**排查步骤**：

```bash
# 1. 使用 pprof 分析内存
go tool pprof http://localhost:8080/debug/pprof/heap

# 2. 检查 goroutine 数
curl http://localhost:8080/debug/pprof/goroutine?debug=1

# 3. 检查是否有未关闭的连接
# 查看 ServiceContext.Close() 是否正确调用

# 4. 分析日志中的错误
docker logs gonio | tail -100
```

### 监控与告警

#### Prometheus 指标暴露

```go
// internal/pkg/metrics/metrics.go (新增文件)
import "github.com/prometheus/client_golang/prometheus"

var (
    httpRequestsTotal = prometheus.NewCounterVec(
        prometheus.CounterOpts{
            Name: "http_requests_total",
            Help: "Total HTTP requests",
        },
        []string{"method", "path", "status"},
    )
    
    httpRequestDuration = prometheus.NewHistogramVec(
        prometheus.HistogramOpts{
            Name: "http_request_duration_seconds",
            Help: "HTTP request duration",
        },
        []string{"method", "path"},
    )
)

func init() {
    prometheus.MustRegister(httpRequestsTotal, httpRequestDuration)
}
```

#### 中间件集成

```go
// internal/middleware/metrics.go
func MetricsMiddleware(metrics *MetricsCollector) gin.HandlerFunc {
    return func(c *gin.Context) {
        start := time.Now()
        
        c.Next()
        
        duration := time.Since(start).Seconds()
        metrics.RecordRequest(c.Request.Method, c.Request.URL.Path, c.Writer.Status(), duration)
    }
}
```

---

## 总结

Gonio 采用 **分层架构 + 依赖注入** 模式：

| 层 | 职责 | 关键文件 |
|----|------|--------|
| **Handler** | HTTP 请求处理、参数验证、响应构造 | `internal/handler/*.go` |
| **Service** | 业务逻辑、事务、缓存、MQ 发布 | `internal/service/*.go` |
| **Repository** | 数据访问、SQL 操作 | `internal/repository/*.go` |
| **Model** | 数据模型、表关系 | `internal/model/*.go` |
| **Database** | 连接池、驱动初始化 | `internal/database/*.go` |
| **Middleware** | 认证、限流、日志、跨域 | `internal/middleware/*.go` |
| **MQ** | 异步事件、消息处理 | `internal/mq/*.go` |
| **Cache** | 缓存策略、Redis 操作 | `internal/pkg/cache/*.go` |

**核心特点**：

✅ **清晰分工** - 每层职责明确，易于扩展  
✅ **依赖注入** - 通过 ServiceContext 统一管理依赖  
✅ **生产就绪** - 限流、认证、日志、监控完整  
✅ **高可用** - 优雅停机、异常恢复、连接池优化  
✅ **可观测性** - 结构化日志、链路追踪、健康检查  

祝使用愉快！🚀
