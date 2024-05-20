package com.example.mvvm_order_managemente.model

data class User(
    val id: Int,
    val name: String,
    val email: String
) {
    override fun toString() = "$name ($email)"
}

data class Product(
    val id: Int,
    val name: String,
    val brand: String,
    val price: Int
) {
    override fun toString() = "$name - $brand ($$price)"
}

data class Order(
    val id: Int,
    val user: User,
    val product: Product
)

val usersList = arrayListOf("olivia", "Liam", "Emna", "Noah", "Anelia", "Oliver", "Ava", "Elijah", "Sophia")

val brandsList = arrayListOf("Amazon", "Apple", "Google", "Microsoft", "Tencent", "Facebook", "Alibaba")

val productsList = arrayListOf("Tv", "Clock", "Bike", "Xbox 360", "Wii", "Spaghetti", "Batman", "Tim Drake")