package com.duongnd.pocketposapp.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.StackedLineChart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.duongnd.pocketposapp.core.navigation.Routes
import com.duongnd.pocketposapp.core.ui.theme.PocketPOSAppTheme
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
    val isTablet = screenWidth > 600.dp

    if (isTablet) {
        PermanentNavigationDrawer(
            drawerContent = {
                PermanentDrawerSheet(
                    modifier = Modifier.width(300.dp),
                    drawerContainerColor = Color(0xFFFBFBFB),
                    drawerTonalElevation = 0.dp
                ) {
                    DrawerContent(navController, drawerState, scope, isPersistent = true)
                }
            }
        ) {
            content()
        }
    } else {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(screenWidth * 0.82f),
                    drawerContainerColor = Color.White,
                    drawerShape = RoundedCornerShape(topEnd = 0.dp, bottomEnd = 0.dp),
                    drawerTonalElevation = 0.dp
                ) {
                    DrawerContent(navController, drawerState, scope, isPersistent = false)
                }
            },
            content = content
        )
    }
}

@Composable
fun DrawerContent(
    navController: NavController,
    drawerState: DrawerState,
    scope: CoroutineScope,
    isPersistent: Boolean
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val primaryColor = MaterialTheme.colorScheme.primary

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // --- Modern Profile Header ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(primaryColor, Color(0xFF673AB7))
                    )
                )
                .padding(top = 10.dp, bottom = 32.dp, start = 24.dp, end = 24.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White.copy(alpha = 0.2f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Storefront,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "PocketPOS",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Quản lý bán hàng",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                
                // Quick Stats Mini Card

            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // --- Menu Items ---
        DrawerSectionHeader("TỔNG QUAN")
        
        val mainItems = remember {
            listOf(
                DrawerItemData("Bán hàng", Icons.Default.QrCodeScanner, Routes.SCANNER),
                DrawerItemData("Báo cáo doanh thu", Icons.Default.StackedLineChart, Routes.STATISTICS)
            )
        }
        
        mainItems.forEach { item ->
            ModernDrawerItem(
                item = item,
                isSelected = currentRoute == item.route,
                onClick = {
                    handleNavigation(navController, drawerState, scope, isPersistent, item.route, currentRoute)
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        DrawerSectionHeader("QUẢN LÝ")

        val inventoryItems = remember {
            listOf(
                DrawerItemData("Thể loại sản phẩm", Icons.Default.Category, Routes.CATEGORIES),
                DrawerItemData("Sản phẩm", Icons.Default.Checklist, Routes.PRODUCTS),
                DrawerItemData("Kho hàng", Icons.Default.Inventory, Routes.PRODUCT_VARIANTS_LIST)
            )
        }

        inventoryItems.forEach { item ->
            ModernDrawerItem(
                item = item,
                isSelected = currentRoute == item.route,
                onClick = {
                    handleNavigation(navController, drawerState, scope, isPersistent, item.route, currentRoute)
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        DrawerSectionHeader("CÀI ĐẶT")
        
        ModernDrawerItem(
            item = DrawerItemData("Cài đặt hệ thống", Icons.Default.Settings, Routes.SETTINGS),
            isSelected = currentRoute == Routes.SETTINGS,
            onClick = {
                handleNavigation(navController, drawerState, scope, isPersistent, Routes.SETTINGS, currentRoute)
            }
        )

        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(32.dp))

        // --- Bottom Action ---
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFFFF5F5))
                .clickable {
                    if (!isPersistent) {
                        scope.launch { drawerState.close() }
                    }
                    navController.navigate(Routes.SPLASH) {
                        popUpTo(0)
                    }
                }
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = null,
                    tint = Color(0xFFE53935),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Đăng xuất tài khoản",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE53935)
                    )
                )
            }
        }
    }
}

@Composable
private fun QuickStatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f))
        Text(text = value, style = MaterialTheme.typography.labelLarge, color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun DrawerSectionHeader(title: String) {
    Text(
        text = title,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
        style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.Black,
            color = Color.LightGray,
            letterSpacing = 1.5.sp
        )
    )
}

@Composable
fun ModernDrawerItem(
    item: DrawerItemData,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else Color.Transparent
    val contentColor = if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFF444444)
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                    color = contentColor,
                    fontSize = 15.sp
                )
            )
            
            if (isSelected) {
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
        }
    }
}

private fun handleNavigation(
    navController: NavController,
    drawerState: DrawerState,
    scope: CoroutineScope,
    isPersistent: Boolean,
    targetRoute: String,
    currentRoute: String?
) {
    if (!isPersistent) {
        scope.launch { drawerState.close() }
    }
    if (currentRoute != targetRoute) {
        navController.navigate(targetRoute) {
            popUpTo(Routes.SCANNER) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }
}

data class DrawerItemData(
    val title: String, 
    val icon: ImageVector, 
    val route: String
)

@Preview(showBackground = true)
@Composable
fun AppDrawerPreview() {
    PocketPOSAppTheme {
        val navController = rememberNavController()
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Open)
        val scope = rememberCoroutineScope()
        AppDrawer(
            navController = navController,
            drawerState = drawerState,
            scope = scope
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text("Nội dung chính")
            }
        }
    }
}

