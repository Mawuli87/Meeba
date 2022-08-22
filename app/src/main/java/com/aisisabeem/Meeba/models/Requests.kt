package com.aisisabeem.Meeba.models




import java.util.*


data class Requests(
    var company_name:String? = "",
    var createdAt: Date? = null,
    var request_id:String? = "",
    var status:String? = "",
    var option:String? = "",
    var user_id:String? = "",
    var rider_id:String = ""
)
