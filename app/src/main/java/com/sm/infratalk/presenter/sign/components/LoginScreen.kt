package com.sm.infratalk.presenter.sign.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sm.infratalk.presenter.sign.form.LoginForm
import com.sm.infratalk.presenter.sign.viewmodel.SignViewModel
import com.sm.infratalk.presenter.sign.viewmodel.ViewEvent
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    viewModel: SignViewModel,
    onSignUpClick: () -> Unit,
    onLoginSuccess: () -> Unit,
    onResetPasswordClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showProgressBar by remember { mutableStateOf(false) }
    var rememberEmail by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(Unit) {
        // 저장된 이메일 불러오기
        runCatching {
            val savedEmail = viewModel.getSavedEmail().email
            if (savedEmail.isNotEmpty()) {
                email = savedEmail
                rememberEmail = true
            }
        }
    }

    val loginState by viewModel.viewEvent.collectAsState(initial = null)

    LaunchedEffect(loginState) {
        when (loginState) {
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
                val errorMessage = when (val error = (loginState as ViewEvent.Error).throwable) {
                    is NotExistEmailException -> "이메일이 존재하지 않습니다"
                    is InvalidEmailException -> "이메일을 확인하세요"
                    is WrongPasswordException -> "암호가 틀렸습니다"
                    is NeedVerifiedEmailException -> "이메일 인증이 필요합니다"
                    is BlockedRequestException -> "요청이 많아 잠시 기다려 주세요"
                    is FailVerifiedEmailException -> "이메일 전송을 실패 했습니다"
                    is FailSelectException -> "계정 정보 조회에 실패했습니다"
                    is FailFirebaseLoginException -> "파이어 베이스 로그인 실패"
                    is NotExistFirebaseUserException -> "데이터 베이스에 유저 정보가 없습니다"
                    else -> "알 수 없는 에러가 발생했습니다"
                }
                // 에러 메시지 표시
                CustomSnackbar.make(composeView = androidx.compose.ui.platform.LocalView.current, 
                    message = errorMessage, 
                    duration = androidx.compose.material3.SnackbarDuration.Short)
                    .setMargin(bottomDp = 66)
                    .show()
            }
            null -> {
                showProgressBar = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("이메일") },
            modifier = Modifier.fillMaxWidth()
        )
        
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("비밀번호") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = rememberEmail,
                onCheckedChange = { rememberEmail = it }
            )
            Text("아이디 저장")
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    when {
                        email.isEmpty() -> {
                            CustomSnackbar.make(composeView = androidx.compose.ui.platform.LocalView.current, 
                                message = "이메일을 입력하세요.", 
                                duration = androidx.compose.material3.SnackbarDuration.Short)
                                .setMargin(bottomDp = 66)
                                .show()
                        }
                        password.isEmpty() -> {
                            CustomSnackbar.make(composeView = androidx.compose.ui.platform.LocalView.current, 
                                message = "비밀번호를 입력하세요.", 
                                duration = androidx.compose.material3.SnackbarDuration.Short)
                                .setMargin(bottomDp = 66)
                                .show()
                        }
                        else -> {
                            showProgressBar = true
                            scope.launch {
                                viewModel.logIn(LoginForm(email, password))
                            }
                        }
                    }
                }
            ) {
                Text("로그인")
            }
            
            Button(onClick = onSignUpClick) {
                Text("회원가입")
            }
        }

        TextButton(onClick = onResetPasswordClick) {
            Text("비밀번호 찾기")
        }

        if (showProgressBar) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
