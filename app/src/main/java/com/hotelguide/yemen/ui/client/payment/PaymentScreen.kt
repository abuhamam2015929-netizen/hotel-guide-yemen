package com.hotelguide.yemen.ui.client.payment

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.hotelguide.yemen.data.model.Hotel
import com.hotelguide.yemen.data.model.PaymentAccount
import com.hotelguide.yemen.data.model.Room

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    hotelId: String,
    roomId: String,
    onBack: () -> Unit,
    viewModel: PaymentViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    LaunchedEffect(hotelId, roomId) {
        viewModel.loadData(hotelId, roomId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("التحويل المالي") },
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
                is PaymentUiState.Loading -> CircularProgressIndicator()
                is PaymentUiState.Error -> Text(
                    "حدث خطأ: ${state.message}",
                    color = MaterialTheme.colorScheme.error
                )
                is PaymentUiState.Success -> {
                    PaymentContent(
                        hotel = state.hotel,
                        room = state.room,
                        onCopyAccount = { accountNumber ->
                            clipboardManager.setText(AnnotatedString(accountNumber))
                        },
                        onConfirm = {
                            viewModel.markRoomAsPending(state.roomDocId) {
                                sendWhatsAppMessage(context, state.hotel, state.room)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PaymentContent(
    hotel: Hotel,
    room: Room,
    onCopyAccount: (String) -> Unit,
    onConfirm: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "غرفة ${room.roomNumber} - ${room.pricePerNight} ريال / الليلة",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (hotel.paymentAccounts.isEmpty()) {
            Text("لا توجد حسابات تحويل متاحة حالياً لهذا الفندق")
        } else {
            Text("حوّل المبلغ إلى أحد الحسابات التالية:", style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(8.dp))

            hotel.paymentAccounts.values.forEach { account ->
                PaymentAccountCard(account = account, onCopy = onCopyAccount)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onConfirm,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("تم التحويل، أرسل عبر واتساب")
        }
    }
}

@Composable
fun PaymentAccountCard(account: PaymentAccount, onCopy: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCopy(account.accountNumber) }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = account.methodName, style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = account.accountNumber, style = MaterialTheme.typography.bodyLarge)
            Text(text = account.beneficiaryName, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "اضغط للنسخ", style = MaterialTheme.typography.labelSmall)
        }
    }
}

fun sendWhatsAppMessage(context: android.content.Context, hotel: Hotel, room: Room) {
    val message = "السلام عليكم، أرغب بحجز غرفة ${room.roomNumber} في ${hotel.name} " +
            "بسعر ${room.pricePerNight} ريال/الليلة. تم إرسال سند التحويل."

    val phone = hotel.whatsappNumber.filter { it.isDigit() }
    val uri = Uri.parse("https://wa.me/$phone?text=${Uri.encode(message)}")
    val intent = Intent(Intent.ACTION_VIEW, uri)
    context.startActivity(intent)
}
