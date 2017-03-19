package com.example.jakerowland.flickrrocket;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewSwitcher;


public class ImageViewer extends AppCompatActivity {

    //Views related to Tag selection
    private TextView enterTag;
    private EditText tag;
    private ImageButton submitTag;

    //Views related to progress bar
    private ProgressBar progressBar;
    private TextView loading;

    //Views related to navigation
    private ImageButton buttonLeft;
    private ImageButton buttonRight;

    //ImageSwitcher to support animations
    private ImageSwitcher imageView;
    private Animation in_left;
    private Animation out_left;
    private Animation in_right;
    private Animation out_right;

    //PhotoBuffer for continueous browsing.
    private PhotoBuffer photos;

    //Variables for swipe gesture
    private float x1;
    private static final int MIN_DISTANCE = 150;

    private void init() {
        //Initilize the ImageSwitcher
        imageView  = (ImageSwitcher) findViewById(R.id.image_switcher);
        //Create ImageView inside ImageSwitcher
        imageView.setFactory(new ViewSwitcher.ViewFactory() {

            /** makeView - Make and set up view for ImageSwitcher
             *
             * @return View: nwe view to use in ImageSwitcher
             */
            @Override
            public View makeView() {
                //Create ImageView
                ImageView newView = new ImageView(getApplicationContext());
                //Set Layout
                newView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
                //Reutrn newVies
                return newView;
            }
        });

        //Initilize the animations
        in_left = AnimationUtils.loadAnimation(this, R.anim.in_left);
        out_left = AnimationUtils.loadAnimation(this, R.anim.out_left);
        in_right = AnimationUtils.loadAnimation(this, R.anim.in_right);
        out_right = AnimationUtils.loadAnimation(this, R.anim.out_right);

        //Initilize the Tag selection
        //Retrieve the Views for the tag selection
        enterTag = (TextView) findViewById(R.id.text_tag);
        tag = (EditText) findViewById(R.id.edit_text_tag);
        submitTag = (ImageButton) findViewById(R.id.tag_submit);
        //Create onClickListener
        submitTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //Get text in tag EditText
                    String tagString = tag.getText().toString();
                    //If text is empty
                    if(tagString.equals("")) {
                        //Deafult to rocket
                        photos.setApiTag("rocket");
                    } else {
                        //Or set tag to text in tag EditText
                        photos.setApiTag(tagString);
                    }
                    //Execute photos
                    photos.execute();
                    //Buffer Images
                    bufferImages();
                    //Remove UI elemets from screen
                }finally {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Remove TextView, EditText, and ImageBuffont
                            enterTag.setVisibility(View.GONE);
                            tag.setVisibility(View.GONE);
                            submitTag.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });

        //Initilize the left and right buttons
        //Define onClick functionality of left arrow button
        buttonLeft = (ImageButton) findViewById(R.id.imageButtonLeft);
        buttonLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get last image
                photos.lastImage();
                //Sets animation to correct direction
                imageView.setInAnimation(out_left);
                imageView.setInAnimation(in_left);
                //Update
                updateImage();
            }
        });
        //Define onClick functionality of left arrow button
        buttonRight = (ImageButton) findViewById(R.id.imageButtonRight);
        buttonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get next image
                photos.nextImage();
                //Sets animation to correct direction
                imageView.setInAnimation(in_right);
                imageView.setInAnimation(out_right);
                //Update
                updateImage();
            }
        });
    }

    /** updateImage - Updates image used in_left the image viewer to the current image in_left the buffer
     *
     */
    private void updateImage() {
        //Run on UI thread to enable mjltithreaded progress bar
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //imageView.setImageDrawable(null);
                //Get current bitmap from buffer
                BitmapDrawable bmp = new BitmapDrawable(imageView.getResources(), photos.getCurrentImage());
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
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        loading = (TextView) findViewById(R.id.loadingTextView);
        //Resize progress to buffer size
        progressBar.setMax(buffer);

        //Set loading and progressBar to VISABLE
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Set visability for progress bar UI effects
                loading.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
            }
        });

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
                    //Adds navigation buttons
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Remove the progress bar
                            progressBar.setVisibility(View.GONE);
                            loading.setVisibility(View.GONE);
                            //Add the navigation buttons
                            buttonLeft.setVisibility(View.VISIBLE);
                            buttonRight.setVisibility(View.VISIBLE);
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

        //Create new PhotoBuffer and populate buffer with 30 images
        photos = new PhotoBuffer();
        //Set tag to user entryroc

        init();
    }

    /** onTouchEvent - Controller for the swipe gestures
     *
     * @param event: MotionEvent - Android build-in_left method for gesture control
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
                float x2 = event.getX();
                //Find the deltaX
                float deltaX = x2 - x1;
                //Swipe left to right
                if (deltaX > MIN_DISTANCE) {
                    //Gets the last image
                    photos.lastImage();
                    //Set animation to correct direction
                    imageView.setInAnimation(out_left);
                    imageView.setInAnimation(in_left);
                    //Updates image
                    updateImage();
                }
                //Swipe right to left
                else if(deltaX < -1 * MIN_DISTANCE) {
                    //Gets the next image
                    photos.nextImage();
                    //Sets animation to correct direction
                    imageView.setInAnimation(in_right);
                    imageView.setInAnimation(out_right);
                    //Updates image
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

    /** onOptionsItemSelected - Handles clicks to options in_left the menu
     *
     * @param item: MenuItem - Item clicked
     * @return boolean - Success
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in_left AndroidManifest.xml.
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
