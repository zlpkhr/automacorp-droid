package com.automacorp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.automacorp.model.RoomDto

class RoomViewModel: ViewModel() {
    var room by mutableStateOf <RoomDto?>(null)
}