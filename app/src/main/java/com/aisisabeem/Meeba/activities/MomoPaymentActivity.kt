package com.aisisabeem.Meeba.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback
import com.aisisabeem.Meeba.R
import com.aisisabeem.Meeba.payment.momo.PaymentData
import com.aisisabeem.Meeba.home.HomeActivity
import com.aisisabeem.Meeba.home.destination.DestinationActivity
import com.aisisabeem.Meeba.payment.repository.MainViewModel
import com.aisisabeem.Meeba.payment.repository.MainViewModelFactory
import com.aisisabeem.Meeba.payment.repository.Repository
import com.aisisabeem.Meeba.utils.BaseActivity
import com.aisisabeem.Meeba.utils.Constants
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import org.json.JSONObject


class MomoPaymentActivity : BaseActivity() {

    private lateinit var spinner04:Spinner
    private lateinit var labelInput:TextInputEditText
    private lateinit var btn_pay_ad:Button
    private var name:String =""
    private var itemId:Int? = null
    private var numb:Int = -1
    private var phoneNumber:String =""
    private lateinit var phone_error: TextInputLayout
    private lateinit var closeActivity:ImageButton

    private lateinit var viewModel: MainViewModel

    private var phone:String = ""
    private var price:String = ""
    private var stopid:String = ""
    private var request_id:String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_momo_payment)

        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this,viewModelFactory).get(MainViewModel::class.java)

        phone = intent.getStringExtra("phone").toString()
        price = intent.getStringExtra("price").toString()
        stopid = intent.getStringExtra("stopid").toString()
        request_id = intent.getStringExtra("request_id").toString()

        spinner04 = findViewById(R.id.descriptionInput)
        labelInput = findViewById(R.id.labelInput)
        btn_pay_ad = findViewById(R.id.btn_pay)
        closeActivity = findViewById(R.id.closeActivity)
        labelInput.setText(phone)


       phone_error = findViewById(R.id.labelLayout)

        // list of spinner items
        val list = listOf(
            "Select Provider",
            "Mtn Money",
            "Vodafone Cash",
            "Tigo Cash",
            "Airtel Money"

        )

        // initialize an array adapter for spinner
        val adapter:ArrayAdapter<String> = object: ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_dropdown_item, list){
            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view:TextView = super.getDropDownView(position, convertView, parent) as TextView
                // set item text size
                view.setTextSize(TypedValue.COMPLEX_UNIT_SP,28F)
                  if (position == spinner04.selectedItemPosition){
                      view.background = ColorDrawable(Color.parseColor("#FFF600"))
                      view.setTextColor(Color.parseColor("#2E2D88"))
                      name = view.text.toString()
                  }



               btn_pay_ad.setOnClickListener {
                   //validate the user input

                  setUpViews()
                   phoneNumber = labelInput.editableText.toString()

                   when(spinner04.selectedItemPosition){
                       1 -> {
                           val payment_type:String = Constants.KEY_MTN
                           setupMtnPay(price,request_id,stopid,phoneNumber,payment_type)
                       }
                       2->{
                           val payment_type:String = Constants.KEY_VODAFONE
                       setupVodaphonePay(price,request_id,stopid,phone,payment_type)
                       }
                       3-> {
                           val payment_type:String = Constants.KEY_TIGO
                           setupTigoPay(price,request_id,stopid,phone,payment_type)
                       }
                       4 ->{
                           val payment_type:String = Constants.KEY_AIRTEL
                           setupairtelPay(price,request_id,stopid,phone,payment_type)
                       }

                   }


                }


                return view
            }
        }

        // finally, data bind spinner with adapter
        spinner04.adapter = adapter

        // set up initial selection
        spinner04.setSelection(0)



        closeActivity.setOnClickListener {

            AlertDialog.Builder(this)
            .setTitle("Exit Payment ")
            .setMessage("You are about to exit payment")
            .setNegativeButton(android.R.string.no, null)
            .setPositiveButton(android.R.string.yes
            ) { arg0, arg1 ->
              // arg0.dismiss()
                finish()
            }.create().show()
        }

    }




    private fun setUpViews(){
        phoneNumber = labelInput.editableText.toString()


        if (phoneNumber.isEmpty()){
            phone_error.helperText = "Please enter your phone number"

            Toast.makeText(this,"Field must not be empty",Toast.LENGTH_LONG).show()
        }
    }



    private fun setupTigoPay(
        price: String,
        requestId: String,
        stopid: String,
        phone: String,
        paymentType: String) {


        MaterialDialog.Builder(this@MomoPaymentActivity)
            .title("Tigo Cash")
            .content(R.string.msg_dialog_payment_airtel_tigo)
            .positiveText(R.string.positive_button_txt)
            .negativeText(R.string.negative_button_txt)
            .onPositive { dialog, _ ->
                startActivity(
                    Intent(
                        this@MomoPaymentActivity,
                        DestinationActivity::class.java
                    )
                )

                //post request for payment
                postPaymentRequest(price,requestId,stopid,phone,paymentType)

            }
            .show()

    }




    private fun setupVodaphonePay(
        price: String,
        requestId: String,
        stopid: String,
        phone: String,
        paymentType: String) {

        MaterialDialog.Builder(this@MomoPaymentActivity)
            .title("Vodafone Cash")
            .content(Constants.VOUCHER_PROCESS_VODAPHONE)

            .negativeText("Continue")
            .onNegative(SingleButtonCallback { dialog, which ->
                MaterialDialog.Builder(this@MomoPaymentActivity)
                    .title("Run *110# or Proceed")
                    .content(R.string.ussd_vodafone)
                    .positiveText(R.string.proceed_button_txt)
                    .negativeText(R.string.getvoucher_button_txt)
                    .onPositive(SingleButtonCallback { dialog, which -> // startActivity(new Intent(NewPaymentActivity.this, MeterActivity.class));
                        enterVoucherCode(price,requestId,stopid,phone,paymentType)

                    })
                    .onNegative(SingleButtonCallback { dialog, which ->
                        startActivity(
                            Intent(
                                this@MomoPaymentActivity,
                                DestinationActivity::class.java
                            )
                        )
                        performUssdCall("*110")
                    })
                    .show()
            })
            .show()



    }


    private fun enterVoucherCode(
        price: String,
        requestId: String,
        stopid: String,
        phone: String,
        paymentType: String){

       MaterialDialog.Builder(this)
            .title("Enter Voucher Code")
            .content(" Enter your voucher code.\nA prompt will be sent to you shortly \n It should be 6 digits")
            .positiveText(R.string.positive_button_txt)
            .inputRangeRes(6, 6, R.color.md_red_500)
            .inputType(InputType.TYPE_CLASS_PHONE)
            .input(R.string.input_hint, R.string.input_prefill) { dialog, input ->
                val voucher_code = input.toString()
                sendVodafonePaymentRequest(price,requestId,stopid,phone,paymentType,voucher_code)
            }

            .show()

    }



    private fun setupMtnPay(
        price: String,
        requestId: String,
        stopid: String,
        phone: String,
        paymentType: String) {

        MaterialDialog.Builder(this@MomoPaymentActivity)
            .title("Mtn Money Pay")
            .content(Constants.PAYING_PROCESS_MTN)
            .positiveText(R.string.positive_button_txt)
            .negativeText(R.string.negative_button_txt)
            .onPositive(SingleButtonCallback { dialog, which ->
                MaterialDialog.Builder(this@MomoPaymentActivity)
                    .title("Pay or Go To Approvals")
                    .content("Run *170# in case you have \n" +
                            " already tried paying, to check \n pending " +
                            " approvals. ")
                    .positiveText("my approvals")
                    .negativeText("Pay")
                    .onPositive(SingleButtonCallback { dialog, which ->
                        startActivity(
                            Intent(
                                this@MomoPaymentActivity,
                                HomeActivity::class.java
                            )
                        )
                        performUssdCall("*170")
                        dialog.dismiss()
                    })
                    .onNegative(SingleButtonCallback { dialog, which ->
                        postPaymentRequest(price,requestId,stopid,phone,paymentType)

                     dialog.dismiss()
                    })
                    .show()
            })
            .show()


    }



    private fun performUssdCall(ussd: String?) {
        if (ussd != null) {
            val callIntent = Intent(Intent.ACTION_DIAL)
            val ussdCode = ussd + Uri.encode("#")
            callIntent.data = Uri.parse("tel:$ussdCode")

            if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            }
            this.startActivity(callIntent)
        }
    }

    private fun setupairtelPay(
        price: String,
        requestId: String,
        stopid: String,
        phone: String,
        paymentType: String) {

        MaterialDialog.Builder(this@MomoPaymentActivity)
            .title("Airtel Money Pay")
            .content(R.string.msg_dialog_payment_airtel_tigo)
            .positiveText(R.string.positive_button_txt)
            .negativeText(R.string.negative_button_txt)
            .onPositive { dialog, _ ->
                startActivity(
                    Intent(
                        this@MomoPaymentActivity,
                        HomeActivity::class.java
                    )
                )

                //post request for payment
                sendPaymentRequest(price,requestId,stopid,phone,paymentType)
                dialog.dismiss()
            }
            .show()


    }

    private fun sendVodafonePaymentRequest(
        price: String,
        requestId: String,
        stopid: String,
        phone: String,
        paymentType: String,
        voucher_code:String
    ) {
        showProgressDialog("Processing payment request..")
        showProgressDialog("Start Payment")

    }



   private fun sendPaymentRequest(
       price: String,
       requestId: String,
       stopid: String,
       phone: String,
       paymentType: String
   ){
         showProgressDialog("Processing mtn payment...")
       val json = JSONObject()
       json.put("amount", price)
       json.put("request_id", request_id)
       json.put("stop_id", stopid)
       json.put("phone", phone)
       json.put("payment_type", paymentType)
       Log.i("PARAM","$price \n $requestId \n $request_id \n $stopid \n $phone \n $paymentType")




   }


    private fun postPaymentRequest(
        price: String,
        requestId: String,
        stopid: String,
        phone: String,
        paymentType: String
    ) {
       showProgressDialog("mtn payment...")
       // viewModel.postPayment(price,requestId,stopid,phone,paymentType)
        val myPost = PaymentData(price,requestId,stopid,phone,paymentType)
        Log.i("PARAM","$price \n $requestId \n $request_id \n $stopid \n $phone \n $paymentType")


        viewModel.pushPost(myPost)
        viewModel.myResponse.observe(this, Observer { data->
            // val adapter = myAdapter(this, data)
            // recyclerView.adapter = adapter
            if (data.isSuccessful){
                HideProgressDialog()
                Log.d("MainMOMO",data.body().toString())
                Log.d("MainMOMO",data.code().toString())
                Log.d("MainMOMO",data.message())

            }else {
                HideProgressDialog()
               Log.d("ERROR",data.code().toString())
               Log.d("ERROR",data.message())
            }

        })
    }


    private fun postPaymentRequestNot(
        price: String,
        requestId: String,
        stopid: String,
        phone: String,
        paymentType: String
    ) {
       // val URL_ORDER_POST = "http://eprepaid.origgin.net/api/orders/add/$token"
        showProgressDialog("mtn momo payment...")

    }













    private fun readFromAsset(): List<PaymentData> {
        val file_name = "network.json"

        val bufferReader = application.assets.open(file_name).bufferedReader()

        val json_string = bufferReader.use {
            it.readText()
        }
        val gson = Gson()
        val modelList: List<PaymentData> = gson.fromJson(json_string, Array<PaymentData>::class.java).toList()
        return modelList
    }
}