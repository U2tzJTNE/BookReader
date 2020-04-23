package com.u2tzjtne.libreader;

/**
 * @author u2tzjtne
 * @date 2020/4/23
 * Email u2tzjtne@gmail.com
 */
public class Reader {
    public static void openBook(Book book) {
        switch (book.getType()) {
            case PDF:
                break;
            case TXT:
                break;
            case EPUB:
                break;
            default:
                break;
        }
    }
}
