package com.u2tzjtne.bookreader.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.u2tzjtne.bookreader.R
import com.u2tzjtne.libreader.Book
import com.u2tzjtne.libreader.Reader

class HomeFragment : Fragment(), View.OnClickListener {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        initView(root)
        return root
    }

    private fun initView(view: View) {
        val btnOpenEPUB: Button = view.findViewById(R.id.btn_open_epub)
        val btnOpenPDF: Button = view.findViewById(R.id.btn_open_pdf)
        val btnOpenTXT: Button = view.findViewById(R.id.btn_open_txt)

        btnOpenEPUB.setOnClickListener(this)
        btnOpenPDF.setOnClickListener(this)
        btnOpenTXT.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        if (view != null) {
            val book = Book()
            when (view.id) {
                R.id.btn_open_txt -> {
                    book.type = Book.Type.TXT
                }
                R.id.btn_open_epub -> {
                    book.type = Book.Type.EPUB
                }
                R.id.btn_open_pdf -> {
                    book.type = Book.Type.PDF
                }
            }
            Reader.openBook(book)
        }
    }
}
