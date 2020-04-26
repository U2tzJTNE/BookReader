package com.u2tzjtne.libpdf

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.u2tzjtne.libpdf.listener.OnLoadCompleteListener
import com.u2tzjtne.libpdf.listener.OnPageChangeListener
import com.u2tzjtne.libpdf.listener.OnPageErrorListener
import com.u2tzjtne.libpdf.scroll.DefaultScrollHandle
import com.u2tzjtne.libpdf.util.FitPolicy
import kotlinx.android.synthetic.main.activity_pdf_read.*
import java.io.File

class ReadActivity : AppCompatActivity(),
    OnPageChangeListener, OnLoadCompleteListener, OnPageErrorListener {
    private var bookFile: File? = null
    private var pageNumber: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_read)
        initData()
        openBook()
    }

    private fun initData() {
        if (intent != null) {
            val bookPath = intent.getStringExtra("book_path")
            if (bookPath != null) {
                bookFile = File(bookPath)
                if (!bookFile!!.exists()) {
                    Toast.makeText(this, "file is not exists", Toast.LENGTH_SHORT).show()
                    return
                }
            }
            pageNumber = intent.getIntExtra("page_number", 0)
        } else {
            Toast.makeText(this, "please pass the corresponding fields", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        fun start(context: Context, bookPath: String, pageNumber: Int) {
            val intent = Intent(context, ReadActivity::class.java)
            intent.putExtra("book_path", bookPath)
            intent.putExtra("page_number", pageNumber)
            context.startActivity(intent)
        }
    }

    private fun openBook() {
        pdf_view.fromFile(bookFile)
            .defaultPage(pageNumber)
            .onPageChange(this)
            .enableAnnotationRendering(true)
            .onLoad(this)
            .scrollHandle(DefaultScrollHandle(this))
            .spacing(10) // in dp
            .onPageError(this)
            .pageFitPolicy(FitPolicy.BOTH)
            .load()
    }

    override fun onPageChanged(page: Int, pageCount: Int) {

    }

    override fun loadComplete(nbPages: Int) {

    }

    override fun onPageError(page: Int, t: Throwable?) {

    }
}
