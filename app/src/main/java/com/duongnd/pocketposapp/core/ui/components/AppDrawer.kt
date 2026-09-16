package com.duongnd.pocketposapp.core.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
    gesturesEnabled: Boolean = true,
    content: @Composable () -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val isTablet = screenWidth > 600.dp

    if (isTablet) {
        PermanentNavigationDrawer(
            drawerContent = {
                PermanentDrawerSheet(
                    modifier = Modifier.width(310.dp),
                    drawerContainerColor = MaterialTheme.colorScheme.surface,
                    drawerTonalElevation = 1.dp
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
            gesturesEnabled = gesturesEnabled,
            drawerContent = {
                ModalDrawerSheet(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(screenWidth * 0.82f),
                    drawerContainerColor = MaterialTheme.colorScheme.surface,
                    drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp),
                    drawerTonalElevation = 2.dp
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
    var showLogoutDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .verticalScroll(rememberScrollState())
    ) {
        // --- Modern Brand Header ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            primaryColor,
                            primaryColor.copy(alpha = 0.85f),
                            Color(0xFF512DA8)
                        )
                    )
                )
                .statusBarsPadding()
                .padding(top = 16.dp, bottom = 24.dp, start = 20.dp, end = 20.dp)
        ) {
            // Background Decorative Element
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 30.dp, y = (-20).dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.08f))
            )

            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            handleNavigation(
                                navController,
                                drawerState,
                                scope,
                                isPersistent,
                                Routes.STORE_INFO,
                                currentRoute
                            )
                        }
                        .padding(4.dp)
                ) {
                    // Store Avatar with active status indicator
                    Box {
                        Surface(
                            modifier = Modifier.size(54.dp),
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White.copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.4f))
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Storefront,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                        // Active status badge
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF4CAF50))
                                .border(2.dp, Color.White, CircleShape)
                                .align(Alignment.BottomEnd)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "PocketPOS",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 18.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Cửa hàng đang hoạt động",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 12.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Store Details",
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- Navigation Sections ---
        
        // SECTION 1: MAIN OPERATIONS
        DrawerSectionHeader("TỔNG QUAN")

        val mainItems = remember {
            listOf(
                DrawerItemData("Bán hàng (POS)", Icons.Default.QrCodeScanner, Routes.SCANNER, badge = "POS"),
                DrawerItemData("Doanh thu", Icons.Default.StackedLineChart, Routes.STATISTICS)
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

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)
        )

        // SECTION 2: INVENTORY & MANAGEMENT
        DrawerSectionHeader("QUẢN LÝ")

        val inventoryItems = remember {
            listOf(
                DrawerItemData("Đơn hàng", Icons.Default.Checklist, Routes.ORDERS),
                DrawerItemData("Sản phẩm", Icons.Default.Inventory, Routes.PRODUCTS),
                DrawerItemData("Thể loại", Icons.Default.Category, Routes.CATEGORIES)
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

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)
        )

        // SECTION 3: STORE & ACCOUNT
        DrawerSectionHeader("TÀI KHOẢN & CÀI ĐẶT")

        val settingItems = remember {
            listOf(
                DrawerItemData("Cửa hàng", Icons.Default.Store, Routes.STORE_INFO),
                DrawerItemData("Tài khoản", Icons.Default.Person, Routes.PROFILE),
                DrawerItemData("Cài đặt chung", Icons.Default.Settings, Routes.SETTINGS)
            )
        }

        settingItems.forEach { item ->
            ModernDrawerItem(
                item = item,
                isSelected = currentRoute == item.route,
                onClick = {
                    handleNavigation(navController, drawerState, scope, isPersistent, item.route, currentRoute)
                }
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(24.dp))

        // --- Bottom Logout Button & App Version ---
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 20.dp)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { showLogoutDialog = true },
                color = Color(0xFFFFF0F0),
                tonalElevation = 0.dp
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 16.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = null,
                        tint = Color(0xFFD32F2F),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Đăng xuất",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD32F2F)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "PocketPOS v1.0.0",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    fontSize = 11.sp
                ),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }

    // --- Logout Confirmation Dialog ---
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = {
                Text(
                    text = "Xác nhận đăng xuất",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Bạn có chắc chắn muốn đăng xuất khỏi ứng dụng không?",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        if (!isPersistent) {
                            scope.launch { drawerState.close() }
                        }
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                ) {
                    Text(
                        "Đăng xuất",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }
}

@Composable
fun DrawerSectionHeader(title: String) {
    Text(
        text = title,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
        style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            letterSpacing = 1.2.sp
        )
    )
}

@Composable
fun ModernDrawerItem(
    item: DrawerItemData,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) primaryColor.copy(alpha = 0.12f) else Color.Transparent,
        animationSpec = tween(durationMillis = 200),
        label = "backgroundColor"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) primaryColor else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
        animationSpec = tween(durationMillis = 200),
        label = "contentColor"
    )
    val indicatorWidth by animateDpAsState(
        targetValue = if (isSelected) 4.dp else 0.dp,
        animationSpec = tween(durationMillis = 200),
        label = "indicatorWidth"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Left Selection Bar Indicator
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .height(20.dp)
                        .width(indicatorWidth)
                        .clip(RoundedCornerShape(2.dp))
                        .background(primaryColor)
                )
                Spacer(modifier = Modifier.width(10.dp))
            } else {
                Spacer(modifier = Modifier.width(4.dp))
            }

            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                tint = contentColor,
                modifier = Modifier.size(22.dp)
            )
            
            Spacer(modifier = Modifier.width(14.dp))
            
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = contentColor,
                    fontSize = 14.5.sp
                ),
                modifier = Modifier.weight(1f)
            )

            if (item.badge != null) {
                Surface(
                    color = if (isSelected) primaryColor else primaryColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = item.badge,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else primaryColor,
                            fontSize = 10.sp
                        )
                    )
                }
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
    val route: String,
    val badge: String? = null
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
                    .systemBarsPadding()
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text("Nội dung màn hình chính")
            }
        }
    }
}
