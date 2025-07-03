package com.example.dishdashboard.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dishdashboard.ui.components.ModernSearchBar
import com.example.dishdashboard.ui.theme.*
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.util.*

// Custom Indian theme colors
val SpicyRed = Color(0xFFE41E31)
val TurmericYellow = Color(0xFFFFD700)
val CurryGreen = Color(0xFF7CB342)
val MasalaOrange = Color(0xFFFF9933)

// Enhanced data models
data class InventoryItem(
    val id: Int,
    val name: String,
    val category: String,
    val quantity: Int,
    val unit: String,
    val minThreshold: Int,
    val maxCapacity: Int,
    val price: Double,
    val supplier: String,
    val lastUpdated: String,
    val expiryDate: String? = null,
    val barcode: String? = null
)

data class InventoryCategory(
    val name: String,
    val icon: ImageVector,
    val color: Color,
    val count: Int = 0
)

enum class SortOption(val displayName: String) {
    NAME("Name"),
    QUANTITY("Quantity"),
    PRICE("Price"),
    CATEGORY("Category"),
    LAST_UPDATED("Last Updated")
}

enum class FilterOption(val displayName: String) {
    ALL("All Items"),
    LOW_STOCK("Low Stock"),
    OUT_OF_STOCK("Out of Stock"),
    EXPIRING_SOON("Expiring Soon"),
    WELL_STOCKED("Well Stocked")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    onNavigateBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedSort by remember { mutableStateOf(SortOption.NAME) }
    var selectedFilter by remember { mutableStateOf(FilterOption.ALL) }
    var showSortSheet by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }
    var showAddItemDialog by remember { mutableStateOf(false) }
    var isVisible by remember { mutableStateOf(false) }

    // Animation trigger
    LaunchedEffect(Unit) {
        isVisible = true
    }

    // Enhanced mock inventory data
    val inventoryItems = remember {
        listOf(
            InventoryItem(1, "Basmati Rice Premium", "Grains", 45, "kg", 20, 100, 120.0, "Royal Grains Co.", "2 hours ago", "2024-06-15"),
            InventoryItem(2, "Tandoori Masala Blend", "Spices", 8, "kg", 15, 50, 650.0, "Spice Masters", "1 day ago", "2025-12-31"),
            InventoryItem(3, "Fresh Paneer", "Dairy", 12, "kg", 10, 30, 380.0, "Dairy Fresh Ltd", "4 hours ago", "2024-01-25"),
            InventoryItem(4, "Organic Tomatoes", "Vegetables", 35, "kg", 15, 60, 65.0, "Green Valley Farms", "6 hours ago", "2024-01-22"),
            InventoryItem(5, "Free Range Chicken", "Meat", 25, "kg", 20, 80, 280.0, "Farm Fresh Poultry", "3 hours ago", "2024-01-24"),
            InventoryItem(6, "Pure Mustard Oil", "Oil", 40, "L", 20, 100, 180.0, "Golden Oil Mills", "1 day ago", null),
            InventoryItem(7, "Garam Masala Special", "Spices", 3, "kg", 8, 25, 950.0, "Spice Masters", "2 days ago", "2025-08-20"),
            InventoryItem(8, "Red Onions", "Vegetables", 55, "kg", 25, 80, 45.0, "Green Valley Farms", "5 hours ago", "2024-02-10"),
            InventoryItem(9, "Clarified Butter (Ghee)", "Dairy", 18, "kg", 12, 40, 520.0, "Dairy Fresh Ltd", "8 hours ago", "2024-08-15"),
            InventoryItem(10, "Whole Wheat Flour", "Grains", 60, "kg", 30, 150, 65.0, "Royal Grains Co.", "12 hours ago", "2024-05-30"),
            InventoryItem(11, "Cumin Seeds", "Spices", 2, "kg", 5, 20, 1200.0, "Spice Masters", "3 days ago", "2025-10-15"),
            InventoryItem(12, "Fresh Mint Leaves", "Herbs", 5, "kg", 8, 15, 150.0, "Herb Garden Co.", "2 hours ago", "2024-01-20")
        )
    }

    val categories = remember {
        listOf(
            InventoryCategory("All", Icons.Default.Apps, ModernTeal, inventoryItems.size),
            InventoryCategory("Grains", Icons.Default.Grain, InventoryColor, inventoryItems.count { it.category == "Grains" }),
            InventoryCategory("Spices", Icons.Default.Whatshot, OrdersColor, inventoryItems.count { it.category == "Spices" }),
            InventoryCategory("Dairy", Icons.Default.LocalDrink, ModernBlue, inventoryItems.count { it.category == "Dairy" }),
            InventoryCategory("Vegetables", Icons.Default.Eco, ReportsColor, inventoryItems.count { it.category == "Vegetables" }),
            InventoryCategory("Meat", Icons.Default.Restaurant, ModernRed, inventoryItems.count { it.category == "Meat" }),
            InventoryCategory("Oil", Icons.Default.WaterDrop, MenuColor, inventoryItems.count { it.category == "Oil" }),
            InventoryCategory("Herbs", Icons.Default.Spa, ModernGreen, inventoryItems.count { it.category == "Herbs" })
        )
    }

    // Filter and sort logic
    val filteredItems = remember(searchQuery, selectedCategory, selectedFilter, selectedSort) {
        var filtered = inventoryItems

        // Apply search filter
        if (searchQuery.isNotEmpty()) {
            filtered = filtered.filter { 
                it.name.contains(searchQuery, ignoreCase = true) ||
                it.category.contains(searchQuery, ignoreCase = true) ||
                it.supplier.contains(searchQuery, ignoreCase = true)
            }
        }

        // Apply category filter
        if (selectedCategory != "All") {
            filtered = filtered.filter { it.category == selectedCategory }
        }

        // Apply status filter
        filtered = when (selectedFilter) {
            FilterOption.LOW_STOCK -> filtered.filter { it.quantity <= it.minThreshold }
            FilterOption.OUT_OF_STOCK -> filtered.filter { it.quantity == 0 }
            FilterOption.EXPIRING_SOON -> filtered.filter { 
                it.expiryDate != null && it.expiryDate <= "2024-01-25"
            }
            FilterOption.WELL_STOCKED -> filtered.filter { 
                it.quantity > it.minThreshold && it.quantity.toFloat() / it.maxCapacity > 0.5f
            }
            FilterOption.ALL -> filtered
        }

        // Apply sorting
        when (selectedSort) {
            SortOption.NAME -> filtered.sortedBy { it.name }
            SortOption.QUANTITY -> filtered.sortedByDescending { it.quantity }
            SortOption.PRICE -> filtered.sortedByDescending { it.price }
            SortOption.CATEGORY -> filtered.sortedBy { it.category }
            SortOption.LAST_UPDATED -> filtered.sortedBy { it.lastUpdated }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Inventory Management",
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
                    IconButton(onClick = { showFilterSheet = true }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter")
                    }
                    IconButton(onClick = { showSortSheet = true }) {
                        Icon(Icons.Default.Sort, contentDescription = "Sort")
                    }
                    IconButton(onClick = { showAddItemDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Item")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            InventoryColor.copy(alpha = 0.1f),
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
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                // Enhanced Header Section with animations
                item {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = slideInVertically(
                            initialOffsetY = { -100 },
                            animationSpec = tween(600, easing = EaseOutBounce)
                        ) + fadeIn(animationSpec = tween(600))
                    ) {
                        EnhancedInventorySummary(inventoryItems)
                    }
                }

                // Modern Search Bar
                item {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = slideInVertically(
                            initialOffsetY = { -50 },
                            animationSpec = tween(800, delayMillis = 200)
                        ) + fadeIn(animationSpec = tween(800, delayMillis = 200))
                    ) {
                        ModernSearchBar(
                            query = searchQuery,
                            onQueryChange = { searchQuery = it },
                            placeholder = "Search items, categories, suppliers..."
                        )
                    }
                }

                // Enhanced Categories with counts
                item {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = slideInHorizontally(
                            initialOffsetX = { -200 },
                            animationSpec = tween(800, delayMillis = 400)
                        ) + fadeIn(animationSpec = tween(800, delayMillis = 400))
                    ) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(horizontal = 16.dp)
                        ) {
                            items(categories) { category ->
                                EnhancedCategoryChip(
                                    category = category,
                                    selected = selectedCategory == category.name,
                                    onSelected = { selectedCategory = category.name }
                                )
                            }
                        }
                    }
                }

                // Critical Alerts Section
                item {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = slideInVertically(
                            initialOffsetY = { 100 },
                            animationSpec = tween(800, delayMillis = 600)
                        ) + fadeIn(animationSpec = tween(800, delayMillis = 600))
                    ) {
                        CriticalAlertsSection(inventoryItems)
                    }
                }

                // Filter and Sort Status
                item {
                    FilterSortStatus(
                        selectedFilter = selectedFilter,
                        selectedSort = selectedSort,
                        itemCount = filteredItems.size,
                        totalCount = inventoryItems.size
                    )
                }

                // Enhanced Inventory List with animations
                items(filteredItems.chunked(1).withIndex().toList()) { (index, itemList) ->
                    val item = itemList.first()
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = slideInVertically(
                            initialOffsetY = { 200 },
                            animationSpec = tween(600, delayMillis = 800 + (index * 100))
                        ) + fadeIn(animationSpec = tween(600, delayMillis = 800 + (index * 100)))
                    ) {
                        EnhancedInventoryItemCard(
                            item = item,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    }

    // Bottom Sheets
    if (showSortSheet) {
        SortBottomSheet(
            selectedSort = selectedSort,
            onSortSelected = { selectedSort = it },
            onDismiss = { showSortSheet = false }
        )
    }

    if (showFilterSheet) {
        FilterBottomSheet(
            selectedFilter = selectedFilter,
            onFilterSelected = { selectedFilter = it },
            onDismiss = { showFilterSheet = false }
        )
    }
}

@Composable
fun EnhancedInventorySummary(items: List<InventoryItem>) {
    val lowStockCount = items.count { it.quantity <= it.minThreshold }
    val outOfStockCount = items.count { it.quantity == 0 }
    val expiringCount = items.count { it.expiryDate != null && it.expiryDate <= "2024-01-25" }
    val totalValue = items.sumOf { it.quantity * it.price }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        item {
            EnhancedSummaryCard(
                title = "Total Items",
                value = items.size.toString(),
                subtitle = "In Inventory",
                icon = Icons.Default.Inventory,
                color = ModernTeal,
                backgroundColor = ModernTeal.copy(alpha = 0.1f)
            )
        }
        item {
            EnhancedSummaryCard(
                title = "Low Stock",
                value = lowStockCount.toString(),
                subtitle = "Need Reorder",
                icon = Icons.Default.Warning,
                color = OrdersColor,
                backgroundColor = OrdersColor.copy(alpha = 0.1f)
            )
        }
        item {
            EnhancedSummaryCard(
                title = "Total Value",
                value = "₹${NumberFormat.getNumberInstance(Locale("en", "IN")).format(totalValue.toInt())}",
                subtitle = "Inventory Worth",
                icon = Icons.Default.CurrencyRupee,
                color = ReportsColor,
                backgroundColor = ReportsColor.copy(alpha = 0.1f)
            )
        }
        item {
            EnhancedSummaryCard(
                title = "Expiring Soon",
                value = expiringCount.toString(),
                subtitle = "Within 3 Days",
                icon = Icons.Default.Schedule,
                color = MenuColor,
                backgroundColor = MenuColor.copy(alpha = 0.1f)
            )
        }
    }
}

@Composable
fun EnhancedSummaryCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(160.dp)
            .height(120.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1f)
                )
            }
            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    ),
                    color = color
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedCategoryChip(
    category: InventoryCategory,
    selected: Boolean,
    onSelected: () -> Unit
) {
    val animatedScale by animateFloatAsState(
        targetValue = if (selected) 1.05f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    FilterChip(
        selected = selected,
        onClick = onSelected,
        label = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                    )
                )
                if (category.count > 0 && category.name != "All") {
                    Surface(
                        shape = CircleShape,
                        color = if (selected) Color.White.copy(alpha = 0.3f) else category.color.copy(alpha = 0.2f),
                        modifier = Modifier.size(18.dp)
                    ) {
                        Text(
                            text = category.count.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (selected) Color.White else category.color,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.wrapContentSize(Alignment.Center)
                        )
                    }
                }
            }
        },
        leadingIcon = {
            Icon(
                imageVector = category.icon,
                contentDescription = category.name,
                tint = if (selected) Color.White else category.color,
                modifier = Modifier.size(18.dp)
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = category.color,
            selectedLabelColor = Color.White,
            containerColor = MaterialTheme.colorScheme.surface,
            labelColor = category.color
        ),
        elevation = FilterChipDefaults.filterChipElevation(
            elevation = if (selected) 8.dp else 2.dp
        ),
        modifier = Modifier.scale(animatedScale)
    )
}

@Composable
fun CriticalAlertsSection(items: List<InventoryItem>) {
    val criticalItems = items.filter { it.quantity <= it.minThreshold || (it.expiryDate != null && it.expiryDate <= "2024-01-25") }
    
    if (criticalItems.isNotEmpty()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = OrdersColor.copy(alpha = 0.1f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PriorityHigh,
                            contentDescription = "Critical Alert",
                            tint = OrdersColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            "Critical Alerts",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = OrdersColor
                        )
                    }
                    Surface(
                        shape = CircleShape,
                        color = OrdersColor,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Text(
                            text = criticalItems.size.toString(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.wrapContentSize(Alignment.Center)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                criticalItems.take(3).forEach { item ->
                    val alertType = when {
                        item.quantity == 0 -> "OUT OF STOCK"
                        item.quantity <= item.minThreshold -> "LOW STOCK"
                        item.expiryDate != null && item.expiryDate <= "2024-01-25" -> "EXPIRING SOON"
                        else -> "ATTENTION NEEDED"
                    }
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.name,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${item.quantity} ${item.unit} • $alertType",
                                style = MaterialTheme.typography.bodySmall,
                                color = OrdersColor
                            )
                        }
                        IconButton(onClick = { /* TODO: Handle quick action */ }) {
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Handle",
                                tint = OrdersColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
                
                if (criticalItems.size > 3) {
                    TextButton(
                        onClick = { /* TODO: Show all critical items */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("View ${criticalItems.size - 3} more critical items")
                    }
                }
            }
        }
    }
}

@Composable
fun FilterSortStatus(
    selectedFilter: FilterOption,
    selectedSort: SortOption,
    itemCount: Int,
    totalCount: Int
) {
    if (selectedFilter != FilterOption.ALL || selectedSort != SortOption.NAME) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (selectedFilter != FilterOption.ALL) {
                AssistChip(
                    onClick = { },
                    label = { Text(selectedFilter.displayName) },
                    leadingIcon = {
                        Icon(
                            Icons.Default.FilterList,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )
            }
            if (selectedSort != SortOption.NAME) {
                AssistChip(
                    onClick = { },
                    label = { Text("Sort: ${selectedSort.displayName}") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Sort,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "$itemCount of $totalCount items",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun EnhancedInventoryItemCard(
    item: InventoryItem,
    modifier: Modifier = Modifier
) {
    val stockPercentage = (item.quantity.toFloat() / item.maxCapacity).coerceIn(0f, 1f)
    val stockColor = when {
        item.quantity == 0 -> OrdersColor
        item.quantity <= item.minThreshold -> MenuColor
        stockPercentage > 0.7f -> ReportsColor
        else -> ModernBlue
    }
    
    val isExpiringSoon = item.expiryDate != null && item.expiryDate <= "2024-01-25"

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { /* TODO: Handle item click */ },
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isExpiringSoon) MenuColor.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        if (isExpiringSoon) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MenuColor
                            ) {
                                Text(
                                    text = "EXPIRING",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = when(item.category) {
                                "Grains" -> InventoryColor.copy(alpha = 0.2f)
                                "Spices" -> OrdersColor.copy(alpha = 0.2f)
                                "Dairy" -> ModernBlue.copy(alpha = 0.2f)
                                "Vegetables" -> ReportsColor.copy(alpha = 0.2f)
                                "Meat" -> ModernRed.copy(alpha = 0.2f)
                                "Oil" -> MenuColor.copy(alpha = 0.2f)
                                else -> ModernTeal.copy(alpha = 0.2f)
                            }
                        ) {
                            Text(
                                text = item.category,
                                style = MaterialTheme.typography.labelSmall,
                                color = when(item.category) {
                                    "Grains" -> InventoryColor
                                    "Spices" -> OrdersColor
                                    "Dairy" -> ModernBlue
                                    "Vegetables" -> ReportsColor
                                    "Meat" -> ModernRed
                                    "Oil" -> MenuColor
                                    else -> ModernTeal
                                },
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Text(
                            text = "By ${item.supplier}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
                
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "₹${NumberFormat.getNumberInstance(Locale("en", "IN")).format(item.price.toInt())}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = ReportsColor
                    )
                    Text(
                        text = "per ${item.unit}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Stock Progress Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Stock Level",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "${item.quantity}/${item.maxCapacity} ${item.unit}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = stockColor
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    LinearProgressIndicator(
                        progress = stockPercentage,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = stockColor,
                        trackColor = stockColor.copy(alpha = 0.2f)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Min: ${item.minThreshold} ${item.unit}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        Text(
                            text = "Updated ${item.lastUpdated}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            }
            
            // Action Buttons
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { /* TODO: Quick add stock */ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = ReportsColor
                    ),
                    border = BorderStroke(1.dp, ReportsColor.copy(alpha = 0.3f))
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Add Stock")
                }
                
                OutlinedButton(
                    onClick = { /* TODO: Edit item */ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = ModernBlue
                    ),
                    border = BorderStroke(1.dp, ModernBlue.copy(alpha = 0.3f))
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Edit")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortBottomSheet(
    selectedSort: SortOption,
    onSortSelected: (SortOption) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "Sort Items",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            SortOption.values().forEach { option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onSortSelected(option)
                            onDismiss()
                        }
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = option.displayName,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    if (selectedSort == option) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    selectedFilter: FilterOption,
    onFilterSelected: (FilterOption) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "Filter Items",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            FilterOption.values().forEach { option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onFilterSelected(option)
                            onDismiss()
                        }
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = option.displayName,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = when(option) {
                                FilterOption.ALL -> "Show all inventory items"
                                FilterOption.LOW_STOCK -> "Items below minimum threshold"
                                FilterOption.OUT_OF_STOCK -> "Items with zero quantity"
                                FilterOption.EXPIRING_SOON -> "Items expiring within 3 days"
                                FilterOption.WELL_STOCKED -> "Items with adequate stock"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    if (selectedFilter == option) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
