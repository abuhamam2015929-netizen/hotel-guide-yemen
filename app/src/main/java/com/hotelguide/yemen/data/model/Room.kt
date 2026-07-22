package com.hotelguide.yemen.data.model

enum class RoomStatus {
    AVAILABLE,
    PENDING,   // تم إرسال طلب حجز، بانتظار مراجعة الإدارة
    BOOKED
}

data class Room(
    val id: String = "",
    val hotelId: String = "",
    val roomNumber: String = "",
    val pricePerNight: Double = 0.0,
    val images: List<String> = emptyList(),
    val amenities: List<String> = emptyList(), // مكيف، واي فاي، شاشة...
    val status: RoomStatus = RoomStatus.AVAILABLE
)
