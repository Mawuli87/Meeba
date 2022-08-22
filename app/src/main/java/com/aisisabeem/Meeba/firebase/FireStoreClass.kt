package com.aisisabeem.Meeba.firebase

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.ArrayMap
import android.util.Log
import com.aisisabeem.Meeba.activities.MyProfileActivity
import com.aisisabeem.Meeba.home.HomeActivity
import com.aisisabeem.Meeba.home.SignInActivity
import com.aisisabeem.Meeba.models.Requests
import com.aisisabeem.Meeba.models.SubRequests
import com.aisisabeem.Meeba.models.User
import com.aisisabeem.Meeba.sub_requests.SubRequestActivity
import com.aisisabeem.Meeba.utils.Constants
import com.google.common.collect.ArrayListMultimap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.util.*
import kotlin.collections.HashMap


class FireStoreClass {

private lateinit var context:Context
    private val mFireStore = FirebaseFirestore.getInstance()


    fun registerUser(activity: SignInActivity, userInfo: User) {
        mFireStore.collection(Constants.RIDERS_PROFILE)
            .document(FirebaseAuth.getInstance().uid.toString())
            .set(userInfo)
            .addOnSuccessListener {
                activity.signInRegisteredSuccessfully()
            }.addOnFailureListener {
                Log.e(activity.javaClass.simpleName, "Error writing document")
            }


    }




    fun signInUser(activity: Activity) {
        mFireStore.collection(Constants.RIDERS_PROFILE)
            .document(FirebaseAuth.getInstance().uid.toString())
            .get()
            .addOnSuccessListener { document ->
                val loggedInUser = document.toObject(User::class.java)
                if (loggedInUser != null) {
                    when (activity) {
                        is SignInActivity -> {
                            activity.signInSuccess()
                        }
                        is HomeActivity -> {
                            activity.updateNavigationUserDetails(loggedInUser)
                        }
                        is MyProfileActivity -> {
                            // activity.getUserDetails(loggedInUser)
                        }
                    }


                }

            }.addOnFailureListener {

                when (activity) {
                    is SignInActivity -> {
                        activity.HideProgressDialog()
                    }
                    is HomeActivity -> {
                        activity.HideProgressDialog()
                    }
                }

                Log.e(activity.javaClass.simpleName, "Error writing document")
            }
    }


    fun getCurentUserId(): String {
        var currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""

        if (currentUser != null) {
            currentUserID = currentUser.uid

        }
        return currentUserID
    }

    fun updateRequestAndRiderProfile(
        activity: MyProfileActivity,
        userHashMap: HashMap<String, Any>

        ) {
        val requestID = FirebaseAuth.getInstance().uid.toString()
        mFireStore.collection((Constants.RIDERS_PROFILE))

            .document(requestID)
            .update(userHashMap)
            .addOnSuccessListener { task ->



                Log.i(activity.javaClass.simpleName, "Request data updated successfully")
                activity.riderIdUpdateSuccess()
            }.addOnFailureListener { e ->
                activity.HideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error updating profile.", e)
            }


    }





fun updateRequestAndRiderCompleted(
    activity: HomeActivity,
    userHashMap: HashMap<String, Any>,
   requests: Requests
){

    val requestOwner = requests.company_name!!
    val requestID = requests.request_id.toString()
    val riderID = requests.rider_id


//    val mapOfReq = HashMap<String, Any>()
//    val array = arrayOf(userHashMap)
//    mapOfReq[riderID] = Arrays.asList(*array)

    val data = mapOf(
        riderID to FieldValue.arrayUnion(userHashMap)
    )


    // Create an initial document to update.
     mFireStore.collection(Constants.RIDER_REQUEST_USER)
        .document(riderID)
        .set(data, SetOptions.merge())
            .addOnSuccessListener { task ->
                Log.i(activity.javaClass.simpleName, "Request data updated successfully")
               // activity.riderIdUpdateSuccess(requestOwner)
               activity.startActivity(Intent(activity, HomeActivity::class.java))
            }.addOnFailureListener { e ->
                activity.HideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error updating profile.", e)
            }


}

fun updateRequestInMainCollection(
    activity: HomeActivity,
    userHashMap: HashMap<String, Any>,
    destList: Requests,
    completeRequestHashMap: HashMap<String, Any>,
){


    mFireStore.collection(Constants.COLLECTION_PARENT)
        .whereEqualTo("request_id", destList.request_id)
        .get()
        .addOnSuccessListener { documents ->
            for (document in documents) {
                val docID = document.id
                Log.i("DOC", "docoment id $docID")

                updateCollectionWithDeliveryArranged(activity,docID,userHashMap,destList,completeRequestHashMap)


            }
        }

}




    fun loadUserData(activity: Activity){
        mFireStore.collection(Constants.RIDERS_PROFILE)
            .document(FirebaseAuth.getInstance().uid.toString())
            .get()
            .addOnSuccessListener {document ->
                val loggedInUser = document.toObject(User::class.java)
                if (loggedInUser != null){
                    when(activity){
                        is SignInActivity -> {
                           // activity.signInSuccess(loggedInUser)
                        }
                        is HomeActivity -> {
                            activity.updateNavigationUserDetails(loggedInUser)
                        }
                        is MyProfileActivity -> {
                            activity.setUserDataInUI(loggedInUser)
                        }
                    }

                }

            }.addOnFailureListener {
                    e->

                when(activity){
                    is SignInActivity -> {
                        activity.HideProgressDialog()
                    }
                    is HomeActivity -> {
                       activity.HideProgressDialog()
                    }
                }

                Log.e(activity.javaClass.simpleName,"Error writing document")
            }
    }




    fun updateRequestWithRiderID(activity: HomeActivity, updaterequest: Requests) {
        val currentTime: Date = Calendar.getInstance().getTime()
        val data = hashMapOf<String, Any>(
            "status" to "Accepted",
            "rider_id" to updaterequest.rider_id,
            "accepted_time" to currentTime,
            "option" to "NotCompleted"
        )

      //this to get the requested document ready and update by the call bellow
        mFireStore.collection(Constants.COLLECTION_PARENT)
            .whereEqualTo("request_id", updaterequest.request_id)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                   val docID = document.id
                    Log.i("DOC", "docoment id $docID")

                    updateCollectionWithAccepted(activity,docID,data)

                }
            }


    }

    private fun updateSubCollectionWithAccepted(
        activity: SubRequestActivity,
        docSubID: String,
        data: HashMap<String, Any>) {


        mFireStore.collection(Constants.COLLECTION_PARENT_SUB)
            .document(docSubID)
            .update(data)
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName,"Request data updated successfully")
                //activity.riderIdUpdateSuccess()
               // activity.startActivity(Intent(activity, SubRequestActivity::class.java))
            }.addOnFailureListener{
                    e->
                activity.HideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error updating Request data.",e)
            }


    }

    private fun updateCollectionWithAccepted(activity: HomeActivity,
                                             docID: String,
                                             updaterequest: HashMap<String, Any>) {
        mFireStore.collection(Constants.COLLECTION_PARENT)
            .document(docID)
            .update(updaterequest)
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName,"Request data updated successfully")
                //activity.riderIdUpdateSuccess()
               // activity.startActivity(Intent(activity, HomeActivity::class.java))
            }.addOnFailureListener{
                    e->
                activity.HideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error updating Request data.",e)
            }


    }

    fun updateCollectionWithDeliveryArranged(
        activity: HomeActivity,
        docID: String,
        updaterequest: HashMap<String, Any>,
        destList: Requests,
        completeRequestHashMap: HashMap<String, Any>
    ){

        mFireStore.collection(Constants.COLLECTION_PARENT)
            .document(docID)
            .update(updaterequest)
            .addOnSuccessListener {
               Log.i(activity.javaClass.simpleName,"Request data updated successfully")
                //activity.riderIdUpdateSuccess()
              // activity.startActivity(Intent(activity, HomeActivity::class.java))
                updateRequestAndRiderCompleted(activity,completeRequestHashMap,destList)

            }.addOnFailureListener{
                    e->
                activity.HideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error updating Request data.",e)
            }

    }

    fun updateUserProfileData(
        activity: MyProfileActivity,
        userHashMap: HashMap<String, Any>
    ) {

        mFireStore.collection(Constants.COLLECTION_PARENT_SUB)
            .document(FirebaseAuth.getInstance().uid.toString())
            .set(userHashMap, SetOptions.merge() )
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName,"Profile data updated successfully")
                activity.profileUpdateSuccess()
            }.addOnFailureListener{
                    e->
                activity.HideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error updating profile.",e)
            }

    }

    fun updateSubRequestWithRiderID(
        subRequestActivity: SubRequestActivity,
        updaterequest: SubRequests) {
        val currentTime: Date = Calendar.getInstance().getTime()
        val data = hashMapOf<String, Any>(
            "status" to "Accepted",
            "rider_id" to updaterequest.rider_id!!,
            "accepted_time" to currentTime,
            "option" to "NotCompleted"
        )


        mFireStore.collection(Constants.COLLECTION_PARENT_SUB)
            .whereEqualTo("request_id", updaterequest.request_id)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val docSubID = document.id
                    Log.i("DOC", "docoment id $docSubID")

                    updateSubCollectionWithAccepted(subRequestActivity,docSubID,data)

                }
            }


    }

    fun updateRequestInMainSubCollection(
        subRequestActivity: SubRequestActivity,
        userHashMap: HashMap<String, Any>,
        destList: SubRequests,
        completeRequestHashMap: HashMap<String, Any>) {


        mFireStore.collection(Constants.COLLECTION_PARENT_SUB)
            .whereEqualTo("request_id", destList.request_id)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val docID = document.id
                    Log.i("DOC", "docoment id $docID")

                    updateSubCollectionWithDeliveryArranged(subRequestActivity,docID,userHashMap,destList,completeRequestHashMap)


                }
            }

    }

    private fun updateSubCollectionWithDeliveryArranged(
        subRequestActivity: SubRequestActivity,
        docID: String,
        userHashMap: HashMap<String, Any>,
        destList: SubRequests,
        completeRequestHashMap: HashMap<String, Any>

    ) {

        mFireStore.collection(Constants.COLLECTION_PARENT_SUB)
            .document(docID)
            .update(userHashMap)
            .addOnSuccessListener {
                Log.i(subRequestActivity.javaClass.simpleName,"Sub Request data updated successfully")
                //activity.riderIdUpdateSuccess()
                // activity.startActivity(Intent(activity, HomeActivity::class.java))
                updateSubRequestAndRiderCompleted(subRequestActivity,completeRequestHashMap,destList)

            }.addOnFailureListener{
                    e->
                subRequestActivity.HideProgressDialog()
                Log.e(subRequestActivity.javaClass.simpleName,"Error updating  SubRequest data.",e)
            }

    }

    private fun updateSubRequestAndRiderCompleted(
        subRequestActivity: SubRequestActivity,
        completeRequestHashMap: HashMap<String, Any>,
        destList: SubRequests) {

        val requestOwner = destList.company_name!!
        val requestID = destList.request_id.toString()
        val riderID = destList.rider_id!!




//        val data = hashMapOf(
//            requestID to completeRequestHashMap,
//        )


        val data = mapOf(
            riderID to FieldValue.arrayUnion(completeRequestHashMap)
        )


        // Create an initial document to update.
        mFireStore.collection(Constants.RIDER_SUB_REQUEST_USER)
            .document(riderID)
            .update(data)
            .addOnSuccessListener { task ->
                Log.i( subRequestActivity.javaClass.simpleName, "Sub Request data updated successfully")
                // activity.riderIdUpdateSuccess(requestOwner)
                subRequestActivity.startActivity(Intent( subRequestActivity, SubRequestActivity::class.java))
            }.addOnFailureListener { e ->
                subRequestActivity.HideProgressDialog()
                Log.e( subRequestActivity.javaClass.simpleName, "Error updating profile.", e)
            }


    }
}
