package com.yendu.Dolab.AsyncTasks;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.io.InputStream;

public class SetWallpaperAsyncTask extends AsyncTask<Void,Void,Boolean> {
    Context context;
    String path;
    public SetWallpaperAsyncTask(Context mContext,String mPath){
        context=mContext;
        path=mPath;
    }
    @Override
    protected Boolean doInBackground(Void... voids) {
//        setWallpaper();

        return setWallpaper();
    }

    @Override
    protected void onPostExecute(Boolean aVoid) {
        super.onPostExecute(aVoid);
        if(aVoid){
            Toast.makeText(context,"Setting succeed",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context,"Error Setting Wallpaper",Toast.LENGTH_SHORT).show();
        }

    }
    public Boolean setWallpaper(){
        WallpaperManager wallpaperManager=WallpaperManager.getInstance(context);
        try{
//            Bitmap bitmap= BitmapFactory.decodeFile(path);
//            Bitmap bitmap= Picasso.get().load("file://"+path).get();
//            Display display=context.getApplicationContext().dis;
//            Bitmap bitmap1= Glide.with(context).asBitmap().load(path).into(200,200).get();
//            wallpaperManager.clear();
//            wallpaperManager.setBitmap(bitmap1);
            DisplayMetrics displayMetrics=new DisplayMetrics();
            ((AppCompatActivity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height=displayMetrics.heightPixels;
            int width=displayMetrics.widthPixels;
            wallpaperManager.setWallpaperOffsetSteps(1,1);
            wallpaperManager.suggestDesiredDimensions(width,height);

            InputStream inputStream=context.getContentResolver().openInputStream(Uri.parse("file://"+path));
//            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
                wallpaperManager.setStream(inputStream);
//            }
//            Toast.makeText(context,"Setting Succeed",Toast.LENGTH_SHORT).show();
            return true;
//
        }catch (Exception ex){
            ex.printStackTrace();
//            Toast.makeText(context,"Error Setting Wallpaper",Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
