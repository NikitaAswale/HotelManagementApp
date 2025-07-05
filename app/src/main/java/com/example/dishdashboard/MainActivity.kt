package com.example.dishdashboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dishdashboard.ui.screens.*
import com.example.dishdashboard.ui.theme.DishDashboardTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DishDashboardTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "login"
                    ) {
                        composable("login") {
                            LoginScreen(
                                onLoginSuccess = {
                                    navController.navigate("dashboard") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("dashboard") {
                            MainDashboardScreen(
                                onNavigateToOrders = { navController.navigate("orders") },
                                onNavigateToMenu = { navController.navigate("menu") },
                                onNavigateToTables = { navController.navigate("tables") },
                                onNavigateToStaff = { navController.navigate("staff") },
                                onNavigateToReports = { navController.navigate("reports") },
                                onNavigateToInventory = { navController.navigate("inventory") },
                                onNavigateToSettings = { navController.navigate("settings") }
                            )
                        }

                        composable("orders") {
                            OrdersScreen(
                                onNavigateBack = { navController.navigateUp() }
                            )
                        }

                        composable("menu") {
                            MenuScreen(
                                onNavigateBack = { navController.navigateUp() }
                            )
                        }

                        composable("tables") {
                            TablesScreen(
                                onNavigateBack = { navController.navigateUp() }
                            )
                        }

                        composable("staff") {
                            StaffScreen(
                                onNavigateBack = { navController.navigateUp() }
                            )
                        }

                        // Placeholder screens
                        composable("reports") {
                            ReportsScreen(
                                onNavigateBack = { navController.navigateUp() }
                            )
                        }
                        
                        composable("inventory") {
                            InventoryScreen(
                                onNavigateBack = { navController.navigateUp() }
                            )
                        }
                        
                        composable("settings") {
                            SettingsScreen(
                                onNavigateBack = { navController.navigateUp() },
                                onLogout = {
                                    navController.navigate("login") {
                                        popUpTo("dashboard") { inclusive = true }
                                    }
                                },
                                onNavigateToPaymentMethods = { navController.navigate("paymentMethods") }
                            )
                        }

                        composable("paymentMethods") {
                            PaymentMethodsScreen(
                                onNavigateBack = { navController.navigateUp() }
                            )
                        }
                    }
                }
            }
        }
    }
}