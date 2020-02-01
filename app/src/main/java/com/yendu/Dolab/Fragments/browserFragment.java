package com.yendu.Dolab.Fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.net.Uri;
import android.os.Bundle;


import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.bumptech.glide.Glide;

import com.bumptech.glide.request.RequestOptions;


import com.github.chrisbanes.photoview.OnScaleChangedListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.yendu.Dolab.BuildConfig;
import com.yendu.Dolab.Utils.ContentLoaderUtils;
import com.yendu.Dolab.Models.PictureModel;
import com.yendu.Dolab.R;
import com.yendu.Dolab.AsyncTasks.SetWallpaperAsyncTask;
import com.yendu.Dolab.hackedViewPager;


import java.io.File;
import java.util.List;

import static androidx.core.view.ViewCompat.setTransitionName;
public class browserFragment extends Fragment implements View.OnClickListener {

    private List<PictureModel>pictureModels;
    RelativeLayout relativeLayout;
    Context context;
//    ImageView imageView;
    PhotoView imageView;
//    TouchImageView imageView;
//    SubsamplingScaleImageView imageView;
//    ZoomageView imageView;
    private PicturesPagerAdapter picturesPagerAdapter;
    int currentPosition;
    private int previousSelected;
   private PhotoViewAttacher photoViewAttacher;
//   private ViewPager viewPager;
    private hackedViewPager viewPager;
   private boolean checkBottomAppBar=true;
   private  BottomNavigationView bottomNavigationView;
//    Menu activityMenu;
//    MenuInflater menuInflater;
//    private BottomAppBar bottomAppBar;
    private LinearLayout bottomAppBar;
    private View view;
    private boolean checkActionBar=false;
    private RelativeLayout relativeLayoutPicBrowPager;
    private ImageView shareButton,wallpaperButton,deleteButton,editButton,infoButton;
    private boolean isZoomed;
//    private FloatingActionButton floatingActionButton;

    public browserFragment(){

    }

    public browserFragment(List<PictureModel> allPictures, Context mContext,int imagePosition){
        this.pictureModels=allPictures;
        this.context=mContext;
        this.currentPosition=imagePosition;
    }

    public static browserFragment newInstance(List<PictureModel>allImages,Context context, int imagePosition){
        browserFragment broFragment=new browserFragment(allImages,context,imagePosition);

        return broFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        setHasOptionsMenu(true);
//        ((AppCompatActivity)getActivity()).getSupportActionBar().setShowHideAnimationEnabled(true);

//        new Handler().postDelayed(visibility,4000);
        return inflater.inflate(R.layout.picture_browser,container,false);



    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager=view.findViewById(R.id.imagePager);
        shareButton=view.findViewById(R.id.share_image_button);
        wallpaperButton=view.findViewById(R.id.wallpaper_image_button);
        editButton=view.findViewById(R.id.edit_image_button);
        infoButton=view.findViewById(R.id.info_image_button);
        deleteButton=view.findViewById(R.id.delete_image_button);
        editButton.setOnClickListener(this);
        shareButton.setOnClickListener(this);
        deleteButton.setOnClickListener(this);
        infoButton.setOnClickListener(this);
        wallpaperButton.setOnClickListener(this);
        relativeLayout=getActivity().findViewById(R.id.main_activity_relativeLayout);
        bottomNavigationView=getActivity().findViewById(R.id.bottom_navigation);
        picturesPagerAdapter=new PicturesPagerAdapter();
        viewPager.setAdapter(picturesPagerAdapter);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setCurrentItem(currentPosition);
        pictureModels.get(currentPosition).setSelected(true);
        previousSelected=currentPosition;


//        floatingActionButton=view.findViewById(R.id.bottom_app_bar_floating);
//        floatingActionButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent=new Intent(Intent.ACTION_EDIT);
//
//                Uri uri= FileProvider.getUriForFile(getContext(),BuildConfig.APPLICATION_ID,new File(pictureModels.get(position).getPath()));
////                intent.setDataAndType(uri,"image/*");
//                intent.setDataAndType(uri,"image/*");
//                Log.d("dataandtype",String.valueOf(new File(pictureModels.get(position).getPath()).exists()));
////                intent.putExt,uri);
//
//
////                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
////                intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//
////                intent.
//                startActivity(Intent.createChooser(intent,null));
//            }
//        });

        bottomAppBar=view.findViewById(R.id.bottom_app_bar);
//        bottomAppBar.replaceMenu(R.menu.custom_menu);
//        bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                switch (item.getItemId()){
//                    case R.id.delete_custom_menu:
//                        return true;
//                    case R.id.details_custom_menu:
//                        return true;
//                    case R.id.edit_custom_menu:
//                        return true;
//                    case R.id.share_custom_menu:
//                        share();
//                        return true;
//                    case R.id.set_as_wallpaper_custom_menu:
//                        setWallpaper();
//                        return true;
//                }
//                return false;
//            }
//        });

//        if(bottomNavigationView!=null){
//            bottomNavigationView.setVisibility(View.GONE);
//        }


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
//                photoViewAttacher.setScale(3.0f);
            }

            @Override
            public void onPageSelected(int position) {
                if(previousSelected!=-1){
                    if(previousSelected<pictureModels.size()){
                        pictureModels.get(previousSelected).setSelected(false);
                    }

                    previousSelected=position;
                    currentPosition=position;
                    pictureModels.get(position).setSelected(true);

                }else{
                    previousSelected=position;
                    currentPosition=position;
                    pictureModels.get(position).setSelected(true);
                }

//                photoViewAttacher.setScale(1f);
//                picturesPagerAdapter.notifyDataSetChanged();
//                imageView.resetZoom();
//                imageView.setDisplayMatrix
//                imageView.setSuppMatrix(new Matrix());
//                   imageView.setScale(1.0f);
                Log.d("getScale",String.valueOf(imageView.getScale()));
                if(isZoomed){
//
//                            imageView.setScale(1f);
                    isZoomed=false;
                    picturesPagerAdapter.notifyDataSetChanged();

                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });




    }

    public void share(){
        Intent intent=new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        Uri uri=FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID,new File(pictureModels.get(currentPosition).getPath()));
        intent.putExtra(Intent.EXTRA_STREAM,uri);
        startActivity(Intent.createChooser(intent,"Select"));

//        intent.setType("image/")
    }
    public void setWallpaper(){
        WallpaperManager wallpaperManager=WallpaperManager.getInstance(getContext());
        try{
            Bitmap bitmap= BitmapFactory.decodeFile(pictureModels.get(currentPosition).getPath());
//            Bitmap bitmap= Picasso.get().load(pictureModels.get(position).getPath()).get();
            wallpaperManager.setBitmap(bitmap);
            Toast.makeText(getContext(),"Setting Succeed",Toast.LENGTH_SHORT).show();

//
        }catch (Exception ex){
            ex.printStackTrace();
            Toast.makeText(getContext(),"Error Setting Wallpaper",Toast.LENGTH_SHORT).show();

        }
    }
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.share_image_button:
                share();
                break;
            case R.id.edit_image_button:

                break;
            case R.id.wallpaper_image_button:
//                setWallpaper();
                new SetWallpaperAsyncTask(getContext(),pictureModels.get(currentPosition).getPath()).execute();
                break;
            case R.id.delete_image_button:
                AlertDialog.Builder alertBuilder=new AlertDialog.Builder(getContext());
                alertBuilder.setMessage("Delete selected item");


                alertBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                            File tobeDelete=new File(pictureModels.get(currentPosition).getPath());

                            ContentLoaderUtils.deleteFile(tobeDelete,getContext());
                            if(currentPosition==pictureModels.size()-1){
                                pictureModels.remove(currentPosition);
                                if(pictureModels.size()>0){
                                    currentPosition=pictureModels.size()-1;
                                }else{
                                    getActivity().finish();
                                }

                            }else{
                                pictureModels.remove(currentPosition);


                            }
//                        picturesPagerAdapter.destroyItem(viewPager,currentPosition,view);

                        picturesPagerAdapter.notifyDataSetChanged();

                        viewPager.setCurrentItem(currentPosition,true);

//                        pictureModels.get(currentPosition).setSelected(true);
//                        previousSelected=currentPosition;


//                            viewPager.setCurrentItem(++currentPosition,true);






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
        PictureModel pictureModel=pictureModels.get(currentPosition);
        ((TextView) dialog.findViewById(R.id.title_info_dialog_real)).setText(pictureModel.getName());
        ((TextView)dialog.findViewById(R.id.time_info_dialog_real)).setText(pictureModel.getDate());
        ((TextView)dialog.findViewById(R.id.width_info_dialog_real)).setText(pictureModel.getWidth());
        ((TextView)dialog.findViewById(R.id.height_info_dialog_real)).setText(pictureModel.getHeight());
        ((TextView)dialog.findViewById(R.id.size_info_dialog_real)).setText(pictureModel.getSize());
        ((TextView)dialog.findViewById(R.id.path_info_dialog_real)).setText(pictureModel.getPath());

        dialog.show();
    }



    private class PicturesPagerAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return pictureModels.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view==((View)object);
        }

        @Override
        public void notifyDataSetChanged() {


            super.notifyDataSetChanged();
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            LayoutInflater layoutInflater=(LayoutInflater)container.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view=layoutInflater.inflate(R.layout.picture_browser_pager,null);
            imageView=view.findViewById(R.id.image_browser_pager);
            photoViewAttacher=new PhotoViewAttacher(imageView);
            relativeLayoutPicBrowPager=view.findViewById(R.id.container_picture_browser_pager);
//            photoViewAttacher=new PhotoViewAttacher(imageView);
            relativeLayoutPicBrowPager.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    checkBottomAppBar=true;
                    if(checkBottomAppBar){
                        makeBottomAppBarVisible();
                        checkBottomAppBar=false;
                    }else{
                        makeBottomAppBarGone();
                        checkBottomAppBar=true;
                    }
//                    makeBottomAppBarVisible();
//                    new Handler().postDelayed(visibility,4000);
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
            PictureModel pictureModel=pictureModels.get(position);
//            SimpleTarget simpleTarget=new SimpleTarget<BitmapDrawable>() {
//                @Override
//                public void onResourceReady(@NonNull BitmapDrawable resource, @Nullable Transition<? super BitmapDrawable> transition) {
//                    imageView.setImageDrawable(resource);
//                }
//
//
//'
//            };imageView.rende
//            imageView.setZoom(1.5f);

            Glide.with(context)
//                    .asBitmap()
                    .load(pictureModel.getPath())
//                    .apply(new RequestOptions().override(1600,1600))
//                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
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
            if(pictureModels.contains(object)){
                return pictureModels.indexOf(object);
            }else{
                return POSITION_NONE;
            }
        }

    }

//    @Override
//    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
////        menu.clear();
//        activityMenu=menu;
//        menuInflater=inflater;
//        menu.clear();
//        inflater.inflate(R.menu.custom_menu,menu);
////        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
//
//        super.onCreateOptionsMenu(menu, inflater);
//    }

    public Runnable visibility=new Runnable() {
        @Override
        public void run() {
//
//            if(checkBottomAppBar){
//
//
////                bottomAppBar.setVisibility(View.VISIBLE);
//
////                    makeBottomAppBarVisible();
//
//
////                if(bottomNavigationView!=null){
////
//////                }
////                if(relativeLayout!=null){
//////                    relativeLayout.animate()
//////                            .translationY(relativeLayout.getHeight())
//////                            .alpha(0.0f)
//////                            .setDuration(3000)
//////                            .setListener(new AnimatorListenerAdapter() {
//////                                @Override
//////                                public void onAnimationEnd(Animator animation) {
//////                                    super.onAnimationEnd(animation);
//////                                    relativeLayout.setVisibility(View.GONE);
//////                                }
//////                            });
////                    relativeLayout.animate().translationY(-relativeLayout.getHeight()).setDuration(300);
////                    ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
////                    relativeLayout.setVisibility(View.GONE);
////                }
////                checkActionBar=true;
//
//            }else{
////                ((AppCompatActivity)getActivity()).getSupportActionBar().show();
////                relativeLayout.setVisibility(View.VISIBLE);
                 makeBottomAppBarGone();
//                bottomAppBar.setVisibility(View.GONE);
//            }

        }
    };
    private void makeBottomAppBarGone(){
        bottomAppBar.animate().translationY(0).setDuration(300).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                bottomAppBar.setVisibility(View.GONE);

            }
        });
    }
    private void makeBottomAppBarVisible(){
        bottomAppBar.animate().translationY(0).setDuration(300).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                bottomAppBar.setVisibility(View.VISIBLE);
                checkBottomAppBar=false;
            }
        });

    }

}
