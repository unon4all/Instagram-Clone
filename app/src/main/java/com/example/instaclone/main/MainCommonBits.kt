package com.example.instaclone.main

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
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