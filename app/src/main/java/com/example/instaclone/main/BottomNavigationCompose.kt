package com.example.instaclone.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import com.example.instaclone.IgViewModel


@Immutable
data class BottomNavigationItemCompose(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)

@Composable
fun BottomNavigationCompose(
    vm: IgViewModel, modifier: Modifier = Modifier, navController: NavController
) {

    val items = listOf(
        BottomNavigationItemCompose(
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
        ),
        BottomNavigationItemCompose(
            selectedIcon = Icons.Filled.Search,
            unselectedIcon = Icons.Outlined.Search,
        ),
        BottomNavigationItemCompose(
            selectedIcon = Icons.Filled.Person,
            unselectedIcon = Icons.Outlined.Person,
        ),
    )

    var selectedIndexDemo by rememberSaveable {
        mutableIntStateOf(
            navController.currentBackStackEntry?.arguments?.getString("selectedIndex")
                ?.toIntOrNull() ?: 0
        )
    }


    Scaffold(bottomBar = {
        NavigationBar(modifier = modifier) {
            items.forEachIndexed { index, item ->
                NavigationBarItem(selected = selectedIndexDemo == index,
                    onClick = { selectedIndexDemo = index },
                    icon = {
                        Icon(
                            imageVector = if (selectedIndexDemo == index) item.selectedIcon else item.unselectedIcon,
                            contentDescription = null
                        )
                    })
            }
        }
    }) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when (selectedIndexDemo) {
                0 -> FeedScreen(navController = navController, vm = vm)
                1 -> SearchScreen(navController = navController, vm = vm)
                else -> MyPostScreen(vm = vm, navController = navController)
            }
        }
    }
}