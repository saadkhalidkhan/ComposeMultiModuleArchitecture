package com.example.feature.users.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.coreui.components.ErrorMessage
import com.example.coreui.components.LoadingIndicator
import com.example.coreui.state.UiState
import com.example.feature.users.domain.model.User

/**
 * Users screen composable
 * Presentation layer - UI
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsersScreen(
    viewModel: UsersViewModel = hiltViewModel()
) {
    val usersState by viewModel.usersState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Users") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (usersState) {
                is UiState.Idle -> {
                    UsersIdleState(onLoad = viewModel::getUsers)
                }
                
                is UiState.Loading -> {
                    LoadingIndicator()
                }
                
                is UiState.Success -> {
                    val users = (usersState as UiState.Success<List<User>>).data
                    UsersList(users = users)
                }
                
                is UiState.Error -> {
                    val message = (usersState as UiState.Error).message
                    ErrorMessage(
                        message = message,
                        onRetry = { viewModel.retry() }
                    )
                }
            }
        }
    }
}

@Composable
private fun UsersIdleState(
    onLoad: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Groups,
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No users yet",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Load the list to see users here.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            FilledTonalButton(onClick = onLoad) {
                Text("Load users")
            }
        }
    }
}

/**
 * List of users composable
 */
@Composable
private fun UsersList(
    users: List<User>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(users) { user ->
            UserItem(user = user)
        }
    }
}

/**
 * Single user item composable
 */
@Composable
private fun UserItem(
    user: User,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = user.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = user.email,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            if (!user.phone.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Phone: ${user.phone}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (!user.website.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Website: ${user.website}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
