package com.aisisabeem.Meeba.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.aisisabeem.Meeba.R
import com.aisisabeem.Meeba.utils.Constants

class HowToPayActivity : AppCompatActivity() {
    var network: String? = null
    var content: String? = null
    var tvContent: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_how_to_pay)

        network = intent.getStringExtra("network")
        content = intent.getStringExtra("content")
        tvContent = findViewById(R.id.tv_content)

        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)



            if (network.equals(Constants.NETWORK_AIRTEL, ignoreCase = true)) {
                content = "Dear Customer, kindly follow the steps below to enable you to make secured payments and prevent mobile money fraud. \n" +
                        "\n" +
                        "To successfully complete a purchase with your Airtel Money, kindly follow the steps below;\n" +
                        "\n" +
                        "1. On the Checkout page, provide your Mobile Money Name, Mobile Money Number and select Airtel from the Mobile Network option.\n" +
                        "\n" +
                        "2. You can click on Save Payment to save your details in your Wallet.\n" +
                        "\n" +
                        " 3. Click on Pay Now to initiate a payment. \n" +
                        "\n" +
                        "4. Enter your Airtel money number and wait for the prompt. \n" +
                        "\n" +
                        "5. Once the prompt appears, enter your 4-digit pin to complete payment successfully.";
            } else if (network.equals(Constants.NETWORK_MTN, ignoreCase = true)) {
                content = "Dear Customer, as requested by MTN, paying with MTN Mobile Money has been changed to enable you to make secure purchase and prevent mobile money fraud. \n" +
                        "\n" +
                        "To successfully complete a purchase with your MTN Mobile Money, kindly follow the steps below;\n" +
                        "\n" +
                        "1. On the Checkout page, provide your Mobile Money Name, Mobile Money Number and select MTN from the Mobile Network option.\n" +
                        "\n" +
                        "2. You can click on Save Payment to save your details in your Wallet.\n" +
                        "\n" +
                        " 3. Click on Pay Now to initiate a payment. \n" +
                        "\n" +
                        "4. Once you initiate a payment, go out of the app to approve the transaction for the payment to be complete. (NB: You've 2 minutes to complete this process or the process is timed out).\n" +
                        "\n" +
                        "5. To approve the transaction, dial *170#\n" +
                        "a. Choose Option: 7) Wallet.\n" +
                        "b. Choose Option: 3): My Approvals.\n" +
                        "c. Enter your MOMO Pin to retrieve your pending approval list.\n" +
                        "d. Choose the pending transaction. \n" +
                        "e. Choose Option 1 to approve.\n" +
                        "7. Tap button to continue.\n" +
                        "\n" +
                        "6. After the approving the transaction, payment is completed successfully.";
            } else if (network.equals(Constants.NETWORK_TIGO, ignoreCase = true)) {
                content = "Dear Customer, kindly follow the steps below to enable you to make secured payments and prevent mobile money fraud. \n" +
                        "\n" +
                        "To successfully complete a purchase with your Tigo Cash, kindly follow the steps below;\n" +
                        "\n" +
                        "1. On the Checkout page, provide your Mobile Money Name, Mobile Money Number and select Tigo from the Mobile Network option.\n" +
                        "\n" +
                        "2. You can click on Save Payment to save your details in your Wallet.\n" +
                        "\n" +
                        " 3. Click on Pay Now to initiate a payment. \n" +
                        "\n" +
                        "4. Enter your Tigo money number and wait for the prompt. \n" +
                        "\n" +
                        "5. Once the prompt appears, enter your 4-digit pin to complete payment successfully.";
            } else if (network.equals(Constants.NETWORK_VODAFONE, ignoreCase = true)) {
                content = "Dear Customer, kindly follow the steps below to enable you make secured payments and prevent mobile money fraud. \n" +
                        "\n" +
                        "To successfully complete a purchase with Vodafone Cash, kindly follow the steps below;\n" +
                        "\n" +
                        "1. On the Checkout page, provide your Mobile Money Name, Mobile Money Number and select Vodafone from the Mobile Network option.\n" +
                        "\n" +
                        "2. You can click on Save Payment to save your details in your Wallet.\n" +
                        "\n" +
                        " 3. Click on Pay Now to initiate a payment. \n" +
                        "\n" +
                        "4. Once you initiate a payment, go out of the app to generate a voucher code to authorize payment for the transaction. (NB: You have 5 minutes to complete this process or the process will be timed out).\n" +
                        "\n" +
                        "To generate a voucher code:\n" +
                        "a) Dial *110#\n" +
                        "b) Choose Option 6) Generate Voucher\n" +
                        "\n" +
                        "A 6-digit voucher code will be generated and sent to you via SMS. \n" +
                        "\n" +
                        "5. Enter the 6 digit Voucher code to authorize payment. \n" +
                        "\n" +
                        "6. Enter your 4-digit PIN code to successfully complete payment"
            }

            tvContent!!.text = content


        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }





}