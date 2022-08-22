package com.aisisabeem.Meeba.home.destination

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.aisisabeem.Meeba.R
import com.aisisabeem.Meeba.activities.HowToPayActivity
import com.aisisabeem.Meeba.activities.MomoPaymentActivity
import com.aisisabeem.Meeba.home.HomeActivity
import com.aisisabeem.Meeba.home.pickup.PickUp
import com.aisisabeem.Meeba.home.pickup.PickUpAdapter
import com.aisisabeem.Meeba.payment.momo.PaymentData
import com.aisisabeem.Meeba.payment.repository.MainViewModel
import com.aisisabeem.Meeba.payment.repository.MainViewModelFactory
import com.aisisabeem.Meeba.payment.repository.Repository
import com.aisisabeem.Meeba.utils.BaseActivity
import com.aisisabeem.Meeba.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import com.skyfishjy.library.RippleBackground
import org.json.JSONArray
import org.json.JSONObject


class DestinationActivity : BaseActivity() {

    //requests
    private lateinit var recyclerView: RecyclerView
    private lateinit var destinationAdapter: DestinationAdapter
    private lateinit var subdestinationAdapter: SubDestinationAdapter
    private lateinit var pickupAdapter: PickUpAdapter
    private lateinit var mPrefs:SharedPreferences
    private var requestID:String =""
    private var companyName:String =""
    private var dateOrd:String =""
    private var user_id:String = ""
    private var status:String =""
    private var requestIDsub:String =""
    private var companyNamesub:String =""
    private var dateOrdsub:String =""
    private var user_idsub:String = ""
    private var statussub:String =""
    private var sub:String =""
    private lateinit var cpName:TextView
    private lateinit var dteOrd:TextView
    private lateinit var statusOrd:TextView
    private lateinit var openPickupLocation:ImageView
    private lateinit var btn_make_payment:Button

    private lateinit var viewModel: MainViewModel


    private val  mFireStore = FirebaseFirestore.getInstance()
    private lateinit var  rippleBackground: RippleBackground

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_destination)


        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this,viewModelFactory).get(MainViewModel::class.java)

        mPrefs = getSharedPreferences(Constants.USER_UUID, Context.MODE_PRIVATE);
       //user_uuid = mPrefs.getString("uuid",Constants.USER_UUID).toString()
        cpName = findViewById(R.id.orderName)
        dteOrd = findViewById(R.id.date_ordered)
        statusOrd = findViewById(R.id.orderStatus)
        openPickupLocation = findViewById(R.id.pickuppoint)
         rippleBackground = findViewById(R.id.content_ripple_destination)
        rippleBackground.startRippleAnimation()
        //btn_make_payment = findViewById(R.id.btn_make_payment)



        val intent = getIntent()

         requestID = intent.getStringExtra("request_id").toString()
         companyName = intent.getStringExtra("company_name").toString()
         dateOrd = intent.getStringExtra("date").toString()
         status = intent.getStringExtra("status").toString()
        user_id = intent.getStringExtra("user_id").toString()


        displayDestinationUITest(requestID)

        cpName.text= companyName
        dteOrd.text = dateOrd
        statusOrd.text = status




        recyclerView = findViewById(R.id.destinationRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)



       // EventChangedListener(this)
       // FireStoreClass().getAllRequestDistance(this)



        openPickupLocation.setOnClickListener { view ->
            StartOpenPickUpLocation(view)
        }



    }

   fun GetTotalPrice(requestID: String) {

        showProgressDialog("Loading request......")
        val destinationItemList = ArrayList<Destinations>()
        mFireStore.collection(Constants.COLLECTION_PARENT)
        .whereEqualTo("request_id", requestID)
        .get()
        .addOnSuccessListener { documents ->
            for (document in documents) {

                // val group_string:String= document.getData().toString()

                val map: Map<String, Any> = document.data
                for ((key, value) in map) {
                    if (key == "destinations") {
                        //  Log.d("ATAG","$key {$value}")
                        val dataMap = value as ArrayList<*>
                        val array = JSONArray(dataMap)
                        var pickup_locationname: String = ""

                        var totalsum = 0
                        for (i in 0 until array.length()) {
                            val destinationData = array.getJSONObject(i)

                            val price: Double = destinationData.getDouble("price")
                            totalsum  += price.toInt()
                            Log.i("MONT",totalsum.toString())


                            if (key == "pickup") {
                                val dataMap1 = value as HashMap<*, *>
                                val oBj = JSONObject(dataMap1)
                                // Log.d("ATAG","$key $oBj")


                                val pickup_location_lat = oBj.getDouble("pickup_location_lat")
                                val pickup_locationlng: Double =
                                    oBj.getDouble("pickup_locationlng")
                                pickup_locationname = oBj.getString("pickup_locationname")

                            }



                            // Whatever you want to do with these fields.
                        }


                        HideProgressDialog()

                    }

                }


            }
        }
        .addOnFailureListener { exception ->
            Log.w("TAG", "Error getting documents: ", exception)
        }


    }


    fun displayDestinationUITest(requestID:String) {
        showProgressDialog("Loading request......")
        val destinationItemList = ArrayList<Destinations>()
        mFireStore.collection(Constants.COLLECTION_PARENT)
            .whereEqualTo("request_id", requestID)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {

                   // val group_string:String= document.getData().toString()

                    val map: Map<String, Any> = document.data
                    for ((key, value) in map) {
                        if (key == "destinations") {
                            //  Log.d("ATAG","$key {$value}")
                            val dataMap = value as ArrayList<*>
                            val array = JSONArray(dataMap)
                            var pickup_locationname: String = ""

                            for (i in 0 until array.length()) {
                                val destinationData = array.getJSONObject(i)
                                val distance = destinationData.getDouble("distance")
                                val keyvalue: Double = destinationData.getDouble("key")
                                val price: Int = destinationData.getDouble("price").toInt()
                                val product_cost: String = destinationData.getString("product_cost")
                                val receiver_contact: String =
                                    destinationData.getString("receiver_contact")
                                val receiver_note: String =
                                    destinationData.getString("receiver_note")
                                val stoplocation_id: String =
                                    destinationData.getString("stoplocation_id")
                                val stoplocation_lat: Double =
                                    destinationData.getDouble("stoplocation_lat")
                                val stoplocation_lng: Double =
                                    destinationData.getDouble("stoplocation_lng")
                                val stoplocation_name: String =
                                    destinationData.getString("stoplocation_name")



                                if (key == "pickup") {
                                    val dataMap1 = value as HashMap<*, *>
                                    val oBj = JSONObject(dataMap1)
                                    // Log.d("ATAG","$key $oBj")


                                    val pickup_location_lat = oBj.getDouble("pickup_location_lat")
                                    val pickup_locationlng: Double =
                                        oBj.getDouble("pickup_locationlng")
                                      pickup_locationname = oBj.getString("pickup_locationname")

                                }


                                destinationItemList.add(
                                    Destinations(
                                        distance,
                                        keyvalue,
                                        price,
                                        product_cost,
                                        receiver_contact,
                                        receiver_note,
                                        stoplocation_id,
                                        stoplocation_lat,
                                        stoplocation_lng,
                                        stoplocation_name,

                                    )
                                )

                                // Whatever you want to do with these fields.
                            }




                            destinationAdapter = DestinationAdapter(this, destinationItemList,object:DestinationAdapter.HandleOpenDestinationClicked{
                                override fun onDestClicked(position: Int, destList: Destinations) {
                                    val lat:String = destList.stoplocation_lat.toString()
                                    val lng:String = destList.stoplocation_lng.toString()
                                    val destname:String = destList.stoplocation_name


                                    openMap(lat,lng,destname)

                                }

                                override fun onPaymentClicked(position: Int, destList: Destinations) {
                                   promptForPayment(position,destList)

                                }

                            })
                            recyclerView.adapter = destinationAdapter
                        HideProgressDialog()

                        }

                    }


                }
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents: ", exception)
            }

    }

    private fun promptForPayment(position: Int, destList: Destinations) {
            AlertDialog.Builder(this)
            .setTitle("Comfirm ${destList.stoplocation_name} request completed")
            .setMessage("You are about to start payment"+
                    " \n  click yes to continue and no to Exit")
            .setNegativeButton(android.R.string.no, null)
            .setPositiveButton(android.R.string.yes
            ) { arg0, arg1 ->

                howToPay(position,destList)
            }.create().show()

    }

    fun howToPay(position: Int, destList: Destinations){
        MaterialDialog.Builder(this)
            .title(getString(R.string.pay_with))
            .items(R.array.items_how_to_pay)
            .itemsCallbackSingleChoice(-1
            ) { dialog, view, which, text ->
                if (which == 0) {
                   PayWithMOMO(position,destList)
                }
                if (which == 1) {
                  PayWithCARD(position,destList)
                }
                if(which == 2){
                NetworkPaymentDialog(position,destList)
                }

                true
            }.show()
    }

   private fun PayWithMOMO(position: Int, destList: Destinations) {
       val phone = destList.receiver_contact
       val stopid = destList.stoplocation_id
       val price = destList.price.toString()
       val request_id = requestID
       val intent = Intent(this@DestinationActivity, MomoPaymentActivity::class.java)
          intent.putExtra("phone",phone)
          intent.putExtra("price",price)
          intent.putExtra("stopid",stopid)
          intent.putExtra("request_id",requestID)

       startActivity(intent)

    }

  private fun PayWithCARD(position: Int, destList: Destinations){
     val price: String = destList.price.toString()
     val stopid: String = destList.stoplocation_id.toString()
     val phone: String = destList.receiver_contact.toString()
      val paymentType:String = "Card"

      MaterialDialog.Builder(this@DestinationActivity)
          .title("Pay with Card")
          .content("To pay with Card follow the instructions\n Enter your phone number if you \n want to pay through new number \nA link will be sent to you shortly \n Click on it to proceed with payment")

          .positiveText("Continue")
          .onPositive(MaterialDialog.SingleButtonCallback { dialog, which ->
              MaterialDialog.Builder(this@DestinationActivity)
                  .title("Proceed with Payment")
                  .content("Enter a new number or use existing  \none to receive the link for payment")
                  .positiveText("New")
                  .negativeText("Use old number")
                  .onPositive(MaterialDialog.SingleButtonCallback { dialog, which -> // startActivity(new Intent(NewPaymentActivity.this, MeterActivity.class));
                      enterphoneNumber(price, requestID, stopid, phone, paymentType)

                  })
                  .onNegative(MaterialDialog.SingleButtonCallback { dialog, which ->
                      useOldPhoneNumber(price, requestID, stopid, phone, paymentType)


                  })
                  .show()
          })
          .show()


  }

    private fun useOldPhoneNumber(
        price: String,
        requestID: String,
        stopid: String,
        phone: String,
        paymentType: String) {

        showProgressDialog("mtn payment...")
        // viewModel.postPayment(price,requestId,stopid,phone,paymentType)
        val myPost = PaymentData(price,requestID,stopid,phone,paymentType)
        Log.i("PARAMCARD","$price \n $requestID \n $stopid \n $phone \n $paymentType")


        viewModel.pushPost(myPost)
        viewModel.myResponse.observe(this, Observer { data->
            // val adapter = myAdapter(this, data)
            // recyclerView.adapter = adapter
            if (data.isSuccessful){
                HideProgressDialog()
                Log.d("MainCARD",data.body().toString())
                Log.d("MainCARD",data.code().toString())
                Log.d("MainCARD",data.message())

            }else {
                HideProgressDialog()
                Log.d("ERRORCARD",data.code().toString())
                Log.d("ERRORCARD",data.message())
            }

        })


    }

    private fun enterphoneNumber(
        price: String,
        requestId: String,
        stopid: String,
        phone: String,
        paymentType: String) {

        MaterialDialog.Builder(this)
            .title("Enter Phone number")
            .content("Enter your phone number if you  .\n want to pay through new number \nA link will be sent to you shortly \n Click on it to proceed with payment")
            .positiveText(R.string.positive_button_txt)
            .inputRangeRes(12, 12, R.color.md_red_500)
            .inputType(InputType.TYPE_CLASS_PHONE)
            .input(R.string.input_hint, R.string.input_prefill) { dialog, input ->
                val new_numver = input.toString()
                postCardPaymentRequest(price,requestId,stopid,new_numver,paymentType)
            }

            .show()

    }

    private fun postCardPaymentRequest(
        price: String,
        requestId: String,
        stopid: String,
        phone: String,
        paymentType: String) {

        showProgressDialog("Card payment...")
        // viewModel.postPayment(price,requestId,stopid,phone,paymentType)
        val myPost = PaymentData(price,requestId,stopid,phone,paymentType)
        Log.i("PARAMCARD","$price \n $requestId \n $requestId \n $stopid \n $phone \n $paymentType")


        viewModel.pushPost(myPost)
        viewModel.myResponse.observe(this, Observer { data->
            // val adapter = myAdapter(this, data)
            // recyclerView.adapter = adapter
            if (data.isSuccessful){
                HideProgressDialog()
                Log.d("MainCARD",data.body().toString())
                Log.d("MainCARD",data.code().toString())
                Log.d("MainCARD",data.message())

            }else {
                HideProgressDialog()
                Log.d("ERRORCARD",data.code().toString())
                Log.d("ERRORCARD",data.message())
            }

        })

    }


    private fun NetworkPaymentDialog(position: Int, destList: Destinations) {

            var network: String
            val content = ""
        MaterialDialog.Builder(this)
                .title(R.string.requirement_how_to_pay)
                .items(R.array.items_network)
                .itemsCallbackSingleChoice(-1
                ) { dialog, view, which, text ->
                    if (which == 0) {
                        gethelpContent(Constants.NETWORK_AIRTEL)
                    }
                    if (which == 1) {
                        gethelpContent(Constants.NETWORK_MTN)
                    }
                    if (which == 2) {
                        gethelpContent(Constants.NETWORK_TIGO)
                    }
                    if (which == 3) {
                        gethelpContent(Constants.NETWORK_VODAFONE)
                    }
                    true
                }.show()


    }


    private fun gethelpContent(network: String) {
        val intent = Intent(this@DestinationActivity, HowToPayActivity::class.java)
        intent.putExtra("network", network)
        startActivity(intent)
    }

    fun StartOpenPickUpLocation(view: View?) {

            showProgressDialog("Loading......")
            val pickupItemList = ArrayList<PickUp>()
            mFireStore.collection(Constants.COLLECTION_PARENT)
                .whereEqualTo("request_id", requestID)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        // val group_string:String= document.getData().toString()

                        val map: Map<String, Any> = document.data
                        for ((key, value) in map) {

                            if (key == "pickup") {
                                val dataMap1 = value as HashMap<*, *>
                                val oBj = JSONObject(dataMap1)
                                // Log.d("ATAG","$key $oBj")


                                val pickup_location_lat = oBj.getDouble("pickup_location_lat").toString()
                                val pickup_locationlng = oBj.getDouble("pickup_locationlng").toString()
                                val pickup_locationName: String = oBj.getString("pickup_locationname")
                               // Log.i("TEST", pickup_location_lat.toString())
                               // Log.i("TEST", pickup_locationlng.toString())
                                openMap(pickup_location_lat,pickup_locationlng,pickup_locationName)
                               HideProgressDialog()

                            }
                        }




                            }



                }
                .addOnFailureListener { exception ->
                    Log.w("TAG", "Error getting documents: ", exception)
                    HideProgressDialog()
                }


        }
    fun openMap(lat:String,lng:String,loctationName:String){
        val alert: AlertDialog.Builder = AlertDialog.Builder(this)
        val mView: View = getLayoutInflater().inflate(R.layout.custom_dialog, null)
        //val txt_inputText = mView.findViewById(R.id.txt_input) as EditText
        val btn_name: Button = mView.findViewById(R.id.locationName) as Button
        btn_name.text = loctationName
        val btn_cancel: Button = mView.findViewById(R.id.btn_cancel) as Button
        val btn_okay: Button = mView.findViewById(R.id.btn_okay) as Button
        alert.setView(mView)
        val alertDialog: AlertDialog = alert.create()
        alertDialog.setCanceledOnTouchOutside(false)
        btn_cancel.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                alertDialog.dismiss()
            }
        })
        btn_okay.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                // myCustomMessage.setText(txt_inputText.text.toString())
                //alertDialog.dismiss()

                val gmmIntentUri: Uri = Uri.parse("google.navigation:q=$lat,$lng")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                startActivity(mapIntent)

            }
        })
        alertDialog.show()
    }




}







