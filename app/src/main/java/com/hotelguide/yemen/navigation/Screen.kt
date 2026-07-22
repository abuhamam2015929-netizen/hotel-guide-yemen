package com.hotelguide.yemen.navigation

sealed class Screen(val route: String) {
    data object Welcome : Screen("welcome")
    data object HotelList : Screen("hotels/{cityId}") {
        fun createRoute(cityId: String) = "hotels/$cityId"
    }
    data object RoomList : Screen("rooms/{hotelId}") {
        fun createRoute(hotelId: String) = "rooms/$hotelId"
    }
    data object RoomDetail : Screen("room-detail/{roomId}") {
        fun createRoute(roomId: String) = "room-detail/$roomId"
    }
    data object Payment : Screen("payment/{roomId}") {
        fun createRoute(roomId: String) = "payment/$roomId"
    }
    data object AdminLogin : Screen("admin-login")
    data object AdminDashboard : Screen("admin-dashboard")
}
