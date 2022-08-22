package com.aisisabeem.Meeba.payment.api

import com.aisisabeem.Meeba.utils.Constants
import com.squareup.okhttp.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {
    private val client = okhttp3.OkHttpClient.Builder().apply {
        addInterceptor(MyInterCeptor())
            .retryOnConnectionFailure(true)
            .connectTimeout(100, TimeUnit.SECONDS)
            .writeTimeout(100, TimeUnit.SECONDS)
            .readTimeout(100, TimeUnit.SECONDS)
    }.build()

    private val retrofit by lazy {

        Retrofit.Builder()
            .baseUrl(Constants.URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api:PaymentApi by lazy {
        retrofit.create(PaymentApi::class.java)
    }
}