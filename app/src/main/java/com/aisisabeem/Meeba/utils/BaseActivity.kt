package com.aisisabeem.Meeba.utils

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler


import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.aisisabeem.Meeba.MainActivity
import com.aisisabeem.Meeba.R
import com.aisisabeem.Meeba.activities.MyProfileActivity


import com.google.android.material.snackbar.Snackbar


import com.google.firebase.auth.FirebaseAuth



open class BaseActivity : AppCompatActivity() {


    private var doubleBackToExitOnce = false
    private lateinit var mProgressDialog: Dialog
    private lateinit var tv_progress_text : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        //mProgressDialog = findViewById(R.id.progressBar)

    }



    fun showProgressDialog(text:String){

        mProgressDialog = Dialog(this)


        /**
         * set the screen content from a layout resource
         * the resource will be inflated, adding all top-level to the screen
         * **/
        mProgressDialog.setContentView(R.layout.dialog_progress)

        tv_progress_text = mProgressDialog.findViewById(R.id.tv_progress_text)
        tv_progress_text.text = text
        //start the dialog and display on the screen
        mProgressDialog.show()

    }


    fun HideProgressDialog(){
        mProgressDialog.dismiss()
    }

    fun getCurrentUserID(): String {
      return FirebaseAuth.getInstance().currentUser!!.uid
    }

fun doubleBackToExit(){
    if (doubleBackToExitOnce){
        super.onBackPressed()
        return
    }
    this.doubleBackToExitOnce = true
    Toast.makeText(this,resources.getString(R.string.please_click_back_again_to_exit),
    Toast.LENGTH_LONG).show()

    val handler = Handler()
    handler.postDelayed({
        doubleBackToExitOnce = false
    },2500)
}



    fun showErrorSnackBar(message: String){

        val snackBar = Snackbar.make(findViewById(R.id.content),
            message,Snackbar.LENGTH_LONG)
        val snacBarView = snackBar.view
        snacBarView.setBackgroundColor(ContextCompat.getColor(this,R.color.snackBarErrorColor))
        snackBar.show()
    }

    fun logoutUser(){
        openLogoutDialog()

    }

    private fun openLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout ?")
            .setNegativeButton(android.R.string.no, null)
            .setPositiveButton(android.R.string.yes
            ) { arg0, arg1 ->
              // arg0.dismiss()
                logOutNow()
            }.create().show()
    }

    private fun logOutNow() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    fun userProfile(){
        val intent = Intent(this, MyProfileActivity::class.java)
        startActivity(intent)
    }

    fun onData() {
        AlertDialog.Builder(this)
            .setTitle("No Internet Connection")
            .setMessage("Please connect and try it again")
            .setNegativeButton(android.R.string.no, null)
            .setPositiveButton(android.R.string.yes
            ) { arg0, arg1 ->
                arg0.dismiss()
            }.create().show()
    }

    @Suppress("DEPRECATION")
     fun networkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo?.isConnectedOrConnecting ?: false
    }


}