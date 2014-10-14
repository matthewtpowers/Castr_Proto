package com.castr.castr_prototype.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.castr.castr_prototype.R;
import com.castr.castr_prototype.config.GenericConstants;
import com.parse.Parse;
import com.parse.ParseUser;

/**
 * Calling class to setup token access, allows the user to set whether or not they are the producer or consumer
 * There is not going to be any error handling between people trying to hijack the same session
 * The purpose of this is to
 * 1) Connect to Parse to authenticate
 * 2) Setup a session through Parse to Tokbox
 * 3) Produce video or consumer video
 * 4) Chat through firebase
 */
public class HomeActivity extends Activity implements View.OnClickListener {

    private static final String LOG_TAG = HomeActivity.class.getSimpleName();

    private Button mProducerBtn;
    private Button mConsumerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Setup UI
        mProducerBtn = (Button)findViewById(R.id.producer_button);
        mConsumerBtn = (Button)findViewById(R.id.consumer_button);
        mProducerBtn.setOnClickListener(this);
        mConsumerBtn.setOnClickListener(this);

        //Setup parse
        Parse.initialize(this, GenericConstants.PARSE_APP_ID, GenericConstants.PARSE_CLIENT_ID);

        //TODO - replace this with real users at some point
        //This is an annonymous user
        ParseUser.enableAutomaticUser();
        ParseUser.getCurrentUser().increment("RunCount");
        ParseUser.getCurrentUser().saveInBackground();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        //Assuming all button clicks are going to be launching an intent
        Intent intent;
        switch(v.getId())
        {
            case R.id.consumer_button:
                intent = new Intent(this, ConsumerActivity.class);
                startActivity(intent);
                break;
            case R.id.producer_button:
                intent = new Intent(this, ProducerActivity.class);
                startActivity(intent);
                break;
         }
    }
}
