package com.aisisabeem.Meeba.payment.api

import com.aisisabeem.Meeba.payment.momo.PaymentData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface PaymentApi {
    @FormUrlEncoded
    @POST("/")
    suspend fun sendPayment(
        @Field("amount") amount: String,
        @Field("request_id") request_id: String,
        @Field("stop_id") stop_id: String,
        @Field("phone") phone: String,
        @Field("payment_type") payment_type: String,

    ): Response<PaymentData>


    @POST("/")
    suspend fun pushPost(
        @Body paymentData: PaymentData
    ):Response<PaymentData>
}