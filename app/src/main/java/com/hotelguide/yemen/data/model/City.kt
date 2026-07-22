package com.hotelguide.yemen.data.model

/**
 * نموذج المدينة — يُقرأ ديناميكياً من Firestore (collection: cities)
 * بحيث إضافة مدينة جديدة مستقبلاً لا تتطلب أي تعديل بالكود أو نشر تحديث.
 *
 * ملاحظة: المدن الست أدناه (عتق، المكلا، عدن، سيئون، بيحان، مأرب) هي
 * القيم الأولية التي تُزرع (seed) في Firestore عند إعداد المشروع لأول مرة،
 * راجع docs/firestore_seed.md لطريقة إضافتها.
 */
data class City(
    val id: String = "",
    val nameAr: String = "",
    val imageUrl: String = "",
    val isActive: Boolean = true,
    val order: Int = 0
)

/**
 * القيم الافتراضية المستخدمة فقط كمرجع أولي / بيانات تجريبية محلية.
 * المصدر الحقيقي دائماً هو Firestore.
 */
object SeedCities {
    val defaultCities = listOf(
        City(id = "ataq", nameAr = "عتق", order = 1),
        City(id = "mukalla", nameAr = "المكلا", order = 2),
        City(id = "aden", nameAr = "عدن", order = 3),
        City(id = "seiyun", nameAr = "سيئون", order = 4),
        City(id = "bayhan", nameAr = "بيحان", order = 5),
        City(id = "marib", nameAr = "مأرب", order = 6)
    )
}
