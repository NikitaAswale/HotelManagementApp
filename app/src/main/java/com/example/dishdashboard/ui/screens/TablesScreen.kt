package com.example.dishdashboard.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dishdashboard.ui.theme.*

data class Table(
    val id: Int,
    val capacity: Int,
    var status: TableStatus = TableStatus.AVAILABLE,
    val currentOccupancy: Int = 0,
    val reservationTime: String? = null,
    val customerName: String? = null
)

enum class TableStatus(val color: Color, val icon: @Composable () -> Unit, val label: String) {
    AVAILABLE(ModernGreen, { Icon(Icons.Default.CheckCircle, "Available") }, "Available"),
    OCCUPIED(ModernRed, { Icon(Icons.Default.Person, "Occupied") }, "Occupied"),
    RESERVED(ModernOrange, { Icon(Icons.Default.Schedule, "Reserved") }, "Reserved"),
    CLEANING(ModernBlue, { Icon(Icons.Default.CleaningServices, "Cleaning") }, "Cleaning")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TablesScreen(onNavigateBack: () -> Unit) {
    var tables by remember { mutableStateOf(getMockTables()) }
    var selectedFilter by remember { mutableStateOf<TableStatus?>(null) }
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    val filteredTables = remember(selectedFilter, tables) {
        if (selectedFilter == null) {
            tables
        } else {
            tables.filter { it.status == selectedFilter }
        }
    }

    Scaffold(
        floatingActionButton = {
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInHorizontally(initialOffsetX = { 200 }) + fadeIn()
            ) {
                FloatingActionButton(
                    onClick = { /* TODO: Add new table */ },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Add, "Add Table")
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
                Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 32.dp, bottom = 16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Table Management",
                                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                "Oversee your restaurant's floor.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, "Back", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    TableStatusSummary(tables)
                }
            }

            // Filter Chips
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(initialOffsetY = { 100 }, animationSpec = tween(500, delayMillis = 100)) + fadeIn(animationSpec = tween(500, delayMillis = 100))
            ) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedFilter == null,
                            onClick = { selectedFilter = null },
                            label = { Text("All (${tables.size})") },
                            leadingIcon = { Icon(Icons.Default.TableRestaurant, null) }
                        )
                    }
                    items(TableStatus.values()) { status ->
                        FilterChip(
                            selected = selectedFilter == status,
                            onClick = { selectedFilter = status },
                            label = { Text("${status.label} (${tables.count { it.status == status }})") },
                            leadingIcon = { status.icon() },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = status.color.copy(alpha = 0.15f),
                                selectedLabelColor = status.color
                            )
                        )
                    }
                }
            }

            // Tables Grid
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(500, delayMillis = 200))
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(160.dp),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    itemsIndexed(filteredTables, key = { _, table -> table.id }) { index, table ->
                        AnimatedVisibility(
                            visible = true,
                            enter = slideInHorizontally(initialOffsetX = { 200 }, animationSpec = tween(500, delayMillis = 50 * index)) + fadeIn(animationSpec = tween(500, delayMillis = 50 * index))
                        ) {
                            TableCard(table) { newStatus ->
                                val indexToUpdate = tables.indexOfFirst { it.id == table.id }
                                if (indexToUpdate != -1) {
                                    tables = tables.toMutableList().also {
                                        it[indexToUpdate] = table.copy(status = newStatus)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TableStatusSummary(tables: List<Table>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        TableStatus.values().forEach { status ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = tables.count { it.status == status }.toString(),
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = status.color
                )
                Text(
                    text = status.label,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun TableCard(table: Table, onStatusChange: (TableStatus) -> Unit) {
    var showStatusMenu by remember { mutableStateOf(false) }

    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier
            .aspectRatio(1f)
            .clickable { showStatusMenu = true }
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(2.dp, table.status.color.copy(alpha = 0.5f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // Background blur effect
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    table.status.color.copy(alpha = 0.1f),
                                    Color.Transparent
                                ),
                                radius = 150f
                            )
                        )
                )

                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Table",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = table.id.toString(),
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 48.sp,
                            color = table.status.color
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.People,
                            contentDescription = "Capacity",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "${table.capacity} Seats",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Status Badge
        Surface(
            modifier = Modifier.shadow(elevation = 6.dp, shape = RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(2.dp, table.status.color)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                table.status.icon()
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    table.status.label,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = table.status.color
                )
            }
        }

        // Status change dropdown
        DropdownMenu(
            expanded = showStatusMenu,
            onDismissRequest = { showStatusMenu = false }
        ) {
            TableStatus.values().forEach { status ->
                DropdownMenuItem(
                    text = { Text(status.label) },
                    onClick = {
                        onStatusChange(status)
                        showStatusMenu = false
                    },
                    leadingIcon = { status.icon() }
                )
            }
        }
    }
}

fun getMockTables(): List<Table> {
    return List(20) {
        Table(
            id = it + 1,
            capacity = if (it % 3 == 0) 6 else if (it % 2 == 0) 4 else 2,
            status = TableStatus.values().random()
        )
    }
}
