package com.aisisabeem.Meeba.payment.api

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class MyInterCeptor:Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request:Request = chain.request()
            .newBuilder()
            .addHeader("Content-Type","application/json")
            .addHeader("X-Platform","Android")
            .build()
        return chain.proceed(request)
    }

}