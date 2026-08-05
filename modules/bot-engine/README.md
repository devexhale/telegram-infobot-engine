# Telegram InfoBot Engine — Spring Boot Starter

The core Spring Boot starter of the project — the module that parses, validates, and executes YAML/JSON dialog and
broadcast definitions to run your Telegram bot. This guide covers installation, full configuration reference, and the
complete dialog/broadcast schema.

⬅️ [Back to project overview](../../README.md)

## Table of Contents

1. [Installation](#-installation)
2. [Configuration Properties Reference](#-configuration-properties-reference)
    - [Bot Identity](#bot-identity)
    - [Dialog Configuration](#dialog-configuration)
    - [Rate Limiting](#rate-limiting)
    - [Broadcast Configuration](#broadcast-configuration)
3. [Dialog Definition Schema & Conventions](#-dialog-definition-schema--conventions)
    - [Dialog Node](#dialog-node)
    - [Content](#content)
    - [Buttons](#buttons)
    - [Dialog Definition Example](#dialog-definition-example)
4. [Broadcast Definition Schema & Conventions](#-broadcast-definition-schema--conventions)
    - [Broadcast Node](#broadcast-node)
    - [Scheduling Behavior](#scheduling-behavior)
    - [Broadcast Definition Example](#broadcast-definition-example)
5. [Chat Behavior](#-chat-behavior)
    - [The 24-Hour Exception](#the-24-hour-exception)
    - [Broadcast & Dialog Interaction](#broadcast--dialog-interaction)
    - [Message Ordering Guarantees](#message-ordering-guarantees)
    - [Commands](#commands)
6. [Redis Setup](#-redis-setup)
    - [Running Redis](#running-redis)
    - [Connecting to Redis](#connecting-to-redis)
7. [Validation & Startup Errors](#-validation--startup-errors)
    - [1. Properties Validation](#1-properties-validation)
    - [2. Definitions Validation](#2-definitions-validation)
    - [3. Redis Connection Validation](#3-redis-connection-validation)
8. [Full Working Example](#-full-working-example)
9. [License](#-license)

## 📦 Installation

The starter is not yet published to Maven Central — for now, it's built and installed into your local Maven repository (
`~/.m2`).

### 1. Clone the repository

```bash
git clone https://github.com/devexhale/telegram-infobot-engine.git
cd telegram-infobot-engine
```

### 2. Publish the starter to your local Maven repository

```bash
./gradlew clean :bot-engine:publishToMavenLocal
```

### 3. Create a new Spring Boot project

Set up a clean Spring Boot project (Gradle or Maven) where you'll build your own bot.

### 4. Add the starter as a dependency

**Gradle**

Add `mavenLocal()` to your repositories:

```groovy
repositories {
    mavenLocal()
}
```

Add the dependency:

```groovy
dependencies {
    implementation 'io.github.devexhale:telegram-infobot-engine-spring-boot-starter:1.0.0'
}
```

**Maven**

Maven checks your local repository (`~/.m2`) by default, so no repository declaration is needed — just add the
dependency:

```xml

<dependency>
    <groupId>io.github.devexhale</groupId>
    <artifactId>telegram-infobot-engine-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

## 🔧 Configuration Properties Reference

### Bot Identity

Core identity settings for the bot instance.

| Property             | Required | Default      | Description                                                                                                   |
|----------------------|----------|--------------|---------------------------------------------------------------------------------------------------------------|
| `telegram.bot.token` | Required | –            | The bot token issued by [@BotFather](https://t.me/BotFather), used to authenticate with the Telegram Bot API. |
| `telegram.bot.name`  | Optional | `DefaultBot` | A display name for the bot instance.                                                                          |

### Dialog Configuration

Settings for the bot dialog operation.

| Property                              | Required | Default | Description                                                                                         |
|---------------------------------------|----------|---------|-----------------------------------------------------------------------------------------------------|
| `telegram.bot.dialog.file-name`       | Required | –       | Path to the YAML/JSON dialog definition file. Must be located on the classpath (under `resources`). |
| `telegram.bot.dialog.buttons-per-row` | Optional | `1`     | Maximum number of buttons rendered per row in inline/reply keyboards. Must be between `1` and `10`. |

### Rate Limiting

Distributed rate limits, backed by Redis and Bucket4j, that protect your bot from Telegram API bans. All six properties
are optional and come with sensible defaults out of the box.

| Property                                  | Required | Default | Description                                                                         |
|-------------------------------------------|----------|---------|-------------------------------------------------------------------------------------|
| `telegram.bot.rate-limit.global.capacity` | Optional | `25`    | Maximum number of tokens in the global bucket, shared across all chats combined.    |
| `telegram.bot.rate-limit.global.rate`     | Optional | `25`    | Number of tokens refilled into the global bucket per interval.                      |
| `telegram.bot.rate-limit.global.interval` | Optional | `1s`    | Refill interval for the global bucket.                                              |
| `telegram.bot.rate-limit.chat.capacity`   | Optional | `3`     | Maximum number of tokens in the per-chat bucket, applied individually to each chat. |
| `telegram.bot.rate-limit.chat.rate`       | Optional | `3`     | Number of tokens refilled into the per-chat bucket per interval.                    |
| `telegram.bot.rate-limit.chat.interval`   | Optional | `1s`    | Refill interval for the per-chat bucket.                                            |

### Broadcast Configuration

Settings for the broadcast (mass messaging) feature.

| Property                           | Required            | Default | Description                                                                                                                  |
|------------------------------------|---------------------|---------|------------------------------------------------------------------------------------------------------------------------------|
| `telegram.bot.broadcast.enabled`   | Optional            | `false` | Enables or disables the broadcast feature.                                                                                   |
| `telegram.bot.broadcast.file-name` | Required if enabled | –       | Path to the YAML/JSON broadcast definition file, e.g. `broadcast.yml`. Must be located on the classpath (under `resources`). |
| `telegram.bot.broadcast.timezone`  | Required if enabled | –       | Timezone used to schedule broadcasts, given as a valid IANA zone ID, e.g. `Europe/Kyiv`, `UTC`.                              |

## 📖 Dialog Definition Schema & Conventions

The entire dialog structure is defined in a single YAML/JSON file, referenced by the `telegram.bot.dialog.file-name`
property (see [Dialog Configuration](#dialog-configuration)).

The examples below use YAML. JSON is fully supported using the same structure in its own syntax.

> **Note:** Field names and their values are case-insensitive (`message`/`Message`, `type: text`/`TEXT` are all valid).
**Node keys are the exception** — `/start` and any node key referenced via `next` must be written exactly as defined,
> since they are matched as plain string keys rather than deserialized fields.

The entire dialog is a map of **Dialog Nodes**, keyed by a unique node ID. The map **must** contain a node with the key
`/start` — this is the entry point of the conversation, triggered when a user starts the bot.

### Dialog Node

| Field         | Required | Description                                                                                                           |
|---------------|----------|-----------------------------------------------------------------------------------------------------------------------|
| `content`     | Optional | A list of content items (text, photo, audio, video, document) sent before the message. See [Content](#content) below. |
| `message`     | Required | The main text of the node. Unlike text inside `content`, this text is always sent immediately before the keyboard.    |
| `button_type` | Optional | `inline` or `reply`. Defaults to `inline` at runtime if omitted.                                                      |
| `buttons`     | Required | A non-empty list of buttons. See [Buttons](#buttons) below.                                                           |

### Content

Each item in `content` is identified by its `type`: `text`, `photo`, `audio`, `video`, or `document`. A node can contain
any number of content items. They are delivered in exactly the order they're defined.

| Field       | Applies to                            | Required | Description                                                                             |
|-------------|---------------------------------------|----------|-----------------------------------------------------------------------------------------|
| `type`      | All                                   | Required | The content type: `text`, `photo`, `audio`, `video`, `document`.                        |
| `text`      | `text` only                           | Required | The text to send.                                                                       |
| `file_name` | `photo`, `audio`, `video`, `document` | Required | The file name of the media to send. The file must be placed under `resources/content/`. |
| `caption`   | `photo`, `audio`, `video`, `document` | Optional | An optional caption shown alongside the media.                                          |

### Buttons

Each item in `buttons` represents a single button.

| Field   | Required    | Description                                                                                                                                                        |
|---------|-------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `label` | Required    | The text shown on the button.                                                                                                                                      |
| `next`  | Conditional | The ID of the node to navigate to when the button is pressed. Required for `reply` buttons. For `inline` buttons, either `next` or `url` must be set — never both. |
| `url`   | Conditional | An external link opened when the button is pressed. Allowed for `inline` buttons only — never for `reply`.                                                         |

Pressing a `next` button navigates to the target node and removes the previous node's message from the chat —
see [Chat Behavior](#-chat-behavior) for the full explanation of this mechanism.

### Dialog Definition Example

A short excerpt of a dialog with a few connected nodes:

```yaml
/start:
  content:
    - type: text
      text: "Test your knowledge and learn more about the world around you!"
    - type: photo
      file_name: Qstart.jpg
      caption: "Start quest..."
    - type: audio
      file_name: start_quest.mp3
  message: "Let's begin our journey. Choose a topic: "
  button_type: inline
  buttons:
    - label: "🏛️ History"
      next: "history_q1"
    - label: "🔬 Science"
      next: "science_q1"
    - label: "🌎 Geography"
      next: "geography_q1"
    - label: "🎬 Cinema"
      next: "cinema_q1"
    - label: "🔗 GitHub"
      url: "https://github.com/devexhale/telegram-infobot-engine"

# HISTORY
history_q1:
  content:
    - type: photo
      file_name: History.jpg
    - type: text
      text: "Question 1️⃣ "
  message: "Which event that took place on June 28, 1914, became the immediate catalyst for the outbreak of World War I?"
  button_type: reply
  buttons:
    - label: "Assassination of Archduke Franz Ferdinand"
      next: "history_q1_correct"
    - label: "Signing of the Treaty of Versailles"
      next: "history_q1_wrong"
    - label: "German invasion of Belgium"
      next: "history_q1_wrong"
    - label: "Battle of the Somme"
      next: "history_q1_wrong"

history_q1_correct:
  message: "✅ Correct! The assassination of Archduke Franz Ferdinand in Sarajevo on June 28, 1914, triggered a chain of events that led to the start of World War I."
  button_type: reply
  buttons:
    - label: "Next ➡️"
      next: "history_q2"
    - label: "🔙 Back to main menu"
      next: "/start"

history_q1_wrong:
  message: "❌ Incorrect! Try again."
  button_type: reply
  buttons:
    - label: "🔄 Try again"
      next: "history_q1"
    - label: "🔙 Back to main menu"
      next: "/start"
```

## 📢 Broadcast Definition Schema & Conventions

Broadcast nodes are defined in a separate file (`telegram.bot.broadcast.file-name`) and are fully independent of each
other and of the dialog — there is no navigation or linking between broadcast nodes.

> **Note:** The same case-insensitivity rule from [Dialog Definition Schema](#-dialog-definition-schema--conventions)
> applies here — field names and values are case-insensitive.

### Broadcast Node

| Field          | Required    | Description                                                                                                                                                                                                                                                                                                        |
|----------------|-------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `content`      | Optional    | A list of content items, following the exact same rules as dialog nodes — see [Content](#content).                                                                                                                                                                                                                 |
| `message`      | Required    | The main text of the broadcast message.                                                                                                                                                                                                                                                                            |
| `return_label` | Required    | The label of the button attached to the broadcast message. Pressing it triggers the `/last` command, which restores the user's current dialog node — the previous dialog message is refreshed, while the broadcast message itself is left untouched in the chat. See [Chat Behavior](#-chat-behavior) for details. |
| `start_at`     | Required    | The scheduled date and time for the first send.                                                                                                                                                                                                                                                                    |
| `total_sends`  | Optional    | The total number of times this broadcast is sent. Defaults to `1` if omitted.                                                                                                                                                                                                                                      |
| `interval`     | Conditional | The time between consecutive sends. Required only when `total_sends` is greater than `1`.                                                                                                                                                                                                                          |

### Scheduling Behavior

- Broadcasts **don't catch up on missed sends**. If the bot was offline at a scheduled send time — whether started later
  than `start_at`, or stopped during a scheduled interval — that specific send is simply skipped, not queued or delayed.
- Skipped sends are logged, so you can always see what was missed. For example, if a send was scheduled in the past by
  the time the bot picked it up:

```
Broadcast node 'template_broadcast_node_1' send #1 at 2026-07-16T14:33 is in the past. Skipping.
```

### Broadcast Definition Example

```yaml
template_broadcast_node_1:
  content:
    - type: text
      text: "Check out the project on GitHub"
    - type: photo
      file_name: github-logo.png
  message: "https://github.com/devexhale/telegram-infobot-engine"
  return_label: "Back to the quest"
  start_at: "2026-07-16T14:33:00"
  # default total_sends value = 1

template_broadcast_node_2:
  content:
    - type: text
      text: "template content text_2"
  message: "This is a broadcast message_2"
  return_label: "Back to menu"
  start_at: "2026-06-08T10:00:00"
  total_sends: 2
  interval: "PT12H"
```

## 💬 Chat Behavior

The bot is interacted with exclusively through buttons — there is no free-text interaction. If a user sends a text
message or a file to the chat, it's treated as **irrelevant input** and deleted automatically. This is logged for
visibility:

```
Irrelevant message sent: 'some text from user'. Message deleted from chat. ChatID=5761336147
```

When a user presses a button and navigates to the next node, the current node's messages are removed from the chat
before the next one is sent. Combined with the automatic cleanup of irrelevant input, this keeps the chat free of
clutter and makes the bot feel like a fully dynamic, interactive menu rather than a growing message history.

### The 24-Hour Exception

If a user hasn't interacted with the bot for more than 24 hours, the last node's messages remain in the chat instead of
being removed on the next transition. This is a limitation of the Telegram API itself — messages older than 24 hours
cannot be deleted — not something the engine can work around. In practice, it doesn't affect the dynamic feel of the
interaction; it only means that one dialog node, sent right before the long gap, stays visible.

### Broadcast & Dialog Interaction

Every broadcast node includes a default **return button** — an inline button where only its `label` is configurable (
see [return_label](#broadcast-node)). Its purpose is to let the user jump straight back to the active dialog without
losing track of the broadcast message. When pressed, the broadcast message remains untouched; instead, the current
dialog node is removed from the chat and re-sent. The practical result is that a fresh copy of the dialog node appears
*underneath* the broadcast message in the chat history — so the broadcast message stays in place, and the dialog node is
positioned right below it, with both fully visible and interactive.

### Message Ordering Guarantees

Dialog nodes and broadcast nodes are fully atomic with respect to each other — messages are never interleaved, whether
between dialog nodes, between broadcast nodes, or between a dialog node and a broadcast node arriving at the same time.
This is enforced with per-chat locking backed by Google Guava, guaranteeing that message delivery for any given chat is
always consistent and strictly ordered.

### Commands

By default, the bot registers a single command — `/start`, which navigates to the `/start` node and can be used at any
time to jump back to the beginning of the dialog.

When broadcasting is enabled (`telegram.bot.broadcast.enabled=true`), an additional command becomes available — `/last`.
It works exactly like the broadcast [return button](#broadcast--dialog-interaction): it restores the user's current
dialog node, giving them a second, manual way back to the dialog in addition to the return button itself.

## 🔴 Redis Setup

Redis is a required piece of infrastructure for the starter — it backs dialog state, message cleanup, distributed rate
limiting, and broadcast delivery (see [Key Features](../../README.md#-key-features) for details on each). The Redis
client library itself, `spring-boot-starter-data-redis`, ships bundled with the starter — you don't need to add it as a
separate dependency.

What you do need to provide is a running Redis instance and a connection to it.

### Running Redis

The quickest way to get a local Redis instance running is via Docker:

```bash
docker run -d --name redis -p 6379:6379 redis:7.4-alpine
```

> For a ready-made Docker Compose setup (development and production), see
> the [bot-template guide](../bot-template/README.md).

### Connecting to Redis

Connection settings are provided through Spring Boot's standard Redis properties in your `application.properties`:

```properties
spring.data.redis.host=localhost
spring.data.redis.port=6379
```

If the application can't reach Redis at startup, it fails fast with a clear error pointing to the connection issue —
see [Validation & Startup Errors](#-validation--startup-errors) for details.

## 🔍 Validation & Startup Errors

The starter performs three fail-fast validation stages, in order — each one fully validating its own domain before the
next one runs:

1. **Application Properties** — all `telegram.bot.*` properties.
2. **Definitions** — the Dialog and, if enabled, Broadcast configuration files.
3. **Redis Connection** — connectivity to the configured Redis instance.

If any of the first two stages fail, the application refuses to start. All validation failures — except non-blocking
warnings — are surfaced through a Spring Boot `FailureAnalyzer`, in the same familiar format Spring Boot uses for its
own startup errors.

### 1. Properties Validation

All properties are validated together, covering every rule described
in [Configuration Properties Reference](#-configuration-properties-reference) — missing required properties and invalid
values are both reported in a single pass:

```
***************************
APPLICATION FAILED TO START
***************************

Description:

Properties failed to load. 

Required properties are missing: 
- telegram.bot.token

Invalid properties: 
- telegram.bot.dialog.file-name: file 'dialog.yml2' not found
- telegram.bot.dialog.buttons-per-row: must be between 1 and 10, got 12

Action:

Fix your properties and restart the application.
```

### 2. Definitions Validation

The Dialog file and, if broadcasting is enabled, the Broadcast file are validated together, covering every rule
described in [Dialog Definition Schema & Conventions](#-dialog-definition-schema--conventions)
and [Broadcast Definition Schema & Conventions](#-broadcast-definition-schema--conventions). Structural errors — missing
required fields, unsupported types, fields that aren't allowed for a given button or content type, and so on — prevent
the bot from starting. Each error is reported per file, pointing to the exact node, field, and (where applicable) list
index where the problem was found:

```
***************************
APPLICATION FAILED TO START
***************************

Description:

Definitions failed to load. 

Dialog map loading failed with 4 errors for file 'dialog.yml':
1. Node '/start.content[0].type' is missing or not supported
2. Node 'history_q1.button_type' is not supported
3. Node 'history_q2.buttons[0].url' must not be present for reply button
4. Node 'history_q2.buttons[0].next' is missing or blank for reply button

Broadcast map loading failed with 3 errors for file 'broadcast.yml':
1. Node 'template_broadcast_node_1.content[1].type' is missing or not supported
2. Node 'template_broadcast_node_1.message' is missing or blank
3. Node 'template_broadcast_node_2.start_at' is missing or blank

Action:

Fix your definition configuration files and restart the application.
```

**Broken node references are a separate, non-blocking case.** If the dialog structure itself is valid, but a button's
`next` points to a node key that doesn't exist anywhere in the file, the bot still starts — this is logged as a warning
rather than treated as a startup failure:

```
1. Node '/start.buttons[0].next' points to missing node 'history_q11'
2. Node '/start.buttons[3].next' points to missing node 'cinema_q12'
3. Node 'history_q1_correct.buttons[0].next' points to missing node 'history_q22'
```

### 3. Redis Connection Validation

Once properties and definitions are valid, the starter verifies that it can actually connect to Redis. If the connection
fails, startup stops with:

```
***************************
APPLICATION FAILED TO START
***************************

Description:

Connection to Redis failed. Please check your connection.

Action:

Ensure that:
• spring-boot-starter-data-redis dependency is added to pom.xml or build.gradle
• Redis server is running on the configured host/port
• spring.data.redis.host and spring.data.redis.port are correct
• Firewall / network / Docker allows connection to redis port
• Redis is not in protected mode or requires password (if configured)
```

## 🚀 Full Working Example

For a complete, runnable dialog and broadcast configuration — tying together everything covered in this guide, so you
can launch a fully working bot and see how everything works — see the [`bot-template`](../bot-template) module and
its [README](../bot-template/README.md).

## 📜 License

This module is distributed under the same [Apache 2.0 License](./LICENSE) as the rest of the project.

⬅️ [Back to project overview](../../README.md)
