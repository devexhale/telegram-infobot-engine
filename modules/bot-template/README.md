# Telegram InfoBot Engine — Bot Template

A ready-to-run example bot built on `bot-engine` — clone it, add your token, and you have a working Telegram bot in
minutes. Use it as-is to explore the engine, or as a starting point for your own bot.

⬅️ [Back to project overview](../../README.md)

## Table of Contents

1. [About This Template](#-about-this-template)
2. [Running Locally (Development Mode)](#-running-locally-development-mode)
3. [What's Included](#-whats-included)
4. [Configuration Highlights](#-configuration-highlights)
5. [Customizing the Dialog](#-customizing-the-dialog)
6. [Production Deployment (Docker)](#-production-deployment-docker)
7. [Dependency Note](#-dependency-note)
8. [License](#-license)

## 🎯 About This Template

The template is an interactive quiz bot covering four topics — **History**, **Science**, **Geography**, and **Cinema** —
each with six questions and instant right/wrong feedback. It's built entirely from the dialog and broadcast files
bundled in this module, with no custom Java code beyond the application's entry point, showcasing dialog navigation,
media content, and broadcast messaging in a single working example.

## 🚀 Running Locally (Development Mode)

1. Clone the repository:

    ```bash
    git clone https://github.com/devexhale/telegram-infobot-engine.git
    cd telegram-infobot-engine
    ```

2. Set your bot token, either directly in `modules/bot-template/src/main/resources/application.properties` under
   `telegram.bot.token`, or by creating a git-ignored `application-secret.properties` file next to it with the same
   property — useful if you don't want the token to ever touch version control.

3. Start Redis for local development:

    ```bash
    cd modules/bot-template
    docker compose -f ./docker-compose.redis-dev.yml up --build -d
    ```

4. Run the bot:

    ```bash
    ./gradlew clean :bot-template:bootRun
    ```

5. Open a chat with your bot on Telegram and send `/start`.

## 📂 What's Included

| File / Folder                                         | Purpose                                                                                                                                                                                                                       |
|-------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `src/main/resources/dialog.yml`                       | The quiz's dialog tree — 4 topics × 6 questions. Full field reference:  [Dialog Definition Schema & Conventions](../bot-engine/README.md#-dialog-definition-schema--conventions)                                              |
| `src/main/resources/broadcast.yml`                    | Two example broadcast nodes, demonstrating a single send and a repeated, scheduled send. Full field reference: [Broadcast Definition Schema & Conventions](../bot-engine/README.md#-broadcast-definition-schema--conventions) |
| `src/main/resources/content/`                         | Media files referenced from `dialog.yml` and `broadcast.yml` — topic images, the intro audio clip, and the GitHub logo used in the broadcast example.                                                                         |
| `src/main/resources/application.properties`           | The bot's configuration. See [Configuration Highlights](#-configuration-highlights) below.                                                                                                                                    |
| `docker-compose.redis-dev.yml`                        | Spins up a local Redis instance for development.                                                                                                                                                                              |
| `Dockerfile` / `docker-compose.bot-template-prod.yml` | Container setup for running the bot in a production-style environment. See [Production Deployment](#-production-deployment-docker).                                                                                           |

## 🔧 Configuration Highlights

`application.properties` is fully commented — required properties are set, optional ones are commented out with their
default values shown. There are only two things you actually need to decide:

- **Your bot token** — `telegram.bot.token`, as described in [Running Locally](#-running-locally-development-mode).
- **Which Redis you're connecting to** — the file ships with two `spring.data.redis.*` blocks: an active one for local
  development (`localhost:6380`, matching `docker-compose.redis-dev.yml`), and a commented-out one for the Docker
  deployment (`redis:6379`, matching `docker-compose.bot-template-prod.yml`). Comment/uncomment the appropriate block
  depending on where you're running the bot.

Everything else — rate limiting, `buttons-per-row`, broadcast settings — already has sensible defaults or is pre-filled
for the template. For the full list of properties, see the Configuration Properties Reference in the `bot-engine` guide.

## 📝 Customizing the Dialog

To turn this into your own bot, replace the bundled `dialog.yml`, `broadcast.yml`, and the contents of `content/` with
your own — the property names in `application.properties` stay the same as long as you keep the same file names, or
update `telegram.bot.dialog.file-name` / `telegram.bot.broadcast.file-name` to match new ones.

For the full set of rules — required fields, content types, button constraints, and the mandatory `/start` node — see
[Dialog Definition Schema & Conventions](../bot-engine/README.md#-dialog-definition-schema--conventions)
and [Broadcast Definition Schema & Conventions](../bot-engine/README.md#-broadcast-definition-schema--conventions) in
the `bot-engine` guide.

## 🐳 Production Deployment (Docker)

The bundled `Dockerfile` and `docker-compose.bot-template-prod.yml` provide a minimal example of running the bot in a
container — not a production-grade setup, but enough to see it deployed end-to-end.

1. In `application.properties`, switch the active Redis block: comment out the local development host/port, and
   uncomment the production ones (`redis:6379`).

2. Build the application:

    ```bash
    ./gradlew clean :bot-template:build
    ```

3. Start the container stack:

    ```bash
    docker compose -f ./docker-compose.bot-template-prod.yml up --build -d
    ```

This starts two services on a shared network — the bot application and a Redis instance with a health check, so the app
waits for Redis to be ready before starting.

For an actual production deployment, go further than this template does: load the bot token from a `.env` file or a
secret manager instead of baking it into the image, set a Redis password, and avoid exposing the Redis port to the host
unless you need it for manual debugging.

## 🔗 Dependency Note

Within this repository, `bot-template` depends on `bot-engine` as a sibling Gradle module (
`implementation project(':bot-engine')`) — not through the `mavenLocal()` coordinates described in the `bot-engine`
Installation guide. That installation flow is for building your **own**, separate project against a locally published
starter; this template simply lives in the same multi-module build as the engine itself.

## 📜 License

This module is distributed under the same [Apache 2.0 License](../../LICENSE) as the rest of the project.

⬅️ [Back to project overview](../../README.md)