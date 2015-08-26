package com.example.scino_book_project.adapter;


public class SimpleBookItem {

    String bookName = null;
    String authorName = null;

    Object picture = null;

    public SimpleBookItem(String boonName, String authorName,Object picture) {
        super();
        this.bookName = boonName;
        this.authorName = authorName;

        this.picture = picture;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String code) {
        this.bookName = code;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }



    public Object getPicture() {
        return picture;
    }

    public void setPicture(Object picture) {
        this.picture = picture;
    }

}