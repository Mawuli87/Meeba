package com.aisisabeem.Meeba.home

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.WindowInsets
import android.widget.Button
import android.widget.ScrollView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.Toolbar
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.aisisabeem.Meeba.R
import com.aisisabeem.Meeba.utils.BaseActivity
import com.aisisabeem.Meeba.utils.Constants
import com.aisisabeem.Meeba.firebase.FireStoreClass
import com.aisisabeem.Meeba.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SignInActivity : BaseActivity() {

    private lateinit var toolbar_sign_in_activity: Toolbar
    private lateinit var auth: FirebaseAuth

    private lateinit var et_login_email : AppCompatEditText
    private lateinit var et_login_name : AppCompatEditText
    private lateinit var et_login_password : AppCompatEditText
    private lateinit var SignInBtn : Button

    // private lateinit var user_uid: SharedPreferences
     private lateinit var mPrefs:SharedPreferences

     private lateinit var signContainer:ScrollView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        signContainer = findViewById(R.id.signContainer)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowInsetsControllerCompat(window, signContainer).let { controller ->
                controller.hide(WindowInsetsCompat.Type.systemBars())
                controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }

        mPrefs = getSharedPreferences(Constants.USER_UUID, Context.MODE_PRIVATE)


        et_login_email = findViewById(R.id.et_login_email)
        et_login_password = findViewById(R.id.passwordSign)
        SignInBtn = findViewById(R.id.SignInBtn)
        et_login_name = findViewById(R.id.et_name_login)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();
        toolbar_sign_in_activity = findViewById(R.id.toolbar_sign_in_activity)

        setUpActionBar()

        SignInBtn.setOnClickListener{
            signInRegisteredUser()
        }

//        SignInFromHere.setOnClickListener{
//            Intent(applicationContext, SignUpActivity::class.java).also {
//                startActivity(it)
//            }
//        }



    }

    fun signInSuccess() {
        HideProgressDialog()
        startActivity(Intent(this,HomeActivity::class.java))
        finish()
    }


    private fun setUpActionBar(){
        setSupportActionBar(toolbar_sign_in_activity)
        val actioBar = supportActionBar
        if (actioBar != null){
            actioBar.setDisplayHomeAsUpEnabled(true)
            actioBar.setHomeAsUpIndicator(R.drawable.ic_back_home)
        }

        toolbar_sign_in_activity.setNavigationOnClickListener {
            onBackPressed()
        }
    }


     fun signInRegisteredUser(){

        val name : String = et_login_name.text.toString().trim()
        val email : String = et_login_email.text.toString().trim()
        val password : String = et_login_password.text.toString().trim()

      if (validateForm(name,email,password))
            showProgressDialog(resources.getString(R.string.please_wait))


            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->

                    HideProgressDialog()
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("SignIn", "createUserWithEmail:success")
                       // val user = auth.currentUser

                        val firebaseUser : FirebaseUser = task.result!!.user!!
                        val registerEmail = firebaseUser.email!!
                        val user = User(firebaseUser.uid,name,registerEmail)
                        FireStoreClass().registerUser(this,user)

                        val prefsEditor: SharedPreferences.Editor = mPrefs.edit()
                        prefsEditor.putString("uuid", firebaseUser.uid)
                        prefsEditor.apply()

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("SignIn", "createUserWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()

                    }
                }

    }



    fun signInRegisteredSuccessfully() {

        Toast.makeText(this@SignInActivity,"Sign In successful",
            Toast.LENGTH_LONG).show()
         HideProgressDialog()
        startActivity(Intent(this,HomeActivity::class.java))
        finish()

    }

    private fun validateForm(name:String,email:String,password:String):Boolean{
        return when {
            TextUtils.isEmpty(name) -> {
                // showErrorSnackBar("Please enter your email")
                false
            }

            TextUtils.isEmpty(email) -> {
               // showErrorSnackBar("Please enter your email")
                false
            }
            TextUtils.isEmpty(password) ->{
               // showErrorSnackBar("Please enter a password")
                false
            }else -> {
                true
            }

        }
    }

}