package com.aisisabeem.Meeba.payment.repository

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aisisabeem.Meeba.payment.momo.PaymentData
import kotlinx.coroutines.launch
import retrofit2.Response

class MainViewModel(private val repository: Repository): ViewModel() {
    val myResponse: MutableLiveData<Response<PaymentData>> = MutableLiveData()

    fun postPayment(amount: String,request_id:String,stop_id:String,phone:String,payment_type:String){
        viewModelScope.launch {
            val response = repository.sendPaymentRequest(amount,request_id,stop_id,phone,payment_type)
            myResponse.value = response
        }
    }


    fun pushPost(post: PaymentData){
        viewModelScope.launch {
            val response = repository.pushPost(post)
            myResponse.value = response
        }
    }

}