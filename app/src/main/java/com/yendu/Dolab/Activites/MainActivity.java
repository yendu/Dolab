package com.yendu.Dolab.Activites;

//import android.app.AlertDialog;
//
//import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.text.TextUtils;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
//import android.view.ViewGroup;
import android.widget.AdapterView;


import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
//import androidx.loader.app.LoaderManager;


import com.google.android.material.bottomnavigation.BottomNavigationView;
//import com.google.firebase.analytics.FirebaseAnalytics;

import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.yendu.Dolab.Adapters.AlbumsCursorAdapter;
import com.yendu.Dolab.Adapters.SpinnerAdapter;
import com.yendu.Dolab.Fragments.AlbumFragment;
import com.yendu.Dolab.Fragments.PictureFragment;
import com.yendu.Dolab.Fragments.SearchFragment;
import com.yendu.Dolab.Fragments.SearchFragmentTest;
import com.yendu.Dolab.Models.AlbumModel;
import com.yendu.Dolab.Models.PictureModel;
import com.yendu.Dolab.R;
//import com.yendu.Dolab.Utils.ContentLoaderUtils;
import com.yendu.Dolab.Utils.LocaleHelper;
import com.yendu.Dolab.interfaces.IonBackPressed;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    MaterialSearchView materialSearchView;
    Toolbar toolbar;
    Spinner spinner;
    Menu menu;
   public static String LANGUAGE = "us";
   public static String SORTCODE="DESCENDING";
    BottomNavigationView bottomNavigationView;
    FragmentManager fragmentManager;
    Fragment active;
    AlbumFragment albumFragment;
    PictureFragment pictureFragment;
    SearchFragmentTest searchFragment;
    private boolean checkChangeOfSort=false;
    private boolean fromVoice=false;
    String query=null;
//    boolean isSearchViewOpen=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        spinner=findViewById(R.id.spinner_toolbar);
        SpinnerAdapter spinnerAdapter=new SpinnerAdapter(this,R.array.language,R.array.shortformlanguage);
        spinner.setAdapter(spinnerAdapter);

        materialSearchView = findViewById(R.id.activity_main_searchView);

        materialSearchView.setVoiceSearch(true);

        toolbar = findViewById(R.id.activity_main_toolbar);


        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        initSpinnerSelection();
        albumFragment = new AlbumFragment();

        pictureFragment = new PictureFragment();
        fragmentManager = getSupportFragmentManager();
        active = albumFragment;
//        System.setProperty("java.util.Arrays.useLegacyMergeSort","true");
        fragmentManager.beginTransaction().add(R.id.container_activity_main, pictureFragment, "2").hide(pictureFragment).commit();
        fragmentManager.beginTransaction().add(R.id.container_activity_main, albumFragment, "1").commit();
        bottomNavigationView = findViewById(R.id.bottom_navigation);
//        bottomNavigationView.getOrCreateBadge(R.id.navigation_albums).setNumber(2);d
        searchFragment=new SearchFragmentTest(null,0,MainActivity.this);
//        searchFragment=new SearchFragmentTest();

        fragmentManager.beginTransaction().add(R.id.container_activity_main, searchFragment, "3").hide(searchFragment).commit();
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                Context context;

                switch (spinner.getSelectedItemPosition()){
                    case 0:
                        LocaleHelper.setLocale(MainActivity.this, "us");
                        LANGUAGE = "us";
//                        LocaleHelper.restart(MainActivity.this);
                        break;
                    case 1:
                        LocaleHelper.setLocale(MainActivity.this, "vi");
                        LANGUAGE = "vi";
//                        LocaleHelper.restart(MainActivity.this);
                        break;
                    case 2:
                        LocaleHelper.setLocale(MainActivity.this, "es");
                        LANGUAGE= "es";
//                        LocaleHelper.restart(MainActivity.this);
                        break;
                    case 3:
                        LocaleHelper.setLocale(MainActivity.this,"zh");
                        LANGUAGE="zh";
//                        LocaleHelper.restart(MainActivity.this);

                }
            }



            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        materialSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

                if(active instanceof AlbumFragment){
                    if(!fromVoice){
                        albumFragment.albumsCursorAdapter.changeCursor(null);
                        albumFragment.textViewNoAlbum.setVisibility(View.VISIBLE);
                    }


                }else{
                    fragmentManager.beginTransaction()
//                            .replace(R.id.container_activity_main,searchFragment,"3")
//                            .replace(searchFragment)
                            .hide(active)
                            .show(searchFragment)
                            .commit();
//                    searchFragment.indicatorAdapter.changeCursor(null);
                    searchFragment.cursor=null;
                    searchFragment.imagesPager.notifyDataSetChanged();
                    searchFragment.imageViewText.setVisibility(View.VISIBLE);
                    searchFragment.indicatorAdapter.changeCursor(null);
                    active = searchFragment;
                }
                if(!fromVoice){
                    albumFragment.cursorCount=0;
                    searchFragment.count=0;
                    bottomNavigationView.getOrCreateBadge(R.id.navigation_albums).setNumber(0);
                    bottomNavigationView.getOrCreateBadge(R.id.navigation_pictures).setNumber(0);
                }
                if(fromVoice){
                    fromVoice=false;
                }





            }

            @Override
            public void onSearchViewClosed() {
                if(active instanceof AlbumFragment){
//                    fragmentManager.beginTransaction().hide(active).show(albumFragment).commit();
//                    albumFragment.getAllAlbum();
                     albumFragment.LoadAlbum();
//                    active = albumFragment;
                }else{
//                    fragmentManager.beginTransaction().replace(R.id.container_activity_main,pictureFragment,"2").show(pictureFragment).commit();
                    albumFragment.LoadAlbum();
                    fragmentManager.beginTransaction().hide(active).show(pictureFragment).commit();
                    active=pictureFragment;
                }

                bottomNavigationView.getOrCreateBadge(R.id.navigation_pictures).setVisible(false);
                bottomNavigationView.getOrCreateBadge(R.id.navigation_albums).setVisible(false);
//                if(!isSearchViewOpen){
//                    isSearchViewOpen=true;
//                }

//                menu.getItem(R.id.action_create_album).setVisible(true);

            }
        });
        materialSearchView.setVoiceSearch(true);
        materialSearchView.setVoiceIcon(getResources().getDrawable(R.drawable.ic_action_voice_search));
        AppCompatImageButton button=findViewById(R.id.action_voice_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSpeech();
            }
        });
        materialSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {


                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText != null && !newText.isEmpty()) {
//                    if(active instanceof AlbumFragment){
                        searchAlbums(newText);
//                    }else{
                        searchPictures(newText);
//                        if(!bottomNavigationView.getBadge(R.id.navigation_albums).isVisible() && !bottomNavigationView.getBadge(R.id.navigation_pictures).isVisible()){
//                            bottomNavigationView.getBadge(R.id.navigation_pictures).setVisible(true);
//                            bottomNavigationView.getBadge(R.id.navigation_albums).setVisible(true);
//                        }
                        new Handler().postDelayed(setNumber,500);


//                    }


                }
                query=newText;

//                searchFragment.queryChanged();
                return true;
            }

        });
    }
    private Runnable setNumber=new Runnable() {
        @Override
        public void run() {
            bottomNavigationView.getOrCreateBadge(R.id.navigation_pictures).setNumber(searchFragment.count);
            bottomNavigationView.getOrCreateBadge(R.id.navigation_albums).setNumber(albumFragment.cursorCount);
            bottomNavigationView.getBadge(R.id.navigation_pictures).setVisible(true);
            bottomNavigationView.getBadge(R.id.navigation_albums).setVisible(true);
        }
    };

    @Override
    public void onContentChanged() {
        super.onContentChanged();
    }

    private void searchPictures(String newText){

        searchFragment.queryChanged(newText);

//        List<PictureModel> foundPictures = new ArrayList<>(/*matchCounter.keySet()*/);
//        for(PictureModel pictureModel:pictureFragment.pictureModels){
//            String[]searchText=newText.split("\\s+");
//            String[] tobeSearched=pictureModel.getName().split("\\s+");
//            int matchedWords=0;
//            for(String n:searchText){
//
//                for(String j:tobeSearched){
//                    if(j.contains(".")){
//                        j=j.substring(0,j.lastIndexOf("."));
////                        Log.d("trimmingstring",n);
//                    }
//                    if(j.equalsIgnoreCase(n)){
//                        matchedWords+=5;
//                        break;
//                    }else if(j.toLowerCase().contains(n.toLowerCase())){
//                        matchedWords+=3;
//                        break;
//                    }
//                }
//
//            }
//            if(matchedWords>0){
////                matchCounter.put(pictureModel,matchedWords);
//                matchedWords-=tobeSearched.length;
//                pictureModel.setSearchResult(matchedWords);
//                foundPictures.add(pictureModel);
////                Log.d("searchTest",String.valueOf(matchCounter.get(pictureModel)));
//            }
//
//
//
//        }
//
//
////        foundPictures.size();
//
//        Collections.sort(foundPictures);
////        for(PictureModel pictureModel:foundPictures){
////            Log.d("searchTest",pictureModel.getName()+String.valueOf(pictureModel.getSearchResult()));
////        }
//
//
//
//
//
//
//
//
//
//        if (foundPictures.size() > 0) {
//            searchFragment.queryChanged(foundPictures);
//            searchFragment.imageViewText.setVisibility(View.GONE);
//            searchFragment.indicatorrecyclerView.setVisibility(View.VISIBLE);
//            searchFragment.viewPager.setVisibility(View.VISIBLE);
//        } else {
//            searchFragment.imageViewText.setVisibility(View.VISIBLE);
//            searchFragment.indicatorrecyclerView.setVisibility(View.GONE);
//            searchFragment.viewPager.setVisibility(View.GONE);
//        }

    }

//    @Override
//    protected void attachBaseContext(Context newBase) {
////        LocaleHelper.setLocale(newBase,"us");
//        super.attachBaseContext(newBase);
//    }


    private void searchAlbums(String newText){

        albumFragment.search(newText);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10 && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    materialSearchView.showSearch();
                    fromVoice=true;
                    materialSearchView.setQuery(searchWrd, false);
                }
            }

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
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
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.navigation_albums:
//                if(active instanceof SearchFragment){
//                albumFragment.refreshAdapter();
//                    fragmentManager.beginTransaction().hide(active).detach(albumFragment).attach(albumFragment).show(albumFragment).commit();
//                    isSearchViewOpen=true;
                    if(materialSearchView.isSearchOpen()){
//                        materialSearchView.closeSearch();
                        if(albumFragment.isAdded()){

                            fragmentManager.beginTransaction().hide(active).show(albumFragment).commit();
                        }else{
                            fragmentManager.beginTransaction().hide(active).add(R.id.container_activity_main,albumFragment,"1")

//                            .detach(albumFragment)
//                            .attach(albumFragment)
                                    .show(albumFragment)
                                    .commit();
                        }
//                        fragmentManager.beginTransaction().replace(R.id.container_activity_main,albumFragment,"1").show(albumFragment);

                        if(query!=null){
                            searchAlbums(query);
//                            query=null;
                        }


                    }else{

//                }else{
                        fragmentManager.beginTransaction().hide(active)
//
////                            .detach(albumFragment)
////                            .attach(albumFragment)
                                .show(albumFragment)
                                .commit();
//                        fragmentManager.beginTransaction().replace(R.id.container_activity_main,albumFragment,"1").show(albumFragment).commit();
                    }


//                }

                active = albumFragment;
                return true;
            case R.id.navigation_pictures:
//                if(isSearchViewOpen){
//
//                    fragmentManager.beginTransaction().hide(active).show(searchFragment).commit();
//                    materialSearchView.showSearch();
//                    active=searchFragment;
//                }else{
                    if(materialSearchView.isSearchOpen()){
//                        materialSearchView.closeSearch();
                        if(searchFragment.isAdded()){
                            fragmentManager.beginTransaction().hide(active).show(searchFragment).commit();
                        }else{
                            fragmentManager.beginTransaction()
                                    .hide(active)
//                                    .add(R.id.container_activity_main,searchFragment,"3")
                                    .show(searchFragment)
                                    .commit();
                        }

//                        fragmentManager.beginTransaction().replace(R.id.container_activity_main,sear"3").show()
                        if(query!=null){
//                            materialSearchView.setQuery(query,false);
                            searchPictures(query);
//                            query=null;
                        }
                        active=searchFragment;

                    }else{
                        fragmentManager.beginTransaction().hide(active)
////                            .detach(pictureFragment)
////                            .attach(pictureFragment)
                                .show(pictureFragment)
                                .commit();
//                        fragmentManager.beginTransaction()
//                                .replace(R.id.container_activity_main,pictureFragment,"2").show(pictureFragment).commit();
                        active = pictureFragment;
                    }

//                }


                return true;
//            case R.id.action_voice_btn:
//                getSpeech();
//                return true;

        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if(materialSearchView.isSearchOpen()){

            if(SearchFragmentTest.ISVISIBLE || active instanceof AlbumFragment){
            materialSearchView.closeSearch();
                return;
            }

            if(!SearchFragmentTest.ISVISIBLE){
                SearchFragmentTest.ISVISIBLE=true;
            }


//            super.onBackPressed();

        }
        if(!(pictureFragment instanceof IonBackPressed)|| !((IonBackPressed)pictureFragment).onBackPressed()){
            super.onBackPressed();
        }
//


    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_voice_search:
                getSpeech();
//                menu.add(R.id.action_create_album).setVisible(true);

                return true;
            case R.id.action_create_album:
                return false;
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
                        albumFragment.sort();
                        pictureFragment.sort();
//                        pictureFragment.refreshAdapter();
                        break;
                    case "DESCENDING":
                        item.setIcon(R.drawable.ic_keyboard_arrow_down_black_24dp);
                        albumFragment.sort();
//                        pictureFragment.refreshAdapter();
                        pictureFragment.sort();
                        break;
                }

//                alertShow();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }






    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;

        getMenuInflater().inflate(R.menu.menu_item_search, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        materialSearchView.setMenuItem(menuItem);
        return true;

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
}
