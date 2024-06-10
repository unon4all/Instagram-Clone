package com.example.instaclone.main

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.instaclone.IgViewModel
import com.google.gson.Gson

@Composable
fun SearchScreen(modifier: Modifier = Modifier, navController: NavController, vm: IgViewModel) {

    val searchedPosts by vm.searchPosts.collectAsState()
    var searchTerm by rememberSaveable { mutableStateOf("") }

    Column(modifier = modifier) {
        SearchBar(
            searchTerm = searchTerm,
            onSearch = { vm.searchPosts(searchTerm) },
            onSearchChange = { searchTerm = it },
        )
        PostList(
            postList = searchedPosts,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(8.dp),
        ) { post ->
            val postDataJson = Uri.encode(Gson().toJson(post))
            navController.navigate("singlepost/$postDataJson")
        }
    }
}

@Composable
fun SearchBar(
    searchTerm: String, onSearch: () -> Unit, onSearchChange: (String) -> Unit = {},
) {
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = searchTerm,
        onValueChange = onSearchChange,
        label = { Text("Search") },
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        shape = CircleShape,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search, keyboardType = KeyboardType.Text
        ),
        keyboardActions = KeyboardActions(onSearch = {
            onSearch()
            focusManager.clearFocus()
        }),
        maxLines = 1,
        singleLine = true,
        trailingIcon = {
            IconButton(onClick = {
                onSearch()
                focusManager.clearFocus()
            }) {
                Icon(imageVector = Icons.Default.Search, contentDescription = null)
            }
        },
    )
}