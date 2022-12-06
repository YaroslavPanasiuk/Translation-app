package com.example.cloudcalculations

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class RenameTopicActivity: AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.rename_topic_activity)
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels
        val topic = Topic(intent.getStringExtra("topicName").toString(), intent.getIntExtra("topicState", 0))

        window.setLayout((Math.min(width, height)*0.8).toInt(), (Math.min(width, height)*0.5).toInt())

        findViewById<TextView>(R.id.name).text = topic.name
    }

    fun renameTopic(view: View){
        val db = DatabaseHandler(this)
        val topic = Topic(intent.getStringExtra("topicName").toString(), intent.getIntExtra("topicState", 0))
        var topicName = findViewById<TextView>(R.id.name).text.toString()
        if(topicName.isNotBlank() && topicName != topic.name){
            topicName = SharedMethods.validateTopicName(topicName, this).toString()
            db.renameTopic(topic, topicName)
        }
        db.close()
        finish()
    }
}