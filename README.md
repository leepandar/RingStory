# RingStory · 时光为轮，记忆成书

> **RingStory** 是一款基于微信生态的私密家庭共享相册工具。它不仅仅是一个云盘，更是家庭的"数字年轮博物馆"——通过**时间轴**、**年轮图谱**和**年轮放映室**，将零散的照片编织成家庭独有的成长史诗。

## 项目结构

```
RingStory/
├── ringstory-gateway/                # BFF 网关 (Spring Cloud Gateway + Sa-Token 鉴权)
├── ringstory-common/                 # 公共模块父模块
│   ├── ringstory-common-core/        #   核心工具 (响应体、异常处理、验证码、对账)
│   ├── ringstory-common-mybatis/     #   MyBatis-Plus 封装 (分页、自动填充、数据权限)
│   ├── ringstory-common-redis/       #   Redis 封装 (缓存、分布式锁)
│   ├── ringstory-common-log/         #   操作日志切面
│   ├── ringstory-common-feign/       #   Feign 调用封装 (拦截器、降级)
│   └── ringstory-common-sms/         #   短信服务封装
├── ringstory-modules/                # 业务模块聚合
│   ├── ringstory-api/                #   Feign 接口定义 (跨服务调用契约)
│   ├── ringstory-user/               #   用户服务 (微信登录、管理后台登录、滑块验证码)
│   ├── ringstory-family/             #   家庭服务 (CRUD、成员管理、邀请)
│   ├── ringstory-album/              #   相册服务 (照片上传、时间轴、点赞/评论)
│   ├── ringstory-ringtree/           #   年轮图谱引擎 (Year→Season→Month→Day 聚类)
│   ├── ringstory-story/              #   故事服务 (照片备注 / 刻录此刻)
│   ├── ringstory-review/             #   年轮放映室 (月/季/年回顾生成)
│   ├── ringstory-search/             #   搜索服务 (Elasticsearch 全文检索 + 多维筛选)
│   └── ringstory-notify/             #   年轮回声 (通知推送)
├── ringstory-plugins/                # 插件模块
│   ├── ringstory-rocketmq/           #   RocketMQ Starter (Spring Cloud Stream 封装)
│   └── ringstory-face/               #   人脸识别 (Rust + ONNX Runtime, GPU)
├── ringstory-nacos/                  # Nacos 服务 (注册/配置中心, 内嵌部署)
├── ringstory-ui/                     # 前端
│   ├── ringstory-admin/              #   管理后台 (Vue 3 + Element Plus + Vite)
│   └── ringstory-mini/               #   小程序前端 (uni-app 3.x + Vue 3 + TypeScript)
├── docs/                             # 文档与配置
│   ├── sql/                          #   数据库初始化 SQL 脚本
│   └── nacos/                        #   Nacos 配置模板 (各微服务 YAML)
└── docker-compose.yml                # Docker 全栈编排
```

## 技术栈

### 后端

| 层级 | 技术 | 版本 |
| :--- | :--- | :--- |
| 后端语言 | Java | 21 LTS (Virtual Threads) |
| 微服务框架 | Spring Boot + Spring Cloud Alibaba | 3.4.3 / 2023.0.3.2 |
| 注册/配置中心 | Nacos | 2.4+ |
| 网关 | Spring Cloud Gateway | 4.1+ |
| 认证鉴权 | Sa-Token | 1.45 |
| ORM | MyBatis-Plus | 3.5.9 |
| 数据库 | MySQL + ShardingSphere | 8.0 / 5.5 |
| 缓存 | Redis | 7.4+ |
| 搜索引擎 | Elasticsearch | 8.15 |
| 消息队列 | RocketMQ (Spring Cloud Stream) | 5.x |
| 对象映射 | MapStruct | 1.6.3 |
| 工具库 | Hutool | 5.8.34 |
| API 文档 | Knife4j (OpenAPI 3) | 4.5.0 |
| 对象存储 | 阿里云 OSS + CDN | - |
| 人脸推理 | Rust + ONNX Runtime | - |
| 可观测性 | Prometheus + Grafana + OTel | - |

### 前端

| 端 | 技术栈 | 说明 |
| :--- | :--- | :--- |
| 小程序 | uni-app 3.x + Vue 3 + TypeScript + Pinia | 微信小程序 / H5 |
| 管理后台 | Vue 3 + Vite 5 + Element Plus + Pinia + vue-router | 后台管理系统 |

## 服务端口

| 服务 | 端口 | 说明 |
| :--- | :--- | :--- |
| ringstory-gateway | 8080 | BFF 网关 |
| ringstory-user | 8081 | 用户服务 |
| ringstory-family | 8082 | 家庭服务 |
| ringstory-album | 8083 | 相册服务 |
| ringstory-ringtree | 8084 | 年轮图谱 |
| ringstory-story | 8085 | 故事服务 |
| ringstory-review | 8086 | 年轮放映室 |
| ringstory-search | 8087 | 搜索服务 |
| ringstory-notify | 8088 | 通知服务 |
| ringstory-face | 8090 | 人脸识别 (Rust) |
| ringstory-nacos | 8848 | Nacos 配置/注册中心 |
| ringstory-admin | 3000 | 管理后台前端 (Vite dev) |
| ringstory-mini | 5173 | 小程序前端 (H5 模式) |

## 快速开始

### 前置条件

- JDK 21+
- Maven 3.9+
- MySQL 8.0+
- Redis 7.4+
- RocketMQ 5.x
- Node.js 18+（前端开发）

### 方式一：Docker Compose 一键启动

```bash
# 先构建所有后端模块
mvn clean package -DskipTests

# 启动全部服务（含基础设施）
docker-compose up -d

# 访问 Nacos 控制台：http://localhost:8848/nacos  (nacos/nacos)
# 访问网关：http://localhost:8080
```

### 方式二：本地开发

#### 1. 启动基础设施

```bash
# 使用 Docker 启动 MySQL、Redis、Elasticsearch、RocketMQ
docker-compose up -d mysql redis elasticsearch rocketmq-namesrv rocketmq-broker
```

#### 2. 初始化数据库

```bash
# 执行 SQL 脚本初始化数据库表结构
mysql -h localhost -u root -p < docs/sql/ringstory.sql
```

#### 3. 配置 Nacos

将 `docs/nacos/` 下的 YAML 配置导入 Nacos 配置中心。

#### 4. 启动后端服务

```bash
# 启动 Nacos（或使用 Docker）
cd ringstory-nacos
mvn spring-boot:run

# 依次启动各微服务（每个服务独立目录）
cd ringstory-gateway && mvn spring-boot:run
cd ringstory-modules/ringstory-user && mvn spring-boot:run
cd ringstory-modules/ringstory-family && mvn spring-boot:run
cd ringstory-modules/ringstory-album && mvn spring-boot:run
# ... 其余服务同理
```

#### 5. 启动前端

```bash
# 管理后台
cd ringstory-ui/ringstory-admin
npm install
npm run dev          # http://localhost:3000

# 小程序（H5 模式开发调试）
cd ringstory-ui/ringstory-mini
npm install --legacy-peer-deps
npm run dev:h5       # http://localhost:5173
```

### 构建部署

```bash
# 全量构建
mvn clean package -DskipTests

# 单个服务构建（以 user 服务为例）
cd ringstory-modules/ringstory-user
mvn clean package -DskipTests
java -jar target/ringstory-user-1.0.0-SNAPSHOT.jar

# Rust 人脸服务
cd ringstory-plugins/ringstory-face
cargo build --release
./target/release/ringstory-face
```

## 许可证

MIT License
