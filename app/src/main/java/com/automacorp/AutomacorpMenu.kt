package com.automacorp

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.automacorp.ui.theme.AutomacorpTheme

fun goToRoomList(context: Context) {
    val intent = Intent(context, RoomListActivity::class.java)
    context.startActivity(intent)
}

fun sendEmail(context: Context) {
    val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:zlpkhr@icloud.com"))
    context.startActivity(intent)
}

fun openGithub(context: Context) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/zlpkhr"))
    context.startActivity(intent)
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AutomacorpTopAppBar(
    title: String? = null,
    returnAction: (() -> Unit)? = null,
    goToRoomList: (() -> Unit)? = null,
    sendEmail: (() -> Unit)? = null,
    openGithub: (() -> Unit)? = null
) {
    val colors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        titleContentColor = MaterialTheme.colorScheme.primary,
    )

    val actions: @Composable RowScope.() -> Unit = {
        if (goToRoomList != null) {
            IconButton(onClick = goToRoomList) {
                Icon(
                    painter = painterResource(R.drawable.ic_action_rooms),
                    contentDescription = stringResource(R.string.app_go_room_description)
                )
            }
        }
        if (sendEmail != null) {
            IconButton(onClick = sendEmail) {
                Icon(
                    painter = painterResource(R.drawable.ic_action_mail),
                    contentDescription = stringResource(R.string.app_go_mail_description)
                )
            }
        }
        if (openGithub != null) {
            IconButton(onClick = openGithub) {
                Icon(
                    painter = painterResource(R.drawable.ic_action_github),
                    contentDescription = stringResource(R.string.app_go_github_description)
                )
            }
        }
    }
    if (title == null) {
        TopAppBar(
            title = { Text("") },
            colors = colors,
            actions = actions
        )
    } else {
        MediumTopAppBar(
            title = { Text(title) },
            colors = colors,
            navigationIcon = {
                if (returnAction != null) {
                    IconButton(onClick = returnAction) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.app_go_back_description)
                        )
                    }
                }
            },
            actions = actions
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AutomacorpTopAppBarHomePreview() {
    AutomacorpTheme {
        AutomacorpTopAppBar(null)
    }
}

@Preview(showBackground = true)
@Composable
fun AutomacorpTopAppBarPreview() {
    AutomacorpTheme {
        AutomacorpTopAppBar("A page")
    }
}
