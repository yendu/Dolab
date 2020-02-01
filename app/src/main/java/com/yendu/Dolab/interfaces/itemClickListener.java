package com.yendu.Dolab.interfaces;

import com.yendu.Dolab.Adapters.PicturesAdapter;
import com.yendu.Dolab.Models.PictureModel;

import java.util.List;

public interface itemClickListener {

    void onPicCLicked(PicturesAdapter.PictureHolder pictureHolder, int position, List<PictureModel> pictureModels);
    void onPicCLicked(String pictureFolderPath,String folderName);

}
