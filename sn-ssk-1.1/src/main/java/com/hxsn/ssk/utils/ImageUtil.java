package com.hxsn.ssk.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.andbase.library.image.AbImageLoader;


/**
 *
 * Created by jiely on 2017/3/16.
 */
public class ImageUtil {

    public static void displayImage(Context context, String url, final ImageView imageView){

        AbImageLoader imageLoader = AbImageLoader.getInstance(context);

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


    public static void displayImage(Context context,String url, final ImageView imageView, int width, int height){
        AbImageLoader imageLoader = AbImageLoader.getInstance(context);

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
