package com.aisisabeem.Meeba.sub_requests




import android.app.Activity
import android.app.PendingIntent.getActivity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aisisabeem.Meeba.R
import com.aisisabeem.Meeba.firebase.FireStoreClass
import com.aisisabeem.Meeba.home.adapters.SubRequestAdapter
import com.aisisabeem.Meeba.home.adapters.SubRequestCompletedAdapter
import com.aisisabeem.Meeba.models.SubRequestCompleted
import com.aisisabeem.Meeba.models.SubRequests
import com.aisisabeem.Meeba.utils.BaseActivity
import com.aisisabeem.Meeba.utils.Constants
import com.google.android.gms.tasks.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.util.*


class SubRequestActivity : BaseActivity(), SubRequestAdapter.HandleCompleteSubRequest {
    private lateinit var db:FirebaseFirestore
    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var subRequestArrayList:ArrayList<SubRequests>
    private lateinit var subRequestAdapter: SubRequestAdapter
    private lateinit var subRequestCompleteAdapter: SubRequestCompletedAdapter
    private lateinit var nodataYet: TextView
    private lateinit var fab: FloatingActionButton
    private lateinit var RiderUserId: String
    private lateinit var mAuth:FirebaseAuth
    private lateinit var subReqValue:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub_request)


          //get value from subcompleted intent
        subReqValue = intent.getStringExtra("sub").toString()


        RiderUserId = FireStoreClass().getCurentUserId()
        mAuth = FirebaseAuth.getInstance()
        mFirestore = FirebaseFirestore.getInstance()
         if (networkAvailable()) {
             EventChangedListenerSub()
         }else{
             onData()
         }

       // supportActionBar?.setDisplayHomeAsUpEnabled(true)
        nodataYet = findViewById(com.aisisabeem.Meeba.R.id.noDataYetSub)
        recyclerView = findViewById(com.aisisabeem.Meeba.R.id.requestsRecyclerViewSub)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        subRequestArrayList = arrayListOf()
         subRequestAdapter =SubRequestAdapter(this,subRequestArrayList,object :SubRequestAdapter.HandleSubDestinationClicked{
             override fun onSubDistClicked(position: Int, subDestList: SubRequests) {
                 fab = findViewById(R.id.fabsub)
                 fab.visibility = View.GONE

                fab.setOnClickListener {
                    startActivity(Intent(this@SubRequestActivity,SubRequestActivity::class.java))
                }

                 val userID: String = subDestList.user_id!!
                 val requestID:String = subDestList.request_id!!
                 val companyName:String = subDestList.company_name!!
                 val date:String = subDestList.createdAt.toString()
                 val date_one: java.util.Date? = subDestList.createdAt
                 val status:String = subDestList.status!!


                 //updateRequestWithriderId(userID,requestID,RiderUserId)
                 val updaterequest = SubRequests(companyName,date_one,false,requestID,"Accepted","NotCompleted",userID,RiderUserId)


                 FireStoreClass().updateSubRequestWithRiderID(this@SubRequestActivity,updaterequest)
                 //toast for request approval
                 Toast.makeText(this@SubRequestActivity,"Sub-Request Approved", Toast.LENGTH_LONG).show()



                 val intent = Intent(this@SubRequestActivity, SubDestinationActivity::class.java)

                 intent.putExtra("user_idsub",userID)
                 intent.putExtra("request_idsub",requestID)
                 intent.putExtra("company_namesub",companyName)
                 intent.putExtra("datesub",date)
                 intent.putExtra("statussub","Accepted")
                 intent.putExtra("subRequestsub","Sub")
                 startActivity(intent)
             }

         },this)

        recyclerView.adapter = subRequestAdapter

          //get riders completed requests
        if (subReqValue == "SubCompleted"){

            Handler().postDelayed({

                displaySubRiderCompleted()
                //fetchSubCompletedRequest()
            }, 2000)

        }

    }


    private fun EventChangedListenerSub() = CoroutineScope(Dispatchers.IO).launch{
        withContext(Dispatchers.Main) {
            showProgressDialog("Please wait loading sub-requests...")
        }

        db = FirebaseFirestore.getInstance()
        db.collection(Constants.COLLECTION_PARENT_SUB)
            .addSnapshotListener(object : EventListener<QuerySnapshot> {

                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null){

                        Log.e("FireStore Error",error.message.toString())
                        return
                    }
                   // HideProgressDialog()
                    for (doc: DocumentChange in value?.documentChanges!!){
                        var reqStatus: Any
                        var reqOption: String? = ""

                        var deliveryarranged:Boolean  = false
                        if (doc.type == DocumentChange.Type.ADDED){
                           // Log.i("SUB","Doc ${doc.document}")
                               val docID = doc.document.id
                            reqStatus = doc.document.data.get("status")!!
                            reqOption = doc.document.data.get("option") as String?
                           //  Log.i("HOMESUB","DOC: $reqOption \n $reqStatus")
                            if (reqStatus == "ongoing" || reqOption == "NotCompleted") {
                               nodataYet.visibility = View.GONE

                               subRequestArrayList.add(doc.document.toObject(SubRequests::class.java))

                            }

                        }
                    }
                    HideProgressDialog()

                   subRequestAdapter.notifyDataSetChanged()
                }


            })


    }

    override fun onSubRequestComple(position: Int, subDestList: SubRequests, valueTag: String) {

        if (valueTag == "Complete Sub-Request"){
            openSubDialog(position, subDestList)

        }

    }

    fun openSubDialog(position: Int, destList: SubRequests) {

        UpdateRiderrequestSubRequestCompleted(position,destList)
    }



    fun UpdateRiderrequestSubRequestCompleted(position: Int, destList: SubRequests) {
        showProgressDialog("Please wait.......")
        //updateRequestWithriderId(userID,requestID,RiderUserId)
      val deliveryarranged:Boolean = true
        val currentTime: String = Calendar.getInstance().getTime().toString()

        val completeRequestHashMap = hashMapOf<String,Any>(
            "rider_id" to destList.rider_id.toString(),
            "Accepted_Status" to "Request Completed",
            "option" to "Delivery Arranged",
            "status" to "Accepted",

            "company_name" to destList.company_name.toString(),
            "user_id" to destList.user_id.toString(),
            "completed_time" to currentTime
        )

        val userHashMap = hashMapOf<String, Any>(
            "rider_id" to destList.rider_id.toString(),
            "Accepted_Status" to "Request Completed",
            "option" to "Delivery Arranged",
            "deliveryarranged" to true,
            "status" to "Accepted"
        )

        FireStoreClass().updateRequestInMainSubCollection(this@SubRequestActivity,userHashMap,destList,completeRequestHashMap)

        // FireStoreClass().updateRequestAndRiderCompleted(this@HomeActivity,completeRequestHashMap,destList)

        fab = findViewById(R.id.fabsub)
        fab.visibility = View.VISIBLE

        val intent = Intent(this@SubRequestActivity, SubRequestActivity::class.java)
        startActivity(intent)
        HideProgressDialog()
    }



    override fun UpdateRiderSubrequestCompleted(position: Int, subDestList: SubRequests) {
        TODO("Not yet implemented")
    }

    override fun viewSubRequest(destList: SubRequests) {
        val userID: String = destList.user_id!!
        val requestID:String = destList.request_id!!
        val companyName:String = destList.company_name!!
        val date:String = destList.createdAt.toString()
        val date_one: java.util.Date? = destList.createdAt
        val status:String = destList.status!!

        val intent = Intent(this@SubRequestActivity, SubDestinationActivity::class.java)

        intent.putExtra("user_idsub",userID)
        intent.putExtra("request_idsub",requestID)
        intent.putExtra("company_namesub",companyName)
        intent.putExtra("datesub",date)
        intent.putExtra("statussub","Accepted")
        intent.putExtra("subRequestsub","Sub")
        startActivity(intent)
    }

   fun displaySubRiderCompleted(){
       showProgressDialog("Loading request......")
       val riderID = FirebaseAuth.getInstance().uid.toString()
       val completedItemList = ArrayList<SubRequestCompleted>()
       val rootRef = FirebaseFirestore.getInstance()
       val applicationsRef = rootRef.collection(Constants.RIDER_SUB_REQUEST_USER)
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

                       completedItemList.add(
                           SubRequestCompleted(
                              acceptedStatus,companyName,completedTime,option,status,userid
                           )
                       )
                   }

                   HideProgressDialog()



                   val alertDialog = AlertDialog.Builder(this,android.R.style.Theme_DeviceDefault_NoActionBar_Fullscreen)

                   alertDialog.setNeutralButton("CLOSE"){dialog,which->
                       // do something on neutral button click
                       dialog.dismiss()
                   }
                   alertDialog.setPositiveButtonIcon(getDrawable(R.drawable.ic_close))



                   val inflater = layoutInflater
                   val convertView: View = inflater.inflate(R.layout.sub_requests_completed_list, null)
                   alertDialog.setView(convertView)



                   val list: RecyclerView = convertView.findViewById(R.id.rv_completed_sub_request)
                  // val close:ImageView = convertView.findViewById(R.id.closeDialog)
                   list.layoutManager = LinearLayoutManager(this)
                   list.setHasFixedSize(true)

                   val adapter = SubRequestCompletedAdapter(this,completedItemList)
                   list.adapter = adapter




                   alertDialog.show()
                   adapter.notifyDataSetChanged()


               }

           }
       }
   }


    fun getScreenWidth(activity: Activity): Int {
        val size = Point()
        activity.windowManager.defaultDisplay.getSize(size)
        return size.x
    }



     private fun  fetchSubCompletedRequest(){
        // val reference = Firebase.firestore
         val riderID = FirebaseAuth.getInstance().uid.toString()
             mFirestore.collection(Constants.RIDER_SUB_REQUEST_USER)
             .document(riderID).get()
             .addOnSuccessListener { documentSnapshot ->

//                 val map = documentSnapshot.data!!
//                 for ((key, value) in map) {
//                     if (key == riderID) {
//                         Log.d("TAGM", value.toString())
//                     }
//                 }

                 //val document: DocumentSnapshot = documentSnapshot.getResult()
                // val group = document["dungeon_group"] as List<String>?

                 if (documentSnapshot.exists()) {
                    // reference.update("usersBlocked", FieldValue.arrayUnion(userIDToBlock))
                     val data = documentSnapshot.data?.get(riderID)
                     Log.i("TAGA",data.toString())
                     val array = JSONArray(data)
                     for (i in 0 until array.length()){
                         val destinationData = array.getJSONObject(i)
                         val companyName = destinationData.getDouble("company_name")
                         Log.i("ARRAY",companyName.toString())
                     }
                     HideProgressDialog()
                 } else {
//                     val userdetail = HashMap<String, Any>()
//                     var usersBlockedList = arrayListOf<String>()
//                     usersBlockedList.add(userIDToBlock)
//                     userdetail["usersBlocked"] = usersBlockedList
//                     Firebase.firestore.collection("usersBlocked").document(userIDBlocker).set(userdetail)
//                         .addOnSuccessListener { success ->
//
//                         }
//                         .addOnFailureListener { exception ->
//                             Log.e("Data Failed", "To added because ${exception}")
//                         }
                       HideProgressDialog()
                     Toast.makeText(this,"Please you do not have any completed request",Toast.LENGTH_LONG).show()

                 }
             }
             .addOnFailureListener { exception ->
                 HideProgressDialog()
                 Log.e("Exception", "${exception}")
             }
     }



}