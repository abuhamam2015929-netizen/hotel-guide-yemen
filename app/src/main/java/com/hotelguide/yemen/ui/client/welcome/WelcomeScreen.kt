package com.hotelguide.yemen.ui.client.welcome

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hotelguide.yemen.R
import com.hotelguide.yemen.data.model.City
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * شاشة الترحيب — أول شاشة يراها المستخدم.
 * تعرض المدن بشكل شبكي (Grid) بدل زرين فقط، لأن التطبيق الآن يدعم 6 مدن
 * وقابل للتوسع لمدن أكثر دون تعديل هذا الملف مطلقاً — البيانات كلها من الـ ViewModel.
 */
@Composable
fun WelcomeScreen(
    viewModel: WelcomeViewModel = viewModel(),
    onCitySelected: (City) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = stringResourceApp(R.string.welcome_title),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResourceApp(R.string.welcome_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                uiState.cities.isEmpty() -> {
                    Text(
                        text = stringResourceApp(R.string.error_generic),
                        color = MaterialTheme.colorScheme.error
                    )
                }

                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(uiState.cities) { city ->
                            CityCard(city = city, onClick = { onCitySelected(city) })
                        }
                    }
                }
            }

            if (uiState.isOffline) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResourceApp(R.string.no_internet),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun CityCard(city: City, onClick: () -> Unit) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = city.nameAr,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

// دالة مساعدة صغيرة لتفادي استيراد stringResource المباشر هنا وتسهيل الاختبار لاحقاً
@Composable
private fun stringResourceApp(id: Int) = androidx.compose.ui.res.stringResource(id)
