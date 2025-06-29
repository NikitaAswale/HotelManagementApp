package com.example.dishdashboard.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.example.dishdashboard.ui.theme.*

data class Table(
    val id: Int,
    val capacity: Int,
    val status: TableStatus = TableStatus.AVAILABLE,
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

enum class TableFilter(val icon: @Composable () -> Unit, val label: String) {
    ALL({ Icon(Icons.Default.ViewModule, "All") }, "All Tables"),
    AVAILABLE({ Icon(Icons.Default.CheckCircle, "Available") }, "Available"),
    OCCUPIED({ Icon(Icons.Default.Person, "Occupied") }, "Occupied"),
    RESERVED({ Icon(Icons.Default.Schedule, "Reserved") }, "Reserved"),
    CLEANING({ Icon(Icons.Default.CleaningServices, "Cleaning") }, "Cleaning")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TablesScreen(onNavigateBack: () -> Unit) {
    var selectedFilter by remember { mutableStateOf(TableFilter.ALL) }
    var searchQuery by remember { mutableStateOf("") }

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
                            text = "Tables",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Manage Restaurant Tables",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.9f)
                        )
                    }
                    Row {
                        IconButton(onClick = { /* TODO: Add filter */ }) {
                            Icon(
                                Icons.Default.FilterList,
                                contentDescription = "Filter",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
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
        }

        // Modern Filter Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(TableFilter.values()) { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { selectedFilter = filter },
                    label = {
                        Text(
                            text = filter.label,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (selectedFilter == filter) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    leadingIcon = filter.icon,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        selectedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        borderWidth = 1.dp
                    )
                )
            }
        }

        // Tables Grid
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 300.dp),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(tables.filter {
                when (selectedFilter) {
                    TableFilter.ALL -> true
                    TableFilter.AVAILABLE -> it.status == TableStatus.AVAILABLE
                    TableFilter.OCCUPIED -> it.status == TableStatus.OCCUPIED
                    TableFilter.RESERVED -> it.status == TableStatus.RESERVED
                    TableFilter.CLEANING -> it.status == TableStatus.CLEANING
                }
            }) { table ->
                TableCard(table)
            }
        }
    }

    // Floating Action Button
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        FloatingActionButton(
            onClick = { /* TODO: Add table */ },
            modifier = Modifier
                .padding(16.dp)
                .shadow(
                    elevation = 6.dp,
                    shape = CircleShape,
                    spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                ),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(Icons.Default.Add, "Add Table")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TableCard(table: Table) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        onClick = { expanded = !expanded },
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .shadow(
                elevation = if (expanded) 8.dp else 4.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = table.status.color.copy(alpha = 0.25f),
                ambientColor = table.status.color.copy(alpha = 0.1f)
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
                            table.status.color.copy(alpha = 0.1f),
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
                // Table Number and Capacity
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Table ${table.id}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.Person,
                            "Capacity",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            "${table.currentOccupancy}/${table.capacity} seats",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                // Status Indicator
                TableStatusChip(table.status)
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                
                Divider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )

                // Additional Information
                when (table.status) {
                    TableStatus.RESERVED -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Person,
                                        "Customer",
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                    Text(
                                        table.customerName ?: "",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Schedule,
                                        "Time",
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                    Text(
                                        table.reservationTime ?: "",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                    )
                                }
                            }

                            // Quick Actions
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                IconButton(
                                    onClick = { /* TODO: Edit reservation */ },
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                                ) {
                                    Icon(
                                        Icons.Default.Edit,
                                        "Edit",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                IconButton(
                                    onClick = { /* TODO: Cancel reservation */ },
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.error.copy(alpha = 0.1f))
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        "Cancel",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                    TableStatus.OCCUPIED -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Current Order: #${1000 + table.id}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )
                            Button(
                                onClick = { /* TODO: View Order */ },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text("View Order")
                            }
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}

@Composable
fun TableStatusChip(status: TableStatus) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = status.color.copy(alpha = 0.1f),
        border = BorderStroke(
            width = 1.dp,
            color = status.color.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CompositionLocalProvider(LocalContentColor provides status.color) {
                status.icon()
                Text(
                    text = status.label,
                    style = MaterialTheme.typography.bodySmall,
                    color = status.color
                )
            }
        }
    }
}

val tables = listOf(
    Table(1, 2, TableStatus.AVAILABLE),
    Table(2, 4, TableStatus.OCCUPIED, 3),
    Table(3, 6, TableStatus.RESERVED, reservationTime = "19:30", customerName = "Rahul Sharma"),
    Table(4, 2, TableStatus.CLEANING),
    Table(5, 8, TableStatus.AVAILABLE),
    Table(6, 4, TableStatus.OCCUPIED, 4),
    Table(7, 2, TableStatus.RESERVED, reservationTime = "20:00", customerName = "Priya Patel"),
    Table(8, 6, TableStatus.AVAILABLE)
)
