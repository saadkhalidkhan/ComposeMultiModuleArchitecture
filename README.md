# 🏗️ Modular Compose Clean Architecture

A modern Android application demonstrating **Clean Architecture** principles with **multi-module structure** using **Jetpack Compose**, **Hilt**, and **Material 3** design.

## 📱 Features

- **Users Management**: Display and manage user information
- **Modern UI**: Material 3 design with Jetpack Compose
- **Clean Architecture**: Separation of concerns with Domain, Data, and Presentation layers
- **Dependency Injection**: Hilt for efficient dependency management
- **Modular Structure**: Feature-based modules for scalability
- **Error Handling**: Comprehensive error states and retry mechanisms
- **Loading States**: Smooth loading indicators and state management

## 🏛️ Architecture

This project follows **Clean Architecture** principles with a **multi-module** approach:

```
📦 ModularComposeCleanArchitecture
├── 📱 app (Main application module)
├── 🔧 core (Core business logic and utilities)
├── 🎨 coreui (Shared UI components and themes)
├── 🌐 network (Network layer with Retrofit)
└── 🏠 feature
    └── 👥 users (Users feature module)
```

### Module Responsibilities

- **`app`**: Main application entry point, dependency injection setup
- **`core`**: Domain models, use cases, repository interfaces, and utilities
- **`coreui`**: Shared UI components, themes, and common Compose utilities
- **`network`**: Network layer implementation with Retrofit and API services
- **`feature:users`**: Complete users feature with data, domain, and presentation layers

## 🛠️ Tech Stack

### Core Technologies
- **Kotlin** - Modern programming language for Android
- **Jetpack Compose** - Modern declarative UI toolkit
- **Material 3** - Latest Material Design components
- **Clean Architecture** - Separation of concerns and dependency inversion

### Libraries & Frameworks
- **Hilt** - Dependency injection
- **Retrofit** - HTTP client for API calls
- **OkHttp** - HTTP client with logging interceptor
- **Kotlin Coroutines** - Asynchronous programming
- **StateFlow** - Reactive state management
- **Navigation Compose** - Navigation between screens
- **Lifecycle ViewModel** - UI-related data holder

### Development Tools
- **Gradle KTS** - Build configuration
- **Android Gradle Plugin 8.2.2** - Latest build tools
- **Kotlin 1.9.22** - Latest stable Kotlin version
- **Compose Compiler 1.5.10** - Compatible with Kotlin 1.9.22

## 🚀 Getting Started

### Prerequisites
- **Android Studio** Hedgehog or later
- **JDK 17** or later
- **Android SDK** with API 34
- **Git** for version control

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/saadkhalidkhan/ComposeMultiModuleArchitecture.git
   cd ComposeMultiModuleArchitecture
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an existing Android Studio project"
   - Navigate to the cloned directory and select it

3. **Build the project**
   ```bash
   ./gradlew clean build
   ```

4. **Run the application**
   - Connect an Android device or start an emulator
   - Click the "Run" button in Android Studio or use:
   ```bash
   ./gradlew installDebug
   ```

## 📂 Project Structure

```
app/
├── src/main/java/com/example/modularcomposecleanarchitecture/
│   ├── MainActivity.kt                 # Main activity
│   ├── MainApplication.kt             # Application class with Hilt
│   └── ui/theme/                      # App-specific themes
│
core/
├── src/main/java/com/example/core/
│   ├── Resource.kt                    # Resource wrapper for API responses
│   └── UseCase.kt                     # Base use case interface
│
coreui/
├── src/main/java/com/example/coreui/
│   ├── components/                    # Shared UI components
│   ├── state/                         # UI state management
│   └── theme/                         # Shared themes and styling
│
network/
├── src/main/java/com/example/network/
│   ├── ApiService.kt                  # API service definitions
│   ├── NetworkModule.kt               # Network dependency injection
│   └── dto/                           # Data transfer objects
│
feature/users/
├── src/main/java/com/example/feature/users/
│   ├── data/                          # Data layer implementation
│   │   ├── mapper/                    # Data mappers
│   │   └── repository/                # Repository implementation
│   ├── di/                            # Feature-specific DI modules
│   ├── domain/                        # Domain layer
│   │   ├── model/                     # Domain models
│   │   ├── repository/                # Repository interfaces
│   │   └── usecase/                   # Use cases
│   └── presentation/                  # Presentation layer
│       ├── UsersScreen.kt             # Compose UI screen
│       └── UsersViewModel.kt          # ViewModel
```

## 🎨 UI Components

The app features a modern Material 3 design with:

- **Top App Bar** with primary container colors
- **Card-based Layout** for user information
- **Loading States** with circular progress indicators
- **Error States** with retry functionality
- **Responsive Design** that adapts to different screen sizes

## 🔄 State Management

The project uses a robust state management approach:

```kotlin
sealed class UiState<out T> {
    object Idle : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
```

## 🧪 Testing

The project is structured to support comprehensive testing:

- **Unit Tests** for ViewModels and Use Cases
- **Integration Tests** for Repository implementations
- **UI Tests** for Compose screens

## 🔧 Build Configuration

### Gradle Versions
- **Gradle**: 8.5
- **Android Gradle Plugin**: 8.2.2
- **Kotlin**: 1.9.22
- **Compose Compiler**: 1.5.10

### Compatibility
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)
- **Compile SDK**: 34

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

**Saad Khalid Khan**
- GitHub: [@saadkhalidkhan](https://github.com/saadkhalidkhan)
- Repository: [ComposeMultiModuleArchitecture](https://github.com/saadkhalidkhan/ComposeMultiModuleArchitecture)

## 🙏 Acknowledgments

- Android team for Jetpack Compose
- Material Design team for Material 3
- Clean Architecture principles by Robert C. Martin
- The Android development community

---

**Built with ❤️ using Modern Android Development practices**