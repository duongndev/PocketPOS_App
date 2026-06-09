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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.duongnd.pocketposapp.core.ui.components.AppOutlinedTextField
import com.duongnd.pocketposapp.core.ui.components.PrimaryButton
import com.duongnd.pocketposapp.feature.auth.components.AuthFooter
import com.duongnd.pocketposapp.feature.auth.components.AuthHeader
import kotlinx.coroutines.flow.collectLatest

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.registerState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is AuthUiEvent.RegisterSuccess -> {
                    Toast.makeText(context, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }
                is AuthUiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                else -> Unit
            }
        }
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    val gradient = Brush.verticalGradient(
        colors = listOf(primaryColor, primaryColor.copy(alpha = 0.8f))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Header Section
            AuthHeader(
                title = "Tạo tài khoản",
                subtitle = "Tham gia cùng PocketPOS ngay hôm nay",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 40.dp),
                iconSize = 80
            )

            // 2. Form Section (Surface trắng)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = screenHeight),
                shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 32.dp)
                        .imePadding(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(32.dp))

                    // Các trường nhập liệu
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        AppOutlinedTextField(
                            value = state.storeName,
                            onValueChange = { viewModel.onRegisterStoreNameChanged(it) },
                            label = { Text("Tên cửa hàng") },
                            isError = state.storeNameError != null,
                            supportingText = state.storeNameError?.let { { Text(it) } },
                            leadingIcon = { Icon(Icons.Default.Store, null, tint = primaryColor) },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                        )

                        AppOutlinedTextField(
                            value = state.fullName,
                            onValueChange = { viewModel.onRegisterFullNameChanged(it) },
                            label = { Text("Họ và tên") },
                            isError = state.fullNameError != null,
                            supportingText = state.fullNameError?.let { { Text(it) } },
                            leadingIcon = { Icon(Icons.Default.Person, null, tint = primaryColor) },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                        )

                        AppOutlinedTextField(
                            value = state.email,
                            onValueChange = { viewModel.onRegisterEmailChanged(it) },
                            label = { Text("Email") },
                            isError = state.emailError != null,
                            supportingText = state.emailError?.let { { Text(it) } },
                            leadingIcon = { Icon(Icons.Default.Email, null, tint = primaryColor) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                        )

                        AppOutlinedTextField(
                            value = state.phone,
                            onValueChange = { viewModel.onRegisterPhoneChanged(it) },
                            label = { Text("Số điện thoại") },
                            isError = state.phoneError != null,
                            supportingText = state.phoneError?.let { { Text(it) } },
                            leadingIcon = { Icon(Icons.Default.Phone, null, tint = primaryColor) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                        )

                        AppOutlinedTextField(
                            value = state.password,
                            onValueChange = { viewModel.onRegisterPasswordChanged(it) },
                            label = { Text("Mật khẩu") },
                            isError = state.passwordError != null,
                            supportingText = state.passwordError?.let { { Text(it) } },
                            leadingIcon = { Icon(Icons.Default.Lock, null, tint = primaryColor) },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, null)
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                        )

                        AppOutlinedTextField(
                            value = state.confirmPassword,
                            onValueChange = { viewModel.onRegisterConfirmPasswordChanged(it) },
                            label = { Text("Xác nhận mật khẩu") },
                            isError = state.confirmPasswordError != null,
                            supportingText = state.confirmPasswordError?.let { { Text(it) } },
                            leadingIcon = { Icon(Icons.Default.LockReset, null, tint = primaryColor) },
                            trailingIcon = {
                                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                    Icon(if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, null)
                                }
                            },
                            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = {
                                focusManager.clearFocus()
                                viewModel.register()
                            })
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    PrimaryButton(
                        text = "ĐĂNG KÝ",
                        isLoading = state.isLoading,
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.register()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    AuthFooter(
                        text = "Đã có tài khoản?",
                        actionText = "Đăng nhập",
                        onActionClick = { navController.popBackStack() }
                    )
                    
                    // Thêm khoảng trống cuối để cuộn qua bàn phím
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }
}
