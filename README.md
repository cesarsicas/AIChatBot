# AI Chatbot — Offline Character Chat on Android

An Android app that lets you chat with famous historical and fictional characters entirely on-device. No internet connection is required for inference. Characters answer questions grounded in their own knowledge base through a Retrieval-Augmented Generation (RAG) pipeline running 100% locally.

Available characters:
- **Sherlock Holmes** — World's greatest consulting detective
- **Marcus Aurelius** — Roman Emperor & Stoic philosopher

---

## Screenshots



https://github.com/user-attachments/assets/cb2e454a-8036-4b23-a21c-4cd5fd01805e



---

## Architecture Overview

The project follows **Clean Architecture** with an **MVI (Model-View-Intent)** presentation layer.

```
app/
├── data/
│   ├── local/          # EmbeddingModel, BertTokenizer, VectorDatabase (JNI)
│   └── repository/     # ChatRepositoryImpl, RagRepositoryImpl
├── di/                 # Hilt modules (AppModule, RagModule)
├── domain/
│   ├── model/          # Character, ChatMessage, ModelStatus, Result
│   ├── repository/     # ChatRepository, RagRepository (interfaces)
│   └── usecase/        # BuildRagContextUseCase, SendMessageUseCase,
│                       # DownloadModelUseCase, ImportModelUseCase,
│                       # InitializeEngineUseCase
├── presentation/
│   ├── characterselection/
│   ├── chat/
│   └── navigation/
└── cpp/                # sqlite3 + sqlite-vec + JNI bridge (CMake)
```

---

## Technologies & Libraries

### Language & Build
| Tool | Version |
|---|---|
| Kotlin | 2.2.10 |
| Android Gradle Plugin (AGP) | 9.2.1 |
| KSP (Kotlin Symbol Processing) | 2.3.9 |
| CMake (NDK native build) | 3.22.1 |
| Min SDK | 24 |
| Target SDK | 36 |
| ABI filters | `arm64-v8a`, `x86_64` |

### UI
| Library | Purpose |
|---|---|
| Jetpack Compose BOM `2026.02.01` | Declarative UI toolkit |
| Material3 | Design system & components |
| `material-icons-extended` | Extended icon set |
| Navigation Compose `2.8.9` | In-app navigation with back stack |
| Activity Compose `1.13.0` | `ComponentActivity` + Compose integration |

### Architecture & DI
| Library | Purpose |
|---|---|
| Hilt `2.59` | Dependency injection (Dagger-based) |
| Hilt Navigation Compose `1.2.0` | `hiltViewModel()` scoped to nav destinations |
| Lifecycle ViewModel Compose `2.10.0` | `collectAsStateWithLifecycle`, `viewModelScope` |
| Kotlin Coroutines `1.10.1` | Async operations, `Flow` for streaming responses |

### On-Device LLM Inference
| Library | Purpose |
|---|---|
| **LiteRT LM** (`com.google.ai.edge.litertlm`) `0.12.0` | Google's on-device LLM runtime (formerly LiteRT/TFLite LM) |
| **Gemma 3 1B IT int4** (`.litertlm` model file) | The language model (~1 GB, 4-bit quantized) |

### RAG / Embeddings
| Library | Purpose |
|---|---|
| **ONNX Runtime Android** `1.20.0` | Runs the embedding model on-device |
| **all-MiniLM-L6-v2** (`.onnx`) | Sentence embedding model (384-dim vectors) |
| **sqlite-vec** (C extension, compiled via NDK) | KNN vector similarity search inside SQLite |
| **SQLite** (amalgamation, compiled via NDK) | Embedded relational + vector database |

---

## Detailed Chat Flow

### 1. Character Selection

The app opens on `CharacterSelectionScreen`. The user picks a character, which triggers `CharacterSelectionIntent.SelectCharacter`. The ViewModel emits a navigation event and the nav graph navigates to `ChatScreen`, passing the `characterId` as a route argument.

---

### 2. Model Setup (first launch only)

On arrival at `ChatScreen`, `ChatIntent.Initialize(character)` is dispatched. The ViewModel checks whether the model file exists in `filesDir`.

**If the model is absent** — `ModelSetupScreen` is shown with two options:

- **Download** — streams `gemma3-1b-it-int4.litertlm` from HuggingFace over HTTP, writing to a `.tmp` file and atomically renaming it on completion. Progress is reported via `ModelStatus.Transferring`.
- **Import from storage** — opens a file picker (`ActivityResultContracts.OpenDocument`). The chosen file is copied into `filesDir` with the same progress-reporting mechanism.

Once the file exists, `InitializeEngineUseCase` runs automatically.

---

### 3. Engine Initialization

```
InitializeEngineUseCase
  └─ ChatRepository.initializeEngine()
       ├─ SamplerConfig(topK=40, topP=0.9, temperature=0.4, seed=0)
       ├─ Engine(EngineConfig(modelPath, maxNumTokens=2048)).initialize()
       └─ Conversation = engine.createConversation(ConversationConfig(samplerConfig))
```

`ModelStatus` transitions: `Absent → Initializing → Ready`

The UI shows an indeterminate `LinearProgressIndicator` while initializing, and enables the input bar once `Ready`.

---

### 4. RAG Pipeline — triggered on every message

When the user hits Send, `ChatViewModel.sendMessage()` executes the following pipeline on the IO/Default dispatcher:

#### Step 1 — Embedding the query

```
BertTokenizer.encode(userText)
```
- Normalizes text (NFD, lowercase, strip diacritics)
- Applies basic tokenization (whitespace + punctuation splitting, CJK char isolation)
- Applies WordPiece sub-word tokenization against `vocab.txt` (30 522 tokens)
- Prepends `[CLS]` (101) and appends `[SEP]` (102), truncates to max 256 tokens
- Produces `inputIds`, `attentionMask`, `tokenTypeIds` as `LongArray`

```
EmbeddingModel.embed(userText)  →  FloatArray (384 dimensions)
```
- Loads `all-MiniLM-L6-v2.onnx` from assets at first use (kept open as a singleton)
- Creates ONNX tensors from the tokenizer output
- Runs the ONNX session → `last_hidden_state` tensor `[1, seqLen, 384]`
- **Mean pooling** — averages the token embeddings of non-masked positions
- **L2 normalization** — normalizes the resulting vector to unit length

#### Step 2 — Vector search

```
VectorDatabase.search(embedding, characterId, topK=3)
```

On first call, `characters_rag.db` is copied from `assets/` to `filesDir` (one-time operation).

The search is executed via **JNI** in native C++:

```cpp
// vector_search.cpp
sqlite3_open_v2(dbPath, ...)
sqlite3_vec_init(db, ...)        // loads the sqlite-vec extension

SELECT content, character_id
FROM character_knowledge
WHERE embedding MATCH ? AND k = ?
ORDER BY distance
```

- `sqlite-vec` exposes a virtual KNN table that matches the query vector against the stored 384-dim embeddings using cosine/L2 distance.
- Results are filtered by `character_id` to return only knowledge from the selected character.
- Returns the top-3 most relevant text chunks.

#### Step 3 — Prompt augmentation

```kotlin
// SendMessageUseCase
"Use the following context to answer the question.\n\n" +
"Context:\n" +
"[sherlock_holmes]: <chunk 1>\n\n" +
"[sherlock_holmes]: <chunk 2>\n\n" +
"[sherlock_holmes]: <chunk 3>\n\n" +
"Question: <user message>"
```

---

### 5. Streaming inference

```
ChatRepository.streamResponse(augmentedPrompt)
  └─ conversation.sendMessageAsync(augmentedPrompt)   // LiteRT LM
       └─ Flow<Content.Text>  →  token-by-token emission
```

- `sendMessageAsync` returns a `Flow` of response objects; each carries incremental `Content.Text` tokens.
- The ViewModel collects this flow and appends each token to the last `ChatMessage` in the UI state, producing a live streaming effect.
- Generation is capped at **400 output tokens** per response.
- The input bar is disabled (`isGenerating = true`) until the flow completes.

---

### 6. State machine summary

```
ModelStatus.Absent
  │  (file download / import)
  ▼
ModelStatus.Transferring(progress, label)
  │  (copy complete)
  ▼
ModelStatus.Initializing
  │  (Engine.initialize() + createConversation())
  ▼
ModelStatus.Ready  ──►  chat loop (RAG + streaming)
  │
  └──► ModelStatus.Failure(message)  on any error
```

---

## RAG Database

`characters_rag.db` is a pre-built SQLite database shipped inside `assets/`. It contains a `character_knowledge` table with:

| Column | Type | Description |
|---|---|---|
| `content` | TEXT | A knowledge chunk (sentence or paragraph) |
| `character_id` | TEXT | `"sherlock_holmes"` or `"marcus_aurelius"` |
| `embedding` | BLOB | 384-float vector (little-endian IEEE 754) |

The sqlite-vec virtual table extension enables sub-millisecond ANN (Approximate Nearest Neighbor) queries directly in SQLite without any external vector store.

---

## Getting Started

### Prerequisites
- Android Studio Meerkat or newer
- NDK installed (the build requires CMake 3.22+ to compile the native sqlite-vec layer)
- A device or emulator with `arm64-v8a` or `x86_64` ABI

### Build & Run
```bash
git clone <repo-url>
cd AIChatbot
./gradlew installDebug
```

### Obtaining the LLM
On first launch the app will prompt you to either:
1. **Download automatically** — requires ~1 GB of data (Wi-Fi recommended)
2. **Import manually** — download `gemma3-1b-it-int4.litertlm` from [HuggingFace litert-community/Gemma3-1B-IT](https://huggingface.co/litert-community/Gemma3-1B-IT) and pick the file from device storage

---

## Project Structure — Key Files

| File | Role |
|---|---|
| `BertTokenizer.kt` | WordPiece tokenizer for all-MiniLM-L6-v2 |
| `EmbeddingModel.kt` | ONNX session wrapper, mean pool + L2 norm |
| `VectorDatabase.kt` | Asset DB copy + JNI bridge to sqlite-vec |
| `vector_search.cpp` | Native KNN query via sqlite3 + sqlite-vec |
| `ChatRepositoryImpl.kt` | LiteRT LM engine lifecycle + streaming |
| `RagRepositoryImpl.kt` | Orchestrates embed → search → context |
| `SendMessageUseCase.kt` | Builds the RAG-augmented prompt |
| `ChatViewModel.kt` | MVI state machine + token streaming |
| `AppNavGraph.kt` | Navigation graph (character selection → chat) |
