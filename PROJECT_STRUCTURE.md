# Project Structure

## 📁 Complete Directory Tree

```
ModularComposeCleanArchitecture/
│
├── 📄 settings.gradle.kts              # Module configuration
├── 📄 build.gradle.kts                 # Root build file
├── 📄 gradle.properties                # Gradle properties
├── 📄 README.md                        # Project overview
├── 📄 ARCHITECTURE.md                  # Architecture documentation
├── 📄 PROJECT_STRUCTURE.md             # This file
│
├── 📦 core/                            # Core module (Pure Kotlin - JVM)
│   ├── 📄 build.gradle.kts
│   └── src/main/java/com/example/core/
│       ├── 📄 Resource.kt              # ✨ Loading/Success/Error wrapper
│       ├── 📄 UseCase.kt               # ✨ Base UseCase classes
│       └── 📄 NetworkException.kt      # Custom exceptions
│
├── 📦 coreui/                          # CoreUI module (Android Library + Compose)
│   ├── 📄 build.gradle.kts
│   ├── 📄 src/main/AndroidManifest.xml
│   └── src/main/java/com/example/coreui/
│       ├── components/
│       │   ├── 📄 LoadingIndicator.kt  # ✨ Reusable loading component
│       │   └── 📄 ErrorMessage.kt      # ✨ Reusable error component
│       └── state/
│           └── 📄 UiState.kt           # ✨ UI state wrapper
│
├── 📦 network/                         # Network module (Pure Kotlin - JVM)
│   ├── 📄 build.gradle.kts
│   └── src/main/java/com/example/network/
│       ├── api/
│       │   └── 📄 ApiService.kt        # ✨ Retrofit API interface
│       ├── model/
│       │   └── 📄 UserDto.kt           # Data Transfer Object
│       ├── datasource/
│       │   └── 📄 RemoteDataSource.kt  # ✨ API call handler
│       └── 📄 NetworkClient.kt         # ✨ Retrofit configuration
│
├── 📦 feature/                         # Feature modules
│   └── users/                          # Users feature (Android Library + Compose)
│       ├── 📄 build.gradle.kts
│       ├── 📄 src/main/AndroidManifest.xml
│       └── src/main/java/com/example/feature/users/
│           │
│           ├── 🎯 domain/              # ✨ DOMAIN LAYER (Business Logic)
│           │   ├── model/
│           │   │   └── 📄 User.kt      # Domain model (pure entity)
│           │   ├── repository/
│           │   │   └── 📄 UserRepository.kt  # Repository interface
│           │   └── usecase/
│           │       ├── 📄 GetUsersUseCase.kt     # ✨ Get all users
│           │       └── 📄 GetUserByIdUseCase.kt  # ✨ Get user by ID
│           │
│           ├── 💾 data/                # ✨ DATA LAYER (Data Management)
│           │   ├── repository/
│           │   │   └── 📄 UserRepositoryImpl.kt  # ✨ Repo implementation
│           │   └── mapper/
│           │       └── 📄 UserMapper.kt          # DTO ↔ Domain mapper
│           │
│           ├── 🎨 presentation/        # ✨ PRESENTATION LAYER (UI)
│           │   ├── 📄 UsersViewModel.kt          # ✨ UI state management
│           │   └── 📄 UsersScreen.kt             # ✨ Compose UI
│           │
│           └── 💉 di/                  # Dependency Injection
│               └── 📄 UsersModule.kt   # ✨ Hilt module
│
└── 📦 app/                             # Main app module (Android Application)
    ├── 📄 build.gradle.kts
    ├── 📄 proguard-rules.pro
    ├── 📄 src/main/AndroidManifest.xml
    └── src/main/
        ├── java/com/example/modularcomposecleanarchitecture/
        │   ├── 📄 MainApplication.kt   # ✨ Application class (@HiltAndroidApp)
        │   ├── 📄 MainActivity.kt      # ✨ Main activity (@AndroidEntryPoint)
        │   └── ui/theme/
        │       ├── 📄 Theme.kt         # Material 3 theme
        │       ├── 📄 Color.kt         # Color palette
        │       └── 📄 Type.kt          # Typography
        └── res/
            ├── values/
            │   ├── 📄 strings.xml
            │   └── 📄 themes.xml
            └── mipmap/
                ├── ic_launcher.png
                └── ic_launcher_round.png
```

## 🎯 Module Dependencies Graph

```
                            ┌─────────┐
                            │   APP   │
                            │ Module  │
                            └────┬────┘
                                 │
            ┌────────────────────┼────────────────────┐
            │                    │                    │
            ▼                    ▼                    ▼
     ┌──────────┐         ┌──────────┐        ┌─────────────┐
     │  COREUI  │         │ NETWORK  │        │   FEATURE   │
     │  Module  │         │  Module  │        │    :users   │
     └────┬─────┘         └────┬─────┘        └──────┬──────┘
          │                    │                     │
          │                    │            ┌────────┼────────┐
          │                    │            │        │        │
          └────────────────────┼────────────┘        │        │
                               │                     │        │
                               ▼                     │        │
                         ┌──────────┐                │        │
                         │   CORE   │◄───────────────┘        │
                         │  Module  │◄────────────────────────┘
                         └──────────┘
```

## 📋 File Count by Layer

### Core Module (4 files)
- ✅ Resource.kt
- ✅ UseCase.kt
- ✅ NetworkException.kt
- ✅ build.gradle.kts

### CoreUI Module (5 files)
- ✅ UiState.kt
- ✅ LoadingIndicator.kt
- ✅ ErrorMessage.kt
- ✅ build.gradle.kts
- ✅ AndroidManifest.xml

### Network Module (6 files)
- ✅ ApiService.kt
- ✅ UserDto.kt
- ✅ RemoteDataSource.kt
- ✅ NetworkClient.kt
- ✅ build.gradle.kts

### Feature:Users Module (11 files)
**Domain (4)**:
- ✅ User.kt
- ✅ UserRepository.kt
- ✅ GetUsersUseCase.kt
- ✅ GetUserByIdUseCase.kt

**Data (2)**:
- ✅ UserRepositoryImpl.kt
- ✅ UserMapper.kt

**Presentation (2)**:
- ✅ UsersViewModel.kt
- ✅ UsersScreen.kt

**DI (1)**:
- ✅ UsersModule.kt

**Config (2)**:
- ✅ build.gradle.kts
- ✅ AndroidManifest.xml

### App Module (11 files)
- ✅ MainApplication.kt
- ✅ MainActivity.kt
- ✅ Theme.kt
- ✅ Color.kt
- ✅ Type.kt
- ✅ build.gradle.kts
- ✅ AndroidManifest.xml
- ✅ strings.xml
- ✅ themes.xml
- ✅ proguard-rules.pro

### Documentation (3 files)
- ✅ README.md
- ✅ ARCHITECTURE.md
- ✅ PROJECT_STRUCTURE.md

### Configuration (3 files)
- ✅ settings.gradle.kts
- ✅ build.gradle.kts (root)
- ✅ gradle.properties

## 📊 Total Files: ~43 files

---

## 🔍 Key Files Explained

### ⭐ Core Module

#### `Resource.kt`
**Purpose**: Wrapper for data operations with three states
```kotlin
Resource.Loading()  // Show loading indicator
Resource.Success(data)  // Show data
Resource.Error(message)  // Show error with retry
```

#### `UseCase.kt`
**Purpose**: Base classes for implementing business logic
- `UseCase<P, R>`: For single operations
- `FlowUseCase<P, R>`: For streaming operations (returns Flow)

#### `NetworkException.kt`
**Purpose**: Custom exceptions for network errors
- NoInternetException
- TimeoutException
- ServerException
- UnknownException

---

### ⭐ CoreUI Module

#### `UiState.kt`
**Purpose**: UI-specific state wrapper
```kotlin
UiState.Idle      // Initial state
UiState.Loading   // Show loading
UiState.Success   // Show data
UiState.Error     // Show error
```

#### `LoadingIndicator.kt`
**Purpose**: Reusable Compose component for loading state
- Centered circular progress indicator
- Fills entire screen

#### `ErrorMessage.kt`
**Purpose**: Reusable Compose component for error state
- Error icon
- Error message
- Retry button (optional)

---

### ⭐ Network Module

#### `ApiService.kt`
**Purpose**: Retrofit interface defining API endpoints
```kotlin
@GET("users")
suspend fun getUsers(): List<UserDto>
```

#### `NetworkClient.kt`
**Purpose**: Retrofit configuration
- Base URL setup
- OkHttp client with logging
- Timeout configuration
- Converter factory (Gson)

#### `RemoteDataSource.kt`
**Purpose**: Handles actual API calls with error handling
- Catches network exceptions
- Converts to custom NetworkException types

#### `UserDto.kt`
**Purpose**: Data Transfer Object for network responses
- Contains @SerializedName annotations
- Represents API response structure

---

### ⭐ Feature:Users Module

#### Domain Layer Files

**`User.kt`** - Pure domain model (no annotations)
**`UserRepository.kt`** - Repository contract
**`GetUsersUseCase.kt`** - Business logic for fetching users
**`GetUserByIdUseCase.kt`** - Business logic for single user

#### Data Layer Files

**`UserRepositoryImpl.kt`** - Repository implementation
- Emits Resource states
- Handles data fetching
- Maps DTOs to domain models

**`UserMapper.kt`** - Extension functions for mapping
```kotlin
fun UserDto.toUser(): User
```

#### Presentation Layer Files

**`UsersViewModel.kt`** - State management
- Observes use case Flow
- Transforms Resource to UiState
- Exposes StateFlow to UI

**`UsersScreen.kt`** - Compose UI
- Observes ViewModel state
- Renders UI based on state
- Handles user interactions

#### DI Layer

**`UsersModule.kt`** - Hilt dependency injection
- Provides ApiService
- Provides RemoteDataSource
- Provides UserRepository

---

### ⭐ App Module

#### `MainApplication.kt`
**Purpose**: Application entry point
- `@HiltAndroidApp` annotation
- Initializes Hilt

#### `MainActivity.kt`
**Purpose**: Main activity
- `@AndroidEntryPoint` annotation
- Sets up Compose
- Displays UsersScreen

#### Theme Files
**`Theme.kt`** - Material 3 theme setup
**`Color.kt`** - Color palette
**`Type.kt`** - Typography definitions

---

## 🎨 Package Naming Convention

```
com.example.modularcomposecleanarchitecture  (app)
com.example.core                             (core)
com.example.coreui                           (coreui)
com.example.network                          (network)
com.example.feature.users                    (feature:users)
```

---

## 🚀 Build Order

When Gradle builds the project, it follows this order:

1. **core** (no dependencies)
2. **network** (depends on core)
3. **coreui** (depends on core)
4. **feature:users** (depends on core, coreui, network)
5. **app** (depends on all modules)

---

## 📝 Gradle Files Summary

### Root `build.gradle.kts`
- Plugin versions
- Common configuration

### `settings.gradle.kts`
- Module includes
- Repository configuration

### Module `build.gradle.kts` Files
Each module has its own:
- Dependencies
- Android/JVM configuration
- Build variants

---

## ✨ What Makes This Structure Clean?

✅ **Separation of Concerns**: Each layer has clear responsibilities
✅ **Modularity**: Features can be developed independently
✅ **Testability**: Easy to mock and test each layer
✅ **Scalability**: Simple to add new features
✅ **Reusability**: Core components shared across features
✅ **Maintainability**: Changes isolated to specific modules
✅ **Dependency Rule**: Inner layers don't depend on outer layers

---

**This structure follows Uncle Bob's Clean Architecture principles! 🎯**
