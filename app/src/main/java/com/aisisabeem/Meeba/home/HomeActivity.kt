package com.aisisabeem.Meeba.home




import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aisisabeem.Meeba.R
import com.aisisabeem.Meeba.activities.CompletedRequestActivity
import com.aisisabeem.Meeba.firebase.FireStoreClass
import com.aisisabeem.Meeba.home.adapters.RequestAdapter
import com.aisisabeem.Meeba.home.adapters.SubRequestCompletedAdapter
import com.aisisabeem.Meeba.home.destination.DestinationActivity
import com.aisisabeem.Meeba.models.Requests
import com.aisisabeem.Meeba.models.SubRequestCompleted
import com.aisisabeem.Meeba.models.User
import com.aisisabeem.Meeba.sub_requests.SubRequestActivity
import com.aisisabeem.Meeba.utils.BaseActivity
import com.aisisabeem.Meeba.utils.Constants
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.util.*


class HomeActivity : BaseActivity(),RequestAdapter.HandleCompleteRequest {
    lateinit var toggle: ActionBarDrawerToggle


    private var doubleBackToExitOnce = false

    private lateinit var userName:TextView
    private lateinit var userEmail:TextView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var RiderUserId: String
    private lateinit var mAuthListener: AuthStateListener
    private lateinit var navView: NavigationView

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var profile_image: de.hdodenhof.circleimageview.CircleImageView
    private lateinit var tv_name: TextView
    private lateinit var fab:FloatingActionButton
    //firestore
   // var db: FirebaseFirestore? = FirebaseFirestore.getInstance()
    //requests
    private lateinit var recyclerView: RecyclerView

    private lateinit var requestArrayList:ArrayList<Requests>
    private lateinit var requestAdapter: RequestAdapter
    private lateinit var db:FirebaseFirestore
    private lateinit var nodataYet:TextView


    private lateinit var appBarID:LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        mAuth = FirebaseAuth.getInstance();
        val currentUser = mAuth.currentUser
        if (currentUser == null) {
            startActivity(Intent(this, SignInActivity::class.java))
        }



        FireStoreClass().loadUserData(this)

        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.nav_view)
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        RiderUserId = FireStoreClass().getCurentUserId()
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        nodataYet = findViewById(R.id.noDataYet)
         fab = findViewById(R.id.fab)

        fab.setOnClickListener{

            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }


        recyclerView = findViewById(R.id.requestsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        requestArrayList = arrayListOf()
        requestAdapter = RequestAdapter(this,requestArrayList,object:RequestAdapter.HandleDestinationClicked{
            override fun onDistClicked(position: Int, destList: Requests) {
               // val destNat =requestArrayList[position]

                fab = findViewById(R.id.fab)
                fab.visibility = View.GONE


                val userID: String = destList.user_id!!
                val requestID:String = destList.request_id!!
                val companyName:String = destList.company_name!!
                val date:String = destList.createdAt.toString()
                val date_one: java.util.Date? = destList.createdAt
                val status:String = destList.status!!

               //updateRequestWithriderId(userID,requestID,RiderUserId)
                val updaterequest = Requests(companyName,date_one,requestID,"Accepted","NotCompleted",userID,RiderUserId)


                FireStoreClass().updateRequestWithRiderID(this@HomeActivity,updaterequest)
                //toast for request approval
                Toast.makeText(this@HomeActivity,"Request Approved",Toast.LENGTH_LONG).show()

                val intent = Intent(this@HomeActivity,DestinationActivity::class.java)

                intent.putExtra("user_id",userID)
                intent.putExtra("request_id",requestID)
                intent.putExtra("company_name",companyName)
                intent.putExtra("date",date)
                intent.putExtra("status","Accepted")
                startActivity(intent)

            }

        },this,this)
        recyclerView.adapter = requestAdapter

        //calling webservice from coroutine
      if (networkAvailable()){
          EventChangedListener()
      }else {
          onData()
      }




        //signuser is called here
        FireStoreClass().signInUser(this)
        navView.setNavigationItemSelectedListener {

            when (it.itemId) {

                R.id.nav_new_request -> goToHomeActivity()
                R.id.nav_completed_request -> goToCompletedRequestActivity()
                R.id.nav_logout -> logoutUser()
                R.id.nav_profile -> userProfile()
                R.id.nav_sub_requests -> subRequest()
                R.id.nav_sub_requests_completed ->getSubRequestCompleted()


                R.id.nav_share -> Toast.makeText(applicationContext, "Share", Toast.LENGTH_SHORT)
                    .show()
                R.id.nav_rate_us -> Toast.makeText(
                    applicationContext,
                    "Rate us",
                    Toast.LENGTH_SHORT
                ).show()

            }

            true


        }



    }

    private fun getSubRequestCompleted(){
        val subCompleted = "SubCompleted"
        val intent = Intent(this,SubRequestActivity::class.java)
        intent.putExtra("sub",subCompleted)
        startActivity(intent)
    }

    private fun subRequest(){
        val intent = Intent(this,SubRequestActivity::class.java)
        startActivity(intent)
    }

    private fun goToHomeActivity(){
        val intent = Intent(this,HomeActivity::class.java)
        startActivity(intent)
    }

    private fun goToCompletedRequestActivity(){
        val intent = Intent(this,CompletedRequestActivity::class.java)
        startActivity(intent)
    }


    private fun EventChangedListener() = CoroutineScope(Dispatchers.IO).launch{

        withContext(Dispatchers.Main){
            showProgressDialog("Please wait loading requests...")
        }
    db = FirebaseFirestore.getInstance()
    db.collection(Constants.COLLECTION_PARENT)
        .addSnapshotListener(object : EventListener<QuerySnapshot>{

            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null){

                        Log.e("FireStore Error",error.message.toString())
                        return
                    }
                HideProgressDialog()
                for (doc:DocumentChange in value?.documentChanges!!){
                    var reqStatus: Any
                    var reqOption: String? = ""

                    var deliveryarranged:Boolean  = false
                    if (doc.type == DocumentChange.Type.ADDED){
                        Log.i("HOMED","DOC: ${doc.document} \n")
                        reqStatus = doc.document.data.get("status")!!
                        reqOption = doc.document.data.get("option") as String?
                       Log.i("HOME","DOC: $reqOption \n $reqStatus")
                        if (reqStatus == "ongoing" || reqOption == "NotCompleted") {
                            nodataYet.visibility = View.GONE
                            requestArrayList.add(doc.document.toObject(Requests::class.java))

                            }

                    }
                }
                HideProgressDialog()

                requestAdapter.notifyDataSetChanged()
            }


        })


}


    override fun onBackPressed() {

        AlertDialog.Builder(this)
            .setTitle("Exit App")
            .setMessage("Are you sure you want to exit?")
            .setNegativeButton(android.R.string.no, null)
            .setPositiveButton(android.R.string.yes
            ) { arg0, arg1 ->
               finishAffinity()
            }.create().show()
    }

    fun updateNavigationUserDetails(loggedInUser: User) {
        profile_image = findViewById(R.id.profile_nav_header)
        tv_name = findViewById(R.id.user_name)
        Glide
            .with(this)
            .load(loggedInUser.image)
            .centerCrop()
            .placeholder(R.drawable.ic_launcher_background)
            .into(profile_image);
        tv_name.text = loggedInUser.name
    }





    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (toggle.onOptionsItemSelected(item)) {
            return true

        }
        return super.onOptionsItemSelected(item)
    }


    fun riderIdUpdateSuccess(requestOwner: String) {
       // Toast.makeText(this,"Request Completed Successfully Updated",Toast.LENGTH_LONG).show()
           AlertDialog.Builder(this)
            .setTitle("Request Completed")
            .setMessage("$requestOwner request \n is successfully completed")
            .setNegativeButton(android.R.string.no, null)
            .setPositiveButton(android.R.string.yes
            ) { arg0, arg1 ->
              // arg0.dismiss()
                startActivity(Intent(this, HomeActivity::class.java))
            }.create().show()
    }

    override fun onRequestComple(position: Int, destList: Requests,valueTag:String) {
       // Toast.makeText(this,"Good completed",Toast.LENGTH_LONG).show()

             if (valueTag == "Complete Request"){
                openDialog(position, destList)

             }

    }

    override fun openDialog(position: Int, destList: Requests) {
//        AlertDialog.Builder(this)
//            .setTitle("Comfirm ${destList.company_name} completed")
//            .setMessage("Order date and time \n ${destList.createdAt}" +
//                    " \n Order Status: Not yet Completed")
//            .setNegativeButton(android.R.string.no, null)
//            .setPositiveButton(android.R.string.yes
//            ) { arg0, arg1 ->
//              // arg0.dismiss()
//            }.create().show()

        UpdateRiderrequestCompleted(position,destList)
    }

    override fun UpdateRiderrequestCompleted(position: Int, destList: Requests) {
          showProgressDialog("Please wait.......")
        //updateRequestWithriderId(userID,requestID,RiderUserId)

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
        )

        FireStoreClass().updateRequestInMainCollection(this@HomeActivity,userHashMap,destList,completeRequestHashMap)

       // FireStoreClass().updateRequestAndRiderCompleted(this@HomeActivity,completeRequestHashMap,destList)

        fab = findViewById(R.id.fab)
        fab.visibility = View.VISIBLE

        val intent = Intent(this@HomeActivity,HomeActivity::class.java)
         startActivity(intent)
        HideProgressDialog()
    }

    override fun viewRequest(destList: Requests) {
        val intent = Intent(this@HomeActivity,DestinationActivity::class.java)

        val userID: String = destList.user_id!!
        val requestID:String = destList.request_id!!
        val companyName:String = destList.company_name!!
        val date:String = destList.createdAt.toString()
        val date_one: java.util.Date? = destList.createdAt
        val status:String = destList.status!!

        intent.putExtra("user_id",userID)
        intent.putExtra("request_id",requestID)
        intent.putExtra("company_name",companyName)
        intent.putExtra("date",date)
        intent.putExtra("status","Accepted")
        startActivity(intent)
    }




//    fun loadDataFromFirStore(){


    fun displayMainRiderCompleted(){
        showProgressDialog("Loading Completed request......")
        val riderID = FirebaseAuth.getInstance().uid.toString()
        val completedItemList = java.util.ArrayList<SubRequestCompleted>()
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

                            completedItemList.add(
                                SubRequestCompleted(
                                    acceptedStatus,companyName,completedTime,option,status,userid
                                )
                            )
                        }

                        HideProgressDialog()



                        val alertDialog = androidx.appcompat.app.AlertDialog.Builder(this,android.R.style.Theme_DeviceDefault_NoActionBar_Fullscreen)

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

//
//        mFirestore.collection(Constants.USERS)
//            .document(UserId).get()
//            .addOnSuccessListener(OnSuccessListener<DocumentSnapshot> { documentSnapshot ->
//                val user_email = documentSnapshot.getString("email")
//                val name = documentSnapshot.getString("name")
//                userName.text = user_email
//                userEmail.text = name
//            })
//    }

}

