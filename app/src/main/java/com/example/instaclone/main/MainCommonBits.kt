package com.example.instaclone.main

import android.widget.Toast
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.instaclone.DestinationScreen
import com.example.instaclone.IgViewModel
import com.example.instaclone.R
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

@Composable
fun CheckedSignIn(vm: IgViewModel, navController: NavController) {
    val alreadySignedIn = remember { mutableStateOf(false) }
    val signInState = remember { mutableStateOf(vm.isUserSignedIn()) }

    LaunchedEffect(signInState.value) {
        if (signInState.value && !alreadySignedIn.value) {
            alreadySignedIn.value = true
            navController.navigate(DestinationScreen.Home.createRoute(0)) {
                popUpTo(navController.graph.startDestinationId) {
                    saveState = true
                }
                launchSingleTop = true
            }
        }
    }
}


@Composable
fun CommonImage(
    modifier: Modifier = Modifier,
    url: String?,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Crop
) {
    val defaultPainter = painterResource(id = R.drawable.ic_person)

    AsyncImage(
        model = url,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
        placeholder = defaultPainter,
        error = defaultPainter,
        fallback = defaultPainter,
        alignment = Alignment.Center
    )
}

@Composable
fun CommonDivider() {

    HorizontalDivider(
        color = Color.LightGray,
        thickness = 1.dp,
        modifier = Modifier
            .alpha(0.3f)
            .padding(top = 8.dp, bottom = 8.dp)
    )
}


private enum class LikeIconSize {
    SMALL, LARGE
}

@Composable
fun LikeAnimation(like: Boolean = true) {
    var sizeState by remember { mutableStateOf(LikeIconSize.SMALL) }
    val transition = updateTransition(targetState = sizeState, label = "")
    val size by transition.animateDp(label = "", transitionSpec = {
        spring(
            dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow
        )
    }) { state ->
        when (state) {
            LikeIconSize.SMALL -> 0.dp
            LikeIconSize.LARGE -> 150.dp
        }
    }

    Image(
        painter = painterResource(
            id = if (like) R.drawable.baseline_favorite_24 else R.drawable.baseline_favorite_border_24
        ),
        contentDescription = null,
        modifier = Modifier.size(size = size),
        colorFilter = ColorFilter.tint(if (like) Color.Red else Color.Gray)
    )
    LaunchedEffect(Unit) {
        sizeState = LikeIconSize.LARGE
    }
}