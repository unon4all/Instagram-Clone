package com.example.instaclone.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.instaclone.DestinationScreen
import com.example.instaclone.IgViewModel
import com.example.instaclone.R
import com.example.instaclone.data.UiState
import com.example.instaclone.main.CheckedSignIn
import com.example.instaclone.main.CommonProgressSpinner
import com.example.instaclone.main.navigateTo

@Composable
fun LoginScreen(modifier: Modifier = Modifier, navController: NavController, vm: IgViewModel) {

    val (emailTextFieldValue, onEmailChange) = rememberTextFieldState()
    val (passwordTextFieldValue, onPasswordChange) = rememberTextFieldState()

    val uiState by vm.uiState.collectAsState()

    CheckedSignIn(navController = navController, vm = vm)

    SignInContent(
        modifier = modifier,
        navController = navController,
        emailTextFieldValue = emailTextFieldValue,
        onEmailChange = onEmailChange,
        passwordTextFieldValue = passwordTextFieldValue,
        onPasswordChange = onPasswordChange,
        vm = vm,
        uiState = uiState,
    )
}


@Composable
fun SignInContent(
    modifier: Modifier = Modifier,
    navController: NavController,
    emailTextFieldValue: TextFieldValue,
    onEmailChange: (TextFieldValue) -> Unit,
    passwordTextFieldValue: TextFieldValue,
    onPasswordChange: (TextFieldValue) -> Unit,
    vm: IgViewModel,
    uiState: UiState,
) {

    val focusManager = LocalFocusManager.current

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(id = R.drawable.ig_logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .width(250.dp)
                    .padding(top = 50.dp)
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Text(
                text = "Login",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp),
                fontSize = 30.sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold
            )

            EmailTextField(
                value = emailTextFieldValue, onValueChange = onEmailChange
            )

            PassTextField(
                value = passwordTextFieldValue, onValueChange = onPasswordChange
            )

            Button(onClick = {
                focusManager.clearFocus(force = true)
                vm.onLogin(emailTextFieldValue.text, passwordTextFieldValue.text)
            }, modifier = Modifier.padding(16.dp)) {
                Text(text = "LOGIN")
            }

            ClickableText(text = AnnotatedString(
                text = "New here? Go to Signup ->",
                spanStyle = SpanStyle(color = Color.LightGray)
            ), modifier = Modifier.padding(8.dp), onClick = {
                navigateTo(navController, DestinationScreen.Signup)
            })
        }

        if (uiState is UiState.Loading) {
            CommonProgressSpinner()
        }
    }
}