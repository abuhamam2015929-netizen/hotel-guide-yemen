package com.hotelguide.yemen.ui.client.roomdetail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.hotelguide.yemen.data.model.Room
import com.hotelguide.yemen.data.model.RoomStatus

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun RoomDetailScreen(
    roomId: String,
    onBack: () -> Unit,
    onBookClick: (Room) -> Unit,
    viewModel: RoomDetailViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(roomId) {
        viewModel.loadRoom(roomId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("تفاصيل الغرفة") },
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
                is RoomDetailUiState.Loading -> CircularProgressIndicator()
                is RoomDetailUiState.Error -> Text(
                    "حدث خطأ: ${state.message}",
                    color = MaterialTheme.colorScheme.error
                )
                is RoomDetailUiState.Success -> {
                    RoomDetailContent(room = state.room, onBookClick = onBookClick)
                }
            }
        }
    }
}

@Composable
fun RoomDetailContent(room: Room, onBookClick: (Room) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (room.images.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(room.images) { imageUrl ->
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "غرفة ${room.roomNumber}",
                        modifier = Modifier
                            .width(280.dp)
                            .height(200.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        Text(
            text = "غرفة ${room.roomNumber}",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "${room.pricePerNight} ريال / الليلة",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (room.amenities.isNotEmpty()) {
            Text(text = "المرافق:", style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(8.dp))
            room.amenities.forEach { amenity ->
                Text(text = "• $amenity")
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        Spacer(modifier = Modifier.weight(1f))

        when (room.status) {
            RoomStatus.AVAILABLE -> {
                Button(
                    onClick = { onBookClick(room) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("طلب حجز")
                }
            }
            RoomStatus.PENDING -> {
                Text(
                    text = "هذه الغرفة قيد المراجعة حالياً، الرجاء المحاولة لاحقاً",
                    color = MaterialTheme.colorScheme.error
                )
            }
            RoomStatus.BOOKED -> {
                Text(
                    text = "هذه الغرفة محجوزة حالياً",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
