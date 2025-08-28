package com.sm.infratalk.presenter.board.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color

// UI 컴포넌트 (상태 없음, 순수 UI 표현)
@Composable
private fun BoardContent(
) {
    val colorPrimaryDark = colorResource(id = R.color.colorPrimaryDark)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

    }
}

// 화면 컴포넌트 (ViewModel 연결)
@Composable
fun BoardScreen(
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

    BoardContent(

    )
}

