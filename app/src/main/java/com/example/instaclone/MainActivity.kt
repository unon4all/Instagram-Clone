package com.example.instaclone

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.instaclone.auth.LoginScreen
import com.example.instaclone.auth.ProfileScreen
import com.example.instaclone.auth.SignUpScreen
import com.example.instaclone.data.PostData
import com.example.instaclone.main.BottomNavigationCompose
import com.example.instaclone.main.NewPostScreen
import com.example.instaclone.main.NotificationMessage
import com.example.instaclone.main.SinglePostScreen
import com.example.instaclone.ui.theme.InstaCloneTheme
import com.google.gson.Gson
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
                    InstaCloneApp(paddingValues = paddingValues)
                }
            }
        }
    }
}


@Composable
fun InstaCloneApp(paddingValues: PaddingValues) {
    val vm = hiltViewModel<IgViewModel>()
    val navController = rememberNavController()

    NotificationMessage(vm = vm)

    NavHost(navController = navController, startDestination = DestinationScreen.Signup.route) {
        composable(DestinationScreen.Signup.route) {
            SignUpScreen(
                navController = navController, vm = vm, modifier = Modifier.padding(paddingValues)
            )
        }

        composable(DestinationScreen.Login.route) {
            LoginScreen(
                navController = navController, vm = vm, modifier = Modifier.padding(paddingValues)
            )
        }

        composable(DestinationScreen.Home.route) {
            BottomNavigationCompose(
                vm = vm, modifier = Modifier.padding(paddingValues), navController = navController
            )
        }

        composable(DestinationScreen.Profile.route) {
            ProfileScreen(
                vm = vm, modifier = Modifier.padding(paddingValues), navController = navController
            )
        }

        composable(DestinationScreen.NewPost.route) { navBackStackEntry ->
            val imageUri = navBackStackEntry.arguments?.getString("imageUri")
            imageUri?.let {
                NewPostScreen(
                    encodedUri = it,
                    vm = vm,
                    modifier = Modifier.padding(paddingValues),
                    navController = navController
                )
            }
        }

        composable(
            route = DestinationScreen.SinglePost.route,
            arguments = listOf(navArgument("postData") { type = NavType.StringType })
        ) { navBackStackEntry ->
            val postDataJson = navBackStackEntry.arguments?.getString("postData")
            val postData = Gson().fromJson(postDataJson, PostData::class.java)

            postData?.let {
                SinglePostScreen(
                    vm = vm, modifier = Modifier, navController = navController, postData = it
                )
            }
        }
    }
}


sealed class DestinationScreen(val route: String) {
    data object Signup : DestinationScreen("signup")
    data object Login : DestinationScreen("login")
    data object Home : DestinationScreen("home?selectedIndex={selectedIndex}") {
        fun createRoute(selectedIndex: Int) = "home?selectedIndex=$selectedIndex"
    }

    data object Profile : DestinationScreen("profile")
    data object NewPost : DestinationScreen("newpost/{imageUri}") {
        fun createRoute(imageUri: String) = "newpost/$imageUri"
    }

    data object SinglePost : DestinationScreen("singlepost/{postData}") {
        fun createRoute(postData: PostData): String {
            return "singlepost/${Uri.encode(Gson().toJson(postData))}"
        }
    }
}
