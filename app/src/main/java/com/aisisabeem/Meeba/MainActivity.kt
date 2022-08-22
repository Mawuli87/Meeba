package com.aisisabeem.Meeba


import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.get
import androidx.viewpager2.widget.ViewPager2
import com.aisisabeem.Meeba.home.HomeActivity
import com.aisisabeem.Meeba.home.SignInActivity
import com.aisisabeem.Meeba.notification.Notification
import com.aisisabeem.Meeba.notification.channelID
import com.aisisabeem.Meeba.utils.SliderSlides.introSliderAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : AppCompatActivity() {

    private val CheckForNewData:Boolean = true
    private var mAlarmManager : AlarmManager? = null

    private lateinit var introSliderViewPager : ViewPager2
    private lateinit var btnNext: Button

    private lateinit var textSkipIntro : TextView
    private lateinit var indicatorsContainer : LinearLayout

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mainContainer: ConstraintLayout
    private var mFireStore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
      mainContainer = findViewById(R.id.mainContainer_main)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowInsetsControllerCompat(window, mainContainer).let { controller ->
                controller.hide(WindowInsetsCompat.Type.systemBars())
                controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }

        mAuth = FirebaseAuth.getInstance();
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            startActivity(Intent(this, HomeActivity::class.java))
        } else {
            // User logged in
        }

        introSliderViewPager = findViewById(R.id.introSliderViewPager)
        btnNext = findViewById(R.id.btnNext)
        introSliderViewPager.adapter = introSliderAdapter
        textSkipIntro = findViewById(R.id.textSkipIntro)
        indicatorsContainer = findViewById(R.id.indicatorsContainer)

        setupIndicators()
        setCurrentIndicators(0)

        introSliderViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicators(position)
            }
        })

        btnNext.setOnClickListener {
            if (introSliderViewPager.currentItem + 1 < introSliderAdapter.itemCount) {
                introSliderViewPager.currentItem +=1
            }else{
                Intent(applicationContext, SignInActivity::class.java).also {
                    startActivity(it)
                    finish()
                }
            }
        }

        textSkipIntro.setOnClickListener {
            Intent(applicationContext, SignInActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }


        createNotificationChannel()
        if (CheckForNewData){
            //subscribeToRealtimeUpdates()
           // scheduleNotification()
            launChNotificationRequest()

        }


    }

private fun launChNotificationRequest(){
    val mIntent = Intent(this, Notification::class.java)

    val mPendingIntent = PendingIntent.getBroadcast(this, 0, mIntent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
    mAlarmManager = this
        .getSystemService(Context.ALARM_SERVICE) as AlarmManager
    mAlarmManager!!.setRepeating(
        AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
        60000, mPendingIntent
    )
}









    private fun setupIndicators() {
        val indicators = arrayOfNulls<ImageView>(introSliderAdapter.itemCount)
        val layoutParams: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

        layoutParams.setMargins(8, 0, 8, 0)
        for (i in indicators.indices) {
            indicators[i] = ImageView(applicationContext)
            indicators[i].apply {
                this?.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive
                    )
                )
                this?.layoutParams = layoutParams
            }
            indicatorsContainer.addView(indicators[i])

        }
    }

    private fun setCurrentIndicators(index : Int) {
        val childCount = indicatorsContainer.childCount
        for (i in 0 until childCount) {
            val imageView =
                indicatorsContainer[i] as ImageView
            if (i == index) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_active
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive
                    )
                )
            }
        }
    }





    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Notif Channel"
        val desc = "A Description of the Channel"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(channelID, name, importance)
        channel.description = desc
            channel.enableLights(true)
            channel.lightColor = Color.RED
           channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
    }



}