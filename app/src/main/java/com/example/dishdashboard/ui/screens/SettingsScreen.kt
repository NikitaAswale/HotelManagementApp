package com.example.dishdashboard.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dishdashboard.R
import com.example.dishdashboard.ui.theme.*

// Modern color scheme
val ModernBlue = Color(0xFF2196F3)
val ModernGreen = Color(0xFF4CAF50)
val ModernOrange = Color(0xFFFF9800)
val ModernRed = Color(0xFFE91E63)
val ModernPurple = Color(0xFF9C27B0)
val ModernTeal = Color(0xFF009688)
val DeepSaffron = Color(0xFFFF7722)

data class SettingItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val iconTint: Color,
    val onClick: () -> Unit = {}
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var isVisible by remember { mutableStateOf(false) }
    var isDarkMode by remember { mutableStateOf(false) }
    var areNotificationsEnabled by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) { isVisible = true }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            SettingsColor.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        ),
                        startY = 0f,
                        endY = 1200f
                    )
                )
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Profile Section
                item {
                    AnimatedVisibility(visible = isVisible, enter = slideInVertically(initialOffsetY = { -100 })) {
                        ProfileHeader()
                    }
                }

                // Account Settings
                item {
                    SettingsSection(
                        title = "Account Settings",
                        items = listOf(
                            { SettingItem("Edit Profile", Icons.Default.Person) },
                            { SettingItem("Change Password", Icons.Default.Lock) },
                            { SettingItem("Payment Methods", Icons.Default.CreditCard) }
                        ),
                        isVisible = isVisible,
                        delay = 200
                    )
                }

                // Restaurant Settings
                item {
                    SettingsSection(
                        title = "Restaurant Settings",
                        items = listOf(
                            { SettingItem("Restaurant Profile", Icons.Default.Restaurant) },
                            { SettingItem("Business Hours", Icons.Default.Schedule) },
                            { SettingItem("Taxes & Fees", Icons.Default.Receipt) }
                        ),
                        isVisible = isVisible,
                        delay = 400
                    )
                }

                // Application Settings
                item {
                    SettingsSection(
                        title = "Application Settings",
                        items = listOf(
                            { SwitchSettingItem("Dark Mode", Icons.Default.Brightness4, isDarkMode) { isDarkMode = it } },
                            { SwitchSettingItem("Notifications", Icons.Default.Notifications, areNotificationsEnabled) { areNotificationsEnabled = it } },
                            { SettingItem("Language", Icons.Default.Language) }
                        ),
                        isVisible = isVisible,
                        delay = 600
                    )
                }

                // Support
                item {
                    SettingsSection(
                        title = "Support",
                        items = listOf(
                            { SettingItem("Help Center", Icons.Default.Help) },
                            { SettingItem("Contact Us", Icons.Default.Mail) },
                            { SettingItem("Privacy Policy", Icons.Default.Shield) }
                        ),
                        isVisible = isVisible,
                        delay = 800
                    )
                }

                // Logout Button
                item {
                    AnimatedVisibility(visible = isVisible, enter = fadeIn(animationSpec = tween(1000, delayMillis = 1000))) {
                        Button(
                            onClick = { showLogoutDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ModernRed.copy(alpha = 0.1f),
                                contentColor = ModernRed
                            )
                        ) {
                            Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Logout")
                        }
                    }
                }
            }
        }
    }

    if (showLogoutDialog) {
        LogoutConfirmationDialog(
            onConfirm = onLogout,
            onDismiss = { showLogoutDialog = false }
        )
    }
}

@Composable
fun ProfileHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_background), // Replace with actual profile image
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = "Sunita Devi",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "Restaurant Manager",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    items: List<@Composable () -> Unit>,
    isVisible: Boolean,
    delay: Int
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInHorizontally(initialOffsetX = { -200 }, animationSpec = tween(800, delayMillis = delay)) + fadeIn(tween(800, delay))
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    items.forEach { item ->
                        item()
                    }
                }
            }
        }
    }
}

@Composable
fun SettingItem(title: String, icon: ImageVector, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = title, tint = SettingsColor)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
fun SwitchSettingItem(title: String, icon: ImageVector, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = title, tint = SettingsColor)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = ReportsColor,
                uncheckedThumbColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
    }
}

@Composable
fun LogoutConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirm Logout", fontWeight = FontWeight.Bold) },
        text = { Text("Are you sure you want to log out from your account?") },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = ModernRed)
            ) {
                Text("Logout")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
