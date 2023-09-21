import com.freetalk.data.model.response.UserResponse
import com.freetalk.domain.entity.UserEntity


fun UserResponse.toEntity(): UserEntity {
    return UserEntity(
        email = email.orEmpty(),
        nickname = nickname.orEmpty(),
        image = null
    )
}
