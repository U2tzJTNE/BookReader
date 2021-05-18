package com.u2tzjtne.libtxt.util;

import android.content.ContentValues;
import android.os.Environment;
import android.text.TextUtils;

import com.u2tzjtne.libtxt.bean.Cache;
import com.u2tzjtne.libtxt.db.BookCatalogue;
import com.u2tzjtne.libtxt.db.BookList;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 * @date 2016/8/11 0011
 */
public class BookUtil {
    private static final String CACHED_PATH = Environment.getExternalStorageDirectory() + "/libtxt/";
    /**
     * 存储的字符数
     */
    private static final int cachedSize = 30000;

    private final ArrayList<Cache> myArray = new ArrayList<>();
    /**
     * 目录
     */
    private List<BookCatalogue> directoryList = new ArrayList<>();

    private String bookName;
    private String bookPath;
    private long bookLen;
    private long position;
    private BookList bookList;

    public BookUtil() {
        File file = new File(CACHED_PATH);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    public synchronized void openBook(BookList bookList) throws IOException {
        this.bookList = bookList;
        //如果当前缓存不是要打开的书本就缓存书本同时删除缓存

        if (bookPath == null || !bookPath.equals(bookList.getBookpath())) {
            cleanCacheFile();
            this.bookPath = bookList.getBookpath();
            bookName = FileUtils.getFileName(bookPath);
            cacheBook();
        }
    }

    private void cleanCacheFile() {
        File file = new File(CACHED_PATH);
        if (!file.exists()) {
            file.mkdir();
        } else {
            File[] files = file.listFiles();
            if (files != null) {
                for (File value : files) {
                    value.delete();
                }
            }
        }
    }

    public int next(boolean back) {
        position += 1;
        if (position > bookLen) {
            position = bookLen;
            return -1;
        }
        char result = current();
        if (back) {
            position -= 1;
        }
        return result;
    }

    public char[] nextLine() {
        if (position >= bookLen) {
            return null;
        }
        StringBuilder line = new StringBuilder();
        while (position < bookLen) {
            int word = next(false);
            if (word == -1) {
                break;
            }
            char wordChar = (char) word;
            if ("\r".equals(wordChar + "") && "\n".equals(((char) next(true)) + "")) {
                next(false);
                break;
            }
            line.append(wordChar);
        }
        return line.toString().toCharArray();
    }

    public char[] preLine() {
        if (position <= 0) {
            return null;
        }
        StringBuilder line = new StringBuilder();
        while (position >= 0) {
            int word = pre(false);
            if (word == -1) {
                break;
            }
            char wordChar = (char) word;
            if ("\n".equals(wordChar + "") && "\r".equals(((char) pre(true)) + "")) {
                pre(false);
                break;
            }
            line.insert(0, wordChar);
        }
        return line.toString().toCharArray();
    }

    public char current() {
        int cachePos = 0;
        int pos = 0;
        int len = 0;
        for (int i = 0; i < myArray.size(); i++) {
            long size = myArray.get(i).getSize();
            if (size + len - 1 >= position) {
                cachePos = i;
                pos = (int) (position - len);
                break;
            }
            len += size;
        }

        char[] charArray = block(cachePos);
        return charArray[pos];
    }

    public int pre(boolean back) {
        position -= 1;
        if (position < 0) {
            position = 0;
            return -1;
        }
        char result = current();
        if (back) {
            position += 1;
        }
        return result;
    }

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    /**
     * 缓存书本
     * @throws IOException
     */
    private void cacheBook() throws IOException {
        String mStrCharsetName;
        if (TextUtils.isEmpty(bookList.getCharset())) {
            mStrCharsetName = FileUtils.getCharset(bookPath);
            if (mStrCharsetName == null) {
                mStrCharsetName = "utf-8";
            }
            ContentValues values = new ContentValues();
            values.put("charset", mStrCharsetName);
            DataSupport.update(BookList.class, values, bookList.getId());
        } else {
            mStrCharsetName = bookList.getCharset();
        }

        File file = new File(bookPath);
        InputStreamReader reader = new InputStreamReader(new FileInputStream(file), mStrCharsetName);
        int index = 0;
        bookLen = 0;
        directoryList.clear();
        myArray.clear();
        while (true) {
            char[] buf = new char[cachedSize];
            int result = reader.read(buf);
            if (result == -1) {
                reader.close();
                break;
            }

            String bufStr = new String(buf);
            bufStr = bufStr.replaceAll("\r\n+\\s*", "\r\n\u3000\u3000");
            bufStr = bufStr.replaceAll("\u0000", "");
            buf = bufStr.toCharArray();
            bookLen += buf.length;

            Cache cache = new Cache();
            cache.setSize(buf.length);
            cache.setData(new WeakReference<char[]>(buf));

            myArray.add(cache);
            try {
                File cacheBook = new File(fileName(index));
                if (!cacheBook.exists()) {
                    cacheBook.createNewFile();
                }
                final OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(fileName(index)), StandardCharsets.UTF_16LE);
                writer.write(buf);
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException("Error during writing " + fileName(index));
            }
            index++;
        }

        new Thread() {
            @Override
            public void run() {
                getChapter();
            }
        }.start();
    }

    /**
     * 获取章节
     */
    private synchronized void getChapter() {
        try {
            long size = 0;
            for (int i = 0; i < myArray.size(); i++) {
                char[] buf = block(i);
                String bufStr = new String(buf);
                String[] paragraphs = bufStr.split("\r\n");
                for (String str : paragraphs) {
                    if (str.length() <= 30 && (str.matches(".*第.{1,8}章.*") || str.matches(".*第.{1,8}节.*"))) {
                        BookCatalogue bookCatalogue = new BookCatalogue();
                        bookCatalogue.setBookCatalogueStartPos(size);
                        bookCatalogue.setBookCatalogue(str);
                        bookCatalogue.setBookpath(bookPath);
                        directoryList.add(bookCatalogue);
                    }
                    if (str.contains("\u3000\u3000")) {
                        size += str.length() + 2;
                    } else if (str.contains("\u3000")) {
                        size += str.length() + 1;
                    } else {
                        size += str.length();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<BookCatalogue> getDirectoryList() {
        return directoryList;
    }

    public long getBookLen() {
        return bookLen;
    }

    private String fileName(int index) {
        return CACHED_PATH + bookName + index;
    }

    /**
     * 获取书本缓存
     * @param index
     * @return
     */
    private char[] block(int index) {
        if (myArray.size() == 0) {
            return new char[1];
        }
        char[] block = myArray.get(index).getData().get();
        if (block == null) {
            try {
                File file = new File(fileName(index));
                int size = (int) file.length();
                if (size < 0) {
                    throw new RuntimeException("Error during reading " + fileName(index));
                }
                block = new char[size / 2];
                InputStreamReader reader =
                        new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_16LE);
                if (reader.read(block) != block.length) {
                    throw new RuntimeException("Error during reading " + fileName(index));
                }
                reader.close();
            } catch (IOException e) {
                throw new RuntimeException("Error during reading " + fileName(index));
            }
            Cache cache = myArray.get(index);
            cache.setData(new WeakReference<>(block));
        }
        return block;
    }

}
