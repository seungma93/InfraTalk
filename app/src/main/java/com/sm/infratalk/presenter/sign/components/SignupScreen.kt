import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.sm.infratalk.R
import coil.compose.AsyncImage

@Composable
fun SignUpScreen(
    isLoading: Boolean,
    onSignUpClick: (email: String, password: String, passwordCheck: String, nickname: String) -> Unit,
    onProfileImageClick: () -> Unit,
    profileImageUri: String? = null
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordCheck by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // 프로필 이미지
            if (profileImageUri != null) {
                AsyncImage(
                    model = profileImageUri,
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(200.dp)
                        .clip(CircleShape)
                        .clickable { onProfileImageClick() },
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.ic_baseline_person_24),
                    contentDescription = "Default Profile",
                    modifier = Modifier
                        .size(200.dp)
                        .clip(CircleShape)
                        .clickable { onProfileImageClick() }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 이메일 입력
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("이메일") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Email, "Email") }
            )

            Text(
                text = "인증 메일이 전송됩니다.",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 비밀번호 입력
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("비밀번호") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Lock, "Password") },
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 비밀번호 확인
            OutlinedTextField(
                value = passwordCheck,
                onValueChange = { passwordCheck = it },
                label = { Text("비밀번호 확인") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Lock, "Password Check") },
                visualTransformation = PasswordVisualTransformation()
            )

            Text(
                text = "비밀번호는 6자리 이상 필요합니다.",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 닉네임 입력
            OutlinedTextField(
                value = nickname,
                onValueChange = { nickname = it },
                label = { Text("닉네임") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Person, "Nickname") }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 회원가입 버튼
            Button(
                onClick = { onSignUpClick(email, password, passwordCheck, nickname) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("회원가입")
            }
        }

        // 로딩 인디케이터
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}
