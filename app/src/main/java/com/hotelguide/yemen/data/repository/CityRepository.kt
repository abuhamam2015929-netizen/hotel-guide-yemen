package com.hotelguide.yemen.data.repository

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import com.hotelguide.yemen.data.model.City
import kotlinx.coroutines.tasks.await

/**
 * مصدر الحقيقة الوحيد لبيانات المدن.
 * الشاشات لا تتكلم مع Firestore مباشرة أبداً — فقط عبر هذا الملف.
 * هذا يسمح لاحقاً باستبدال المصدر (مثلاً إضافة كاش Room) دون تعديل أي شاشة.
 */
class CityRepository {

    private val db = Firebase.firestore

    /**
     * يجلب المدن النشطة فقط، مرتبة حسب حقل order.
     * Firestore نفسه يوفر offline persistence (مفعّل في HotelGuideApp)
     * لذلك هذا الاستدعاء يعمل حتى بدون إنترنت إن وُجدت بيانات محفوظة سابقاً.
     */
    suspend fun getActiveCities(): Result<List<City>> {
        return try {
            val snapshot = db.collection("cities")
                .whereEqualTo("isActive", true)
                .orderBy("order")
                .get()
                .await()
            Result.success(snapshot.toObjects<City>())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
