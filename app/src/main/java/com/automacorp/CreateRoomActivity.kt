package com.automacorp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.automacorp.model.RoomCommandDto
import com.automacorp.service.ApiServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

class CreateRoomActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val roomName = remember { mutableStateOf("") }
            val targetTemperature = remember { mutableFloatStateOf(18.0f) }
            val currentTemperature = remember { mutableFloatStateOf(18.0f) }
            val floor = remember { mutableStateOf("1") }
            val buildingId = remember { mutableStateOf("-10") }

            Scaffold(
                topBar = {
                    AutomacorpTopAppBar(
                        "Create Room",
                        returnAction = { finish() }
                    )
                },
                content = { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(16.dp)
                    ) {
                        OutlinedTextField(
                            value = roomName.value,
                            onValueChange = { roomName.value = it },
                            label = { Text("Room Name") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        )
                        Text(
                            text = "Current Temperature: ${
                                String.format(
                                    Locale.getDefault(),
                                    "%.1f",
                                    currentTemperature.floatValue
                                )
                            }°C",
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Slider(
                            value = currentTemperature.floatValue,
                            onValueChange = { currentTemperature.floatValue = it },
                            valueRange = 10f..28f,
                            steps = 0,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        )
                        Text(
                            text = "Target Temperature: ${
                                String.format(
                                    Locale.getDefault(),
                                    "%.1f",
                                    targetTemperature.floatValue
                                )
                            }°C",
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Slider(
                            value = targetTemperature.floatValue,
                            onValueChange = { targetTemperature.floatValue = it },
                            valueRange = 10f..28f,
                            steps = 0,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        )
                        OutlinedTextField(
                            value = floor.value,
                            onValueChange = { floor.value = it },
                            label = { Text("Floor") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = buildingId.value,
                            onValueChange = { buildingId.value = it },
                            label = { Text("Building ID") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        )
                        Button(
                            onClick = {
                                val command = RoomCommandDto(
                                    name = roomName.value,
                                    targetTemperature = targetTemperature.floatValue.toDouble()
                                        .let { Math.round(it * 10) / 10.0 },
                                    currentTemperature = currentTemperature.floatValue.toDouble()
                                        .let { Math.round(it * 10) / 10.0 },
                                    floor = floor.value.toIntOrNull() ?: 1,
                                    buildingId = buildingId.value.toLongOrNull() ?: -10
                                )

                                lifecycleScope.launch(Dispatchers.IO) {
                                    try {
                                        val response =
                                            ApiServices.roomsApiService.createRoom(command)
                                                .execute()
                                        if (response.isSuccessful) {
                                            launch(Dispatchers.Main) {
                                                Toast.makeText(
                                                    applicationContext,
                                                    "Room created!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                setResult(RESULT_OK)
                                                finish()
                                            }
                                        } else {
                                            launch(Dispatchers.Main) {
                                                Toast.makeText(
                                                    applicationContext,
                                                    "Failed to create room",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    } catch (e: Exception) {
                                        launch(Dispatchers.Main) {
                                            Toast.makeText(
                                                applicationContext,
                                                "An error occurred: ${e.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp)
                        ) {
                            Text("Save")
                        }
                    }
                }
            )
        }
    }
}
