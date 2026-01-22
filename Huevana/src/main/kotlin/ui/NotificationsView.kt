package org.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import domain.User
import kotlinx.coroutines.launch
import org.example.presentation.NotificationsViewModel

@Composable
fun Notifications(
    currentUser: User,
    viewModel: NotificationsViewModel,
    modifier: Modifier = Modifier
) {
    var showDropdown by remember { mutableStateOf(false) }
    var notifications by remember { mutableStateOf(currentUser.notifications) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(currentUser.id) {
        val user = viewModel.getCurrentUser(currentUser.id)
        notifications = user.notifications
    }

    Box(modifier = modifier) {
        IconButton(
            onClick = { showDropdown = !showDropdown }
        ) {
            BadgedBox(
                badge = {
                    if (notifications.isNotEmpty()) {
                        Badge(
                            backgroundColor = Color(0xFFE95D7A)
                        ) {
                            Text(
                                text = if (notifications.size > 9) "9+" else notifications.size.toString(),
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = Color(0xFFF5E6EA),
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        if (showDropdown) {
            NotificationDropdownPanel(
                notifications = notifications,
                onDismiss = { showDropdown = false },
                onClearAll = {
                    scope.launch {
                        viewModel.clearAllNotifications(currentUser.id)
                        notifications = mutableListOf<String>()                    }
                },
                onRemoveNotification = { notification ->
                    scope.launch {
                        viewModel.removeNotification(currentUser.id, notification)
                        val user = viewModel.getCurrentUser(currentUser.id)
                        notifications = user.notifications
                    }
                }
            )
        }
    }
}

@Composable
fun NotificationDropdownPanel(
    notifications: List<String>,
    onDismiss: () -> Unit,
    onClearAll: () -> Unit,
    onRemoveNotification: (String) -> Unit
) {
    Popup(
        alignment = Alignment.TopEnd,
        onDismissRequest = onDismiss,
        properties = PopupProperties(focusable = true)
    ) {
        Card(
            modifier = Modifier
                .width(400.dp)
                .heightIn(max = 500.dp)
                .padding(top = 8.dp, end = 8.dp),
            elevation = 8.dp,
            shape = RoundedCornerShape(12.dp),
            backgroundColor = Color.White
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Notifications",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (notifications.isNotEmpty()) {
                            TextButton(
                                onClick = onClearAll,
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    "Clear All",
                                    color = Color(0xFFE95D7A),
                                    fontSize = 12.sp
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color(0xFF999999),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Divider(color = Color(0xFFE0E0E0))

                if (notifications.isEmpty()) {
                    EmptyNotificationsDropdown()
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 400.dp),
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(notifications) { notification ->
                            NotificationItem(
                                notification = notification,
                                onDismiss = { onRemoveNotification(notification) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationItem(
    notification: String,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        backgroundColor = Color(0xFFF8F9FA),
        elevation = 0.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        color = Color(0xFFE95D7A).copy(alpha = 0.1f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notification",
                    tint = Color(0xFFE95D7A),
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = notification,
                fontSize = 13.sp,
                color = Color(0xFF333333),
                lineHeight = 18.sp,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss",
                    tint = Color(0xFF999999),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun EmptyNotificationsDropdown() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        color = Color(0xFFE0E0E0),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "No notifications",
                    tint = Color(0xFF999999),
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                "No notifications",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF666666)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                "You're all caught up!",
                fontSize = 12.sp,
                color = Color(0xFF999999)
            )
        }
    }
}