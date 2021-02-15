package com.example.mytest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.auth.api.phone.SmsRetrieverClient
import org.apache.commons.lang3.StringUtils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class MainActivity : AppCompatActivity() {
    private lateinit var smsRetrieverClient: SmsRetrieverClient
    private lateinit var otpTextView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        otpTextView = findViewById(R.id.otp_text_view)
        smsRetrieverClient = SmsRetriever.getClient(this)
        val signatureHelper = AppSignatureHelper(this);
        Log.e("SIGNATURE", signatureHelper.appSignatures.toString())
        startSmsListener()
    }

    private fun startSmsListener() {
        smsRetrieverClient.startSmsRetriever()
                .addOnSuccessListener {
                    Toast.makeText(this@MainActivity, "Starting Sms Retriever", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener { e ->
                    e.printStackTrace()
                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                }
    }
    @Subscribe
    fun onEvent(event: SmsRetrievedEvent) {
        Log.d("SMS EVENT", "RECEIVED SMS EVENT")
        Log.d("SMS EVENT - Timeout=", event.isTimeout.toString())
        Log.d("SMS EVENT - Message=", if(!event.isTimeout) event.smsMessage!! else "Timeout: No Message")

        val otp: String =
                StringUtils.substringAfterLast(event.smsMessage, "is").replace(":", "")
                        .trim().substring(0, 4)
        Log.d("EVENT", otp)

        runOnUiThread {
            otpTextView.text = if (!event.isTimeout) otp else "Timeout :("
        }

        startSmsListener()
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().unregister(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }
}