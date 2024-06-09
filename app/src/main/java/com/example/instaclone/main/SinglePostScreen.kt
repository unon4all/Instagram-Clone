package com.example.instaclone.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.instaclone.IgViewModel
import com.example.instaclone.data.PostData

@Composable
fun SinglePostScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    vm: IgViewModel,
    postData: PostData,
) {

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Single Post Screen : ${postData.postDescription}")
    }
}