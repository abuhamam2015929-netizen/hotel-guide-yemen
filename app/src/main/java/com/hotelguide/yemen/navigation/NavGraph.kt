package com.hotelguide.yemen.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hotelguide.yemen.ui.client.welcome.WelcomeScreen

/**
 * شجرة التنقل الرئيسية. كل شاشة جديدة تُضاف هنا كسطر composable() واحد فقط.
 * حالياً مُفعّلة شاشة الترحيب، وباقي الشاشات ستُضاف تباعاً بنفس النمط
 * دون الحاجة لتعديل أي شاشة سابقة.
 */
@Composable
fun HotelGuideNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Screen.Welcome.route
    ) {
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onCitySelected = { city ->
                    navController.navigate(Screen.HotelList.createRoute(city.id))
                }
            )
        }

        // composable(Screen.HotelList.route) { ... } — الخطوة القادمة
        // composable(Screen.RoomList.route) { ... }
        // composable(Screen.RoomDetail.route) { ... }
        // composable(Screen.Payment.route) { ... }
        // composable(Screen.AdminLogin.route) { ... }
        // composable(Screen.AdminDashboard.route) { ... }
    }
}
