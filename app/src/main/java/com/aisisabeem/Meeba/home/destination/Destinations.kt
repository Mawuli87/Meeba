package com.aisisabeem.Meeba.home.destination



data class Destinations(
    var distance:Number? = 23.45,
    var key: Number? = 344,
    var price: Number? = 344,
    var product_cost: String? = "",
    var receiver_contact: String? = "",
    var receiver_note: String? = "",
    var stoplocation_id:String? = "",
    var stoplocation_lat:Number? = 23.56,
    var stoplocation_lng:Number = 345.76,
    var stoplocation_name:String = ""



) {
    override fun toString(): String {
        return "Destinations(distance=$distance, key=$key, price=$price, product_cost=$product_cost, receiver_contact=$receiver_contact, receiver_note=$receiver_note, stoplocation_id=$stoplocation_id, stoplocation_lat=$stoplocation_lat, stoplocation_lng=$stoplocation_lng, stoplocation_name=$stoplocation_name)"
    }
}
