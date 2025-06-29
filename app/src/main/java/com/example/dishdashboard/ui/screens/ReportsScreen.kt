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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.util.*

// Custom colors for Indian theme
val IndianOrange = Color(0xFFFF9933)
val IndianGreen = Color(0xFF138808)
val IndianBlue = Color(0xFF000080)
val IndianSaffron = Color(0xFFFF9933)
val IndianSpiceRed = Color(0xFFE41E31)
val IndianTurmericYellow = Color(0xFFFFD700)
val IndianCurryGreen = Color(0xFF7CB342)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    onNavigateBack: () -> Unit
) {
    var selectedTimeRange by remember { mutableStateOf("Today") }
    val timeRanges = listOf("Today", "This Week", "This Month", "This Year")
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Reports & Analytics",
                        style = MaterialTheme.typography.headlineMedium.copy(
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = IndianOrange.copy(alpha = 0.1f)
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Time Range Selector
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(timeRanges) { range ->
                        FilterChip(
                            selected = selectedTimeRange == range,
                            onClick = { selectedTimeRange = range },
                            label = { Text(range) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = IndianGreen,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            // Revenue Overview
            item {
                RevenueCard()
            }

            // Order Statistics
            item {
                OrderStatsCard()
            }

            // Popular Items
            item {
                PopularItemsCard()
            }

            // Staff Performance
            item {
                StaffPerformanceCard()
            }
        }
    }
}

@Composable
fun RevenueCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = IndianSaffron.copy(alpha = 0.15f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Revenue Overview",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = IndianBlue
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                RevenueMetric(
                    title = "Total Revenue",
                    value = "₹1,24,458.90",
                    icon = Icons.Default.AttachMoney,
                    color = IndianGreen
                )
                RevenueMetric(
                    title = "Average Order",
                    value = "₹420.30",
                    icon = Icons.Default.Receipt,
                    color = IndianSpiceRed
                )
            }
        }
    }
}

@Composable
fun RevenueMetric(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = RoundedCornerShape(12.dp),
            color = color.copy(alpha = 0.2f)
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = color
            )
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun OrderStatsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = IndianTurmericYellow.copy(alpha = 0.15f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Order Statistics",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = IndianBlue
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OrderMetric(
                    count = "156",
                    label = "Total Orders",
                    icon = Icons.Default.ShoppingCart,
                    color = IndianSpiceRed
                )
                OrderMetric(
                    count = "142",
                    label = "Completed",
                    icon = Icons.Default.CheckCircle,
                    color = IndianGreen
                )
                OrderMetric(
                    count = "14",
                    label = "Pending",
                    icon = Icons.Default.Pending,
                    color = IndianOrange
                )
            }
        }
    }
}

@Composable
fun OrderMetric(
    count: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = count,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = color
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun PopularItemsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = IndianCurryGreen.copy(alpha = 0.15f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Popular Items",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = IndianBlue
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            PopularItem(
                name = "Butter Chicken",
                orderCount = 45,
                revenue = 13500.00
            )
            PopularItem(
                name = "Paneer Tikka Masala",
                orderCount = 38,
                revenue = 9500.00
            )
            PopularItem(
                name = "Masala Dosa",
                orderCount = 32,
                revenue = 6400.00
            )
            PopularItem(
                name = "Biryani Special",
                orderCount = 30,
                revenue = 9000.00
            )
        }
    }
}

@Composable
fun PopularItem(
    name: String,
    orderCount: Int,
    revenue: Double
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    color = IndianBlue
                )
            )
            Text(
                text = "$orderCount orders",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
        Text(
            text = "₹${String.format("%,.2f", revenue)}",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Medium,
                color = IndianGreen
            )
        )
    }
}

@Composable
fun StaffPerformanceCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = IndianBlue.copy(alpha = 0.15f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Staff Performance",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = IndianBlue
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            StaffPerformanceItem(
                name = "Rajesh Kumar",
                ordersServed = 42,
                rating = 4.8f
            )
            StaffPerformanceItem(
                name = "Priya Sharma",
                ordersServed = 38,
                rating = 4.9f
            )
            StaffPerformanceItem(
                name = "Amit Patel",
                ordersServed = 35,
                rating = 4.7f
            )
        }
    }
}

@Composable
fun StaffPerformanceItem(
    name: String,
    ordersServed: Int,
    rating: Float
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    color = IndianBlue
                )
            )
            Text(
                text = "$ordersServed orders served",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Rating",
                tint = IndianTurmericYellow,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = String.format("%.1f", rating),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    color = IndianBlue
                )
            )
        }
    }
}
