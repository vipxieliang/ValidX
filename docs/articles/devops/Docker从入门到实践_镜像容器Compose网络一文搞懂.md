# Docker 从入门到实践：镜像、容器、Compose、网络一文搞懂

> 适用人群：会用命令行，但还没系统学过 Docker 的后端、运维、前端开发者
> 环境：macOS / Windows / Linux 均可，所有命令可直接复制
> 承诺：从零开始，一条链路学到能上手生产：**概念 → 命令 → 镜像构建 → 网络 → 数据卷 → Compose 编排 → 生产配置 → 私有仓库**

## 引言：为什么"Docker 入门"是常年 top 搜索词

Docker 解决了部署领域最痛的三个问题：**环境不一致**（"我机器上明明是好的"）、**依赖地狱**（装个 MySQL 要折腾半天依赖）、**资源浪费**（一个虚拟机只为跑一个进程）。它把"应用 + 运行环境"打包成一个标准交付物，从此部署变成"拉镜像、跑容器"两个动作。

2026 年的今天，容器已经不只是运维工具：前端、测试、数据工程师都在用。更重要的是，K8s、DevOps、云原生这些名词的底层地基，全是 Docker 这套概念。**学不懂 Docker，后面所有云原生技术都是空中楼阁。**

本文按一条完整的上手链路组织，看完你能独立完成：写 Dockerfile → 构建镜像 → 跑通多服务 → 配置生产参数 → 搭建私有仓库。

---

## 一、核心概念三件套：镜像、容器、仓库

先把三个最容易混淆的概念讲透，后面所有操作都建立在这之上：

| 概念 | 类比 | 说明 |
|------|------|------|
| **镜像（Image）** | 安装包 / 类 | 只读的模板，包含程序、依赖、配置、环境变量 |
| **容器（Container）** | 运行中的程序 / 对象 | 镜像的一个可运行实例，可启停、可删除，彼此隔离 |
| **仓库（Registry）** | 应用商店 | 存放镜像的服务器，`Docker Hub` 是最著名的公共仓库 |

```text
镜像（静态模板） --docker run--> 容器（运行实例）
                --docker push/pull--> 仓库（分发中心）
```

**三个关键直觉**：
1. 一个镜像可以同时跑出多个互不干扰的容器；
2. 对容器做的修改（装软件、改配置）**不会**写回镜像——要保存修改得重新构建或 commit（通常重新构建）；
3. 容器停止后，它的文件系统改动仍然在（除非加 `--rm`），但数据要持久化必须用数据卷（见第七章）。

---

## 二、安装与第一个容器

### 2.1 安装

- **macOS / Windows**：安装 Docker Desktop（`docker.com/products/docker-desktop`），图形化界面，自带 CLI；
- **Linux**：包管理器安装即可（Ubuntu/Debian：`apt install docker.io`，并 `systemctl enable --now docker`）。

### 2.2 验证

```bash
docker --version
docker info          # 查看安装信息
```

### 2.3 跑第一个容器

```bash
docker run hello-world
```

能打印出 "Hello from Docker!" 即成功。`hello-world` 是最小镜像，用于验证链路。

再试一个真实场景——一行命令起一个 nginx：

```bash
docker run -d -p 8080:80 --name web nginx:alpine
# -d        后台运行（detach）
# -p 8080:80 把容器内 80 端口映射到本机 8080
# --name web 给容器命名
# nginx:alpine 镜像名:标签（alpine 是小体积精简版）

curl http://localhost:8080   # 看到 nginx 欢迎页
docker logs web              # 看容器日志
docker stop web              # 停止
docker rm web                # 删除容器
```

> 顺手记忆：`docker run` = `docker pull`（下载镜像）+ `docker create`（创建容器）+ `docker start`（启动）。

---

## 三、镜像管理

### 3.1 常用命令

```bash
docker pull nginx:alpine        # 从仓库拉取镜像
docker images                   # 列出本地镜像（同 docker image ls）
docker search mysql             # 搜索仓库中的镜像
docker rmi nginx:alpine         # 删除镜像（需先删掉依赖它的容器）
docker image inspect nginx:alpine   # 查看镜像详情（含分层、入口命令）
docker history nginx:alpine     # 查看镜像的构建历史（每一层）
```

### 3.2 打标签与上传

```bash
docker tag nginx:alpine myregistry.example.com/team/nginx:v1.0
# 规范格式：仓库地址/命名空间/镜像名:版本

docker push myregistry.example.com/team/nginx:v1.0   # 推送到私有仓库
```

### 3.3 清理空间

```bash
docker system df               # 查看磁盘占用（镜像/容器/卷/缓存）
docker system prune            # 清理悬空镜像和停止的容器（-a 连未使用的镜像一起）
docker system prune -a --volumes   # 彻底清理（慎用，会删掉未使用的数据卷）
```

---

## 四、容器生命周期

### 4.1 核心命令一览

| 命令 | 作用 |
|------|------|
| `docker run` | 创建并启动容器 |
| `docker ps` / `docker ps -a` | 查看运行中 / 全部容器 |
| `docker start/stop/restart` | 启 / 停 / 重启 |
| `docker logs -f <容器>` | 跟踪查看日志 |
| `docker exec -it <容器> bash` | 进入容器内部执行命令（排障神器） |
| `docker cp <容器>:路径 本地路径` | 从容器拷文件到本地 |
| `docker rm` / `docker rmi` | 删除容器 / 镜像 |

### 4.2 常用启动参数速查

```bash
docker run -d \                     # 后台运行
  -p 8080:80 \                      # 端口映射 本机:容器
  -e MYSQL_ROOT_PASSWORD=secret \   # 注入环境变量
  -v mydata:/var/lib/mysql \        # 挂载命名数据卷
  --memory 512m --cpus 0.5 \        # 资源限制（见第九章）
  --restart unless-stopped \        # 开机自启、异常退出自动拉起
  --name mysql \                    # 命名
  mysql:8
```

### 4.3 容器排障三板斧

```bash
docker ps -a                    # 1. 容器还在吗？状态是什么（Exited=崩了，Restarting=反复重启）
docker logs -f <容器>           # 2. 日志说了什么（90% 的问题在这里）
docker exec -it <容器> bash     # 3. 进容器里看（ps、curl、cat 配置文件）
```

---

## 五、Dockerfile：构建自己的镜像

跑现成镜像只是入门，**会写 Dockerfile 才算真正会 Docker**。

### 5.1 一个最小 Dockerfile

以 Node.js 应用为例：

```dockerfile
# 语法：指令 参数（指令通常大写）
FROM node:20-alpine          # 基础镜像：node 20 的 Alpine 精简版
WORKDIR /app                 # 设置工作目录（自动创建）
COPY package*.json ./        # 先只拷依赖清单（利用层缓存，见 5.3）
RUN npm install              # 安装依赖
COPY . .                     # 拷入源码（此时才拷源码，依赖层不变）
EXPOSE 3000                  # 声明容器监听端口（仅文档性，真正映射靠 -p）
CMD ["node", "server.js"]    # 容器启动命令
```

构建并运行：

```bash
docker build -t myapp:v1 .     # 在当前目录找 Dockerfile 构建，-t 打标签
docker run -d -p 3000:3000 myapp:v1
```

### 5.2 常用指令对照表

| 指令 | 作用 | 说明 |
|------|------|------|
| `FROM` | 指定基础镜像 | 每个 Dockerfile 第一行 |
| `WORKDIR` | 设置工作目录 | 后续命令都在此目录执行 |
| `COPY` / `ADD` | 复制文件进镜像 | 优先用 COPY，ADD 额外支持解压/URL |
| `RUN` | 构建时执行命令 | 每一条 RUN 产生一个镜像层 |
| `ENV` | 设置环境变量 | 运行时也能用 |
| `ARG` | 构建参数 | 仅构建期有效，如版本号 |
| `EXPOSE` | 声明端口 | 纯文档作用 |
| `CMD` | 默认启动命令 | 可被 `docker run` 后面的命令覆盖 |
| `ENTRYPOINT` | 固定启动命令 | 不可覆盖，常与 CMD 配合传参 |
| `HEALTHCHECK` | 健康检查 | 见第九章 |

> `CMD` vs `ENTRYPOINT` 一句话：`ENTRYPOINT` 是"这个镜像就是干这个的"（固定程序），`CMD` 是"默认参数"（可被覆盖）。

### 5.3 最佳实践①：多阶段构建

问题：构建产物（编译后的二进制）只需要几十 MB，但构建环境（JDK/Maven/Go SDK）有几个 GB，不该带进最终镜像。

解法：多阶段构建——**构建阶段有完整工具链，运行阶段只拷贝产物**。

```dockerfile
# —— 阶段 1：构建（含完整工具链）——
FROM golang:1.23-alpine AS builder
WORKDIR /app
COPY go.mod go.sum ./
RUN go mod download
COPY . .
RUN CGO_ENABLED=0 go build -o server .

# —— 阶段 2：运行（极简运行时）——
FROM alpine:3.20
WORKDIR /app
COPY --from=builder /app/server .   # 只从构建阶段拷产物
EXPOSE 8080
CMD ["./server"]
```

效果：最终镜像可能只有 20 MB（对比单阶段的 800 MB+）。Java 同理：`maven` 阶段编译出 jar，`jre` 阶段只拷 jar。

### 5.4 最佳实践②：层缓存（构建提速）

Docker 按层构建，**某层没变，后续层全部复用缓存**。所以：

- **变化慢的放前面，变化快的放后面**：依赖清单 → 依赖安装 → 源码 → 编译；
- 示例里先 `COPY package*.json` + `RUN npm install`，再 `COPY . .`——这样改一行源码，依赖层缓存直接命中，构建秒级；
- 别写 `COPY . .` 后再 `RUN npm install`，否则每次改代码都重装依赖。

### 5.5 最佳实践③：.dockerignore

和 `.gitignore` 同理，排除不会进镜像的文件，避免把 `node_modules`、`target`、`.git` 塞进构建上下文（又大又泄密）：

```text
node_modules
.git
target
*.log
.DS_Store
```

---

## 六、网络：容器之间怎么通信

### 6.1 三种内置网络模式

```bash
docker network ls        # 查看网络
```

| 模式 | 说明 | 场景 |
|------|------|------|
| `bridge`（默认） | 容器间可通过 IP 通信，但**不能通过容器名**互相访问 | 单机默认 |
| `host` | 容器直接用宿主机网络，无隔离 | 追求性能、端口不冲突时 |
| `none` | 无网络 | 纯计算、安全隔离场景 |

> 默认 bridge 网络下，容器重启 IP 会变，靠 IP 通信非常脆弱——所以要用**自定义网络**。

### 6.2 自定义网络（生产正确姿势）

自定义网络自带 **DNS 解析：容器名即域名**，容器重启 IP 变了也不影响。

```bash
docker network create mynet            # 创建自定义网络
docker run -d --name app --network mynet myapp:v1
docker run -d --name db  --network mynet mysql:8

# 在 app 容器里可以直接 ping 通 db：
docker exec app ping db                # 容器名即主机名
```

应用代码里连数据库时，`host` 直接写 `db` 而不是 IP：

```js
// 连接串示例：db 是容器名，3306 是容器内端口
const conn = mysql.createConnection({ host: "db", port: 3306, user: "root", ... });
```

### 6.3 端口映射回顾

```text
宿主机 8080 端口  <- -p 8080:80->  容器 80 端口（nginx 监听）
```

- 只有映射出去的端口，外部才能访问；
- 容器与容器之间走内部网络，**不需要也不应该**互相映射端口。

---

## 七、数据卷：容器数据不丢失的关键

**容器是临时的**（删了就没了），但数据库、上传文件、日志必须持久化。三种方式：

| 方式 | 语法 | 特点 |
|------|------|------|
| 命名卷 | `-v mydata:/var/lib/mysql` | 数据由 Docker 管理，位置不可见，**推荐** |
| 绑定挂载 | `-v /host/path:/container/path` | 直接映射宿主机目录，便于查看/备份 |
| 匿名卷 | `-v /var/lib/mysql` | 不指定名字，Docker 随机分配，仅用于临时 |

```bash
# 命名卷：删容器数据还在
docker volume create mysql-data
docker run -d --name mysql \
  -v mysql-data:/var/lib/mysql \
  -e MYSQL_ROOT_PASSWORD=secret mysql:8

docker rm -f mysql          # 删容器……
docker run -d --name mysql2 \
  -v mysql-data:/var/lib/mysql \
  -e MYSQL_ROOT_PASSWORD=secret mysql:8   # ……数据完整回来了

# 绑定挂载：开发时热更新源码（改本地文件，容器内立即生效）
docker run -d -p 8080:80 -v "$PWD/site:/usr/share/nginx/html:ro" nginx:alpine
```

**备份恢复**一句话：打包数据卷目录再解压回去即可。

---

## 八、Compose：一键编排多服务

`docker run` 一次只管一个容器，真实项目往往是"前端 + 后端 + 数据库 + 缓存"多个服务。**Compose 用一份声明式 YAML 定义整个应用栈**，一条命令全部拉起。

### 8.1 一个完整示例（前后端 + MySQL + Redis）

新建 `compose.yaml`：

```yaml
services:
  web:                          # 前端：nginx 静态站点
    image: nginx:alpine
    ports:
      - "8080:80"
    volumes:
      - ./frontend:/usr/share/nginx/html
    depends_on:
      - api

  api:                          # 后端：构建本地 Dockerfile
    build: ./backend
    environment:
      DB_HOST: db               # 服务名即域名，Compose 自带 DNS
      DB_PASSWORD: secret
      REDIS_HOST: redis
    ports:
      - "3000:3000"
    depends_on:
      - db
      - redis

  db:                           # 数据库
    image: mysql:8
    environment:
      MYSQL_ROOT_PASSWORD: secret
      MYSQL_DATABASE: app
    volumes:
      - db-data:/var/lib/mysql  # 命名卷持久化
    healthcheck:                # 健康检查（见第九章）
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 10s
      retries: 5

  redis:                        # 缓存
    image: redis:7-alpine

volumes:
  db-data:                      # 声明命名卷
```

### 8.2 常用命令

```bash
docker compose up -d            # 构建并后台启动全部服务
docker compose ps               # 查看服务状态
docker compose logs -f api      # 跟踪某个服务日志
docker compose exec api bash    # 进入某个服务容器
docker compose down             # 停止并删除容器（-v 连数据卷一起删，慎用！）
docker compose up -d --build    # 代码改了，重新构建并启动
```

### 8.3 几个要点

- 文件名 `compose.yaml`（新版）/ `docker-compose.yml`（旧版）均可，新版 CLI 是 `docker compose`（中间无连字符）；
- **服务名即域名**：`api` 服务里访问 `db`、`redis` 直接写名字，网络细节全交给 Compose；
- `depends_on` 只保证启动顺序，**不保证服务就绪**（MySQL 要等就绪才收连接），所以数据库类服务要配 `healthcheck` + Compose 的长寿条件；
- 环境变量支持 `$VAR` 引用宿主环境，也支持 `.env` 文件统一管理。

---

## 九、生产环境三件套：资源限制、日志、健康检查

跑通只是第一步，上生产前这三件事必须做。

### 9.1 资源限制（防止一个容器拖垮整台机器）

```bash
docker run -d \
  --memory 512m \          # 内存上限 512MB
  --memory-swap 1g \       # 含 swap 上限
  --cpus 0.5 \             # 最多使用 0.5 核
  --pids-limit 256 \       # 进程数上限（防 fork 炸弹）
  myapp:v1

# Compose 写法
# deploy:
#   resources:
#     limits:
#       memory: 512M
#       cpus: "0.5"
```

### 9.2 日志（限制体积 + 输出到标准输出）

容器日志只认 **stdout/stderr**，应用日志要打到标准输出，再由 Docker 统一收集（`docker logs`、ELK、Loki 都靠这个）。

```yaml
# compose.yaml 中限制日志大小与轮转
logging:
  driver: json-file
  options:
    max-size: "10m"       # 单个日志文件最大 10MB
    max-file: "3"         # 最多保留 3 个
```

### 9.3 健康检查（让编排系统知道服务"活没活"）

```dockerfile
# Dockerfile 里声明
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD curl -f http://localhost:8080/health || exit 1
```

```bash
docker inspect --format='{{json .State.Health}}' <容器>   # 查看健康状态
```

> 健康检查的价值：K8s、Compose、负载均衡器依赖它做**自动重启、流量摘除、就绪等待**。没有它，编排系统只能靠"进程在不在"判断，服务假死（进程活着但响应不了）就没人管。

---

## 十、私有仓库：搭建与安全加固

企业内部镜像不能都放公共 Docker Hub，私有仓库是刚需（对应热榜 Registry 需求）。

### 10.1 一分钟起一个私有仓库

```bash
docker run -d -p 5000:5000 --name registry registry:2
docker tag myapp:v1 localhost:5000/myapp:v1
docker push localhost:5000/myapp:v1      # 推送成功即可用
docker pull localhost:5000/myapp:v1
```

### 10.2 安全加固①：密码认证

```bash
# 生成密码文件（htpasswd 来自 apache2-utils / httpd-tools）
mkdir -p /auth && htpasswd -Bc /auth/htpasswd admin

docker run -d -p 5000:5000 --name registry \
  -v "$PWD/auth:/auth" \
  -e "REGISTRY_AUTH=htpasswd" \
  -e "REGISTRY_AUTH_HTPASSWD_REALM=Registry Realm" \
  -e "REGISTRY_AUTH_HTPASSWD_PATH=/auth/htpasswd" \
  -e "REGISTRY_STORAGE_DELETE_ENABLED=true" \   # 开启删除镜像功能
  registry:2

docker login localhost:5000    # 推拉镜像前先登录
```

### 10.3 安全加固②：HTTPS（生产必须）

- 用反向代理（Nginx / Traefik）终结 TLS，把 `443` 转发给 registry；
- 或给 registry 直接配 TLS 证书（`REGISTRY_HTTP_TLS_CERTIFICATE` / `REGISTRY_HTTP_TLS_KEY`）；
- 没有 TLS 时，Docker 客户端默认拒绝非 localhost 的推送，这是保护而非麻烦。

### 10.4 安全加固③：镜像安全

```bash
docker scan <镜像>            # 官方漏洞扫描（需登录 Docker Hub）
# 或接入 Trivy / Clair 等开源扫描器，CI 里镜像构建后自动扫

docker trust inspect <镜像>   # 查看镜像签名（Docker Content Trust）
# 开启签名校验：export DOCKER_CONTENT_TRUST=1
```

**安全清单**：
- 镜像尽量用 `alpine`/`distroless` 精简版，暴露面小、更安全；
- 基础镜像固定版本（`nginx:1.27.3`），别用漂浮的 `latest`；
- 少用 `root` 运行容器，Dockerfile 里 `USER appuser`；
- 定期 `docker system prune` + 清理旧镜像，打补丁重建。

---

## 十一、常用命令速查表

| 想做什么 | 命令 |
|------|------|
| 构建镜像 | `docker build -t 名:标签 .` |
| 运行容器 | `docker run -d -p 本机:容器 镜像` |
| 看运行中的容器 | `docker ps` |
| 看全部容器 | `docker ps -a` |
| 看日志 | `docker logs -f 容器` |
| 进容器 | `docker exec -it 容器 bash` |
| 停/删容器 | `docker stop 容器` / `docker rm 容器` |
| 删镜像 | `docker rmi 镜像` |
| 网络列表/创建 | `docker network ls` / `docker network create 名` |
| 卷列表 | `docker volume ls` |
| 编排启动 | `docker compose up -d` |
| 编排停止 | `docker compose down` |
| 磁盘清理 | `docker system prune -a` |
| 本地私有仓库 | `docker run -d -p 5000:5000 registry:2` |

---

## 十二、常见坑与 FAQ

| 问题 | 原因与解法 |
|------|------|
| 端口映射不生效 | 容器里服务监听的是 `0.0.0.0` 还是 `127.0.0.1`？只监听回环就映射不出去 |
| `docker exec` 报 not found | 精简镜像（alpine）没有 bash，改用 `sh` |
| 数据在 `docker rm` 后消失 | 没挂数据卷，容器删除即数据删除，见第七章 |
| 容器反复 Restarting | `docker logs` 看启动报错，多数是配置/连接串问题 |
| 镜像太大、构建太慢 | 多阶段构建 + 层缓存顺序，见 5.3/5.4 |
| 两个容器互相访问不到 | 放同一自定义网络，用容器名通信，见 6.2 |
| 私有仓库 push 被拒（http） | 没配 TLS，客户端默认要求 https，加 `insecure-registries` 或上证书 |
| Compose 里 MySQL 总连不上 | `depends_on` 不等就绪，配 `healthcheck` + 就绪等待 |

---

## 结语：从会用命令到理解架构

本文这条链路走完，你就拥有了完整的最小知识闭环：**镜像怎么来的（Dockerfile）→ 怎么跑的（容器）→ 怎么互通的（网络）→ 数据怎么留（卷）→ 多服务怎么编排（Compose）→ 生产怎么稳（资源/日志/健康检查）→ 镜像怎么分发（私有仓库）**。

下一步可以沿着两个方向深入：往上走是 **K8s**（把多台机器的容器编排起来），往下走是 **容器原理**（namespace / cgroup / 镜像分层）。地基已经打好，祝你在云原生世界里少踩坑。

---

**相关阅读**（本仓库系列）：
- 《Ollama 本地大模型部署实战：从下载到接入 IDE、Web 和 API》——本地模型同样可以用容器跑
- 《2026 自托管指南：用一台 NAS 替代 10 个付费服务》——Docker Compose 一键部署的实践场景
