package com.yendu.Dolab.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.yendu.Dolab.R;
import com.yendu.Dolab.interfaces.ImenuController;
import com.yendu.Dolab.interfaces.itemClickListener;

import java.util.ArrayList;
import java.util.List;


public class PicturesCursorAdapter extends CursorAdapter<PicturesCursorAdapter.PicturesViewHolder> {
    Context context;
    public Cursor cursor;
    itemClickListener mItemClickListener;
    ImenuController imenuController;
    public List<Integer> selectedList;
    public boolean selected=false;
    public int position;
    public boolean refresh=false;

    public PicturesCursorAdapter(Context mContext, Cursor mCursor ,itemClickListener listener, ImenuController imenuController) {
        super(mContext, mCursor);
        this.context=mContext;
        this.cursor=mCursor;
        this.imenuController=imenuController;
        this.mItemClickListener=listener;
        this.selectedList=new ArrayList<>();
    }

    @Override
    public void onBindViewHolder(final PicturesViewHolder holder, final Cursor cursor, final int position) {
        Glide.with(context)
                .load(cursor.getString(0))
//                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(holder.pictureImageView);


        holder.pictureImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selected){

//                    if(selectedList.size()>0){
                    if(selectedList.contains(holder.getAdapterPosition())){
//                        pictureModelArrayList.get(position).setSelected(false);
                        selectedList.remove(((Integer) holder.getAdapterPosition()));
//                            holder.itemView.setBackgroundResource(0);
                        holder.frameLayout.setForeground(null);
                    }else{
                        selectedList.add(holder.getAdapterPosition());
//                        pictureModelArrayList.get(position).setSelected(true);

                        holder.frameLayout.setForeground(holder.itemView.getContext().getResources().getDrawable(R.drawable.selected));
                    }



//                    }else{
//                        selectedList.add(position);
//                        holder.itemView.setBackgroundResource(R.drawable.selected);
//                    }

                    if(selectedList.size()==0){
                        selected=false;
                        refresh=true;
                        imenuController.rebackToNormal();
                    }else if(selectedList.size()==1){
                        imenuController.renameMenuController();;
                    }
                    else{
                        imenuController.menuController();
                    }


                }else{
                    mItemClickListener.onPicCLicked(null,position,null);
//                    mItemClickListener.onPicCLicked();

                }

            }
        });

        if(imenuController!=null){
            holder.pictureImageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
//                set
                    selected=true;
                    cursor.moveToPosition(position);
                    if(!selectedList.contains(holder.getAdapterPosition())){
                        selectedList.add(holder.getAdapterPosition());

//                        pictureModelArrayList.get(holder.getAdapterPosition()).setSelected(true);

                    }
                    imenuController.renameMenuController();
                    if(selectedList.size()>1){
                        imenuController.menuController();
                    }


                    holder.frameLayout.setForeground(holder.itemView.getContext().getResources().getDrawable(R.drawable.selected));
                    setPosition(holder.getAdapterPosition());
//                Toast.makeText(context,"long click",Toast.LENGTH_LONG).show();
                    return true;
                }
            });
        }
        if(refresh){
            holder.frameLayout.setForeground(null);
            refresh=false;
        }
//        if(pictureModelArrayList.get(holder.getAdapterPosition()).isSelected() && selected){
//            holder.frameLayout.setForeground(holder.itemView.getContext().getResources().getDrawable(R.drawable.selected));
//        }else{
//            holder.frameLayout.setForeground(null);
//        }
        if(selectedList.contains(cursor.getPosition())&&selected){
            holder.frameLayout.setForeground(holder.itemView.getContext().getResources().getDrawable(R.drawable.selected));
        }else{
            holder.frameLayout.setForeground(null);
        }

    }
    public void setPosition(int position){
        this.position=position;
    }

    @NonNull
    @Override
    public PicturesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.pictures_item,parent,false);
        return new PicturesViewHolder(view);
    }



    public static class PicturesViewHolder extends RecyclerView.ViewHolder{
        ImageView pictureImageView;

        FrameLayout frameLayout;
        PicturesAdapter picturesAdapter;
        public PicturesViewHolder(@NonNull View itemView) {
            super(itemView);
            pictureImageView=itemView.findViewById(R.id.picture_image_view);
            frameLayout=itemView.findViewById(R.id.image_container);
//            itemView.setOnClickListener(this);
//            picturesAdapter=mPicturesAdapter;
//            itemView.setOnCreateContextMenuListener(this);
//            itemView.getContext().

//            pictureImageView=itemView.findViewById(R.id.picture_image_view);
        }
    }
    @Override
    public void changeCursor(Cursor cursor) {
        super.changeCursor(cursor);
        this.cursor=cursor;
    }
}
