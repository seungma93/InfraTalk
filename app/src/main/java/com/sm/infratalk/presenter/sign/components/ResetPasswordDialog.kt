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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
            modifier = Modifier.width(332.dp), 
            shape = RoundedCornerShape(16.dp),
            color = Color.White
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
                                modifier = Modifier.height(40.dp),
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
                                    coroutineScope.launch {
                                        signViewModel.resetPassword(ResetPasswordForm(email))
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
                                    fontSize = 16.sp
                                )
                            }

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
                            Text(
                                text = "이메일을 확인하세요",
                                fontSize = 20.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp)
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = onDismissRequest,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(60.dp),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text("확인")
                            }
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
                                    .padding(horizontal = 20.dp)
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
