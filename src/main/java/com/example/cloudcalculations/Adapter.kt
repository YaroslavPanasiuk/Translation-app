package com.example.cloudcalculations

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import java.io.File


class Adapter(var context: Context, var filesAndFolders: Array<File>) :
    RecyclerView.Adapter<Adapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.recycler_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val selectedFile = filesAndFolders[position]
            holder.textView.text = selectedFile.name
        if (selectedFile.isDirectory) {
            holder.imageView.setImageResource(R.drawable.ic_baseline_folder_24)
        } else {
            val extention = selectedFile.extension
            when (extention) {
                "pdf" -> { holder.imageView.setImageResource(R.drawable.ic_baseline_insert_drive_file_24_red) }
                "doc" -> { holder.imageView.setImageResource(R.drawable.ic_baseline_insert_drive_file_24) }
                "docx" -> { holder.imageView.setImageResource(R.drawable.ic_baseline_insert_drive_file_24_blue) }
                "txt" -> { holder.imageView.setImageResource(R.drawable.ic_baseline_insert_drive_file_24_black) }
                else -> {}
            }
        }
        holder.itemView.setOnClickListener(View.OnClickListener {
            if (selectedFile.isDirectory) {
                val intent = Intent(context, FileListActivity::class.java)
                val path = selectedFile.absolutePath
                intent.putExtra("path", path)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            } else {
                try {
                    val intent = Intent(context, WordsInFileActivity::class.java)
                    val path = selectedFile.absolutePath
                    intent.putExtra("path", path)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(
                        context.applicationContext,
                        "Cannot open the file",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }

    override fun getItemCount(): Int {
        if(!filesAndFolders.isNullOrEmpty()){
            return filesAndFolders!!.size
        }
        return 0
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textView: TextView = itemView.findViewById(R.id.file_name_text_view)
        var imageView: ImageView = itemView.findViewById(R.id.icon_view)

    }

}