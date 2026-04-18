# hello-agent

This repository documents my hands-on study of [hello-agents.datawhale.cc](https://hello-agents.datawhale.cc/). The goal is to reimplement the Python examples from the course in Java while keeping the original tutorial code in the same repo for reference.

This is not a single codebase. The repository contains two separate parts:

- A Spring Boot 3.5 / Java 21 project at the root
- The original Python tutorial code and sub-projects under `src/code/`

At the moment, the implemented Java portion focuses on **Chapter 4**, including:

- ReAct Agent
- Reflection Agent
- Plan-and-Solve Agent

## Project Layout

```text
hello-agent/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/com/agent/learn/
│   │   │   ├── HelloAgentApplication.java
│   │   │   └── chapter4/
│   │   │       ├── agent/         # ReAct / Reflection / Plan-and-Solve
│   │   │       ├── config/        # LLM and SerpApi configuration
│   │   │       ├── demo/          # Command-line demo entry
│   │   │       ├── llm/           # OpenAI-compatible chat client
│   │   │       ├── tool/          # SearchTool and ToolRegistry
│   │   │       ├── exception/     # Unified exception model
│   │   │       └── web/           # HTTP API, DTOs, exception handling
│   │   └── resources/
│   │       └── application.properties
│   ├── test/                      # Web and agent unit tests
│   └── code/                      # Original tutorial Python code and sub-projects
└── CLAUDE.md
```

## Current Scope

The current Java implementation covers three classic Chapter 4 agent workflows.

### 1. ReAct Agent

- Uses prompt instructions to force `Thought` and `Action` output
- Supports tool invocation through the `Search` tool
- The current search implementation is backed by `SerpApi`

### 2. Reflection Agent

- Produces an initial code draft
- Reflects on algorithmic efficiency
- Refines the result over multiple iterations

### 3. Plan-and-Solve Agent

- Breaks a complex question into smaller steps
- Executes the steps sequentially
- Returns the final answer from the execution phase

## Requirements

- JDK 21
- Maven 3.9+
- An OpenAI-compatible LLM endpoint
- Optional: a SerpApi key for ReAct search

## Configuration

The project uses defaults from `src/main/resources/application.properties`, and all key values can be overridden through environment variables.

Current defaults:

```properties
llm.model-id=qwen3.6-flash-2026-04-16
llm.api-key=${LLM_API_KEY:}
llm.base-url=https://dashscope.aliyuncs.com/compatible-mode/v1
llm.timeout=60

serpapi.api-key=${SERPAPI_API_KEY:}
```

You can configure the application with environment variables:

```bash
export LLM_API_KEY=your_llm_api_key
export SERPAPI_API_KEY=your_serpapi_key
```

You can also store local secrets in `src/main/resources/application-local.properties`. That file is imported optionally and is suitable for machine-local configuration.

Example:

```properties
llm.api-key=your_llm_api_key
serpapi.api-key=your_serpapi_key
```

## Running the Project

### Start the Web Application

```bash
mvn spring-boot:run
```

Once the application starts, the Chapter 4 endpoints are available under `/chapter4`.

### Run the Command-Line Demos

```bash
mvn spring-boot:run -Dspring-boot.run.arguments=react
mvn spring-boot:run -Dspring-boot.run.arguments=reflection
mvn spring-boot:run -Dspring-boot.run.arguments=plan-and-solve
```

The demo entry point is:

- `src/main/java/com/agent/learn/chapter4/demo/Chapter4DemoRunner.java`

## API Examples

### ReAct

```bash
curl -X POST http://localhost:8080/chapter4/react \
  -H "Content-Type: application/json" \
  -d '{
    "question": "What is Huawei'\''s latest phone and what are its key selling points?"
  }'
```

### Reflection

```bash
curl -X POST http://localhost:8080/chapter4/reflection \
  -H "Content-Type: application/json" \
  -d '{
    "task": "Write a Python function that finds all prime numbers between 1 and n."
  }'
```

### Plan-and-Solve

```bash
curl -X POST http://localhost:8080/chapter4/plan-and-solve \
  -H "Content-Type: application/json" \
  -d '{
    "question": "A fruit shop sold 15 apples on Monday, twice as many on Tuesday, and 5 fewer on Wednesday than on Tuesday. How many apples were sold over the three days?"
  }'
```

### Unified Success Response

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

### Unified Error Response

```json
{
  "success": false,
  "code": "VALIDATION_ERROR",
  "message": "question: question must not be blank"
}
```

Common error codes include:

- `VALIDATION_ERROR`: request field is missing or blank
- `LLM_CONFIGURATION_MISSING`: LLM configuration is incomplete
- `LLM_CALL_FAILED`: the LLM request failed
- `TOOL_CONFIGURATION_MISSING`: a required tool configuration is missing, such as `SERPAPI_API_KEY`
- `TOOL_EXECUTION_FAILED`: a tool invocation failed
- `PLAN_GENERATION_FAILED`: Plan-and-Solve could not generate a valid plan
- `ACTION_PARSE_FAILED`: ReAct could not parse a valid action from the model output

## About `src/code`

`src/code/` is not part of the Java build. It contains the original Python chapter code from the tutorial and is kept here for:

- comparing the original implementation with the Java version
- checking whether the Java port stays aligned with the tutorial logic
- supporting future chapter-by-chapter Java rewrites

Some chapters include complete standalone sub-projects, for example:

- `src/code/chapter13/helloagents-trip-planner`
- `src/code/chapter14/helloagents-deepresearch`
- `src/code/chapter15/Helloagents-AI-Town`

Each of those directories has its own dependency and runtime setup and is not part of the root Maven project.

## Current Status

This repository is still best understood as a learning and experimentation project rather than a production-ready framework.

Current highlights:

- Chapter 4 agent workflows have been reimplemented in Java
- LLM access is wrapped behind an OpenAI-compatible client
- Search is integrated through SerpApi and registered through `ToolRegistry`
- The Chapter 4 API now uses typed request DTOs, a unified response model, and global exception handling
- A first set of Chapter 4 web and agent unit tests has been added
- The repository can continue to grow chapter by chapter on top of this structure

## Tests

The current Chapter 4 test coverage includes:

- `Chapter4ControllerTest`: success responses, validation failures, and exception mapping
- `PlannerTest`: JSON plan parsing and fallback behavior
- `PlanAndSolveAgentTest`: empty-plan failure semantics and normal execution
- `ReActAgentTest`: immediate finish, action parse failure, and tool-assisted completion

Run tests with:

```bash
mvn test
```

If multiple JDKs are installed on your machine, make sure Maven is actually using Java 21.

## Next Steps

- Continue porting later tutorial chapters from Python to Java
- Expand test coverage for the current agents
- Improve failure handling and diagnostics
- Add chapter-specific implementation notes and parity documentation

## Suggested Reading Order

If you are also studying Hello Agents, the most useful way to use this repository is:

1. Read the original Python tutorial code first
2. Review the Java rewrite under `src/main/java/com/agent/learn/chapter4`
3. Compare the agent flow, prompt design, and execution model across both versions
