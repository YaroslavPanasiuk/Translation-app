package com.example.cloudcalculations

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class AddNewTopicActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_new_topic_activity)
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels

        window.setLayout((Math.min(width, height)*0.8).toInt(), (Math.min(width, height)*0.5).toInt())
    }
    fun addNewTopic(view: View){
        val db = DatabaseHandler(this)
        var topicName = findViewById<TextView>(R.id.name).text.toString()
        topicName = SharedMethods.validateTopicName(topicName, this).toString()
        db.insertTopic(topicName, 1)
        db.close()
        finish()

    }
}