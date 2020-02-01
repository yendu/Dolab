package com.yendu.Dolab.Fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Slide;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.yendu.Dolab.Models.PictureModel;
import com.yendu.Dolab.R;
import com.yendu.Dolab.interfaces.iQueryListener;
import com.yendu.Dolab.interfaces.imageIndicatorListener;
import com.yendu.Dolab.recyclerViewPagerImageIndicator;

import java.util.List;

public class SearchFragment extends Fragment implements imageIndicatorListener, iQueryListener {

    public RecyclerView indicatorrecyclerView;
    ImageView imageViewOfSelected;
    TextView  textViewOfSelected;
    List<PictureModel> pictureModelList;

    private Context context;
    public ViewPager viewPager;
    private ImagesPager imagesPager;
    public TextView imageViewText;
    private int previousSelcected=-1;

    private int position;


    public SearchFragment(){


    }

    public SearchFragment(List<PictureModel>allImages,int imagePosition,Context context){
        this.pictureModelList=allImages;
        this.position=imagePosition;
        this.context=context;
//        this.pictureModelListForStart=allImages;

    }
    public static SearchFragment newInstance(List<PictureModel> allImages,int imagePosition,Context context){
       SearchFragment searchFragment=new SearchFragment(allImages,imagePosition,context);
       return searchFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search_view_fragment,container,false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        viewVisibilityController=0;
//        viewVisibilitylooper=0;

        viewPager =view.findViewById(R.id.imagePager_search_view);
        imagesPager=new ImagesPager();
        viewPager.setAdapter(imagesPager);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setCurrentItem(position);
        imageViewText=view.findViewById(R.id.text_view_no_image);


        indicatorrecyclerView=view.findViewById(R.id.search_result_recycler);
        indicatorrecyclerView.hasFixedSize();
        indicatorrecyclerView.setLayoutManager(new GridLayoutManager(getContext(),1,RecyclerView.HORIZONTAL,false));
        final RecyclerView.Adapter indicatorAdapter=new recyclerViewPagerImageIndicator(pictureModelList,context,this);
        indicatorrecyclerView.setAdapter(indicatorAdapter);


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(previousSelcected!=-1){
                    if(previousSelcected<pictureModelList.size()){
                        pictureModelList.get(previousSelcected).setSelected(false);
                    }

                    previousSelcected=position;
                    pictureModelList.get(position).setSelected(true);

                    indicatorrecyclerView.getAdapter().notifyDataSetChanged();
                    indicatorrecyclerView.scrollToPosition(position);
                }else{
                    previousSelcected=position;
                    pictureModelList.get(position).setSelected(true);
                    indicatorrecyclerView.getAdapter().notifyDataSetChanged();
                    indicatorrecyclerView.scrollToPosition(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });








    }

    @Override
    public void onImageIndicatorClicked(int ImagePosition) {

        if(previousSelcected!=-1){
            if(previousSelcected!=ImagePosition){
                if(previousSelcected<pictureModelList.size()){
                    pictureModelList.get(previousSelcected).setSelected(false);

                }
                previousSelcected=ImagePosition;
                indicatorrecyclerView.getAdapter().notifyDataSetChanged();

            }

        }else{
            if(previousSelcected!=ImagePosition){
                if(previousSelcected<pictureModelList.size()){
                previousSelcected=ImagePosition;
            }}

        }
        viewPager.setCurrentItem(ImagePosition);

    }

    @Override
    public void queryChange(List<PictureModel> pictureModelList, Context context) {
        this.pictureModelList=pictureModelList;
//        indicatorrecyclerView.getAdapter().notifyDataSetChanged();
//        imagesPager.notifyDataSetChanged();



    }
    public void queryChanged(List<PictureModel>pictureModels){
        this.pictureModelList=pictureModels;
        recyclerViewPagerImageIndicator indicatorPager=new recyclerViewPagerImageIndicator(pictureModelList,context,this);
        indicatorrecyclerView.setAdapter(indicatorPager);
        imagesPager=new ImagesPager();
        viewPager.setAdapter(imagesPager);

    }
//    public void refresh(){
//        this.pictureModelList=this.pictureModelListForStart;
//    }

    private class ImagesPager extends PagerAdapter{

        @Override
        public int getCount() {
            if(pictureModelList!=null){
                return pictureModelList.size();
            }
            return 0;

        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, final int position) {
            LayoutInflater layoutInflater=(LayoutInflater)container.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view=layoutInflater.inflate(R.layout.search_result_pager,null);
            imageViewOfSelected=view.findViewById(R.id.search_result_imageView);
            textViewOfSelected=view.findViewById(R.id.image_name_search_result_pager);
            PictureModel pictureModel=pictureModelList.get(position);
            textViewOfSelected.setText(pictureModel.getName());
            Glide.with(context)
                    .load(pictureModel.getPath())
                    .apply(new RequestOptions().fitCenter())
                    .into(imageViewOfSelected);
            imageViewOfSelected.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    browserFragment bowsFragment=browserFragment.newInstance(pictureModelList,getContext(),position);

                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
                        bowsFragment.setEnterTransition(new Slide());
                        bowsFragment.setExitTransition(new Slide());
                    }

//        getFragmentManager().popBackStack(null,0);
                    getFragmentManager()

                            .beginTransaction()

//                .addSharedElement(pictureHolder.pictureImageView,position+"picture")
                            .add(R.id.main_activity_container,bowsFragment)
                            .addToBackStack(bowsFragment.getClass().getName())
                            .commit();
                }
            });


            ((ViewPager)container).addView(view);
            return view;

        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view==((View)object);
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            ((ViewPager)container).removeView((View)object);
        }


    }
}
