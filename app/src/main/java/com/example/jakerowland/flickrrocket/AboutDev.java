package com.example.jakerowland.flickrrocket;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by The One True God on 3/19/2017.
 */

public class AboutDev extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_dev);

        final Button goBack = (Button) findViewById(R.id.about_dev_return);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goBack = new Intent(AboutDev.this, ImageViewer.class);
                startActivity(goBack);
            }
        });
    }
}
