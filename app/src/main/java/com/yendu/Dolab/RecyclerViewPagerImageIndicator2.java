
package com.yendu.Dolab;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.yendu.Dolab.Adapters.CursorAdapter;
import com.yendu.Dolab.Models.PictureModel;
import com.yendu.Dolab.interfaces.imageIndicatorListener;



public class RecyclerViewPagerImageIndicator2 extends CursorAdapter<SearchResultHolder> {


    Cursor cursor;
    Context pictureContx;
    private final imageIndicatorListener imageListerner;




    public RecyclerViewPagerImageIndicator2(Cursor cursor, Context pictureContx, imageIndicatorListener imageListerner) {
        super(pictureContx,cursor);
        this.cursor= cursor;
        this.pictureContx = pictureContx;
        this.imageListerner = imageListerner;
    }


    @NonNull
    @Override
    public SearchResultHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View cell = inflater.inflate(R.layout.search_result_holder, parent, false);
        return new SearchResultHolder(cell);
    }

//    @Override
//    public void onBindViewHolder(@NonNull SearchResultHolder holder, final int position) {
//
//        final PictureModel pic = pictureList.get(position);
//
//        holder.activeImageController.setBackgroundColor(pic.isSelected() ? Color.parseColor("#00000000") : Color.parseColor("#8c000000"));
//
//        Glide.with(pictureContx)
//                .load(pic.getPath())
//                .apply(new RequestOptions().centerCrop())
//                .into(holder.imageView);
//
////        holder.textView.setText(pic.getName());
////        if (pic.isSelected()) {
////            holder.textView.setVisibility(View.VISIBLE);
////        } else {
////            holder.textView.setVisibility(View.INVISIBLE);
////        }
//
//
//        holder.imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //holder.card.setCardElevation(5);
//                pic.setSelected(true);
//                notifyDataSetChanged();
//                imageListerner.onImageIndicatorClicked(position);
//            }
//        });
//
//    }

//    @Override
//    public int getItemCount() {
//        if (pictureList != null) {
//            return pictureList.size();
//        }
//        return 0;
//
//    }

    @Override
    public void onBindViewHolder(SearchResultHolder holder, Cursor cursor, final int position) {

        holder.activeImageController.setBackgroundColor(Color.parseColor("#8c000000"));
        Glide.with(pictureContx)
                .load(cursor.getString(0))
                .apply(new RequestOptions().centerCrop())
                .into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //holder.card.setCardElevation(5);
//                pic.setSelected(true);
                notifyDataSetChanged();
                imageListerner.onImageIndicatorClicked(position);
            }
        });

    }
    @Override
    public void changeCursor(Cursor cursor) {
        super.changeCursor(cursor);
        this.cursor=cursor;
    }
}
