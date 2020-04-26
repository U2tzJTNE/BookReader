package com.u2tzjtne.libreader;

/**
 * @author u2tzjtne
 * @date 2020/4/23
 * Email u2tzjtne@gmail.com
 */
public class Book {
    public static enum Type {
        PDF,
        TXT,
        EPUB
    }

    private Type type;
    private String path;
    private String name;
    private int pageNumber;

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
