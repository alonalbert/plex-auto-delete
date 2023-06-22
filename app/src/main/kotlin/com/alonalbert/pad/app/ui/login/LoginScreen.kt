package com.alonalbert.pad.app.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alonalbert.pad.app.R

@Composable
fun LoginScreen(
    onConnected: () -> Unit,
) {
    val viewModel: LoginViewModel = hiltViewModel()

    val loginState by viewModel.loginState.collectAsStateWithLifecycle(null)
    if (loginState != null) {
        LaunchedEffect("Connected") { onConnected() }
        return
    }

    val server = remember { mutableStateOf(TextFieldValue()) }
    val username = remember { mutableStateOf(TextFieldValue()) }
    val password = remember { mutableStateOf(TextFieldValue()) }

    LoginScreenContent(server, username, password) {
        viewModel.setLoginState(server.value.text, username.value.text, password.value.text)
    }
}

@Composable
fun LoginScreenContent(
    server: MutableState<TextFieldValue>,
    username: MutableState<TextFieldValue>,
    password: MutableState<TextFieldValue>,
    onConnectClick: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        ClickableText(
            text = AnnotatedString("Sign up here"),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(20.dp),
            onClick = { },
            style = TextStyle(
                fontSize = 14.sp,
                textDecoration = TextDecoration.Underline,
                color = MaterialTheme.colorScheme.onPrimary
            )
        )
    }
    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(text = stringResource(R.string.connect), style = TextStyle(fontSize = 40.sp, fontFamily = FontFamily.Cursive))

        Spacer(modifier = Modifier.height(20.dp))
        TextField(
            label = { Text(text = stringResource(R.string.server)) },
            value = server.value,
            onValueChange = { server.value = it })

        Spacer(modifier = Modifier.height(20.dp))
        TextField(
            label = { Text(text = stringResource(R.string.username)) },
            value = username.value,
            onValueChange = { username.value = it })

        Spacer(modifier = Modifier.height(20.dp))
        TextField(
            label = { Text(text = stringResource(R.string.password)) },
            value = password.value,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            onValueChange = { password.value = it })

        Spacer(modifier = Modifier.height(20.dp))
        Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
            Button(
                onClick = onConnectClick,
                enabled = server.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty(),
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = stringResource(R.string.connect))
            }
        }
    }
}

private fun MutableState<TextFieldValue>.isNotEmpty() = value.text.isNotEmpty()

@Preview
@Composable
fun PreviewLoginScreen() {
    LoginScreenContent(
        server = remember { mutableStateOf(TextFieldValue()) },
        username = remember { mutableStateOf(TextFieldValue()) },
        password = remember { mutableStateOf(TextFieldValue()) },
    ) {}
}