package com.example.dishdashboard.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.dishdashboard.R
import com.example.dishdashboard.ui.theme.*

data class PaymentMethod(
    val id: String,
    val cardType: String,
    val lastFourDigits: String,
    val expiryDate: String,
    val cardHolder: String,
    val isDefault: Boolean,
    val icon: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodsScreen(
    onNavigateBack: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { isVisible = true }

    val paymentMethods = remember {
        listOf(
            PaymentMethod("1", "Visa", "4242", "12/25", "Suresh Kumar", true, R.drawable.ic_launcher_foreground),
            PaymentMethod("2", "Mastercard", "5555", "08/26", "Suresh Kumar", false, R.drawable.ic_launcher_foreground),
            PaymentMethod("3", "Google Pay", "", "N/A", "Suresh Kumar", false, R.drawable.ic_launcher_foreground),
            PaymentMethod("4", "Paytm Wallet", "", "N/A", "Suresh Kumar", false, R.drawable.ic_launcher_foreground)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Payment Methods", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Add new payment method */ },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Payment Method", tint = Color.White)
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            ModernBlue.copy(alpha = 0.1f),
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
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(paymentMethods) { method ->
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = slideInHorizontally(
                            initialOffsetX = { -200 },
                            animationSpec = tween(800, delayMillis = paymentMethods.indexOf(method) * 200)
                        ) + fadeIn(tween(800, paymentMethods.indexOf(method) * 200))
                    ) {
                        PaymentMethodCard(method = method)
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentMethodCard(method: PaymentMethod) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = method.icon),
                        contentDescription = method.cardType,
                        modifier = Modifier.height(32.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = if (method.cardType.contains("Pay")) method.cardType else "${method.cardType} **** ${method.lastFourDigits}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        if (method.expiryDate != "N/A") {
                            Text(
                                text = "Expires ${method.expiryDate}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
                IconButton(onClick = { /* TODO: Show options */ }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Options")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = method.cardHolder,
                    style = MaterialTheme.typography.bodyMedium
                )
                if (method.isDefault) {
                    Text(
                        text = "Default",
                        style = MaterialTheme.typography.bodySmall,
                        color = ReportsColor,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(ReportsColor.copy(alpha = 0.1f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
} 