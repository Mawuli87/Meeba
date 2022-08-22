package com.aisisabeem.Meeba.sub_requests



import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aisisabeem.Meeba.R
import com.aisisabeem.Meeba.home.destination.SubDestination
import com.aisisabeem.Meeba.home.destination.SubDestinationAdapter
import com.aisisabeem.Meeba.home.pickup.PickUp
import com.aisisabeem.Meeba.home.pickup.PickUpAdapter
import com.aisisabeem.Meeba.utils.BaseActivity
import com.aisisabeem.Meeba.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import com.skyfishjy.library.RippleBackground
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class SubDestinationActivity : BaseActivity() {

    //requests
    private lateinit var recyclerView: RecyclerView
    private lateinit var subdestinationAdapter: SubDestinationAdapter
    private lateinit var pickupAdapter: PickUpAdapter
    private var requestIDsub:String =""
    private var companyNamesub:String =""
    private var dateOrdsub:String =""
    private var user_idsub:String = ""
    private var statussub:String =""
    private var sub:String =""
    private lateinit var cpName: TextView
    private lateinit var dteOrd: TextView
    private lateinit var statusOrd: TextView
    private lateinit var openPickupLocation: ImageView


    private val  mFireStore = FirebaseFirestore.getInstance()
    private lateinit var  rippleBackground: RippleBackground

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub_destination)
       // supportActionBar?.setDisplayHomeAsUpEnabled(true)

       // mPrefs = getSharedPreferences(Constants.USER_UUID, Context.MODE_PRIVATE);
        //user_uuid = mPrefs.getString("uuid",Constants.USER_UUID).toString()
        cpName = findViewById(R.id.orderName)
        dteOrd = findViewById(R.id.date_ordered)
        statusOrd = findViewById(R.id.orderStatus)
        openPickupLocation = findViewById(R.id.pickuppoint)
        rippleBackground = findViewById(R.id.content_ripple_destination)
        rippleBackground.startRippleAnimation()



        requestIDsub = intent.getStringExtra("request_idsub").toString()
        companyNamesub = intent.getStringExtra("company_namesub").toString()
        dateOrdsub = intent.getStringExtra("datesub").toString()
        statussub = intent.getStringExtra("statussub").toString()
        user_idsub = intent.getStringExtra("user_idsub").toString()
        sub = intent.getStringExtra("subRequestsub").toString()

        displaySubDestinationsData(requestIDsub)
        cpName.text= companyNamesub
        dteOrd.text = dateOrdsub
        statusOrd.text = statussub


        recyclerView = findViewById(R.id.destinationRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        openPickupLocation.setOnClickListener { view ->
            StartOpenPickUpLocation(view)
        }
    }



    private fun displaySubDestinationsData(requestIDsub: String)
    = CoroutineScope(Dispatchers.IO).launch {

        withContext(Dispatchers.Main){
            showProgressDialog("Loading subs request......")
        }
        val destinationSubItemList = ArrayList<SubDestination>()
        mFireStore.collection(Constants.COLLECTION_PARENT_SUB)
            .whereEqualTo("request_id", requestIDsub)
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


                                destinationSubItemList.add(
                                    SubDestination(
                                        distance,
                                        keyvalue,
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


                            subdestinationAdapter = SubDestinationAdapter(this@SubDestinationActivity,
                                destinationSubItemList,
                                object:SubDestinationAdapter.HandleOpenSubDestinationClicked{
                                    override fun onDestClicked(position: Int, destSubList: SubDestination) {
                                        val lat:String = destSubList.stoplocation_lat.toString()
                                        val lng:String = destSubList.stoplocation_lng.toString()
                                        val destname:String = destSubList.stoplocation_name


                                        openMap(lat,lng,destname)
                                    }


                                })
                            recyclerView.adapter =  subdestinationAdapter
                            HideProgressDialog()

                        }

                    }


                }
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents: ", exception)
            }


    }



    fun StartOpenPickUpLocation(view: View?) {

        showProgressDialog("Loading......")
        val pickupItemList = ArrayList<PickUp>()
        mFireStore.collection(Constants.COLLECTION_PARENT_SUB)
            .whereEqualTo("request_id", requestIDsub)
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
