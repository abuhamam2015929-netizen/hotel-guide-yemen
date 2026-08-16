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
import com.hotelguide.yemen.ui.client.roomdetail.RoomDetailScreen
import com.hotelguide.yemen.ui.client.payment.PaymentScreen
import com.hotelguide.yemen.ui.admin.login.AdminLoginScreen
import com.hotelguide.yemen.ui.admin.dashboard.AdminDashboardScreen

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
                },
                onAdminLoginClick = {
                    navController.navigate(Screen.AdminLogin.route)
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
                    navController.navigate(Screen.RoomDetail.createRoute(room.id))
                }
            )
        }

        composable(
            route = Screen.RoomDetail.route,
            arguments = listOf(navArgument("roomId") { type = NavType.StringType })
        ) { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString("roomId") ?: ""
            RoomDetailScreen(
                roomId = roomId,
                onBack = { navController.popBackStack() },
                onBookClick = { room ->
                    navController.navigate(Screen.Payment.createRoute(room.hotelId, room.id))
                }
            )
        }

        composable(
            route = Screen.Payment.route,
            arguments = listOf(
                navArgument("hotelId") { type = NavType.StringType },
                navArgument("roomId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val hotelId = backStackEntry.arguments?.getString("hotelId") ?: ""
            val roomId = backStackEntry.arguments?.getString("roomId") ?: ""
            PaymentScreen(
                hotelId = hotelId,
                roomId = roomId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AdminLogin.route) {
            AdminLoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.AdminDashboard.route) {
                        popUpTo(Screen.AdminLogin.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AdminDashboard.route) {
            AdminDashboardScreen(
                onLogout = {
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(0)
                    }
                },
                onRoomClick = { roomDocId ->
                    // بنضيف شاشة تعديل الغرفة بالخطوة القادمة
                }
            )
        }
    }
}
