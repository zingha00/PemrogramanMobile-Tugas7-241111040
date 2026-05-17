package com.utama.aplikasiloginsederhana

data class Event(
    val id: Int = 0,
    val name: String = "",
    val date: String = "",
    val location: String = "",
    val price: Int = 0,
    val description: String = "",
    val isRegistered: Boolean = false
) {
    fun getFormattedPrice(): String {
        return if (price == 0) "Gratis" else "Rp ${"%,d".format(price)}"
    }
}