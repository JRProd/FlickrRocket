package com.example.jakerowland.flickrrocket;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/** About - Basic Activity to share more information about the application
 *
 * Created by Jake Rowland on 3/19/2017.
 */
public class About extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);

        //Simple button to return to ImageViewer
        final Button goBack = (Button) findViewById(R.id.about_return);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create intent to return to ImageViewer
                Intent goBack = new Intent(About.this, ImageViewer.class);
                startActivity(goBack);
            }
        });
    }
}
