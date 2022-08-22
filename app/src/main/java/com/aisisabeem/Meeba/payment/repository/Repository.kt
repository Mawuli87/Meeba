package com.aisisabeem.Meeba.payment.repository

import com.aisisabeem.Meeba.payment.api.RetrofitInstance
import com.aisisabeem.Meeba.payment.momo.PaymentData
import retrofit2.Response

class Repository {
    suspend fun sendPaymentRequest(amount: String,request_id:String,stop_id:String,phone:String,payment_type:String)
    : Response<PaymentData> {
        return RetrofitInstance.api.sendPayment(amount,request_id,stop_id,phone,payment_type)
    }

    suspend fun pushPost(post: PaymentData):Response<PaymentData>{
        return RetrofitInstance.api.pushPost(post)
    }
}