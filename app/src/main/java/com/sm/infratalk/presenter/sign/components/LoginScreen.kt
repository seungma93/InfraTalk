package com.sm.infratalk.presenter.sign.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.sm.infratalk.R
import com.sm.infratalk.data.BlockedRequestException
import com.sm.infratalk.data.FailFirebaseLoginException
import com.sm.infratalk.data.FailSelectException
import com.sm.infratalk.data.FailVerifiedEmailException
import com.sm.infratalk.data.InvalidEmailException
import com.sm.infratalk.data.NeedVerifiedEmailException
import com.sm.infratalk.data.NotExistEmailException
import com.sm.infratalk.data.NotExistFirebaseUserException
import com.sm.infratalk.data.WrongPasswordException
import com.sm.infratalk.presenter.common.CustomSnackbar
import com.sm.infratalk.presenter.sign.form.LoginForm
import com.sm.infratalk.presenter.sign.viewmodel.SignViewModel
import com.sm.infratalk.presenter.sign.viewmodel.ViewEvent
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    viewModel: SignViewModel,
    onSignUpClick: () -> Unit,
    onLoginSuccess: () -> Unit,
    onResetPasswordClick: () -> Unit,
    onError: (String) -> Unit = {},
    showSnackbar: Boolean = false,
    snackbarMessage: String = "",
    onDismissSnackbar: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showProgressBar by remember { mutableStateOf(false) }
    var rememberEmail by remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(Unit) {
        runCatching {
            val savedEmail = viewModel.getSavedEmail().email
            if (savedEmail.isNotEmpty()) {
                email = savedEmail
                rememberEmail = true
            }
        }
    }

    val viewEvent by viewModel.viewEvent.collectAsState(initial = null)
    
    LaunchedEffect(viewEvent) {
        when (viewEvent) {
            is ViewEvent.LogIn -> {
                if (rememberEmail && email != viewModel.getSavedEmail().email) {
                    viewModel.setSavedEmail(com.sm.infratalk.presenter.sign.form.SavedEmailSetForm(email))
                } else if (!rememberEmail) {
                    viewModel.deleteSavedEmail()
                }
                showProgressBar = false
                onLoginSuccess()
            }
            is ViewEvent.Error -> {
                showProgressBar = false
                val error = (viewEvent as ViewEvent.Error).throwable
                val errorMessage = when (error) {
                    is NotExistEmailException -> "이메일이 존재하지 않습니다"
                    is InvalidEmailException -> "이메일을 확인하세요"
                    is WrongPasswordException -> "암호가 틀렸습니다"
                    is NeedVerifiedEmailException -> "이메일 인증이 필요합니다"
                    is BlockedRequestException -> "요청이 많아 잠시 기다려 주세요"
                    is FailVerifiedEmailException -> "이메일 전송을 실패했습니다"
                    is FailSelectException -> "계정 정보 조회에 실패했습니다"
                    is FailFirebaseLoginException -> "파이어베이스 로그인 실패"
                    is NotExistFirebaseUserException -> "데이터베이스에 유저 정보가 없습니다"
                    else -> "로그인 중 오류가 발생했습니다: ${error.message}"
                }
                onError(errorMessage)
            }
            is ViewEvent.Loading -> {
                showProgressBar = true
            }
            else -> {}
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // 로고 이미지
                Image(
                    painter = painterResource(id = R.drawable.ic_logo),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(120.dp)
                        .padding(bottom = 16.dp)
                )

                // 앱 타이틀
                Text(
                    text = "InfraTalk",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                // 이메일 입력
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("이메일") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    leadingIcon = {
                        Icon(
                            Icons.Default.Email,
                            contentDescription = "Email",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                )

                // 비밀번호 입력
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("비밀번호") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    leadingIcon = {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = "Password",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                )

                // 아이디 저장 스위치
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Switch(
                        checked = rememberEmail,
                        onCheckedChange = { rememberEmail = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                    Text(
                        "아이디 저장",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // 로그인 버튼
                Button(
                    onClick = {
                        when {
                            email.isEmpty() -> {
                                snackbarMessage = "이메일을 입력하세요"
                                showSnackbar = true
                            }
                            password.isEmpty() -> {
                                snackbarMessage = "비밀번호를 입력하세요"
                                showSnackbar = true
                            }
                            else -> {
                                showProgressBar = true
                                scope.launch {
                                    viewModel.logIn(LoginForm(email, password))
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        "로그인",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 회원가입 버튼
                OutlinedButton(
                    onClick = onSignUpClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        "회원가입",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 비밀번호 찾기 버튼
                TextButton(
                    onClick = onResetPasswordClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "비밀번호 찾기",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            // 로딩 인디케이터
            if (showProgressBar) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(60.dp)
                    )
                }
            }
            
            CustomSnackbar(
                message = snackbarMessage,
                isVisible = showSnackbar,
                onDismiss = onDismissSnackbar,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}
