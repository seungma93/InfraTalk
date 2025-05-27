import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.sm.infratalk.R
import com.sm.infratalk.presenter.common.CustomSnackbar

@Composable
fun SignUpScreen(
    isLoading: Boolean = false,
    onSignUpClick: (String, String, String, String) -> Unit,
    onProfileImageClick: () -> Unit,
    profileImageUri: Uri? = null,
    showSnackbar: Boolean = false,
    snackbarMessage: String = "",
    onSnackbarDismissed: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordCheck by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
        
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable { onProfileImageClick() }
            ) {
                if (profileImageUri != null) {
                    AsyncImage(
                        model = profileImageUri,
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_person_24),
                        contentDescription = "Default Profile",
                        modifier = Modifier
                            .size(200.dp)
                            .align(Alignment.Center)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("이메일") },
                modifier = Modifier.fillMaxWidth(0.8f) // 수정: 0.6f → 0.8f
            )
            
            Text(
                text = "인증 메일이 전송 됩니다.",
                modifier = Modifier
                    .fillMaxWidth(0.8f) // 수정: 0.6f → 0.8f
                    .padding(top = 5.dp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(5.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("비밀번호") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(0.8f) // 수정: 0.6f → 0.8f
            )

            Spacer(modifier = Modifier.height(5.dp))

            OutlinedTextField(
                value = passwordCheck,
                onValueChange = { passwordCheck = it },
                label = { Text("비밀번호 확인") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(0.8f) // 수정: 0.6f → 0.8f
            )
            
            Text(
                text = "비밀번호는 6자리 이상 필요합니다.",
                modifier = Modifier
                    .fillMaxWidth(0.8f) // 수정: 0.6f → 0.8f
                    .padding(top = 5.dp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(5.dp))

            OutlinedTextField(
                value = nickname,
                onValueChange = { nickname = it },
                label = { Text("닉네임") },
                modifier = Modifier.fillMaxWidth(0.8f) // 수정: 0.6f → 0.8f
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { 
                    if (!isLoading) {
                        onSignUpClick(email, password, passwordCheck, nickname) 
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.8f) // 수정: 0.6f → 0.8f
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.colorAccent)
                )
            ) {
                Text(
                    "회원가입",
                    fontSize = 16.sp
                )
            }
        }
        
        CustomSnackbar(
            message = snackbarMessage,
            isVisible = showSnackbar,
            onDismiss = onSnackbarDismissed,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    SignUpScreen(
        isLoading = false,
        onSignUpClick = { _, _, _, _ -> },
        onProfileImageClick = { },
        profileImageUri = null,
        showSnackbar = false,
        snackbarMessage = "",
        onSnackbarDismissed = { }
    )
}
