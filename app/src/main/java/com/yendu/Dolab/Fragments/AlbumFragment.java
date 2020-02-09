package com.yendu.Dolab.Fragments;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
//import android.net.Uri;
import android.database.Cursor;
import android.database.CursorJoiner;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.text.TextUtils;

import android.view.LayoutInflater;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//import com.crashlytics.android.Crashlytics;
import com.futuremind.recyclerviewfastscroll.FastScroller;

import com.yendu.Dolab.Activites.MainActivity;
import com.yendu.Dolab.Adapters.AlbumsCursorAdapter;
import com.yendu.Dolab.Adapters.SpinnerAdapter;

import com.yendu.Dolab.Utils.ContentLoaderUtils;
import com.yendu.Dolab.Models.PictureModel;
import com.yendu.Dolab.R;
import com.yendu.Dolab.Utils.LocaleHelper;

import java.io.File;
import java.util.ArrayList;

import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.yendu.Dolab.Activites.MainActivity.LANGUAGE;

public class AlbumFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static boolean sorted = false;
    public TextView textViewNoAlbum;

    public AlbumsCursorAdapter albumsCursorAdapter;
    private RecyclerView recyclerView;

    public int cursorCount = 0;
    private View view;
    private ProgressBar progressBar;
    private boolean firstTimeLoaded = false;
    private EditText editText1;
    private FastScroller fastScroller;

    public AlbumFragment() {


    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        if (!firstTimeLoaded) {

            LoaderManager.getInstance(this).initLoader(88, null, this);
            firstTimeLoaded = true;
        } else {
            LoaderManager.getInstance(this).restartLoader(88, null, this);


        }

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @NonNull
    @Override

    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        if (id == 88) {
//            String[] PROJECTION_BUCKET = {MediaStore.Images.ImageColumns.BUCKET_ID,MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, MediaStore.Images.ImageColumns.DATE_TAKEN, MediaStore.Images.ImageColumns.DATA};
            String[] PROJECTION_BUCKET = {MediaStore.Files.FileColumns.BUCKET_ID, MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME, MediaStore.Files.FileColumns.DATE_TAKEN, MediaStore.Files.FileColumns.DATA};
            String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                    + " OR "
                    + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

            String BUCKET_GROUP_BY = "1) GROUP BY 1,(2";
//            String BUCKET_ORDER_BY = "MAX(datetaken) DESC";
            String order;
            if (sorted) {
                order = "ASC";
            } else {
                order = "DESC";

            }

//            Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Uri images = MediaStore.Files.getContentUri("external");

            if (args != null) {
                String s = args.getString("SEARCHWORDS");
                String[] splited = s.split("\\s+");
                StringBuilder selectionn = new StringBuilder();
//                selection.append(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME);
                String[] argss = new String[splited.length];
                if (splited.length > 1) {
                    for (int i = 0; i < splited.length; i++) {
                        argss[i] = "%" + splited[i] + "%";
//                    selection.append();
                        if (i + 1 == splited.length) {
                            selectionn.append(MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME + " like ? or ");
                            selectionn.append(MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME + " like ?");
                            break;
                        }
                        selectionn.append(MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME + " like ? or ");
                        //CursorJoiner cursorJoiner=new CursorJoiner(getContext());


                    }

                } else {
                    argss[0] = "%" + splited[0] + "%";
                    selectionn.append(MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME + " like ?");
                }
//
                order = "CASE WHEN bucket_display_name ='" + s + "' THEN 0 WHEN bucket_display_name LIKE '" + s + "%" + "' THEN 1 WHEN bucket_display_name LIKE '" + "%" + s + "%" + "' THEN 2 WHEN bucket_display_name LIKE '" + "%" + s + "' THEN 3 ELSE 4 END, bucket_display_name DESC";
                return new CursorLoader(getContext(), images, PROJECTION_BUCKET, "(" + selection + ") AND (" + selectionn.toString() + ")) GROUP BY 1,(2", argss, order);
//
            }
//

            return new CursorLoader(getContext(), images, PROJECTION_BUCKET, selection + ") GROUP BY 1,(2", null, MediaStore.Files.FileColumns.DATE_TAKEN + " " + order);
//            return new CursorLoader(getContext(),images,PROJECTION_BUCKET,BUCKET_GROUP_BY,null,order);
        }

        return null;


    }

    public void LoadAlbum() {
        LoaderManager.getInstance(this).restartLoader(88, null, this);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        try {
            if (data != null && data.getCount() > 0) {


                cursorCount = data.getCount();
                if (albumsCursorAdapter == null) {
                    albumsCursorAdapter = new AlbumsCursorAdapter(getContext(), data);
                    recyclerView.setAdapter(albumsCursorAdapter);
                } else {
                    albumsCursorAdapter.changeCursor(data);
                    albumsCursorAdapter.notifyDataSetChanged();
                }
                progressBar.setVisibility(View.GONE);

                textViewNoAlbum.setVisibility(View.GONE);


            } else {
                cursorCount = 0;
                albumsCursorAdapter.changeCursor(data);
                albumsCursorAdapter.notifyDataSetChanged();
                textViewNoAlbum.setVisibility(View.VISIBLE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
//        loader.reset();
        albumsCursorAdapter.changeCursor(null);
//        loader.reset();


    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        registerForContextMenu(recyclerView);
    }


    @Override
    public void onStart() {
//        refreshAdapter();
        super.onStart();
    }


    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.rename_context_menu:
                final Dialog dialog1 = new Dialog(getContext(), android.R.style.Theme_DeviceDefault_Light_Dialog);
                dialog1.setContentView(R.layout.dialog);
                ((TextView) dialog1.findViewById(R.id.dialog_textView)).setText(R.string.rename);
                editText1 = ((EditText) dialog1.findViewById(R.id.dialog_editText));

                editText1.setText(albumsCursorAdapter.cursor.getString(1));
                final String path = albumsCursorAdapter.cursor.getString(3);
                editText1.selectAll();
                ((TextView) dialog1.findViewById(R.id.ok_dialog_textView)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        File secondPhoto =new File(albumsCursorAdapter.cursor.getString(3).substring(0,albumsCursorAdapter.cursor.getString(3).lastIndexOf("/"))) ;
                        File oldAlbum = new File(path.substring(0, path.lastIndexOf("/")));

                        if (!editText1.getText().toString().isEmpty()) {
                            String newName = editText1.getText().toString();
                            File newAlbum = new File(oldAlbum.getParent() + "/" + newName);


                            if (oldAlbum.renameTo(newAlbum)) {
                                updateMediaStore(getContext(), ContentLoaderUtils.getAllImagesFromFolder(getContext(), oldAlbum.getAbsolutePath()), newAlbum.getAbsolutePath());
                               // albumsCursorAdapter.notifyDataSetChanged();
                               // refresh();

                            } else {
                                Toast.makeText(getContext(), "Can't rename this album", Toast.LENGTH_SHORT).show();
                            }


//                            secondPhoto.

//
                            dialog1.cancel();

                        }
                    }
                });
                ((TextView) dialog1.findViewById(R.id.cancel_dialog_textView)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog1.cancel();
                    }
                });

                ((ImageButton) dialog1.findViewById(R.id.mic_button_dialog)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getSpeech();
                    }
                });
                final Spinner spinner1 = ((Spinner) dialog1.findViewById(R.id.spinner_toolbar_dialog));
                SpinnerAdapter spinnerAdapter1 = new SpinnerAdapter(getContext(), R.array.language, R.array.shortformlanguage);
                spinner1.setAdapter(spinnerAdapter1);
                spinner1.setSelection(MainActivity.SELECTEDLANGUAGEINDEX);
                spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        switch (spinner1.getSelectedItemPosition()) {
                            case 0:
                                LocaleHelper.setLocale(getContext(), "us");
                                LANGUAGE = "us";
                                MainActivity.SELECTEDLANGUAGEINDEX=0;
//                        LocaleHelper.restart(MainActivity.this);
                                break;
                            case 1:
                                LocaleHelper.setLocale(getContext(), "vi");
                                LANGUAGE = "vi";
                                MainActivity.SELECTEDLANGUAGEINDEX=1;
//                        LocaleHelper.restart(MainActivity.this);
                                break;
                            case 2:
                                LocaleHelper.setLocale(getContext(), "es");
                                LANGUAGE = "es";
                                MainActivity.SELECTEDLANGUAGEINDEX=2;
//                        LocaleHelper.restart(MainActivity.this);
                                break;
                            case 3:
                                LocaleHelper.setLocale(getContext(), "zh");
                                LANGUAGE = "zh";
                                MainActivity.SELECTEDLANGUAGEINDEX=3;
//                        LocaleHelper.restart(MainActivity.this);

                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                dialog1.setCanceledOnTouchOutside(false);
                dialog1.show();

                return true;
            case R.id.delete_context_menu:
                final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
                alertBuilder.setMessage("Delete selected album");
                alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ContentLoaderUtils.deleteFiles(ContentLoaderUtils.getAllImagesFromFolder(getContext(), albumsCursorAdapter.cursor.getString(3).substring(0, albumsCursorAdapter.cursor.getString(3).lastIndexOf("/"))), getContext());


                    }
                });
                alertBuilder.create().show();

        }

        return super.onContextItemSelected(item);
    }

    private void updateMediaStore(Context context, List<PictureModel> selected, String newCreateAlbumPath) {
        for (PictureModel images : selected) {
            String path = images.getPath();
            String newPath = newCreateAlbumPath + "/" + images.getName();
//            String newPath=newCreateAlbumPath;
//            String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
//                    + MediaStore.Images.ImageColumns.MEDIA_TYPE_IMAGE
//                    + " OR "
//                    + MediaStore.Images.ImageColumns.MEDIA_TYPE + "="
//                    + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

            Uri uri=MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            try{
                if(images.getMedia_type()==1){
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MediaStore.Images.ImageColumns.DATA, newPath);

                    context.getContentResolver().update(uri, contentValues, MediaStore.Images.ImageColumns.DATA + "='" + path + "'", null);
                }else {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MediaStore.Video.VideoColumns.DATA, newPath);
                    context.getContentResolver().update(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues, MediaStore.Video.VideoColumns.DATA + "='" + path + "'", null);

                }
                ContentValues contentValues1=new ContentValues();
                contentValues1.put(MediaStore.Files.FileColumns.DATA,newPath);
                context.getContentResolver().update(MediaStore.Files.getContentUri("external"),contentValues1,MediaStore.Files.FileColumns.DATA+ "='"+newPath+"'",null);


            }catch (Exception ex){
                ex.printStackTrace();
            }



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

    private void getSpeech() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, LANGUAGE);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, LANGUAGE);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, LANGUAGE);
        intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, LANGUAGE);

        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivityForResult(intent, 10);
        } else {
            Toast.makeText(getContext(), "Your Device Does n't Support Speech Input", Toast.LENGTH_LONG).show();
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.album_fragment, container, false);
        recyclerView = view.findViewById(R.id.albums_recycler_view_fragment);
        textViewNoAlbum = view.findViewById(R.id.album_fragment_no_album_text_view);
        progressBar = view.findViewById(R.id.album_fragment_loader);
        fastScroller = view.findViewById(R.id.fastscroll_album);

        refreshAdapter();
        return view;
    }

    private void refreshAdapter() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);

        albumsCursorAdapter = new AlbumsCursorAdapter(getContext(), null);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(albumsCursorAdapter);

        fastScroller.setRecyclerView(recyclerView);
    }

    public void sort() {
        sorted = !sorted;
        refresh();

    }
    public void refresh(){
        LoaderManager.getInstance(this).restartLoader(88, null, this);

    }

    public void search(String search) {
        Bundle bundle = new Bundle();
        bundle.putString("SEARCHWORDS", search);
        LoaderManager.getInstance(this).restartLoader(88, bundle, this);
    }
}
