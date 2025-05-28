package com.sm.infratalk.presenter.sign.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.sm.infratalk.presenter.sign.form.SavedEmailSetForm
import com.sm.infratalk.presenter.sign.viewmodel.SignViewModel
import com.sm.infratalk.presenter.sign.viewmodel.ViewEvent
import kotlinx.coroutines.launch

// UI 컴포넌트 (상태 없음, 순수 UI 표현)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoginContent(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    rememberEmail: Boolean,
    onRememberEmailChange: (Boolean) -> Unit,
    onLoginClick: () -> Unit,
    onSignUpClick: () -> Unit,
    onResetPasswordClick: () -> Unit,
    isLoading: Boolean,
    showSnackbar: Boolean = false,
    snackbarMessage: String = "",
    onDismissSnackbar: () -> Unit = {}
) {
    val colorPrimaryDark = colorResource(id = R.color.colorPrimaryDark)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.4f)
                ) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_logo),
                            contentDescription = "Logo",
                            modifier = Modifier.size(width = 200.dp, height = 100.dp)
                        )
                        
                        Text(
                            text = "InfraTalk",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontSize = 50.sp
                            ),
                            fontWeight = FontWeight.Bold,
                            color = colorPrimaryDark,
                            modifier = Modifier.padding(top = 10.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(35.dp))
                
                OutlinedTextField(
                    value = email,
                    onValueChange = onEmailChange,
                    label = { Text("이메일") },
                    modifier = Modifier.width(250.dp),
                    singleLine = true,
                    colors = androidx.compose.material3.TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = colorPrimaryDark,
                        cursorColor = colorPrimaryDark,
                        focusedLabelColor = colorPrimaryDark
                    )
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = password,
                    onValueChange = onPasswordChange,
                    label = { Text("비밀번호") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.width(250.dp),
                    singleLine = true,
                    colors = androidx.compose.material3.TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = colorPrimaryDark,
                        cursorColor = colorPrimaryDark,
                        focusedLabelColor = colorPrimaryDark
                    )
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = onLoginClick,
                    modifier = Modifier
                        .width(250.dp)
                        .height(65.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorPrimaryDark)
                ) {
                    Text(
                        "로그인",
                        fontSize = 20.sp
                    )
                }
                
                Box(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = (0.2f * LocalConfiguration.current.screenWidthDp).dp)
                    ) {
                        Checkbox(
                            checked = rememberEmail,
                            onCheckedChange = onRememberEmailChange,
                            colors = androidx.compose.material3.CheckboxDefaults.colors(
                                checkedColor = colorPrimaryDark,
                                checkmarkColor = androidx.compose.ui.graphics.Color.White
                            )
                        )
                        Text("아이디 저장")
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Row(
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextButton(
                        onClick = onSignUpClick,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = colorPrimaryDark
                        )
                    ) {
                        Text(
                            "회원가입",
                            fontSize = 18.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(10.dp))
                    
                    TextButton(
                        onClick = onResetPasswordClick,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = colorPrimaryDark
                        )
                    ) {
                        Text(
                            "암호 초기화",
                            fontSize = 18.sp
                        )
                    }
                }
            }
            
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                )
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

// 화면 컴포넌트 (ViewModel 연결)
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
                    viewModel.setSavedEmail(SavedEmailSetForm(email))
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
            else -> {}
        }
    }

    LoginContent(
        email = email,
        onEmailChange = { email = it },
        password = password,
        onPasswordChange = { password = it },
        rememberEmail = rememberEmail,
        onRememberEmailChange = { rememberEmail = it },
        onLoginClick = {
            when {
                email.isEmpty() -> {
                    onError("이메일을 입력하세요")
                }
                password.isEmpty() -> {
                    onError("비밀번호를 입력하세요")
                }
                else -> {
                    showProgressBar = true
                    scope.launch {
                        try {
                            viewModel.logIn(LoginForm(email, password))
                        } catch (e: Exception) {
                            showProgressBar = false
                            onError("로그인 중 오류가 발생했습니다: ${e.message}")
                        }
                    }
                }
            }
        },
        onSignUpClick = onSignUpClick,
        onResetPasswordClick = onResetPasswordClick,
        isLoading = showProgressBar,
        showSnackbar = showSnackbar,
        snackbarMessage = snackbarMessage,
        onDismissSnackbar = onDismissSnackbar
    )
}

// 프리뷰
@Preview(showBackground = true)
@Composable
fun LoginContentPreview() {
    MaterialTheme {
        LoginContent(
            email = "user@example.com",
            onEmailChange = {},
            password = "••••••••",
            onPasswordChange = {},
            rememberEmail = true,
            onRememberEmailChange = {},
            onLoginClick = {},
            onSignUpClick = {},
            onResetPasswordClick = {},
            isLoading = false
        )
    }
}

// 로딩 상태 프리뷰
@Preview(showBackground = true)
@Composable
fun LoginContentLoadingPreview() {
    MaterialTheme {
        LoginContent(
            email = "user@example.com",
            onEmailChange = {},
            password = "••••••••",
            onPasswordChange = {},
            rememberEmail = true,
            onRememberEmailChange = {},
            onLoginClick = {},
            onSignUpClick = {},
            onResetPasswordClick = {},
            isLoading = true
        )
    }
}
