package com.sm.infratalk.presenter.sign.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
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
import com.sm.infratalk.presenter.sign.viewmodel.SignViewModel
import com.sm.infratalk.presenter.sign.viewmodel.ViewEvent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
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
            else -> {}
        }
    }

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
                    onValueChange = { email = it },
                    label = { Text("이메일") },
                    modifier = Modifier.width(250.dp),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("비밀번호") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.width(250.dp),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = {
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
                            onCheckedChange = { rememberEmail = it }
                        )
                        Text("아이디 저장")
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Row(
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextButton(
                        onClick = { onSignUpClick() },
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
                        onClick = { onResetPasswordClick() },
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
            
            if (showProgressBar) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    val onSignUpClick: () -> Unit = {}
    val onLoginSuccess: () -> Unit = {}
    val onResetPasswordClick: () -> Unit = {}
    val onError: (String) -> Unit = {}
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
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
                        Box(
                            modifier = Modifier
                                .size(width = 200.dp, height = 100.dp)
                                .background(Color.LightGray)
                        )
                        
                        Text(
                            text = "InfraTalk",
                            fontSize = 50.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1976D2),
                            modifier = Modifier.padding(top = 10.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(35.dp))
                
                OutlinedTextField(
                    value = "user@example.com",
                    onValueChange = { },
                    label = { Text("이메일") },
                    modifier = Modifier.width(250.dp),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = "••••••••",
                    onValueChange = { },
                    label = { Text("비밀번호") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.width(250.dp),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = { },
                    modifier = Modifier
                        .width(250.dp)
                        .height(65.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1976D2)
                    )
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
                            .padding(start = 70.dp)
                    ) {
                        Checkbox(
                            checked = true,
                            onCheckedChange = { }
                        )
                        Text("아이디 저장")
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Row(
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextButton(
                        onClick = { },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color(0xFF1976D2)
                        )
                    ) {
                        Text(
                            "회원가입",
                            fontSize = 18.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(10.dp))
                    
                    TextButton(
                        onClick = { },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color(0xFF1976D2)
                        )
                    ) {
                        Text(
                            "암호 초기화",
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    }
}