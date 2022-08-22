package com.aisisabeem.Meeba.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.aisisabeem.Meeba.R
import com.aisisabeem.Meeba.firebase.FireStoreClass
import com.aisisabeem.Meeba.models.User
import com.aisisabeem.Meeba.utils.BaseActivity
import com.aisisabeem.Meeba.utils.Constants
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import java.io.IOException
import com.google.firebase.storage.StorageReference


class MyProfileActivity : BaseActivity() {

    private var mSelectedImageUri: Uri? = null
    private var mProfileImageUri: String = ""
    private lateinit var et_profile_update_email : AppCompatEditText
    private lateinit var et_profile_update_phone : AppCompatEditText
    private lateinit var et_update_name : AppCompatEditText
    private lateinit var btn_update_details : Button
    private lateinit var img_profile: de.hdodenhof.circleimageview.CircleImageView
    private lateinit var toolbar_activity_profile: Toolbar
    private lateinit var mUserDetails: User


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)
        toolbar_activity_profile = findViewById(R.id.toolbar_activity_profile)
        et_profile_update_phone = findViewById(R.id.et_phone_number)
        et_profile_update_email = findViewById(R.id.et_update_email)
        et_update_name = findViewById(R.id.et_update_name)
        img_profile = findViewById(R.id.profile_image)
        btn_update_details = findViewById(R.id.btn_update)
        setUpActionBar()

        FireStoreClass().loadUserData(this)

        img_profile.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED){
                //TODO Show image chooser
                showImageChooser()

            }else{
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE)
            }
        }
        btn_update_details.setOnClickListener{
            if (mSelectedImageUri != null){
                uploadUserImage()
            }else{
                showProgressDialog("Please wait...")


                updateUserProfileData()
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //TODO Show image chooser
                showImageChooser()
            }
        }else{
            Toast.makeText(baseContext, "Please you should allow us permission to read external storage.",
                Toast.LENGTH_SHORT).show()
        }
    }


    fun showImageChooser(){
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, Constants.PICK_IMAGE_REQUEST_CODE)
        // val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        // startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.PICK_IMAGE_REQUEST_CODE && data!!.data != null){
            mSelectedImageUri = data.data

            try {
                Glide
                    .with(this)
                    .load(mSelectedImageUri)
                    .centerCrop()
                    .into(img_profile);
            }catch (e: IOException){
                e.printStackTrace()
            }

        }
    }
    private fun setUpActionBar(){
        setSupportActionBar(toolbar_activity_profile)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_home)
            actionBar.title = "My Profile"

        }
        toolbar_activity_profile.setNavigationOnClickListener {
            onBackPressed()
        }
    }


    fun setUserDataInUI(user:User){
        mUserDetails = user
        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.loading_spinner)
            .into(img_profile);
        et_update_name.setText(user.name)
        et_profile_update_email.setText(user.email)
        if (user.mobile != 0){
            et_profile_update_phone.setText(user.mobile.toString())
        }
    }

    private fun updateUserProfileData(){
        val userHashMap = HashMap<String,Any>()
        var anyChangesMade = false
        if (mProfileImageUri.isNotEmpty()
            && mProfileImageUri != mUserDetails.image){
            userHashMap[Constants.IMAGE] = mProfileImageUri
            anyChangesMade = true
        }
        if (et_update_name.text.toString() != mUserDetails.name){
            userHashMap[Constants.NAME] = et_update_name.text.toString()
            anyChangesMade = true
        }

        if (et_profile_update_phone.text.toString() != mUserDetails.mobile.toString()){
            userHashMap[Constants.MOBILE] = et_profile_update_phone.text.toString().toLong()
            anyChangesMade = true
        }
        val email = mUserDetails.email
        Log.i("TAG2",email.toString())
        if (anyChangesMade) {
            FireStoreClass().updateRequestAndRiderProfile(this, userHashMap)
        }
    }



    fun riderIdUpdateSuccess() {
        Toast.makeText(this,"Request Completion Successful",Toast.LENGTH_LONG).show()
    }





    private fun uploadUserImage(){
        showProgressDialog("Please wait....")
        if (mSelectedImageUri != null){
            val sRef:StorageReference =
                FirebaseStorage.getInstance()
                    .reference.child("USER_IMAGE"+
                            System.currentTimeMillis()+"."+ Constants.getFileExtension(this,mSelectedImageUri))
            sRef.putFile(mSelectedImageUri!!).addOnSuccessListener {
                    taskSnapshot ->
                Log.e("Firebase Image URL",
                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString())
                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                        uri ->
                    Log.i("Downloadable Image URL",uri.toString())
                    mProfileImageUri = uri.toString()
                    //TODO update user profile image
                    HideProgressDialog()
                    updateUserProfileData()


                }
            }.addOnFailureListener{
                    exception ->
                Toast.makeText(baseContext, exception.message,
                    Toast.LENGTH_SHORT).show()
                HideProgressDialog()
            }
        }
    }


    fun profileUpdateSuccess(){
        HideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }


}