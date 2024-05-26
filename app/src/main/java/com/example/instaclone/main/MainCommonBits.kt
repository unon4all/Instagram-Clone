package com.example.instaclone.main

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.example.instaclone.DestinationScreen
import com.example.instaclone.IgViewModel
import com.example.instaclone.data.UiState


@Composable
fun NotificationMessage(vm: IgViewModel) {
    val notifyState by vm.popupNotification.collectAsState()
    val uiState by vm.uiState.collectAsState()
    val context = LocalContext.current


    notifyState?.getContentOrNull()?.let { notifyMessage ->
        Toast.makeText(context, notifyMessage, Toast.LENGTH_SHORT).show()
    }

    when (uiState) {
        is UiState.Loading -> Toast.makeText(LocalContext.current, "Loading...", Toast.LENGTH_SHORT)
            .show()

        is UiState.Success -> Toast.makeText(
            LocalContext.current, (uiState as UiState.Success).message, Toast.LENGTH_SHORT
        ).show()

        is UiState.Error -> { /* Error message is already shown via notifyState */
        }

        is UiState.Idle -> {
            Toast.makeText(context, "Idle", Toast.LENGTH_SHORT).show()
        }
    }
}

@Preview
@Composable
fun CommonProgressSpinner() {
    Row(
        modifier = Modifier
            .alpha(0.5f)
            .background(color = Color.LightGray)
            .fillMaxSize()
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
        )
    }
}


fun navigateTo(navController: NavController, destination: DestinationScreen) {
    navController.navigate(destination.route) {
        popUpTo(navController.graph.startDestinationId) {
            saveState = true
        }
        launchSingleTop = true
    }
}