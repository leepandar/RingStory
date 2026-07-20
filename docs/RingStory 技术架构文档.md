# RingStory 技术架构设计文档

| 文档版本 | 修改日期   | 修改内容     | 修改人 |
| :------- | :--------- | :----------- | :----- |
| V1.0     | 2026-07-20 | 初始版本创建（Java + 阿里云 + Vue 技术栈） | 架构师 |

---

> 本文档基于 RingStory 产品需求文档，采用 **Java 21 + Spring Cloud Alibaba + Nacos + MySQL + uni-app(Vue)** 技术栈，对象存储和消息队列均选用阿里云系产品。

---

## 1. 系统架构总览

### 1.1 整体架构图（文字描述）

RingStory 采用**前后端分离 + 事件驱动**的微服务架构，整体分为五层：

`
┌──────────────────────────────────────────────────┐
│                  客户端层                          │
│  WeChat 小程序（uni-app）  Web管理后台（Vue 3）     │
└──────────────────────┬───────────────────────────┘
                       │ HTTPS / WSS
┌──────────────────────▼───────────────────────────┐
│             接入层 / 网关                           │
│     Alibaba CLB → Spring Cloud Gateway            │
│   路由注册至 Nacos / 限流(Sentinel) / 鉴权 / 日志   │
└──────────────────────┬───────────────────────────┘
                       │
┌──────────────────────▼───────────────────────────┐
│             核心服务层（Java 21 + Spring Boot）     │
│  ┌──────────┐ ┌──────────┐ ┌──────────────────┐  │
│  │ 用户服务  │ │ 家庭服务  │ │    相册服务      │  │
│  │ (Java)   │ │ (Java)   │ │ (Java)          │  │
│  ├──────────┤ ├──────────┤ ├──────────────────┤  │
│  │ 年轮图谱  │ │ 年轮放映  │ │    搜索服务      │  │
│  │ 服务(Java)│ │ 室服务   │ │ (Java + ES)     │  │
│  │          │ │ (Java)   │ │                  │  │
│  └──────────┘ └──────────┘ └──────────────────┘  │
└──────────────────────┬───────────────────────────┘
                       │
┌──────────────────────▼───────────────────────────┐
│              AI / 异步处理层                        │
│  ┌──────────┐ ┌──────────┐ ┌──────────────────┐  │
│  │ 图片处理  │ │ 人脸聚类  │ │ 内容安全检测      │  │
│  │ Worker   │ │ Worker   │ │ Worker          │  │
│  │ (Java)   │ │ (Rust)   │ │ (Java)          │  │
│  └──────────┘ └──────────┘ └──────────────────┘  │
│  ┌──────────┐ ┌──────────┐                       │
│  │ 视频合成  │ │ 回顾生成  │                       │
│  │ Worker   │ │ Worker   │                       │
│  └──────────┘ └──────────┘                       │
└──────────────────────┬───────────────────────────┘
                       │
┌──────────────────────▼───────────────────────────┐
│                 数据存储层                          │
│  MySQL 8.4 + ShardingSphere / Redis / ES / OSS   │
│  RocketMQ / Prometheus + Grafana                 │
└──────────────────────────────────────────────────┘
`

### 1.2 架构设计原则

1. **事件驱动**：异步任务（图片处理、AI检测、视频合成）通过 RocketMQ 解耦，核心 API 不阻塞。
2. **读写分离**：照片瀑布流、年轮图谱等读密集型场景通过 Redis + ES 缓存/索引承载，写路径走 MySQL。
3. **数据分片**：MySQL 按 family_id 哈希分库分表（ShardingSphere），突破单库瓶颈。
4. **渐进式灰度**：新服务版本通过 Nacos 流量权重 + 灰度发布逐步放量。

---

## 2. 技术栈选型

### 2.1 完整技术栈

| 层级 | 技术选型 | 版本 | 选型理由 |
| :--- | :------- | :--- | :------- |
| **后端语言** | **Java** | Java 21 LTS | 虚拟线程（Virtual Threads）大幅降低并发成本；GraalVM Native Image AOT 编译将启动时间降至 ~200ms；国内 Java 人才储备充足 |
| **Web 框架** | **Spring Boot 3.4** + **Spring Cloud Alibaba 2023** | SB 3.4 / SCA 2023.x | Spring Cloud Alibaba 是国内微服务事实标准，与 Nacos/Sentinel/Seata 原生集成 |
| **服务间通信** | **OpenFeign** (REST) + **gRPC** (高性能路径) | Feign 13+ / gRPC-Java 1.64 | 外部 API 用 Feign 声明式调用；内部高吞吐链路（年轮图谱查询）用 gRPC |
| **注册中心 / 配置中心** | **Nacos** | Nacos 2.4+ | 阿里开源，服务发现 + 配置管理一体化；AP 模式（注册中心）+ CP 模式（配置中心）双模式切换 |
| **API 网关** | **Spring Cloud Gateway** | Gateway 4.1+ | 路由规则通过 Nacos 动态配置，无需重启；集成 Sentinel 限流 |
| **限流 / 熔断** | **Sentinel** | Sentinel 1.8+ | 与 Nacos 深度集成，规则推送到 Nacos 持久化；支持热点参数限流、系统自适应保护 |
| **前端框架** | **uni-app 3.x** + **Vue 3** | uni-app 3.9+ / Vue 3.4 | Vue 生态最成熟的跨端框架，一套代码编译到微信小程序 + Web 管理后台；社区活跃，组件生态丰富 |
| **状态管理** | **Pinia** | Pinia 2.x | Vue 3 官方推荐状态管理，TypeScript 友好，比 Vuex 更轻量 |
| **HTTP 请求** | **uni.request** + **uni-ajax** | — | uni-app 内置请求库，配合 uni-ajax 封装拦截器 |
| **UI 组件库** | **uni-ui** / **uView Plus** | — | uni-app 生态主流 UI 库，覆盖表单/反馈/展示组件 |
| **数据库** | **MySQL** + **ShardingSphere** | MySQL 8.4 / ShardingSphere 5.5 | MySQL 8.4 是 LTS 版本，稳定性经过大规模验证；ShardingSphere 提供分库分表、读写分离、数据加密 |
| **缓存** | **Redis** | Redis 7.4+ / Valkey 8 | 会话 Token、年轮图谱树结构、热点数据缓存 |
| **搜索引擎** | **Elasticsearch** | ES 8.15+ | 备注全文检索 + 照片标签聚合 + 地理位置查询 |
| **对象存储** | **阿里云 OSS** | — | 与腾讯 COS 功能对标，图片处理通过 OSS 图片处理管道（x-oss-process）动态调整尺寸/格式 |
| **CDN** | **阿里云 CDN** | — | OSS + CDN 同厂商集成，加速图片分发 |
| **消息队列** | **阿里云 RocketMQ** | RocketMQ 5.3+ | 事务消息支持上传最终一致性；延迟消息支持定时任务；商业版提供消息轨迹、死信队列管理 |
| **容器编排** | **阿里云 ACK** (Kubernetes) | K8s 1.30+ | 托管 K8s 集群，与阿里云 VPC/OSS/SLB 内网互通 |
| **可观测性** | **OpenTelemetry** + **Grafana** + **Prometheus** | OTel 1.30+ / Grafana 11+ | Spring Boot 3.x 原生 Micrometer + OTel 集成；阿里云 SLS 作为日志后备 |
| **CI/CD** | **GitLab CI** / **阿里云云效** | — | 云效与阿里云产品线高度集成，支持自动部署到 ACK |

### 2.2 版本依赖总表

`
Java 21 LTS + GraalVM for JDK 21
Spring Boot 3.4 + Spring Cloud Alibaba 2023
Spring Cloud Gateway 4.1
Nacos 2.4+
Sentinel 1.8+
Seata 2.x (AT 模式)
MySQL 8.4 + ShardingSphere 5.5
Redis 7.4 / Valkey 8
Elasticsearch 8.15
阿里云 RocketMQ 5.x
阿里云 OSS + CDN
uni-app 3.9+
Vue 3.4 + Pinia 2.x + Vite 5
OpenTelemetry 1.30
Grafana 11 + Prometheus
GitLab CI / 阿里云云效
`

---

## 3. 前端架构（微信小程序 + Web 管理后台）

### 3.1 技术选型

| 模块 | 技术 | 说明 |
| :--- | :--- | :--- |
| 小程序框架 | **uni-app 3.x**（Vue 3 语法） | 一套代码编译到微信小程序 + H5（Web 管理后台），Vue 3 Composition API + <script setup> |
| 状态管理 | **Pinia** | 轻量，TypeScript 类型推导完整，无 Provider 嵌套，支持 HMR |
| 路由 | **uni-router** / vue-router（H5）| 小程序端使用 uni-app 内置路由，H5 端使用 vue-router |
| HTTP 请求 | **uni-ajax**（封装 uni.request）| 拦截器处理 Token 注入 / 刷新 / 统一错误弹出 |
| UI 组件库 | **uView Plus** + **uni-ui** | uView Plus 基于 Vue 3 重构，组件覆盖完善（表单/反馈/展示/导航） |
| 图片加载 | OSS 图片处理参数 + **uni-image** | 利用阿里云 OSS 图片处理管道（?x-oss-process=image/resize,w_360/format,webp）动态输出 |
| 动画 | **vue-use-motion** / Lottie 轻量 | Onboarding 引导页使用 Lottie 动画 |
| 构建工具 | **Vite 5** + uni-app 插件 | Vite 5 开发服务器热更新 <1s，HMR 极速 |

### 3.2 包体积优化（小程序 2MB 限制）

1. **按需加载**：年轮图谱、视频回顾等非首屏页面使用 uni-app 的 subPackages 分包机制。
2. **图片 CDN 直出**：缩略图通过 OSS + CDN URL 动态生成，不内置任何图片资源。
3. **分包策略**：
   - 主包（~900KB）：首页、上传、登录、公共组件
   - 分包1（~600KB）：年轮图谱、照片详情、备注编辑
   - 分包2（~400KB）：年轮放映室、个人设置
   - 分包3（~200KB）：Onboarding 引导页

### 3.3 关键性能指标

| 指标 | 目标 | 实现手段 |
| :--- | :--- | :------- |
| 首屏渲染 | <= 1.5s | 预请求 + uni.setStorage 缓存首页数据；OSS CDN 预热常用缩略图 |
| 图片瀑布流滚动 | 60fps | Virtual Scroll（虚拟列表），节点控制在 20 个以内 |
| 上传成功率 | >= 99.5% | 分片上传 OSS + 断点续传 + 网络状态感知重试 |
| 年轮图谱展开 | <= 500ms | 后端预计算树结构缓存至 Redis，前端按需请求子节点 |

---

## 4. 后端服务架构

### 4.1 服务拆分

| 服务名 | 职责 | 技术栈 | 部署实例数（MVP） |
| :----- | :--- | :----- | :--------------- |
| **user-svc** | 微信登录、用户信息管理、JWT 维护 | Spring Boot 3.4 + MySQL + Redis | 2 |
| **family-svc** | 家庭 CRUD、成员管理、角色权限、邀请链接 | Spring Boot 3.4 + MySQL + Redis | 2 |
| **album-svc** | 照片上传、时间轴、瀑布流、点赞/评论 | Spring Boot 3.4 + MySQL + Redis | 4 |
| **ringtree-svc** | 年轮图谱数据构建与聚合查询 | Spring Boot 3.4 + MySQL + Redis | 2 |
| **story-svc** | 备注 CRUD、富文本解析、@提及解析 | Spring Boot 3.4 + MySQL | 2 |
| **review-svc** | 年轮放映室回顾生成、调度、素材选取 | Spring Boot 3.4 + RocketMQ + FFmpeg | 2 |
| **search-svc** | 全局搜索索引构建与查询 | Spring Boot 3.4 + Elasticsearch | 2 |
| **face-svc** | 人脸检测、特征提取、聚类管理 | **Rust** + ONNX Runtime | 2 (GPU) |
| **notification-svc** | 通知生成、推送（微信订阅消息）、偏好管理 | Spring Boot 3.4 + RocketMQ + Redis | 2 |
| **bff-gateway** | BFF，处理小程序登录态、聚合多个微服务数据 | Spring Cloud Gateway | 2 |

### 4.2 关键接口设计

#### 4.2.1 照片上传流程（核心链路）

`
用户选择照片 → BFF 请求 OSS STS 临时凭证
  → 前端分片直传 OSS（每片 1MB）
  → OSS 上传完成回调 → RocketMQ（photo_uploaded 事件）
  → album-svc 消费事件（@Transactional + RocketMQ 事务消息）：
    1. 写入 photo 表（MySQL + ShardingSphere）
    2. 发送缩略图生成任务 → RocketMQ（image_process 队列）
    3. 发送内容安全检测任务 → RocketMQ（cms_check 队列）
    4. 发送人脸检测任务 → RocketMQ（face_detect 队列）
  → 前端轮询获取处理状态
`

**Spring 声明式事务 + RocketMQ 事务消息的伪代码：**

`java
@Service
@Slf4j
public class PhotoUploadService {

    @Autowired
    private RocketMQTemplate rocketMqTemplate;
    
    @Transactional
    public void handleUploadCallback(UploadCallbackEvent event) {
        // 1. 写入照片元数据
        Photo photo = new Photo();
        photo.setFamilyId(event.getFamilyId());
        photo.setOssKey(event.getOssKey());
        photo.setMd5(event.getMd5());
        photo.setShootTime(parseExif(event.getOssKey()));
        photo.setStatus(PhotoStatus.PROCESSING);
        photoMapper.insert(photo);
    
        // 2. 更新家庭照片计数（统计冗余字段）
        familyMapper.incrementPhotoCount(event.getFamilyId());
    
        // 3. 发送异步处理任务（事务提交后发送）
        TransactionSendResult result = rocketMqTemplate.sendMessageInTransaction(
            "photo-process-tx-group",
            new PhotoUploadedMessage(photo.getId(), event.getFamilyId()),
            null
        );
    
        log.info("Photo uploaded: id={}, txResult={}", photo.getId(), result.getSendStatus());
    }
}
`

#### 4.2.2 Nacos 配置中心配置示例

`yaml

nacos-config/ringstory-album-svc.yaml

spring:
  datasource:
    url: jdbc:mysql://{shardingsphere-proxy}:3307/ringstory?useSSL=false
    username: 
    password: 
  shardingsphere:
    sharding:
      tables:
        photo:
          actualDataNodes: ds$->{0..3}.photo_$->{2024..2027}
          tableStrategy:
            standard:
              shardingColumn: shoot_time
              preciseAlgorithmClassName: com.ringstory.sharding.ShootTimeShardingAlgorithm
          databaseStrategy:
            standard:
              shardingColumn: family_id
              preciseAlgorithmClassName: com.ringstory.sharding.FamilyIdShardingAlgorithm

配置通过 Nacos 热更新，不重启服务即可生效

`

### 4.3 年轮图谱引擎（核心算法）

年轮图谱不是简单的 SQL 查询，而是一个**预计算 + 实时聚合**的混合引擎：

`
                     ┌─────────────────────┐
                     │  Photo 写入事件       │
                     └─────────┬───────────┘
                               │
                    ┌──────────▼───────────┐
                    │  年轮图谱 Worker      │
                    │  (RocketMQ 消费者)    │
                    │  1. 解析 shoot_time   │
                    │  2. Year→Season→Month │
                    │     →Day 层级计算     │
                    │  3. 更新 Redis 树结构  │
                    │  4. 稀疏月份合并策略   │
                    └──────────┬───────────┘
                               │
                    ┌──────────▼───────────┐
                    │  Redis 年轮树缓存     │
                    │  Key: ringtree:{familyId} │
                    │  Type: Tree               │
                    └──────────┬───────────┘
                               │
                    ┌──────────▼───────────┐
                    │  API 读取 Redis 返回  │
                    │  若 Redis Miss，      │
                    │  从 MySQL 实时构建并回填│
                    └─────────────────────┘
`

**2026 年优化点**：Valkey 8 的树状数据结构原生支持层级查询，替代手工序列化方案。

---

## 5. 数据存储设计

### 5.1 数据库选型矩阵

| 数据类型 | 存储引擎 | 分片/分区策略 | 说明 |
| :------- | :------- | :------------ | :--- |
| 用户/家庭/成员关系 | MySQL 8.4 + ShardingSphere | 按 family_id 哈希分库（4库8表）| 核心业务数据，ACID 事务保障 |
| 照片元数据 | MySQL 8.4 + ShardingSphere | 按 family_id 分库 + shoot_time 按年分表 | 写入量大，按月归档 |
| 评论/点赞 | MySQL 8.4 + ShardingSphere | 按 photo_id 哈希分表 | 高频写入，关注最终一致性 |
| 全文本搜索 | Elasticsearch 8.15 | 按 family_id 分片 | 备注内容 + 照片标签全文检索 |
| 高频读缓存 | Redis 7.4 / Valkey 8 | 按 family_id 分片 | 年轮图谱、时间轴、会话 Token |
| 原始图片 | 阿里云 OSS | 按 family_id/date 路径组织 | 生命周期管理：自动沉降归档 |
| 缩略图 | 阿里云 OSS + CDN | 同原始图片 | CDN TTL 缓存，过期自动回源 |
| 审计日志 | MySQL 分表 + OSS 归档 | 按月分表，180天后转 OSS 归档 | 审计与合规需求 |
| 异步任务状态 | RocketMQ 消息轨迹 + MySQL 持久化 | 按 task_id 哈希 | 任务追踪与重试 |

### 5.2 MySQL 核心表设计

`sql
-- 用户表
CREATE TABLE t_user (
    id                BIGINT PRIMARY KEY AUTO_INCREMENT,
    open_id           VARCHAR(64) NOT NULL UNIQUE,
    nick_name         VARCHAR(64) NOT NULL DEFAULT '',
    avatar_url        VARCHAR(256) DEFAULT '',
    phone             VARCHAR(20) DEFAULT '',
    status            TINYINT DEFAULT 1 COMMENT '1:正常 2:冻结 3:注销',
    preferences       JSON COMMENT '通知偏好/关怀模式等',
    last_active_time  DATETIME,
    created_at        DATETIME DEFAULT CURRENT_TIMESTAMP,
    deleted_at        DATETIME DEFAULT NULL,
    INDEX idx_open_id(open_id),
    INDEX idx_status(status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 家庭表
CREATE TABLE t_family (
    id                BIGINT PRIMARY KEY AUTO_INCREMENT,
    name              VARCHAR(20) NOT NULL,
    cover_url         VARCHAR(256) DEFAULT '',
    created_by        BIGINT NOT NULL,
    join_type         TINYINT DEFAULT 0 COMMENT '0:直接加入 1:需审核',
    member_count      INT DEFAULT 0,
    photo_count       INT DEFAULT 0,
    storage_used      BIGINT DEFAULT 0,
    storage_limit     BIGINT DEFAULT 10737418240,
    status            TINYINT DEFAULT 1,
    created_at        DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at        DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_created_by(created_by)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 照片表（核心，分库分表）
-- 分库键：family_id % 4（4个物理库）
-- 分表键：YEAR(shoot_time)（每年一张表）
CREATE TABLE t_photo_2026 (
    id                BIGINT AUTO_INCREMENT,
    family_id         BIGINT NOT NULL,
    uploader_id       BIGINT NOT NULL,
    oss_key           VARCHAR(256) NOT NULL COMMENT 'OSS 存储路径',
    original_name     VARCHAR(128) DEFAULT '',
    md5               VARCHAR(32) NOT NULL COMMENT '文件去重',
    format            VARCHAR(10) DEFAULT 'jpeg',
    width             INT DEFAULT 0,
    height            INT DEFAULT 0,
    file_size         BIGINT DEFAULT 0,
    shoot_time        DATETIME NOT NULL,
    upload_time       DATETIME DEFAULT CURRENT_TIMESTAMP,
    latitude          DOUBLE DEFAULT NULL,
    longitude         DOUBLE DEFAULT NULL,
    is_hidden_location BOOLEAN DEFAULT TRUE,
    blur_hash         VARCHAR(64) DEFAULT '',
    status            TINYINT DEFAULT 1 COMMENT '1:正常 2:处理中 3:违规拦截',
    is_favorite       BOOLEAN DEFAULT FALSE,
    like_count        INT DEFAULT 0,
    comment_count     INT DEFAULT 0,
    updated_at        DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at        DATETIME DEFAULT NULL,
    PRIMARY KEY (id, shoot_time),
    INDEX idx_family_shoot(family_id, shoot_time),
    INDEX idx_uploader(uploader_id),
    INDEX idx_md5(md5)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 备注表
CREATE TABLE t_photo_note (
    id                BIGINT PRIMARY KEY AUTO_INCREMENT,
    photo_id          BIGINT NOT NULL,
    author_id         BIGINT NOT NULL,
    content           TEXT COMMENT '富文本备注',
    location_name     VARCHAR(128) DEFAULT '',
    mentioned_users   JSON COMMENT '[@提及的用户ID列表]',
    version           INT DEFAULT 1 COMMENT '乐观锁版本号',
    created_at        DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at        DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at        DATETIME DEFAULT NULL,
    INDEX idx_photo_id(photo_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
`

### 5.3 ShardingSphere 分片策略

`yaml

ShardingSphere 配置（Nacos 配置中心管理）

databaseStrategy:
  photo:
    shardingColumns: family_id
    shardingAlgorithm:
      type: MOD
      props:
        sharding-count: 4   # 4个数据库实例

tableStrategy:
  photo:
    shardingColumns: shoot_time
    shardingAlgorithm:
      type: CLASS_BASED
      props:
        strategy: standard
        algorithmClassName: com.ringstory.sharding.ShootTimeAlgorithm
        # 2024年 → t_photo_2024, 2025年 → t_photo_2025, ...
`

### 5.4 Elasticsearch 索引设计

`json
{
  "index": "ringstory_photos",
  "settings": {
    "number_of_shards": 3,
    "number_of_replicas": 1,
    "analysis": { "analyzer": { "ik_smart": { "type": "custom", "tokenizer": "ik_smart" } } }
  },
  "mappings": {
    "properties": {
      "photo_id":    { "type": "keyword" },
      "family_id":   { "type": "keyword" },
      "shoot_time":  { "type": "date" },
      "note_content":{ "type": "text", "analyzer": "ik_smart" },
      "tags":        { "type": "keyword" },
      "location":    { "type": "geo_point" },
      "persons":     { "type": "keyword" },
      "is_favorite": { "type": "boolean" }
    }
  }
}
`

---

## 6. AI 与智能处理管线

### 6.1 图片上传处理管线

`
OSS 上传完成
       │
       ▼
RocketMQ: photo_uploaded
       │
       ├──────────────────┬──────────────────┐
       ▼                  ▼                  ▼
┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│ 内容安全检测   │  │ 缩略图生成    │  │ 人脸检测      │
│ Worker(Java)  │  │ Worker(Java) │  │ Worker(Rust) │
│               │  │              │  │              │
│ 阿里云内容安全 │  │ OSS 图片管道  │  │ YOLOv8-face  │
│ API / 绿网    │  │ x-oss-process│  │ + ArcFace    │
└───────┬───────┘  └──────┬───────┘  └──────┬───────┘
        │                 │                  │
  违规拦截→通知管理员       │                  │
        │                 ▼                  ▼
        ▼           OSS 缩略图生成      Face 向量存入 ES
    RocketMQ       ?x-oss-process=      (按 family_id 检索)
    cms_result     image/resize,w_360
                   /format,webp
`

### 6.2 内容安全检测（阿里云绿网）

`java
@Component
public class ContentSafetyWorker {

    @Autowired
    private GreenClient greenClient;  // 阿里云内容安全 SDK
    
    @RocketMQMessageListener(topic = "cms_check", consumerGroup = "cms-group")
    public void onMessage(PhotoUploadedMessage message) {
        // 调用阿里云图片审核（鉴黄/暴恐/政治敏感）
        GreenResult result = greenClient.imageScan()
            .withTasks(new ScanTask().setDataId(message.getPhotoId())
                                     .setUrl(ossUrl))
            .suggestion(GreenSuggestion.PASS);
    
        if (result.getSuggestion() == GreenSuggestion.BLOCK) {
            // 违规：标记照片状态为违规，通知管理员
            photoService.markAsViolation(message.getPhotoId(), result.getLabel());
            notificationService.notifyAdmin(message.getFamilyId(), "照片违规拦截");
        } else {
            // 通过：标记处理完成
            photoService.markAsCompleted(message.getPhotoId());
        }
    }
}
`

### 6.3 人脸聚类引擎

| 组件 | 技术选型 | 说明 |
| :--- | :------- | :--- |
| 人脸检测 | YOLOv8-face + ONNX Runtime（Rust 绑定）| 推理速度 ~5ms/张（GPU），Rust 无 GC 抖动 |
| 特征提取 | ArcFace → ONNX 转换 | 512维 embedding，L2 归一化 |
| 向量检索 | ES dense_vector + HNSW | 阿里云 ES 支持 1024 维向量检索 |
| 聚类算法 | HDBSCAN（全量）+ 增量最近邻 | 不要求预先指定聚类数 |
| 相似度阈值 | cosine >= 0.65（可动态调整） | |

### 6.4 年轮放映室视频合成

`
Cron 触发（Spring @Scheduled + Redis 分布式锁）
       │
       ▼
review-svc：选取最佳照片 → 组装参数
       │
       ├── 主方案：阿里云 VOD（视频点播）视频合成
       │    照片上传至 OSS → VOD 模板合成 → MP4 回写 OSS → CDN
       │
       └── 降级方案：FFmpeg + drawtext 静态拼图海报
            直接输出 JPG → OSS → CDN
       │
       ▼
RocketMQ: review_complete → notification-svc 推送用户
`

---

## 7. 安全与隐私设计

### 7.1 安全架构

| 层级 | 安全措施 | 技术实现 |
| :--- | :------- | :------- |
| **传输层** | 全站 HTTPS | ALB 强制 HTTPS 重定向，TLS 1.3 |
| **应用层** | JWT 双 Token | Access Token（30min）+ Refresh Token（7天），小程序 Storage 加密存储 |
| **API 层** | 签名 + 防重放 | Spring Cloud Gateway 全局过滤器校验请求签名 |
| **数据层** | 行级权限过滤 | MyBatis-Plus 插件自动注入 family_id 条件 + ShardingSphere 数据脱敏 |
| **存储层** | 手机号加密 | AES-256 字段级加密（MyBatis TypeHandler）|
| **文件层** | OSS 私有读写 + STS 临时凭证 | 所有图片 URL 附带 STS 签名（默认 1 小时有效期）|
| **配置层** | 敏感配置加密 | Nacos 配置中心结合 jasypt-spring-boot 对数据库密码等敏感字段加密 |

### 7.2 隐私合规实现（PIPL 2026）

`
用户请求删除数据 → user-svc 标记 deleted_at
  → RocketMQ: user_delete 事件
  → 异步删除 Worker：
    1. 清除 Token / Session
    2. 用户上传的照片 → 软删除（保留 30 天）
    3. 人脸数据 → 从 ES 解绑
    4. 评论/点赞 → 匿名化
    5. 30 天后 OSS 文件物理删除
    6. Audit Log 写入删除记录
`

---

## 8. DevOps 与运维

### 8.1 持续部署流水线

`
Git Push → GitLab CI
  ├── Maven build + unit test（~5min）
  ├── Docker build (GraalVM Native Image, ~8min)
  │     └── Push to 阿里云 ACR (镜像仓库)
  └── 云效 Flow 发布
        ├── Dev 命名空间自动部署
        ├── Staging（合并 main 后）
        └── Production（手动审批 + Nacos 权重灰度）
              └── 10% → 50% → 100% 渐进放量
`

### 8.2 可观测性

`
Spring Boot Actuator + Micrometer + OTel
       │
       ├── Prometheus → Grafana 看板
       │     • 服务拓扑图
       │     • 业务看板（家庭数/上传量/回顾生成量）
       │     • 告警规则（P0/P1）
       │
       ├── 阿里云 SLS（日志服务）
       │     • 应用日志 JSON 结构化采集
       │     • 错误日志实时告警
       │
       └── 阿里云 ARMS（APM）
             • 分布式链路追踪
             • 接口响应时间/慢 SQL/Sentinel 规则分析
`

### 8.3 告警规则

| 级别 | 规则 | 响应时间 |
| :--- | :--- | :------- |
| P0 | API 错误率 > 5%（持续5分钟） | 15分钟 |
| P0 | 上传成功率 < 95% | 15分钟 |
| P1 | RocketMQ 积压 > 1000 | 30分钟 |
| P1 | MySQL 慢查询 > 100ms（持续10分钟） | 30分钟 |
| P2 | Redis 内存 > 80% | 4小时 |

---

## 9. 性能与扩展性

### 9.1 核心性能目标

| 场景 | 目标 | 手段 |
| :--- | :--- | :--- |
| 首屏加载 | <= 1.5s | uni-app 预加载 + OSS CDN + Redis 缓存 |
| 照片上传 TTFB | <= 200ms | 分片直传 OSS，不经过后端代理 |
| 年轮图谱展开 | <= 500ms | Redis 树结构缓存 |
| 全局搜索 | <= 1s | ES 全文检索 |
| 年度回顾生成 | <= 5min（异步） | RocketMQ 排队 + 并行处理 |
| 单家庭最大照片数 | 10万张 | ShardingSphere 按年分表 + 冷热分离 |
| 单家庭最大成员数 | 500人 | Redis 缓存权限位图 |

### 9.2 MySQL 扩展性规划

`
MAU 1万                MAU 10万               MAU 100万
  │                       │                      │
  ▼                       ▼                      ▼
┌──────────┐       ┌──────────────┐       ┌───────────────┐
│ 单库 MySQL │       │ ShardingSphere│       │ 单元化架构     │
│ 8C16G x1  │       │ 4库8表          │       │ 8库16表        │
│ → Redis   │       │ + ES 3节点      │       │ + ES 9节点     │
│ → OSS     │       │ + RocketMQ 集群 │       │ + 冷热分离     │
│           │       │ + 读写分离      │       │ + 只读副本      │
└──────────┘       └──────────────┘       └───────────────┘
`

---

## 10. 关键技术决策记录（ADR）

### ADR-001 使用 MySQL + ShardingSphere 而非 TiDB

**状态**：已决策  
**背景**：需要分布式数据库但团队对 MySQL 生态更熟练  
**决策**：MySQL 8.4 + ShardingSphere 5.5  
**理由**：
- 团队 MySQL 运维经验丰富，故障排查能力更强
- ShardingSphere 提供分库分表 + 读写分离 + 数据加密 + SQL 审计一站式方案
- 对于 RingStory 的体量（MVP 10 万家庭），4库8表的水平扩展足够
- 相比 TiDB，MySQL + ShardingSphere 的部署成本和运维复杂度更低
- **代价**：失去了 TiDB 的 HTAP 能力和向量检索，需要借助 ES 弥补分析查询和向量搜索

### ADR-002 使用 Nacos 而非 Consul / Eureka

**状态**：已决策  
**背景**：需要服务发现 + 配置中心一体化方案  
**决策**：Nacos 2.4+  
**理由**：
- Spring Cloud Alibaba 生态原生集成，配置热更新无需额外组件
- AP + CP 双模式切换（注册中心 AP, 配置中心 CP）
- 阿里内部大规模验证，中文社区活跃
- 控制台 Web UI 中文友好，运维门槛低

### ADR-003 异步处理使用 RocketMQ 而非 RabbitMQ / Kafka

**状态**：已决策  
**背景**：需要事务消息支撑图片上传的最终一致性  
**决策**：阿里云 RocketMQ 5.x  
**理由**：
- 事务消息原生支持"OSS 上传 → MySQL 写入"的分布式事务
- 延迟消息支持"7天后清理临时文件"等定时任务
- 阿里云商业版提供消息轨迹、死信队列、自动重试
- 与阿里云 OSS + ACK 内网互通，延迟低

### ADR-004 人脸推理使用 Rust + ONNX 而非 Java + DJL

**状态**：已决策  
**背景**：需要低延迟的人脸特征提取  
**决策**：模型转换为 ONNX 格式，Rust + ONNX Runtime 部署  
**理由**：
- Java Deep Java Library (DJL) 的推理延迟比 Rust ONNX 高 3-5x
- Rust 无 GC 停顿，适合长时间运行的推理 Worker
- 对于 RingStory，face-svc 是唯一的 GPU 密集型服务，单独维护 Rust 服务的成本可控

### ADR-005 使用 uni-app 而非原生小程序开发

**状态**：已决策  
**背景**：团队熟悉 Vue，同时需要支持小程序和 Web 管理后台  
**决策**：uni-app 3.x + Vue 3  
**理由**：
- 团队 Vue 技术栈，免去学习 React/Taro 的成本
- uni-app 一套代码编译到微信小程序 + H5，管理后台可复用 60%+ 的代码
- uni-app 在 2026 年已迭代成熟，微信小程序 API 覆盖率达 95%+

---

## 11. 第三方服务与成本

### 11.1 阿里云服务清单

| 服务 | 用途 | 预估月成本（MVP） |
| :--- | :--- | :--------------- |
| 阿里云 ECS | 后端服务器（8 台 4C8G + GraalVM） | ~2,500 CNY |
| 阿里云 RDS MySQL | 数据库（8C32G * 2 主从） | ~2,000 CNY |
| 阿里云 OSS | 图片存储（预估 50GB/月） | ~50 CNY |
| 阿里云 CDN | 图片分发（预估 200GB/月） | ~100 CNY |
| 阿里云 RocketMQ | 消息队列（基础版） | ~300 CNY |
| 阿里云 Elasticsearch | 搜索服务（3 节点） | ~1,500 CNY |
| 阿里云内容安全（绿网） | 图片审核（按次计费） | ~300 CNY |
| 阿里云 VOD | 视频合成（1000条/月） | ~800 CNY |
| 阿里云 ACK | K8s 托管集群 | ~500 CNY (管理费) |
| 阿里云 SLS | 日志服务 | ~200 CNY |
| **总计** | | **~8,250 CNY/月** |

### 11.2 成本 vs Go + 腾讯云方案对比

| 项目 | Go + 腾讯云（原方案） | Java + 阿里云（当前方案） | 差异 |
| :--- | :------------------ | :---------------------- | :--- |
| 服务器 | 6台 ~2,000 CNY | 8台（Java 资源需求高）~2,500 CNY | +500 |
| 数据库 | TiDB 3节点 ~3,000 | MySQL 2主从 ~2,000 | -1,000 |
| 消息队列 | RocketMQ ~300 | RocketMQ ~300 | 持平 |
| 对象存储+CDN | COS ~150 | OSS ~150 | 持平 |
| 搜索服务 | ES ~1,500 | ES ~1,500 | 持平 |
| 内容安全 | 腾讯云 ~300 | 阿里云 ~300 | 持平 |
| 视频合成 | 腾讯 VOD ~800 | 阿里 VOD ~800 | 持平 |
| 研发人力 | Go 工程师 ~75k/月 | Java 工程师 ~65k/月 | -10k |
| **总技术+人力** | **~82k/月** | **~73k/月** | **-9k** |

---

*RingStory — 时光为轮，记忆成书。*

