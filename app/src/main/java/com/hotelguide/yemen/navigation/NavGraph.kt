package com.hotelguide.yemen.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hotelguide.yemen.ui.client.welcome.WelcomeScreen
import com.hotelguide.yemen.ui.client.hotels.HotelsScreen
import com.hotelguide.yemen.ui.client.rooms.RoomsScreen

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

        composable(
            route = Screen.HotelList.route,
            arguments = listOf(navArgument("cityId") { type = NavType.StringType })
        ) { backStackEntry ->
            val cityId = backStackEntry.arguments?.getString("cityId") ?: ""
            HotelsScreen(
                cityId = cityId,
                onBack = { navController.popBackStack() },
                onHotelClick = { hotel ->
                    navController.navigate(Screen.RoomList.createRoute(hotel.id))
                }
            )
        }

        composable(
            route = Screen.RoomList.route,
            arguments = listOf(navArgument("hotelId") { type = NavType.StringType })
        ) { backStackEntry ->
            val hotelId = backStackEntry.arguments?.getString("hotelId") ?: ""
            RoomsScreen(
                hotelId = hotelId,
                onBack = { navController.popBackStack() },
                onRoomClick = { room ->
                    // بننشئ RoomDetailScreen بالخطوة القادمة
                }
            )
        }

        // composable(Screen.RoomDetail.route) { ... }
        // composable(Screen.Payment.route) { ... }
        // composable(Screen.AdminLogin.route) { ... }
        // composable(Screen.AdminDashboard.route) { ... }
    }
}
