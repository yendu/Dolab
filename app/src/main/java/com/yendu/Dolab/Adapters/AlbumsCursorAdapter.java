package com.yendu.Dolab.Adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.yendu.Dolab.Activites.PicturesView;
import com.yendu.Dolab.R;
import com.yendu.Dolab.Utils.ContentLoaderUtils;

public class AlbumsCursorAdapter extends CursorAdapter<AlbumsCursorAdapter.AlbumViewHolder> {
    Context context;
   public Cursor cursor;

    public AlbumsCursorAdapter(Context mContext, Cursor mCursor) {
        super(mContext, mCursor);
        context=mContext;
        cursor=mCursor;
    }


    @Override
    public void onBindViewHolder(AlbumViewHolder holder, final Cursor cursor, final int position) {
        Glide.with(context)
                .load(cursor.getString(3))
//                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(holder.albumImageView);
//        holder.cursor=cursor;
        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try{
                    cursor.moveToPosition(position);

                    Intent intent=new Intent(context, PicturesView.class);
                    intent.putExtra("albumPath",  cursor.getString(3).substring(0,cursor.getString(3).lastIndexOf("/")));

                    intent.putExtra("albumName",cursor.getString(1));

                    context.startActivity(intent);
                }catch (Exception ex){
                    ex.printStackTrace();
                }

            }
        });

        try{
            holder.albumName.setText(cursor.getString(1));
            holder.picturesCount.setText(String.valueOf(ContentLoaderUtils.photoCountByAlbum(context,cursor.getString(1))));

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    cursor.moveToPosition(position);
                    return false;
                }
            });
        }catch (Exception ex){
            ex.printStackTrace();
        }


    }


    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.album_item,parent,false);
        return new AlbumViewHolder(view,this);
    }

    public static class AlbumViewHolder extends RecyclerView.ViewHolder implements /*View.OnClickListener*/ View.OnCreateContextMenuListener{

        private ImageView albumImageView;
        private TextView albumName;
        private TextView picturesCount;
        private Cursor cursor;
        private AlbumsCursorAdapter albumAdapter;



        public AlbumViewHolder(@NonNull View itemView,AlbumsCursorAdapter albumAdapter) {
            super(itemView);
            albumImageView=itemView.findViewById(R.id.album_image_view);
            albumName=itemView.findViewById(R.id.album_names_text_view);
            picturesCount=itemView.findViewById(R.id.images_count_text_view);
            itemView.setOnCreateContextMenuListener(this);
            this.albumAdapter=albumAdapter;
//            itemView.setOnClickListener(this);

        }

//        @Override
//        public void onClick(View v) {
//            Intent intent=new Intent(albumAdapter.context, PicturesView.class);
//            intent.putExtra("albumPath",  cursor.getString(3).substring(0,cursor.getString(3).lastIndexOf("/")));
//
//            intent.putExtra("albumName",cursor.getString(1));
//
//            itemView.getContext().startActivity(intent);
////            ((AppCompatActivity)itemView.getContext()).startActivityForResult(intent,1000);
//        }



        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(Menu.NONE,R.id.rename_context_menu,menu.NONE,"Rename");
            menu.add(Menu.NONE,R.id.delete_context_menu,menu.NONE,"Delete");
        }
    }

    @Override
    public void changeCursor(Cursor cursor) {
        super.changeCursor(cursor);
        this.cursor=cursor;
    }
}
