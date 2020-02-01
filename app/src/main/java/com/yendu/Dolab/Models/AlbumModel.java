package com.yendu.Dolab.Models;



public class AlbumModel implements Comparable<AlbumModel> {
   private String name;
   private int numberOfPictures=0;
   private int searchResult;
   private String firstPicturePath;
   private int dateModified;

    public int getSearchResult() {
        return searchResult;
    }

    public void setSearchResult(int searchResult) {
        this.searchResult = searchResult;
    }

    public int getNumberOfPictures() {
        return numberOfPictures;
    }

    public void setNumberOfPictures(int numberOfPictures) {
        this.numberOfPictures = numberOfPictures;

    }

    public int getDateModified() {
        return dateModified;
    }

    public void setDateModified(int dateModified) {
        this.dateModified = dateModified;
    }

    public String getFirstPicturePath() {
        return firstPicturePath;
    }

    public void setFirstPicturePath(String firstPicturePath) {
        this.firstPicturePath = firstPicturePath;
    }
    public void incrementNumOfPic(){
        numberOfPictures++;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    private String path;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPhotoCount() {
        return numberOfPictures;
    }

    public void setPhotoCount(int numberOfPictures) {
        this.numberOfPictures = numberOfPictures;
    }

    @Override
    public int compareTo(AlbumModel o) {
//        int search=o.getSearchResult();
//        if(search-this.searchResult==0){
//            return o.dateModified-this.dateModified;
//        }else{
//            return search-this.searchResult;
//        }
//        if(search-this.searchResult>0){
//            return 1;
//        }
//        if(search-this.searchResult<0){
//            return 0;
//        }
//        o.getSearchResult()-searchResultint compared=;
//        if(compared==0){
//            compared=o.dateModified-dateModified;
//        }

        return  o.getSearchResult()-searchResult;


    }


}
