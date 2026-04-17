# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Repository Purpose

This repo holds the user's hands-on study notes for the *Hello-agents* textbook. It is **two things in one tree**, and they do not share a build:

1. **A Spring Boot 3.5.13 / Java 21 starter** at the project root — currently a bare scaffold (`HelloAgentApplication`, empty `contextLoads` test, no business code yet). Maven coordinates: `com.agent:learn`.
2. **Per-chapter Python tutorial code** under `src/code/chapterN/` (chapters 1–16). Each chapter is self-contained; some chapters (6, 9, 12, 13, 14, 15) embed full sub-projects with their own `requirements.txt` / `pyproject.toml` / `README.md` / `.env.example`.

When the user asks about "the project", clarify whether they mean the Java app or a specific chapter — they are unrelated codebases that happen to share a directory.

## Common Commands

### Java (root)
Use the Maven Wrapper, never a system `mvn`:
```bash
./mvnw clean package           # build (jar in target/)
./mvnw spring-boot:run         # run the app
./mvnw test                    # run all tests
./mvnw -Dtest=HelloAgentApplicationTests#contextLoads test   # single test
./mvnw compile                 # compile only (use after edits per global rule)
```

### Python (per chapter)
Each chapter is run from its own directory. Activate the chapter's virtualenv (see global rules: must be `.venv` and managed with `uv`, not pip/poetry/conda):
```bash
cd src/code/chapterN
uv venv                        # creates .venv if missing
uv pip install -r requirements.txt    # only where requirements.txt exists
uv run python <script>.py
```
Sub-projects with their own `pyproject.toml` (e.g. `chapter10/weather-mcp-server`, `chapter14/helloagents-deepresearch/backend`) should be installed with `uv sync` from inside that sub-project directory.

## Architecture Notes

### Java side
- Spring Boot dependencies wired in `pom.xml`: `spring-boot-starter-web`, `spring-boot-starter-data-redis`, `mysql-connector-j` (runtime), `lombok`. There is **no MySQL/Redis connection configured yet** in `application.properties` — adding either will require config before the app boots cleanly.
- Lombok annotation processing is set up in both `default-compile` and `default-testCompile` executions; do not remove either when editing `pom.xml`.
- Per the user's global rules, when business code is added it must follow MVC + Repository pattern, programmatic transactions at the Repository layer (no `@Transactional`), and prefer `@Setter`/`@Getter`/`@ToString` over `@Data`.

### Python side — `src/code/chapterN/` layout
- File-numbered scripts (`01_xxx.py`, `02_xxx.py` …) are meant to be run **in numeric order** within a chapter — later scripts often build on artifacts or concepts from earlier ones.
- Most chapters import a shared `HelloAgents` framework. Chapter 11 illustrates the convention explicitly: scripts add `Path(__file__).parent.parent / "HelloAgents"` to `sys.path`. This `HelloAgents/` directory is **not vendored in this repo** — it must exist as a sibling of the chapter folder (or be installed) for those scripts to run. Do not invent imports for it; verify with the user where their copy lives.
- LLM configuration is unified across chapters via `.env` files (template in each chapter's `.env.example`): `LLM_MODEL_ID`, `LLM_API_KEY`, `LLM_BASE_URL`, `LLM_TIMEOUT`, plus optional tool keys like `GITHUB_PERSONAL_ACCESS_TOKEN`. The framework auto-detects the provider from these four variables.
- Chapter highlights (so you know where to look without listing every file):
  - **chapter4**: `ReAct.py`, `Reflection.py`, `Plan_and_solve.py` — classic agent loops, sharing `llm_client.py` and `tools.py`.
  - **chapter6**: four framework demos side-by-side (`AgentScopeDemo`, `AutoGenDemo`, `CAMEL`, `Langgraph`), each with its own `requirements.txt`.
  - **chapter7**: hand-rolled agent toolkit + matching `test_*.py` files (run with `uv run pytest`).
  - **chapter8**: memory + RAG progression (`MemoryTool`, `WorkingMemory`, `RAGTool`, consolidation).
  - **chapter10**: MCP and A2A protocol experiments, including a publishable `weather-mcp-server` package (own `pyproject.toml`/`Dockerfile`).
  - **chapter11**: SFT/GRPO/LoRA training pipeline using the `RLTrainingTool` from HelloAgents.
  - **chapter12**: BFCL & GAIA evaluation harnesses plus data-generation flows.
  - **chapter13**: full-stack trip planner (FastAPI backend + Vue3/TS/Vite frontend) using the Amap MCP server.
  - **chapter14**: `helloagents-deepresearch` backend (uv-managed `pyproject.toml`).
  - **chapter15**: `Helloagents-AI-Town` — Godot 4.x game frontend + FastAPI backend, multi-agent NPC simulation.
  - **chapter5**: workflow exports only (n8n JSON, Dify YAML, Coze ZIP) — not runnable Python.
  - **chapter16**: a single Markdown file (`共创路径.md`); no code.

### What is *not* here
- No CLAUDE.md previously existed (this is the first one).
- No `.cursor/rules`, `.cursorrules`, or `.github/copilot-instructions.md`.
- The root `HELP.md` is the unmodified Spring Initializr boilerplate — it carries no project-specific guidance.

## Working Conventions Specific to This Repo

- **Per global rules, always run `./mvnw compile` after Java edits** to confirm the change still builds, then clean up any throwaway code.
- **Keep the project root clean** — only the existing top-level files (`pom.xml`, `mvnw*`, `HELP.md`, `.gitignore`, `.gitattributes`, `src/`, `.mvn/`, `.idea/`, `.git/`) belong there. Put any new docs under `docs/` (formal) or `discuss/` (drafts), per the user's global rules.
- **Do not cross-pollinate chapters.** A change in `src/code/chapterN` should not import from `chapterM`; chapters are independent learning units.
- **Chinese content is expected** in many chapter READMEs, comments, and `.env.example` files — preserve it when editing; do not translate to English unless asked.
