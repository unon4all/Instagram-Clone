package com.example.instaclone.main

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.instaclone.DestinationScreen
import com.example.instaclone.IgViewModel
import com.example.instaclone.R
import com.example.instaclone.data.PostData

@Composable
fun MyPostScreen(modifier: Modifier = Modifier, vm: IgViewModel, navController: NavController) {

    val postData by vm.posts.collectAsState()

    val newPostImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                val encoded = Uri.encode(it.toString())
                val route = DestinationScreen.NewPost.createRoute(encoded)
                navController.navigate(route)
            }
        })

    val userData by vm.userData.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            ProfileImage(
                imgUrl = userData?.imgUrl,
                onClick = { newPostImageLauncher.launch("image/*") })
            Text(
                text = "15\nPosts",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            )
            Text(
                text = "15\nFollowers",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            )
            Text(
                text = "15\nFollowing",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            )
        }
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = userData?.name.orEmpty(), fontWeight = FontWeight.Bold
            )
            Text(
                text = userData?.userName?.let { "@$it" }.orEmpty()
            )
            Text(
                text = userData?.bio.orEmpty()
            )
        }
        OutlinedButton(
            onClick = {
                navigateTo(
                    navController = navController, destination = DestinationScreen.Profile
                )
            },
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.Transparent
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 0.dp, pressedElevation = 0.dp, disabledElevation = 0.dp
            ),
            shape = RoundedCornerShape(32.dp)
        ) {
            Text(text = "Edit Profile", color = Color.Black)
        }
        Text(
            text = "My Posts", modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        PostList(
            postList = postData,
            onPostClick = { postData ->
                navController.navigate(DestinationScreen.SinglePost.createRoute(postData = postData))
            },
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxSize()
        )
    }
}


@Composable
fun PostList(
    modifier: Modifier = Modifier, postList: List<PostData>, onPostClick: (PostData) -> Unit
) {
    if (postList.isEmpty()) {
        Column(
            modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.Center
        ) {
            Text(text = "No posts available")
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 128.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = modifier
        ) {
            items(items = postList) { post ->
                PostImage(imageUri = post.postImage,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(4.dp))
                        .clickable {
                            onPostClick(post)
                        })
            }
        }
    }
}


@Composable
fun PostImage(imageUri: String?, modifier: Modifier) {
    Box(modifier = modifier) {
        var customModifier = Modifier.fillMaxSize()

        if (imageUri == null) {
            customModifier = modifier.clickable(enabled = false) { }
        }

        CommonImage(
            url = imageUri,
            modifier = customModifier,
            contentScale = ContentScale.Fit,
        )
    }
}


@Composable
fun ProfileImage(imgUrl: String?, onClick: () -> Unit) {
    Box(
        modifier = Modifier.padding(top = 16.dp)
    ) {
        CommonImage(
            url = imgUrl,
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .border(2.dp, Color.LightGray, CircleShape),
        )
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .clip(CircleShape)
                .size(32.dp)
                .align(Alignment.BottomEnd),
            colors = IconButtonDefaults.iconButtonColors(containerColor = Color.LightGray)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_add), contentDescription = null
            )
        }
    }
}