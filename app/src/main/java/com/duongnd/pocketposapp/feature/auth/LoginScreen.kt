package com.duongnd.pocketposapp.feature.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.duongnd.pocketposapp.core.navigation.Routes
import com.duongnd.pocketposapp.core.ui.components.AppOutlinedTextField
import com.duongnd.pocketposapp.core.ui.components.PrimaryButton
import com.duongnd.pocketposapp.data.remote.dto.auth.login.LoginRequest

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    
    val state by viewModel.loginState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            navController.navigate(Routes.SCANNER) {
                popUpTo(Routes.LOGIN) { inclusive = true }
            }
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    val gradient = Brush.verticalGradient(
        colors = listOf(primaryColor, primaryColor.copy(alpha = 0.8f))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Section with Logo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    modifier = Modifier.size(100.dp),
                    color = Color.White.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.PointOfSale,
                            contentDescription = null,
                            modifier = Modifier.size(60.dp),
                            tint = Color.White
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "PocketPOS",
                    style = MaterialTheme.typography.displaySmall,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Giải pháp bán hàng hiện đại",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        // Login Form Section
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(2f),
            shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                // Email Field
                AppOutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email hoặc Tên đăng nhập") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = primaryColor) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Password Field
                AppOutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Mật khẩu") },
                    modifier = Modifier.fillMaxWidth(),
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
                        if (email.isNotBlank() && password.isNotBlank()) {
                            viewModel.login(LoginRequest(email, password))
                        } else {
                            Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                // Register Section
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 32.dp)
                ) {
                    Text(
                        text = "Bạn chưa có tài khoản?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    TextButton(onClick = { navController.navigate(Routes.REGISTER) }) {
                        Text(
                            text = "Đăng ký ngay",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = primaryColor
                        )
                    }
                }
            }
        }
    }
}
