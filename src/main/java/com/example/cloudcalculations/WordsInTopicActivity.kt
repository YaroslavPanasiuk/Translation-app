package com.example.cloudcalculations

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.text.InputType
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children


class WordsInTopicActivity : AppCompatActivity() {

    override fun onStart() {
        super.onStart()
        val extras = intent.extras

        if(extras == null){
            Toast.makeText(this, "null", Toast.LENGTH_SHORT).show()
            return
        }
        Toast.makeText(this, extras.getString("1"), Toast.LENGTH_SHORT).show()

        //val wordsFromFile = SharedMethods.textToWords(textInFile)
        //if(!wordsFromFile.isNullOrEmpty()){
        //    inflateWordsListVBox(wordsFromFile)
        //}
    }

    override fun onResume() {
        super.onResume()
        val textInFile = intent.getStringExtra("wordsFromFile")
        if(textInFile == null){
            Toast.makeText(this, "onresume", Toast.LENGTH_SHORT).show()
            return
        }
        val wordsFromFile = SharedMethods.textToWords(textInFile)
        if(!wordsFromFile.isNullOrEmpty()){
            inflateWordsListVBox(wordsFromFile)
        }
    }

    override fun onStop() {
        super.onStop()
        val textInFile = intent.getStringExtra("wordsFromFile")
        if(textInFile == null){
            Toast.makeText(this, "onstop", Toast.LENGTH_SHORT).show()
            return
        }
        val wordsFromFile = SharedMethods.textToWords(textInFile)
        if(!wordsFromFile.isNullOrEmpty()){
            inflateWordsListVBox(wordsFromFile)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_words_in_topic)

        val topicNameTextView = findViewById<TextView>(R.id.topicName)
        val saveButton = findViewById<Button>(R.id.saveButton)
        val add_words_from_file_button = findViewById<Button>(R.id.words_from_file_button)
        val wordsAdder = findViewById<EditText>(R.id.words_adder)

        val topic = Topic(intent.getStringExtra("topicName").toString(), intent.getIntExtra("topicState", 0))
        val db = DatabaseHandler(this)
        val words = db.readWordsInTopic(topic.name)

        inflateWordsListVBox(words)

        wordsAdder.requestFocus()
        wordsAdder.inputType = InputType.TYPE_CLASS_TEXT
        wordsAdder.onSubmit {
            addWord(wordsAdder.text.toString())
            wordsAdder.setText("")
        }


        saveButton.setOnClickListener { save() }
        add_words_from_file_button.setOnClickListener {
            if(SharedMethods.checkPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                val intent = Intent(this, FileListActivity::class.java)
                val path = Environment.getExternalStorageDirectory().absolutePath
                intent.putExtra("path", path)
                startActivity(intent)
            } else {
                SharedMethods.requestPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
        topicNameTextView.text = topic.name




    }

    private fun addWord(text:String){
        if(text.isEmpty()){ return }
        val wordsToInsert = SharedMethods.textToWords(text)
        for(word in wordsToInsert){
            inflateWordLine(word)
        }
    }

    private fun deleteWord(textView:View){
        textView.clearFocus()
        val hBox = textView.parent as LinearLayout
        val wordsList = hBox.parent as LinearLayout
        var i = wordsList.indexOfChild(hBox)
        wordsList.removeView(hBox)

        while(i < wordsList.childCount-1){
            val wordLine = wordsList.getChildAt(i) as LinearLayout
            val counter  = wordLine.getChildAt(0) as TextView
            i += 1
            counter.text = i.toString()
            }
    }

    private fun inflateWordLine(word:Word){
        val container = findViewById<LinearLayout>(R.id.words_list_VBox)
        val horizontalContainer = LinearLayout(this)
        horizontalContainer.orientation = LinearLayout.HORIZONTAL
        val textView = EditText(this)
        val counter = TextView(this)
        val switch = SwitchCompat(this)
        horizontalContainer.addView(counter)
        horizontalContainer.addView(textView)
        horizontalContainer.addView(switch)
        container.addView(horizontalContainer, container.childCount-1)

        textView.setPadding(30, 0, 40, 0)
        textView.imeOptions = EditorInfo.IME_ACTION_DONE
        textView.isSingleLine = true
        textView.onSubmit {
            if(textView.text.isNullOrEmpty()) {
                deleteWord(textView)
            }
        }
        textView.setText(word.unknownWord + " - " + word.translation)

        counter.text = (container.indexOfChild(horizontalContainer)+1).toString()
        val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        lp.setMargins(20, 0, 20, 0);
        lp.weight = 1F
        textView.layoutParams = lp

        switch.gravity = Gravity.END
        switch.isChecked = word.state == 1
        switch.setOnCheckedChangeListener { _, isChecked ->
            val db = DatabaseHandler(this)
            if(isChecked){
                db.updateWord(word, 1)
            } else {
                db.updateWord(word, 0)
            }
        }
    }

    private fun inflateWordsListVBox(words: MutableList<Word>){
        val topic = Topic(intent.getStringExtra("topicName").toString(), intent.getIntExtra("topicState", 0))
        val db = DatabaseHandler(this)
        val wordsInTopic = db.readWordsInTopic(topic.name)
        for(word in words){
            inflateWordLine(word)
        }
    }

    private fun EditText.onSubmit(func: () -> Unit) {
        setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) { func() }
            true
        }
    }

    private fun save(){
        val db = DatabaseHandler(this)
        val topic = Topic(intent.getStringExtra("topicName").toString(), intent.getIntExtra("topicState", 0))
        db.deleteWordsInTopic(topic.name)
        val wordsList = findViewById<LinearLayout>(R.id.words_list_VBox)
        for(i in 0 until wordsList.childCount-1){
            val hBox = wordsList.getChildAt(i) as LinearLayout
            val editText = hBox.getChildAt(1) as EditText
            val switch = hBox.getChildAt(2) as SwitchCompat
            val wordToInsert = SharedMethods.textToWords(editText.text.toString())[0]
            wordToInsert.topicName = topic.name
            wordToInsert.state = if(switch.isChecked) 1 else 0
            db.insertWord(wordToInsert)
        }
        //while(wordsList.childCount > 1){
        //    wordsList.removeViewAt(0)
        //}
        //inflateWordsListVBox()
    }
}