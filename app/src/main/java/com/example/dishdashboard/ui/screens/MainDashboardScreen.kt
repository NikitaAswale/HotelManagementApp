package com.example.dishdashboard.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dishdashboard.ui.components.ModernSearchBar
import com.example.dishdashboard.ui.theme.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

data class QuickStat(
    val title: String,
    val value: String,
    val icon: ImageVector,
    val color: Color,
    val trend: String = ""
)

data class DashboardItem(
    val title: String,
    val icon: ImageVector,
    val backgroundColor: Color,
    val badge: String? = null,
    val onClick: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainDashboardScreen(
    onNavigateToOrders: () -> Unit,
    onNavigateToMenu: () -> Unit,
    onNavigateToTables: () -> Unit,
    onNavigateToInventory: () -> Unit,
    onNavigateToStaff: () -> Unit,
    onNavigateToReports: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var isVisible by remember { mutableStateOf(false) }
    
    // Animation trigger
    LaunchedEffect(Unit) {
        isVisible = true
    }
    
    // Current time and date
    val currentTime = remember {
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    }
    val currentDate = remember {
        SimpleDateFormat("EEE, MMM dd", Locale.getDefault()).format(Date())
    }
    
    // Quick stats data
    val quickStats = remember {
        listOf(
            QuickStat("Today's Orders", "47", Icons.Default.ShoppingCart, Color(0xFF4CAF50), "+12%"),
            QuickStat("Revenue", "₹23,450", Icons.Default.TrendingUp, Color(0xFF2196F3), "+8%"),
            QuickStat("Tables Occupied", "12/18", Icons.Default.TableBar, Color(0xFFFF9800), "67%"),
            QuickStat("Staff Active", "8", Icons.Default.People, Color(0xFF9C27B0), "All")
        )
    }
    
    val dashboardItems = remember {
        listOf(
            DashboardItem("Orders", Icons.Default.ShoppingCart, Color(0xFF4CAF50), "5 New", onNavigateToOrders),
            DashboardItem("Menu", Icons.Default.Restaurant, Color(0xFFFF9800), "", onNavigateToMenu),
            DashboardItem("Tables", Icons.Default.TableBar, Color(0xFF2196F3), "12/18", onNavigateToTables),
            DashboardItem("Inventory", Icons.Default.Inventory2, Color(0xFF9C27B0), "Low Stock", onNavigateToInventory),
            DashboardItem("Staff", Icons.Default.People, Color(0xFF00BCD4), "8 Active", onNavigateToStaff),
            DashboardItem("Reports", Icons.Default.Assessment, Color(0xFF795548), "", onNavigateToReports),
            DashboardItem("Settings", Icons.Default.Settings, Color(0xFF607D8B), "", onNavigateToSettings)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF667eea),
                        Color(0xFF764ba2),
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.surfaceVariant
                    ),
                    startY = 0f,
                    endY = 1200f
                )
            )
            .verticalScroll(rememberScrollState())
    ) {
        // Enhanced Header Section
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(
                initialOffsetY = { -100 },
                animationSpec = tween(800, easing = EaseOutBounce)
            ) + fadeIn(animationSpec = tween(800))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 32.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "🙏 Namaste!",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 18.sp
                            ),
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "DishDashboard",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 36.sp
                            ),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Restaurant Management Excellence",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 16.sp
                            ),
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                    
                    // Time and Date Card
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .alpha(0.9f),
                        color = Color.White.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = currentTime,
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color.White
                            )
                            Text(
                                text = currentDate,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }

        // Search Bar
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInHorizontally(
                initialOffsetX = { -300 },
                animationSpec = tween(1000, delayMillis = 200)
            ) + fadeIn(animationSpec = tween(1000, delayMillis = 200))
        ) {
            ModernSearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholder = "Search dashboard features...",
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )
        }

        // Quick Stats Section
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(
                initialOffsetY = { 100 },
                animationSpec = tween(1000, delayMillis = 400)
            ) + fadeIn(animationSpec = tween(1000, delayMillis = 400))
        ) {
            Column {
                Text(
                    text = "📊 Quick Overview",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
                )
                
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(quickStats) { stat ->
                        QuickStatCard(stat)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Dashboard Grid Title
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(1000, delayMillis = 600))
        ) {
            Text(
                text = "🏠 Management Hub",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )
        }

        // Enhanced Dashboard Grid
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(
                initialOffsetY = { 200 },
                animationSpec = tween(1200, delayMillis = 800)
            ) + fadeIn(animationSpec = tween(1200, delayMillis = 800))
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.height(600.dp)
            ) {
                items(dashboardItems) { item ->
                    EnhancedDashboardCard(item)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun QuickStatCard(stat: QuickStat) {
    var scale by remember { mutableStateOf(1f) }
    
    LaunchedEffect(Unit) {
        while (true) {
            scale = 1.05f
            delay(2000L)
            scale = 1f
            delay(2000L)
        }
    }
    
    Surface(
        modifier = Modifier
            .width(160.dp)
            .scale(scale)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = stat.color.copy(alpha = 0.3f)
            ),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            stat.color.copy(alpha = 0.1f),
                            stat.color.copy(alpha = 0.05f)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = stat.color.copy(alpha = 0.15f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = stat.icon,
                            contentDescription = null,
                            tint = stat.color,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    if (stat.trend.isNotEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF4CAF50).copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = stat.trend,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF4CAF50),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = stat.value,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = stat.title,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedDashboardCard(item: DashboardItem) {
    var isPressed by remember { mutableStateOf(false) }
    var rotation by remember { mutableStateOf(0f) }
    
    LaunchedEffect(Unit) {
        while (true) {
            delay((3000 + (0..2000).random()).toLong())
            rotation += 360f
        }
    }

    Card(
        onClick = { 
            isPressed = true
            item.onClick()
        },
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .scale(if (isPressed) 0.95f else 1f)
            .shadow(
                elevation = if (isPressed) 12.dp else 8.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = item.backgroundColor.copy(alpha = 0.3f)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp,
            pressedElevation = 16.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            item.backgroundColor.copy(alpha = 0.2f),
                            item.backgroundColor.copy(alpha = 0.05f),
                            Color.Transparent
                        ),
                        radius = 200f
                    )
                )
                .padding(20.dp)
        ) {
            // Badge
            if (!item.badge.isNullOrEmpty()) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 8.dp, y = (-8).dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFF5722)
                ) {
                    Text(
                        text = item.badge,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    item.backgroundColor.copy(alpha = 0.2f),
                                    item.backgroundColor.copy(alpha = 0.1f)
                                )
                            ),
                            shape = CircleShape
                        )
                        .border(
                            width = 2.dp,
                            color = item.backgroundColor.copy(alpha = 0.3f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = item.backgroundColor,
                        modifier = Modifier
                            .size(32.dp)
                            .rotate(if (item.title == "Settings") rotation else 0f)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
    
    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(150L)
            isPressed = false
        }
    }
}
