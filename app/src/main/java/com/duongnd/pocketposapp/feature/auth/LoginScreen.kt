package com.duongnd.pocketposapp.feature.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.duongnd.pocketposapp.core.navigation.Routes
import com.duongnd.pocketposapp.core.ui.components.AppOutlinedTextField
import com.duongnd.pocketposapp.core.ui.components.PrimaryButton
import com.duongnd.pocketposapp.feature.auth.components.AuthFooter
import com.duongnd.pocketposapp.feature.auth.components.AuthHeader
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.loginState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var passwordVisible by remember { mutableStateOf(false) }
    var showIncompleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is AuthUiEvent.LoginSuccess -> {
                    if (event.isCompleteProfile) {
                        navController.navigate(Routes.SCANNER) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    } else {
                        showIncompleteDialog = true
                    }
                }
                is AuthUiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                else -> Unit
            }
        }
    }

    val primaryColor = MaterialTheme.colorScheme.primary

    if (showIncompleteDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Thông tin cửa hàng") },
            text = { Text("Cửa hàng của bạn chưa hoàn thiện thông tin. Vui lòng cập nhật để tiếp tục sử dụng ứng dụng.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showIncompleteDialog = false
                        navController.navigate(Routes.storeInfo("login")) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    }
                ) {
                    Text("Cập nhật ngay")
                }
            }
        )
    }

    val gradient = Brush.verticalGradient(
        colors = listOf(primaryColor, primaryColor.copy(alpha = 0.8f))
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .systemBarsPadding()
            .imePadding()
    ) {
        val screenHeight = maxHeight
        val isSmallScreen = screenHeight < 600.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.heightIn(min = screenHeight),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Section
                AuthHeader(
                    title = "PocketPOS",
                    subtitle = "Giải pháp bán hàng hiện đại",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = if (isSmallScreen) 24.dp else 48.dp),
                    iconSize = if (isSmallScreen) 64 else 100,
                    titleStyle = if (isSmallScreen) MaterialTheme.typography.headlineMedium else MaterialTheme.typography.displaySmall
                )

                // Login Form Section
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 8.dp,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(32.dp))

                        // Email Field
                        AppOutlinedTextField(
                            value = state.email,
                            onValueChange = { viewModel.onEmailChanged(it) },
                            label = { Text("Email") },
                            modifier = Modifier.fillMaxWidth(),
                            isError = state.emailError != null,
                            supportingText = state.emailError?.let { { Text(it) } },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = primaryColor) },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Password Field
                        AppOutlinedTextField(
                            value = state.password,
                            onValueChange = { viewModel.onPasswordChanged(it) },
                            label = { Text("Mật khẩu") },
                            modifier = Modifier.fillMaxWidth(),
                            isError = state.passwordError != null,
                            supportingText = state.passwordError?.let { { Text(it) } },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = primaryColor) },
                            trailingIcon = {
                                val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(imageVector = image, contentDescription = null)
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                    viewModel.login()
                                }
                            )
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { /* TODO */ }) {
                                Text(
                                    text = "Quên mật khẩu?",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = primaryColor,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Login Button
                        PrimaryButton(
                            text = "ĐĂNG NHẬP",
                            isLoading = state.isLoading,
                            onClick = {
                                focusManager.clearFocus()
                                viewModel.login()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        // Register Section
                        AuthFooter(
                            text = "Bạn chưa có tài khoản?",
                            actionText = "Đăng ký ngay",
                            onActionClick = { navController.navigate(Routes.REGISTER) }
                        )
                    }
                }
            }
        }
    }
}
