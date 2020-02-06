package com.yendu.Dolab.Fragments;


import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import android.speech.RecognizerIntent;
import android.text.TextUtils;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
//import android.widget.CursorAdapter;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Slide;


//import com.crashlytics.android.Crashlytics;
import com.futuremind.recyclerviewfastscroll.FastScroller;

import com.yendu.Dolab.Adapters.PicturesAdapter;
import com.yendu.Dolab.Adapters.PicturesCursorAdapter;
import com.yendu.Dolab.Adapters.SpinnerAdapter;
import com.yendu.Dolab.Utils.ContentLoaderUtils;
import com.yendu.Dolab.Utils.LocaleHelper;
import com.yendu.Dolab.interfaces.ImenuController;
import com.yendu.Dolab.interfaces.IonBackPressed;
import com.yendu.Dolab.Models.PictureModel;
import com.yendu.Dolab.R;
import com.yendu.Dolab.interfaces.itemClickListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.yendu.Dolab.Activites.MainActivity.LANGUAGE;


public class PictureFragment extends Fragment implements itemClickListener, IonBackPressed, ImenuController, LoaderManager.LoaderCallbacks<Cursor> {

    private View view;

    private RecyclerView recyclerView;
    private Menu menu;
    private ProgressBar progressBar;
    private File albumFolder;
    private static String appname;
    private FastScroller fastScroller;
    private AlertDialog alertDialog;
    private EditText editText1;
    private boolean firstTimeLoaded = false;
    private static int imagesRenameNumber;
    private PicturesCursorAdapter picturesCursorAdapter;
    public static boolean Sorted = false;
    private MediaScannerConnection mediaScannerConnection;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.picture_fragment, container, false);
        recyclerView = view.findViewById(R.id.pictures_recycler_view_fragment);
        fastScroller = view.findViewById(R.id.fastscroll);
        progressBar = view.findViewById(R.id.picture_fragment_loader);

        refreshAdapter();


        return view;
    }

    @Override
    public void onStart() {
//        refreshAdapter();
        super.onStart();
    }

    private void refreshAdapter() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);

        picturesCursorAdapter = new PicturesCursorAdapter(getContext(), null, this, this);


//    3    picturesAdapter=new PicturesAdapter(getContext(),pictureModels,this,this);
        recyclerView.setLayoutManager(gridLayoutManager);
//        recyclerView.setLayoutManager(new GridView(getContext(),null));
        recyclerView.setAdapter(picturesCursorAdapter);

        fastScroller.setRecyclerView(recyclerView);


    }

    public void sort() {


        Sorted = !Sorted;
        LoaderManager.getInstance(this).restartLoader(100, null, this);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        appname = getString(R.string.app_name);

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
//         inflater.inflate(R.menu.selection_menu_item,menu);
        this.menu = menu;

//        menu.findItem(R.id.action_create_album).setVisible(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_rename:

                final Dialog dialog1=new Dialog(getContext(),android.R.style.Theme_DeviceDefault_Light_Dialog);
//                AlertDialog.Builder alertDial=new AlertDialog.Builder(getContext());
//                alertDial.setView(R.layout.dialog);
//                final AlertDialog dialog1=alertDial.create();
                dialog1.setContentView(R.layout.dialog);
                ((TextView)dialog1.findViewById(R.id.dialog_textView)).setText("Rename");
                editText1=((EditText)dialog1.findViewById(R.id.dialog_editText));
                dialog1.setCanceledOnTouchOutside(false);
                String name = picturesCursorAdapter.cursor.getString(1);
                final String format = name.substring(name.lastIndexOf("."));
                final String path=picturesCursorAdapter.cursor.getString(0);

                name = name.substring(0, name.lastIndexOf("."));
                editText1.setText(name);
                editText1.selectAll();
                ((TextView)dialog1.findViewById(R.id.ok_dialog_textView)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        File secondPhoto = new File(path);
                        if (!editText1.getText().toString().isEmpty()) {
                            if (!editText1.getText().toString().contains(".")) {


                                String newName = editText1.getText().toString();
                                File newPhotoFile = new File(secondPhoto.getParent() + "/" + newName + format);


                                for (int i = 0; i < 3; i++) {
                                    if (secondPhoto.renameTo(newPhotoFile)) {
//                                getContext().getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA+ "= ?",new String[]{secondPhoto.getAbsolutePath()});
                                        int media_type;
                                        if(isBuildAboveJellyBean()){
                                            media_type=picturesCursorAdapter.cursor.getInt(7);
                                        }else{
                                            media_type=picturesCursorAdapter.cursor.getInt(5);
                                        }
                                        if(media_type==1){
                                            ContentLoaderUtils.deleteFile(secondPhoto, getContext());
                                        }else{
                                            ContentLoaderUtils.deleteVideo(secondPhoto,getContext());
                                        }

                                        getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(newPhotoFile)));
                                        picturesCursorAdapter.selectedList.clear();
                                        rebackToNormal();
                                        picturesCursorAdapter.selected = false;
                                        break;

                                    } else {
                                        imagesRenameNumber *= 10;
                                        newName += String.valueOf(imagesRenameNumber);
                                        newPhotoFile = new File(secondPhoto.getParent() + "/" + newName + format);
                                    }

                                }


                            } else {
                                Toast.makeText(getContext(), "Dont use Dot(.) character", Toast.LENGTH_SHORT).show();
                            }

                        }
                        dialog1.cancel();

                    }
                });
                ((TextView)dialog1.findViewById(R.id.cancel_dialog_textView)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog1.cancel();
                    }
                });

                ((ImageButton)dialog1.findViewById(R.id.mic_button_dialog)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getSpeech();
                    }
                });
                final Spinner spinner1=((Spinner)dialog1.findViewById(R.id.spinner_toolbar_dialog));
                SpinnerAdapter spinnerAdapter1=new SpinnerAdapter(getContext(),R.array.language,R.array.shortformlanguage);
                spinner1.setAdapter(spinnerAdapter1);
                spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        switch (spinner1.getSelectedItemPosition()){
                            case 0:
                                LocaleHelper.setLocale(getContext(), "us");
                                LANGUAGE = "us";
//                        LocaleHelper.restart(MainActivity.this);
                                break;
                            case 1:
                                LocaleHelper.setLocale(getContext(), "vi");
                                LANGUAGE = "vi";
//                        LocaleHelper.restart(MainActivity.this);
                                break;
                            case 2:
                                LocaleHelper.setLocale(getContext(), "es");
                                LANGUAGE= "es";
//                        LocaleHelper.restart(MainActivity.this);
                                break;
                            case 3:
                                LocaleHelper.setLocale(getContext(),"zh");
                                LANGUAGE="zh";
//                        LocaleHelper.restart(MainActivity.this);

                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                dialog1.show();
                return true;

            case R.id.action_create_album:
                final Dialog dialog=new Dialog(getContext(),android.R.style.Theme_DeviceDefault_Light_Dialog);
                dialog.setContentView(R.layout.dialog);
                dialog.setCanceledOnTouchOutside(false);
                ((TextView)dialog.findViewById(R.id.dialog_textView)).setText("Create Tag");
                editText1=((EditText)dialog.findViewById(R.id.dialog_editText));
                ((TextView)dialog.findViewById(R.id.ok_dialog_textView)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!editText1.getText().toString().isEmpty()){
                        if (createAlbum(editText1.getText().toString())) {
                            if (picturesCursorAdapter.selectedList.size() == 1) {
                                picturesCursorAdapter.cursor.moveToPosition(picturesCursorAdapter.selectedList.get(0));

//                                   String des=ContentLoaderUtils.MoveFile(pictureModels.get(picturesAdapter.selectedList.get(0)).getPath(),albumFolder.getAbsolutePath());
                                String des = ContentLoaderUtils.MoveFile(picturesCursorAdapter.cursor.getString(0), albumFolder.getAbsolutePath());
                                updateMediaStore(getContext(),picturesCursorAdapter.cursor.getPosition(),albumFolder.getAbsolutePath());
                                picturesCursorAdapter.selectedList.clear();
                            }
                            if (picturesCursorAdapter.selectedList.size() > 1) {
                                MoveFile(picturesCursorAdapter.selectedList, albumFolder.getAbsolutePath());

                                updateMediaStore(getContext(), picturesCursorAdapter.selectedList, albumFolder.getAbsolutePath());

//                                   for(Iterator<Integer>iterator=picturesAdapter.selectedList.iterator();iterator.hasNext();){
//                                       Integer integer=iterator.next();
//                                       String path=albumFolder+"/"+pictureModels.get(integer).getName();
//                                       pictureModels.get(integer).setPath(path);
//
//                                       picturesAdapter.pictureModelArrayList.get(integer).setPath(path);
//                                       picturesAdapter.pictureModelArrayList.get(integer).setSelected(false);
//                                       iterator.remove();
//
//                                   }
                                picturesCursorAdapter.selectedList.clear();


                            }
                            rebackToNormal();
                        }

                        dialog.cancel();
                    }}

                    });
               final Spinner spinner=((Spinner)dialog.findViewById(R.id.spinner_toolbar_dialog));
                SpinnerAdapter spinnerAdapter=new SpinnerAdapter(getContext(),R.array.language,R.array.shortformlanguage);
                spinner.setAdapter(spinnerAdapter);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        switch (spinner.getSelectedItemPosition()){
                            case 0:
                                LocaleHelper.setLocale(getContext(), "us");
                                LANGUAGE = "us";
//                        LocaleHelper.restart(MainActivity.this);
                                break;
                            case 1:
                                LocaleHelper.setLocale(getContext(), "vi");
                                LANGUAGE = "vi";
//                        LocaleHelper.restart(MainActivity.this);
                                break;
                            case 2:
                                LocaleHelper.setLocale(getContext(), "es");
                                LANGUAGE= "es";
//                        LocaleHelper.restart(MainActivity.this);
                                break;
                            case 3:
                                LocaleHelper.setLocale(getContext(),"zh");
                                LANGUAGE="zh";
//                        LocaleHelper.restart(MainActivity.this);

                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                ((TextView)dialog.findViewById(R.id.cancel_dialog_textView)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                ((ImageButton)dialog.findViewById(R.id.mic_button_dialog)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getSpeech();
                    }
                });
                dialog.show();
                dialog.getWindow().setLayout((6 * getResources().getDisplayMetrics().widthPixels)/7, ViewGroup.LayoutParams.WRAP_CONTENT);

                return true;

            case R.id.action_delete:
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
                if (picturesCursorAdapter.selectedList.size() == 1) {
                    alertBuilder.setMessage("Delete selected item");

                } else {
                    alertBuilder.setMessage("Delete selected items");
                }
                alertBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (picturesCursorAdapter.selectedList.size() == 1) {
//                            File tobeDelete=new File(pictureModels.get(picturesCuAdapter.getPosition()).getPath());
                            picturesCursorAdapter.cursor.moveToPosition(picturesCursorAdapter.selectedList.get(0));
                            File tobeDeleted = new File(picturesCursorAdapter.cursor.getString(0));
                            int media_type;
                            if(isBuildAboveJellyBean()){
                                media_type=picturesCursorAdapter.cursor.getInt(7);
                            }else{
                                media_type=picturesCursorAdapter.cursor.getInt(5);
                            }
                            if(media_type==1){
                                ContentLoaderUtils.deleteFile(tobeDeleted, getContext());

                            }else{
                                ContentLoaderUtils.deleteVideo(tobeDeleted,getContext());
                            }
//                            picturesAdapter.pictureModelArrayList.remove(picturesAdapter.getPosition());
//                            picturesAdapter.notifyDataSetChanged();
                            rebackToNormal();
                            picturesCursorAdapter.selected = false;
                            picturesCursorAdapter.selectedList.clear();
                        }
                        if (picturesCursorAdapter.selectedList.size() > 1) {
                            deleteFiles(picturesCursorAdapter.selectedList);
//                            picturesAdapter.selectedList.remo
                            picturesCursorAdapter.selectedList.clear();
//                            picturesCursorAdapter.notifyDataSetChanged();
                            picturesCursorAdapter.selected = false;
                            rebackToNormal();

                        }
                    }
                });
                alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog = alertBuilder.create();
                alertDialog.show();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void updateMediaStore(Context context,int selectedIndex,String newCreatedAlbumPath){
        picturesCursorAdapter.cursor.moveToPosition(selectedIndex);
        String path=picturesCursorAdapter.cursor.getString(0);
        String newPath=newCreatedAlbumPath+"/"+picturesCursorAdapter.cursor.getString(1);
        int media_type;
        if(isBuildAboveJellyBean()){
            media_type=picturesCursorAdapter.cursor.getInt(7);
        }else{
            media_type=picturesCursorAdapter.cursor.getInt(5);
        }
        if(media_type==1){
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Images.ImageColumns.DATA, newPath);
            ContentValues contentValues1=new ContentValues();
            contentValues1.put(MediaStore.Files.FileColumns.DATA,newPath);

            try {
                context.getContentResolver().update(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues, MediaStore.Images.ImageColumns.DATA + "='" + path + "'", null);
                context.getContentResolver().update(MediaStore.Files.getContentUri("external"),contentValues1,MediaStore.Files.FileColumns.DATA+"='"+newPath+"'",null);
                //  context.getContentResolver().update(MediaStore.Files.getContentUri("external"), contentValues, MediaStore.Images.ImageColumns.DATA + "='" + path + "'", null);

            } catch (Exception ex) {
//
                ex.printStackTrace();

            }
        }else{
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Video.VideoColumns.DATA, newPath);
            ContentValues contentValues1=new ContentValues();
            contentValues1.put(MediaStore.Files.FileColumns.DATA,newPath);

            try {

                context.getContentResolver().update(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues, MediaStore.Video.VideoColumns.DATA + "='" + path + "'", null);
                context.getContentResolver().update(MediaStore.Files.getContentUri("external"), contentValues, MediaStore.Files.FileColumns.DATA + "='" + path + "'", null);


            } catch (Exception ex) {
//


            }
        }

    }

    private void updateMediaStore(final Context context, List<Integer> selected, String newCreateAlbumPath) {
        for (Integer integer : selected) {
            picturesCursorAdapter.cursor.moveToPosition(integer);
            String path = picturesCursorAdapter.cursor.getString(0);
//            String path=pictureModels.get(integer).getPath();
//            String newPath=newCreateAlbumPath+"/"+pictureModels.get(integer).getName();
            final String newPath = newCreateAlbumPath + "/" + picturesCursorAdapter.cursor.getString(1);

            int media_type;
            if(isBuildAboveJellyBean()){
                media_type=picturesCursorAdapter.cursor.getInt(7);
            }else{
                media_type=picturesCursorAdapter.cursor.getInt(5);
            }

            if(media_type==1){
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.Images.ImageColumns.DATA, newPath);
                ContentValues contentValues1=new ContentValues();
                contentValues1.put(MediaStore.Files.FileColumns.DATA,newPath);

                try {
                    context.getContentResolver().update(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues, MediaStore.Images.ImageColumns.DATA + "='" + path + "'", null);
                    context.getContentResolver().update(MediaStore.Files.getContentUri("external"),contentValues1,MediaStore.Files.FileColumns.DATA+"='"+newPath+"'",null);
                  //  context.getContentResolver().update(MediaStore.Files.getContentUri("external"), contentValues, MediaStore.Images.ImageColumns.DATA + "='" + path + "'", null);

                } catch (Exception ex) {
//
                    ex.printStackTrace();

                }
            }else{
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.Video.VideoColumns.DATA, newPath);
                ContentValues contentValues1=new ContentValues();
                contentValues1.put(MediaStore.Files.FileColumns.DATA,newPath);

                try {

                    context.getContentResolver().update(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues, MediaStore.Video.VideoColumns.DATA + "='" + path + "'", null);
                    context.getContentResolver().update(MediaStore.Files.getContentUri("external"), contentValues, MediaStore.Files.FileColumns.DATA + "='" + path + "'", null);


                } catch (Exception ex) {
//


                }
            }

        }
    }

    private void MoveFile(List<Integer> selected, String destination) {
        for (Integer integer : selected) {
            picturesCursorAdapter.cursor.moveToPosition(integer);
//            String path=pictureModels.get(integer).getPath();
            String path = picturesCursorAdapter.cursor.getString(0);
            String desti = destination + "/" + path.substring(path.lastIndexOf("/") + 1);
            ContentLoaderUtils.moveFile(path, desti);
        }
    }

    private void deleteFiles(List<Integer> selectedToBeDeleted) {
//        ArrayList<File>filesTobDeleted=new ArrayList<>();
        for (Integer integer : selectedToBeDeleted) {
            picturesCursorAdapter.cursor.moveToPosition(integer);
//            File file=new File(pictureModels.get(integer).getPath());
//            filesTobDeleted.
            int media_type;
            if(isBuildAboveJellyBean()){
                media_type=picturesCursorAdapter.cursor.getInt(7);
            }else{
                media_type=picturesCursorAdapter.cursor.getInt(5);
            }
            File file = new File(picturesCursorAdapter.cursor.getString(0));

            if(media_type==1){
                ContentLoaderUtils.deleteFile(file, getContext());

            }else{
                ContentLoaderUtils.deleteVideo(file,getContext());
            }

        }


    }

    private boolean createAlbum(String name) {
        String toBeNamed = ContentLoaderUtils.getExistedAlbumPath(getContext(), name);
        if (toBeNamed == null) {
            albumFolder = new File(Environment.getExternalStorageDirectory(), appname + "/" + name);

            albumFolder.mkdirs();
//
//            return true;
        } else {
            albumFolder = new File(toBeNamed);

        }
        Toast.makeText(getContext(), String.valueOf("Tag " + name + " created"), Toast.LENGTH_SHORT).show();
        return true;


    }

    @Override
    public void onPicCLicked(PicturesAdapter.PictureHolder pictureHolder, int position, List<PictureModel> pictureModels) {

//        browserFragment bowsFragment=browserFragment.newInstance(pictureModels,getContext(),position);
        browserFragmentTest bowsFragment = browserFragmentTest.newInstance(picturesCursorAdapter.cursor, getContext(), position);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bowsFragment.setEnterTransition(new Slide());
            bowsFragment.setExitTransition(new Slide());
        }

//        getFragmentManager().popBackStack(null,0);
        if(getFragmentManager()!=null){
            getFragmentManager()

                    .beginTransaction()

//                .addSharedElement(pictureHolder.pictureImageView,position+"picture")
                    .add(R.id.main_activity_container, bowsFragment)
                    .addToBackStack(bowsFragment.getClass().getName())
                    .commit();
        }


    }

    @Override
    public void onPicCLicked(String pictureFolderPath, String folderName) {

    }

    @Override
    public boolean onBackPressed() {

        if (picturesCursorAdapter != null) {


            if (picturesCursorAdapter.selected) {
                picturesCursorAdapter.selected = false;
                picturesCursorAdapter.refresh = true;
                picturesCursorAdapter.notifyDataSetChanged();

                rebackToNormal();


                return true;
            }
        }
        return false;
    }

    @Override
    public void menuController() {
        menu.findItem(R.id.action_rename).setVisible(false);


    }

    public void getSpeech() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, LANGUAGE);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, LANGUAGE);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, LANGUAGE);
        intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, LANGUAGE);

        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivityForResult(intent, 10);
        } else {
            Toast.makeText(getContext(), "Your Device Don't Support Speech Input", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (matches != null && matches.size() > 0) {
                        String searchWrd = matches.get(0);
                        if (!TextUtils.isEmpty(searchWrd)) {
                            editText1.setText(searchWrd);
                        }
                    }
                }
        }
    }

    @Override
    public void rebackToNormal() {
        menu.findItem(R.id.action_search).setVisible(true);
        menu.findItem(R.id.action_camera).setVisible(true);
//        menu.findItem(R.id.action_change_language).setVisible(true);
        menu.findItem(R.id.action_sort).setVisible(true);

        menu.findItem(R.id.action_rename).setVisible(false);
        menu.findItem(R.id.action_delete).setVisible(false);
        menu.findItem(R.id.action_voice_search).setVisible(true);
        menu.findItem(R.id.action_create_album).setVisible(false);
    }

    @Override
    public void renameMenuController() {
        menu.findItem(R.id.action_create_album).setVisible(true);
        menu.findItem(R.id.action_voice_search).setVisible(false);
//        menu.findItem(R.id.action_change_language).setVisible(false);
        menu.findItem(R.id.action_sort).setVisible(false);
        menu.findItem(R.id.action_camera).setVisible(false);
        menu.findItem(R.id.action_delete).setVisible(true);
        menu.findItem(R.id.action_rename).setVisible(true);
        menu.findItem(R.id.action_search).setVisible(false);
    }

    @Override
    public void onPause() {
        super.onPause();


    }

    @Override
    public void onResume() {
        super.onResume();
//        if(menu!=null){
//            rebackToNormal();
//        }

    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
//        Cursor c;
        if (id == 100) {
           // Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Uri uri=MediaStore.Files.getContentUri("external");
            String order = Sorted ? "ASC" : "DESC";
            String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                    + " OR "
                    + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
               // String[] projection = {MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DISPLAY_NAME, MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.ImageColumns.DATE_TAKEN, MediaStore.Images.ImageColumns.SIZE, MediaStore.Images.ImageColumns.WIDTH, MediaStore.Images.ImageColumns.HEIGHT};
               String[] projection={MediaStore.Files.FileColumns.DATA,MediaStore.Files.FileColumns.DISPLAY_NAME,MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME,MediaStore.Files.FileColumns.DATE_TAKEN,MediaStore.Files.FileColumns.SIZE,MediaStore.Files.FileColumns.WIDTH,MediaStore.Files.FileColumns.HEIGHT,MediaStore.Files.FileColumns.MEDIA_TYPE};
               // return new CursorLoader(getContext(), uri, projection, null, null, MediaStore.Images.ImageColumns.DATE_TAKEN + " " + order);
                return new CursorLoader(getContext(), uri, projection, selection, null, MediaStore.Files.FileColumns.DATE_TAKEN + " " + order);

            } else {
               // String[] projection = {MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DISPLAY_NAME, MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.ImageColumns.DATE_TAKEN, MediaStore.Images.ImageColumns.SIZE,};
                String[] projection={MediaStore.Files.FileColumns.DATA,MediaStore.Files.FileColumns.DISPLAY_NAME,MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME,MediaStore.Files.FileColumns.DATE_TAKEN,MediaStore.Files.FileColumns.SIZE,MediaStore.Files.FileColumns.MEDIA_TYPE};
                return new CursorLoader(getContext(), uri, projection, selection, null, MediaStore.Files.FileColumns.DATE_TAKEN + " " + order);

//                return new CursorLoader(getContext(), uri, projection, null, null, MediaStore.Images.ImageColumns.DATE_TAKEN + " " + order);
            }
        }
        return null;


    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        try{


        if (data != null && data.getCount() > 0) {


            if (picturesCursorAdapter == null) {
                picturesCursorAdapter = new PicturesCursorAdapter(getContext(), data, this, this);
                recyclerView.setAdapter(picturesCursorAdapter);
            } else {
                picturesCursorAdapter.changeCursor(data);
            }
            progressBar.setVisibility(View.GONE);


        }}catch(Exception ex){
          ex.printStackTrace();
            }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        if (!firstTimeLoaded) {
            LoaderManager.getInstance(this).initLoader(100, null, this);
            firstTimeLoaded = true;
        } else {
            LoaderManager.getInstance(this).restartLoader(100, null, this);
        }
//        setRetainInstance(true);
//        LoaderManager.getInstance(this).initLoader(100,null,this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        loader.reset();

    }
    private boolean isBuildAboveJellyBean(){
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.JELLY_BEAN){
            return true;
        }
        return false;
    }

}
