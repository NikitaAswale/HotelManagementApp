package com.example.dishdashboard.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dishdashboard.ui.theme.*
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

enum class TimeRange(val displayName: String) {
    TODAY("Today"),
    WEEK("This Week"),
    MONTH("This Month"),
    YEAR("This Year")
}

data class RevenueDataPoint(val day: String, val revenue: Float)
data class OrderTypeData(val type: String, val count: Int, val color: Color)
data class PopularItem(val name: String, val orders: Int, val revenue: Double)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    onNavigateBack: () -> Unit
) {
    var selectedTimeRange by remember { mutableStateOf(TimeRange.WEEK) }
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { isVisible = true }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reports & Analytics", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                },
                actions = {
                    IconButton(onClick = { /*TODO: Export*/ }) { Icon(Icons.Default.Share, contentDescription = "Export") }
                    IconButton(onClick = { /*TODO: Calendar*/ }) { Icon(Icons.Default.CalendarToday, contentDescription = "Select Date") }
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
                            ReportsColor.copy(alpha = 0.1f),
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
                contentPadding = PaddingValues(16.dp)
            ) {
                // Time Range Filter
                item {
                    TimeRangeSelector(selectedTimeRange) { selectedTimeRange = it }
                }

                // Key Metrics
                item {
                    KeyMetricsSection(isVisible)
                }

                // Revenue Chart
                item {
                    RevenueChartSection(isVisible)
                }

                // Order Overview
                item {
                    OrderOverviewSection(isVisible)
                }

                // Popular Items
                item {
                    PopularItemsSection(isVisible)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeRangeSelector(selected: TimeRange, onSelect: (TimeRange) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(TimeRange.values()) { range ->
            val isSelected = selected == range
            FilterChip(
                selected = isSelected,
                onClick = { onSelect(range) },
                label = { Text(range.displayName) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = ReportsColor,
                    selectedLabelColor = Color.White
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = if (isSelected) ReportsColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    selectedBorderColor = ReportsColor
                )
            )
        }
    }
}

@Composable
fun KeyMetricsSection(isVisible: Boolean) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { -100 }, animationSpec = tween(600, easing = EaseOutBounce)) + fadeIn(tween(600))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MetricCard("Total Revenue", "₹1,24,458", Icons.Default.TrendingUp, ReportsColor, Modifier.weight(1f))
            MetricCard("Total Orders", "1,876", Icons.Default.ShoppingCart, ModernBlue, Modifier.weight(1f))
            MetricCard("Avg. Rating", "4.6", Icons.Default.Star, MenuColor, Modifier.weight(1f))
        }
    }
}

@Composable
fun MetricCard(title: String, value: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = color, modifier = Modifier.size(24.dp))
            Text(text = value, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold), color = color)
            Text(text = title, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
        }
    }
}

@Composable
fun RevenueChartSection(isVisible: Boolean) {
    val revenueData = listOf(
        RevenueDataPoint("Mon", 12000f),
        RevenueDataPoint("Tue", 18000f),
        RevenueDataPoint("Wed", 15000f),
        RevenueDataPoint("Thu", 22000f),
        RevenueDataPoint("Fri", 25000f),
        RevenueDataPoint("Sat", 32000f),
        RevenueDataPoint("Sun", 28000f)
    )

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInHorizontally(initialOffsetX = { -200 }, animationSpec = tween(800, delayMillis = 200)) + fadeIn(tween(800, 200))
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Revenue Trend", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(16.dp))
                LineChart(data = revenueData, modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp))
            }
        }
    }
}

@Composable
fun LineChart(data: List<RevenueDataPoint>, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val maxValue = data.maxOf { it.revenue }
        val path = Path()

        data.forEachIndexed { i, point ->
            val x = (i.toFloat() / (data.size - 1)) * size.width
            val y = (1 - (point.revenue / maxValue)) * size.height
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }

        drawPath(
            path = path,
            color = ReportsColor,
            style = Stroke(width = 6f)
        )
    }
}

@Composable
fun OrderOverviewSection(isVisible: Boolean) {
    val orderData = listOf(
        OrderTypeData("Dine-in", 1240, ModernGreen),
        OrderTypeData("Takeaway", 480, ModernOrange),
        OrderTypeData("Delivery", 156, ModernRed)
    )

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { 100 }, animationSpec = tween(800, delayMillis = 400)) + fadeIn(tween(800, 400))
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Order Overview", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(16.dp))
                    orderData.forEach { data ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
                            Box(modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(data.color))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("${data.type}: ${data.count} orders", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
                PieChart(data = orderData, modifier = Modifier.size(120.dp))
            }
        }
    }
}

@Composable
fun PieChart(data: List<OrderTypeData>, modifier: Modifier = Modifier) {
    val total = data.sumOf { it.count }.toFloat()
    var startAngle = 0f

    Canvas(modifier = modifier) {
        data.forEach {
            val sweepAngle = (it.count / total) * 360f
            drawArc(
                color = it.color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                size = Size(size.width, size.height)
            )
            startAngle += sweepAngle
        }
    }
}

@Composable
fun PopularItemsSection(isVisible: Boolean) {
    val popularItems = listOf(
        PopularItem("Butter Chicken", 450, 135000.0),
        PopularItem("Paneer Tikka", 320, 80000.0),
        PopularItem("Garlic Naan", 650, 45500.0),
        PopularItem("Masala Dosa", 280, 56000.0),
        PopularItem("Mango Lassi", 550, 66000.0)
    )

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInHorizontally(initialOffsetX = { 200 }, animationSpec = tween(800, delayMillis = 600)) + fadeIn(tween(800, 600))
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Most Popular Items", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(16.dp))
                popularItems.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(item.name, style = MaterialTheme.typography.bodyLarge)
                        Column(horizontalAlignment = Alignment.End) {
                            Text("${item.orders} orders", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                            Text(
                                "₹${NumberFormat.getNumberInstance(Locale("en", "IN")).format(item.revenue)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = ReportsColor
                            )
                        }
                    }
                }
            }
        }
    }
}
