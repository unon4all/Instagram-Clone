package com.example.instaclone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.instaclone.auth.SignUpScreen
import com.example.instaclone.ui.theme.InstaCloneTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            InstaCloneTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { paddingValues ->
                    InstaCloneApp(modifier = Modifier.padding(paddingValues))
                }
            }
        }
    }
}


@Composable
fun InstaCloneApp(modifier: Modifier = Modifier) {
    val vm = hiltViewModel<IgViewModel>()
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = DestinationScreen.Signup.route) {
        composable(DestinationScreen.Signup.route) {
            SignUpScreen(navController = navController, viewModel = vm, modifier = modifier)
        }
    }

}


@Preview
@Composable
fun InstaCloneAppPreview() {
    InstaCloneTheme {
        InstaCloneApp()
    }
}


sealed class DestinationScreen(val route: String) {
    data object Signup : DestinationScreen("signup")
    data object Home : DestinationScreen("home")
    data object Profile : DestinationScreen("profile")
}