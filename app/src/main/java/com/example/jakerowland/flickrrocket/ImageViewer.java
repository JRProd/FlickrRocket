package com.example.jakerowland.flickrrocket;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;


public class ImageViewer extends AppCompatActivity {

    private ImageSwitcher imageView;

    //PhotoBuffer for continueous browsing.
    private PhotoBuffer photos;

    //Variables for swipe gesture
    private float x1,x2;
    private static final int MIN_DISTANCE = 150;

    /** updateImage - Updates image used in the image viewer to the current image in the buffer
     *
     */
    private void updateImage() {
        //Run on UI thread to enable mjltithreaded progress bar
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Get current bitmap from buffer
                BitmapDrawable bmp = new BitmapDrawable(getResources(), photos.getCurrentImage());
                //Set image to bitmap
                imageView.setImageDrawable(bmp);
            }
        });
    }

    /** bufferImages - Initial image buffering and progress bar operations. This method defines
     *      how many images are initially buffered and handles updating the progress bar as the
     *      images buffer
     *
     */
    private void bufferImages() {
        //Number of images to buffer
        final int buffer = 25;
        //ProgressBar setup
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        final TextView loading = (TextView) findViewById(R.id.loadingTextView);
        //Resize progress to buffer size
        progressBar.setMax(buffer);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //Populate the buffer with "buffer"# of images
                    for (int i = 0; i < buffer; i++) {
                        //Increment the progresbar
                        progressBar.incrementProgressBy(1);
                        //Add image
                        photos.populateBuffer(1);
                    }
                    //Run when thread ends
                } finally {
                    //Update the image
                    updateImage();
                    //Run on UI thread to remove the progress bar at then end of buffering
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Remove the progress bar
                            progressBar.setVisibility(View.GONE);
                            loading.setVisibility(View.GONE);
                        }
                    });
                }
            }
            //Start the thread
        }).start();
    }

    /** about - Opens the about activity
     *
     */
    private void about() {
        //Creates Intent to open About activity
        Intent about = new Intent(ImageViewer.this, About.class);
        //Starts activity
        startActivity(about);
    }

    /** aboutDev - Opens the aboutDev activity
     *
     */
    private void aboutDev() {
        //Creates Intent to open About activity
        Intent aboutDev = new Intent(ImageViewer.this, AboutDev.class);
        //Starts activity
        startActivity(aboutDev);
    }

    /** onCreate - Initilizes data memebers and processing for Activity on creation
     *
     * @param savedInstanceState: Bundle - Saved instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Auto generated code
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

        //Retrieve the image view from the
        imageView  = (ImageSwitcher) findViewById(R.id.imageView);
        Animation in = AnimationUtils.loadAnimation(this, R.anim.in);
        Animation out = AnimationUtils.loadAnimation(this, R.anim.out);
        imageView.setInAnimation(in);
        imageView.setOutAnimation(out);

        //Create new PhotoBuffer and populate buffer with 30 images
        photos = new PhotoBuffer();
        //Set tag to user entry
        photos.setApiTag("swag");
        //Launch process of retrieving API from URL
        photos.execute();

        bufferImages();

        //Define onClick functionality of left arrow button
        final ImageButton buttonLeft = (ImageButton) findViewById(R.id.imageButtonLeft);
        buttonLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get last image
                photos.lastImage();
                //Update
                updateImage();
            }
        });
        //Define onClick functionality of left arrow button
        final ImageButton buttonRight = (ImageButton) findViewById(R.id.imageButtonRight);
        buttonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get next image
                photos.nextImage();
                //Update
                updateImage();
            }
        });
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

    /** onCreateOptionsMenu - Opperations to run when options menu is created
     *
     * @param menu: Menu - Menu to create
     * @return boolean - Success
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_image_viewer, menu);
        return true;
    }

    /** onOptionsItemSelected - Handles clicks to options in the menu
     *
     * @param item: MenuItem - Item clicked
     * @return boolean - Success
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //Handle Overflow menu
        switch (id){
            //Case for About button
            case R.id.action_about:
                about();
                break;
            //Case for About Dev button
            case R.id.action_about_dev:
                aboutDev();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
