package com.example.cloudcalculations

import android.annotation.SuppressLint
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.AsyncTask
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.security.ProviderInstaller
import java.io.File
import java.sql.Connection
import java.sql.DriverManager


class MainActivity : AppCompatActivity() {

    var con: Connection? = null

    override fun onStart() {
        super.onStart()
        val wordsInDatabaseTextView = findViewById<TextView>(R.id.wordsInDatabase)
        //val async = Async()
        //async.execute("")
        wordsInDatabaseTextView.text = SharedMethods.getCurrentDatabase(this).size.toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val dbFile: File = getDatabasePath(dbName)
        val db = DatabaseHandler(this)
        db.onCreate(SQLiteDatabase.openOrCreateDatabase(dbFile.absolutePath, null))
        ProviderInstaller.installIfNeeded(applicationContext);
    }
    fun learnWords(view: View){
        startActivity(Intent(this, LearnWordsActivity::class.java))
    }
    fun showDatabase(view: View){
        startActivity(Intent(this, ShowDataBaseActivity::class.java))
    }

    inner class Async : AsyncTask<String?, Int?, Int>() {
        var z = ""
        var isSuccess = false
        var name1 = "fail"

        override fun onPreExecute(){
            val toast = Toast.makeText(applicationContext, "PreExecute", Toast.LENGTH_SHORT)
            toast.show()
        }

        override fun doInBackground(vararg params: String?): Int? {
            try {
                con = connectionClass()
                name1 = "name1"
                if(con == null){
                    name1 = "connection == null"
                }
                else{
                    val toast = Toast.makeText(applicationContext, "Connection succeeded", Toast.LENGTH_SHORT)
                    toast.show()
                    var statement = con!!.createStatement()
                    var resultSet = statement.executeQuery("SELECT * FROM _1000_English_Words")
                    if(resultSet.next()){
                        name1 = resultSet.getString(1)
                        isSuccess = true
                        con!!.close()
                    }
                    else{
                        isSuccess = false
                    }
                }
            }catch (e:Exception){
                isSuccess = false
            }
            return null
        }

        override fun onProgressUpdate(vararg values: Int?) {
            // [... Обновите индикатор хода выполнения, уведомления или другой
            // элемент пользовательского интерфейса ...]
        }

        override fun onPostExecute(result: Int?) {
            val toast = Toast.makeText(applicationContext, "PostExecute", Toast.LENGTH_SHORT)
            toast.show()
        }
    }

    @SuppressLint("AuthLeak")
    fun connectionClass(): Connection? {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        var connection: Connection? = null
        var connectionURL:String? = null
        //val SQL = "select * from dbo.Student"

        //Class.forName("net.sourceforge.jtds.jdbc.Driver")
        //val url =
        //    String.format("jdbc:jtds:sqlserver://***.database.windows.net:1433/<your database name>;user=***;password=***;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;")
        //conn = DriverManager.getConnection(url)/

        //stmt = conn.createStatement()
        //rs = stmt.executeQuery(SQL)

        //while (rs.next()) {
        //   System.out.println(rs.getString("name"))
        //}
        //close()

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver")
            connectionURL = "jdbc:jtds:sqlserver://yaroslavserver.database.windows.net:1433;database=English_to_Ukrainian_words_db;user=Yaroslav@yaroslavserver;password=124Pan673;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;"
            connection = DriverManager.getConnection(connectionURL)
        }
        catch (e: Exception){
            Log.e("PORNO", e.stackTraceToString())
        }
        return connection
    }
}