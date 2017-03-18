package com.example.jakerowland.flickrrocket;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by Jake Rowland on 3/17/2017.
 */

public class ImageDownloader extends AsyncTask<URL, Void, Bitmap> {

    @Override
    protected Bitmap doInBackground(URL... urls) {
        Bitmap image = null;
        try {
            InputStream is = urls[0].openStream();
            image = BitmapFactory.decodeStream(is);
        }catch(IOException e) {
            Log.e(this.getClass().toString(), "ImageDownloader.doInBackground(URL... urls): " + e.toString());
        }
        return image;
    }
}
