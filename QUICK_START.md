# Quick Start Guide

## 🚀 Getting Started with Modular Compose Clean Architecture

This guide will help you build, run, and understand the project quickly.

---

## 📋 Prerequisites

### Required
- ✅ **Android Studio**: Hedgehog (2023.1.1) or newer
- ✅ **JDK**: Version 17 or higher
- ✅ **Android SDK**: API 34
- ✅ **Gradle**: 8.x (included in wrapper)
- ✅ **Minimum Android Device**: API 24 (Android 7.0)

### Check Your Setup
```bash
# Check Java version
java -version
# Should show: openjdk version "17.x.x" or higher

# Check Android SDK
echo $ANDROID_HOME
# Should show your Android SDK path
```

---

## 🏗️ Project Setup

### Option 1: Open in Android Studio

1. **Open Android Studio**
2. **File → Open**
3. **Navigate to**: `C:\Users\Saad\ModularComposeCleanArchitecture`
4. **Click**: Open
5. **Wait** for Gradle sync to complete

### Option 2: Command Line Build

```bash
# Navigate to project directory
cd C:\Users\Saad\ModularComposeCleanArchitecture

# Make gradlew executable (Linux/Mac)
chmod +x gradlew

# Build the project (Windows)
.\gradlew.bat build

# Build the project (Linux/Mac)
./gradlew build
```

---

## ▶️ Running the App

### Method 1: From Android Studio

1. **Connect** an Android device or start an emulator
2. **Click** the Run button (▶️) or press `Shift + F10`
3. **Select** your device
4. **Wait** for the app to install and launch

### Method 2: Command Line

```bash
# Install debug build
.\gradlew.bat installDebug

# Install and run
.\gradlew.bat installDebug
adb shell am start -n com.example.modularcomposecleanarchitecture/.MainActivity
```

---

## 📱 What You'll See

### App Launch Sequence

1. **Splash/Idle State** (brief)
   - App initializes
   - Hilt dependencies injected

2. **Loading State** (~1-2 seconds)
   - Circular progress indicator appears
   - API call to JSONPlaceholder in progress

3. **Success State** (on success)
   - List of 10 users displayed
   - Each card shows:
     - Name
     - Email
     - Phone
     - Website

4. **Error State** (if network fails)
   - Error icon displayed
   - Error message shown
   - Retry button available

---

## 🔍 Exploring the Code

### Key Entry Points

#### 1. **MainApplication.kt**
```kotlin
// Location: app/src/main/java/.../MainApplication.kt
// Purpose: Initializes Hilt DI

@HiltAndroidApp
class MainApplication : Application()
```

#### 2. **MainActivity.kt**
```kotlin
// Location: app/src/main/java/.../MainActivity.kt
// Purpose: Sets up Compose and displays UsersScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ModularComposeCleanArchitectureTheme {
                UsersScreen()
            }
        }
    }
}
```

#### 3. **UsersScreen.kt**
```kotlin
// Location: feature/users/src/main/java/.../presentation/UsersScreen.kt
// Purpose: UI that observes ViewModel state

@Composable
fun UsersScreen(viewModel: UsersViewModel = hiltViewModel()) {
    val usersState by viewModel.usersState.collectAsState()
    
    when (usersState) {
        is UiState.Loading -> LoadingIndicator()
        is UiState.Success -> UsersList(users)
        is UiState.Error -> ErrorMessage(message, onRetry)
    }
}
```

#### 4. **UsersViewModel.kt**
```kotlin
// Location: feature/users/src/main/java/.../presentation/UsersViewModel.kt
// Purpose: Manages UI state

@HiltViewModel
class UsersViewModel @Inject constructor(
    private val getUsersUseCase: GetUsersUseCase
) : ViewModel() {
    
    private val _usersState = MutableStateFlow<UiState<List<User>>>(UiState.Idle)
    val usersState: StateFlow<UiState<List<User>>> = _usersState.asStateFlow()
    
    init {
        getUsers()
    }
}
```

---

## 🧪 Testing the App

### Test Different States

#### Test Loading State
```kotlin
// Modify: NetworkClient.kt
// Change timeout to very long value
.connectTimeout(60, TimeUnit.SECONDS)

// Result: You'll see loading spinner for longer
```

#### Test Error State
```kotlin
// Modify: NetworkClient.kt
// Change base URL to invalid URL
private const val BASE_URL = "https://invalid-url.com/"

// Result: You'll see error message with retry button
```

#### Test Success State
```kotlin
// Default configuration
private const val BASE_URL = "https://jsonplaceholder.typicode.com/"

// Result: You'll see list of users
```

---

## 🔧 Common Issues & Solutions

### Issue 1: Gradle Sync Failed

**Problem**: "Could not resolve dependencies"

**Solution**:
```bash
# Clean and rebuild
.\gradlew.bat clean
.\gradlew.bat build

# Or in Android Studio:
# Build → Clean Project
# Build → Rebuild Project
```

### Issue 2: Hilt Compilation Error

**Problem**: "Hilt components not generated"

**Solution**:
1. Build → Clean Project
2. Build → Rebuild Project
3. Make sure all modules have proper Hilt setup

### Issue 3: Compose Preview Not Working

**Problem**: Preview shows error

**Solution**:
1. Ensure you're using latest Android Studio
2. Invalidate caches: File → Invalidate Caches → Restart
3. Update Compose compiler version in build.gradle.kts

### Issue 4: Network Request Fails

**Problem**: "Unable to resolve host"

**Solution**:
1. Check internet connection
2. Check emulator network settings
3. Verify API URL in NetworkClient.kt
4. Add INTERNET permission in AndroidManifest.xml (already added)

---

## 🎯 Customization Guide

### Add Your Own API

#### Step 1: Update ApiService
```kotlin
// File: network/src/main/java/.../api/ApiService.kt

interface ApiService {
    @GET("your-endpoint")
    suspend fun getYourData(): List<YourDto>
}
```

#### Step 2: Update NetworkClient
```kotlin
// File: network/src/main/java/.../NetworkClient.kt

private const val BASE_URL = "https://your-api.com/"
```

#### Step 3: Create DTO
```kotlin
// File: network/src/main/java/.../model/YourDto.kt

data class YourDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)
```

#### Step 4: Follow Clean Architecture layers
- Domain: Create model, repository interface, use case
- Data: Implement repository, create mapper
- Presentation: Create ViewModel, create screen

---

## 📊 Module Overview

### What Each Module Does

```
core/
├── Purpose: Core business logic
├── Contains: Resource, UseCase, Exceptions
└── No UI dependencies

coreui/
├── Purpose: Reusable UI components
├── Contains: LoadingIndicator, ErrorMessage, UiState
└── Android + Compose

network/
├── Purpose: Network operations
├── Contains: ApiService, NetworkClient, DTOs
└── Retrofit + OkHttp

feature:users/
├── Purpose: Users feature implementation
├── Contains: Domain, Data, Presentation layers
└── Complete Clean Architecture example

app/
├── Purpose: Main application module
├── Contains: MainActivity, Theme, DI setup
└── Ties all modules together
```

---

## 🚀 Next Steps

### 1. Understand the Flow
- Read `ARCHITECTURE.md` for architecture details
- Read `STATE_MANAGEMENT_GUIDE.md` for state flow
- Read `PROJECT_STRUCTURE.md` for file organization

### 2. Modify the Code
- Change API endpoint
- Add new fields to User model
- Customize UI theme and colors
- Add more screens

### 3. Add New Features
- Create new feature module
- Follow Users module as example
- Implement Clean Architecture layers
- Add to app module

### 4. Learn Testing
- Write unit tests for use cases
- Test ViewModels with fake repositories
- Test UI with Compose testing

---

## 📚 Learning Resources

### Official Documentation
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [Hilt Dependency Injection](https://developer.android.com/training/dependency-injection/hilt-android)
- [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)

### API Used in This Project
- [JSONPlaceholder](https://jsonplaceholder.typicode.com/)
  - Free fake API for testing
  - Endpoints: /users, /posts, /comments, etc.

---

## 🐛 Debugging Tips

### Enable Logging
```kotlin
// Network logs are already enabled in NetworkClient.kt
HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}

// View logs in Logcat
// Filter: Tag = "OkHttp"
```

### Debug State Changes
```kotlin
// Add logging in ViewModel
fun getUsers() {
    getUsersUseCase(Unit).onEach { resource ->
        Log.d("UsersViewModel", "State: $resource")
        // ... transform to UiState
    }.launchIn(viewModelScope)
}
```

### Debug Compose
```kotlin
// Add to Composable
LaunchedEffect(usersState) {
    Log.d("UsersScreen", "Current state: $usersState")
}
```

---

## ✅ Verification Checklist

Before considering setup complete:

- [ ] Project builds without errors
- [ ] App installs on device/emulator
- [ ] Loading state appears briefly
- [ ] Users list displays after loading
- [ ] Can scroll through users
- [ ] User cards show all information
- [ ] Error state appears when network fails
- [ ] Retry button works in error state
- [ ] No crashes or force closes

---

## 🎓 Understanding the Architecture

### Quick Architecture Summary

```
┌─────────────────────────────────────────┐
│         PRESENTATION LAYER              │
│  (ViewModel, Composables, UiState)      │
│         UI Logic & State                │
└──────────────┬──────────────────────────┘
               │ calls
               ▼
┌─────────────────────────────────────────┐
│           DOMAIN LAYER                  │
│  (Use Cases, Repository Interfaces)     │
│         Business Logic                  │
└──────────────┬──────────────────────────┘
               │ calls
               ▼
┌─────────────────────────────────────────┐
│            DATA LAYER                   │
│  (Repository Impl, Data Sources)        │
│         Data Operations                 │
└──────────────┬──────────────────────────┘
               │ calls
               ▼
┌─────────────────────────────────────────┐
│          NETWORK LAYER                  │
│  (API Service, Network Client)          │
│         Remote Data                     │
└─────────────────────────────────────────┘
```

### Data Flow

```
User Action
    ↓
ViewModel calls UseCase
    ↓
UseCase calls Repository
    ↓
Repository calls RemoteDataSource
    ↓
RemoteDataSource calls ApiService
    ↓
API Response
    ↓
Map DTO → Domain Model
    ↓
Emit Resource (Loading/Success/Error)
    ↓
Transform to UiState
    ↓
UI Updates
```

---

## 🎉 You're Ready!

You now have a complete understanding of:
- ✅ How to build and run the project
- ✅ Where key files are located
- ✅ How state management works
- ✅ How to customize the app
- ✅ How to debug issues

**Start exploring and building! Happy coding! 🚀**

---

## 📞 Need Help?

If you encounter issues:
1. Check the documentation files (README, ARCHITECTURE, etc.)
2. Review error messages carefully
3. Check Logcat for detailed logs
4. Verify your setup meets prerequisites
5. Try Clean and Rebuild

---

**This project is ready for production use and learning! 🎯**
