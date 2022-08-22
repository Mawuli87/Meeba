package com.aisisabeem.Meeba.utils

import android.app.Activity
import android.net.Uri
import android.webkit.MimeTypeMap

object Constants {

    const val RIDERS_PROFILE: String = "sme_riders"
    const val USER_UUID: String = "requests"
    const val USER_ID: String = "com.aisisabeem.Meeba.Utils"
    val COLLECTION_PARENT = "sme_requests"
    val COLLECTION_PARENT_SUB = "subs_requests"

    val DESTINATIONS = "destinations"
    val FIELD_CONTENT_TYPE ="contentType"
    val RIDER_REQUEST_USER = "sme_completed"
    val RIDER_SUB_REQUEST_USER = "sub_completed"

    const val NOTIFICATION_ID = "Meeba_notification_id"
    const val NOTIFICATION_NAME = "Meeba"

    const val IMAGE: String = "image"
    const val NAME: String = "name"
    const val MOBILE: String = "mobile"

    const val READ_STORAGE_PERMISSION_CODE = 1
    const val PICK_IMAGE_REQUEST_CODE = 2


    const val NETWORK_MTN = "MTN Mobile Money"
    const val NETWORK_AIRTEL = "Airtel Money"
    const val NETWORK_TIGO = "Tigo Cash"
    const val NETWORK_VODAFONE = "Vodafone Cash"
    const val KEY_MTN = "MTN"
    const val KEY_AIRTEL = "ATL"
    const val KEY_TIGO = "TIGO"
    const val KEY_VODAFONE = "VDF"
    const val URL = "https://api.meeba-sisia.com"

   const val  PAYING_PROCESS_MTN = "If your transaction has been initiated." + "\n" +
    "You will receive a prompt shortly to complete payment if not\n" +
           "The following is the process flow for the MTN payment service:\n" +
    "1. Dial *170#\n" +
    "2. Choose Option: 7) Wallet\n" +
    "3. Choose Option: 3): My Approvals \n" +
    "4. Enter your MOMO Pin to retrieve your pending approval list\n" +
    "5. Choose a pending transaction\n" +
    "6. Choose Option 1 to approve\n"

    const val VOUCHER_PROCESS_VODAPHONE = " 1.Before  you initiate a payment," +
            " go out of the app to generate a voucher code to authorize payment " +
            "for the transaction. " +
            "(NB: You have less than 5 minutes to complete" +
            " this process or the process will be timed out).\n" +
            "To generate a voucher code:\n" +
            " a) Dial *110# \n" +
            "b) Choose Option 6) Generate Voucher \n" +
            " \n" +
            "A 6-digit voucher code will be generated and sent to you via SMS.\n" +
            " \n" +
            "5. Enter the 6 digit Voucher code to authorize payment.\n" +
            " \n" +
            "6. Enter your 4-digit PIN code to successfully complete payment"




    fun getFileExtension(activity: Activity, uri: Uri?): String?{
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }

}