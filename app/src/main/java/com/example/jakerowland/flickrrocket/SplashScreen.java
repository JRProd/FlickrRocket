package com.example.jakerowland.flickrrocket;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/** SplashScreen - Qucik screen to show application and prompt user
 *
 * Created by Jake Rowland on 19-Mar-17.
 */
public class SplashScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        //Create thread to pause splash screen for 600ms
        Thread timerThread = new Thread(){
            public void run(){
                try{
                    //Pause for 600ms
                    sleep(600);
                }catch(InterruptedException e){
                    e.printStackTrace();
                //After time is up continue to main ImageViewer
                }finally{
                    //Create intent to transfer to ImageViewer
                    Intent intent = new Intent(SplashScreen.this,ImageViewer.class);
                    startActivity(intent);
                }
            }
        };
        timerThread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Finish activity on pause so back button does not reactivate splash
        finish();
    }

}