package com.castr.castr_prototype.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.castr.castr_prototype.R;
import com.castr.castr_prototype.streamsource.TokSource;

/**
 * @author Matt Powers, Applico
 *
 * This activity is intended to test the casting of video with Parse and TokBox
 * TODO - a lot of this needs to be moved off of the main thread.
 *
 */

public class ProducerActivity extends Activity implements  View.OnClickListener, TokSource.SourceCallback {

    private static final String LOG_TAG = ProducerActivity.class.getSimpleName();
    private static final String NOT_CASTING_TEXT = "Start Casting!";
    private static final String CASTING_TEXT = "Stop Casting..";

    //UI Related Elements
    private RelativeLayout mPubView;
    private Button mCastButton;
    private ImageView mLogoView;

    //Boolean for if we are live
    private boolean isCasting = false;

    //TokSource drive everything right now.  Forewarning its coupled with Parse
    private TokSource mTokSource;


    //TODO move the connection activity off of the main thread.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producer);
        mPubView = (RelativeLayout)findViewById(R.id.publisherview);
        mCastButton = (Button)findViewById(R.id.cast_button);
        mCastButton.setOnClickListener(this);
        mLogoView = (ImageView)findViewById(R.id.logo);
        mTokSource = new TokSource(this,this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.producer, menu);
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

    //Android Lifecycle Methods
    @Override
    protected void onResume() {
        if(mTokSource != null)
        {
            mTokSource.resumeBroadcast();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.e(LOG_TAG,"On Pause");
        if(mTokSource != null)
        {
            mTokSource.pauseBroadcast();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy()
    {
       if(mTokSource != null)
       {
           mTokSource.endBroadcast();
       }
        mTokSource = null;
        super.onDestroy();
        finish();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
             case R.id.cast_button:
                if(!isCasting) {
                    Log.i(LOG_TAG,"Connect to Tok");
                    mTokSource.connect(1,"This is Matt's Stream");

                }
                else
                {
                    Log.i(LOG_TAG,"Disconnect from Tok");
                    mTokSource.endBroadcast();
                }
                break;
        }
    }



    /**
     * Once the session has been created we want to publish the stream
     */
    @Override
    public void sessionCreated() {
        Log.i(LOG_TAG,"Session has been created");
        mTokSource.publishStream(mPubView);
    }

    /**
     * Fired once we go live
     */
    @Override
    public void isLive() {
        Log.i(LOG_TAG,"Session is Live");
        isCasting = true;
        mLogoView.setVisibility(View.INVISIBLE);
        mCastButton.setText(CASTING_TEXT);
    }

    /**
     * Fired when the session is terminated
     */
    @Override
    public void sessionTerminated() {
        Log.e(LOG_TAG,"Session has been terminated");
        isCasting = false;
        mLogoView.setVisibility(View.VISIBLE);
        mCastButton.setText(NOT_CASTING_TEXT);
    }
}
