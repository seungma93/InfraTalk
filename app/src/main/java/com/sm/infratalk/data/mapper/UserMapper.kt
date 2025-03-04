import com.sm.infratalk.data.model.response.user.UserResponse
import com.sm.infratalk.domain.user.entity.UserEntity


fun UserResponse.toEntity(): UserEntity {
    return UserEntity(
        email = email.orEmpty(),
        nickname = nickname.orEmpty(),
        image = image
    )
}
