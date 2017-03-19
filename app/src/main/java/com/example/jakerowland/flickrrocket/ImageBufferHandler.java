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
 *  With the underlying AysncTask to allow loading of images while the user is interacting with
 *      this application. I achieve this moving buffer by requesting images from the ImageDownloader
 *      on an as needed basis. My solution revolves around stroing the JSONArray contaning the
 *      picture details globaly in_left this class. After the AsyncTask has finsihed. I can access
 *      picture metadata anywhere in_left the program.
 *
 * Created by Jake Rowland on 3/17/2017.
 */
class ImageBufferHandler extends AsyncTask<URL, Double, JSONArray> {
    //Array of image metadata
    private JSONArray imageData;


    /** convertStreamToString - I retrive the JSONObject from the URL as a stream. This method
     *      converts that stream to String to allow JSONParser to create JSONObjects.
     *
     * @param is: InputStream - Content retrieved from the API call
     * @return String - String representation of content retrieved from API call
     */
    @NonNull
    private String convertStreamToString (InputStream is) {
        //Create BufferedReader to read from the InputStream
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        //Builds string one line at a time
        StringBuilder sb = new StringBuilder();

        //Temporary variable for parsing
        String line;
        try {
            //Parse InputStream until EOF
            while((line = reader.readLine()) != null)
                //Concurently build string
                sb.append(line).append("\n");

        }catch(IOException e) {
            //Handle IOException
            Log.e(this.getClass().toString(), "ImageBufferHandler.convertStreamToString(InputStream is): " + e.toString());
        }finally {
            try {
                //Close the InputStream to conserve memory
                is.close();
            }catch (IOException e) {
                //Handle IOException
                Log.e(this.getClass().toString(), "ImageBufferHandler.convertStreamToString(InputStream is): " + e.toString());
            }
        }
        //Return String representation of API result
        return sb.toString();
    }

    /** doInBackground - Main portion of ImageBufferHandler. This method opens a connection to the
     *      Flickr API and converts the InputStream to String and then to a JSONObject.
     *
     *  From the JSONObject, I extract the photo JSONArray and store that globally for other use.
     *
     * @param urls: URL - This contains the link to the API
     * @return JSONArray - Photo metadata used to download images
     */
    @Override
    protected JSONArray doInBackground(URL... urls) {
        //Creates teh HTTPS connection and the JSONArray that is the goal of this method
        HttpsURLConnection connection;
        JSONArray photo = null;
        try {
            //Open the connection to the Flickr API
            connection = (HttpsURLConnection)(urls[0].openConnection());
            connection.setRequestMethod("GET");
            connection.connect();

            //Check status of connection
            int status = connection.getResponseCode();

            String JSONRelpy;
            //If status is success
            if(status == 200 || status == 201){
                //Get response back from URL
                InputStream response = connection.getInputStream();
                //Convert to string
                JSONRelpy = convertStreamToString(response);

                //Create JSONObject
                JSONObject json;
                try {
                    //Convert String to JSONObject
                    json = new JSONObject(JSONRelpy);
                    //Extract JSONArray from
                    photo = json.getJSONObject("photos").getJSONArray("photo");
                    //Set global JSONArray
                    imageData = photo;
                }catch (JSONException e) {
                    //Catch any JSONExceptions
                    Log.e(this.getClass().toString(), "ImageBufferHandler.doInBackground(URL... urls): " + e.toString());
                }finally {
                    //Close response when finsihed
                    response.close();
                }
            }
        }catch(IOException e) {
            Log.e(this.getClass().toString(), "ImageBufferHandler.doInBackground(URL... urls): " + e.toString());
        }
        //Return JSONArray
        return photo;
    }

    @Override
    protected void onProgressUpdate(Double... progress) {
    }

    @Override
    protected void onPostExecute(JSONArray array) {

    }

    /** nextImage - Uses ImageDownloader to get the next image. Each call uses a new ImageDownloader
     *      to speed up concurrent image requests. Each ImageDownloader is also an AsnycTask in_left
     *      itself
     *
     * @param index: int - The index of the image in_left th JSONArray to retrieve
     * @return Bitmap - Image retrieved.
     */
    Bitmap nextImage(int index) {
        JSONObject imageMeta;
        try {
            //Get ImageData from JSONArray
            imageMeta = (JSONObject) imageData.get(index);

            //Instantiate ImageDownloader
            ImageDownloader id = new ImageDownloader();
            //Retrieve image from url
            // 'http://farm"FARM#".static.flickr.com/"SERVER"/"PICTURE_ID"_"SECRET"_m.jpg
            URL url = new URL("http://farm" + imageMeta.get("farm") + ".static.flickr.com/" +
                    imageMeta.get("server") + "/" + imageMeta.get("id") + "_" +
                    imageMeta.get("secret") + "_m.jpg");

            //Launch the AsyncTask
            id.execute(url);
            //Return image
            return id.get();
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
