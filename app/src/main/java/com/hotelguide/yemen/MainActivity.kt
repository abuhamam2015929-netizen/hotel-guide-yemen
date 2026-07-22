package com.hotelguide.yemen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.runtime.CompositionLocalProvider
import com.hotelguide.yemen.navigation.HotelGuideNavGraph
import com.hotelguide.yemen.ui.theme.HotelGuideYemenTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // فرض اتجاه RTL بالكامل لأن التطبيق عربي بالكامل
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                HotelGuideYemenTheme {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        HotelGuideNavGraph()
                    }
                }
            }
        }
    }
}
