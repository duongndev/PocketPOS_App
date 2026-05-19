package com.duongnd.pocketposapp.feature.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.duongnd.pocketposapp.data.remote.dto.auth.register.RegisterRequest
import com.duongnd.pocketposapp.feature.auth.components.AuthFooter
import com.duongnd.pocketposapp.feature.auth.components.AuthHeader

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var fullName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    
    val state by viewModel.registerState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            Toast.makeText(context, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
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
        // Top Section
        AuthHeader(
            title = "Tạo tài khoản mới",
            subtitle = "Tham gia cùng PocketPOS ngay hôm nay",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 30.dp)
                .weight(0.7f)
        )

        // Register Form Section
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(2.3f),
            shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                RegisterForm(
                    fullName = fullName,
                    onFullNameChange = { fullName = it },
                    username = username,
                    onUsernameChange = { username = it },
                    email = email,
                    onEmailChange = { email = it },
                    phone = phone,
                    onPhoneChange = { phone = it },
                    password = password,
                    onPasswordChange = { password = it },
                    confirmPassword = confirmPassword,
                    onConfirmPasswordChange = { confirmPassword = it },
                    passwordVisible = passwordVisible,
                    onPasswordVisibleChange = { passwordVisible = it },
                    confirmPasswordVisible = confirmPasswordVisible,
                    onConfirmPasswordVisibleChange = { confirmPasswordVisible = it },
                    primaryColor = primaryColor
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Register Button
                PrimaryButton(
                    text = "ĐĂNG KÝ",
                    isLoading = state.isLoading,
                    onClick = {
                        if (fullName.isNotBlank() && username.isNotBlank() && email.isNotBlank() && password.isNotBlank() && confirmPassword.isNotBlank()) {
                            if (password == confirmPassword) {
                                viewModel.register(RegisterRequest(fullName, username, email, password, confirmPassword, phone))
                            } else {
                                Toast.makeText(context, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                        }
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
            }
        }
    }
}

@Composable
fun RegisterForm(
    fullName: String,
    onFullNameChange: (String) -> Unit,
    username: String,
    onUsernameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    phone: String,
    onPhoneChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onPasswordVisibleChange: (Boolean) -> Unit,
    confirmPasswordVisible: Boolean,
    onConfirmPasswordVisibleChange: (Boolean) -> Unit,
    primaryColor: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        AppOutlinedTextField(
            value = fullName,
            onValueChange = onFullNameChange,
            label = { Text("Họ và tên") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = primaryColor) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next)
        )

        AppOutlinedTextField(
            value = username,
            onValueChange = onUsernameChange,
            label = { Text("Tài khoản") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = primaryColor) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next)
        )

        AppOutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = primaryColor) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next)
        )

        AppOutlinedTextField(
            value = phone,
            onValueChange = onPhoneChange,
            label = { Text("Số điện thoại") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = primaryColor) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next)
        )

        AppOutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Mật khẩu") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = primaryColor) },
            trailingIcon = {
                val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(onClick = { onPasswordVisibleChange(!passwordVisible) }) {
                    Icon(imageVector = image, contentDescription = null)
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next)
        )

        AppOutlinedTextField(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            label = { Text("Xác nhận mật khẩu") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.LockReset, contentDescription = null, tint = primaryColor) },
            trailingIcon = {
                val image = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(onClick = { onConfirmPasswordVisibleChange(!confirmPasswordVisible) }) {
                    Icon(imageVector = image, contentDescription = null)
                }
            },
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done)
        )
    }
}
