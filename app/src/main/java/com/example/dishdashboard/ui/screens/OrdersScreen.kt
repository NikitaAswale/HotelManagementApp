package com.example.dishdashboard.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dishdashboard.ui.components.ModernSearchBar
import com.example.dishdashboard.ui.theme.*
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

enum class OrderStatus(val icon: @Composable () -> Unit, val label: String) {
    NEW({ Icon(Icons.Default.FiberNew, "New Orders") }, "New"),
    PREPARING({ Icon(Icons.Default.Restaurant, "Preparing") }, "Preparing"),
    READY({ Icon(Icons.Default.DoneAll, "Ready") }, "Ready"),
    DELIVERED({ Icon(Icons.Default.DeliveryDining, "Delivered") }, "Delivered"),
    CANCELLED({ Icon(Icons.Default.Cancel, "Cancelled") }, "Cancelled")
}

data class OrderItem(
    val name: String,
    val quantity: Int,
    val price: Double
)

data class Order(
    val id: String,
    val tableNumber: Int,
    val items: List<OrderItem>,
    val status: OrderStatus,
    val timestamp: Long,
    val specialInstructions: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(onNavigateBack: () -> Unit) {
    var selectedTab by remember { mutableStateOf(OrderStatus.NEW) }
    var searchQuery by remember { mutableStateOf("") }
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    val mockOrders = remember {
        listOf(
            Order("1", 5, listOf(
                OrderItem("Butter Chicken", 2, 299.99),
                OrderItem("Naan", 4, 49.99)
            ), OrderStatus.NEW, System.currentTimeMillis()),
            Order("2", 3, listOf(
                OrderItem("Paneer Tikka", 1, 249.99),
                OrderItem("Jeera Rice", 2, 129.99),
                OrderItem("Lassi", 2, 79.99)
            ), OrderStatus.PREPARING, System.currentTimeMillis() - 900000),
            Order("3", 7, listOf(
                OrderItem("Dal Makhani", 1, 199.99),
                OrderItem("Biryani", 1, 349.99)
            ), OrderStatus.READY, System.currentTimeMillis() - 1800000),
            Order("4", 1, listOf(
                OrderItem("Veg Pulao", 2, 150.0)
            ), OrderStatus.DELIVERED, System.currentTimeMillis() - 3600000),
            Order("5", 9, listOf(
                OrderItem("Fish Curry", 1, 320.0)
            ), OrderStatus.CANCELLED, System.currentTimeMillis() - 7200000),
        )
    }

    val filteredOrders = remember(searchQuery, selectedTab) {
        mockOrders.filter { order ->
            (order.status == selectedTab) &&
                    (searchQuery.isBlank() ||
                            order.id.contains(searchQuery, ignoreCase = true) ||
                            order.items.any { it.name.contains(searchQuery, ignoreCase = true) })
        }
    }

    Scaffold(
        floatingActionButton = {
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInHorizontally(initialOffsetX = { 200 }) + fadeIn()
            ) {
                FloatingActionButton(
                    onClick = { /* TODO: Add new order */ },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Add, "Add Order")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surfaceVariant
                        ),
                        startY = 0f,
                        endY = 1000f
                    )
                )
        ) {
            // Header
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(initialOffsetY = { -100 }) + fadeIn()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 32.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Orders",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Manage Restaurant Orders",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Search Bar
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInHorizontally(initialOffsetX = { -200 }, animationSpec = tween(500, delayMillis = 100)) + fadeIn(animationSpec = tween(500, delayMillis = 100))
            ) {
                ModernSearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = "Search orders by ID or item...",
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            // Status Tabs
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(initialOffsetY = { 100 }, animationSpec = tween(500, delayMillis = 200)) + fadeIn(animationSpec = tween(500, delayMillis = 200))
            ) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(OrderStatus.values()) { status ->
                        ModernTab(
                            selected = selectedTab == status,
                            onClick = { selectedTab = status },
                            status = status,
                            count = mockOrders.count { it.status == status }
                        )
                    }
                }
            }

            // Orders List
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(500, delayMillis = 300))
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    itemsIndexed(filteredOrders, key = { _, order -> order.id }) { index, order ->
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = slideInHorizontally(
                                initialOffsetX = { 200 },
                                animationSpec = tween(500, delayMillis = 100 * index)
                            ) + fadeIn(animationSpec = tween(500, delayMillis = 100 * index))
                        ) {
                            OrderCard(order)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernTab(
    selected: Boolean,
    onClick: () -> Unit,
    status: OrderStatus,
    count: Int
) {
    val statusColor = getStatusColor(status)
    val backgroundColor by animateColorAsState(
        if (selected) statusColor.copy(alpha = 0.15f)
        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    )
    val contentColor by animateColorAsState(
        if (selected) statusColor
        else MaterialTheme.colorScheme.onSurfaceVariant
    )
    val borderColor by animateColorAsState(
        if (selected) statusColor.copy(alpha = 0.5f)
        else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
    )

    Surface(
        onClick = onClick,
        modifier = Modifier.shadow(
            elevation = if (selected) 8.dp else 2.dp,
            shape = RoundedCornerShape(16.dp),
            spotColor = if (selected) statusColor else Color.Transparent
        ),
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CompositionLocalProvider(LocalContentColor provides contentColor) {
                status.icon()
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = status.label,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                )
                Spacer(modifier = Modifier.width(8.dp))
                Badge(
                    containerColor = contentColor.copy(alpha = 0.2f),
                    contentColor = contentColor
                ) {
                    Text(count.toString())
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderCard(order: Order) {
    var expanded by remember { mutableStateOf(false) }
    val totalAmount = order.items.sumOf { it.price * it.quantity }
    val statusColor = getStatusColor(order.status)

    val infiniteTransition = rememberInfiniteTransition()
    val shimmerBrush = if (order.status == OrderStatus.NEW) {
        val shimmerColors = listOf(
            statusColor.copy(alpha = 0.1f),
            statusColor.copy(alpha = 0.3f),
            statusColor.copy(alpha = 0.1f)
        )
        val translateAnim = infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1000f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Restart
            )
        )
        Brush.linearGradient(
            colors = shimmerColors,
            start = androidx.compose.ui.geometry.Offset.Zero,
            end = androidx.compose.ui.geometry.Offset(x = translateAnim.value, y = translateAnim.value)
        )
    } else {
        SolidColor(Color.Transparent)
    }

    Card(
        onClick = { expanded = !expanded },
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = statusColor.copy(alpha = 0.3f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, statusColor.copy(alpha = 0.3f))
    ) {
        Box(modifier = Modifier.background(shimmerBrush)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Order #${order.id}",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.TableRestaurant,
                                "Table",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Table ${order.tableNumber}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        StatusBadge(status = order.status)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            formatTimestamp(order.timestamp),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                if (expanded) {
                    Column {
                        order.items.forEach { item ->
                            OrderItemRow(item)
                        }
                        if (!order.specialInstructions.isNullOrBlank()) {
                            Divider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                            Row(verticalAlignment = Alignment.Top) {
                                Icon(
                                    Icons.Default.Info,
                                    "Special Instructions",
                                    modifier = Modifier.size(16.dp).padding(top = 2.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    order.specialInstructions,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "${order.items.size} items",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "Total: ${formatCurrency(totalAmount)}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun OrderItemRow(item: OrderItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "${item.quantity} x ${item.name}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            formatCurrency(item.price * item.quantity),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun StatusBadge(status: OrderStatus) {
    val color = getStatusColor(status)
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.1f),
        contentColor = color
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            status.icon()
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                status.label,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

private fun formatCurrency(amount: Double): String {
    return NumberFormat.getCurrencyInstance(Locale("en", "IN")).format(amount)
}

private fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
    return when {
        minutes < 1 -> "just now"
        minutes < 60 -> "$minutes min ago"
        else -> SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(timestamp))
    }
}

fun getStatusColor(status: OrderStatus): Color {
    return when (status) {
        OrderStatus.NEW -> ModernBlue
        OrderStatus.PREPARING -> ModernOrange
        OrderStatus.READY -> ModernGreen
        OrderStatus.DELIVERED -> ModernPurple
        OrderStatus.CANCELLED -> ModernRed
    }
}
