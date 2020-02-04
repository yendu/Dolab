package com.yendu.Dolab.Utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
//import android.text.style.TtsSpan;
import android.util.Log;


import com.yendu.Dolab.Models.AlbumModel;
import com.yendu.Dolab.Models.PictureModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
//import java.sql.DatabaseMetaData;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
//import java.util.Locale;

public class ContentLoaderUtils {


    public static String DATEFORMAT="MM/dd/yyyy HH:mm:ss";
    public static String convertDate(String dateInSeconds,String dateFormat) {

        DateFormat simpleDateFormat=SimpleDateFormat.getDateTimeInstance();


        Date date= new Date();
        date.setTime(Long.parseLong(dateInSeconds));
//        Log.d("dateInmilisecond",String.valueOf(date.getTime()));
        return simpleDateFormat.format(date);
    }

    public static String calculateSize(String size){
        double sizeOfFile=Double.valueOf(size)/(1024d*1024d);
        String formattedSize;
        if(sizeOfFile>1){

            double s=Double.valueOf(size)/1048756d;

            formattedSize= String.format("%.01f", s);
            formattedSize+="  MB";
        }else{
            double s=Double.valueOf(size)/1024d;

            formattedSize=String.format("%.01f",s);
            formattedSize+="  KB";
        }
        return formattedSize;
    }
//    public static String convertDate(long dateInMilliseconds,String dateFormat) {
//        return DateFormat.format(dateFormat, dateInMilliseconds).toString();
//
//    }

    public static List<PictureModel> getAllImagesFromFolder(Context context) {

//
        List<PictureModel> tempPicturesList = new ArrayList<>();

        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        Cursor c;
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN){
           String[] projection = {MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DISPLAY_NAME, MediaStore.Images.Media.BUCKET_DISPLAY_NAME,MediaStore.Images.ImageColumns.DATE_TAKEN,MediaStore.Images.ImageColumns.SIZE,MediaStore.Images.ImageColumns.WIDTH,MediaStore.Images.ImageColumns.HEIGHT};
            c = context.getContentResolver().query(uri, projection, null, null, MediaStore.Images.ImageColumns.DATE_TAKEN+" DESC");
        }else{
            String[] projection={MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DISPLAY_NAME, MediaStore.Images.Media.BUCKET_DISPLAY_NAME,MediaStore.Images.ImageColumns.DATE_TAKEN,MediaStore.Images.ImageColumns.SIZE,};
            c = context.getContentResolver().query(uri, projection, null, null, MediaStore.Images.ImageColumns.DATE_TAKEN+" DESC");
        }




        if (c != null) {

            while (c.moveToNext()) {
                PictureModel pictureModel = new PictureModel();
                String picPath = c.getString(0);
                String title = c.getString(1);
//                String folder = c.getString(2);
                String dateAdded=convertDate(c.getString(3),DATEFORMAT);
                String size=c.getString(4);

                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN){
                    String width=c.getString(5);
                    String height=c.getString(6);
                    pictureModel.setWidth(width);
                    pictureModel.setHeight(height);
                }
                long dateAdde=c.getLong(3); ///get date added in long
//                String ss=c.getString(7);
                int date=(int)(dateAdde/10L); //cast to int
                pictureModel.setDateAdded(date);
                pictureModel.setPath(picPath);
//                if(title.contains(".")){
//                    realTitle=title.substring(0,title.lastIndexOf("."));
//                    pictureModel.setName(realTitle);
//                }else{
                    pictureModel.setName(title);
//                }
                pictureModel.setSize(calculateSize(size));

                pictureModel.setDate(dateAdded);





                tempPicturesList.add(pictureModel);

            }
            c.close();

        } else {

        }
        return tempPicturesList;

    }
    public static String getExistedAlbumPath(Context context,String name){
        Cursor cursor;
        String path=null;
        Uri uri=MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

            String []projection={MediaStore.Images.ImageColumns.DATA,MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME};
            cursor=context.getContentResolver().query(uri,projection,MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME+"= ?",new String[]{name},null);

         if(cursor.getCount()>0){
             cursor.moveToFirst();
             path=cursor.getString(0);
             path=path.substring(0,path.lastIndexOf("/"));
         }

         return path;
    }

    public static List<PictureModel> getAllImagesFromFolder(Context context, String path) {

      String searchParams;
        List<PictureModel> tempPicturesList = new ArrayList<>();
//        MediaMetadataRetriever mediaMetadataRetriever=new MediaMetadataRetriever();
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String bucket=path.substring(path.lastIndexOf("/")+1);

        searchParams = "bucket_display_name = \"" + bucket + "\"";
//        Cursor mPhotoCursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, searchParams, null, orderBy + " DESC");
//
//        String[] projection = {MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DISPLAY_NAME, MediaStore.Images.Media.SIZE,MediaStore.Images.ImageColumns.DATE_ADDED};
        Cursor c;
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN){
            String[] projection = {MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DISPLAY_NAME, MediaStore.Images.Media.BUCKET_DISPLAY_NAME,MediaStore.Images.ImageColumns.DATE_ADDED,MediaStore.Images.ImageColumns.SIZE,MediaStore.Images.ImageColumns.WIDTH,MediaStore.Images.ImageColumns.HEIGHT};
            c = context.getContentResolver().query(uri, projection, MediaStore.Images.Media.DATA + " like ?", new String[]{"%" + path + "%"}, MediaStore.Images.ImageColumns.DATE_ADDED+" DESC");
        }else{
            String[] projection={MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DISPLAY_NAME, MediaStore.Images.Media.BUCKET_DISPLAY_NAME,MediaStore.Images.ImageColumns.DATE_ADDED,MediaStore.Images.ImageColumns.SIZE,};
            c = context.getContentResolver().query(uri, projection, MediaStore.Images.Media.DATA + " like ?", new String[]{"%" + path + "%"}, MediaStore.Images.ImageColumns.DATE_ADDED+" DESC");
        }
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN){
            String[] projection = {MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DISPLAY_NAME, MediaStore.Images.Media.BUCKET_DISPLAY_NAME,MediaStore.Images.ImageColumns.DATE_TAKEN,MediaStore.Images.ImageColumns.SIZE,MediaStore.Images.ImageColumns.WIDTH,MediaStore.Images.ImageColumns.HEIGHT};
            c = context.getContentResolver().query(uri, projection, MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME+" = ?", new String[]{bucket},MediaStore.Images.ImageColumns.DATE_TAKEN+" DESC");
        }else{
            String[] projection={MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DISPLAY_NAME, MediaStore.Images.Media.BUCKET_DISPLAY_NAME,MediaStore.Images.ImageColumns.DATE_TAKEN,MediaStore.Images.ImageColumns.SIZE,};
            c = context.getContentResolver().query(uri, projection, MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME+" = ?",new String[]{bucket}, MediaStore.Images.ImageColumns.DATE_TAKEN+" DESC");
        }



        // Cursor c=context.getContentResolver().query(uri,projection,null,null, null);
        if (c != null) {

            while (c.moveToNext()) {
                PictureModel pictureModel = new PictureModel();
//                String picPath = c.getString(0);
//                String title = c.getString(1);
//
//
//                //    String name=c.getString(1);
//
////                mediaMetadataRetriever.setDataSource(path);
////                audioModel.setAlbumArt(mediaMetadataRetriever.getEmbeddedPicture());
////                String name=path.substring(path.lastIndexOf("/")+1,path.lastIndexOf("."));
////                if(name.contains("_")){
////                    String artits=name.substring(0,name.indexOf("_"));
////                    String album=name.substring(0,name.lastIndexOf("_"));
////                    audioModel.setArtist(artits);
////                    audioModel.setAlbum(album);
////                }
//
//                pictureModel.setPath(picPath);
////                if(title.contains(".")){
////                    pictureModel.setName(title.substring(0,title.lastIndexOf(".")));
////                }else{
//                    pictureModel.setName(title);
////                }
//
//                pictureModel.setSize(c.getString(2));

                String picPath = c.getString(0);
                String title = c.getString(1);
                String folder = c.getString(2);

                String dateAdded=convertDate(c.getString(3),DATEFORMAT);
//                String dateAdded=c.getString(3);
                Log.d("asdfasdfasdf",c.getString(3));
                String size=c.getString(4);



                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN){
                    String width=c.getString(5);
                    String height=c.getString(6);
                    pictureModel.setWidth(width);
                    pictureModel.setHeight(height);
                }

//                String ss=c.getString(7);


                pictureModel.setPath(picPath);
//                if(title.contains(".")){
//                    realTitle=title.substring(0,title.lastIndexOf("."));
//                    pictureModel.setName(realTitle);
//                }else{
                pictureModel.setName(title);
//                }
                pictureModel.setSize(calculateSize(size));

                pictureModel.setDate(dateAdded);

//                Log.d("titlesOfMusic",name);
                tempPicturesList.add(pictureModel);

            }
//            c.close();

        } else {

        }
        return tempPicturesList;

    }

//    public static List<AlbumModel> getAllAlbumss(Context context) {
////        List<AlbumModel> albumModels = new ArrayList<>();
////        List<String> picturePaths = new ArrayList<>();
////        Uri imagesUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//////        Uri imagesUriSd=MediaStore.Images.Media.INTERNAL_CONTENT_URI;
////
////
////        String[] projection = {MediaStore.Images.ImageColumns.DATA, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.BUCKET_DISPLAY_NAME,MediaStore.Images.Media.BUCKET_ID};
////       // String[] projection={MediaStore.Images.ImageColumns.DATA,MediaStore.Images.ImageColumns.DISPLAY_NAME,MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,MediaStore.Images.ImageColumns.BUCKET_ID};
//////        String[] projection={MediaStore.Images.ImageColumns.DATA,MediaStore.Images.ImageColumns.DISPLAY_NAME,MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
//////        Cursor cursorExternal=context.getContentResolver().query(imagesUriSd,projection,null,null,null);
////
////        Cursor cursor = context.getContentResolver().query(imagesUri, projection, null, null, null);
//////        Cursor cursor=new MergeCursor(new Cursor[]{cursorExternal,cursorInternal});
//////        try {
////            if (cursor != null) {
////                cursor.moveToFirst();
////
////                while (cursor.moveToNext()) {
////                    AlbumModel albumModel = new AlbumModel();
////                    String name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
////                    String folder = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
////                    String datapath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
////                    Log.d("checkfinalcheck", datapath);
////                    String folderpaths = datapath.substring(0, datapath.lastIndexOf(folder + "/"));
////                    folderpaths = folderpaths + folder + "/";
////                    if (!picturePaths.contains(folderpaths)) {
////                        picturePaths.add(folderpaths);
////
////                        albumModel.setPath(folderpaths);
////                        albumModel.setName(folder);
////                        albumModel.setFirstPicturePath(datapath);//if the folder has only one picture this line helps to set it as first so as to avoid blank image in itemview
////                        albumModel.incrementNumOfPic();
////                        albumModels.add(albumModel);
////                    } else {
////                        for (int i = 0; i < albumModels.size(); i++) {
////                            if (albumModels.get(i).getPath().equals(folderpaths)) {
////                                albumModels.get(i).setFirstPicturePath(datapath);
////                                albumModels.get(i).incrementNumOfPic();
////                            }
////                        }
////                    }
////                }
////                cursor.close();
////
////            }
//////        } catch (Exception e) {
//////            e.printStackTrace();
//////        }
////        return albumModels//        ;
//        ArrayList<AlbumModel> picFolders = new ArrayList<>();
//        ArrayList<String> picPaths = new ArrayList<>();
//        Uri allImagesuri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//        String[] projection = {MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DISPLAY_NAME,
//                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, MediaStore.Images.ImageColumns.BUCKET_ID};
//        Cursor cursor = context.getContentResolver().query(allImagesuri, projection, null, null, null);
//        try {
//            if (cursor != null) {
//                cursor.moveToFirst();
//            }
//            do {
//                AlbumModel folds = new AlbumModel();
//                String name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
//                String folder = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
//                String datapath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
//                Log.d("pleaseplease",folder);
//                //String folderpaths =  datapath.replace(name,"");
//                String folderpaths = datapath.substring(0, datapath.lastIndexOf(folder + "/"));
//                folderpaths = folderpaths + folder + "/";
//                if (!picPaths.contains(folderpaths)) {
//                    picPaths.add(folderpaths);
//
//                    folds.setPath(folderpaths);
//                    folds.setName(folder);
//                    folds.setFirstPicturePath(datapath);//if the folder has only one picture this line helps to set it as first so as to avoid blank image in itemview
//                    folds.incrementNumOfPic();
//                    picFolders.add(folds);
//                } else {
//                    for (int i = 0; i < picFolders.size(); i++) {
//                        if (picFolders.get(i).getPath().equals(folderpaths)) {
//                            picFolders.get(i).setFirstPicturePath(datapath);
//                            picFolders.get(i).incrementNumOfPic();
//                        }
//                    }
//                }
//            } while (cursor.moveToNext());
//            cursor.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return picFolders;
//
//    }
    public static List<AlbumModel>getAllAlbums(Context context){
        List<AlbumModel> galleryPhotoAlbums = new ArrayList<>();
    String[] PROJECTION_BUCKET = {MediaStore.Images.ImageColumns.BUCKET_ID, MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, MediaStore.Images.ImageColumns.DATE_TAKEN, MediaStore.Images.ImageColumns.DATA};
    String BUCKET_GROUP_BY = "1) GROUP BY 1,(2";
    String BUCKET_ORDER_BY = "MAX(datetaken) DESC";
    Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    Cursor cur = context.getContentResolver().query(images, PROJECTION_BUCKET, BUCKET_GROUP_BY, null, MediaStore.Images.ImageColumns.DATE_TAKEN+" DESC" );
    AlbumModel album;
        if(cur.moveToFirst())

    {
        String bucket;

        String data;

        int bucketColumn = cur.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

        int dataColumn = cur.getColumnIndex(MediaStore.Images.Media.DATA);

        do {
            // Get the field values
            bucket = cur.getString(bucketColumn);


            data = cur.getString(dataColumn);

            if (bucket != null && bucket.length() > 0) {
                album = new AlbumModel();
//                album.setBucketId(bucketId);
                album.setName(bucket);
                album.setFirstPicturePath(data);
                album.setPath(data.substring(0,data.lastIndexOf("/")));
                File filePath=new File(album.getPath());
                if(filePath.exists()){
                   int dateModified= (int)(filePath.lastModified()/10L);
                   album.setDateModified(dateModified);
                }
                album.setNumberOfPictures(photoCountByAlbum(context,bucket));

                galleryPhotoAlbums.add(album);

//

            }


        } while (cur.moveToNext());

    }

        cur.close();
        return galleryPhotoAlbums;
}
    public static int photoCountByAlbum(Context context,String bucketName) {
         Cursor mPhotoCursor=null;
         Cursor mVideosCursor = null;
        try {
//            String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
//                    + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
//                    + " OR "
//                    + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
//                    + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;


           String searchParams = "bucket_display_name = \"" + bucketName + "\"";
          // Uri uri=MediaStore.Files.getContentUri("external");
           //MediaStore.
             mPhotoCursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, searchParams, null, null);
             mVideosCursor=context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,null,searchParams,null,null);
          //  Cursor mPhotoCursor = context.getContentResolver().query(uri, null, searchParams, null, null);

            if (mPhotoCursor.getCount() > 0 || mVideosCursor.getCount()>0) {

                return mPhotoCursor.getCount() + mVideosCursor.getCount();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(mVideosCursor!=null){
                mVideosCursor.close();
            }
            if(mPhotoCursor!=null){
                mPhotoCursor.close();
            }


        }

        return 0;
    }

    public static void deleteFile(File file, Context context){
        String[] projectio={MediaStore.Images.Media._ID};
        String selection=MediaStore.Images.Media.DATA+ " = ?";
        String[] selectionArgs=new String[]{file.getAbsolutePath()};

        Uri queryUri=MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver contentResolver=context.getContentResolver();
        Cursor cursor=contentResolver.query(queryUri,projectio,selection,selectionArgs,null);
        try{

            if (cursor != null && cursor.moveToFirst()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                Uri deleteUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                contentResolver.delete(deleteUri, null, null);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

        cursor.close();
    }
    public static void deleteFiles(List<PictureModel>pictureModels,Context context){
        for(PictureModel pictureModel:pictureModels){
            File file=new File(pictureModel.getPath());
            deleteFile(file,context);
        }

    }
    public static void updateMediaStore(Context context, ArrayList<String>paths, String newCreateAlbumPath){
        for (String path:paths){
            String newPath=newCreateAlbumPath+"/"+path.substring(path.lastIndexOf("/"));

            ContentValues contentValues=new ContentValues();
            contentValues.put(MediaStore.Images.ImageColumns.DATA,newPath);
            context.getContentResolver().update(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues, MediaStore.Images.ImageColumns.DATA+ "='"+path+"'",null);

        }

    }
    public static void updateMediaStore(Context context,String newCreatedPath,String oldpath){
        String newPath=newCreatedPath+"/"+oldpath.substring(oldpath.lastIndexOf("/")+1);

        ContentValues contentValues=new ContentValues();
        contentValues.put(MediaStore.Images.ImageColumns.DATA,newPath);
        context.getContentResolver().update(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues, MediaStore.Images.ImageColumns.DATA+ "='"+oldpath+"'",null);

    }
    public static String MoveFile(String source,String destination){
//        File sources=new File(source);
        String destinations=destination+"/"+source.substring(source.lastIndexOf("/")+1);


        moveFile(source,destinations);
        return destinations;

    }
    public static void MoveFile(ArrayList<String>source,String destination){
        for(String path:source){
            String desti=destination+"/"+path.substring(path.lastIndexOf("/"));
            moveFile(path,desti);
        }
    }
    public static void moveFile(String inputPath, String outputPath) {

        InputStream in = null;
        OutputStream out = null;
        try {



            in = new FileInputStream(inputPath);
            out = new FileOutputStream(outputPath);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file
            out.flush();
            out.close();
            out = null;

            // delete the original file
            new File(inputPath).delete();


        }

        catch (FileNotFoundException fnfe1) {

        }
        catch (Exception e) {

        }

    }



}
