package com.aisisabeem.Meeba.models

import com.google.firebase.Timestamp



data class SubRequestCompleted(
    var Accepted_Status:String? = "",
    var company_name:String? = "",
    var completed_time: String = "",
    var option: String? = "",
    var request_id:String? = "",
    var status:String? = "",
    var user_id:String = ""
)
