package com.yendu.Dolab.Activites;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Slide;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import android.provider.MediaStore;

import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.futuremind.recyclerviewfastscroll.FastScroller;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.yendu.Dolab.Adapters.PicturesAdapter;
import com.yendu.Dolab.Adapters.PicturesCursorAdapter;
import com.yendu.Dolab.Adapters.SpinnerAdapter;
import com.yendu.Dolab.Fragments.browserFragmentTest;
import com.yendu.Dolab.Models.PictureModel;
import com.yendu.Dolab.R;
import com.yendu.Dolab.Utils.ContentLoaderUtils;
import com.yendu.Dolab.Utils.LocaleHelper;
import com.yendu.Dolab.interfaces.ImenuController;
import com.yendu.Dolab.interfaces.IonBackPressed;
import com.yendu.Dolab.interfaces.itemClickListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.yendu.Dolab.Activites.MainActivity.LANGUAGE;
import static com.yendu.Dolab.Activites.MainActivity.SORTCODE;
import static com.yendu.Dolab.Fragments.PictureFragment.appname;
import static com.yendu.Dolab.Fragments.PictureFragment.isBuildAboveJellyBean;

public class PicturesView extends AppCompatActivity implements itemClickListener , IonBackPressed, ImenuController, LoaderManager.LoaderCallbacks<Cursor> {

    RecyclerView picturesRecycler;
    public List<PictureModel> pictureModels;
    String albumPath;
    MaterialSearchView materialSearchView;
    Toolbar toolbar;
    Spinner spinner;
    private File albumFolder;
    private AlertDialog alertDialog;
    TextView noImageFoundTextView;
    private boolean sorted=false;
    public static String bucketName;
    private boolean checkForCallingFromLoader=false;
    ProgressBar progressBar;
    FastScroller fastScroller;
    private static int imagesRenameNumber;

    Menu menu;
    private EditText editText1;
    public PicturesCursorAdapter picturesCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pictures_view);
      //  getSupportActionBar().hide();
        spinner=findViewById(R.id.spinner_toolbar_activity_album);
        SpinnerAdapter spinnerAdapter=new SpinnerAdapter(this,R.array.language,R.array.shortformlanguage);
        spinner.setAdapter(spinnerAdapter);
        noImageFoundTextView=findViewById(R.id.no_picture_activity_album);

        materialSearchView = findViewById(R.id.activity_main_searchView_activity_album);

        materialSearchView.setVoiceSearch(true);

        toolbar = findViewById(R.id.activity_album_toolbar);
        toolbar.setTitle(bucketName);

        setSupportActionBar(toolbar);

        progressBar=findViewById(R.id.loader);
        fastScroller=findViewById(R.id.fastscroll_album_pictures);
        pictureModels=new ArrayList<>();
       // albumName=findViewById(R.id.albumname);
        picturesRecycler=findViewById(R.id.pictures_recycler_view);
        picturesRecycler.hasFixedSize();
        picturesRecycler.setLayoutManager(new GridLayoutManager(this,3));
        bucketName=getIntent().getStringExtra("albumName");
        //albumName.setText(bucketName);

        getSupportActionBar().setTitle(bucketName);
        albumPath=getIntent().getStringExtra("albumPath");
        initSpinnerSelection();

        if(pictureModels.isEmpty()){
            progressBar.setVisibility(View.VISIBLE);

//            pictureModels = ContentLoaderUtils.getAllImagesFromFolder(this,albumPath);
//            picturesAdapter=new PicturesAdapter(this,pictureModels,this);
//            Log.d("albumPaths",String.valueOf(pictureModels.size()));
            picturesCursorAdapter=new PicturesCursorAdapter(this,null,this,this);
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
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                Context context;

                switch (spinner.getSelectedItemPosition()){
                    case 0:
                        LocaleHelper.setLocale(PicturesView.this, "us");
                        LANGUAGE = "us";
//                        LocaleHelper.restart(MainActivity.this);
                        break;
                    case 1:
                        LocaleHelper.setLocale(PicturesView.this, "vi");
                        LANGUAGE = "vi";
//                        LocaleHelper.restart(MainActivity.this);
                        break;
                    case 2:
                        LocaleHelper.setLocale(PicturesView.this, "es");
                        LANGUAGE= "es";
//                        LocaleHelper.restart(MainActivity.this);
                        break;
                    case 3:
                        LocaleHelper.setLocale(PicturesView.this,"zh");
                        LANGUAGE="zh";
//                        LocaleHelper.restart(MainActivity.this);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        materialSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText!=null && !newText.isEmpty()){
                    search(newText);


                }
                return true;
            }
        });
        materialSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {


            }

            @Override
            public void onSearchViewClosed() {

                LoadPictures();
            }
        });


    }
    private void LoadPictures(){
        LoaderManager.getInstance(this).restartLoader(99,null,this);

    }



    @Override
    public void onBackPressed() {



        if(materialSearchView.isSearchOpen()){
            if(checkForCallingFromLoader){
                checkForCallingFromLoader=false;
            }else{
                materialSearchView.closeSearch();

            }
        }else if (picturesCursorAdapter.selected) {
            picturesCursorAdapter.selected = false;
            picturesCursorAdapter.refresh = true;
            picturesCursorAdapter.notifyDataSetChanged();

            rebackToNormal();
        }


        else{
            super.onBackPressed();

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10 && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    materialSearchView.showSearch();

                    materialSearchView.setQuery(searchWrd, false);
                }
            }

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPicCLicked(PicturesAdapter.PictureHolder pictureHolder, int position, List<PictureModel> pictureModels) {
//        browserFragment bowsFragment=browserFragment.newInstance(pictureModels,PicturesView.this,position);
        browserFragmentTest bowsFragment=browserFragmentTest.newInstance(picturesCursorAdapter.cursor,this,position);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            bowsFragment.setEnterTransition(new Slide());
            bowsFragment.setExitTransition(new Slide());
        }
        if(materialSearchView.isSearchOpen()){
            materialSearchView.closeSearch();
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
        String[] projection2={MediaStore.Files.FileColumns.DATA,MediaStore.Files.FileColumns.TITLE,MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME,MediaStore.Files.FileColumns.DATE_TAKEN,MediaStore.Files.FileColumns.SIZE,MediaStore.Files.FileColumns.MEDIA_TYPE};
        String[] projection = {MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.DISPLAY_NAME, MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME,MediaStore.Files.FileColumns.DATE_TAKEN,MediaStore.Files.FileColumns.SIZE,MediaStore.Files.FileColumns.WIDTH,MediaStore.Files.FileColumns.HEIGHT,MediaStore.Files.FileColumns.MEDIA_TYPE};
        String order1;
        if (sorted) {
            order1 = "ASC";
        } else {
            order1 = "DESC";

        }

        if (args != null) {
            String s = args.getString("SEARCHWORDS");
            String bucketSelection=MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME+ "=" +"'"+bucketName+"'";
            String[] splited = s.split("\\s+");
            StringBuilder selectionn = new StringBuilder();
//                selection.append(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME);
            String[] argss = new String[splited.length];
            if (splited.length > 1) {
                for (int i = 0; i < splited.length; i++) {
                    argss[i] = "%" + splited[i] + "%";
//                    selection.append();
                    if (i + 1 == splited.length) {


                        selectionn.append(MediaStore.Files.FileColumns.TITLE + " like ?");
                        //  selection.append(MediaStore.Images.ImageColumns.DISPLAY_NAME + " like ?");

                        break;
                    }
                    selectionn.append(MediaStore.Files.FileColumns.TITLE + " like ? or ");
                    // selection.append(MediaStore.Images.ImageColumns.DISPLAY_NAME + " like ? or ");


                }

            } else {
                argss[0] = "%" + splited[0] + "%";
                selectionn.append(MediaStore.Files.FileColumns.TITLE + " like ?");
                //  selection.append(MediaStore.Images.ImageColumns.TITLE + " like ?");

            }
//

           String order = "CASE WHEN _display_name ='" + s + "' THEN 0 WHEN _display_name LIKE '" + s + "%" + "' THEN 1 WHEN _display_name LIKE '" + "%" + s + "%" + "' THEN 2 WHEN _display_name LIKE '" + "%" + s + "' THEN 3 ELSE 4 END, _display_name DESC";
            // return new CursorLoader(getContext(), images, projection, "("+selectionn+") AND ("+selection.toString()+")", argss, order);
            if(Build.VERSION.SDK_INT>Build.VERSION_CODES.JELLY_BEAN){
                return new CursorLoader(this, uri, projection, "("+selectionn+") AND ("+selection.toString()+") AND ("+bucketSelection+")", argss, order);

            }else{
                return new CursorLoader(this, uri, projection2, "("+selectionn+") AND ("+selection.toString()+") AND ("+bucketSelection+")", argss, order);

            }
//            return new CursorLoader(getContext(), images, projection, selection.toString(), argss, order);

        }else{
            if(Build.VERSION.SDK_INT>Build.VERSION_CODES.JELLY_BEAN){
                return new CursorLoader(this,uri, projection, "("+selection+") AND ("+MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME+" = ?)", new String[]{bucketName},MediaStore.Images.ImageColumns.DATE_TAKEN+" "+order1);

            }else{
                return new CursorLoader(this,uri, projection, "("+selection+") AND ("+MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME+" = ?)", new String[]{bucketName},MediaStore.Images.ImageColumns.DATE_TAKEN+" "+order1);

            }
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
                noImageFoundTextView.setVisibility(View.GONE);
            }else{
                picturesCursorAdapter.changeCursor(data);
                picturesCursorAdapter.notifyDataSetChanged();
                checkForCallingFromLoader=true;
                noImageFoundTextView.setVisibility(View.VISIBLE);
                onBackPressed();

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;

        getMenuInflater().inflate(R.menu.menu_item_search, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        materialSearchView.setMenuItem(menuItem);

        return true;
    }
    private void initSpinnerSelection(){
        switch (LANGUAGE){
            case "us":
                spinner.setSelection(0);
                break;
            case "vi":
                spinner.setSelection(1);
                break;
            case "es":
                spinner.setSelection(2);
                break;
            case "zh":
                spinner.setSelection(3);
                break;
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_voice_search:
                getSpeech();
//                menu.add(R.id.action_create_album).setVisible(true);

                return true;
            case R.id.action_rename:
                final Dialog dialog1=new Dialog(PicturesView.this,android.R.style.Theme_DeviceDefault_Light_Dialog);
//                AlertDialog.Builder alertDial=new AlertDialog.Builder(getContext());
//                alertDial.setView(R.layout.dialog);
//                final AlertDialog dialog1=alertDial.create();
                dialog1.setContentView(R.layout.dialog);
                ((TextView)dialog1.findViewById(R.id.dialog_textView)).setText(R.string.rename);
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
                                            ContentLoaderUtils.deleteFile(secondPhoto, PicturesView.this);
                                        }else{
                                            ContentLoaderUtils.deleteVideo(secondPhoto,PicturesView.this);
                                        }

                                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(newPhotoFile)));
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
                                Toast.makeText(PicturesView.this, "Dont use Dot(.) character", Toast.LENGTH_SHORT).show();
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
                SpinnerAdapter spinnerAdapter1=new SpinnerAdapter(this,R.array.language,R.array.shortformlanguage);
                spinner1.setAdapter(spinnerAdapter1);
                spinner1.setSelection(MainActivity.SELECTEDLANGUAGEINDEX);
                spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        switch (spinner1.getSelectedItemPosition()){
                            case 0:
                                LocaleHelper.setLocale(PicturesView.this, "us");
                                LANGUAGE = "us";
                                MainActivity.SELECTEDLANGUAGEINDEX=0;
//                        LocaleHelper.restart(MainActivity.this);
                                break;
                            case 1:
                                LocaleHelper.setLocale(PicturesView.this, "vi");
                                LANGUAGE = "vi";
                                MainActivity.SELECTEDLANGUAGEINDEX=1;
//                        LocaleHelper.restart(MainActivity.this);
                                break;
                            case 2:
                                LocaleHelper.setLocale(PicturesView.this, "es");
                                LANGUAGE= "es";
                                MainActivity.SELECTEDLANGUAGEINDEX=2;
//                        LocaleHelper.restart(MainActivity.this);
                                break;
                            case 3:
                                LocaleHelper.setLocale(PicturesView.this,"zh");
                                LANGUAGE="zh";
                                MainActivity.SELECTEDLANGUAGEINDEX=2;
//                        LocaleHelper.restart(MainActivity.this);

                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                dialog1.show();
                return true;
            case R.id.action_camera:
                openCamera();
                return true;
//            case R.id.action_change_language:
//                changeLanguage();
//                return true;
            case R.id.action_sort:

                if(SORTCODE.equals("ASCENDING")){
                    SORTCODE="DESCENDING";
                }else{
                    SORTCODE="ASCENDING";
                }

                switch (SORTCODE){
                    case "ASCENDING":
                        item.setIcon(R.drawable.ic_keyboard_arrow_up_black_24dp);
                      //  albumFragment.sort();
                       // pictureFragment.sort();
//                        pictureFragment.refreshAdapter();
                        sorted=!sorted;
                        LoadPictures();
                        break;
                    case "DESCENDING":
                        item.setIcon(R.drawable.ic_keyboard_arrow_down_black_24dp);
                        //albumFragment.sort();
//                        pictureFragment.refreshAdapter();
                        //pictureFragment.sort();
                        sorted=!sorted;
                        LoadPictures();
                        break;

                }



//                alertShow();
                return true;
            case R.id.action_delete:
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                if (picturesCursorAdapter.selectedList.size() == 1) {
                    alertBuilder.setMessage("Delete selected item");

                } else {
                    alertBuilder.setMessage("Delete selected items");
                }
                alertBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (picturesCursorAdapter.selectedList.size() >=1) {
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
            case R.id.action_create_album:
                final Dialog dialog=new Dialog(this,android.R.style.Theme_DeviceDefault_Light_Dialog);
                dialog.setContentView(R.layout.dialog);
                dialog.setCanceledOnTouchOutside(false);
                ((TextView)dialog.findViewById(R.id.dialog_textView)).setText("Create Tag");
                editText1=((EditText)dialog.findViewById(R.id.dialog_editText));
                ((TextView)dialog.findViewById(R.id.ok_dialog_textView)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!editText1.getText().toString().isEmpty()){
                            if (createAlbum(editText1.getText().toString())) {
                                if (picturesCursorAdapter.selectedList.size() >= 1) {
                                    MoveFile(picturesCursorAdapter.selectedList, albumFolder.getAbsolutePath());

                                    updateMediaStore(PicturesView.this, picturesCursorAdapter.selectedList, albumFolder.getAbsolutePath());

                                    picturesCursorAdapter.selectedList.clear();


                                }
                                picturesCursorAdapter.selected=false;
                                rebackToNormal();
                            }

                            dialog.cancel();
                        }}

                });
                final Spinner spinner=((Spinner)dialog.findViewById(R.id.spinner_toolbar_dialog));
                SpinnerAdapter spinnerAdapter=new SpinnerAdapter(this,R.array.language,R.array.shortformlanguage);
                spinner.setAdapter(spinnerAdapter);
                spinner.setSelection(MainActivity.SELECTEDLANGUAGEINDEX);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        switch (spinner.getSelectedItemPosition()){
                            case 0:
                                LocaleHelper.setLocale(PicturesView.this, "us");
                                LANGUAGE = "us";
                                MainActivity.SELECTEDLANGUAGEINDEX=0;
//                        LocaleHelper.restart(MainActivity.this);
                                break;
                            case 1:
                                LocaleHelper.setLocale(PicturesView.this, "vi");
                                LANGUAGE = "vi";
                                MainActivity.SELECTEDLANGUAGEINDEX=1;
//                        LocaleHelper.restart(MainActivity.this);
                                break;
                            case 2:
                                LocaleHelper.setLocale(PicturesView.this, "es");
                                LANGUAGE= "es";
                                MainActivity.SELECTEDLANGUAGEINDEX=2;
//                        LocaleHelper.restart(MainActivity.this);
                                break;
                            case 3:
                                LocaleHelper.setLocale(PicturesView.this,"zh");
                                LANGUAGE="zh";
                                MainActivity.SELECTEDLANGUAGEINDEX=3;
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
        }
        return super.onOptionsItemSelected(item);
    }
    private boolean createAlbum(String name) {
        String toBeNamed = ContentLoaderUtils.getExistedAlbumPath(this, name);
        if (toBeNamed == null) {
            albumFolder = new File(Environment.getExternalStorageDirectory(), appname + "/" + name);

            albumFolder.mkdirs();
//
//            return true;
        } else {
            albumFolder = new File(toBeNamed);

        }
        Toast.makeText(this, String.valueOf("Tag " + name + " created"), Toast.LENGTH_SHORT).show();
        return true;


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
                    context.getContentResolver().update(MediaStore.Files.getContentUri("external"), contentValues1, MediaStore.Files.FileColumns.DATA + "='" + path + "'", null);


                } catch (Exception ex) {
//


                }
            }

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
                ContentLoaderUtils.deleteFile(file, this);

            }else{
                ContentLoaderUtils.deleteVideo(file,this);
            }

        }


    }

    private void openCamera(){
        Intent intent=new Intent();
        intent.setAction(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);

        if(intent.resolveActivity(getPackageManager())!=null){
            startActivity(intent);
        }
    }
    public void getSpeech() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, LANGUAGE);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, LANGUAGE);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, LANGUAGE);
        intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, LANGUAGE);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 10);
        } else {
            Toast.makeText(this, getString(R.string.messageAboutSpeech), Toast.LENGTH_LONG).show();
        }
    }
    public void search(String search) {
        Bundle bundle = new Bundle();
        bundle.putString("SEARCHWORDS", search);
        LoaderManager.getInstance(this).restartLoader(99, bundle, this);
    }

    @Override
    public void menuController() {
        menu.findItem(R.id.action_rename).setVisible(false);

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
    private void MoveFile(List<Integer> selected, String destination) {
        for (Integer integer : selected) {
            picturesCursorAdapter.cursor.moveToPosition(integer);
//            String path=pictureModels.get(integer).getPath();
            String path = picturesCursorAdapter.cursor.getString(0);
            String desti = destination + "/" + path.substring(path.lastIndexOf("/") + 1);
            ContentLoaderUtils.moveFile(path, desti);
        }
    }

    @Override
    public boolean onbackpressed() {
        return false;
    }
}
