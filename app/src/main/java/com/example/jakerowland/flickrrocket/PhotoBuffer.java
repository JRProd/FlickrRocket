package com.example.jakerowland.flickrrocket;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ProgressBar;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/** PhotoBuffer - Underlying logic and data structure for my image viewing app. Based on a LinkedList
 *      to allow bidirectonal scrolling and on the fly resizing.
 *
 *  With implementations of both forward and reverse scrolling, I have allowed a history for users.
 *      PhotoBuffer also controls the initial amount of images loaded along with, when and how many
 *      new images are buffered when the scrolling
 *
 * Created by Jake Rowland on 3/17/2017.
 */
class PhotoBuffer {

    //Define URL
    private URL api = null;

    //Buffer Handler and LinkedList data structure
    private ImageBufferHandler loader;
    private LinkedList<Bitmap> buffer;

    //Image stats
    private int loadedImage;
    private int currentImage;
    private boolean hasMoreImages;

    /** PhotoBuffer - Initilizes and implements the PhotoBuffer
     *
     */
    PhotoBuffer() {
        //Init global variables
        buffer = new LinkedList<>();
        loadedImage = 0;
        currentImage = 0;
        hasMoreImages = true;

        //Create the ImageBUfferHandler and execute the AsyncTask
        loader = new ImageBufferHandler();
    }

    /** setApiTag - Sets api tag to allow users to change the tag for the image search
     *
     * @param tag: string - Tag to match photos to
     */
    void setApiTag(String tag) {
        try {
            //Creates URL with custom tag in_left it
            api = new URL("https://api.flickr.com/services/rest/?format=json&sort=random&method=" +
                    "flickr.photos.search&tags=" + tag + "&tag_mode=all&api_key=" +
                    "0e2b6aaf8a6901c264acb91f151a3350&nojsoncallback=1");
        }catch (MalformedURLException e){
            Log.e(this.getClass().toString(), "PhotoBuffer.PhotoBuffer(): " + e.toString());
        }
    }

    /** execute - execute the AsyncTask in_left ImageBufferHandler
     *
     */
    void execute() {
        //Checks if api set
        if(api == null)
            setApiTag("rocket");
        //Launches AsyncTask
        loader.execute(api);
    }

    /** populateBuffer - Populates the initial buffer with images. This allows for quick scrolling
     *      from the apps inception.
     *
     * @param startingBuffer: int - Variable to quickly change how many images are initially loaded
     */
    void populateBuffer(int startingBuffer)
    {
        try {
            //Wait until the loader as recieved the API response.
            loader.get(1000, TimeUnit.MILLISECONDS);
            Log.d(this.getClass().toString(), "ImageBufferHandler.populateBuffer: loader loaded" );
        }catch (InterruptedException e) {
            Log.e(this.getClass().toString(), "ImageBufferHandler.populateBuffer(): " + e);
        }catch (ExecutionException e) {
            Log.e(this.getClass().toString(), "ImageBufferHandler.populateBuffer(): " + e);
        }catch (TimeoutException e) {
            Log.e(this.getClass().toString(), "ImageBufferHandler.populateBuffer(): " + e);
        }

        //Add images to the buffer from the loader.
        for(int i = 0; i < startingBuffer; i++) {
            //Use loadedImage index to store number of images loaded. Used to calculate when user is
            // nearing the end of the buffered images
            buffer.add(loader.nextImage(loadedImage));
            loadedImage++;
        }
    }

    /** getCurrentImage - Returns the current image
     *
     * @return Bitmap - Current Image
     */
    Bitmap getCurrentImage() {
        return buffer.get(currentImage);
    }

    /** nextImage - Retrieves the next image for display
     *
     * @return Bitmap - Next image recieved
     */
    Bitmap nextImage() {
        //Boundary check to stop users from scrolling past buffered images
        if(currentImage < buffer.size() -1)
            currentImage ++;
        else
            currentImage = 0;

        //Buffer Update and number added when buffering
        int bufferUpdate = 2, bufferOverflow = 3;

        //Check if nearing the end of the buffer or if there are more images to buffer
        if(buffer.size() - currentImage < bufferUpdate && hasMoreImages)
        {
            //If neat the end. Load more images to facilitate continued scrolling
            for(int i = 0; i < bufferOverflow; i++) {
                Bitmap img = loader.nextImage(loadedImage);
                if(img != null) {
                    buffer.add(img);
                    loadedImage++;
                } else {
                    hasMoreImages = false;
                    break;
                }
            }
        }
        //Return the new current image
        return buffer.get(currentImage);
    }

    /** lastImage - Gets the last image viewed. Used for history and reverse scrolling.
     *
     * @return Bitmap - Last image sceen
     */
    Bitmap lastImage() {
        //Boundary check
        if(currentImage > 0)
            currentImage --;
        else
            currentImage = buffer.size() - 2;
        //Gets the last image seen
        return buffer.get(currentImage);
    }
}
