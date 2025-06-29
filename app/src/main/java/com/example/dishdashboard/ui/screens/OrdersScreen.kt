package com.example.dishdashboard.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.dishdashboard.ui.theme.*
import com.example.dishdashboard.ui.components.ModernSearchBar
import java.text.NumberFormat
import java.util.*

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
            ), OrderStatus.READY, System.currentTimeMillis() - 1800000)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.surfaceVariant
                    ),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
    ) {
        // Title section with seamless gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
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
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Manage Restaurant Orders",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.9f)
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
        }

        // Search Bar
        ModernSearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            placeholder = "Search orders..."
        )

        // Status Tabs
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(OrderStatus.values()) { status ->
                ModernTab(
                    selected = selectedTab == status,
                    onClick = { selectedTab = status },
                    status = status
                )
            }
        }

        // Orders List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(mockOrders.filter { it.status == selectedTab }) { order ->
                OrderCard(order)
            }
        }
    }
}

@Composable
fun ModernTab(
    selected: Boolean,
    onClick: () -> Unit,
    status: OrderStatus
) {
    val statusColor = getStatusColor(status)
    val backgroundColor = if (selected) {
        statusColor.copy(alpha = 0.15f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    }
    
    val contentColor = if (selected) {
        statusColor
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        onClick = onClick,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .defaultMinSize(minWidth = 100.dp)
            .shadow(
                elevation = if (selected) 4.dp else 0.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = if (selected) statusColor.copy(alpha = 0.25f) else Color.Transparent
            ),
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor,
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) statusColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CompositionLocalProvider(LocalContentColor provides contentColor) {
                status.icon()
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = status.label,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                    color = contentColor
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderCard(order: Order) {
    var expanded by remember { mutableStateOf(false) }
    val totalAmount = order.items.sumOf { it.price * it.quantity }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .shadow(
                elevation = if (expanded) 8.dp else 4.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = getStatusColor(order.status).copy(alpha = 0.25f),
                ambientColor = getStatusColor(order.status).copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (expanded) 8.dp else 4.dp,
            pressedElevation = 12.dp,
            focusedElevation = 8.dp,
            hoveredElevation = 6.dp
        ),
        onClick = { expanded = !expanded }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            getStatusColor(order.status).copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Table ${order.tableNumber}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                StatusChip(order.status)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            order.items.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "${item.quantity}x ${item.name}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                    Text(
                        "₹${formatPrice(item.price * item.quantity)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            if (expanded && order.specialInstructions != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            "Special Instructions",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            order.specialInstructions,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Divider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Total",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "₹${formatPrice(totalAmount)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun StatusChip(status: OrderStatus) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = getStatusColor(status).copy(alpha = 0.1f),
        border = BorderStroke(1.dp, getStatusColor(status).copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            status.icon()
            Text(
                status.label,
                style = MaterialTheme.typography.bodySmall,
                color = getStatusColor(status)
            )
        }
    }
}

fun getStatusColor(status: OrderStatus): Color = when (status) {
    OrderStatus.NEW -> ModernBlue
    OrderStatus.PREPARING -> ModernOrange
    OrderStatus.READY -> ModernGreen
    OrderStatus.DELIVERED -> ModernPurple
    OrderStatus.CANCELLED -> ModernRed
}

fun formatPrice(price: Double): String {
    return String.format("%.2f", price)
}
