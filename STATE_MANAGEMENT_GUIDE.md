# State Management Guide

## 🎯 Complete State Management Flow

This guide explains how ERROR, LOADING, and SUCCESS states are managed throughout the application.

---

## 📊 State Flow Visualization

```
USER ACTION
    │
    ▼
┌─────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                        │
│  ┌──────────────────────────────────────────────────────┐   │
│  │ UsersScreen (Composable)                             │   │
│  │  - User clicks screen / Init                         │   │
│  │  - ViewModel.getUsers() called                       │   │
│  └──────────────────┬───────────────────────────────────┘   │
│                     │                                        │
│  ┌──────────────────▼───────────────────────────────────┐   │
│  │ UsersViewModel                                       │   │
│  │  State: MutableStateFlow<UiState<List<User>>>       │   │
│  │                                                      │   │
│  │  1. Idle → Initial state                            │   │
│  │  2. Loading → Show progress                         │   │
│  │  3. Success(data) → Show list                       │   │
│  │  4. Error(message) → Show error + retry             │   │
│  └──────────────────┬───────────────────────────────────┘   │
└─────────────────────┼────────────────────────────────────────┘
                      │
                      ▼ calls UseCase
┌─────────────────────────────────────────────────────────────┐
│                      DOMAIN LAYER                            │
│  ┌──────────────────────────────────────────────────────┐   │
│  │ GetUsersUseCase                                      │   │
│  │  - Executes business logic                           │   │
│  │  - Calls repository                                  │   │
│  │  - Returns Flow<Resource<List<User>>>               │   │
│  └──────────────────┬───────────────────────────────────┘   │
└─────────────────────┼────────────────────────────────────────┘
                      │
                      ▼ calls Repository
┌─────────────────────────────────────────────────────────────┐
│                       DATA LAYER                             │
│  ┌──────────────────────────────────────────────────────┐   │
│  │ UserRepositoryImpl                                   │   │
│  │                                                      │   │
│  │  Step 1: emit(Resource.Loading())                   │   │
│  │          ↓                                           │   │
│  │  Step 2: fetch data from RemoteDataSource           │   │
│  │          ↓                                           │   │
│  │  Step 3: map UserDto → User                         │   │
│  │          ↓                                           │   │
│  │  Step 4: emit(Resource.Success(users))              │   │
│  │          │                                           │   │
│  │          └─ on error → emit(Resource.Error(msg))    │   │
│  └──────────────────┬───────────────────────────────────┘   │
└─────────────────────┼────────────────────────────────────────┘
                      │
                      ▼ calls API
┌─────────────────────────────────────────────────────────────┐
│                     NETWORK LAYER                            │
│  ┌──────────────────────────────────────────────────────┐   │
│  │ RemoteDataSource                                     │   │
│  │  - Makes API call via ApiService                     │   │
│  │  - Handles network exceptions                        │   │
│  │  - Returns List<UserDto>                            │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

---

## 🔄 State Transformation Flow

### Step 1: Initial State (Idle)
```kotlin
// ViewModel
private val _usersState = MutableStateFlow<UiState<List<User>>>(UiState.Idle)

// UI
when (usersState) {
    is UiState.Idle -> {
        // Could show empty state or placeholder
    }
}
```

### Step 2: Loading State
```kotlin
// Repository emits
emit(Resource.Loading())

// ViewModel receives and transforms
is Resource.Loading -> {
    _usersState.value = UiState.Loading
}

// UI receives and displays
is UiState.Loading -> {
    LoadingIndicator()  // Shows circular progress
}
```

### Step 3a: Success State
```kotlin
// Repository emits
emit(Resource.Success(usersList))

// ViewModel receives and transforms
is Resource.Success -> {
    _usersState.value = UiState.Success(resource.data ?: emptyList())
}

// UI receives and displays
is UiState.Success -> {
    val users = (usersState as UiState.Success<List<User>>).data
    UsersList(users = users)
}
```

### Step 3b: Error State
```kotlin
// Repository emits
emit(Resource.Error("Network error occurred"))

// ViewModel receives and transforms
is Resource.Error -> {
    _usersState.value = UiState.Error(
        resource.message ?: "An unexpected error occurred"
    )
}

// UI receives and displays
is UiState.Error -> {
    val message = (usersState as UiState.Error).message
    ErrorMessage(
        message = message,
        onRetry = { viewModel.retry() }
    )
}
```

---

## 💻 Complete Code Example

### 1️⃣ Repository (Data Layer)

```kotlin
class UserRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : UserRepository {
    
    override fun getUsers(): Flow<Resource<List<User>>> = flow {
        try {
            // STATE 1: Emit Loading
            emit(Resource.Loading())
            
            // Fetch data
            val usersDto = remoteDataSource.getUsers()
            
            // Map DTO to Domain
            val users = usersDto.map { it.toUser() }
            
            // STATE 2: Emit Success
            emit(Resource.Success(users))
            
        } catch (e: NetworkException.NoInternetException) {
            // STATE 3: Emit Error (No Internet)
            emit(Resource.Error("No internet connection. Please check your network."))
            
        } catch (e: NetworkException.TimeoutException) {
            // STATE 3: Emit Error (Timeout)
            emit(Resource.Error("Request timed out. Please try again."))
            
        } catch (e: Exception) {
            // STATE 3: Emit Error (Unknown)
            emit(Resource.Error(e.message ?: "An unexpected error occurred"))
        }
    }
}
```

### 2️⃣ ViewModel (Presentation Layer)

```kotlin
@HiltViewModel
class UsersViewModel @Inject constructor(
    private val getUsersUseCase: GetUsersUseCase
) : ViewModel() {
    
    // Internal mutable state
    private val _usersState = MutableStateFlow<UiState<List<User>>>(UiState.Idle)
    
    // Public immutable state
    val usersState: StateFlow<UiState<List<User>>> = _usersState.asStateFlow()
    
    init {
        getUsers()
    }
    
    fun getUsers() {
        getUsersUseCase(Unit)
            .onEach { resource ->
                // Transform Resource → UiState
                when (resource) {
                    is Resource.Loading -> {
                        _usersState.value = UiState.Loading
                        // UI will show LoadingIndicator
                    }
                    
                    is Resource.Success -> {
                        _usersState.value = UiState.Success(
                            resource.data ?: emptyList()
                        )
                        // UI will show UsersList
                    }
                    
                    is Resource.Error -> {
                        _usersState.value = UiState.Error(
                            resource.message ?: "Unknown error"
                        )
                        // UI will show ErrorMessage
                    }
                }
            }
            .launchIn(viewModelScope)
    }
    
    fun retry() {
        getUsers()  // Retry the operation
    }
}
```

### 3️⃣ UI (Composable)

```kotlin
@Composable
fun UsersScreen(
    viewModel: UsersViewModel = hiltViewModel()
) {
    // Collect state as Compose State
    val usersState by viewModel.usersState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Users") })
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            // Render based on state
            when (usersState) {
                // Initial state
                is UiState.Idle -> {
                    // Could show empty state or splash
                    Box(modifier = Modifier.fillMaxSize())
                }
                
                // Loading state
                is UiState.Loading -> {
                    LoadingIndicator()
                    // Shows: Centered CircularProgressIndicator
                }
                
                // Success state
                is UiState.Success -> {
                    val users = (usersState as UiState.Success<List<User>>).data
                    UsersList(users = users)
                    // Shows: LazyColumn with user cards
                }
                
                // Error state
                is UiState.Error -> {
                    val message = (usersState as UiState.Error).message
                    ErrorMessage(
                        message = message,
                        onRetry = { viewModel.retry() }
                    )
                    // Shows: Error icon + message + retry button
                }
            }
        }
    }
}
```

---

## 🎬 Timeline of State Changes

```
Time    Layer           State                   UI Display
─────────────────────────────────────────────────────────────
T0      ViewModel       UiState.Idle           Empty/Splash
                        ↓
T1      Repository      Resource.Loading()     
        ViewModel       → UiState.Loading      Loading Spinner
                        ↓
T2      Network         API Call in progress   
                        ↓
T3a     Repository      Resource.Success()     
        ViewModel       → UiState.Success      List of Users
                        
        OR
                        
T3b     Repository      Resource.Error()       
        ViewModel       → UiState.Error        Error Message
                                               + Retry Button
```

---

## 🛡️ Error Handling Examples

### Example 1: No Internet Connection

```kotlin
// Network Layer
try {
    apiService.getUsers()
} catch (e: IOException) {
    throw NetworkException.NoInternetException()
}

// Repository Layer
catch (e: NetworkException.NoInternetException) {
    emit(Resource.Error("No internet connection"))
}

// ViewModel Layer
is Resource.Error -> {
    _usersState.value = UiState.Error(resource.message)
}

// UI Layer
is UiState.Error -> {
    ErrorMessage(
        message = "No internet connection",
        onRetry = { viewModel.retry() }
    )
}
```

### Example 2: Request Timeout

```kotlin
// Network Layer
try {
    apiService.getUsers()
} catch (e: SocketTimeoutException) {
    throw NetworkException.TimeoutException()
}

// Repository Layer
catch (e: NetworkException.TimeoutException) {
    emit(Resource.Error("Request timed out"))
}

// ViewModel Layer
is Resource.Error -> {
    _usersState.value = UiState.Error(resource.message)
}

// UI Layer
is UiState.Error -> {
    ErrorMessage(
        message = "Request timed out",
        onRetry = { viewModel.retry() }
    )
}
```

### Example 3: Server Error (500)

```kotlin
// Network Layer
if (response.code() == 500) {
    throw NetworkException.ServerException("Server error")
}

// Repository Layer
catch (e: NetworkException.ServerException) {
    emit(Resource.Error("Server error. Please try again later."))
}

// ViewModel Layer
is Resource.Error -> {
    _usersState.value = UiState.Error(resource.message)
}

// UI Layer
is UiState.Error -> {
    ErrorMessage(
        message = "Server error. Please try again later.",
        onRetry = { viewModel.retry() }
    )
}
```

---

## 🔄 Retry Mechanism

### How Retry Works

```kotlin
// 1. User sees error
ErrorMessage(
    message = "Network error",
    onRetry = { viewModel.retry() }  // User clicks retry button
)

// 2. ViewModel retry function
fun retry() {
    getUsers()  // Calls the same function again
}

// 3. State cycle repeats
Idle → Loading → Success/Error
```

### Complete Retry Flow

```
USER CLICKS RETRY
    │
    ▼
viewModel.retry()
    │
    ▼
getUsers() called
    │
    ▼
_usersState.value = UiState.Loading
    │
    ▼
UI shows LoadingIndicator
    │
    ▼
API call attempts again
    │
    ├─ Success → UiState.Success → Show data
    │
    └─ Error → UiState.Error → Show error + retry
```

---

## 🎯 State Management Best Practices

### ✅ DO

```kotlin
// 1. Use sealed classes for states
sealed class UiState<out T> { ... }

// 2. Expose immutable StateFlow
val usersState: StateFlow<UiState<List<User>>> = _usersState.asStateFlow()

// 3. Handle all state cases
when (state) {
    is UiState.Idle -> { }
    is UiState.Loading -> { }
    is UiState.Success -> { }
    is UiState.Error -> { }
}

// 4. Transform states at ViewModel layer
is Resource.Loading -> _usersState.value = UiState.Loading

// 5. Provide retry mechanism
ErrorMessage(onRetry = { viewModel.retry() })
```

### ❌ DON'T

```kotlin
// 1. Don't expose MutableStateFlow
val usersState: MutableStateFlow<UiState<List<User>>>  // ❌

// 2. Don't handle business logic in UI
if (error) {
    retryApiCall()  // ❌ Should be in ViewModel
}

// 3. Don't skip Loading state
emit(Resource.Success(data))  // ❌ Should emit Loading first

// 4. Don't ignore error handling
try {
    apiCall()
} catch (e: Exception) { }  // ❌ Should emit error

// 5. Don't use multiple state variables
val isLoading: Boolean  // ❌
val data: List<User>?   // ❌
val error: String?      // ❌
// Use single UiState instead ✅
```

---

## 📊 State Comparison Table

| State | When Used | UI Display | User Action |
|-------|-----------|------------|-------------|
| **Idle** | Initial state | Empty/Placeholder | Wait for loading |
| **Loading** | During API call | Progress indicator | Wait |
| **Success** | Data fetched | Display data | Interact with data |
| **Error** | API failed | Error message | Click retry |

---

## 🧪 Testing States

### Test Loading State

```kotlin
@Test
fun `when getUsers is called, emits Loading state first`() = runTest {
    // Given
    val viewModel = UsersViewModel(mockUseCase)
    
    // When
    viewModel.getUsers()
    
    // Then
    val state = viewModel.usersState.value
    assertTrue(state is UiState.Loading)
}
```

### Test Success State

```kotlin
@Test
fun `when getUsers succeeds, emits Success state with data`() = runTest {
    // Given
    val mockUsers = listOf(User(1, "John", "john@email.com"))
    coEvery { mockUseCase(Unit) } returns flowOf(Resource.Success(mockUsers))
    
    // When
    val viewModel = UsersViewModel(mockUseCase)
    
    // Then
    val state = viewModel.usersState.value
    assertTrue(state is UiState.Success)
    assertEquals(mockUsers, (state as UiState.Success).data)
}
```

### Test Error State

```kotlin
@Test
fun `when getUsers fails, emits Error state with message`() = runTest {
    // Given
    val errorMessage = "Network error"
    coEvery { mockUseCase(Unit) } returns flowOf(Resource.Error(errorMessage))
    
    // When
    val viewModel = UsersViewModel(mockUseCase)
    
    // Then
    val state = viewModel.usersState.value
    assertTrue(state is UiState.Error)
    assertEquals(errorMessage, (state as UiState.Error).message)
}
```

---

## 🎨 UI Components for Each State

### Loading Component
```kotlin
@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp)
        )
    }
}
```

### Error Component
```kotlin
@Composable
fun ErrorMessage(
    message: String,
    onRetry: (() -> Unit)? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = "Error",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        
        if (onRetry != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}
```

---

## 🚀 Summary

This state management system provides:

✅ **Clear State Transitions**: Idle → Loading → Success/Error
✅ **Type Safety**: Sealed classes prevent invalid states
✅ **Error Handling**: Comprehensive error catching and user feedback
✅ **Retry Mechanism**: Users can retry failed operations
✅ **Separation of Concerns**: Each layer handles its own state
✅ **Testability**: Easy to test each state independently
✅ **Reactive UI**: UI automatically updates with state changes

**This is production-ready state management! 🎉**
