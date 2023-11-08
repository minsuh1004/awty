package edu.uw.ischool.msk812.awty

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

const val ALARM_ACTION = "edu.uw.ischool.msk812.ALARM"

class MainActivity : AppCompatActivity() {
    lateinit var startBtn : Button
    lateinit var messageText : EditText
    lateinit var phoneText : EditText
    lateinit var minutesText : EditText
    var receiver : BroadcastReceiver? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startBtn = findViewById(R.id.startBtn)
        messageText = findViewById(R.id.messageText)
        phoneText = findViewById(R.id.phoneNum)
        minutesText = findViewById(R.id.minutesNag)


        //val intent = Intent(this, MessageReceiver::class.java)
        //val intent = Intent(ALARM_ACTION)

        //val alarmManager : AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager


        startBtn.setOnClickListener {
            if (startBtn.text == "Start") {
                if (messageText.text.isNotEmpty() && phoneText.text.length == 10 &&
                    minutesText.text.isNotBlank() && minutesText.text[0] != '0') {
                    Toast.makeText(this, "Started alarm", Toast.LENGTH_SHORT).show()
                    /*intent.putExtra("message", messageText.text.toString())
                    intent.putExtra("phoneNum", phoneText.text.toString())
                    val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                    val calcMinutes = minutesText.text.toString().toInt() * 60 * 1000
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), calcMinutes.toLong(), pendingIntent)
                    startBtn.text = "Stop"*/
                    receiveMessage()
                }
            } else {
                Toast.makeText(this, "Stopped alarm", Toast.LENGTH_SHORT).show()
                /*val pendingIntent = PendingIntent.getBroadcast(this, 0, intent,
                    PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE)
                alarmManager.cancel(pendingIntent)
                startBtn.text = "Start"*/
                cancelAlarm()
            }
        }
    }


    fun receiveMessage() {
        val activityThis = this
        val message = messageText.text.toString()
        val phoneNum = phoneText.text.toString()
        val calcMinutes = minutesText.text.toString().toInt() * 60 * 1000

        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val phoneNumFormat = "(${phoneNum.substring(0, 3)}) ${phoneNum.substring(3, 6)}-${phoneNum.substring(6, 10)}"
                val fullMessage = "${phoneNumFormat}: $message"
                Toast.makeText(activityThis, fullMessage, Toast.LENGTH_SHORT).show()
            }
        }
        val filter = IntentFilter(ALARM_ACTION)
        registerReceiver(receiver, filter)

        val intent = Intent(ALARM_ACTION)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val alarmManager : AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), calcMinutes.toLong(), pendingIntent)
        startBtn.text = "Stop"
    }

    fun cancelAlarm() {
        val intent = Intent(ALARM_ACTION)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val alarmManager : AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
        unregisterReceiver(receiver)
        receiver = null
        startBtn.text = "Start"
    }

    /*class MessageReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val message = intent!!.getStringExtra("message")
            val phoneNum = intent!!.getStringExtra("phoneNum")
            val phoneNumFormat = "(${phoneNum!!.substring(0, 3)}) ${phoneNum.substring(3, 6)}-${phoneNum.substring(6, 10)}"
            val fullMessage = "${phoneNumFormat}: $message"
            Toast.makeText(context, fullMessage, Toast.LENGTH_SHORT).show()
        }
    }*/
}