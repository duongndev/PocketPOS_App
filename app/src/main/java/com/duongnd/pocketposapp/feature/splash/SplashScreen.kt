package com.duongnd.pocketposapp.feature.splash

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.duongnd.pocketposapp.core.navigation.Routes
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: SplashViewModel = hiltViewModel()
) {
    var startAnimation by remember { mutableStateOf(false) }
    var showIncompleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        startAnimation = true
        viewModel.checkAuth()
    }

    LaunchedEffect(viewModel.uiState) {
        viewModel.uiState.collectLatest { state ->
            Timber.tag("SplashScreen").d("Current state: $state")
            when (state) {
                is SplashUiState.Authenticated -> {
                    Timber.tag("SplashScreen").d("Navigating to SCANNER")
                    navController.navigate(Routes.SCANNER) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
                is SplashUiState.IncompleteProfile -> {
                    Timber.tag("SplashScreen").d("Show Incomplete Profile Dialog")
                    showIncompleteDialog = true
                }
                is SplashUiState.Unauthenticated -> {
                    Timber.tag("SplashScreen").d("Navigating to LOGIN")
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
                else -> Unit
            }
        }
    }

    if (showIncompleteDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Thông tin cửa hàng") },
            text = { Text("Cửa hàng của bạn chưa hoàn thiện thông tin. Vui lòng cập nhật để tiếp tục sử dụng ứng dụng.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showIncompleteDialog = false
                        navController.navigate(Routes.storeInfo("splash")) {
                            popUpTo(Routes.SPLASH) { inclusive = true }
                        }
                    }
                ) {
                    Text("Cập nhật ngay")
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                visible = startAnimation,
                enter = fadeIn(animationSpec = tween(1000)) +
                        slideInVertically(initialOffsetY = { it / 2 })
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.PointOfSale,
                        contentDescription = "Logo",
                        modifier = Modifier.size(100.dp),
                        tint = Color.White
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "PocketPOS",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 2.sp
                        )
                    )

                    Text(
                        text = "Quản lý bán hàng",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Normal
                        )
                    )
                }
            }
        }
    }
}
