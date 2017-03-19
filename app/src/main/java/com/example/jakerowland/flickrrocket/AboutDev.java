package com.example.jakerowland.flickrrocket;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/** AboutDev - Basic Activity to desplay infromation about me
 *
 * Created by Jake Rowland on 3/19/2017.
 */
public class AboutDev extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_dev);

        //Quick button declaration to allow user to return to image viewer
        final Button goBack = (Button) findViewById(R.id.about_dev_return);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create intent to return to main class
                Intent goBack = new Intent(AboutDev.this, ImageViewer.class);
                startActivity(goBack);
            }
        });
    }
}
