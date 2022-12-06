package com.example.cloudcalculations

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class FileListActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_list)

        val recyclerView = findViewById<RecyclerView>(R.id.files_list)
        val noFilesTextView = findViewById<TextView>(R.id.no_files_textview)

        val path = intent.getStringExtra("path").toString()
        val root = File(path)
        val filesAndFolders = root.listFiles{
                file -> file.isDirectory ||
                file.name.endsWith(".txt") ||
                file.name.endsWith(".pdf") ||
                file.name.endsWith(".doc") ||
                file.name.endsWith(".docx")
        }

        if(filesAndFolders.isNullOrEmpty()){
            noFilesTextView.visibility = View.VISIBLE
            return
        }
        noFilesTextView.visibility = View.INVISIBLE

        filesAndFolders.sort()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = Adapter(applicationContext, filesAndFolders)
    }

}