# RingStory · 时光为轮，记忆成书

> **RingStory** 是一款基于微信生态的私密家庭共享相册工具。它不仅仅是一个云盘，更是家庭的"数字年轮博物馆"——通过**时间轴**、**年轮图谱**和**年轮放映室**，将零散的照片编织成家庭独有的成长史诗。

## 项目结构

```
ringstory/
├── ringstory-gateway/           # BFF 网关 (Spring Cloud Gateway + Sentinel)
├── ringstory-user/              # 用户服务 (微信登录、用户信息管理)
├── ringstory-family/            # 家庭服务 (CRUD、成员管理、邀请)
├── ringstory-album/             # 相册服务 (照片上传、时间轴、点赞/评论)
├── ringstory-ringtree/          # 年轮图谱引擎 (Year→Season→Month→Day 聚类)
├── ringstory-story/             # 故事服务 (照片备注/刻录此刻)
├── ringstory-review/            # 年轮放映室 (月/季/年回顾生成)
├── ringstory-search/            # 搜索服务 (全文检索+多维筛选)
├── ringstory-notify/            # 年轮回声 (通知推送)
├── ringstory-face/              # 人脸识别 (Rust + ONNX Runtime, GPU)
├── ringstory-common/            # 公共模块
├── ringstory-api/               # Feign 接口定义
└── docs/                        # 设计文档
```

## 技术栈

| 层级 | 技术 | 版本 |
| :--- | :--- | :--- |
| 后端语言 | Java | 21 LTS (Virtual Threads) |
| 微服务框架 | Spring Boot + Spring Cloud Alibaba | 3.4 / 2023.x |
| 注册/配置中心 | Nacos | 2.4+ |
| 网关 | Spring Cloud Gateway + Sentinel | 4.1+ |
| 数据库 | MySQL + ShardingSphere | 8.4 / 5.5 |
| 缓存 | Redis / Valkey | 7.4+ |
| 搜索引擎 | Elasticsearch | 8.15+ |
| 消息队列 | RocketMQ | 5.x |
| 对象存储 | 阿里云 OSS + CDN | - |
| 人脸推理 | Rust + ONNX Runtime | - |
| 可观测性 | Prometheus + Grafana + OTel | - |

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

## 快速开始

### 前置条件
- JDK 21+
- Maven 3.9+
- MySQL 8.4+
- Nacos 2.4+
- Redis 7.4+
- RocketMQ 5.x

### 启动顺序
1. 启动 Nacos、MySQL、Redis、RocketMQ
2. 执行 `01-schema.sql` 初始化数据库
3. 依次启动各微服务

### 构建
```bash
# 每个服务独立构建
cd ringstory-user
mvn clean package -DskipTests
java -jar target/ringstory-user-1.0.0-SNAPSHOT.jar

# Rust 人脸服务
cd ringstory-face
cargo build --release
./target/release/ringstory-face
```

## 核心数据流

```
用户选择照片 → Gateway → OSS STS 凭证 → 前端直传 OSS
  → OSS 回调 → RocketMQ (photo_uploaded)
  → album 消费:
    1. 写入 photo 表 (MySQL + ShardingSphere)
    2. 发送缩略图生成 (RocketMQ)
    3. 发送内容安全检测 (RocketMQ)
    4. 发送人脸检测 (RocketMQ → face)
```

## 数据库分片策略

- **分库**: `family_id` HASH_MOD 4 (4 个物理库)
- **分表**: `shoot_time` 按年分表 (`t_photo_2024`, `t_photo_2025`, ...)
- **配置**: Nacos 配置中心动态管理，无需重启

## 许可证

MIT License
