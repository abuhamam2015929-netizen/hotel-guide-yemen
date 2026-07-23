package com.hotelguide.yemen.ui.client.hotels

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.hotelguide.yemen.data.model.Hotel

@Composable
fun HotelsScreen(
    cityId: String,
    onBack: () -> Unit,
    viewModel: HotelsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(cityId) {
        viewModel.loadHotels(cityId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("الفنادق") },
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
                is HotelsUiState.Loading -> CircularProgressIndicator()
                is HotelsUiState.Empty -> Text("لا توجد فنادق متاحة حالياً بهذه المدينة")
                is HotelsUiState.Error -> Text(
                    "حدث خطأ: ${state.message}",
                    color = MaterialTheme.colorScheme.error
                )
                is HotelsUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(state.hotels) { hotel ->
                            HotelCard(hotel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HotelCard(hotel: Hotel) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            AsyncImage(
                model = hotel.imageUrl,
                contentDescription = hotel.name,
                modifier = Modifier.fillMaxWidth().height(180.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = hotel.name, style = MaterialTheme.typography.titleMedium)
        }
    }
}
