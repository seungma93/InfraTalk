package com.sm.infratalk.presenter.sign.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sm.infratalk.data.FailResetPasswordException
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
                }
                else -> {}
            }
        }
    }

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(16.dp)
            ) {
                when (uiState) {
                    ResetPasswordUiState.Input -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "비밀번호 재설정",
                                style = TextStyle(
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("이메일") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        signViewModel.resetPassword(ResetPasswordForm(email))
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("비밀번호 찾기")
                            }
                        }
                    }
                    ResetPasswordUiState.Success -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "이메일로 재설정 링크를 보냈습니다",
                                style = TextStyle(
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                modifier = Modifier.padding(vertical = 16.dp)
                            )

                            Button(
                                onClick = onDismissRequest,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("확인")
                            }
                        }
                    }
                    ResetPasswordUiState.Failed -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "패스워드 초기화 실패",
                                style = TextStyle(
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Red
                                ),
                                modifier = Modifier.padding(vertical = 16.dp)
                            )

                            Button(
                                onClick = onDismissRequest,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("확인")
                            }
                        }
                    }
                }
            }
        }
    }
}

sealed class ResetPasswordUiState {
    object Input : ResetPasswordUiState()
    object Success : ResetPasswordUiState()
    object Failed : ResetPasswordUiState()
}
