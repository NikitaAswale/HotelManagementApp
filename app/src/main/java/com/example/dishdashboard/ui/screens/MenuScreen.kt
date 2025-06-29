package com.example.dishdashboard.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.dishdashboard.ui.components.ModernSearchBar
import java.text.NumberFormat
import java.util.*

data class MenuItem(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val category: MenuCategory,
    val isAvailable: Boolean = true,
    val isVegetarian: Boolean = false,
    val isSpicy: Boolean = false,
    val prepTime: Int = 15 // in minutes
)

enum class MenuCategory(val icon: @Composable () -> Unit, val label: String, val color: Color) {
    STARTERS({ Icon(Icons.Default.RestaurantMenu, "Starters") }, "Starters", Color(0xFFFF9F1C)),
    MAIN_COURSE({ Icon(Icons.Default.DinnerDining, "Main Course") }, "Main Course", Color(0xFFE74C3C)),
    DESSERTS({ Icon(Icons.Default.Cake, "Desserts") }, "Desserts", Color(0xFFE84393)),
    BEVERAGES({ Icon(Icons.Default.LocalBar, "Beverages") }, "Beverages", Color(0xFF3498DB)),
    SPECIALS({ Icon(Icons.Default.Star, "Specials") }, "Specials", Color(0xFF2ECC71))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(onNavigateBack: () -> Unit) {
    var selectedCategory by remember { mutableStateOf(MenuCategory.STARTERS) }
    var searchQuery by remember { mutableStateOf("") }
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    val mockMenuItems = remember {
        listOf(
            MenuItem("1", "Paneer Tikka", "Grilled cottage cheese cubes marinated in spiced yogurt.", 299.0, MenuCategory.STARTERS, isVegetarian = true, isSpicy = true),
            MenuItem("2", "Butter Chicken", "Tender chicken in a rich, creamy tomato sauce.", 499.0, MenuCategory.MAIN_COURSE, prepTime = 25),
            MenuItem("3", "Gulab Jamun", "Soft, spongy balls made of milk solids, soaked in sweet syrup.", 149.0, MenuCategory.DESSERTS, isVegetarian = true),
            MenuItem("4", "Masala Dosa", "Crispy rice crepe filled with spiced potato masala.", 199.0, MenuCategory.SPECIALS, isVegetarian = true, isSpicy = true),
            MenuItem("5", "Mango Lassi", "A refreshing yogurt drink blended with sweet mangoes.", 99.0, MenuCategory.BEVERAGES, isVegetarian = true),
            MenuItem("6", "Samosa", "Crispy pastry filled with spiced potatoes and peas.", 80.0, MenuCategory.STARTERS, isVegetarian = true, isSpicy = true),
            MenuItem("7", "Palak Paneer", "Cottage cheese in a smooth, creamy spinach gravy.", 350.0, MenuCategory.MAIN_COURSE, isVegetarian = true),
        )
    }

    val filteredMenuItems = remember(searchQuery, selectedCategory) {
        mockMenuItems.filter { item ->
            item.category == selectedCategory &&
                    (searchQuery.isBlank() || item.name.contains(searchQuery, ignoreCase = true))
        }
    }

    Scaffold(
        floatingActionButton = {
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInHorizontally(initialOffsetX = { 200 }) + fadeIn()
            ) {
                FloatingActionButton(
                    onClick = { /* TODO: Add new menu item */ },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Add, "Add Menu Item")
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
                            "Menu",
                            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            "Explore Our Delicious Cuisine",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = MaterialTheme.colorScheme.onSurfaceVariant)
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
                    placeholder = "Search for a dish...",
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            // Category Tabs
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
                    items(MenuCategory.values()) { category ->
                        CategoryTab(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            category = category
                        )
                    }
                }
            }

            // Menu Items List
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(500, delayMillis = 300))
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    itemsIndexed(filteredMenuItems, key = { _, item -> item.id }) { index, item ->
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = slideInHorizontally(initialOffsetX = { 200 }, animationSpec = tween(500, delayMillis = 100 * index)) + fadeIn(animationSpec = tween(500, delayMillis = 100 * index))
                        ) {
                            MenuItemCard(item)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryTab(
    selected: Boolean,
    onClick: () -> Unit,
    category: MenuCategory
) {
    val backgroundColor by animateColorAsState(
        if (selected) category.color.copy(alpha = 0.15f)
        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    )
    val contentColor by animateColorAsState(
        if (selected) category.color
        else MaterialTheme.colorScheme.onSurfaceVariant
    )
    val borderColor by animateColorAsState(
        if (selected) category.color.copy(alpha = 0.5f)
        else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
    )

    Surface(
        onClick = onClick,
        modifier = Modifier.shadow(
            elevation = if (selected) 8.dp else 2.dp,
            shape = RoundedCornerShape(16.dp),
            spotColor = if (selected) category.color else Color.Transparent
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
                category.icon()
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = category.label,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuItemCard(item: MenuItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = item.category.color.copy(alpha = 0.3f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        item.name,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        item.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    formatCurrency(item.price),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (item.isVegetarian) {
                        DietaryTag("Veg", Color(0xFF2ECC71))
                    }
                    if (item.isSpicy) {
                        DietaryTag("Spicy", Color(0xFFE74C3C))
                    }
                }

                Button(
                    onClick = { /* TODO: Add to order */ },
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Default.AddShoppingCart, "Add to Order", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add")
                }
            }
        }
    }
}

@Composable
fun DietaryTag(label: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.1f),
        contentColor = color,
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

private fun formatCurrency(amount: Double): String {
    return NumberFormat.getCurrencyInstance(Locale("en", "IN")).format(amount)
}
