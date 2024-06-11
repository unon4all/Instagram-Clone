package com.example.instaclone.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class PostData(
    val postId: String? = null,
    val userId: String? = null,
    val userName: String? = null,
    val userImage: String? = null,
    val postImage: String? = null,
    val postDescription: String? = null,
    val postTime: Long? = null,
    var postLikes: List<String>? = null,
    val postComments: List<String>? = null,
    val searchTerms: List<String>? = null,
    val following: List<String>? = null,
) : Parcelable