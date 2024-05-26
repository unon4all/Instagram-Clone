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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.instaclone.IgViewModel
import com.example.instaclone.R


@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: IgViewModel,
) {

    // State for each TextField
    val (usernameTextFieldValue, onUsernameChange) = rememberTextFieldState()
    val (emailTextFieldValue, onEmailChange) = rememberTextFieldState()
    val (passwordTextFieldValue, onPasswordChange) = rememberTextFieldState()

    SignUpContent(
        modifier = modifier,
        usernameTextFieldValue = usernameTextFieldValue,
        onUsernameChange = onUsernameChange,
        emailTextFieldValue = emailTextFieldValue,
        onEmailChange = onEmailChange,
        passwordTextFieldValue = passwordTextFieldValue,
        onPasswordChange = onPasswordChange
    )
}

@Composable
fun SignUpContent(
    modifier: Modifier,
    usernameTextFieldValue: TextFieldValue,
    onUsernameChange: (TextFieldValue) -> Unit,
    emailTextFieldValue: TextFieldValue,
    onEmailChange: (TextFieldValue) -> Unit,
    passwordTextFieldValue: TextFieldValue,
    onPasswordChange: (TextFieldValue) -> Unit
) {
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
                    .align(CenterHorizontally)
            )

            Text(
                text = "Sign Up",
                modifier = Modifier
                    .align(CenterHorizontally)
                    .padding(16.dp),
                fontSize = 30.sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold
            )

            UsernameTextField(
                value = usernameTextFieldValue, onValueChange = onUsernameChange
            )

            EmailTextField(
                value = emailTextFieldValue, onValueChange = onEmailChange
            )

            PassTextField(
                value = passwordTextFieldValue, onValueChange = onPasswordChange
            )

            Button(onClick = { /*TODO*/ }, modifier = Modifier.padding(16.dp)) {
                Text(text = "SIGN UP")
            }

            ClickableText(text = AnnotatedString(
                text = "Already have an account? Sign in",
                spanStyle = SpanStyle(color = Color.White)
            ), modifier = Modifier.padding(8.dp), onClick = {})
        }
    }
}

@Composable
fun UsernameTextField(value: TextFieldValue, onValueChange: (TextFieldValue) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Username") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        singleLine = true
    )
}

@Composable
fun EmailTextField(value: TextFieldValue, onValueChange: (TextFieldValue) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Email") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        singleLine = true
    )
}

@Composable
fun PassTextField(value: TextFieldValue, onValueChange: (TextFieldValue) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Password") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        singleLine = true,
        visualTransformation = PasswordVisualTransformation()
    )
}

@Composable
fun rememberTextFieldState(): Pair<TextFieldValue, (TextFieldValue) -> Unit> {
    var text by rememberSaveable { mutableStateOf("") }
    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(text, TextRange(text.length)))
    }

    val onValueChange: (TextFieldValue) -> Unit = { newValue ->
        textFieldValue = newValue
        text = newValue.text
    }

    return textFieldValue to onValueChange
}