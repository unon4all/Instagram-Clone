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
            arguments = listOf(navArgument("postId") { type = NavType.StringType },
                navArgument("userId") { type = NavType.StringType },
                navArgument("userName") { type = NavType.StringType },
                navArgument("userImage") { type = NavType.StringType },
                navArgument("postImage") { type = NavType.StringType },
                navArgument("postDescription") { type = NavType.StringType },
                navArgument("postTime") { type = NavType.LongType })
        ) { navBackStackEntry ->
            val postId = Uri.decode(navBackStackEntry.arguments?.getString("postId"))
            val userId = Uri.decode(navBackStackEntry.arguments?.getString("userId"))
            val userName = Uri.decode(navBackStackEntry.arguments?.getString("userName"))
            val userImage = Uri.decode(navBackStackEntry.arguments?.getString("userImage"))
            val postImage = Uri.decode(navBackStackEntry.arguments?.getString("postImage"))
            val postDescription =
                Uri.decode(navBackStackEntry.arguments?.getString("postDescription"))
            val postTime = navBackStackEntry.arguments?.getLong("postTime")

            val postData = PostData(
                postId = postId,
                userId = userId,
                userName = userName,
                userImage = userImage,
                postImage = postImage,
                postDescription = postDescription,
                postTime = postTime
            )

            SinglePostScreen(
                vm = vm,
                modifier = Modifier.padding(paddingValues),
                navController = navController,
                postData = postData
            )
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

    data object SinglePost :
        DestinationScreen("singlepost/{postId}/{userId}/{userName}/{userImage}/{postImage}/{postDescription}/{postTime}") {
        fun createRoute(postData: PostData) =
            "singlepost/" + "${Uri.encode(postData.postId)}/" + "${Uri.encode(postData.userId)}/" + "${
                Uri.encode(postData.userName)
            }/" + "${Uri.encode(postData.userImage)}/" + "${Uri.encode(postData.postImage)}/" + "${
                Uri.encode(
                    postData.postDescription
                )
            }/" + "${postData.postTime}"
    }
}
