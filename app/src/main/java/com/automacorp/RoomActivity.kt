package com.automacorp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.automacorp.model.RoomDto
import com.automacorp.ui.theme.AutomacorpTheme
import com.automacorp.viewmodel.RoomViewModel
import kotlinx.coroutines.launch
import kotlin.math.round

class RoomActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val param = intent.getStringExtra(MainActivity.ROOM_PARAM)
        val viewModel: RoomViewModel by viewModels()

        if (param != null) {
            val id = param.toDoubleOrNull()
            if (id != null) {
                viewModel.findRoom(id.toLong())
            } else {
                viewModel.findAll()
                lifecycleScope.launch {
                    lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        viewModel.roomsState.collect { roomList ->
                            val room =
                                roomList.rooms.find { it.name.equals(param, ignoreCase = true) }
                            if (room != null) {
                                viewModel.room = room
                            }
                        }
                    }
                }
            }
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
    val room = model.room
    LazyColumn(modifier = modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {


        item {
            OutlinedTextField(
                value = room?.name ?: "",
                onValueChange = { model.room = room?.copy(name = it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                placeholder = { Text(stringResource(R.string.act_room_name)) },
                label = { Text(stringResource(R.string.act_room_name)) }
            )
        }
        item {
            Text(
                text = stringResource(R.string.act_room_current_temperature),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
        item {

            Text(
                text = "${(room?.currentTemperature ?: "N/A")} °C",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        item {

            Text(
                text = stringResource(R.string.act_room_target_temperature),
                style = MaterialTheme.typography.labelSmall,
            )
        }

        item {
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
        }
        item {
            Text(
                text = (round((room?.targetTemperature ?: 18.0) * 10) / 10).toString(),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        if (model.room?.windows !== null) {
            items(model.room!!.windows, key = { it.id }) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    var text by remember { mutableStateOf(it.name) }
                    val ctx = LocalContext.current

                    OutlinedTextField(
                        value = text,

                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Done,
                                contentDescription = "Save",
                                modifier = Modifier.clickable {
                                    model.updateWindow(it.id, text)
                                    Toast.makeText(ctx, "Window name updated", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            )
                        },
                        onValueChange = { newName ->
                            text = newName

                        },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Window Name") }
                    )

                    Button(
                        onClick = {
                            model.switchWindow(it.id)
                        },
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(text = it.windowStatus.toString())
                    }

                    Button(
                        onClick = {
                            model.deleteWindow(it.id, it.roomId)
                        },
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(text = "X")
                    }
                }
            }
            item {
                Button(
                    onClick = {
                        room?.id?.let { model.createWindow(it) }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text("Add Window")
                }
            }
        }

        item {
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

