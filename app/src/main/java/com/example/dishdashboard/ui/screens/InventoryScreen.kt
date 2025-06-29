package com.example.dishdashboard.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import java.text.NumberFormat
import java.util.*

// Custom Indian theme colors
val SpicyRed = Color(0xFFE41E31)
val TurmericYellow = Color(0xFFFFD700)
val CurryGreen = Color(0xFF7CB342)
val MasalaOrange = Color(0xFFFF9933)

data class InventoryItem(
    val id: Int,
    val name: String,
    val category: String,
    val quantity: Int,
    val unit: String,
    val minThreshold: Int,
    val price: Double
)

data class InventoryCategory(
    val name: String,
    val icon: ImageVector,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    onNavigateBack: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf("All") }
    var showAddItemDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    // Mock inventory data
    val inventoryItems = remember {
        listOf(
            InventoryItem(1, "Basmati Rice", "Grains", 50, "kg", 20, 85.0),
            InventoryItem(2, "Tandoori Masala", "Spices", 15, "kg", 5, 450.0),
            InventoryItem(3, "Paneer", "Dairy", 25, "kg", 10, 320.0),
            InventoryItem(4, "Tomatoes", "Vegetables", 30, "kg", 15, 40.0),
            InventoryItem(5, "Chicken", "Meat", 40, "kg", 20, 220.0),
            InventoryItem(6, "Cooking Oil", "Oil", 60, "L", 20, 150.0),
            InventoryItem(7, "Garam Masala", "Spices", 8, "kg", 5, 800.0),
            InventoryItem(8, "Onions", "Vegetables", 45, "kg", 20, 35.0),
            InventoryItem(9, "Butter", "Dairy", 20, "kg", 8, 420.0),
            InventoryItem(10, "Wheat Flour", "Grains", 70, "kg", 30, 45.0)
        )
    }

    val categories = remember {
        listOf(
            InventoryCategory("All", Icons.Default.Apps, TurmericYellow),
            InventoryCategory("Grains", Icons.Default.Grass, CurryGreen),
            InventoryCategory("Spices", Icons.Default.Whatshot, SpicyRed),
            InventoryCategory("Dairy", Icons.Default.LocalDrink, Color(0xFF2196F3)),
            InventoryCategory("Vegetables", Icons.Default.Eco, CurryGreen),
            InventoryCategory("Meat", Icons.Default.Restaurant, SpicyRed),
            InventoryCategory("Oil", Icons.Default.WaterDrop, MasalaOrange)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Inventory",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Navigate back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Implement search */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = { showAddItemDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Item")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Inventory Summary Cards
            item {
                InventorySummary(inventoryItems)
            }

            // Categories
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { category ->
                        CategoryChip(
                            category = category,
                            selected = selectedCategory == category.name,
                            onSelected = { selectedCategory = category.name }
                        )
                    }
                }
            }

            // Low Stock Alerts
            item {
                LowStockAlert(inventoryItems)
            }

            // Inventory List
            item {
                Text(
                    "Inventory Items",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(inventoryItems.filter {
                selectedCategory == "All" || it.category == selectedCategory
            }) { item ->
                InventoryItemCard(item)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun InventorySummary(items: List<InventoryItem>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SummaryCard(
            title = "Total Items",
            value = items.size.toString(),
            icon = Icons.Default.Inventory,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
        )
        SummaryCard(
            title = "Low Stock",
            value = items.count { it.quantity <= it.minThreshold }.toString(),
            icon = Icons.Default.Warning,
            color = SpicyRed,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun SummaryCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = color
                )
            }
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryChip(
    category: InventoryCategory,
    selected: Boolean,
    onSelected: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onSelected,
        label = {
            Text(category.name)
        },
        leadingIcon = {
            Icon(
                imageVector = category.icon,
                contentDescription = category.name,
                tint = if (selected) Color.White else category.color
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = category.color,
            selectedLabelColor = Color.White
        )
    )
}

@Composable
fun LowStockAlert(items: List<InventoryItem>) {
    val lowStockItems = items.filter { it.quantity <= it.minThreshold }
    if (lowStockItems.isNotEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = SpicyRed.copy(alpha = 0.1f)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Low Stock Alert",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = SpicyRed
                    )
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Warning",
                        tint = SpicyRed
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                lowStockItems.take(3).forEach { item ->
                    Text(
                        "${item.name}: ${item.quantity}/${item.minThreshold} ${item.unit}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }
                if (lowStockItems.size > 3) {
                    Text(
                        "And ${lowStockItems.size - 3} more items",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
fun InventoryItemCard(item: InventoryItem) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = item.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "${item.quantity} ${item.unit}",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        color = if (item.quantity <= item.minThreshold) SpicyRed else MaterialTheme.colorScheme.onSurface
                    )
                )
                Text(
                    text = "₹${String.format("%,.2f", item.price)}/${item.unit}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
