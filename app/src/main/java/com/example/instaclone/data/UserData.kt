package com.example.instaclone.data

data class UserData(
    val userId: String? = null,
    val name: String? = null,
    val userName: String? = null,
    val imgUrl: String? = null,
    val bio: String? = null,
    val followers: Int? = null,
    val following: List<String>? = listOf(),
) {
    fun toMap() = mapOf(
        "userId" to userId,
        "name" to name,
        "userName" to userName,
        "imgUrl" to imgUrl,
        "bio" to bio,
    )
}
