package com.umit.paginationexampleapp.shared.utilities

object PeopleGenUtility {
    private val firstNames = listOf("Fatma", "Mehmet", "Ayşe", "Mustafa", "Emine", "Ahmet", "Hatice", "Ali", "Zeynep", "Hüseyin", "Elif", "Hasan", "İbrahim", "Can", "Murat", "Özlem")
    private val lastNames = listOf("Yılmaz", "Şahin", "Demir", "Çelik", "Şahin", "Öztürk", "Kılıç", "Arslan", "Taş", "Aksoy", "Barış", "Dalkıran")

    fun generateRandomFullName(): String {
        val firstNamesCount = firstNames.size
        val lastNamesCount = lastNames.size
        val firstName = firstNames[RandomUtility.generateRandomInt(range = 0 until firstNamesCount)]
        val lastName = lastNames[RandomUtility.generateRandomInt(range = 0 until lastNamesCount)]
        return "$firstName $lastName"
    }
}
