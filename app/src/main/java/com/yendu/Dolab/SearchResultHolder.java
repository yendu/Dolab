package com.yendu.Dolab;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class SearchResultHolder extends RecyclerView.ViewHolder {

    public ImageView imageView;
    CardView cardView;
    View activeImageController;
    public TextView textView;


    public SearchResultHolder(@NonNull View itemView) {
        super(itemView);
        imageView=itemView.findViewById(R.id.imageIndicator);
        cardView=itemView.findViewById(R.id.indicatorCard);
        activeImageController=itemView.findViewById(R.id.activeImage);
        textView=itemView.findViewById(R.id.image_name_search_result_holder);

    }
}
