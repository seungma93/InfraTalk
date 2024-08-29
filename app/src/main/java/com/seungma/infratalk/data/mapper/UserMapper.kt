import com.seungma.infratalk.data.model.response.user.UserResponse
import com.seungma.infratalk.domain.user.entity.UserEntity


fun UserResponse.toEntity(): UserEntity {
    return UserEntity(
        email = email.orEmpty(),
        nickname = nickname.orEmpty(),
        image = image
    )
}
