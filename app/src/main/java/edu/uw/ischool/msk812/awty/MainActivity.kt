package edu.uw.ischool.msk812.awty

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

const val ALARM_ACTION = "edu.uw.ischool.msk812.ALARM"

class MainActivity : AppCompatActivity() {
    lateinit var startBtn : Button
    lateinit var messageText : EditText
    lateinit var phoneText : EditText
    lateinit var minutesText : EditText
    var receiver : BroadcastReceiver? = null
    val smsPermisionCode = 812
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startBtn = findViewById(R.id.startBtn)
        messageText = findViewById(R.id.messageText)
        phoneText = findViewById(R.id.phoneNum)
        minutesText = findViewById(R.id.minutesNag)


        startBtn.setOnClickListener {
            if (startBtn.text == "Start") {
                if (messageText.text.isNotEmpty() && phoneText.text.length == 10 &&
                    minutesText.text.isNotBlank() && minutesText.text[0] != '0') {
                    Toast.makeText(this, "Started alarm", Toast.LENGTH_SHORT).show()
                    receiveMessage()
                }
            } else {
                Toast.makeText(this, "Stopped alarm", Toast.LENGTH_SHORT).show()
                cancelMessage()
            }
        }
    }

    fun receiveMessage() {
        val activityThis = this
        val smsManager = SmsManager.getDefault()
        val message = messageText.text.toString()
        val phoneNum = phoneText.text.toString()
        val calcMinutes = minutesText.text.toString().toLong() * 60 * 1000
        val phoneNumFormat = "(${phoneNum.substring(0, 3)}) ${phoneNum.substring(3, 6)}-${phoneNum.substring(6, 10)}"

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), smsPermisionCode)
        } else {
            receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    Log.d("SMS", "Sending Text Message")
                    smsManager.sendTextMessage(phoneNumFormat, null, message, null, null)
                }
            }

            val filter = IntentFilter(ALARM_ACTION)
            registerReceiver(receiver, filter)

            val intent = Intent(ALARM_ACTION)
            val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

            val alarmManager : AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), calcMinutes, pendingIntent)
            startBtn.text = "Stop"
        }
    }

    fun cancelMessage() {
        val intent = Intent(ALARM_ACTION)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val alarmManager : AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
        unregisterReceiver(receiver)
        receiver = null
        startBtn.text = "Start"
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            smsPermisionCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (minutesText.text.isNotBlank() && minutesText.text[0] != '0') {
                        receiveMessage()
                    }
                } else {
                    Toast.makeText(this, "Permission denied. SMS cannot be sent.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}