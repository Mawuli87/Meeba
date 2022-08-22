package com.aisisabeem.Meeba.models

import java.util.*

data class Completed(
    var completed_status:String? = "",
    var company_name:String? = "",
    var completed_time:String = "",
    var option: String? = "",
    var request_id:String? = "",
    var status:String? = "",
    var user_id:String? = "",
    var rider_id:String = ""
)
