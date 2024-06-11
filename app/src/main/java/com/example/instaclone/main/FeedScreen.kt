package com.example.instaclone.main

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.instaclone.IgViewModel
import com.example.instaclone.data.PostData

@Composable
fun FeedScreen(modifier: Modifier = Modifier, vm: IgViewModel, navController: NavController) {

    val userData by vm.userData.collectAsState()
    val personalisedFeed by vm.postFeed.collectAsState()

    Log.d("FeedScreen", "FeedScreen: $personalisedFeed")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = Color.LightGray)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(Color.White)
        ) {
            UserImageCard(userData = userData)
        }
        GenericPostList(
            postFeed = personalisedFeed,
            navController = navController,
            vm = vm,
            currentUserId = userData?.userId ?: ""
        )
    }
}


@Composable
fun GenericPostList(
    modifier: Modifier = Modifier,
    postFeed: List<PostData>,
    navController: NavController,
    vm: IgViewModel,
    currentUserId: String
) {
    Box(modifier = modifier) {
        LazyColumn {
            items(postFeed) { postData ->
                PostContent(post = postData, vm = vm, currentUserId = currentUserId, onPostClick = {
                    navController.navigate("singlepost/${postData}")
                })
            }
        }
    }
}

@Composable
fun PostContent(
    post: PostData, vm: IgViewModel, currentUserId: String, onPostClick: () -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(4.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    shape = CircleShape, modifier = Modifier
                        .padding(4.dp)
                        .size(32.dp)
                ) {
                    CommonImage(
                        url = post.userImage, contentScale = ContentScale.Crop
                    )
                }
                Text(text = post.userName ?: "", modifier = Modifier.padding(4.dp))
            }

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CommonImage(
                    url = post.postImage,
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 150.dp),
                    contentScale = ContentScale.FillWidth
                )
            }
        }
    }
}