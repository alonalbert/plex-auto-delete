package com.alonalbert.pad.app.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alonalbert.pad.app.R
import com.alonalbert.pad.app.ui.components.ButtonComponent
import com.alonalbert.pad.app.ui.components.HeadingTextComponent
import com.alonalbert.pad.app.ui.components.MyTextFieldComponent
import com.alonalbert.pad.app.ui.components.NormalTextComponent
import com.alonalbert.pad.app.ui.components.PasswordTextFieldComponent
import com.alonalbert.pad.app.ui.login.LoginViewModel.LoginInfo

@Composable
fun LoginScreen(
    onLoggedIn: () -> Unit,
) {
    val viewModel: LoginViewModel = hiltViewModel()

    val loginState by viewModel.loginInfo.collectAsStateWithLifecycle(null)

    LoginScreenContent(loginState) { server, username, password ->
        viewModel.saveLoginInfo(server, username, password)
        onLoggedIn()
    }
}

@Composable
fun LoginScreenContent(
    loginInfo: LoginInfo?,
    onConnectClick: (String, String, String) -> Unit,
) {
    var server by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    if (loginInfo != null) {
        server = loginInfo.server
        username = loginInfo.username
        password = loginInfo.password
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(28.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {

                Spacer(modifier = Modifier.height(20.dp))

                NormalTextComponent(value = stringResource(id = R.string.connect))
                HeadingTextComponent(value = stringResource(id = R.string.welcome))
                Spacer(modifier = Modifier.height(20.dp))

                MyTextFieldComponent(
                    text = server,
                    labelValue = stringResource(id = R.string.server),
                    painterResource(id = R.drawable.server),
                    onTextChanged = { server = it },
                    isError = server.isBlank()
                )

                MyTextFieldComponent(
                    text = username,
                    labelValue = stringResource(id = R.string.username),
                    painterResource(id = R.drawable.username),
                    onTextChanged = { username = it },
                    isError = username.isBlank()
                )

                PasswordTextFieldComponent(
                    text = password,
                    labelValue = stringResource(id = R.string.password),
                    painterResource(id = R.drawable.lock),
                    onTextChanged = { password = it },
                    isError = password.isBlank()
                )

                Spacer(modifier = Modifier.height(40.dp))
                ButtonComponent(
                    value = stringResource(id = R.string.connect),
                    onButtonClicked = {
                        onConnectClick(server, username, password)
                    },
                    isEnabled = server.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty()
                )

            }

        }
    }
}

@Preview
@Composable
fun PreviewLoginScreen() {
    LoginScreenContent(loginInfo = LoginInfo(), onConnectClick = { _, _, _ -> })
}