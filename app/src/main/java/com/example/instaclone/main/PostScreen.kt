package com.example.instaclone.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.instaclone.IgViewModel
import com.example.instaclone.R

@Composable
fun MyPostScreen(modifier: Modifier = Modifier, vm: IgViewModel) {
    val userData by vm.userData.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        Row(modifier = Modifier.padding(16.dp)) {
            ProfileImage(imgUrl = userData?.imgUrl, onClick = {})
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
            onClick = { /* TODO */ },
            modifier = Modifier
                .padding(16.dp)
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
            text = "My Posts", modifier = Modifier
                .padding(16.dp)
                .weight(1f)
        )
    }
}


@Composable
fun ProfileImage(imgUrl: String?, onClick: () -> Unit) {
    Box(
        modifier = Modifier.padding(top = 16.dp)
    ) {
        CommonImage(
            url = imgUrl, modifier = Modifier
                .padding(8.dp)
                .size(80.dp)
        )
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .clip(CircleShape)
                .size(32.dp)
                .align(Alignment.BottomEnd),
            colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Blue)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_add), contentDescription = null
            )
        }
    }
}