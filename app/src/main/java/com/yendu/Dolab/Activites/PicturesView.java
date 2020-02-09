package com.yendu.Dolab.Activites;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Slide;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.futuremind.recyclerviewfastscroll.FastScroller;
import com.yendu.Dolab.Adapters.PicturesAdapter;
import com.yendu.Dolab.Adapters.PicturesCursorAdapter;
import com.yendu.Dolab.Fragments.browserFragment;
import com.yendu.Dolab.Fragments.browserFragmentTest;
import com.yendu.Dolab.Models.PictureModel;
import com.yendu.Dolab.R;
import com.yendu.Dolab.Utils.ContentLoaderUtils;
import com.yendu.Dolab.interfaces.itemClickListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.yendu.Dolab.Utils.ContentLoaderUtils.DATEFORMAT;
import static com.yendu.Dolab.Utils.ContentLoaderUtils.calculateSize;
import static com.yendu.Dolab.Utils.ContentLoaderUtils.convertDate;

public class PicturesView extends AppCompatActivity implements itemClickListener , LoaderManager.LoaderCallbacks<Cursor> {

    RecyclerView picturesRecycler;
    public List<PictureModel> pictureModels;
    TextView albumName;
    String albumPath;
    public static String bucketName;
    public PicturesAdapter picturesAdapter;
    ProgressBar progressBar;
    FastScroller fastScroller;
    public PicturesCursorAdapter picturesCursorAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pictures_view);
        getSupportActionBar().hide();
        progressBar=findViewById(R.id.loader);
        fastScroller=findViewById(R.id.fastscroll_album_pictures);
        pictureModels=new ArrayList<>();
        albumName=findViewById(R.id.albumname);
        picturesRecycler=findViewById(R.id.pictures_recycler_view);
        picturesRecycler.hasFixedSize();
        picturesRecycler.setLayoutManager(new GridLayoutManager(this,3));
        bucketName=getIntent().getStringExtra("albumName");
        albumName.setText(bucketName);
        albumPath=getIntent().getStringExtra("albumPath");

        if(pictureModels.isEmpty()){
            progressBar.setVisibility(View.VISIBLE);

//            pictureModels = ContentLoaderUtils.getAllImagesFromFolder(this,albumPath);
//            picturesAdapter=new PicturesAdapter(this,pictureModels,this);
//            Log.d("albumPaths",String.valueOf(pictureModels.size()));
            picturesCursorAdapter=new PicturesCursorAdapter(this,null,this,null);
//            picturesRecycler.setAdapter(picturesAdapter);
            picturesRecycler.setAdapter(picturesCursorAdapter);
            fastScroller.setRecyclerView(picturesRecycler);
            fastScroller.setMotionEventSplittingEnabled(true);

        }else{

        }
        if(LoaderManager.getInstance(this).hasRunningLoaders()){
            LoaderManager.getInstance(this).restartLoader(99,null,this);
        }else{
            LoaderManager.getInstance(this).initLoader(99,null,this);
        }


    }



    @Override
    public void onBackPressed() {

//        try{
//           if( getSupportFragmentManager().findFragmentByTag("bowsFragment").getView()!=null){
//
//
//
//            pictureModels = ContentLoaderUtils.getAllImagesFromFolder(this,albumPath);
//            picturesAdapter=new PicturesAdapter(this,pictureModels,this);
////            Log.d("albumPaths",String.valueOf(pictureModels.size()));
//            picturesRecycler.setAdapter(picturesAdapter);}
//        }catch (Exception e){
//            super.onBackPressed();
//        }
        super.onBackPressed();

    }

    @Override
    public void onPicCLicked(PicturesAdapter.PictureHolder pictureHolder, int position, List<PictureModel> pictureModels) {
//        browserFragment bowsFragment=browserFragment.newInstance(pictureModels,PicturesView.this,position);
        browserFragmentTest bowsFragment=browserFragmentTest.newInstance(picturesCursorAdapter.cursor,this,position);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            bowsFragment.setEnterTransition(new Slide());
            bowsFragment.setExitTransition(new Slide());
        }
        getSupportFragmentManager()

                .beginTransaction()
//                .addSharedElement(pictureHolder.pictureImageView,position+"picture")
                .add(R.id.displayContainer,bowsFragment,"bowsFragment")
                .addToBackStack(null)

                .commit();

    }

    @Override
    public void onPicCLicked(String pictureFolderPath, String folderName) {

    }


    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.rename_context_menu:
//                Toast.makeText(getContext(),"renameToast",Toast.LENGTH_SHORT).show();
                AlertDialog.Builder alert=new AlertDialog.Builder(this);
                final EditText editText=new EditText(this);
                alert.setTitle("Rename");
                alert.setMessage("Enter Your Message");
                editText.setText(pictureModels.get(picturesAdapter.getPosition()).getName());
                editText.setMaxLines(1);
                editText.canScrollHorizontally(View.SCROLL_AXIS_HORIZONTAL);
                editText.selectAll();
                alert.setView(editText);
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        File secondPhoto=new File(pictureModels.get(picturesAdapter.getPosition()).getPath());
//                        Log.d("pathOfPicture",selectedPhoto.getPath());

//                        Path source=selectedPhoto.to\\;
//                        Files.move(source,source.resolveSibling(editText.getText().toString()));
                        if(!editText.getText().toString().isEmpty()){
                            String newName=editText.getText().toString();
                            File newPhotoFile=new File(secondPhoto.getParent()+"/"+newName);


                            if(secondPhoto.renameTo(newPhotoFile)){
//                                getContext().getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA+ "= ?",new String[]{secondPhoto.getAbsolutePath()});
                                ContentLoaderUtils.deleteFile(secondPhoto,PicturesView.this);
                                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(newPhotoFile)));
                                pictureModels.get(picturesAdapter.getPosition()).setName(newName);
                                pictureModels.get(picturesAdapter.getPosition()).setPath(newPhotoFile.getAbsolutePath());
                                picturesAdapter.notifyDataSetChanged();

                            }


//                            secondPhoto.

//
//                            Toast.makeText(getContext(),String.valueOf(succdss)+" "+String.valueOf(exist),Toast.LENGTH_SHORT).show();
//                            Log.d("pathpathpath",selectedPhoto.getPath());

                        }
                    }
                });

                alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                alert.show();
                break;


        }
        return super.onContextItemSelected(item);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
//        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Uri uri = MediaStore.Files.getContentUri("external");

        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                + " OR "
                + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN){
            String[] projection = {MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.DISPLAY_NAME, MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME,MediaStore.Files.FileColumns.DATE_TAKEN,MediaStore.Files.FileColumns.SIZE,MediaStore.Files.FileColumns.WIDTH,MediaStore.Files.FileColumns.HEIGHT,MediaStore.Files.FileColumns.MEDIA_TYPE};
        return new CursorLoader(this,uri, projection, "("+selection+") AND ("+MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME+" = ?)", new String[]{bucketName},MediaStore.Images.ImageColumns.DATE_TAKEN+" DESC");
        }else{
            String[] projection={MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.DISPLAY_NAME, MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME,MediaStore.Files.FileColumns.DATE_TAKEN,MediaStore.Files.FileColumns.SIZE,MediaStore.Files.FileColumns.MEDIA_TYPE};
            return new CursorLoader(this,uri, projection, "("+selection+") AND ("+MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME+" = ?)",new String[]{bucketName}, MediaStore.Images.ImageColumns.DATE_TAKEN+" DESC");
        }


    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        try{
            if(data!=null && data.getCount()>0){
//            picturesAdapter.pictureModelArrayList=getAllImagesFromFolder(this,bucketName,data);
                picturesCursorAdapter.changeCursor(data);
                picturesCursorAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }




    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
            picturesCursorAdapter.changeCursor(null);
            loader.reset();
    }
    public  List<PictureModel> getAllImagesFromFolder(Context context, String path, Cursor c) {

        String searchParams;
        List<PictureModel> tempPicturesList = new ArrayList<>();
//        MediaMetadataRetriever mediaMetadataRetriever=new MediaMetadataRetriever();
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//        Uri urii=MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String bucket=path.substring(path.lastIndexOf("/")+1);

        searchParams = "bucket_display_name = \"" + bucket + "\"";
//        Cursor mPhotoCursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, searchParams, null, orderBy + " DESC");

//        String[] projection = {MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DISPLAY_NAME, MediaStore.Images.Media.SIZE,MediaStore.Images.ImageColumns.DATE_ADDED};
//        Cursor c;
//        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN){
//            String[] projection = {MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DISPLAY_NAME, MediaStore.Images.Media.BUCKET_DISPLAY_NAME,MediaStore.Images.ImageColumns.DATE_ADDED,MediaStore.Images.ImageColumns.SIZE,MediaStore.Images.ImageColumns.WIDTH,MediaStore.Images.ImageColumns.HEIGHT};
//            c = context.getContentResolver().query(uri, projection, MediaStore.Images.Media.DATA + " like ?", new String[]{"%" + path + "%"}, MediaStore.Images.ImageColumns.DATE_ADDED+" DESC");
//        }else{
//            String[] projection={MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DISPLAY_NAME, MediaStore.Images.Media.BUCKET_DISPLAY_NAME,MediaStore.Images.ImageColumns.DATE_ADDED,MediaStore.Images.ImageColumns.SIZE,};
//            c = context.getContentResolver().query(uri, projection, MediaStore.Images.Media.DATA + " like ?", new String[]{"%" + path + "%"}, MediaStore.Images.ImageColumns.DATE_ADDED+" DESC");
//        }
//        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN){
            String[] projection = {MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DISPLAY_NAME, MediaStore.Images.Media.BUCKET_DISPLAY_NAME,MediaStore.Images.ImageColumns.DATE_TAKEN};
            c = context.getContentResolver().query(uri, projection, MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME+" = ?", new String[]{bucket},MediaStore.Images.ImageColumns.DATE_TAKEN+" DESC");
//        }else{
//            String[] projection={MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DISPLAY_NAME, MediaStore.Images.Media.BUCKET_DISPLAY_NAME,MediaStore.Images.ImageColumns.DATE_TAKEN};
//            c = context.getContentResolver().query(uri, projection, MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME+" = ?",new String[]{bucket}, MediaStore.Images.ImageColumns.DATE_TAKEN+" DESC");
//        }



        // Cursor c=context.getContentResolver().query(uri,projection,null,null, null);
        if (c != null) {

            while (c.moveToNext()) {
                PictureModel pictureModel = new PictureModel();


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
}
