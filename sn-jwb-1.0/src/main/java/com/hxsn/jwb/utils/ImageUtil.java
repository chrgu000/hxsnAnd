package com.hxsn.jwb.utils;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.andbase.library.image.AbImageLoader;
import com.hxsn.jwb.TApplication;

/**
 *
 * Created by jiely on 2017/3/16.
 */
public class ImageUtil {

    public static void displayImage(String url, final ImageView imageView){

        AbImageLoader imageLoader = AbImageLoader.getInstance(TApplication.context);

        imageLoader.download(url, 300,200, new AbImageLoader.OnImageDownloadListener() {
            @Override
            public void onEmpty() {

            }

            @Override
            public void onLoading() {

            }

            @Override
            public void onError() {

            }

            @Override
            public void onSuccess(Bitmap bitmap) {
                imageView.setImageBitmap(bitmap);
            }
        });
    }


    public static void displayImage(String url, final ImageView imageView, int width, int height){
        AbImageLoader imageLoader = AbImageLoader.getInstance(TApplication.context);

        imageLoader.download(url, width,height, new AbImageLoader.OnImageDownloadListener() {
            @Override
            public void onEmpty() {

            }

            @Override
            public void onLoading() {

            }

            @Override
            public void onError() {

            }

            @Override
            public void onSuccess(Bitmap bitmap) {
                imageView.setImageBitmap(bitmap);
            }
        });
    }
}
