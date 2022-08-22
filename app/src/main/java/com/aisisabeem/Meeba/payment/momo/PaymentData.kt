package com.aisisabeem.Meeba.payment.momo

data class PaymentData(
    val amount: String,
    val request_id: String,
    val stop_id: String,
    val phone: String,
    val payment_type: String
)