package com.example.instaclone.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.instaclone.IgViewModel
import com.example.instaclone.R
import com.example.instaclone.data.PostData
import com.example.instaclone.data.UserData

@Composable
fun SinglePostScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    vm: IgViewModel,
    postData: PostData,
) {


    val comments by vm.commentsData.collectAsState()

    LaunchedEffect(key1 = Unit) {
        vm.getComments(postData.postId ?: "")
    }

    postData.userId?.let {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(8.dp)
        ) {
            TextButton(onClick = { navController.popBackStack() }) {
                Text(text = "Back")
            }

            CommonDivider()

            SinglePostDisplay(
                navController = navController,
                vm = vm,
                postData = postData,
                nbComments = comments.size
            )
        }
    }
}


@Composable
fun SinglePostDisplay(
    navController: NavController, vm: IgViewModel, postData: PostData, nbComments: Int
) {

    val userData by vm.userData.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            UserImageCard(userData)

            Text(text = postData.userName ?: "")
            Text(text = ".", modifier = Modifier.padding(8.dp))

            if (userData?.userId == postData.userId) {
                // Current User Post don't show anything
            } else if (userData?.following?.contains(postData.userId) == true) {
                TextButton(
                    onClick = { vm.onFollowClick(postData.userId ?: "") },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Gray)
                ) {
                    Text(text = "Following")
                }
            } else {
                TextButton(
                    onClick = { vm.onFollowClick(postData.userId ?: "") },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Blue)
                ) {
                    Text(text = "Follow")
                }
            }
        }
    }

    Box {
        val customModifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 150.dp)

        CommonImage(
            url = postData.postImage,
            modifier = customModifier,
            contentScale = ContentScale.FillWidth
        )
    }

    Row(
        modifier = Modifier.padding(0.dp, 8.dp, 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.baseline_favorite_24),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = Color.Red
        )
        Text(
            text = " ${postData.postLikes?.size ?: 0} likes",
            modifier = Modifier.padding(start = 0.dp)
        )
    }


    Row(modifier = Modifier.padding(8.dp)) {
        Text(
            text = postData.userName ?: "", fontWeight = FontWeight.Bold
        )
        Text(text = postData.postDescription ?: "", modifier = Modifier.padding(start = 8.dp))
    }

    Row(modifier = Modifier.padding(horizontal = 8.dp)) {
        Text(text = "$nbComments comments", color = Color.Gray, modifier = Modifier.clickable {
            postData.postId?.let {
                navController.navigate("comments/${it}")
            }
        })
    }
}

@Composable
fun UserImageCard(userData: UserData?) {
    Card(
        shape = CircleShape, modifier = Modifier
            .padding(8.dp)
            .size(32.dp)
    ) {
        userData?.imgUrl?.let {
            CommonImage(url = it)
        }
    }
}