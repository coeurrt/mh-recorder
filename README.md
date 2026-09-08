# 🎥 MH Recorder

A small Java desktop project that controls **OBS Studio** through **OBS WebSocket** to record Monster Hunter hunts.

> 🚧 **Work in progress / POC**
>
> The project is currently focused on building a reliable OBS integration and a minimal JavaFX interface before moving on to automatic hunt detection and video management.

---

## ✨ Current features

- Connect to OBS through WebSocket
- OBS WebSocket authentication
- Start recording from the application
- Stop recording from the application
- Retrieve the path of the recorded video
- Listen to OBS `RecordStateChanged` events
- Display recording status in a minimal JavaFX UI
- Basic handling of failed OBS requests

---

## 🖥️ Current UI

The current interface is intentionally minimal:

- **Record** button
- **Stop** button
- Recording status

The goal for now is functionality and learning, not visual polish.

---

## 🛠️ Tech stack

- **Java 25**
- **Maven**
- **JavaFX**
- **Jackson**
- **Java-WebSocket**
- **OBS Studio**
- **OBS WebSocket 5.x**

---

## ✅ Requirements

Before running the project, you need:

- **JDK 25**
- **OBS Studio**
- OBS WebSocket enabled

In OBS:

1. Open **Tools**
2. Open **WebSocket Server Settings**
3. Enable the WebSocket server
4. Keep note of the port and password

The default OBS WebSocket port is usually:

```text
4455
```

The project currently expects OBS on:

```text
ws://localhost:4455
```

### OBS password

Do **not** hard-code the OBS password in the source code.

The project uses an environment variable:

```text
OBS_PASSWORD
```

In IntelliJ IDEA, it can be configured under:

```text
Run
→ Edit Configurations
→ Environment variables
```

Example:

```text
OBS_PASSWORD=your_password
```

---

## ▶️ Running the project

The application is launched through the regular Java entry point in `Main`.

The startup flow is currently:

```text
Main
 ↓
OBS WebSocket client
 ↓
JavaFX application
 ↓
MH Recorder UI
```

Run `Main` directly from IntelliJ IDEA.

---

## 🧱 Project structure

```text
src/main/java/com/coeurrt/
│
├── Main.java
│
└── mhrecorder/
    ├── obs/
    │   ├── ObsWebSocketClient.java
    │   ├── ObsHandler.java
    │   └── ObsRequestFactory.java
    │
    └── ui/
        └── MhRecorderApplication.java
```

### Responsibilities

**`ObsWebSocketClient`**  
Handles the WebSocket connection, receives OBS messages and sends requests.

**`ObsHandler`**  
Interprets OBS messages such as authentication responses and recording state events.

**`ObsRequestFactory`**  
Builds JSON requests sent to OBS.

**`MhRecorderApplication`**  
Contains the current JavaFX interface and reacts to user actions.

---

## 🔄 OBS communication

The current connection flow is:

```text
Java client
   ↓
Connect to OBS
   ↓
Hello (op 0)
   ↓
Authentication challenge
   ↓
Identify (op 1)
   ↓
Identified (op 2)
   ↓
OBS requests enabled
```

Recording commands currently use OBS request messages:

```text
StartRecord
StopRecord
```

OBS responses and events are then used to update the application state and retrieve the final video path.

---

## 🗺️ Roadmap

### Next steps

- [ ] Track the full connection lifecycle
- [ ] Disable recording controls until OBS is identified
- [ ] Handle OBS disconnection cleanly
- [ ] Read the initial recording state with `GetRecordStatus`
- [ ] Separate connection status from recording status
- [ ] Display the latest recorded file in the UI

### Later

- [ ] Improve the JavaFX interface
- [ ] Store hunt metadata locally
- [ ] Automatically organize recorded videos
- [ ] Detect Monster Hunter quest start/end
- [ ] Automatically cut recordings
- [ ] Create short clips
- [ ] Add configurable storage limits
- [ ] Package the application for Windows

---

## 🎓 Learning project

MH Recorder is also a project I use to improve my Java skills.

The goal is not only to make the application work, but to understand the concepts behind it:

- Object-oriented design
- Separation of responsibilities
- WebSockets
- JSON
- Event-driven programming
- Callbacks
- JavaFX
- Threading between JavaFX and WebSocket events
- File handling
- Git workflows
- Application architecture

The project is intentionally developed step by step instead of starting with a large architecture.

---

## 📌 Project status

Current milestone:

```text
OBS connection     ✅
Authentication     ✅
Start recording    ✅
Stop recording     ✅
Output path        ✅
OBS events         ✅
Basic JavaFX UI    ✅

Automatic hunts    ⏳
Video management   ⏳
```

---

## 📄 License

No license has been selected yet.
