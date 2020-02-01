package com.yendu.Dolab.Adapters;

import android.content.Context;
import android.content.Intent;
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
import com.yendu.Dolab.Models.AlbumModel;
import com.yendu.Dolab.Activites.PicturesView;
import com.yendu.Dolab.R;

import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {

    public Context context;
    public List<AlbumModel>photoModels;
    public int position=0;


    public AlbumAdapter(Context mContext, List<AlbumModel> mPhotoModels){
        context=mContext;
        photoModels=mPhotoModels;
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.album_item,parent,false);
        AlbumViewHolder albumViewHolder=new AlbumViewHolder(v,this);

        return albumViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, final int position) {

        Glide.with(context)
                .load(photoModels.get(position).getFirstPicturePath())
//                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(holder.albumImageView);
        holder.picturesCount.setText(String.valueOf(photoModels.get(position).getNumberOfPictures()));
        holder.albumName.setText(photoModels.get(position).getName());

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setPosition(position);

                return false;
            }
        });

    }




    public void setPosition(int pos){
        this.position=pos;
    }

    public int getPosition(){
        return this.position;
    }


    @Override
    public int getItemCount() {
        if(photoModels!=null){
            return photoModels.size();
        }
        return 0;

    }



    public static class AlbumViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener ,View.OnCreateContextMenuListener{

        private ImageView albumImageView;
        private TextView albumName;
        private TextView picturesCount;
        private AlbumAdapter albumAdapter;

        public AlbumViewHolder(@NonNull View itemView,AlbumAdapter albumAdapter) {
            super(itemView);
            albumImageView=itemView.findViewById(R.id.album_image_view);
            albumName=itemView.findViewById(R.id.album_names_text_view);
            picturesCount=itemView.findViewById(R.id.images_count_text_view);
            itemView.setOnCreateContextMenuListener(this);
            this.albumAdapter=albumAdapter;
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
             Intent intent=new Intent(albumAdapter.context, PicturesView.class);
             intent.putExtra("albumPath",albumAdapter.photoModels.get(getAdapterPosition()).getPath());
             intent.putExtra("albumName",albumAdapter.photoModels.get(getAdapterPosition()).getName());

             itemView.getContext().startActivity(intent);
//            ((AppCompatActivity)itemView.getContext()).startActivityForResult(intent,1000);
        }



        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(Menu.NONE,R.id.rename_context_menu,menu.NONE,"Rename");
            menu.add(Menu.NONE,R.id.delete_context_menu,menu.NONE,"Delete");
        }
    }
}
