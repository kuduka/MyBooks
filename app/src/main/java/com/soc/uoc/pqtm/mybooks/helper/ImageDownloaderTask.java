package com.soc.uoc.pqtm.mybooks.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

//Code from https://stackoverflow.com/questions/2471935/how-to-load-an-imageview-by-url-in-android

public class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {

    private ImageView bmImage;

    public ImageDownloaderTask(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        /*Drawable[] layers = new Drawable[2];
        layers[0] = new BitmapDrawable(bmImage.getResources(), bmImage.getDrawingCache());
        layers[1] = new BitmapDrawable(bmImage.getResources(), result);

        TransitionDrawable transitionDrawable = new TransitionDrawable(layers);
        transitionDrawable.setCrossFadeEnabled(true);

        bmImage.setImageDrawable(transitionDrawable);
        transitionDrawable.startTransition(1000);*/
        bmImage.setImageBitmap(result);

    }
}