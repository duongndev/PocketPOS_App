package com.duongnd.pocketposapp.core.navigation

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.duongnd.pocketposapp.core.ui.components.AppDrawer
import com.duongnd.pocketposapp.feature.category.CategoryScreen
import com.duongnd.pocketposapp.feature.category.AddCategoryScreen
import com.duongnd.pocketposapp.feature.product.AddEditProductScreen
import com.duongnd.pocketposapp.feature.product.ProductVariantScreen
import com.duongnd.pocketposapp.feature.product.VariantListScreen
import com.duongnd.pocketposapp.feature.product.ProductScreen
import com.duongnd.pocketposapp.feature.product.ProductDetailScreen
import com.duongnd.pocketposapp.feature.product.AddEditProductViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.duongnd.pocketposapp.feature.splash.SplashScreen
import com.duongnd.pocketposapp.feature.scanner.ScannerScreen
import com.duongnd.pocketposapp.feature.setting.SettingScreen
import com.duongnd.pocketposapp.feature.statistics.StatisticsScreen
import kotlinx.coroutines.launch

@Composable
fun AppNavGraph(
    navController: NavHostController
) {
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Danh sách các màn hình KHÔNG hiển thị Drawer (ví dụ Splash)
    val screensWithoutDrawer = listOf(Routes.SPLASH)
    val shouldShowDrawer = currentRoute !in screensWithoutDrawer

    if (shouldShowDrawer) {
        AppDrawer(
            navController = navController,
            drawerState = drawerState,
            scope = scope
        ) {
            NavContent(navController, onOpenDrawer = { scope.launch { drawerState.open() } })
        }
    } else {
        NavContent(navController, onOpenDrawer = {})
    }
}

@Composable
fun NavContent(
    navController: NavHostController,
    onOpenDrawer: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(navController)
        }
        composable(Routes.SCANNER) {
            ScannerScreen(navController)
        }
        composable(Routes.CATEGORIES) {
            CategoryScreen(navController, onOpenDrawer = onOpenDrawer)
        }
        composable(Routes.ADD_CATEGORY) {
            AddCategoryScreen(navController)
        }
        composable(Routes.PRODUCTS) {
            ProductScreen(navController, onOpenDrawer = onOpenDrawer)
        }
        composable(
            route = Routes.PRODUCT_DETAIL,
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) {
            ProductDetailScreen(navController = navController)
        }
        composable(Routes.PRODUCT_VARIANTS_LIST) {
            VariantListScreen(navController, onOpenDrawer = onOpenDrawer)
        }
        composable(Routes.ADD_PRODUCT) {
            AddEditProductScreen(navController = navController)
        }
        composable(
            route = Routes.EDIT_PRODUCT,
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            AddEditProductScreen(navController = navController, productId = productId)
        }
        composable(Routes.PRODUCT_VARIANTS) { backStackEntry ->
            // Lấy ViewModel từ BackStackEntry trước đó (Add hoặc Edit screen) để dùng chung dữ liệu đang nhập
            val parentEntry = remember(backStackEntry) {
                navController.previousBackStackEntry
            }
            
            val viewModel: AddEditProductViewModel = if (parentEntry != null) {
                hiltViewModel(parentEntry)
            } else {
                hiltViewModel(backStackEntry)
            }
            
            val state by viewModel.state.collectAsStateWithLifecycle()

            ProductVariantScreen(
                navController = navController,
                productName = state.name.ifBlank { "Sản phẩm mới" },
                variants = state.variants,
                onUpdateVariant = { index, variant ->
                    viewModel.updateVariant(
                        index, 
                        variant.price, 
                        variant.costPrice, 
                        variant.stock, 
                        variant.sku, 
                        variant.barcode
                    )
                },
                onSave = { navController.popBackStack() }
            )
        }
        composable(Routes.SETTINGS) {
            SettingScreen(navController)
        }
        composable(Routes.STATISTICS) {
            StatisticsScreen(navController, onOpenDrawer = onOpenDrawer)
        }
    }
}
