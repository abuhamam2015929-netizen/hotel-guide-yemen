package com.hotelguide.yemen.data.model

data class Hotel(
    val id: String = "",
    val cityId: String = "",
    val name: String = "",
    val rating: Double = 0.0,
    val imageUrl: String = "",
    val whatsappNumber: String = "",
    val paymentAccounts: Map<String, PaymentAccount> = emptyMap(),
    val isActive: Boolean = true
)

data class PaymentAccount(
    val methodName: String = "",   // الكريمي / النجم / العمقي
    val accountNumber: String = "",
    val beneficiaryName: String = ""
)
