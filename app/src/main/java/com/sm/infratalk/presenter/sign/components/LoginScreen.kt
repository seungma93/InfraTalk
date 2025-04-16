package com.sm.infratalk.presenter.sign.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import com.sm.infratalk.presenter.sign.viewmodel.SignViewModel
import com.sm.infratalk.presenter.sign.viewmodel.ViewEvent
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(viewModel: SignViewModel) {
    val viewModel: SignViewModel by viewModels()
    val coroutineScope = rememberCoroutineScope()

    // ViewModel 상태 관찰
    val loginState by viewModel.viewEvent.collectAsStateWithLifecycle()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showProgressBar by remember { mutableStateOf(false) }
    var showMessage by remember { mutableStateOf("") }

    // 상태 변화에 따른 처리
    LaunchedEffect(loginState) {
        when (loginState) {
            is ViewEvent.LogIn -> {
                showProgressBar = false
                // Main으로 이동
            }
            is ViewEvent.Error -> {
                showProgressBar = false
                showMessage = "에러 발생: " + (loginState as ViewEvent.Error).throwable.message
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        BasicTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth()
        )
        BasicTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth()
        )
        Row {
            Button(onClick = {
                coroutineScope.launch {
                    showProgressBar = true
                    viewModel.logIn(LoginForm(email, password))
                }
            }) {
                Text("Login")
            }
            Button(onClick = {
                // Sign Up으로 이동
            }) {
                Text("Sign Up")
            }
        }
        if (showProgressBar) {
            CircularProgressIndicator()
        }
        if (showMessage.isNotEmpty()) {
            Snackbar {
                Text(showMessage)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    // 미리보기 용으로 Mock ViewModel 사용 또는 필요한 데이터 제공
    val dummyViewModel = SignViewModel()
    LoginScreen(viewModel = dummyViewModel)
}