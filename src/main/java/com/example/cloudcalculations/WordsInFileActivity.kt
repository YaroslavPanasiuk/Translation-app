package com.example.cloudcalculations

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.util.DisplayMetrics
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor
import org.apache.poi.hwpf.*
import org.apache.poi.hwpf.extractor.WordExtractor
import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.File
import java.io.FileInputStream

class WordsInFileActivity : AppCompatActivity() {


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_words_in_file)

        val fileNameTextView = findViewById<TextView>(R.id.FileNameTextView)
        val textBox = findViewById<EditText>(R.id.wordsInFileList)
        val addWordsToTopicButton = findViewById<Button>(R.id.addWordsFromFileBtn)
        val cancelButton = findViewById<Button>(R.id.cancelBtn)

        val file = File(intent.getStringExtra("path").toString())
        val extention = file.extension
        var textInFile = ""

        when (extention) {
            "pdf" -> { textInFile = readPDF(file) }
            "doc" -> { textInFile = readDOC(file) }
            "docx" -> { textInFile = readDOCX(file) }
            "txt" -> { textInFile = readTXT(file) }
            else -> { textInFile = "Cannot read file"
                Toast.makeText(this, textInFile, Toast.LENGTH_SHORT).show()
            }
        }
        textInFile = SharedMethods.wordsToText(SharedMethods.textToWords(textInFile))
        textBox.setText(textInFile)
        fileNameTextView.text = file.name

        addWordsToTopicButton.setOnClickListener {
            val intent = Intent(this, WordsInTopicActivity::class.java)
            intent.putExtra("wordsFromFile", textBox.text.toString())
            intent.putExtra("1", "1")
            Toast.makeText(this, "sent", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }
        cancelButton.setOnClickListener {
            val intent = Intent(this, WordsInTopicActivity::class.java)
            startActivity(intent)
        }
    }

    private fun readPDF(file:File):String {
        var textInFile = ""
        val document = com.itextpdf.kernel.pdf.PdfDocument(PdfReader(file.path))
        for (i in 1..document.numberOfPages) {
            val text = PdfTextExtractor.getTextFromPage(document.getPage(i))
            textInFile += text + "\n"
        }
        return textInFile
    }
    private fun readDOC(file:File):String{
        var textInFile = ""
        try{
            val fis = FileInputStream(file.absolutePath)
            val document = HWPFDocument(fis)
            val extractor = WordExtractor(document)
            val paragraphs = extractor.paragraphText
            for(paragraph in paragraphs){
                if(paragraph != null){
                    textInFile += paragraph + "\n"
                }
            }
            fis.close()
        } catch (e:Exception){
            textInFile = readDOCX(file)
        }
        return textInFile
    }
    private fun readDOCX(file:File):String{
        var textInFile = ""
        val fis = FileInputStream(file.absolutePath)
        val document = XWPFDocument(fis)
        val paragraphs = document.paragraphs
        for(paragraph in paragraphs){
            if(paragraphs != null){
                textInFile += paragraph.text + "\n"
            }
        }
        fis.close()
        return textInFile
    }
    private fun readTXT(file:File):String{
        var textInFile = ""
        for (line in file.readLines()) {
            textInFile += line + "\n"
        }
        return textInFile
    }
}