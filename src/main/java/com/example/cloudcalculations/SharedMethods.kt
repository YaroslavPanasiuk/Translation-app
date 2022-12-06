package com.example.cloudcalculations

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.regex.Matcher
import java.util.regex.Pattern


class SharedMethods {
    companion object{
        private var extraText = ""
        fun setExtraText(extraText: String){
            this.extraText = extraText
        }
        fun getExtraText():String{
            return extraText
        }
        fun getCurrentDatabase(activity: Activity): ArrayList<Word> {
            val currentDatabase = ArrayList<Word>()

            val db = DatabaseHandler(activity)
            val topics = db.readTopics()

            for (topic in topics) {
                if (topic.state == 1 && topic.name != "") {
                    for(word in db.readWordsInTopic(topic.name)){
                        if(word.state == 1){currentDatabase.add(word)}
                    }
                }
            }
            db.close()

            return currentDatabase
        }
        fun wordsToText(words: MutableList<Word>): String {
            var text = ""
            for(word in words){
                text += word.unknownWord + " - " + word.translation + "\n"
            }
            return text
        }

        fun textToWords(text: String):ArrayList<Word>{
            var unknownWord = ""
            var translation = ""
            val wordsToInsert = ArrayList<Word>()
            for(line in text.lines()){
                val deliminator: Pattern = Pattern.compile("\\s+\\W+\\s+")
                val deliminatorMatcher: Matcher = deliminator.matcher(line)

                if (deliminatorMatcher.find()) {
                    unknownWord = (line.substring(0, deliminatorMatcher.start()))
                    translation = (line.substring(deliminatorMatcher.end(), line.length))
                }
                else{
                    if(line.isNotEmpty()){
                        unknownWord = (line)
                        translation = ("no translation found to word '$unknownWord'")
                    }
                }

                if(line.isNotEmpty()) {
                    wordsToInsert.add(Word(unknownWord, translation))
                }
            }
            return wordsToInsert
        }

        fun validateTopicName(currentName: String?, activity: Activity):String?{
            val pattern = "[\\p{L}\\d]".toRegex()
            var newName = currentName
            val db = DatabaseHandler(activity)
            val topics = db.readTopics()
            if(newName.isNullOrBlank() || !pattern.containsMatchIn(newName)){
                return "Topic" + (topics.size + 1)
            }
            val topicNames = ArrayList<String>()
            for(topic in topics){ topicNames.add(topic.name) }
            var i = 1
            while (topicNames.contains(newName)){
                newName = "$currentName ($i)"
                i += 1
            }
            return  newName
        }
        fun checkPermission(context: Context, permission: String): Boolean {
            val result = ContextCompat.checkSelfPermission(context, permission)
            return result == PackageManager.PERMISSION_GRANTED
        }
        fun requestPermission(context: Context, permission: String) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, permission)) {
                Toast.makeText(context, "Storage permission is requires,please allow from settings", Toast.LENGTH_SHORT).show()
            } else ActivityCompat.requestPermissions(context, arrayOf(permission),
                111
            )
        }
    }
}