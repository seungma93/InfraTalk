package com.seungma.infratalk.data.model.response.user

import com.google.gson.annotations.SerializedName

data class GetUserMeResponse(
    @SerializedName("users") val users: List<UsersResponse>?
)

data class UsersResponse(
    @SerializedName("localId") val localId: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("emailVerified") val emailVerified: Boolean?,
    @SerializedName("displayName") val displayName: String?,
    @SerializedName("providerUserInfo") val providerUserInfo: List<ProviderUserInfoResponse>?,
    @SerializedName("photoUrl") val photoUrl: String?,
    @SerializedName("passwordHash") val passwordHash: String?,
    @SerializedName("passwordUpdatedAt") val passwordUpdatedAt: Float?,
    @SerializedName("validSince") val validSince: String?,
    @SerializedName("disabled") val disabled: Boolean?,
    @SerializedName("lastLoginAt") val lastLoginAt: String?,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("customAuth") val customAuth: Boolean?


)

data class ProviderUserInfoResponse(
    @SerializedName("providerId") val providerId: String?,
    @SerializedName("displayName") val displayName: String?,
    @SerializedName("photoUrl") val photoUrl: String?,
    @SerializedName("federatedId") val federatedId: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("rawId") val rawId: String?,
    @SerializedName("screenName") val screenName: String?,
)

