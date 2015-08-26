package com.example.scino_book_project.adapter;

public class BookItem {

    String bookName = null;
    String authorName = null;
    boolean selected = false;
    Object picture = null;

    public BookItem(String boonName, String authorName, boolean selected, Object picture) {
        super();
        this.bookName = boonName;
        this.authorName = authorName;
        this.selected = selected;
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

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public Object getPicture() {
        return picture;
    }

    public void setPicture(Object picture) {
        this.picture = picture;
    }

}