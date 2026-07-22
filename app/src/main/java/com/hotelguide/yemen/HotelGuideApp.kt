package com.hotelguide.yemen

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

/**
 * نقطة الدخول الرئيسية للتطبيق.
 * هنا يتم تهيئة Firebase وتفعيل التخزين المؤقت المحلي (Offline Persistence)
 * وهذا أساسي جداً لدعم ضعف الشبكة بين المدن.
 */
class HotelGuideApp : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)

        // تفعيل التخزين المؤقت offline من Firestore نفسه
        // بحيث آخر بيانات تم تحميلها تبقى متاحة حتى بدون إنترنت
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
        Firebase.firestore.firestoreSettings = settings
    }
}
