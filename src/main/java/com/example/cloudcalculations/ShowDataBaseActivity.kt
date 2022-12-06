package com.example.cloudcalculations

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import java.io.File


class ShowDataBaseActivity: AppCompatActivity() {

    override fun onResume() {
        super.onResume()
        update()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_database)
    }

    fun update(){
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels

        val db = DatabaseHandler(this)
        val topics = db.readTopics()

        val linearLayout = findViewById<LinearLayout>(R.id.load_topics_layout)
        linearLayout.removeAllViews()
        for (topic in topics) {
            val name = topic.name
            val state = topic.state
            val parent = LinearLayout(this)
            parent.orientation = LinearLayout.HORIZONTAL
            parent.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT)

            val button = Button(this)
            button.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            button.layoutParams.width = width-250
            val marginParams = MarginLayoutParams(button.layoutParams)
            marginParams.setMargins(0, 0, 20, 0)
            button.layoutParams = RelativeLayout.LayoutParams(marginParams)
            button.text = name
            button.setOnClickListener { showWordsInTopic(button) }
            button.setOnLongClickListener { showPopup(button) }

            val switch = SwitchCompat(this)
            switch.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
            switch.gravity = Gravity.CENTER
            switch.isChecked = state == 1
            switch.setOnCheckedChangeListener { _, isChecked ->
                if(isChecked){
                    db.updateTopic(topic, 1)
                } else {
                    db.updateTopic(topic, 0)
                }
            }
            parent.addView(button)
            parent.addView(switch)
            linearLayout.addView(parent)
        }
        db.close()
    }

    fun addNewTopic(view: View){
        startActivity(Intent(this, AddNewTopicActivity::class.java))
    }

    fun renameTopic(view: View){
        val intent = Intent(this, RenameTopicActivity::class.java)
        if(view is Button){
            intent.putExtra("topicName", view.text.toString())
            intent.putExtra("topicState", 1)
        }
        startActivity(intent)
    }

    fun loadTopics(view: View){
        startActivity(Intent(this, LoadTopicsActivity::class.java))
    }

    fun deleteTopic(view: View){
        val button:Button = view as Button
        val db = DatabaseHandler(this)
        db.deleteTopic(button.text.toString())
        update()
    }

    fun showWordsInTopic(view: View){
        val intent = Intent(this, WordsInTopicActivity::class.java)
        if(view is Button){
            intent.putExtra("topicName", view.text.toString())
            intent.putExtra("topicState", 1)
        }
        startActivity(intent)
    }

    private fun showPopup(view: View): Boolean {
        val popup = PopupMenu(this, view)
        popup.inflate(R.menu.popup_menu)

        popup.setOnMenuItemClickListener { item: MenuItem? ->

            when (item!!.itemId) {
                R.id.header1 -> {
                    showWordsInTopic(view)
                }
                R.id.header2 -> {
                    renameTopic(view)
                }
                R.id.header3 -> {
                    deleteTopic(view)
                }
            }
            true
        }

        popup.show()
        return true
    }
}