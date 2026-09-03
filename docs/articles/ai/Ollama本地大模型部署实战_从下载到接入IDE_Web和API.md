# Ollama 本地大模型部署实战：从下载到接入 IDE、Web 和 API

> 适用人群：想在自己电脑上跑大模型的开发者
> 环境：macOS / Windows / Linux 均可，全程命令可复制

## 引言：为什么要把大模型跑在本地

2026 年，云端大模型 API 已经非常成熟，但仍有越来越多开发者把模型搬到本地，核心原因有四：

1. **隐私与合规**：代码、文档、业务数据不出本机，金融/政企/内网场景的硬性要求
2. **成本可控**：API 按 token 计费，重度使用（写代码补全、批量处理）月账单可观；本地只付电费
3. **离线可用**：断网、出差、内网隔离环境也能工作
4. **可控可调**：模型、量化、上下文、提示词完全由自己掌控，还能微调

本文从零开始，覆盖一条完整的"本地大模型接入链"：**模型选型 → 安装部署 → 终端交互 → IDE 补全 → Web 界面 → API 调用 → 性能调优 → 实测对比**，全部命令可复制、可直接跑通。

---

## 一、模型怎么选：参数、显存与量化

### 1.1 先搞清楚三个概念

- **参数量（B）**：模型规模，越大越聪明，显存需求越高
- **量化（Q4_K_M / Q8_0 等）**：把权重压缩到低精度，换取显存降低和速度提升，质量损失很小（尤其是 Q4 以上）
- **上下文窗口（num_ctx）**：模型一次能"记住"的 token 数，默认 2048~4096，调大更费显存

### 1.2 主流模型速览（版本以官方模型库为准）

| 模型 | 参数量 | 定位 | 适合场景 |
|------|:---:|------|------|
| `qwen3:0.6b` | 0.6B | 轻量通用 | 老机器、简单问答 |
| `qwen3:8b` | 8B | 均衡全能 | **新手首选** |
| `qwen3:14b` | 14B | 更强推理 | 需要一定推理能力 |
| `qwen3-coder:8b` | 8B | 代码专用 | IDE 补全、代码问答 |
| `deepseek-r1:7b/14b` | 7B/14B | 推理链（CoT） | 数学、逻辑推理 |
| `llama3.3:70b` | 70B | 旗舰通用 | 高显存机器 |

### 1.3 显存参考表（Q4 量化，典型值）

| 参数量 | 量化后约需显存 | 建议 GPU |
|------|:---:|------|
| 1.5B | ~1 GB | 核显可跑 |
| 3B | ~2 GB | 8 GB 无压力 |
| 8B | ~5-6 GB | **8 GB 起步，16 GB 舒适** |
| 14B | ~9-10 GB | 16 GB |
| 32B | ~20 GB | 24 GB+ |
| 70B | ~40 GB+ | 多卡或服务器 |

> 注意：显存不够时 Ollama 会自动退化为 CPU 推理，速度会慢一个数量级。具体占用以 `ollama ps` 为准。

**选型建议**：第一次玩直接 `qwen3:8b`；写代码选 `qwen3-coder:8b`；显存只有 8 GB 可以选 `qwen3:4b` 或更低量化。

---

## 二、安装 Ollama

### 2.1 macOS

```bash
brew install ollama
```

或用官方安装包（`ollama.com/download`），双击安装后顶部菜单栏出现图标。

### 2.2 Linux

```bash
curl -fsSL https://ollama.com/install.sh | sh
```

安装脚本会同时配置 systemd 服务并开机自启。

### 2.3 Windows

官网下载 `.exe` 安装包，安装后 Ollama 以托盘程序常驻，并提供命令行工具。

### 2.4 验证安装

```bash
ollama --version
ollama serve   # 启动服务（默认端口 11434），正常情况输出 Listening on 127.0.0.1:11434
```

> 本机服务地址固定为 `http://localhost:11434`，后续 IDE、Web、API 全部走这个地址。

---

## 三、下载并运行第一个模型

### 3.1 拉取模型

```bash
ollama pull qwen3:8b
```

首次会下载几个 GB，之后可离线使用。

### 3.2 交互式对话

```bash
ollama run qwen3:8b
>>> 用三句话介绍一下你自己
```

输入 `/help` 查看内置命令，`/bye` 退出。

### 3.3 常用管理命令

| 命令 | 作用 |
|------|------|
| `ollama list` | 查看已下载的模型 |
| `ollama ps` | 查看当前加载到内存/显存的模型 |
| `ollama rm qwen3:8b` | 删除模型 |
| `ollama show qwen3:8b` | 查看模型详情（参数量、上下文长度等） |
| `ollama cp qwen3:8b my-model` | 复制模型（用于定制） |

到这一步，你已经可以在终端和本地大模型对话了。接下来把它接入三个高频场景：IDE、Web、API。

---

## 四、接入 IDE：VS Code + Continue

本地模型最常见的用途之一就是**代码补全和代码问答**。这里用 VS Code + Continue 插件（免费、支持 Ollama）。

### 4.1 安装 Continue

VS Code 扩展市场搜索 **Continue** 安装。

### 4.2 配置 Ollama 模型

Continue 使用 `~/.continue/config.yaml` 配置文件，追加：

```yaml
models:
  - name: "Local Qwen3 Coder"
    provider: ollama
    model: qwen3-coder:8b
    roles:
      - autocomplete
      - chat
      - edit
```

保存后重启窗口，右上角出现 Continue 面板。

### 4.3 使用

- **代码补全**：写代码时自动触发（`Tab` 接受建议）
- **对话**：`Ctrl + I`（内联编辑）/ `Ctrl + L`（侧边栏对话）
- **编辑**：选中代码后 `Ctrl + I` 描述修改意图

### 4.4 小技巧

- 代码补全建议用 **code 系列模型**（`qwen3-coder`），通用模型补全效果明显偏差
- 补全慢时把 `roles` 里的 `autocomplete` 拆成一个独立的小模型（如 `qwen3-coder:1.5b`），chat/edit 用 8B
- 在 Continue 面板的 `/model` 里随时切换云端和本地模型，两者可以共存

---

## 五、搭建 Web 界面：Open WebUI

终端和 IDE 之外，一个浏览器聊天界面（带历史记录、多会话、文件上传）能显著提升体验。**Open WebUI** 是当前最流行的选择。

### 5.1 Docker 部署

```bash
docker run -d -p 3000:8080 \
  -e OLLAMA_BASE_URL=http://host.docker.internal:11434 \
  -v open-webui:/app/backend/data \
  --name open-webui --restart always \
  ghcr.io/open-webui/open-webui:main
```

> `OLLAMA_BASE_URL` 指向宿主机上的 Ollama。macOS/Windows 的 Docker Desktop 用 `host.docker.internal`；Linux 用 `--network=host` 并改为 `http://127.0.0.1:11434`。

### 5.2 使用

1. 浏览器打开 `http://localhost:3000`
2. 首次进入注册管理员账号（数据存在本地 volume）
3. 顶部模型下拉框选择 `qwen3:8b` 即可对话
4. 支持多用户注册、历史会话、知识库上传（RAG）

### 5.3 可选：把 Ollama 暴露给局域网

修改 Ollama 监听地址：

```bash
# 重启前设置
export OLLAMA_HOST=0.0.0.0
ollama serve
```

之后局域网内其他设备（手机、同事电脑）也能访问 `http://<你的IP>:11434`。**注意**：暴露到局域网等于让所有人可用你的模型，务必配合防火墙。

---

## 六、通过 API 调用：Python 流式 + Function Calling

Ollama 提供两套 API：
- **原生 API**：`/api/generate`（单轮）、`/api/chat`（多轮）
- **OpenAI 兼容 API**：`/v1/chat/completions`，可直接用 OpenAI SDK，**强烈推荐**

### 6.1 命令行快速验证（原生 API）

```bash
# 单轮生成
curl http://localhost:11434/api/generate -d '{
  "model": "qwen3:8b",
  "prompt": "写一首关于秋天的五言诗",
  "stream": false
}'

# 多轮对话
curl http://localhost:11434/api/chat -d '{
  "model": "qwen3:8b",
  "messages": [
    {"role": "user", "content": "1+1等于几？"}
  ],
  "stream": false
}'
```

### 6.2 OpenAI 兼容 API

```bash
curl http://localhost:11434/v1/chat/completions \
  -H "Content-Type: application/json" \
  -d '{
    "model": "qwen3:8b",
    "messages": [{"role": "user", "content": "用一句话解释什么是递归"}]
  }'
```

> `api_key` 随便填（本地服务不校验），填 `ollama` 即可。

### 6.3 Python 流式输出

```bash
pip install openai
```

```python
from openai import OpenAI

client = OpenAI(
    base_url="http://localhost:11434/v1",  # 关键：指向本地 Ollama
    api_key="ollama",
)

stream = client.chat.completions.create(
    model="qwen3:8b",
    messages=[{"role": "user", "content": "用三句话介绍大模型量化"}],
    stream=True,  # 流式
)
for chunk in stream:
    print(chunk.choices[0].delta.content or "", end="", flush=True)
```

切换云端模型只需要改 `base_url` 和 `api_key`，**业务代码零改动**——这是用 OpenAI 兼容 API 的最大好处。

### 6.4 Function Calling（工具调用）

本地模型同样支持 tool calls，适合做 Agent。示例：让模型决定调用"查天气"工具。

```python
from openai import OpenAI

client = OpenAI(base_url="http://localhost:11434/v1", api_key="ollama")

tools = [{
    "type": "function",
    "function": {
        "name": "get_weather",
        "description": "查询指定城市的天气",
        "parameters": {
            "type": "object",
            "properties": {
                "city": {"type": "string", "description": "城市名，如北京"}
            },
            "required": ["city"]
        }
    }
}]

resp = client.chat.completions.create(
    model="qwen3:8b",
    messages=[{"role": "user", "content": "北京今天天气怎么样？"}],
    tools=tools,
)
print(resp.choices[0].message.tool_calls)
# 输出示例：工具名 get_weather，参数 {"city": "北京"}
```

拿到 `tool_calls` 后，在代码里执行真实函数，再把结果作为消息回传给模型，就完成了一个完整的 Agent 闭环。

> 注意：Function Calling 需要模型本身支持 tools，`qwen3` 系列支持良好；部分小模型（<3B）可能不稳定，建议 8B 起步。

---

## 七、性能调优

### 7.1 用 Modelfile 定制模型

复制一个模型，定制参数和系统提示词：

```bash
ollama create my-qwen -f - <<'EOF'
FROM qwen3:8b
PARAMETER temperature 0.7
PARAMETER num_ctx 8192
SYSTEM 你是一位精通 Java 的技术顾问，回答要简洁、给出代码示例。
EOF

ollama run my-qwen
```

常用 `PARAMETER`：

| 参数 | 作用 |
|------|------|
| `temperature` | 随机性，0 偏确定，1 偏发散 |
| `num_ctx` | 上下文长度，调大更聪明但更费显存 |
| `num_predict` | 单次最大生成 token 数 |
| `top_p` / `top_k` | 采样控制 |

### 7.2 常用环境变量

| 变量 | 作用 |
|------|------|
| `OLLAMA_HOST=0.0.0.0` | 允许局域网访问 |
| `OLLAMA_NUM_PARALLEL=4` | 并发处理请求数（吃显存） |
| `OLLAMA_KEEP_ALIVE=5m` | 模型驻留内存/显存时间，减少反复加载 |
| `OLLAMA_MODELS=/data/models` | 模型存放目录（默认 `~/.ollama/models`） |

### 7.3 提速三板斧

1. **确认走 GPU**：`ollama ps` 查看模型所在设备；macOS 用 Metal、N 卡用 CUDA
2. **降低量化**：`qwen3:8b` 改 `qwen3:8b-q4_K_M`，显存和速度立竿见影
3. **控制上下文**：`num_ctx` 从 8192 降到 4096，可显著降低显存占用

---

## 八、实测对比：本地 vs 云端（典型环境参考）

> 数据为典型环境（单张 4060Ti/3080 级别显卡、宽带 100M）下的量级参考，不同硬件/网络差异明显。

| 维度 | 本地（qwen3:8b Q4） | 云端 API（DeepSeek/GPT 级别） |
|------|------|------|
| 首 token 延迟 | ~50-150 ms | 500 ms - 2 s（含网络） |
| 生成速度 | 20-60 tokens/s（GPU）/ 5-15 tokens/s（CPU） | 50-100 tokens/s |
| 中文质量 | 简单问答/代码接近；复杂推理有差距 | 强 |
| 复杂推理 | 8B 偏弱，14B 明显更好 | 强 |
| 成本 | 电费，无按量计费 | 按 token 计费 |
| 隐私 | 数据不出域 | 数据上传云端 |
| 运维 | 自己管升级/显存/并发 | 零运维 |

**结论**：
- **代码补全、隐私敏感、高频低成本**场景 → 本地，性价比极高
- **复杂推理、长文生成、多模态、需要最新知识** → 云端
- 最实用的是**两者共存**：IDE 补全走本地，重活走云端（Continue / 代码里按需切换）

---

## 九、常见问题（FAQ）

| 问题 | 原因与解决 |
|------|------|
| `ollama: command not found` | 环境变量未生效，重开终端或手动加 PATH |
| 拉取模型一直失败/超时 | 网络问题，配置代理；或从镜像站拉取 |
| 生成时显存 OOM | 换更小模型 / 更低量化 / 减小 `num_ctx` |
| 生成速度只有几 tokens/s | 大概率在跑 CPU，确认 GPU 驱动与 `ollama ps` 设备列 |
| Open WebUI 连不上 Ollama | 检查 `OLLAMA_BASE_URL`（macOS/Windows 用 `host.docker.internal`） |
| Continue 补全没反应 | 确认模型支持 autocomplete 角色；换 code 系列模型 |
| 回复总是英文/乱码 | 用 Modelfile 设置 `SYSTEM 请始终用中文回答` |
| 端口 11434 被占用 | `lsof -i :11434` 查占用进程，或改 `OLLAMA_HOST` 端口 |

---

## 总结

一条命令链回顾全文：

```bash
# 安装（macOS）
brew install ollama
# 拉模型
ollama pull qwen3:8b
# 终端对话
ollama run qwen3:8b
# IDE：VS Code 装 Continue，config.yaml 指到 ollama
# Web：docker run open-webui，OLLAMA_BASE_URL 指向 11434
# API：OpenAI SDK + base_url=http://localhost:11434/v1
```

本地大模型的定位不是"替代云端"，而是补齐云端的短板：**隐私、成本、离线、低延迟**。对大多数开发者来说，正确的姿势是"本地 8B 干日常活，云端旗舰干重活"，两者互补。

跑通本文后，建议下一步：把 Function Calling 接成一个真实 Agent、用 `ollama create` 定制专属模型、或用 `ollama run` 配合 LangChain 做 RAG。祝玩得开心。
