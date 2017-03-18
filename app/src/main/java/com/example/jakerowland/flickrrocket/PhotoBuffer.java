package com.example.jakerowland.flickrrocket;

import android.graphics.Bitmap;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by Jake Rowland on 3/17/2017.
 */

public class PhotoBuffer {

    private int bufferUpdate = 4, bufferOverflow = 10;

    private ImageBufferHandler loader;
    private LinkedList<Bitmap> buffer;

    private int loadedImage;
    private int currentImage;

    PhotoBuffer() {
        //Create both a forward and backward buffer.
        buffer = new LinkedList<Bitmap>();
        loadedImage = 0;
        currentImage = 0;

        URL api = null;
        try {
            api = new URL("https://api.flickr.com/services/rest/?format=json&sort=random&method=" +
                    "flickr.photos.search&tags=rocket&tag_mode=all&api_key=" +
                    "0e2b6aaf8a6901c264acb91f151a3350&nojsoncallback=1");
        }catch (MalformedURLException e) {
            Log.e(this.getClass().toString(), "PhotoBuffer.PhotoBuffer(): " + e.toString());
        }
        loader = new ImageBufferHandler();
        loader.execute(api);


    }

    public boolean populateBuffer(int startingBuffer)
    {
        try {
            loader.get(1000, TimeUnit.MILLISECONDS);
        }catch (InterruptedException e) {
            Log.e(this.getClass().toString(), "ImageBufferHandler.nextImage(): " + e);
        }catch (ExecutionException e) {
            Log.e(this.getClass().toString(), "ImageBufferHandler.nextImage(): " + e);
        }catch (TimeoutException e) {
            Log.e(this.getClass().toString(), "ImageBufferHandler.nextImage(): " + e);
        }

        for(int i = 0; i < startingBuffer; i++) {
            buffer.add(loader.nextImage(loadedImage));
            loadedImage++;
            Log.v(this.getClass().toString(), "LoadedImage: " + loadedImage);
        }

        return true;
    }

    public Bitmap getCurrentImage() {
        return buffer.get(currentImage);
    }

    public Bitmap nextImage() {
        if(currentImage < buffer.size() -1)
            currentImage ++;
        else
            return null;

        if(buffer.size() - currentImage < bufferUpdate)
        {
            for(int i = 0; i < bufferOverflow; i++) {
                buffer.add(loader.nextImage(loadedImage));
                loadedImage++;
                Log.v(this.getClass().toString(), "LoadedImage: " + loadedImage);
                Log.v(this.getClass().toString(), "Buffer.size: " + buffer.size() + " | CurrentImage: " + currentImage);
            }
        }
        return buffer.get(currentImage);
    }

    public Bitmap lastImage() {
        if(currentImage > 0)
            currentImage --;
        else
            return null;
        return buffer.get(currentImage);
    }
}
