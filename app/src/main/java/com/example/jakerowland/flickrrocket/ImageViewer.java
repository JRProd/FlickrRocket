package com.example.jakerowland.flickrrocket;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;


public class ImageViewer extends AppCompatActivity {

    //PhotoBuffer for continueous browsing.
    private PhotoBuffer photos;

    //Variables for swipe gesture
    private float x1,x2;
    private static final int MIN_DISTANCE = 150;

    /** updateImage - Updates image used in the image viewer to the current image in the buffer
     *
     */
    private void updateImage() {
        //Get the imageView
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        //Get current bitmap from buffer
        Bitmap bmp = photos.getCurrentImage();
        //Set image to bitmap
        imageView.setImageBitmap(bmp);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //Create new PhotoBuffer and populate buffer with 30 images
        photos = new PhotoBuffer();
        photos.populateBuffer(30);

        //Update image to new image in buffer
        updateImage();
    }

    /** onTouchEvent - Controller for the swipe gestures
     *
     * @param event: MotionEvent - Android build-in method for gesture control
     * @return boolean
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Switch on Event action
        switch(event.getAction()) {
            //On Finger down
            case MotionEvent.ACTION_DOWN:
                //Get first x value
                x1 = event.getX();
                break;
            //On Finger up
            case MotionEvent.ACTION_UP:
                //Get second x value
                x2 = event.getX();
                //Find the deltaX
                float deltaX = x2 - x1;
                //Swipe left to right
                if (deltaX > MIN_DISTANCE) {
                    photos.lastImage();
                    updateImage();
                }
                //Swipe right to left
                else if(deltaX < -1 * MIN_DISTANCE) {
                    photos.nextImage();
                    updateImage();
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_image_viewer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
