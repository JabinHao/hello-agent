# hello-agent

这是我在学习 [hello-agents.datawhale.cc](https://hello-agents.datawhale.cc/) 过程中的练习仓库，目标是用 Java 逐步复现教程中的 Python 示例，并保留原始章节代码作为对照参考。

当前仓库不是单一项目，而是两部分内容放在同一个目录中：

- 根目录是一个基于 Spring Boot 3.5 / Java 21 的 Java 实现项目
- `src/code/` 下保存了 Hello Agents 教程各章节的 Python 示例与配套子项目

目前已经落地的是 **Chapter 4 的几个基础 Agent 模式 Java 版本**，包括：

- ReAct Agent
- Reflection Agent
- Plan-and-Solve Agent

## 项目结构

```text
hello-agent/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/com/agent/learn/
│   │   │   ├── HelloAgentApplication.java
│   │   │   └── chapter4/
│   │   │       ├── agent/         # ReAct / Reflection / Plan-and-Solve
│   │   │       ├── config/        # LLM 与 SerpApi 配置
│   │   │       ├── demo/          # 命令行演示入口
│   │   │       ├── llm/           # OpenAI-compatible Chat API 封装
│   │   │       ├── tool/          # SearchTool 与 ToolRegistry
│   │   │       ├── exception/     # 统一异常模型
│   │   │       └── web/           # HTTP API、DTO、异常处理
│   │   └── resources/
│   │       └── application.properties
│   ├── test/                      # Web + Agent 单元测试
│   └── code/                      # 教程原始 Python 代码与各章节子项目
└── CLAUDE.md
```

## 当前实现范围

当前 Java 部分主要聚焦在 Chapter 4，对应三种经典 Agent 工作流：

### 1. ReAct Agent

- 通过 Prompt 约束模型输出 `Thought` 和 `Action`
- 支持调用 `Search` 工具
- 工具当前接入的是 `SerpApi`

### 2. Reflection Agent

- 先生成一版代码
- 再让模型从算法效率角度进行反思
- 根据反馈多轮优化输出结果

### 3. Plan-and-Solve Agent

- 先把复杂问题拆成步骤
- 再按步骤逐步执行
- 最终返回最后一步的结果

## 环境要求

- JDK 21
- Maven 3.9+
- 可用的 OpenAI-compatible 模型接口
- 可选：SerpApi Key（仅 ReAct 搜索能力需要）

## 配置说明

项目使用 `src/main/resources/application.properties` 中的默认配置，并支持通过环境变量覆盖。

当前关键配置如下：

```properties
llm.model-id=qwen3-max
llm.api-key=${LLM_API_KEY:}
llm.base-url=https://dashscope.aliyuncs.com/compatible-mode/v1
llm.timeout=60

serpapi.api-key=${SERPAPI_API_KEY:}
```

你可以直接使用环境变量：

```bash
export LLM_API_KEY=your_llm_api_key
export SERPAPI_API_KEY=your_serpapi_key
```

也可以在 `src/main/resources/application-local.properties` 中写本地私有配置。该文件已被设计为可选导入，适合放本地密钥。

示例：

```properties
llm.api-key=your_llm_api_key
serpapi.api-key=your_serpapi_key
```

## 启动方式

### 运行 Web 服务

```bash
mvn spring-boot:run
```

默认启动后可通过 Chapter 4 接口进行调用。

### 运行命令行 Demo

```bash
mvn spring-boot:run -Dspring-boot.run.arguments=react
mvn spring-boot:run -Dspring-boot.run.arguments=reflection
mvn spring-boot:run -Dspring-boot.run.arguments=plan-and-solve
```

对应演示逻辑位于：

- `src/main/java/com/agent/learn/chapter4/demo/Chapter4DemoRunner.java`

## API 示例

### 1. ReAct

```bash
curl -X POST http://localhost:8080/chapter4/react \
  -H "Content-Type: application/json" \
  -d '{
    "question": "华为最新手机是什么？它的卖点有哪些？"
  }'
```

### 2. Reflection

```bash
curl -X POST http://localhost:8080/chapter4/reflection \
  -H "Content-Type: application/json" \
  -d '{
    "task": "Write a Python function that finds all prime numbers between 1 and n."
  }'
```

### 3. Plan-and-Solve

```bash
curl -X POST http://localhost:8080/chapter4/plan-and-solve \
  -H "Content-Type: application/json" \
  -d '{
    "question": "A fruit shop sold 15 apples on Monday, twice as many on Tuesday, and 5 fewer on Wednesday than on Tuesday. How many apples were sold over the three days?"
  }'
```

### 统一成功响应

```json
{
  "success": true,
  "code": "OK",
  "message": "success",
  "data": {
    "answer": "..."
  }
}
```

### 统一失败响应

```json
{
  "success": false,
  "code": "VALIDATION_ERROR",
  "message": "question: question must not be blank"
}
```

当前常见错误码包括：

- `VALIDATION_ERROR`：请求参数缺失或为空
- `LLM_CONFIGURATION_MISSING`：未配置 LLM 相关参数
- `LLM_CALL_FAILED`：模型调用失败
- `TOOL_CONFIGURATION_MISSING`：工具缺少必要配置，例如 `SERPAPI_API_KEY`
- `TOOL_EXECUTION_FAILED`：工具调用执行失败
- `PLAN_GENERATION_FAILED`：Plan-and-Solve 未能生成有效计划
- `ACTION_PARSE_FAILED`：ReAct 输出未能解析出合法 Action

## `src/code` 目录说明

`src/code/` 不是 Java 项目的一部分，而是我在学习教程时保留下来的 Python 章节代码，用来：

- 对照原教程实现思路
- 核对 Java 版本是否和原始示例一致
- 后续继续按章节迁移到 Java

其中部分章节是完整子项目，例如：

- `src/code/chapter13/helloagents-trip-planner`
- `src/code/chapter14/helloagents-deepresearch`
- `src/code/chapter15/Helloagents-AI-Town`

这些目录各自有自己的依赖和启动方式，不参与根目录 Maven 构建。

## 当前状态

当前仓库更适合作为学习记录和实验项目，而不是生产级框架，现阶段特点包括：

- 已完成 Chapter 4 的基础 Agent 机制 Java 化
- LLM 调用采用 OpenAI-compatible API 封装
- 搜索工具已接入 SerpApi，并通过 `ToolRegistry` 统一注册
- Chapter 4 API 已改为独立请求 DTO + 统一响应体 + 全局异常处理
- 已补充一批 Chapter 4 的 Web/Agent 单元测试
- 后续可以继续按教程章节逐步补全 Java 版本

## 测试

当前已补充的 Chapter 4 测试包括：

- `Chapter4ControllerTest`：接口成功返回、参数校验、异常映射
- `PlannerTest`：计划 JSON 解析与失败回退
- `PlanAndSolveAgentTest`：空计划失败语义与成功执行路径
- `ReActAgentTest`：直接完成、Action 解析失败、工具调用后完成

运行方式：

```bash
mvn test
```

如果你本机有多个 JDK，需确认 Maven 实际使用的是 Java 21。

## 后续计划

- 按章节继续把 Python 示例迁移到 Java
- 为现有 Agent 增加更完整的测试
- 补充更稳定的错误处理与日志信息
- 逐步整理每章的实现笔记与差异说明

## 说明

如果你也是在学习 Hello Agents，这个仓库更适合这样使用：

1. 先阅读教程原始 Python 示例
2. 再看我在 `src/main/java/com/agent/learn/chapter4` 下的 Java 改写
3. 对照两边的 Agent 思路、Prompt 设计和执行流程

如果你希望，我下一步还可以继续帮你补：

- `README` 的英文版
- 更详细的“Chapter 4 设计说明”
- 接口时序图 / 类图
- 运行截图与调用结果示例
