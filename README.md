# دليل الفنادق اليمني 🏨

تطبيق أندرويد (Kotlin + Jetpack Compose + Firebase) يربط بين الزبائن والفنادق في
**عتق، المكلا، عدن، سيئون، بيحان، ومأرب**، مع "ذكاء هجين" للعمل بضعف الإنترنت،
وحلقة وصل عبر واتساب لإتمام الحجوزات بدون تعقيدات الدفع الإلكتروني.

## الحالة الحالية

- [x] هيكل المشروع الكامل (Gradle, Manifest, Theme)
- [x] شاشة الترحيب واختيار المدينة (المدن الست مفعّلة)
- [x] طبقة Repository + ViewModel لشاشة الترحيب
- [x] نظام تنقل (Navigation) جاهز لإضافة باقي الشاشات
- [x] GitHub Actions لبناء APK تلقائياً
- [ ] شاشة دليل الفنادق
- [ ] شاشة استعراض الغرف
- [ ] شاشة تفاصيل الغرفة + الربط بواتساب
- [ ] شاشة التحويل المالي
- [ ] لوحة تحكم أصحاب الفنادق
- [ ] Room Database للتخزين المؤقت offline

## إعداد المشروع خطوة بخطوة (يعمل من الهاتف بالكامل)

### 1. ربط Firebase

1. افتح [console.firebase.google.com](https://console.firebase.google.com) من متصفح الهاتف
2. أنشئ مشروعاً جديداً باسم `HotelGuideYemen`
3. أضف تطبيق أندرويد بالـ package name: `com.hotelguide.yemen`
4. حمّل ملف `google-services.json` — **لا ترفعه على GitHub مباشرة** (محمي في `.gitignore`)
5. فعّل **Firestore Database** و **Storage** من القائمة الجانبية

### 2. إعداد بيانات المدن الأولية (Seed)

من Firestore Console، أنشئ collection باسم `cities` وأضف 6 مستندات بهذه الحقول:

| id | nameAr | order | isActive |
|---|---|---|---|
| ataq | عتق | 1 | true |
| mukalla | المكلا | 2 | true |
| aden | عدن | 3 | true |
| seiyun | سيئون | 4 | true |
| bayhan | بيحان | 5 | true |
| marib | مأرب | 6 | true |

### 3. رفع المشروع على GitHub

من تطبيق GitHub أو المتصفح على الهاتف:
```
1. أنشئ Repository جديد باسم hotel-guide-yemen
2. ارفع كل الملفات في هذا المجلد (عدا الملفات المستثناة في .gitignore)
```

### 4. تفعيل البناء التلقائي (بدون Android Studio)

1. من إعدادات الـ Repo على GitHub: `Settings → Secrets and variables → Actions`
2. أضف secret جديد باسم `GOOGLE_SERVICES_JSON`
   - القيمة: محتوى ملف `google-services.json` بعد تحويله لـ base64
   - (يمكن تحويله عبر أي أداة base64 encoder من الهاتف، أو موقع مثل base64encode.org)
3. أي رفعة كود لفرع `main` أو `develop` ستُشغّل `.github/workflows/build.yml`
   تلقائياً وتبني ملف APK تقدر تنزّله من تبويب **Actions → Artifacts**

## البنية المعمارية

راجع [docs/architecture.md](docs/architecture.md) للتفاصيل الكاملة.

النمط المتبع: `UI (Compose) → ViewModel → Repository → Firebase/Room Cache`
هذا الفصل يسمح بإضافة مدن أو ميزات جديدة دون كسر الكود الموجود.

## استراتيجية الفروع

- `main` — نسخة مستقرة وقابلة للنشر دائماً
- `develop` — التطوير النشط
- `feature/*` — كل ميزة جديدة في فرع منفصل قبل دمجها

## ملاحظة تقنية عن Gradle Wrapper

هذا المشروع لا يتضمن ملف `gradlew` الثنائي (`gradle-wrapper.jar`) لأنه ملف binary
لا يمكن إنشاؤه هنا. هذا لا يؤثر على البناء عبر GitHub Actions (يستخدم Gradle
مباشرة عبر `gradle/actions/setup-gradle`). لكن لو فتحت المشروع لاحقاً على
Android Studio (من جهاز كمبيوتر)، اضغط File → Sync، وAndroid Studio سيولّد
ملف الـ wrapper تلقائياً بدون أي تدخل منك.

## الترخيص

مشروع خاص — جميع الحقوق محفوظة.
