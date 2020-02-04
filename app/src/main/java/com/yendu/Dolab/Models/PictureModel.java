package com.yendu.Dolab.Models;

public class PictureModel implements Comparable<PictureModel> {
    private String path;
    private String size;
    private String name;
    private String width;
    private String height;
    private String date;
    private int dateAdded;
    private int media_type;

    public String getDate() {
        return date;
    }

    public int getDateAdded() {
        return dateAdded;
    }
//
    public void setDateAdded(int dateAdded) {
        this.dateAdded = dateAdded;
    }

    public void setDate(String date) {
        this.date = date;

    }

    public int getMedia_type() {
        return media_type;
    }

    public void setMedia_type(int media_type) {
        this.media_type = media_type;
    }

    private boolean selected=false;
    private int SearchResult;

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public int getSearchResult() {
        return SearchResult;
    }

    public void setSearchResult(int searchResult) {
        SearchResult = searchResult;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;


    }

    @Override
    public int compareTo(PictureModel o) {
//        int search=((PictureModel)o).getSearchResult();
//        if(search-this.SearchResult==0){
//
//            return ((PictureModel)o).dateAdded-this.dateAdded;
//        }else{
//            return search-this.SearchResult;
//        }

//        int search=o.getSearchResult()-SearchResult;
//        if(search==0){
//            search=o.getDateAdded()-dateAdded;
//        }
        return o.getSearchResult()-SearchResult;


    }
}
