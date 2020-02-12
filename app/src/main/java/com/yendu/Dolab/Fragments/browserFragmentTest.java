package com.yendu.Dolab.Fragments;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
//import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.chrisbanes.photoview.OnScaleChangedListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.yendu.Dolab.Activites.PicturesView;
import com.yendu.Dolab.BuildConfig;
import com.yendu.Dolab.Utils.ContentLoaderUtils;
//import com.yendu.Dolab.Models.PictureModel;
import com.yendu.Dolab.R;
import com.yendu.Dolab.AsyncTasks.SetWallpaperAsyncTask;
import com.yendu.Dolab.hackedViewPager;
import java.io.File;
//import java.util.List;
import static androidx.core.view.ViewCompat.setTransitionName;
import static com.yendu.Dolab.Activites.PicturesView.bucketName;
import static com.yendu.Dolab.Fragments.PictureFragment.isBuildAboveJellyBean;
import static com.yendu.Dolab.Utils.ContentLoaderUtils.DATEFORMAT;
import static com.yendu.Dolab.Utils.ContentLoaderUtils.calculateSize;
import static com.yendu.Dolab.Utils.ContentLoaderUtils.convertDate;

public class browserFragmentTest extends Fragment implements /* View.OnClickListener ,*/ LoaderManager.LoaderCallbacks<Cursor> {


    private RelativeLayout relativeLayout;
    private Context context;
    public Cursor cursor;
    //    ImageView imageView;
    private PhotoView imageView;
    private PicturesPagerAdapter picturesPagerAdapter;
    private int currentPosition;
    private int previousSelected;
    private PhotoViewAttacher photoViewAttacher;
    private CoordinatorLayout frameLayout;

    private hackedViewPager viewPager;
    private boolean checkBottomAppBar=true;
    private  BottomNavigationView bottomNavigationView;
   // private LinearLayout bottomAppBar;
    private View view;
    private boolean checkActionBar=false;
    private RelativeLayout relativeLayoutPicBrowPager;
    private boolean isZoomed;
//    public PictureModel pictureModel1;
    private boolean firstTimeLoaded=false;
    private String query=null;
//    private FloatingActionButton floatingActionButton;
//    private Loader<Cursor>loader;
    public browserFragmentTest(){

    }

    public browserFragmentTest(Cursor cursor, Context mContext,int imagePosition){
        this.cursor=cursor;
        this.context=mContext;
        this.currentPosition=imagePosition;


    }
    public browserFragmentTest(Cursor cursor,Context context,int imagePosition,String searchQuery){
        this.cursor=cursor;
        this.context=context;
        this.currentPosition=imagePosition;
        this.query=searchQuery;
    }

    public static browserFragmentTest newInstance(Cursor cursor,Context context, int imagePosition){
        return new browserFragmentTest(cursor,context,imagePosition);
//        Toast.makeText(context,context.getClass().getName(),Toast.LENGTH_SHORT).show();

    }
    public static browserFragmentTest newInstance(Cursor cursor,Context context,int imagePosition,String searchQuery) {
        return new browserFragmentTest(cursor, context, imagePosition, searchQuery);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.picture_browser,container,false);



    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(firstTimeLoaded==false){

            LoaderManager.getInstance(this).initLoader(101,null,this);
            firstTimeLoaded=true;
        }else{

            LoaderManager.getInstance(this).restartLoader(101,null,this);
        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager=view.findViewById(R.id.imagePager);
//        shareButton=view.findViewById(R.id.share_image_button);
//        wallpaperButton=view.findViewById(R.id.wallpaper_image_button);
//        editButton=view.findViewById(R.id.edit_image_button);
//        infoButton=view.findViewById(R.id.info_image_button);
//        deleteButton=view.findViewById(R.id.delete_image_button);
//        editButton.setOnClickListener(this);
//        shareButton.setOnClickListener(this);
//        deleteButton.setOnClickListener(this);
//        infoButton.setOnClickListener(this);
//        wallpaperButton.setOnClickListener(this);
        relativeLayout=getActivity().findViewById(R.id.main_activity_relativeLayout);
        bottomNavigationView=getActivity().findViewById(R.id.bottom_navigation);
        cursor.moveToPosition(currentPosition);
        picturesPagerAdapter=new PicturesPagerAdapter();
        viewPager.setAdapter(picturesPagerAdapter);
       // frameLayout=view.findViewById(R.id.picture_browser_container);

        viewPager.setOffscreenPageLimit(1);
        viewPager.setCurrentItem(currentPosition);
        previousSelected=currentPosition;

        //bottomAppBar=view.findViewById(R.id.bottom_app_bar);

        if(((AppCompatActivity)getActivity()).getSupportActionBar().isShowing()){
            ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
            checkActionBar=true;
            if(bottomNavigationView!=null){
                bottomNavigationView.setVisibility(View.GONE);
            }
            if(relativeLayout!=null){
                relativeLayout.setVisibility(View.GONE);
            }
//                checkActionBar=true;

        }


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(previousSelected!=-1){
                    previousSelected=position;
                    currentPosition=position;


                }else{
                    previousSelected=position;
                    currentPosition=position;

                }

                if(isZoomed){

                    isZoomed=false;
                    picturesPagerAdapter.notifyDataSetChanged();

                }
//                Toast.makeText(getContext(),"here it is ",Toast.LENGTH_LONG).show();
//                makeBottomAppBarVisible();
             //   makeBottomAppBarGone();


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    public void share(){
        Intent intent=new Intent(Intent.ACTION_SEND);
       // intent.setType("image/*");
        cursor.moveToPosition(currentPosition);
        int media_type;
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.JELLY_BEAN){
            media_type=cursor.getInt(7);
        }else{
            media_type=cursor.getInt(5);
        }
        if(media_type==3){
            intent.setType("video/*");
        }else{
            intent.setType("image/*");
        }
        Uri uri=FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID,new File(cursor.getString(0)));
        intent.putExtra(Intent.EXTRA_STREAM,uri);
        startActivity(Intent.createChooser(intent,"Select"));

    }
//    public void setWallpaper(){
//        WallpaperManager wallpaperManager=WallpaperManager.getInstance(getContext());
//        try{
//            Bitmap bitmap= BitmapFactory.decodeFile(pictureModels.get(currentPosition).getPath());
//
//            wallpaperManager.setBitmap(bitmap);
//            Toast.makeText(getContext(),"Setting Succeed",Toast.LENGTH_SHORT).show();
//
////
//        }catch (Exception ex){
//            ex.printStackTrace();
//            Toast.makeText(getContext(),"Error Setting Wallpaper",Toast.LENGTH_SHORT).show();
//
//        }
//    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        try{
//            if(checkActionBar){
//                ((AppCompatActivity)getActivity()).getSupportActionBar().show();
//                bottomNavigationView.setVisibility(View.VISIBLE);
//                relativeLayout.setVisibility(View.VISIBLE);
//
//            }
//            menuInflater.inflate(R.menu.menu_item_search,activityMenu);
            if(checkActionBar){

                ((AppCompatActivity)getActivity()).getSupportActionBar().show();
                bottomNavigationView.setVisibility(View.VISIBLE);
                relativeLayout.setVisibility(View.VISIBLE);
                checkActionBar=false;
            }



        }catch (NullPointerException ex){
            ex.printStackTrace();
        }


    }

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.share_image_button:
//                share();
//                break;
//            case R.id.edit_image_button:
//                editImage();
//                break;
//            case R.id.wallpaper_image_button:
//                cursor.moveToPosition(currentPosition);
//                new SetWallpaperAsyncTask(getContext(),cursor.getString(0)).execute();
//                break;
//            case R.id.delete_image_button:
//                AlertDialog.Builder alertBuilder=new AlertDialog.Builder(getContext());
//                alertBuilder.setMessage("Delete selected item");
//
//
//                alertBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//
////                                File tobeDelete=new File(pictureModels.get(currentPosition).getPath());
//                                cursor.moveToPosition(currentPosition);
//                                File tobeDelete=new File(cursor.getString(0));
////                                Log.d("SizeOfCursor",String.valueOf(cursor.getCount()));
//                                ContentLoaderUtils.deleteFile(tobeDelete,getContext());
////                                Log.d("SizeOfCursor",String.valueOf(cursor.getCount()));
//
//                                int media_type;
//                                if(isBuildAboveJellyBean()){
//                                    media_type=cursor.getInt(7);
//                                }else{
//                                    media_type=cursor.getInt(5);
//                                }
//
//
//                                if(media_type==1){
//                                    ContentLoaderUtils.deleteFile(tobeDelete, getContext());
//
//                                }else{
//                                    ContentLoaderUtils.deleteVideo(tobeDelete,getContext());
//                                }
//                                if(cursor.getPosition()==cursor.getCount()-1){
//                                    if(cursor.getCount()>0){
//                                        cursor.moveToPosition(cursor.getCount()-1);
//
//                                        currentPosition=cursor.getPosition();
//                                    }else{
////                                        getActivity().finish();
////                                         onDestroy();
////                                        getActivity().onbackpressed();
//
//                                    }
//
//                                }else{
//                                    cursor.moveToPosition(currentPosition);
//                                    Log.d("SizeOfCursor",String.valueOf(cursor.getPosition()));
//                                }
//
//                                picturesPagerAdapter.notifyDataSetChanged();
//
//                                viewPager.setCurrentItem(currentPosition,true);
//
//
//
//                            }
//
//                        }
//                );
//                alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//                alertBuilder.create().show();
//
//
//
//                break;
//            case R.id.info_image_button:
//                showInfo();
//
//                break;
//        }
//    }
    private void editImage(){

        cursor.moveToPosition(currentPosition);
        Uri uri=FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID,new File(cursor.getString(0)));
        int flags=Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION;
        Intent editIntent = new Intent(Intent.ACTION_EDIT);
        editIntent.setDataAndType(uri, "image/*");
        editIntent.addFlags(flags);
//        intent.putExtra(Intent.EXTRA_STREAM,uri);
//        startActivity(Intent.createChooser(intent,"Select"));
        editIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivity(Intent.createChooser(editIntent,"Edit picture"));

    }

    private void showInfo(){
        final Dialog dialog=new Dialog(getContext());
        dialog.setTitle("Details");
        dialog.setContentView(R.layout.info_dialog);
        dialog.setCancelable(true);
        dialog.findViewById(R.id.imageView_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
//        PictureModel pictureModel=pictureModels.get(currentPosition);


        cursor.moveToPosition(currentPosition);
        ((TextView) dialog.findViewById(R.id.title_info_dialog_real)).setText(cursor.getString(1));
        ((TextView)dialog.findViewById(R.id.time_info_dialog_real)).setText(convertDate(cursor.getString(3),DATEFORMAT));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            ((TextView)dialog.findViewById(R.id.width_info_dialog_real)).setText(cursor.getString(5));
            ((TextView)dialog.findViewById(R.id.height_info_dialog_real)).setText(cursor.getString(6));
        }

        ((TextView)dialog.findViewById(R.id.size_info_dialog_real)).setText(calculateSize(cursor.getString(4)));
        ((TextView)dialog.findViewById(R.id.path_info_dialog_real)).setText(cursor.getString(0));

        dialog.show();
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
       // Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Uri uri=MediaStore.Files.getContentUri("external");
        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                + " OR "
                + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
        String select="("+selection+") AND ";

        if(PicturesView.class.getName()==context.getClass().getName()){
           if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN){
             //  String[] projection = {MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DISPLAY_NAME, MediaStore.Images.Media.BUCKET_DISPLAY_NAME,MediaStore.Images.ImageColumns.DATE_TAKEN,MediaStore.Images.ImageColumns.SIZE,MediaStore.Images.ImageColumns.WIDTH,MediaStore.Images.ImageColumns.HEIGHT};
               String[] projection={MediaStore.Files.FileColumns.DATA,MediaStore.Files.FileColumns.TITLE,MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME,MediaStore.Files.FileColumns.DATE_TAKEN,MediaStore.Files.FileColumns.SIZE,MediaStore.Files.FileColumns.WIDTH,MediaStore.Files.FileColumns.HEIGHT,MediaStore.Files.FileColumns.MEDIA_TYPE};
               return new CursorLoader(context,uri, projection, select+MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME+" = ?", new String[]{bucketName},MediaStore.Files.FileColumns.DATE_TAKEN+" DESC");

//               return new CursorLoader(context,uri, projection, MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME+" = ?", new String[]{bucketName},MediaStore.Images.ImageColumns.DATE_TAKEN+" DESC");
           }else{
              // String[] projection={MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DISPLAY_NAME, MediaStore.Images.Media.BUCKET_DISPLAY_NAME,MediaStore.Images.ImageColumns.DATE_TAKEN,MediaStore.Images.ImageColumns.SIZE,};
               String[] projection={MediaStore.Files.FileColumns.DATA,MediaStore.Files.FileColumns.TITLE,MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME,MediaStore.Files.FileColumns.DATE_TAKEN,MediaStore.Files.FileColumns.SIZE,MediaStore.Files.FileColumns.MEDIA_TYPE};
               return new CursorLoader(context,uri, projection, selection+MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME+" = ?", new String[]{bucketName},MediaStore.Files.FileColumns.DATE_TAKEN+" DESC");

//               return new CursorLoader(context,uri, projection, MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME+" = ?",new String[]{bucketName}, MediaStore.Images.ImageColumns.DATE_TAKEN+" DESC");
           }
       }
       if(query!=null){
           if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN){
              // Uri images=MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
               Uri images=MediaStore.Files.getContentUri("external");
              // String[] projection = {MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DISPLAY_NAME, MediaStore.Images.Media.BUCKET_DISPLAY_NAME,MediaStore.Images.ImageColumns.DATE_TAKEN,MediaStore.Images.ImageColumns.SIZE,MediaStore.Images.ImageColumns.WIDTH,MediaStore.Images.ImageColumns.HEIGHT};
               String[] projection = {MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.TITLE, MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME,MediaStore.Files.FileColumns.DATE_TAKEN,MediaStore.Files.FileColumns.SIZE,MediaStore.Files.FileColumns.WIDTH,MediaStore.Files.FileColumns.HEIGHT,MediaStore.Files.FileColumns.MEDIA_TYPE};

               String[] splited = query.split("\\s+");
               StringBuilder selectionn = new StringBuilder();
               String order;
//                selection.append(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME);
               String[] argss = new String[splited.length];
               if (splited.length > 1) {
                   for (int i = 0; i < splited.length; i++) {
                       argss[i] = "%" + splited[i] + "%";
//                    selection.append();
                       if (i + 1 == splited.length) {

                           selectionn.append(MediaStore.Files.FileColumns.TITLE + " like ?");
                           break;
                       }
                       selectionn.append(MediaStore.Files.FileColumns.TITLE + " like ? or ");

                   }

               } else {
                   argss[0] = "%" + splited[0] + "%";
                   selectionn.append(MediaStore.Files.FileColumns.TITLE + " like ?");
               }
//


               order = "CASE WHEN _display_name ='" + query + "' THEN 0 WHEN _display_name LIKE '" + query + "%" + "' THEN 1 WHEN _display_name LIKE '" + "%" + query + "%" + "' THEN 2 WHEN _display_name LIKE '" + "%" + query + "' THEN 3 ELSE 4 END, _display_name DESC";
               return new CursorLoader(getContext(), images, projection, select+"("+selectionn.toString()+")", argss, order);
//               return new CursorLoader(context,uri, projection, MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME+" = ?", new String[]{bucketName},MediaStore.Images.ImageColumns.DATE_TAKEN+" DESC");
           }else{
              // String[] projection={MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DISPLAY_NAME, MediaStore.Images.Media.BUCKET_DISPLAY_NAME,MediaStore.Images.ImageColumns.DATE_TAKEN,MediaStore.Images.ImageColumns.SIZE,};
               String[] projection = {MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.TITLE, MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME,MediaStore.Files.FileColumns.DATE_TAKEN,MediaStore.Files.FileColumns.SIZE,MediaStore.Files.FileColumns.MEDIA_TYPE};
                Uri images=MediaStore.Files.getContentUri("external");
             //  Uri images=MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//               String[] projection = {MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DISPLAY_NAME, MediaStore.Images.Media.BUCKET_DISPLAY_NAME,MediaStore.Images.ImageColumns.DATE_TAKEN,MediaStore.Images.ImageColumns.SIZE,MediaStore.Images.ImageColumns.WIDTH,MediaStore.Images.ImageColumns.HEIGHT};
               String[] splited = query.split("\\s+");
               StringBuilder selectionn = new StringBuilder();
               String order;
//                selection.append(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME);
               String[] argss = new String[splited.length];
               if (splited.length > 1) {
                   for (int i = 0; i < splited.length; i++) {
                       argss[i] = "%" + splited[i] + "%";
//                    selection.append();
                       if (i + 1 == splited.length) {

                           selectionn.append(MediaStore.Files.FileColumns.TITLE+ " like ?");
                           break;
                       }
                       selectionn.append(MediaStore.Files.FileColumns.TITLE + " like ? or ");

                   }

               } else {
                   argss[0] = "%" + splited[0] + "%";
                   selectionn.append(MediaStore.Files.FileColumns.TITLE + " like ?");
               }
//

               order = "CASE WHEN _display_name ='" + query + "' THEN 0 WHEN _display_name LIKE '" + query + "%" + "' THEN 1 WHEN _display_name LIKE '" + "%" + query + "%" + "' THEN 2 WHEN _display_name LIKE '" + "%" + query + "' THEN 3 ELSE 4 END, _display_name DESC";
               return new CursorLoader(getContext(), images, projection, select+"("+selectionn.toString()+")", argss, order);
//               return new CursorLoader(context,uri, projection, MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME+" = ?",new String[]{bucketName}, MediaStore.Images.ImageColumns.DATE_TAKEN+" DESC");
           }
       }



        String order=PictureFragment.Sorted ?"ASC":"DESC";

       /* if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN){
            String[] projection = {MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DISPLAY_NAME, MediaStore.Images.Media.BUCKET_DISPLAY_NAME,MediaStore.Images.ImageColumns.DATE_TAKEN,MediaStore.Images.ImageColumns.SIZE,MediaStore.Images.ImageColumns.WIDTH,MediaStore.Images.ImageColumns.HEIGHT};
            return new CursorLoader(getContext(),uri, projection, null, null, MediaStore.Images.ImageColumns.DATE_TAKEN+" "+order);
        }else{
            String[] projection={MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DISPLAY_NAME, MediaStore.Images.Media.BUCKET_DISPLAY_NAME,MediaStore.Images.ImageColumns.DATE_TAKEN,MediaStore.Images.ImageColumns.SIZE,};
            return new CursorLoader(getContext(),uri, projection, null, null, MediaStore.Images.ImageColumns.DATE_TAKEN+" "+order);
        }*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            // String[] projection = {MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DISPLAY_NAME, MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.ImageColumns.DATE_TAKEN, MediaStore.Images.ImageColumns.SIZE, MediaStore.Images.ImageColumns.WIDTH, MediaStore.Images.ImageColumns.HEIGHT};
            String[] projection={MediaStore.Files.FileColumns.DATA,MediaStore.Files.FileColumns.TITLE,MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME,MediaStore.Files.FileColumns.DATE_TAKEN,MediaStore.Files.FileColumns.SIZE,MediaStore.Files.FileColumns.WIDTH,MediaStore.Files.FileColumns.HEIGHT,MediaStore.Files.FileColumns.MEDIA_TYPE};
            // return new CursorLoader(getContext(), uri, projection, null, null, MediaStore.Images.ImageColumns.DATE_TAKEN + " " + order);
            return new CursorLoader(getContext(), uri, projection, selection, null, MediaStore.Files.FileColumns.DATE_TAKEN + " " + order);

        } else {
            // String[] projection = {MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DISPLAY_NAME, MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.ImageColumns.DATE_TAKEN, MediaStore.Images.ImageColumns.SIZE,};
            String[] projection={MediaStore.Files.FileColumns.DATA,MediaStore.Files.FileColumns.TITLE,MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME,MediaStore.Files.FileColumns.DATE_TAKEN,MediaStore.Files.FileColumns.SIZE,MediaStore.Files.FileColumns.MEDIA_TYPE};
            return new CursorLoader(getContext(), uri, projection, selection, null, MediaStore.Files.FileColumns.DATE_TAKEN + " " + order);

//                return new CursorLoader(getContext(), uri, projection, null, null, MediaStore.Images.ImageColumns.DATE_TAKEN + " " + order);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        try{
            if(data!=null && data.getCount()>0) {

                this.cursor=data;
//            cursor.moveToPosition(currentPosition);
                picturesPagerAdapter.notifyDataSetChanged();


            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }



    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }


    private class PicturesPagerAdapter extends PagerAdapter implements View.OnClickListener{
        int positionOfCurrentItem;
        private ImageView shareButton,wallpaperButton,deleteButton,editButton,infoButton;
        @Override
        public int getCount() {
            try{
                if(cursor!=null && !cursor.isClosed()){
                    return cursor.getCount();
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }

            return 0;

        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view==((View)object);
        }

        @Override
        public void notifyDataSetChanged() {


            super.notifyDataSetChanged();
        }

        private void makeBottomAppBarGone(final LinearLayout bottomAppBar){
            bottomAppBar.animate().alpha(0).setDuration(300).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    bottomAppBar.setVisibility(View.GONE);

                }
            });

        }
        private void makeBottomAppBarVisible(final LinearLayout bottomAppBar){
            bottomAppBar.animate().alpha(1).setDuration(300).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    bottomAppBar.setVisibility(View.VISIBLE);
                    //checkBottomAppBar=false;
                }
            });


        }
        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            final LinearLayout bottomAppBar;
            ImageButton imageButton;

            LayoutInflater layoutInflater=(LayoutInflater)container.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view=layoutInflater.inflate(R.layout.picture_browser_pager,null);
            imageView=view.findViewById(R.id.image_browser_pager);
            shareButton=view.findViewById(R.id.share_image_button);
            deleteButton=view.findViewById(R.id.delete_image_button);
            wallpaperButton=view.findViewById(R.id.wallpaper_image_button);
            infoButton=view.findViewById(R.id.info_image_button);
            editButton=view.findViewById(R.id.edit_image_button);
            bottomAppBar=view.findViewById(R.id.bottom_app_bar);
            shareButton.setOnClickListener(this);
            deleteButton.setOnClickListener(this);
            infoButton.setOnClickListener(this);
            photoViewAttacher=new PhotoViewAttacher(imageView);
            imageButton=view.findViewById(R.id.play_button_picture_browser_pager);
            imageButton.setOnClickListener(this);
            relativeLayoutPicBrowPager=view.findViewById(R.id.container_picture_browser_pager);
           // bottomAppBar.setVisibility(View.GONE);
//            photoViewAttacher=new PhotoViewAttacher(imageView);
            int media_type;
            cursor.moveToPosition(position);


//            if(query!=null){
//                media_type=cursor.getInt(2);
//            }else{
                if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN){
                    media_type=cursor.getInt(7);
                }else{
                    media_type=cursor.getInt(5);
                }
//            }


            if(media_type==3){
                imageButton.setVisibility(View.VISIBLE);
                editButton.setOnClickListener(null);
                wallpaperButton.setOnClickListener(null);
                wallpaperButton.setImageAlpha(100);
                editButton.setImageAlpha(100);}
//            }else{
//                imageButton.setVisibility(View.GONE);
//                editButton.setOnClickListener(this);
//                wallpaperButton.setOnClickListener(this);
//                wallpaperButton.setImageAlpha(1000);
//                editButton.setImageAlpha(1000);
//            }

            photoViewAttacher.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    checkBottomAppBar=true;
                 ///  Toast.makeText(getContext(),"here it is ",Toast.LENGTH_LONG).show();
                //  View view= viewPager.findViewWithTag(viewPager.getCurrentItem());

                  // bottomAppBar.setVisibility(View.GONE);
                //   view..setVisibility(View.GONE);
                    if(checkBottomAppBar){
                        makeBottomAppBarVisible(bottomAppBar);
                      //  bottomAppBar.setVisibility(View.VISIBLE);
                        checkBottomAppBar=false;
                    }else{
                        makeBottomAppBarGone(bottomAppBar);
                        checkBottomAppBar=true;
                    }
////                    makeBottomAppBarVisible();
////                    new Handler().postDelayed(visibility,4000);
                }
            });

            photoViewAttacher.setOnScaleChangeListener(new OnScaleChangedListener() {
                @Override
                public void onScaleChange(float scaleFactor, float focusX, float focusY) {
//                    if(scaleFactor>2f){
//                        imageView.setScale(1f);
//                    }
                    isZoomed=true;

//                    photoViewAttacher.setSuppMatrix(new Matrix());
                }
            });


            setTransitionName(imageView,String.valueOf(position)+"_image");
//            PictureModel pictureModel=pictureModels.get(position);
//            currentPosition=position;

//

            Glide.with(context)
//                    .asBitmap()
                    .load(cursor.getString(0))
                    .apply(new RequestOptions().fitCenter())
//                    .into(imageView);
                    .into(imageView);
//
//

            ((ViewPager) container).addView(view);
//            Picasso.get().load(pictureModel.getPath()).fit().into(imageView);
            return view;


        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            ((ViewPager)container).removeView((View)object);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {

                return POSITION_NONE;

        }
        private void openVideo(){
           // intent1.setType("video/*");
            cursor.moveToPosition(currentPosition);
            Uri uri=FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID,new File(cursor.getString(0)));
           // intent1.putExtra(Intent.EXTRA_STREAM,uri);
            Intent intent1=new Intent(Intent.ACTION_VIEW,uri);
            intent1.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            intent1.setDataAndType(uri,"video/*");
            startActivity(Intent.createChooser(intent1,"Select"));

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.share_image_button:
                    share();
                    break;
                case R.id.edit_image_button:
                    editImage();
                    break;
                case R.id.play_button_picture_browser_pager:
                    openVideo();
                    break;
                case R.id.wallpaper_image_button:
                    cursor.moveToPosition(currentPosition);
                    new SetWallpaperAsyncTask(getContext(), cursor.getString(0)).execute();
                    break;
                case R.id.delete_image_button:
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
                    alertBuilder.setMessage("Delete selected item");


                    alertBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

//                                File tobeDelete=new File(pictureModels.get(currentPosition).getPath());
                                    cursor.moveToPosition(currentPosition);
                                    File tobeDelete = new File(cursor.getString(0));
//                                Log.d("SizeOfCursor",String.valueOf(cursor.getCount()));
                                    ContentLoaderUtils.deleteFile(tobeDelete, getContext());
//                                Log.d("SizeOfCursor",String.valueOf(cursor.getCount()));

                                    int media_type;
                                    if (isBuildAboveJellyBean()) {
                                        media_type = cursor.getInt(7);
                                    } else {
                                        media_type = cursor.getInt(5);
                                    }


                                    if (media_type == 1) {
                                        ContentLoaderUtils.deleteFile(tobeDelete, getContext());

                                    } else {
                                        ContentLoaderUtils.deleteVideo(tobeDelete, getContext());
                                    }
                                    if (cursor.getPosition() == cursor.getCount() - 1) {
                                        if (cursor.getCount() ==1) {
                                            getActivity().onBackPressed();
//                                            getActivity().onbackpressed();

                                        } else {
//                                        getActivity().finish();
//                                         onDestroy();
                                            cursor.moveToPosition(cursor.getCount() - 1);

                                            currentPosition = cursor.getPosition();

                                        }

                                    } else {
                                        cursor.moveToPosition(currentPosition);
                                        Log.d("SizeOfCursor", String.valueOf(cursor.getPosition()));
                                    }

                                    picturesPagerAdapter.notifyDataSetChanged();

                                    viewPager.setCurrentItem(currentPosition, true);


                                }

                            }
                    );
                    alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertBuilder.create().show();


                    break;
                case R.id.info_image_button:
                    showInfo();

                    break;
            }

        }
    }



}
