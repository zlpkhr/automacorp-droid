package com.automacorp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.automacorp.model.RoomDto
import com.automacorp.ui.theme.AutomacorpTheme
import com.automacorp.viewmodel.RoomViewModel
import kotlin.math.round

class RoomActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val param = intent.getStringExtra(MainActivity.ROOM_PARAM)?.toLongOrNull()
        val viewModel: RoomViewModel by viewModels()
        param?.let {
            viewModel.findRoom(it)
        }


        val onRoomSave: () -> Unit = {
            if (viewModel.room != null) {
                viewModel.updateRoom(viewModel.room!!.id, viewModel.room!!)
                Toast.makeText(
                    baseContext,
                    "Room ${viewModel.room!!.name} was updated",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        }

        val navigateBack: () -> Unit = {
            finish()
        }

        enableEdgeToEdge()
        setContent {
            AutomacorpTheme {
                val roomState = viewModel.room

                Scaffold(
                    topBar = {
                        AutomacorpTopAppBar(
                            "Room",
                            navigateBack,
                            goToRoomList = { goToRoomList(this) },
                            sendEmail = { sendEmail(this) },
                            openGithub = { openGithub(this) }
                        )
                    },
                    floatingActionButton = {
                        if (roomState != null) {
                            RoomUpdateButton(onRoomSave)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    if (roomState != null) {
                        RoomDetail(viewModel, Modifier.padding(innerPadding), onDelete = {
                            viewModel.deleteRoom(roomState.id)
                            finish()
                        })
                    } else {
                        NoRoom(Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }
}

@Composable
fun RoomDetail(model: RoomViewModel, modifier: Modifier = Modifier, onDelete: () -> Unit) {
    Column(modifier = modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
        val room = model.room

        OutlinedTextField(
            value = room?.name ?: "",
            onValueChange = { model.room = room?.copy(name = it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            placeholder = { Text(stringResource(R.string.act_room_name)) },
            label = { Text(stringResource(R.string.act_room_name)) }
        )
        Text(
            text = stringResource(R.string.act_room_current_temperature),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = "${(room?.currentTemperature ?: "N/A")} °C",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = stringResource(R.string.act_room_target_temperature),
            style = MaterialTheme.typography.labelSmall,
        )
        Slider(
            value = room?.targetTemperature?.toFloat() ?: 18f,
            onValueChange = { model.room = room?.copy(targetTemperature = it.toDouble()) },
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.secondary,
                activeTrackColor = MaterialTheme.colorScheme.secondary,
                inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            valueRange = 10f..28f,
            steps = 0,

            )
        Text(
            text = (round((room?.targetTemperature ?: 18.0) * 10) / 10).toString(),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp)
        )


        Button(
            onClick = onDelete,

            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            if (model.room != null) {
                Text("Delete Room")
            } else {
                Text("How I am even rendered?")
            }
        }
    }

}


@Composable
fun NoRoom(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.act_room_none),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun RoomUpdateButton(onClick: () -> Unit) {
    ExtendedFloatingActionButton(
        onClick = { onClick() },
        icon = {
            Icon(
                Icons.Filled.Done,
                contentDescription = stringResource(R.string.act_room_save),
            )
        },
        text = { Text(text = stringResource(R.string.act_room_save)) }
    )
}

@Preview(showBackground = true)
@Composable
fun RoomDetailPreview() {
    AutomacorpTheme {
        val viewModel = RoomViewModel()
        viewModel.room = RoomDto(
            id = 1L,
            name = "Room EF 6.10",
            currentTemperature = 18.2,
            targetTemperature = 20.0,
            windows = emptyList()
        )

        RoomDetail(viewModel, onDelete = {})
    }
}

@Preview(showBackground = true)
@Composable
fun NoRoomPreview() {
    AutomacorpTheme {
        NoRoom()
    }
}