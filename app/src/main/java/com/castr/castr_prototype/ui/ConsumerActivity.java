package com.castr.castr_prototype.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.castr.castr_prototype.R;
import com.castr.castr_prototype.model.CastrBroadcast;
import com.castr.castr_prototype.streamsource.StreamSource;
import com.castr.castr_prototype.streamsource.TokSource;
import com.castr.castr_prototype.util.ParseHelper;
import com.opentok.android.Session;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

public class ConsumerActivity extends Activity implements View.OnClickListener, ActionBar.OnNavigationListener, TokSource.SourceCallback{

    private static final String LOG_TAG = ConsumerActivity.class.getSimpleName();
    private static final String NOT_CASTING_TEXT = "Grab a Cast!";
    private static final String CASTING_TEXT = "Get me out of here!";

    //Hack for the action bar
    private boolean mInitializing = true;

    private boolean mIsStreaming = false;

    private int mSelectedStream = -1;

    //UI Related Elements
    private RelativeLayout mConsumerView;
    private Button mCastButton;
    private ImageView mLogoView;
    private ActionBar mActionBar;

    //Parse Elements
    private List<CastrBroadcast> mBroadcasts;


    //TokSource drive everything right now.  Forewarning its coupled with Parse
    private TokSource mTokSource;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumer);
        mActionBar = getActionBar();
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        mConsumerView = (RelativeLayout) findViewById(R.id.consumerview);
        mCastButton = (Button) findViewById(R.id.consume_button);
        mCastButton.setOnClickListener(this);
        mLogoView = (ImageView)findViewById(R.id.logo);
        getAvailableStreams();
        mTokSource = new TokSource(this,this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.consumer, menu);
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
            mTokSource.resume();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if(mTokSource != null)
        {
            mTokSource.pause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if(mTokSource != null)
        {
            mTokSource.endConsumption();
        }
        mTokSource = null;
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.consume_button:
                if(!mIsStreaming && mSelectedStream >= 0) {
                    mTokSource.connectToStream(mBroadcasts.get(mSelectedStream));

                }
                else
                {
                    this.sessionTerminated();
                }
                break;
        }

    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId)
    {
        if(mInitializing)
        {
            mInitializing = false;
            mSelectedStream = itemPosition;
            return true;
        }
        else {
            Log.e(LOG_TAG,"Item Selected");

            return false;
        }

    }

    /**
     * Create a list of available streams for the client to choose from
     */
    private void getAvailableStreams()
    {
        ParseHelper.getAvailableBroadcasts(new FindCallback<CastrBroadcast>() {
            @Override
            public void done(List<CastrBroadcast> castrBroadcasts, ParseException e) {
                if (e == null) {
                    mBroadcasts = castrBroadcasts;
                    List<String> broadcastTitles = new ArrayList<String>();
                    for (int i =0; i < mBroadcasts.size(); i++)
                    {
                        if(mBroadcasts.get(i).getTitle() == null)
                        {
                            broadcastTitles.add("No Title");
                        }
                        else
                        {
                            broadcastTitles.add(mBroadcasts.get(i).getTitle());
                        }
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item, broadcastTitles);
                    mActionBar.setListNavigationCallbacks(arrayAdapter, ConsumerActivity.this);
                } else {
                    Log.e(LOG_TAG, "There is an exception: " + e.getLocalizedMessage());

                }
            }
        });
    }

    @Override
    public void sessionCreated() {
        Log.i(LOG_TAG,"Session Created");
        mCastButton.setText(CASTING_TEXT);
        mIsStreaming = true;
    }

    @Override
    public void isLive() {
        Log.i(LOG_TAG,"Is Live");
        mLogoView.setVisibility(View.INVISIBLE);
        mTokSource.consumeStream(mConsumerView);

    }

    @Override
    public void sessionTerminated() {
        Log.i(LOG_TAG,"Session Terminated");
        if(mTokSource != null)
        {
            mTokSource.endConsumption();
        }
        mIsStreaming = false;
        mCastButton.setText(NOT_CASTING_TEXT);
    }
}
