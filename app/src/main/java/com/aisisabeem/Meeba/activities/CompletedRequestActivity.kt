package com.aisisabeem.Meeba.activities


import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aisisabeem.Meeba.R
import com.aisisabeem.Meeba.home.adapters.CompletedAdapter
import com.aisisabeem.Meeba.home.adapters.RequestAdapter
import com.aisisabeem.Meeba.home.adapters.SubRequestCompletedAdapter
import com.aisisabeem.Meeba.home.destination.Destinations
import com.aisisabeem.Meeba.models.Completed
import com.aisisabeem.Meeba.models.Requests
import com.aisisabeem.Meeba.models.SubRequestCompleted
import com.aisisabeem.Meeba.utils.BaseActivity
import com.aisisabeem.Meeba.utils.Constants
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import org.json.JSONArray
import org.json.JSONObject


class CompletedRequestActivity : BaseActivity() {
    private val mFireStore = FirebaseFirestore.getInstance()
    private lateinit var recyclerView: RecyclerView

    private lateinit var requestArrayList:ArrayList<Completed>
    private lateinit var requestAdapter: CompletedAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_completed_request)


        EventChangedListener()

        recyclerView = findViewById(R.id.rv_completed_request)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

       // requestArrayList = arrayListOf()

         // fetchCompletedRequest()


    }



    private fun EventChangedListener(){

        showProgressDialog("Loading Completed request......")
        val destinationItemList = ArrayList<Completed>()
        val riderID = FirebaseAuth.getInstance().uid.toString()
        val rootRef = FirebaseFirestore.getInstance()
        val applicationsRef = rootRef.collection(Constants.RIDER_REQUEST_USER)
        val applicationIdRef = applicationsRef.document(riderID)
        applicationIdRef
            .get()
            .addOnCompleteListener { task: Task<DocumentSnapshot?> ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document!!.exists()) {
                        val riderCompletedData = document[riderID] as List<*>?
                        //Log.d("ATAGQQQ",riderCompletedData.toString())
                        var array = JSONArray(riderCompletedData)

                        for (i in 0 until array.length()){
                            val completedData = array.getJSONObject(i)
                            val acceptedStatus = completedData.getString("Accepted_Status").toString()
                            val companyName = completedData.getString("company_name").toString()
                            val completedTime = completedData.getString("completed_time")

                            val option = completedData.getString("option").toString()
                            val status = completedData.getString("status").toString()
                            val userid = completedData.getString("user_id").toString()

                            destinationItemList.add(
                               Completed(
                                    acceptedStatus,companyName,completedTime,option,status,userid
                                )
                            )
                        }
                        requestAdapter = CompletedAdapter(this,destinationItemList)
                        recyclerView.adapter = requestAdapter


                        requestAdapter.notifyDataSetChanged()

                        HideProgressDialog()


                    }

                }
            }

    }





    private fun fetchCompletedRequest() {

      //  val rideruid = FirebaseAuth.getInstance().uid.toString()

     //  showProgressDialog("Please wait....")



//        mFireStore.collection(Constants.RIDER_REQUEST_USER)
//            .document(rideruid)
//            .get()
//            .addOnSuccessListener { documents ->
//
//                 if (documents != null){
//                    val map: MutableMap<String, Any>? = documents.data
//                     Log.d("MAWULI", " $map \n")
//                     if (map != null) {
//                         val tvText:TextView = findViewById(R.id.tvtext)
//                         val str = StringBuilder()
//                         for ((key, value) in map) {
//                             val dataMap1 = value as HashMap<*, *>
//                            val oBj = JSONObject(dataMap1)
//                             val np = oBj.getString("company_name")
//                             tvText.text = str.append("$np \n")
//                             Log.i("MAWULI","Value is $np")
//
//
//                         }
//                         HideProgressDialog()
//                     }
//
//
//                 }
//
//                HideProgressDialog()
//            }
//
//            .addOnFailureListener { exception ->
//                HideProgressDialog()
//                Log.w("TAG", "Error getting documents: ", exception)
//            }













        showProgressDialog("Please wait a minute.....")
        val rideruid = FirebaseAuth.getInstance().uid.toString()
       // mFireStore.collection(Constants.RIDER_REQUEST_USER)
        val docRef =  mFireStore.collection(Constants.RIDER_REQUEST_USER)
            .document(rideruid)
            docRef.get()
            .addOnSuccessListener { documents ->

                if (documents != null) {
                    val data = documents.data!!
                    var job = JSONObject(data)

                    Log.i("ATA",job.toString())
                  
                    //var map =  ArrayMap<String, Any>()
                   // map = data as ArrayMap<String, Any>
                   // val map: Map<String, Any?> = data as Map<String, Any?>
//                    for ((key, value) in map) {
//
//                        val dataMap1 = value as HashMap<*, *>
//                        val oBj = JSONObject(dataMap1)
//
//
//                        val pickup_location_lat = oBj.getString("company_name").toString()
//                        Log.i("MAGA",pickup_location_lat)
//
//                        HideProgressDialog()
//
//                    }





                    //Log.d("TAGA", "DocumentSnapshot $data")
                } else {
                    Log.d("TAGA", "No such document")
                }
                HideProgressDialog()
            }
            .addOnFailureListener { exception ->
                HideProgressDialog()
                Log.d("TAGA", "get failed with ", exception)
            }






    }

    }