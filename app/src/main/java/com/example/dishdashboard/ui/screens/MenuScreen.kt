package com.example.dishdashboard.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.dishdashboard.ui.components.ModernSearchBar
import java.util.*

data class MenuItem(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val category: MenuCategory,
    val imageUrl: String,
    val isAvailable: Boolean = true,
    val isVegetarian: Boolean = false,
    val isSpicy: Boolean = false,
    val preparationTime: Int = 15 // in minutes
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
    
    // Mock data for testing with Indian dishes
    val mockMenuItems = remember {
        listOf(
            MenuItem(
                "1",
                "Paneer Tikka",
                "Marinated cottage cheese cubes grilled to perfection with Indian spices",
                299.0,
                MenuCategory.STARTERS,
                "",
                isVegetarian = true,
                isSpicy = true
            ),
            MenuItem(
                "2",
                "Butter Chicken",
                "Tender chicken in rich tomato-based curry with butter and cream",
                499.0,
                MenuCategory.MAIN_COURSE,
                "",
                isSpicy = true,
                preparationTime = 25
            ),
            MenuItem(
                "3",
                "Gulab Jamun",
                "Deep-fried milk solids soaked in sugar syrup, served warm",
                149.0,
                MenuCategory.DESSERTS,
                "",
                isVegetarian = true,
                preparationTime = 15
            ),
            MenuItem(
                "4",
                "Masala Dosa",
                "Crispy rice crepe filled with spiced potato mixture",
                199.0,
                MenuCategory.SPECIALS,
                "",
                isVegetarian = true,
                isSpicy = true
            ),
            MenuItem(
                "5",
                "Mango Lassi",
                "Refreshing yogurt-based drink with sweet mango pulp",
                99.0,
                MenuCategory.BEVERAGES,
                "",
                isVegetarian = true
            )
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
                            text = "Menu",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Delicious Indian Cuisine",
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
            placeholder = "Search menu items..."
        )

        // Category Tabs
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(MenuCategory.values()) { category ->
                CategoryTab(
                    selected = selectedCategory == category,
                    onClick = { selectedCategory = category },
                    category = category
                )
            }
        }

        // Menu Items List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(mockMenuItems.filter { it.category == selectedCategory }) { item ->
                MenuItemCard(item)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuItemCard(item: MenuItem) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        onClick = { expanded = !expanded },
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .shadow(
                elevation = if (expanded) 8.dp else 4.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = item.category.color.copy(alpha = 0.25f),
                ambientColor = item.category.color.copy(alpha = 0.1f)
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
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            item.category.color.copy(alpha = 0.1f),
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
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (item.isVegetarian) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = ModernGreen.copy(alpha = 0.1f)
                                ) {
                                    Icon(
                                        Icons.Default.Eco,
                                        "Vegetarian",
                                        modifier = Modifier
                                            .size(16.dp)
                                            .padding(2.dp),
                                        tint = ModernGreen
                                    )
                                }
                            }
                            if (item.isSpicy) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = ModernRed.copy(alpha = 0.1f)
                                ) {
                                    Icon(
                                        Icons.Default.Whatshot,
                                        "Spicy",
                                        modifier = Modifier
                                            .size(16.dp)
                                            .padding(2.dp),
                                        tint = ModernRed
                                    )
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                
                Text(
                    text = "₹${item.price}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                
                Divider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.Timer,
                            contentDescription = "Preparation Time",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${item.preparationTime} mins",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (item.isAvailable) {
                            ModernGreen.copy(alpha = 0.1f)
                        } else {
                            ModernRed.copy(alpha = 0.1f)
                        },
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (item.isAvailable) {
                                ModernGreen.copy(alpha = 0.5f)
                            } else {
                                ModernRed.copy(alpha = 0.5f)
                            }
                        )
                    ) {
                        Text(
                            text = if (item.isAvailable) "Available" else "Unavailable",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.bodySmall,
                            color = if (item.isAvailable) ModernGreen else ModernRed
                        )
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
    Surface(
        onClick = onClick,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .shadow(
                elevation = if (selected) 4.dp else 0.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = category.color.copy(alpha = 0.25f)
            ),
        shape = RoundedCornerShape(16.dp),
        color = if (selected) {
            category.color.copy(alpha = 0.15f)
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        },
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) {
                category.color.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            }
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CompositionLocalProvider(
                LocalContentColor provides if (selected) {
                    category.color
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            ) {
                category.icon()
                Text(
                    text = category.label,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}
