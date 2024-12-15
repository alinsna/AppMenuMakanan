package com.example.appmenumakanan.Model

import com.example.appmenumakanan.model.Menu

data class MenuApiResponse(
    val id: Int = 0,
    val name: String,
    val price: Double,
    val photoUrl: String,
    val description: String,
    val category: String
)

fun MenuApiResponse.toMenu(): Menu {
    return Menu(
        id = this.id,
        name = this.name,
        price = this.price,
        photoUrl = this.photoUrl,
        description = this.description,
        category = this.category
    )
}