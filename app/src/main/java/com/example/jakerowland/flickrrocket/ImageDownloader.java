package com.example.jakerowland.flickrrocket;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/** ImageDownloader - Task created to download images. I decided to separate this from the API call
 *      because it separates the HTTPS calls and allows me to call ImageDownloader multiple times
 *      concurently to buffer multiple images at the same time
 *
 * Created by Jake Rowland on 3/17/2017.
 */
public class ImageDownloader extends AsyncTask<URL, Void, Bitmap> {

    /** doInBackground - Connect to the Image URL and download the image as a bitmap.
     *
     * @param urls: URL - URL to retrieve image from
     * @return Bitmap - Image from URL
     */
    @Override
    protected Bitmap doInBackground(URL... urls) {
        //Bitmap to return
        Bitmap image = null;
        try {
            //Open the connection as an InputStream
            InputStream is = urls[0].openStream();
            //Decode stream as Bitmap
            image = BitmapFactory.decodeStream(is);
        }catch(IOException e) {
            Log.e(this.getClass().toString(), "ImageDownloader.doInBackground(URL... urls): " + e.toString());
        }
        //Return Image
        return image;
    }
}
