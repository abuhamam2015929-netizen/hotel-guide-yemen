package com.hotelguide.yemen.ui.client.rooms

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.hotelguide.yemen.data.model.Room
import com.hotelguide.yemen.data.model.RoomStatus

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun RoomsScreen(
    hotelId: String,
    onBack: () -> Unit,
    onRoomClick: (Room) -> Unit,
    viewModel: RoomsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(hotelId) {
        viewModel.loadRooms(hotelId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("الغرف") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("←")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                is RoomsUiState.Loading -> CircularProgressIndicator()
                is RoomsUiState.Empty -> Text("لا توجد غرف متاحة حالياً بهذا الفندق")
                is RoomsUiState.Error -> Text(
                    "حدث خطأ: ${state.message}",
                    color = MaterialTheme.colorScheme.error
                )
                is RoomsUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(state.rooms) { room ->
                            RoomCard(room = room, onClick = { onRoomClick(room) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RoomCard(room: Room, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (room.images.isNotEmpty()) {
                AsyncImage(
                    model = room.images.first(),
                    contentDescription = room.roomNumber,
                    modifier = Modifier.fillMaxWidth().height(180.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            Text(text = "غرفة ${room.roomNumber}", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "${room.pricePerNight} ريال / الليلة")
            Spacer(modifier = Modifier.height(4.dp))
            StatusBadge(status = room.status)
        }
    }
}

@Composable
fun StatusBadge(status: RoomStatus) {
    val (label, color) = when (status) {
        RoomStatus.AVAILABLE -> "متاحة" to Color(0xFF2E7D32)
        RoomStatus.PENDING -> "قيد المراجعة" to Color(0xFFF9A825)
        RoomStatus.BOOKED -> "محجوزة" to Color(0xFFC62828)
    }
    Text(text = label, color = color, style = MaterialTheme.typography.labelLarge)
}
