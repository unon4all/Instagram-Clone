package com.example.instaclone.data

import kotlinx.serialization.Serializable


@Serializable
data class PostData(
    val postId: String? = null,
    val userId: String? = null,
    val userName: String? = null,
    val userImage: String? = null,
    val postImage: String? = null,
    val postDescription: String? = null,
    val postTime: Long? = null
)
