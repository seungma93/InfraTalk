package com.sm.infratalk.presenter.sign.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sm.infratalk.data.BlockedRequestException
import com.sm.infratalk.data.FailResetPasswordException
import com.sm.infratalk.data.InvalidEmailException
import com.sm.infratalk.data.NotExistEmailException
import com.sm.infratalk.presenter.common.CustomSnackbar
import com.sm.infratalk.presenter.sign.form.ResetPasswordForm
import com.sm.infratalk.presenter.sign.viewmodel.SignViewModel
import com.sm.infratalk.presenter.sign.viewmodel.ViewEvent
import kotlinx.coroutines.launch

@Composable
fun ResetPasswordDialog(
    onDismissRequest: () -> Unit,
    signViewModel: SignViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var uiState by remember { mutableStateOf<ResetPasswordUiState>(ResetPasswordUiState.Input) }
    
    // 스낵바 상태 변수 추가
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        signViewModel.viewEvent.collect { event ->
            when (event) {
                is ViewEvent.ResetPassword -> {
                    uiState = ResetPasswordUiState.Success
                }
                is ViewEvent.Error -> {
                    if (event.throwable is FailResetPasswordException) {
                        uiState = ResetPasswordUiState.Failed
                    }
                    // 에러 메시지를 스낵바로 표시
                    snackbarMessage = when (val error = event.throwable) {
                        is FailResetPasswordException -> "비밀번호 초기화 요청 실패"
                        is NotExistEmailException -> "등록되지 않은 이메일입니다"
                        is InvalidEmailException -> "유효하지 않은 이메일 형식입니다"
                        is BlockedRequestException -> "요청이 너무 많습니다. 잠시 후 다시 시도해주세요"
                        else -> "오류가 발생했습니다: ${error.message}"
                    }
                    showSnackbar = true
                }
                else -> {}
            }
        }
    }

    Dialog(onDismissRequest = onDismissRequest) {
        Box {
            Surface(
                modifier = Modifier.width(332.dp), 
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    when (uiState) {
                        ResetPasswordUiState.Input -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Spacer(modifier = Modifier.height(20.dp))

                                Text(
                                    text = "비밀번호 초기화",
                                    fontSize = 25.sp,
                                    modifier = Modifier
                                        .height(40.dp)
                                        .fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                OutlinedTextField(
                                    value = email,
                                    onValueChange = { email = it },
                                    label = { Text("이메일") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(70.dp),
                                    shape = RoundedCornerShape(4.dp)
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                Button(
                                    onClick = {
                                        if (email.isEmpty()) {
                                            // 이메일이 비어있을 경우 스낵바 표시
                                            snackbarMessage = "이메일을 입력해주세요"
                                            showSnackbar = true
                                        } else {
                                            coroutineScope.launch {
                                                try {
                                                    signViewModel.resetPassword(ResetPasswordForm(email))
                                                } catch (e: Exception) {
                                                    // 예외 발생 시 스낵바 표시
                                                    snackbarMessage = "오류가 발생했습니다: ${e.message}"
                                                    showSnackbar = true
                                                }
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(60.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    ),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        "메일 전송",
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                }

                                // 하단 여백을 XML과 동일하게 맞춤
                                Spacer(modifier = Modifier.height(20.dp))
                            }
                        }
                        ResetPasswordUiState.Success -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 20.dp)
                            ) {
                                // complete_text와 동일한 스타일과 여백 적용
                                Text(
                                    text = "이메일을 확인하세요",
                                    fontSize = 20.sp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .padding(start = 20.dp),
                                    textAlign = TextAlign.Start
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                Button(
                                    onClick = onDismissRequest,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(60.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    ),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        "확인",
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(20.dp))
                            }
                        }
                        ResetPasswordUiState.Failed -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 20.dp)
                            ) {
                                Text(
                                    text = "비밀번호 초기화 실패",
                                    fontSize = 20.sp,
                                    color = Color.Red,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .padding(start = 20.dp),
                                    textAlign = TextAlign.Start
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                Button(
                                    onClick = onDismissRequest,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(60.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.Red
                                    ),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        "확인",
                                        color = Color.White
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(20.dp))
                            }
                        }
                    }
                }
            }
            
            // CustomSnackbar 추가
            CustomSnackbar(
                message = snackbarMessage,
                isVisible = showSnackbar,
                onDismiss = { showSnackbar = false },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

sealed class ResetPasswordUiState {
    object Input : ResetPasswordUiState()
    object Success : ResetPasswordUiState()
    object Failed : ResetPasswordUiState()
}
