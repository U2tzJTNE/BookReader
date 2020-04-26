package com.u2tzjtne.libreader;

import android.content.Context;

import com.u2tzjtne.libepub.EpubReader;
import com.u2tzjtne.libpdf.ReadActivity;

/**
 * @author u2tzjtne
 * @date 2020/4/23
 * Email u2tzjtne@gmail.com
 */
public class Reader {
    public static void openBook(Context context,Book book) {
        switch (book.getType()) {
            case PDF:
                ReadActivity.Companion.start(context,book.getPath(),book.getPageNumber());
                break;
            case TXT:
                break;
            case EPUB:
                EpubReader.get().openBook(book.getPath(),"0");
                break;
            default:
                break;
        }
    }
}
