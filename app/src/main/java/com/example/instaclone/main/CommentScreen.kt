package com.example.instaclone.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.instaclone.IgViewModel
import com.example.instaclone.data.CommentData

@Composable
fun CommentScreen(
    modifier: Modifier = Modifier, navController: NavController, postId: String, vm: IgViewModel,
) {

    var commentText by rememberSaveable {
        mutableStateOf("")
    }
    val focusManager = LocalFocusManager.current

    val comments by vm.commentsData.collectAsState()

    // Display the comments for the specified post
    Column(modifier.fillMaxSize()) {

        if (comments.isEmpty()) {
            Text("No comments yet")
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(comments) { comment ->
                    CommentRow(comment)
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = commentText,
                onValueChange = { commentText = it },
                modifier = Modifier.weight(1f),
                label = { Text("Add a comment") },
            )

            Button(onClick = {
                vm.createComment(postId, commentText)
                commentText = ""
                focusManager.clearFocus()
            }, modifier = Modifier.padding(start = 8.dp)) {
                Text("Comments")
            }
        }
    }
}

@Composable
fun CommentRow(comment: CommentData?) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(comment?.userName ?: "", fontWeight = FontWeight.Bold)
        Text(comment?.comment ?: "", modifier = Modifier.padding(start = 8.dp))
    }
}