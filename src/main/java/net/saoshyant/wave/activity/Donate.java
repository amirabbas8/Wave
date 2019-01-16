package net.saoshyant.wave.activity;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import net.saoshyant.wave.R;


public class Donate extends Activity {
Button donatebtn1,donatebtn2,donatebtn5;
    String howmuchdonate;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.donate);
        donatebtn1=(Button) findViewById(R.id.donatebtn1);
        donatebtn1.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                howmuchdonate="1000";
            }
        });
        donatebtn2=(Button) findViewById(R.id.donatebtn2);
        donatebtn2.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                howmuchdonate="2000";
            }
        });
        donatebtn5=(Button) findViewById(R.id.donatebtn5);
        donatebtn5.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                howmuchdonate="5000";
            }
        });
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

        }
    }
}