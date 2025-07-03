package com.example.dishdashboard.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dishdashboard.R
import com.example.dishdashboard.ui.components.ModernSearchBar
import com.example.dishdashboard.ui.theme.*
import java.text.NumberFormat
import java.util.*

enum class StaffStatus(val displayName: String, val color: Color) {
    ACTIVE("Active", ReportsColor),
    OFF_DUTY("Off-duty", ModernOrange),
    ON_LEAVE("On Leave", ModernRed),
    TRAINING("Training", ModernBlue)
}

enum class StaffRole(val displayName: String, val icon: ImageVector) {
    CHEF("Chef", Icons.Default.RestaurantMenu),
    SERVER("Server", Icons.Default.RoomService),
    BARTENDER("Bartender", Icons.Default.LocalBar),
    HOST("Host", Icons.Default.People),
    KITCHEN_STAFF("Kitchen Staff", Icons.Default.Kitchen),
    MANAGER("Manager", Icons.Default.SupervisorAccount),
    ALL("All Roles", Icons.Default.Work)
}

data class StaffMember(
    val id: Int,
    val name: String,
    val role: StaffRole,
    val status: StaffStatus,
    val shift: String,
    val phone: String,
    val email: String,
    val joinDate: String,
    val rating: Double
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffScreen(
    onNavigateBack: () -> Unit
) {
    var selectedRole by remember { mutableStateOf(StaffRole.ALL) }
    var showAddStaffDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    // Mock data
    val staffList = remember {
        listOf(
            StaffMember(1, "Rajesh Kumar", StaffRole.CHEF, StaffStatus.ACTIVE, "Morning", "+91 98765 43210", "rajesh@email.com", "2022-01-15", 4.8),
            StaffMember(2, "Priya Sharma", StaffRole.SERVER, StaffStatus.ACTIVE, "Evening", "+91 98765 43211", "priya@email.com", "2022-03-10", 4.5),
            StaffMember(3, "Amit Patel", StaffRole.BARTENDER, StaffStatus.OFF_DUTY, "Night", "+91 98765 43212", "amit@email.com", "2023-05-20", 4.2),
            StaffMember(4, "Neha Gupta", StaffRole.HOST, StaffStatus.ACTIVE, "Morning", "+91 98765 43213", "neha@email.com", "2023-02-18", 4.6),
            StaffMember(5, "Suresh Verma", StaffRole.KITCHEN_STAFF, StaffStatus.ON_LEAVE, "Evening", "+91 98765 43214", "suresh@email.com", "2022-08-01", 4.0),
            StaffMember(6, "Sunita Devi", StaffRole.MANAGER, StaffStatus.ACTIVE, "Day", "+91 98765 43215", "sunita@email.com", "2021-11-25", 4.9),
            StaffMember(7, "Vikram Singh", StaffRole.SERVER, StaffStatus.TRAINING, "Night", "+91 98765 43216", "vikram@email.com", "2024-01-10", 3.8)
        )
    }

    val filteredStaff = remember(searchQuery, selectedRole) {
        staffList.filter { member ->
            val matchesSearch = searchQuery.isEmpty() ||
                    member.name.contains(searchQuery, ignoreCase = true) ||
                    member.email.contains(searchQuery, ignoreCase = true) ||
                    member.phone.contains(searchQuery, ignoreCase = true)

            val matchesRole = selectedRole == StaffRole.ALL || member.role == selectedRole

            matchesSearch && matchesRole
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Staff Management",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /*TODO: Filter*/ }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter")
                    }
                    IconButton(onClick = { showAddStaffDialog = true }) {
                        Icon(Icons.Default.PersonAdd, contentDescription = "Add Staff")
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
                            StaffColor.copy(alpha = 0.1f),
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
                // Summary Cards
                item {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = slideInVertically(
                            initialOffsetY = { -100 },
                            animationSpec = tween(600, easing = EaseOutBounce)
                        ) + fadeIn(animationSpec = tween(600))
                    ) {
                        StaffSummary(staffList)
                    }
                }

                // Search Bar
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
                            placeholder = "Search staff by name, role, email..."
                        )
                    }
                }

                // Role Filters
                item {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = slideInHorizontally(
                            initialOffsetX = { -200 },
                            animationSpec = tween(800, delayMillis = 400)
                        ) + fadeIn(animationSpec = tween(800, delayMillis = 400))
                    ) {
                        RoleFilterChips(
                            selectedRole = selectedRole,
                            onRoleSelected = { selectedRole = it }
                        )
                    }
                }

                // Staff List Header
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${filteredStaff.size} Members Found",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        IconButton(onClick = { /*TODO: Sort*/ }) {
                            Icon(Icons.Default.Sort, contentDescription = "Sort Staff")
                        }
                    }
                }

                // Staff List
                items(filteredStaff) { staff ->
                    StaffMemberCard(staff = staff, modifier = Modifier.padding(horizontal = 16.dp))
                }
            }
        }
    }

    if (showAddStaffDialog) {
        AddStaffDialog(onDismiss = { showAddStaffDialog = false })
    }
}

@Composable
fun StaffSummary(staffList: List<StaffMember>) {
    val totalStaff = staffList.size
    val activeStaff = staffList.count { it.status == StaffStatus.ACTIVE }
    val averageRating = staffList.map { it.rating }.average()

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        item {
            SummaryCard(
                title = "Total Staff",
                value = totalStaff.toString(),
                icon = Icons.Default.Groups,
                color = StaffColor
            )
        }
        item {
            SummaryCard(
                title = "On Duty",
                value = activeStaff.toString(),
                icon = Icons.Default.CheckCircle,
                color = ReportsColor
            )
        }
        item {
            SummaryCard(
                title = "Avg. Rating",
                value = String.format("%.1f", averageRating),
                icon = Icons.Default.Star,
                color = MenuColor
            )
        }
    }
}

@Composable
fun SummaryCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color
) {
    Card(
        modifier = Modifier.width(120.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = color
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleFilterChips(
    selectedRole: StaffRole,
    onRoleSelected: (StaffRole) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(StaffRole.values()) { role ->
            val isSelected = selectedRole == role
            FilterChip(
                selected = isSelected,
                onClick = { onRoleSelected(role) },
                label = { Text(role.displayName) },
                leadingIcon = {
                    Icon(
                        imageVector = role.icon,
                        contentDescription = role.displayName,
                        modifier = Modifier.size(18.dp)
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = StaffColor,
                    selectedLabelColor = Color.White,
                    selectedLeadingIconColor = Color.White
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = if (isSelected) StaffColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    selectedBorderColor = StaffColor
                )
            )
        }
    }
}

@Composable
fun StaffMemberCard(
    staff: StaffMember,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = staff.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = staff.role.displayName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Circle,
                        contentDescription = "Status",
                        tint = staff.status.color,
                        modifier = Modifier.size(8.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = staff.status.displayName,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = staff.status.color,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = MenuColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = staff.rating.toString(),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MenuColor
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = { /*TODO: View Profile*/ },
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, StaffColor.copy(alpha = 0.5f)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        "Profile",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStaffDialog(onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    var shift by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Add New Staff Member",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = role,
                    onValueChange = { role = it },
                    label = { Text("Role") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = shift,
                    onValueChange = { shift = it },
                    label = { Text("Shift") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Add Staff")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
