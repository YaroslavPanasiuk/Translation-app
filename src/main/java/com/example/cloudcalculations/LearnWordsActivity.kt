package com.example.cloudcalculations

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import java.util.*
import kotlin.random.Random

class LearnWordsActivity : AppCompatActivity() {

    var currentDatabase = arrayListOf<Word>()
    var previousWords: Queue<Int> = LinkedList()
    var wordsList = mutableMapOf<Int, Word>()
    var currentWordIndex: Int = -1

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learn_words)
        val wordToLearn = findViewById<TextView>(R.id.wordToLearn)
        val wordTranslation = findViewById<TextView>(R.id.wordTranslation)
        val switchLanguages = findViewById<CheckBox>(R.id.radio_switch_languages)
        val canvas = findViewById<LinearLayout>(R.id.learn_words_screen)

        currentDatabase = SharedMethods.getCurrentDatabase(this)

        if(currentDatabase.isNullOrEmpty()){
            openDialog()
            return
        }

        for(i in 0 until currentDatabase.size / 2){
            previousWords.add(-1)
        }

        selectNextWord()
        showWord(wordToLearn, wordTranslation, switchLanguages)

        switchLanguages.setOnCheckedChangeListener {_,_ ->
            val temp = wordToLearn.text
            wordToLearn.text = wordTranslation.text
            wordTranslation.text = temp
        }

        canvas.setOnTouchListener(object: OnSwipeTouchListener() {
            override fun onSwipeLeft(): Boolean {
                selectNextWord()
                showWord(wordToLearn, wordTranslation, switchLanguages)
                return true
            }
            override fun onSwipeRight(): Boolean {
                selectPreviousWord()
                showWord(wordToLearn, wordTranslation, switchLanguages)
                return true
            }
        })

        canvas.setOnClickListener {
            wordTranslation.visibility = View.VISIBLE
        }
    }

    private fun openDialog(){
        AlertDialog.Builder(this)
            .setTitle("No words")
            .setMessage("Selected database is empty")
            .setPositiveButton("ok") {_, _ -> run { finish() } }
            .show()
    }

    private fun showWord(textBox: TextView, translation: TextView, switchLanguages: CheckBox){
        if(switchLanguages.isChecked){
            translation.text = wordsList[currentWordIndex]!!.unknownWord
            textBox.text = wordsList[currentWordIndex]!!.translation
        } else {
            textBox.text = wordsList[currentWordIndex]!!.unknownWord
            translation.text = wordsList[currentWordIndex]!!.translation
        }
        translation.visibility = View.INVISIBLE
    }

    private fun selectNextWord(){
        currentWordIndex += 1
        if(currentWordIndex == wordsList.count()){
            var wordIndex = Random.nextInt(0, currentDatabase.size)
            while(previousWords.contains(wordIndex)){
                wordIndex = Random.nextInt(0, currentDatabase.size)
            }
            if(previousWords.isNotEmpty()) {
                previousWords.poll()
                previousWords.add(wordIndex)
            }
            wordsList[currentWordIndex] = currentDatabase[wordIndex]

        }
    }

    private fun selectPreviousWord(){
        if(currentWordIndex > 0) {currentWordIndex -=1}
    }
}