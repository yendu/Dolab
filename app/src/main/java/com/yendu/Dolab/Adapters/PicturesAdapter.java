package com.yendu.Dolab.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.yendu.Dolab.interfaces.ImenuController;
import com.yendu.Dolab.Models.PictureModel;
import com.yendu.Dolab.R;
import com.yendu.Dolab.interfaces.itemClickListener;

import java.util.ArrayList;
import java.util.List;

public class PicturesAdapter extends RecyclerView.Adapter<PicturesAdapter.PictureHolder>  {

    public List<PictureModel> pictureModelArrayList;
    private Context context;
    private final itemClickListener clickListener;
    private ImenuController imenuController;
    public int position;
    public List<Integer>selectedList;
    public boolean selected=false;
    public boolean refresh=false;

    public PicturesAdapter(Context mContext,List<PictureModel>mPictureModelArrayList,itemClickListener listener,ImenuController imenuController){
        this.pictureModelArrayList=mPictureModelArrayList;
        this.context=mContext;
        this.clickListener=listener;
        this.imenuController=imenuController;
        this.selectedList=new ArrayList<>();
//        setHasStableIds(true);

    }
    public PicturesAdapter(Context mContext,List<PictureModel>mPictureModelArrayList,itemClickListener listener){
        this.pictureModelArrayList=mPictureModelArrayList;
        this.context=mContext;
        this.clickListener=listener;
    }
    public int getPosition(){
        return position;
    }
    public void setPosition(int position){
        this.position=position;
    }

    @NonNull
    @Override
    public PictureHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.pictures_item,parent,false);
        PictureHolder pictureHolder=new PictureHolder(view,this);
        return pictureHolder;
    }



    @Override
    public void onBindViewHolder(@NonNull final PictureHolder holder, final int position) {
        Glide.with(context)
                .load(pictureModelArrayList.get(position).getPath())
//                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(holder.pictureImageView);


        holder.pictureImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selected){

//                    if(selectedList.size()>0){
                        if(selectedList.contains(holder.getAdapterPosition())){
                            pictureModelArrayList.get(position).setSelected(false);
                            selectedList.remove(((Integer) holder.getAdapterPosition()));
//                            holder.itemView.setBackgroundResource(0);
                            holder.frameLayout.setForeground(null);
                        }else{
                            selectedList.add(holder.getAdapterPosition());
                            pictureModelArrayList.get(position).setSelected(true);

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
                    clickListener.onPicCLicked(holder,holder.getAdapterPosition(),pictureModelArrayList);

                }

            }
        });
        if(imenuController!=null){
            holder.pictureImageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
//                set
                    selected=true;
                    if(!selectedList.contains(holder.getAdapterPosition())){
                        selectedList.add(holder.getAdapterPosition());
                        pictureModelArrayList.get(holder.getAdapterPosition()).setSelected(true);

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
        if(pictureModelArrayList.get(holder.getAdapterPosition()).isSelected() && selected){
            holder.frameLayout.setForeground(holder.itemView.getContext().getResources().getDrawable(R.drawable.selected));
        }else{
            holder.frameLayout.setForeground(null);
        }


    }

    @Override
    public int getItemCount() {

        if(pictureModelArrayList!=null){
            return pictureModelArrayList.size();
        }
        return 0;

    }

//    @Override
//    public String getSectionTitle(int position) {
//        return pictureModelArrayList.get(position).getName().substring(0,1);
//    }

    public static class PictureHolder extends RecyclerView.ViewHolder implements View.OnClickListener/*,View.OnCreateContextMenuListener*/{

        ImageView pictureImageView;
        FrameLayout frameLayout;
        PicturesAdapter picturesAdapter;
        public PictureHolder(@NonNull View itemView,PicturesAdapter mPicturesAdapter) {
            super(itemView);
            frameLayout=itemView.findViewById(R.id.image_container);
//            itemView.setOnClickListener(this);
            picturesAdapter=mPicturesAdapter;
//            itemView.setOnCreateContextMenuListener(this);
//            itemView.getContext().

            pictureImageView=itemView.findViewById(R.id.picture_image_view);

        }

        @Override
        public void onClick(View v) {
//            browserFragment bowsFragment=browserFragment.newInstance(picturesAdapter.pictureModelArrayList,itemView.getContext(),getAdapterPosition());
//
//            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
//                bowsFragment.setEnterTransition(new Slide());
//                bowsFragment.setExitTransition(new Slide());
//            }
//            ((FragmentActivity)itemView.getContext()).getSupportFragmentManager()
//                    .beginTransaction()
////                .addSharedElement(pictureHolder.pictureImageView,position+"picture")
//                    .add(R.id.displayContainer,bowsFragment)
//                    .addToBackStack(null)
//                    .commit();


        }

    }
}
