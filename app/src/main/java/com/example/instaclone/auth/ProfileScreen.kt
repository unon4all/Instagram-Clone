package com.example.instaclone.auth

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.instaclone.DestinationScreen
import com.example.instaclone.IgViewModel
import com.example.instaclone.main.CommonDivider
import com.example.instaclone.main.CommonImage
import com.example.instaclone.main.navigateTo

@Composable
fun ProfileScreen(navController: NavController, vm: IgViewModel, modifier: Modifier) {

    val userData by vm.userData.collectAsState()
    var name by rememberSaveable { mutableStateOf(userData?.name) }
    var username by rememberSaveable { mutableStateOf(userData?.userName) }
    var bio by rememberSaveable { mutableStateOf(userData?.bio) }

    ProfileContent(vm = vm,
        name = name,
        username = username,
        bio = bio,
        onNameChange = { name = it },
        onUsernameChange = { username = it },
        onBioChange = { bio = it },
        onBack = {
            navController.navigate(DestinationScreen.Home.createRoute(2))
        },
        onSave = { vm.updateUserData(name, username, bio) },
        onLogout = {
            vm.logout()
            navigateTo(navController, DestinationScreen.Login)
        },
        modifier = modifier
    )
}


@Composable
fun ProfileContent(
    modifier: Modifier = Modifier,
    vm: IgViewModel,
    name: String?,
    username: String?,
    bio: String?,
    onNameChange: (String) -> Unit = {},
    onUsernameChange: (String) -> Unit = {},
    onBioChange: (String) -> Unit = {},
    onSave: () -> Unit = {},
    onBack: () -> Unit = {},
    onLogout: () -> Unit = {},
) {

    val scrollState = rememberScrollState()
    val imageUrl = vm.userData.collectAsState().value?.imgUrl

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            TextButton(onClick = onBack) {
                Text("Back")
            }
            TextButton(onClick = onSave) {
                Text("Save")
            }
        }

        CommonDivider()

        ProfileImage(imageUrl = imageUrl, vm = vm)

        CommonDivider()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Name", modifier = Modifier.width(100.dp))
            OutlinedTextField(
                value = name ?: "", onValueChange = onNameChange, colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ), singleLine = true
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Username", modifier = Modifier.width(100.dp))
            OutlinedTextField(
                value = username ?: "",
                onValueChange = onUsernameChange,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                singleLine = true
            )
        }


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Bio", modifier = Modifier.width(100.dp))
            OutlinedTextField(
                value = bio ?: "", onValueChange = onBioChange, colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ), modifier = Modifier.height(150.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            TextButton(onClick = onLogout) {
                Text("Logout")
            }
        }
    }
}


@Composable
fun ProfileImage(imageUrl: String?, vm: IgViewModel) {

    val launcher =
        rememberLauncherForActivityResult(contract = androidx.activity.result.contract.ActivityResultContracts.GetContent(),
            onResult = { uri ->
                uri?.let {
                    vm.uploadProfileImage(it)
                }
            })

    Box(modifier = Modifier.height(IntrinsicSize.Min)) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .clickable {
                    launcher.launch("image/*")
                }, horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CommonImage(
                url = imageUrl,
                modifier = Modifier
                    .size(180.dp)
                    .clip(CircleShape)
                    .border(1.dp, Color.LightGray, CircleShape),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Change Profile Picture")
        }
    }
}