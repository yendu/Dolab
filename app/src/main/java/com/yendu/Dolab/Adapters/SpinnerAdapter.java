package com.yendu.Dolab.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yendu.Dolab.R;

public class SpinnerAdapter extends BaseAdapter {
    String []strings;
    String []shortFormStrings;
    public static int[] imageResources={R.drawable.ic_united_states,R.drawable.ic_vietnam,R.drawable.ic_spain,R.drawable.ic_china};
    private Context context;


    public SpinnerAdapter(@NonNull Context context, int resource,int secondResources) {

        this.context=context;
        this.strings=this.context.getResources().getStringArray(resource);
        this.shortFormStrings=this.context.getResources().getStringArray(secondResources);

    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view=View.inflate(context,R.layout.spinner_item_list,null);
        TextView textView=view.findViewById(R.id.spinner_text_view);
        ImageView imageView=view.findViewById(R.id.spinner_image_view);

        imageView.setImageResource(imageResources[position]);
        ;

//        TextView textView=new TextView(context);

//        textView.setText(context.getResources().getStringArray(R.array.language)[position]);
        textView.setText(strings[position]);
//        textView.setClickable(true);
//        textView.setFocusable(true);
//        row.addView(textView);
//        row.addView(textView);


        return view;
    }

    @Override
    public int getCount() {
        return strings.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view=View.inflate(context,R.layout.spinner_item_top,null);
        TextView textView=view.findViewById(R.id.spinner_text_view_top);
//        textView.setText(context.getResources().getStringArray(R.array.shortformlanguage)[position]);
//        row.addView(textView);
        textView.setText(shortFormStrings[position]);
        return view;
    }
}
