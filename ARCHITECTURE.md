# Architecture Documentation

## 🏛️ Clean Architecture Overview

This project follows Clean Architecture principles with clear separation of concerns across three main layers:

### 1. Domain Layer (Business Logic)
**Location**: `feature/users/domain/`

**Responsibility**: Contains the core business logic, independent of any framework or external dependencies.

**Components**:
- **Models**: Pure Kotlin data classes representing business entities
  ```kotlin
  data class User(
      val id: Int,
      val name: String,
      val email: String,
      val phone: String?,
      val website: String?
  )
  ```

- **Repository Interfaces**: Define contracts for data operations
  ```kotlin
  interface UserRepository {
      fun getUsers(): Flow<Resource<List<User>>>
  }
  ```

- **Use Cases**: Single responsibility business logic operations
  ```kotlin
  class GetUsersUseCase @Inject constructor(
      private val repository: UserRepository
  ) : FlowUseCase<Unit, List<User>>()
  ```

**Key Principles**:
- ✅ No Android dependencies
- ✅ No framework dependencies
- ✅ Pure Kotlin code
- ✅ Testable in isolation

---

### 2. Data Layer (Data Management)
**Location**: `feature/users/data/` & `network/`

**Responsibility**: Implements data operations defined by the domain layer.

**Components**:
- **Repository Implementations**: Concrete implementations of repository interfaces
  ```kotlin
  class UserRepositoryImpl @Inject constructor(
      private val remoteDataSource: RemoteDataSource
  ) : UserRepository {
      override fun getUsers(): Flow<Resource<List<User>>> = flow {
          emit(Resource.Loading())
          try {
              val data = remoteDataSource.getUsers()
              emit(Resource.Success(data.map { it.toUser() }))
          } catch (e: Exception) {
              emit(Resource.Error(e.message))
          }
      }
  }
  ```

- **Data Sources**: Handle actual data fetching (API, Database, etc.)
  ```kotlin
  class RemoteDataSource @Inject constructor(
      private val apiService: ApiService
  ) {
      suspend fun getUsers(): List<UserDto> = apiService.getUsers()
  }
  ```

- **DTOs (Data Transfer Objects)**: Network/database models
  ```kotlin
  data class UserDto(
      @SerializedName("id") val id: Int,
      @SerializedName("name") val name: String
  )
  ```

- **Mappers**: Convert between DTOs and domain models
  ```kotlin
  fun UserDto.toUser(): User = User(id, name, email, phone, website)
  ```

**Key Principles**:
- ✅ Implements domain contracts
- ✅ Handles data sources (API, DB, Cache)
- ✅ Error handling
- ✅ Data transformation

---

### 3. Presentation Layer (UI)
**Location**: `feature/users/presentation/` & `app/`

**Responsibility**: Manages UI state and user interactions.

**Components**:
- **ViewModels**: Manage UI state using StateFlow
  ```kotlin
  @HiltViewModel
  class UsersViewModel @Inject constructor(
      private val getUsersUseCase: GetUsersUseCase
  ) : ViewModel() {
      private val _usersState = MutableStateFlow<UiState<List<User>>>(UiState.Idle)
      val usersState: StateFlow<UiState<List<User>>> = _usersState.asStateFlow()
  }
  ```

- **Composables**: UI components built with Jetpack Compose
  ```kotlin
  @Composable
  fun UsersScreen(viewModel: UsersViewModel = hiltViewModel()) {
      val state by viewModel.usersState.collectAsState()
      when (state) {
          is UiState.Loading → LoadingIndicator()
          is UiState.Success → UsersList(state.data)
          is UiState.Error → ErrorMessage(state.message)
      }
  }
  ```

**Key Principles**:
- ✅ Observes ViewModel state
- ✅ Displays UI based on state
- ✅ Handles user interactions
- ✅ No business logic

---

## 🔄 State Management System

### Two-Layer State System

#### Layer 1: Resource (Data Layer)
**Location**: `core/Resource.kt`

**Purpose**: Wrap data layer operations with loading/success/error states.

```kotlin
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T>(data: T? = null) : Resource<T>(data)
}
```

**Usage in Repository**:
```kotlin
fun getUsers(): Flow<Resource<List<User>>> = flow {
    emit(Resource.Loading())                    // 1. Emit loading
    val data = remoteDataSource.getUsers()       // 2. Fetch data
    emit(Resource.Success(data))                 // 3. Emit success
}
```

**Benefits**:
- ✅ Consistent error handling across data sources
- ✅ Can include cached data during loading
- ✅ Type-safe state representation

---

#### Layer 2: UiState (Presentation Layer)
**Location**: `coreui/state/UiState.kt`

**Purpose**: Represent UI-specific states for screen rendering.

```kotlin
sealed class UiState<out T> {
    object Idle : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
```

**Usage in ViewModel**:
```kotlin
fun getUsers() {
    getUsersUseCase(Unit).onEach { resource ->
        when (resource) {
            is Resource.Loading -> _usersState.value = UiState.Loading
            is Resource.Success -> _usersState.value = UiState.Success(resource.data)
            is Resource.Error -> _usersState.value = UiState.Error(resource.message)
        }
    }.launchIn(viewModelScope)
}
```

**Benefits**:
- ✅ Idle state for initial screen
- ✅ Clear UI state transitions
- ✅ Easy to handle in Composables

---

### Complete State Flow Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                      USER INTERACTION                        │
│                    (Button Click / Init)                     │
└──────────────────────────┬──────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                   PRESENTATION LAYER                         │
│  ViewModel: Call UseCase                                     │
│  _uiState.value = UiState.Loading ◄────┐                   │
└──────────────────────────┬──────────────┴───────────────────┘
                           │                  │
                           ▼                  │
┌─────────────────────────────────────────────┴───────────────┐
│                     DOMAIN LAYER                             │
│  UseCase: Execute business logic                            │
│  ├─ Validation                                              │
│  ├─ Call Repository                                         │
│  └─ Return Flow<Resource<T>>                               │
└──────────────────────────┬──────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                      DATA LAYER                              │
│  Repository: Handle data operations                          │
│  ├─ emit(Resource.Loading())                                │
│  ├─ remoteDataSource.getUsers()                            │
│  ├─ map DTO to Domain model                                │
│  └─ emit(Resource.Success(data))                           │
└──────────────────────────┬──────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                    NETWORK LAYER                             │
│  RemoteDataSource: Make API call                            │
│  ├─ apiService.getUsers()                                   │
│  ├─ Handle exceptions                                       │
│  └─ Return UserDto list                                     │
└──────────────────────────┬──────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                      API RESPONSE                            │
│  ├─ Success: Return data                                    │
│  ├─ Error: Throw exception                                  │
│  └─ Timeout: SocketTimeoutException                         │
└──────────────────────────┬──────────────────────────────────┘
                           │
                           ▼
        ┌──────────────────┴──────────────────┐
        │                                      │
        ▼                                      ▼
┌──────────────────┐              ┌──────────────────┐
│  SUCCESS PATH    │              │   ERROR PATH     │
│  Resource.Success│              │  Resource.Error  │
└────────┬─────────┘              └────────┬─────────┘
         │                                  │
         ▼                                  ▼
┌──────────────────┐              ┌──────────────────┐
│ UiState.Success  │              │  UiState.Error   │
└────────┬─────────┘              └────────┬─────────┘
         │                                  │
         ▼                                  ▼
┌──────────────────┐              ┌──────────────────┐
│  Show UsersList  │              │ Show ErrorMessage│
│  with data       │              │  with Retry      │
└──────────────────┘              └──────────────────┘
```

---

## 🧩 Module Dependencies

```
┌─────────────────────────────────────────────────────────────┐
│                            APP                               │
│  ├─ MainActivity                                            │
│  ├─ Theme & UI Setup                                        │
│  └─ Navigation (if any)                                     │
└──┬──────────────┬──────────────┬──────────────┬────────────┘
   │              │              │              │
   ▼              ▼              ▼              ▼
┌──────┐   ┌──────────┐   ┌──────────┐   ┌─────────────────┐
│ CORE │   │  COREUI  │   │ NETWORK  │   │ FEATURE:USERS   │
└──────┘   └──────────┘   └──────────┘   └─────────────────┘
   ▲              ▲              ▲         ▲        ▲
   │              │              │         │        │
   └──────────────┴──────────────┴─────────┘        │
                  (All modules depend on CORE)      │
                                                     │
   ┌─────────────────────────────────────────────────┘
   │ (Feature depends on CORE, COREUI, NETWORK)
```

### Module Responsibilities

#### `core` Module
- Base classes (UseCase, Resource)
- Network exceptions
- Shared utilities
- **Pure Kotlin (JVM)**

#### `coreui` Module
- Reusable UI components
- UI state wrapper
- Common Composables
- **Android Library with Compose**

#### `network` Module
- API service definitions
- Network client configuration
- Remote data sources
- DTOs
- **Pure Kotlin (JVM)**

#### `feature:users` Module
- Domain layer (models, repos, use cases)
- Data layer (repo impl, mappers)
- Presentation layer (ViewModels, screens)
- **Android Library with Compose**

---

## 🎯 Dependency Injection with Hilt

### Module Setup

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object UsersModule {
    
    @Provides
    @Singleton
    fun provideApiService(): ApiService {
        return NetworkClient.provideApiService()
    }
    
    @Provides
    @Singleton
    fun provideRemoteDataSource(apiService: ApiService): RemoteDataSource {
        return RemoteDataSource(apiService)
    }
    
    @Provides
    @Singleton
    fun provideUserRepository(remoteDataSource: RemoteDataSource): UserRepository {
        return UserRepositoryImpl(remoteDataSource)
    }
}
```

### Injection Points

1. **Application**: `@HiltAndroidApp`
2. **Activity**: `@AndroidEntryPoint`
3. **ViewModel**: `@HiltViewModel` with constructor injection
4. **Composables**: `hiltViewModel()`

---

## 📊 Error Handling Strategy

### Network Exception Hierarchy

```kotlin
sealed class NetworkException(message: String) : Exception(message) {
    class NoInternetException : NetworkException("No internet connection")
    class ServerException(message: String) : NetworkException(message)
    class TimeoutException : NetworkException("Request timeout")
    class UnknownException(message: String) : NetworkException(message)
}
```

### Error Handling Flow

```kotlin
// 1. RemoteDataSource catches specific exceptions
try {
    apiService.getUsers()
} catch (e: SocketTimeoutException) {
    throw NetworkException.TimeoutException()
} catch (e: IOException) {
    throw NetworkException.NoInternetException()
}

// 2. Repository catches and wraps in Resource
try {
    val data = remoteDataSource.getUsers()
    emit(Resource.Success(data))
} catch (e: Exception) {
    emit(Resource.Error(e.message ?: "Unknown error"))
}

// 3. ViewModel transforms to UiState
is Resource.Error -> _uiState.value = UiState.Error(resource.message)

// 4. UI displays error with retry
is UiState.Error -> ErrorMessage(message, onRetry = { viewModel.retry() })
```

---

## 🧪 Testing Strategy

### Unit Tests

**Domain Layer**:
```kotlin
@Test
fun `GetUsersUseCase returns success when repository succeeds`() {
    // Given
    val fakeRepository = FakeUserRepository()
    val useCase = GetUsersUseCase(fakeRepository)
    
    // When
    val result = useCase(Unit).first()
    
    // Then
    assertTrue(result is Resource.Success)
}
```

**ViewModel**:
```kotlin
@Test
fun `ViewModel emits Loading then Success`() {
    // Given
    val fakeUseCase = FakeGetUsersUseCase()
    val viewModel = UsersViewModel(fakeUseCase)
    
    // When
    val states = viewModel.usersState.value
    
    // Then
    assertTrue(states is UiState.Success)
}
```

### Benefits of This Architecture for Testing

- ✅ Each layer can be tested independently
- ✅ Easy to create fake implementations
- ✅ No need to mock complex dependencies
- ✅ Use cases are pure functions
- ✅ ViewModels don't depend on Android framework

---

## 🚀 Adding a New Feature

### Step-by-Step Guide

1. **Create Feature Module**
   ```kotlin
   // In settings.gradle.kts
   include(":feature:newfeature")
   ```

2. **Define Domain Models**
   ```kotlin
   data class Product(val id: Int, val name: String)
   ```

3. **Create Repository Interface**
   ```kotlin
   interface ProductRepository {
       fun getProducts(): Flow<Resource<List<Product>>>
   }
   ```

4. **Implement Use Cases**
   ```kotlin
   class GetProductsUseCase @Inject constructor(
       private val repository: ProductRepository
   ) : FlowUseCase<Unit, List<Product>>()
   ```

5. **Implement Repository**
   ```kotlin
   class ProductRepositoryImpl @Inject constructor(
       private val remoteDataSource: RemoteDataSource
   ) : ProductRepository
   ```

6. **Create ViewModel**
   ```kotlin
   @HiltViewModel
   class ProductsViewModel @Inject constructor(
       private val getProductsUseCase: GetProductsUseCase
   ) : ViewModel()
   ```

7. **Build UI**
   ```kotlin
   @Composable
   fun ProductsScreen(viewModel: ProductsViewModel = hiltViewModel())
   ```

8. **Setup DI**
   ```kotlin
   @Module
   @InstallIn(SingletonComponent::class)
   object ProductsModule
   ```

---

## 💡 Best Practices

### ✅ DO

- Keep domain models pure (no annotations)
- Use interfaces for repositories
- Single responsibility for use cases
- Map DTOs to domain models
- Handle errors at every layer
- Use StateFlow for state management
- Inject dependencies via constructor

### ❌ DON'T

- Put business logic in ViewModels
- Use Android classes in domain layer
- Expose mutable state from ViewModels
- Catch errors without handling them
- Create God classes
- Skip the mapper layer
- Use LiveData (prefer StateFlow)

---

## 🔍 Code Quality

### SOLID Principles Applied

**S - Single Responsibility**
- Each use case has one job
- Repository handles data operations only
- ViewModel manages UI state only

**O - Open/Closed**
- Resource and UiState are sealed classes
- Easy to extend with new states

**L - Liskov Substitution**
- Repository implementations can be swapped
- Use cases work with any repository implementation

**I - Interface Segregation**
- Small, focused interfaces
- Repository interface has specific methods

**D - Dependency Inversion**
- Domain depends on abstractions (interfaces)
- Data layer implements those abstractions
- High-level modules don't depend on low-level modules

---

This architecture provides a **scalable**, **testable**, and **maintainable** foundation for Android apps! 🎉
