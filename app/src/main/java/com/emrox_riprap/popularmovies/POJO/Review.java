package com.emrox_riprap.popularmovies.POJO;

import com.bignerdranch.expandablerecyclerview.Model.ParentObject;

import java.util.List;

/**
 * Created by scott on 7/23/2016.
 */
public class Review implements ParentObject{

    private String author;
    private String content;

    private List<Object> mChildrenList;

    public Review() {
    }

    public Review(String author, String reviewText) {
        this.author = author;
        this.content = reviewText;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    //implements ParentObject for expandablelistview stuff from bignerdranch.
    @Override
    public List<Object> getChildObjectList() {
        return mChildrenList;
    }

    @Override
    public void setChildObjectList(List<Object> list) {
        mChildrenList = list;
    }


}
