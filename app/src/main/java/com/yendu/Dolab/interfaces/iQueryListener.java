package com.yendu.Dolab.interfaces;

import android.content.Context;

import com.yendu.Dolab.Models.PictureModel;

import java.util.List;

public interface iQueryListener {

    public void queryChange(List<PictureModel> pictureModelList, Context context);

}
