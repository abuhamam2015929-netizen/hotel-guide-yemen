# المعمارية التفصيلية

## مبدأ الفصل بين الطبقات

```
ui/ (Compose Screens)
   ↓ يستدعي فقط
viewmodel/ (State + منطق العرض)
   ↓ يستدعي فقط
data/repository/ (يقرر: كاش محلي أو Firebase)
   ↓
data/remote (Firestore) + data/local (Room Cache)
```

**القاعدة الذهبية:** أي Composable لا يستورد `com.google.firebase.*` مباشرة أبداً.
كل تواصل مع Firebase يمر عبر Repository فقط. هذا يسمح لاحقاً باستبدال
Firebase بأي backend آخر دون لمس شاشة واحدة.

## هيكلة Firestore

```
cities/{cityId}
  - nameAr, order, isActive, imageUrl

hotels/{hotelId}
  - cityId, name, rating, imageUrl, whatsappNumber
  - paymentAccounts: { alkuraimi: {...}, alnajm: {...} }

rooms/{roomId}
  - hotelId, roomNumber, pricePerNight, images[], amenities[]
  - status: "AVAILABLE" | "PENDING" | "BOOKED"

admins/{adminId}
  - hotelId, username (المصادقة تتم عبر Firebase Auth، ليس تخزين باسورد نصي)
```

## حالة الغرفة الثلاثية (مهم)

عند ضغط الزبون "تأكيد الحجز"، الحالة تتحول فوراً من `AVAILABLE` إلى `PENDING`
(وليس مباشرة إلى `BOOKED`). هذا يمنع تعارض الحجوزات المزدوجة قبل ما يراجع
مدير الفندق السند المرسل عبر واتساب. المدير بعدها يحوّلها يدوياً:
- إلى `BOOKED` إذا تأكد التحويل
- إلى `AVAILABLE` إذا لم يصل تحويل خلال مدة معينة

## التخزين المؤقت (Offline-First)

طبقتان من الكاش تعملان معاً:
1. **Firestore Offline Persistence** (مفعّل في `HotelGuideApp.kt`) — تلقائي، بدون كود إضافي
2. **Room Database** (لاحقاً) — لتخزين منظم أكثر للفنادق/الغرف المتصفحة حديثاً، مفيد
   خصوصاً لسيناريو "تصفح في عتق، فقدان الاتصال أثناء الطريق، وصول للمكلا"

## الأمان

- كلمات مرور الإدارة عبر **Firebase Authentication**، وليس حقل نصي في Firestore
- Firestore Security Rules يجب أن تمنع:
  - أي كتابة على `rooms.status` من غير المصادقين كأصحاب فنادق
  - قراءة `paymentAccounts` بالكامل من غير الحاجة (تُعرض فقط عند الحجز الفعلي)
