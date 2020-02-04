package com.yendu.Dolab.Fragments;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Slide;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.yendu.Dolab.Models.PictureModel;
import com.yendu.Dolab.R;
import com.yendu.Dolab.RecyclerViewPagerImageIndicator2;
import com.yendu.Dolab.interfaces.iQueryListener;
import com.yendu.Dolab.interfaces.imageIndicatorListener;
import com.yendu.Dolab.recyclerViewPagerImageIndicator;

import java.util.List;


public class SearchFragmentTest extends Fragment implements imageIndicatorListener, iQueryListener, LoaderManager.LoaderCallbacks<Cursor> {

    public RecyclerView indicatorrecyclerView;
    ImageView imageViewOfSelected;
    TextView  textViewOfSelected;
    List<PictureModel> pictureModelList;
    public Cursor cursor;
    private Context context;
    public ViewPager viewPager;
    public ImagesPager imagesPager;
    boolean firstTimeLoaded =false;
    public TextView imageViewText;
    private int previousSelcected=-1;
//    RecyclerView.Adapter indicatorAdapter;
    private int position;
    private String query;
    public RecyclerViewPagerImageIndicator2 indicatorAdapter;
    public static boolean ISVISIBLE=true;
    public int count;

    public SearchFragmentTest(){


    }

    public SearchFragmentTest(Cursor cursor,int imagePosition,Context context){
        this.cursor=cursor;
        this.position=imagePosition;
        this.context=context;
//        this.pictureModelListForStart=allImages;

    }
    public static SearchFragmentTest newInstance(Cursor cursor,int imagePosition,Context context){
        SearchFragmentTest searchFragment=new SearchFragmentTest(cursor,imagePosition,context);
        return searchFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search_view_fragment,container,false);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(firstTimeLoaded==false){
            LoaderManager.getInstance(this).initLoader(78,null,this);

        }else{
            LoaderManager.getInstance(this).restartLoader(78,null,this);
        }
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        viewVisibilityController=0;
//        viewVisibilitylooper=0;

        viewPager =view.findViewById(R.id.imagePager_search_view);
        if(cursor!=null){
            cursor.moveToPosition(position);
        }

        imagesPager=new ImagesPager();
        viewPager.setAdapter(imagesPager);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setCurrentItem(position);
        imageViewText=view.findViewById(R.id.text_view_no_image);


        indicatorrecyclerView=view.findViewById(R.id.search_result_recycler);
        indicatorrecyclerView.hasFixedSize();
        indicatorrecyclerView.setLayoutManager(new GridLayoutManager(getContext(),1,RecyclerView.HORIZONTAL,false));
//        final RecyclerView.Adapter indicatorAdapter=new recyclerViewPagerImageIndicator(pictureModelList,context,this);
         indicatorAdapter=new RecyclerViewPagerImageIndicator2(cursor,getContext(),this);
        indicatorrecyclerView.setAdapter(indicatorAdapter);


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(previousSelcected!=-1){
//                    if(previousSelcected<pictureModelList.size()){
//                        pictureModelList.get(previousSelcected).setSelected(false);
//                    }

                    previousSelcected=position;
//                    pictureModelList.get(position).setSelected(true);

                    indicatorrecyclerView.getAdapter().notifyDataSetChanged();
                    indicatorrecyclerView.scrollToPosition(position);
                }else{
                    previousSelcected=position;
//                    pictureModelList.get(position).setSelected(true);
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
//                if(previousSelcected<pictureModelList.size()){
//                    pictureModelList.get(previousSelcected).setSelected(false);

//                }
                previousSelcected=ImagePosition;
                indicatorrecyclerView.getAdapter().notifyDataSetChanged();

            }

        }else{
            if(previousSelcected!=ImagePosition){
                if(previousSelcected<cursor.getCount()){
                    previousSelcected=ImagePosition;
                }}

        }
        viewPager.setCurrentItem(ImagePosition);

    }

    @Override
    public void queryChange(List<PictureModel> pictureModelList, Context context) {
        this.pictureModelList=pictureModelList;




    }
    public void queryChanged(String query){
        this.query=query;
        Bundle bundle=new Bundle();
        bundle.putString("SEARCHWORDS",query);
        LoaderManager.getInstance(this).restartLoader(78,bundle,this);

    }


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
//        if(id==78) {


           // Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Uri images= MediaStore.Files.getContentUri("external");
            String order;
            String selectionn = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                + " OR "
                + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
//            String[] projection = {MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DISPLAY_NAME};
            String[] projection = {MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.TITLE};

        if (args != null) {
                String s = args.getString("SEARCHWORDS");
                String[] splited = s.split("\\s+");
                StringBuilder selection = new StringBuilder();
//                selection.append(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME);
                String[] argss = new String[splited.length];
                if (splited.length > 1) {
                    for (int i = 0; i < splited.length; i++) {
                        argss[i] = "%" + splited[i] + "%";
//                    selection.append();
                        if (i + 1 == splited.length) {


                            selection.append(MediaStore.Files.FileColumns.TITLE + " like ?");
                          //  selection.append(MediaStore.Images.ImageColumns.DISPLAY_NAME + " like ?");

                            break;
                        }
                        selection.append(MediaStore.Files.FileColumns.TITLE + " like ? or ");
                       // selection.append(MediaStore.Images.ImageColumns.DISPLAY_NAME + " like ? or ");


                    }

                } else {
                    argss[0] = "%" + splited[0] + "%";
                    selection.append(MediaStore.Files.FileColumns.TITLE + " like ?");
                  //  selection.append(MediaStore.Images.ImageColumns.TITLE + " like ?");

                }
//

                order = "CASE WHEN _display_name ='" + s + "' THEN 0 WHEN _display_name LIKE '" + s + "%" + "' THEN 1 WHEN _display_name LIKE '" + "%" + s + "%" + "' THEN 2 WHEN _display_name LIKE '" + "%" + s + "' THEN 3 ELSE 4 END, _display_name DESC";
                return new CursorLoader(getContext(), images, projection, "("+selectionn+") AND ("+selection.toString()+")", argss, order);
//            return new CursorLoader(getContext(), images, projection, selection.toString(), argss, order);

        } else {
                return new CursorLoader(getContext(),images,projection,null,null,null);
            }
//        }
//        return null;



    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        try {
            this.cursor=data;

            if(data!=null){
                count=data.getCount();
            }else{
                count=0;
            }
            imagesPager.notifyDataSetChanged();
            indicatorAdapter.changeCursor(data);
            indicatorAdapter.notifyDataSetChanged();
            if(this.cursor.getCount()>0){
                imageViewText.setVisibility(View.GONE);
            }else{
                imageViewText.setVisibility(View.VISIBLE);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }



    @Override
    public void onLoaderReset(@NonNull Loader loader) {
        indicatorAdapter.changeCursor(null);
        loader.reset();


    }
//    public void refresh(){
//        this.pictureModelList=this.pictureModelListForStart;
//    }

    public class ImagesPager extends PagerAdapter{

        @Override
        public int getCount() {
            try {
                if(cursor!=null && !cursor.isClosed()){
                    return cursor.getCount();
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }

            return 0;

        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }
        @Override
        public int getItemPosition(@NonNull Object object) {

            return POSITION_NONE;

        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, final int position) {
            LayoutInflater layoutInflater=(LayoutInflater)container.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view=layoutInflater.inflate(R.layout.search_result_pager,null);
            imageViewOfSelected=view.findViewById(R.id.search_result_imageView);
            textViewOfSelected=view.findViewById(R.id.image_name_search_result_pager);
            cursor.moveToPosition(position);
//            PictureModel pictureModel=pictureModelList.get(position);
            textViewOfSelected.setText(cursor.getString(1));
            Glide.with(context)
                    .load(cursor.getString(0))
                    .apply(new RequestOptions().fitCenter())
                    .into(imageViewOfSelected);
            imageViewOfSelected.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    browserFragment bowsFragment=browserFragment.newInstance(pictureModelList,getContext(),position);
                     browserFragmentTest bowsFragment=browserFragmentTest.newInstance(cursor,getContext(),position,query);
                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
                        bowsFragment.setEnterTransition(new Slide());
                        bowsFragment.setExitTransition(new Slide());
                    }

                    ISVISIBLE=false;
//        getFragmentManager().popBackStack(null,0);
                    getFragmentManager()

                            .beginTransaction()

//                .addSharedElement(pictureHolder.pictureImageView,position+"picture")
                            .add(R.id.main_activity_container,bowsFragment,"bowsFragment")
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
