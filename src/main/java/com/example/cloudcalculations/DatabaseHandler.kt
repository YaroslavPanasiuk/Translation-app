package com.example.cloudcalculations

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.view.View
import android.widget.PopupMenu

const val dbName = "app.db"

class DatabaseHandler(context: Context): SQLiteOpenHelper(context, dbName, null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        val createTableTopics = "CREATE TABLE IF NOT EXISTS topics (topic TEXT, state INTEGER, UNIQUE(topic))"
        val createTableWords = "CREATE TABLE IF NOT EXISTS words (id INTEGER PRIMARY KEY, unknownWord TEXT, translation TEXT, topicName TEXT, state INTEGER)"
        //db?.execSQL("DROP TABLE IF EXISTS words")
        db?.execSQL(createTableTopics)
        db?.execSQL(createTableWords)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    fun insertTopic(newTopic: String, newState:Int){
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put("topic", newTopic)
        cv.put("state", newState)
        db.insert("topics", null, cv)
    }

    fun readTopics():MutableList<Topic>{
        val list: MutableList<Topic> = ArrayList()
        val db = this.readableDatabase
        val query = db.rawQuery("SELECT * FROM topics", null)
        while (query.moveToNext()){
            val topic = Topic()
            topic.name = query.getString(0)
            topic.state = query.getInt(1)
            list.add(topic)
        }
        return list
    }

    fun getTopic(topicName: String):Topic{
        val db = this.readableDatabase
        val query = db.rawQuery("SELECT * FROM topics WHERE topic = '$topicName';", null)
        val topic = Topic()
        if(query.moveToNext()){
            topic.name = query.getString(0)
            topic.state = query.getInt(1)
        }
        return topic
    }

    fun deleteTopic(topic: String){
        val db = this.writableDatabase
        db.delete("topics", "topic = ?", arrayOf(topic))
        deleteWordsInTopic(topic)
        db.close()
    }

    fun updateTopic(currentTopic: Topic, newState:Int){
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put("topic", currentTopic.name)
        cv.put("state", newState)
        db.update("topics", cv, "topic = ?", arrayOf(currentTopic.name))
    }

    fun renameTopic(currentTopic: Topic, newName:String){
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put("topic", newName)
        cv.put("state", currentTopic.state)
        db.update("topics", cv, "topic = ?", arrayOf(currentTopic.name))

        val wordsCV = ContentValues()
        wordsCV.put("topicName", newName)
        db.update("words", wordsCV, "topicName = ?", arrayOf(currentTopic.name))
    }


    fun insertWord(word: Word){
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put("unknownWord", word.unknownWord)
        cv.put("translation", word.translation)
        cv.put("topicName", word.topicName)
        cv.put("state", word.state)
        db.insert("words", null, cv)
    }
    fun deleteWord(word: Word){
        val db = this.writableDatabase
        val wordsInTopic = readWordsInTopic(word.topicName)
        if(!wordsInTopic.contains(word)){
            return
        }
        db.delete("words", "id = ?", arrayOf(word.index as String))
    }

    fun readWordsInTopic(topicName: String):MutableList<Word>{
        val list: MutableList<Word> = ArrayList()
        val db = this.readableDatabase
        val query = db.rawQuery("SELECT * FROM words WHERE topicName = '$topicName';", null)
        while (query.moveToNext()){
            val word = Word()
            word.index = query.getInt(0)
            word.unknownWord = query.getString(1)
            word.translation = query.getString(2)
            word.topicName = query.getString(3)
            word.state = query.getInt(4)
            list.add(word)
        }
        return list
    }

    fun deleteWordsInTopic(topic: String){
        val db = this.writableDatabase
        db.delete("words", "topicName = ?", arrayOf(topic))
        db.close()
    }

    fun updateWord(word: Word, newState:Int){
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put("state", newState)
        db.update("words", cv, "id = ?", arrayOf(word.index.toString()))
    }

}