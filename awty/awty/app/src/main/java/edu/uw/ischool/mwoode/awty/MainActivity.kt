package edu.uw.ischool.mwoode.awty

import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.Timer
import java.util.TimerTask


class MainActivity : AppCompatActivity() {
    private var timer: Timer? = null
    // private val handler = Handler(Looper.getMainLooper())
    private var isTimerRunning = false
    private val SEND_SMS_PERMISSION_REQUEST_CODE = 1
//    lateinit var smsManager:SmsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


//        if (Build.VERSION.SDK_INT>=23) {
//            //if SDK is greater that or equal to 23 then
//            //this is how we will initialize the SmsManager
//            smsManager = this.getSystemService(SmsManager::class.java)
//        }
//        else{
//            //if user's SDK is less than 23 then
//            //SmsManager will be initialized like this
//            smsManager = SmsManager.getDefault()
//        }


        val editTextMessage = findViewById<EditText>(R.id.editTextMessage)
        val editTextPhoneNumber = findViewById<EditText>(R.id.editTextPhoneNumber)
        val editTextMinutes = findViewById<EditText>(R.id.editTextMinutes)
        val btnStart = findViewById<Button>(R.id.btnStart)


        // Set initial state of the button
        btnStart.isEnabled = false

        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not needed for this example
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not needed for this example
            }

            override fun afterTextChanged(s: Editable?) {
                // Check if all EditTexts are filled to enable the button
                val isMessageNotEmpty = editTextMessage.text.isNotEmpty()
                val isPhoneNumberNotEmpty = editTextPhoneNumber.text.isNotEmpty()
                val isMinutesNotEmpty = editTextMinutes.text.isNotEmpty()

                btnStart.isEnabled = isMessageNotEmpty && isPhoneNumberNotEmpty && isMinutesNotEmpty
            }
        }

        // button enabling listeners
        editTextMessage.addTextChangedListener(watcher)
        editTextPhoneNumber.addTextChangedListener(watcher)
        editTextMinutes.addTextChangedListener(watcher)

        // Set onClickListener for the Start button
        btnStart.setOnClickListener {
            if (isTimerRunning) {
                // Cancel the timer
                timer?.cancel()

                // Change the button text to "Start"
                btnStart.text = "Start"
                isTimerRunning = false
            } else {
                val minutes = editTextMinutes.text.toString().toLong()
                val intervalMillis = minutes * 60 * 1000

                timer = Timer()
                // Schedule the TimerTask to run every 1 minute (60,000 milliseconds)
                timer?.scheduleAtFixedRate(UpdateProfileTask(), 10, intervalMillis)

                // Change the button text to "Stop"
                btnStart.text = "Stop"
                isTimerRunning = true
            }
        }
    }



    private inner class UpdateProfileTask : TimerTask() {
        override fun run() {
            // Get the message and phone number from EditTexts
            val message = findViewById<EditText>(R.id.editTextMessage).text.toString()
            val phoneNumber = findViewById<EditText>(R.id.editTextPhoneNumber).text.toString()

            // Send SMS
            sendSms(phoneNumber, message)
        }
    }

    private fun sendSms(phoneNumber: String, message: String) {
        // Check if the SEND_SMS permission is not granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            checkSelfPermission(android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request the SEND_SMS permission
            requestPermissions(arrayOf(android.Manifest.permission.SEND_SMS), SEND_SMS_PERMISSION_REQUEST_CODE)
        } else {
            val smsManager = SmsManager.getDefault()
            // Send the SMS
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
        }
    }

    private fun formatPhoneNumber(phoneNumber: String): String {
        if (phoneNumber.length == 11) {
            return phoneNumber
        } else {
            throw InvalidPhoneException("Invalid Phone Number")
        }
    }
}

class InvalidPhoneException(message: String) : Exception(message)