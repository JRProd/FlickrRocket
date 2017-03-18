package com.example.jakerowland.flickrrocket;

import android.graphics.Bitmap;
import android.media.Image;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;


/** ImageBufferHandler - The purpose of this class is to handle the buffering of images when the
 *      application opens and to continually buffer as the user scrolls
 *
 *  With the underlying
 * Created by Jake Rowland on 3/17/2017.
 */

public class ImageBufferHandler extends AsyncTask<URL, Double, JSONArray> {

    private ImageDownloader id;
    private JSONArray imageData;
    private int currentIndex = 0;


    @NonNull
    private String convertStreamToString (InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while((line = reader.readLine()) != null)
                sb.append(line + "\n");
        }catch(IOException e) {
            Log.e(this.getClass().toString(), "ImageBufferHandler.convertStreamToString(InputStream is): " + e.toString());
        }finally {
            try {
                is.close();
            }catch (IOException e) {
                Log.e(this.getClass().toString(), "ImageBufferHandler.convertStreamToString(InputStream is): " + e.toString());
            }
        }
        return sb.toString();
    }

    @Override
    protected JSONArray doInBackground(URL... urls) {
        HttpsURLConnection connection = null;
        JSONArray photo = null;
        Log.d(this.getClass().toString(), "URL: " + urls[0]);
        try {
            connection = (HttpsURLConnection)(urls[0].openConnection());
            connection.setRequestMethod("GET");
            connection.connect();

            int status = connection.getResponseCode();

            String JSONRelpy;
            if(status == 200 || status == 201){
                InputStream response = connection.getInputStream();
                JSONRelpy = convertStreamToString(response);

                JSONObject json = null;
                JSONObject photos = null;

                try {
                    json = new JSONObject(JSONRelpy);
                    photos = json.getJSONObject("photos");
                    photo = photos.getJSONArray("photo");
                    imageData = photo;
                }catch (JSONException e) {
                    Log.e(this.getClass().toString(), "ImageBufferHandler.doInBackground(URL... urls): " + e.toString());
                }finally {
                    response.close();
                }
                Log.d(this.getClass().toString(), "Response: " + photo.toString());
            }
        }catch(IOException e) {
            Log.e(this.getClass().toString(), "ImageBufferHandler.doInBackground(URL... urls): " + e.toString());
        }
        return photo;
    }

    @Override
    protected void onProgressUpdate(Double... progress) {
    }

    @Override
    protected void onPostExecute(JSONArray array) {

    }

    public Bitmap nextImage(int index) {
        if(imageData == null)
            Log.d(this.getClass().toString(), "ImageData void");

        JSONObject imageMeta = null;
        try {
            imageMeta = (JSONObject) imageData.get(index);

            id = new ImageDownloader();
            URL url = new URL("http://farm" + imageMeta.get("farm") + ".static.flickr.com/" +
                    imageMeta.get("server") + "/" + imageMeta.get("id") + "_" +
                    imageMeta.get("secret") + "_m.jpg");

            Log.d(this.getClass().toString(), "URL: " + url);

            id.execute(url);
            Bitmap image = id.get();
            return image;
        }catch (JSONException e) {
            Log.e(this.getClass().toString(), "ImageBufferHandler.nextImage(): " + e);
        }catch (MalformedURLException e) {
            Log.e(this.getClass().toString(), "ImageBufferHandler.nextImage(): " + e);
        }catch (InterruptedException e) {
            Log.e(this.getClass().toString(), "ImageBufferHandler.nextImage(): " + e);
        }catch (ExecutionException e) {
            Log.e(this.getClass().toString(), "ImageBufferHandler.nextImage(): " + e);
        }

        return null;
    }
}
