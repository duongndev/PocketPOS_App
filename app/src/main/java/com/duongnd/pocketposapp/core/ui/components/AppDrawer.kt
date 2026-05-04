package com.duongnd.pocketposapp.core.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.StackedLineChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.duongnd.pocketposapp.core.navigation.Routes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun AppDrawer(
    navController: NavController,
    drawerState: DrawerState,
    scope: CoroutineScope,
    content: @Composable () -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    
    // Ngưỡng Tablet là 600dp
    val isTablet = screenWidth > 600.dp

    if (isTablet) {
        // Chế độ Sidebar cố định cho màn hình lớn
        Row(modifier = Modifier.fillMaxSize()) {
            PermanentNavigationDrawer(
                drawerContent = {
                    PermanentDrawerSheet(
                        modifier = Modifier.width(280.dp),
                        drawerContainerColor = MaterialTheme.colorScheme.surface
                    ) {
                        DrawerContent(navController, drawerState, scope, isPersistent = true)
                    }
                }
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    content()
                }
            }
        }
    } else {
        // Chế độ Modal (vuốt/bấm menu) cho màn hình nhỏ
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(screenWidth * 0.85f) // Không chiếm toàn bộ chiều ngang
                ) {
                    DrawerContent(navController, drawerState, scope, isPersistent = false)
                }
            },
            content = content
        )
    }
}

@Composable
fun ColumnScope.DrawerContent(
    navController: NavController,
    drawerState: DrawerState,
    scope: CoroutineScope,
    isPersistent: Boolean
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Spacer(modifier = Modifier.height(24.dp))
    
    Text(
        "PocketPOS",
        modifier = Modifier.padding(horizontal = 24.dp),
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.ExtraBold,
        color = MaterialTheme.colorScheme.primary
    )
    
    Spacer(modifier = Modifier.height(16.dp))
    HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp), thickness = 0.5.dp)
    Spacer(modifier = Modifier.height(16.dp))

    val menuItems = remember {
        listOf(
            DrawerItemData("Quét mã", Icons.Default.Home, Routes.SCANNER),
            DrawerItemData("Thể loại", Icons.Default.Category, Routes.CATEGORIES),
            DrawerItemData("Sản phẩm", Icons.Default.Checklist, Routes.PRODUCTS),
            DrawerItemData("Thống kê", Icons.Default.StackedLineChart, Routes.STATISTICS),
            DrawerItemData("Cài đặt", Icons.Default.Settings, Routes.SETTINGS)
        )
    }

    menuItems.forEach { item ->
        NavigationDrawerItem(
            label = { Text(item.title, fontWeight = FontWeight.Medium) },
            selected = currentRoute == item.route,
            icon = { Icon(item.icon, contentDescription = null) },
            onClick = {
                if (!isPersistent) {
                    scope.launch { drawerState.close() }
                }
                if (currentRoute != item.route) {
                    navController.navigate(item.route) {
                        popUpTo(Routes.SCANNER) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
            shape = MaterialTheme.shapes.medium
        )
    }

    Spacer(modifier = Modifier.weight(1f))
    
    HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp), thickness = 0.5.dp)
    
    NavigationDrawerItem(
        label = { Text("Đăng xuất", color = MaterialTheme.colorScheme.error) },
        selected = false,
        icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
        onClick = {
            if (!isPersistent) {
                scope.launch { drawerState.close() }
            }
            navController.navigate(Routes.SPLASH) {
                popUpTo(0)
            }
        },
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp),
        shape = MaterialTheme.shapes.medium
    )
}

data class DrawerItemData(
    val title: String, 
    val icon: androidx.compose.ui.graphics.vector.ImageVector, 
    val route: String
)
