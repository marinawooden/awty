package edu.uw.ischool.mwoode.awty

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import java.util.Timer
import java.util.TimerTask

class MainActivity : AppCompatActivity() {
    private var timer: Timer? = null
    private val handler = Handler(Looper.getMainLooper())
    private var isTimerRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
            // Show your Toast message here
            showToast("Your periodic message!")
        }
    }

    private fun showToast(message: String) {
        handler.post {
            Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
        }
    }
}