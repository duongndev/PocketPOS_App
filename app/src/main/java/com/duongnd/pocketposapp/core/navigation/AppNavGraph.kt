package com.duongnd.pocketposapp.core.navigation

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.duongnd.pocketposapp.core.ui.components.AppDrawer
import com.duongnd.pocketposapp.feature.category.CategoryScreen
import com.duongnd.pocketposapp.feature.category.AddCategoryScreen
import com.duongnd.pocketposapp.feature.product.AddProductScreen
import com.duongnd.pocketposapp.feature.product.EditProductScreen
import com.duongnd.pocketposapp.feature.product.ProductScreen
import com.duongnd.pocketposapp.feature.product.ProductDetailScreen
import androidx.hilt.navigation.compose.hiltViewModel
import com.duongnd.pocketposapp.feature.auth.LoginScreen
import com.duongnd.pocketposapp.feature.auth.RegisterScreen
import com.duongnd.pocketposapp.feature.splash.SplashScreen
import com.duongnd.pocketposapp.feature.scanner.ScannerScreen
import com.duongnd.pocketposapp.feature.scanner.ScanViewModel
import com.duongnd.pocketposapp.feature.checkout.CheckoutScreen
import com.duongnd.pocketposapp.feature.checkout.CheckoutViewModel
import com.duongnd.pocketposapp.feature.checkout.PaymentQRScreen
import com.duongnd.pocketposapp.feature.checkout.PaymentSuccessScreen
import com.duongnd.pocketposapp.feature.order.OrderListScreen
import com.duongnd.pocketposapp.feature.order.OrderDetailScreen
import com.duongnd.pocketposapp.feature.setting.SettingScreen
import com.duongnd.pocketposapp.feature.setting.ProfileScreen
import com.duongnd.pocketposapp.feature.setting.ChangePasswordScreen
import com.duongnd.pocketposapp.feature.store.StoreInfoScreen
import com.duongnd.pocketposapp.feature.setting.PrinterConfigScreen
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
    val screensWithoutDrawer = listOf(
        Routes.SPLASH,
        Routes.LOGIN,
        Routes.REGISTER,
        Routes.CHECKOUT,
        Routes.PAYMENT_QR,
        Routes.PAYMENT_SUCCESS
    )
    val shouldShowDrawer = currentRoute !in screensWithoutDrawer

    AppDrawer(
        navController = navController,
        drawerState = drawerState,
        scope = scope,
        gesturesEnabled = shouldShowDrawer
    ) {
        NavContent(
            navController = navController,
            onOpenDrawer = {
                if (shouldShowDrawer) {
                    scope.launch { drawerState.open() }
                }
            }
        )
    }
}

@Composable
fun NavContent(
    navController: NavHostController,
    onOpenDrawer: () -> Unit
) {
    // ViewModel dùng chung
    val scanViewModel: ScanViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(navController)
        }
        composable(Routes.LOGIN) {
            LoginScreen(navController)
        }
        composable(Routes.REGISTER) {
            RegisterScreen(navController)
        }
        composable(Routes.SCANNER) {
            ScannerScreen(navController, onOpenDrawer = onOpenDrawer, scanViewModel = scanViewModel)
        }
        composable(Routes.CHECKOUT) {
            val checkoutViewModel: CheckoutViewModel = hiltViewModel()
            CheckoutScreen(navController, viewModel = checkoutViewModel)
        }
        composable(
            route = Routes.PAYMENT_QR,
            arguments = listOf(
                navArgument("orderId") { type = NavType.StringType },
                navArgument("totalPrice") { type = NavType.FloatType },
                navArgument("qrUrl") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            val totalPrice = backStackEntry.arguments?.getFloat("totalPrice")?.toDouble() ?: 0.0
            val qrUrl = backStackEntry.arguments?.getString("qrUrl") ?: ""
            val checkoutViewModel: CheckoutViewModel = hiltViewModel()
            PaymentQRScreen(navController, checkoutViewModel, orderId, totalPrice, qrUrl)
        }
        composable(
            route = Routes.PAYMENT_SUCCESS,
        ) { backStackEntry ->
            PaymentSuccessScreen(navController)
        }
        composable(Routes.CATEGORIES) {
            CategoryScreen(navController, onOpenDrawer = onOpenDrawer)
        }
        composable(Routes.ADD_CATEGORY) {
            AddCategoryScreen(navController)
        }
        composable(Routes.ORDERS) {
            OrderListScreen(navController, onOpenDrawer = onOpenDrawer)
        }
        composable(
            route = Routes.ORDER_DETAIL,
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            OrderDetailScreen(navController, orderId)
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
        composable(Routes.ADD_PRODUCT) {
            AddProductScreen(navController = navController)
        }
        composable(
            route = Routes.EDIT_PRODUCT,
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            EditProductScreen(navController = navController, productId = productId)
        }
        composable(Routes.SETTINGS) {
            SettingScreen(navController, onOpenDrawer = onOpenDrawer)
        }
        composable(Routes.PROFILE) {
            ProfileScreen(navController)
        }
        composable(Routes.CHANGE_PASSWORD) {
            ChangePasswordScreen(navController)
        }
        composable(
            route = Routes.STORE_INFO,
            arguments = listOf(navArgument("from") { 
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            val from = backStackEntry.arguments?.getString("from")
            StoreInfoScreen(navController, from = from)
        }
        composable(Routes.PRINTER_CONFIG) {
            PrinterConfigScreen(navController)
        }
        composable(Routes.STATISTICS) {
            StatisticsScreen(onOpenDrawer = onOpenDrawer)
        }
    }
}
