package com.aisisabeem.Meeba.notification



import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.text.format.DateFormat
import android.util.Log
import androidx.core.app.NotificationCompat
import com.aisisabeem.Meeba.R
import com.aisisabeem.Meeba.home.HomeActivity
import com.aisisabeem.Meeba.utils.Constants
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


const val notificationID = 1
const val channelID = "channel1"
class Notification() : BroadcastReceiver() {
    private var mFireStore = FirebaseFirestore.getInstance()
    private lateinit var mAuth: FirebaseAuth
    override fun onReceive(context: Context, intent: Intent) {
       // Toast.makeText(context,"This toast will be shown every X minutes", Toast.LENGTH_SHORT).show()
        subscribeToRealtimeUpdates(context)
    }


    fun subscribeToRealtimeUpdates(context: Context) = CoroutineScope(Dispatchers.IO).launch{





        mFireStore.collection(Constants.COLLECTION_PARENT)
            .addSnapshotListener(object : EventListener<QuerySnapshot> {

                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null){

                        Log.e("FireStore Error",error.message.toString())
                        return
                    }

                    for (doc: DocumentChange in value?.documentChanges!!){
                        var reqName:Any = ""
                        var reqTime: Timestamp = Timestamp.now()
                        var reqStatus:Any = ""
                        var reqOption:Any = ""
                        var deliveryarranged:Boolean  = false
                        if (doc.type == DocumentChange.Type.ADDED){
                            reqStatus = doc.document.data.get("status")!!


                            if (reqStatus == "ongoing"){
                                reqName = doc.document.data.get("company_name")!!
                                reqTime = (doc.document.get("createdAt") as Timestamp?)!!
                                reqStatus = doc.document.get("status")!!
                                Log.i("NOTI",reqName.toString())



                                sendNotification(context,reqName,reqStatus,reqTime as Timestamp)

                            }

                        }
                    }

                }


            })



    }

    private fun sendNotification(context: Context,reqName: Any, reqStatus: Any, reqTime: Timestamp) {


            val activityIntent = Intent(context, HomeActivity::class.java)
            val actionIntent = PendingIntent.getActivity(context,
                0, activityIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)



            // Assign big picture notification
            // Assign big picture notification
            val bpStyle = NotificationCompat.BigPictureStyle()
            bpStyle.bigPicture(BitmapFactory.decodeResource(context.resources, R.drawable.meeba)).build()
            val dateReq: Date = reqTime.toDate()
            val date = DateFormat.format("dd-MM-yyyy hh:mm:ss", dateReq).toString()
            val notification = NotificationCompat.Builder(context, channelID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("New Order Received from $reqName \nstatus: $reqStatus")
                .setContentText("Request time is $date \n Please click view request to Accept")
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setColor(Color.BLUE)
                .setContentIntent(actionIntent)
                .setAutoCancel(true)
                .setStyle(bpStyle)
                .setOnlyAlertOnce(true)
                .addAction(R.drawable.ic_view, "View Request", actionIntent)
                .build()

            val  manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.notify(notificationID, notification)



        }




    }






