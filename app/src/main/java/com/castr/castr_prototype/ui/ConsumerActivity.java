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
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.castr.castr_prototype.R;
import com.castr.castr_prototype.model.CastrBroadcast;
import com.castr.castr_prototype.streamsource.StreamSource;
import com.castr.castr_prototype.streamsource.TokSource;
import com.castr.castr_prototype.util.ParseHelper;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;
import com.opentok.android.SubscriberKit;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

public class ConsumerActivity extends Activity implements View.OnClickListener, ActionBar.OnNavigationListener, TokSource.SourceCallback{

    private static final String LOG_TAG = ConsumerActivity.class.getSimpleName();
    private static final String NOT_CASTING_TEXT = "Grab a Cast!";
    private static final String CASTING_TEXT = "Get me out of here!";

    //UI Related Elements
    private RelativeLayout mConsumerView;
    private Button mCastButton;
    private ImageView mLogoView;
    private ActionBar mActionBar;

    //Parse Elements
    private List<CastrBroadcast> mBroadcasts;

    //Tokbox Elements
    private Session mSession;

    private boolean isSubscribed = false;

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
        Log.e(LOG_TAG,"On Pause");
        if(mTokSource != null)
        {
            mTokSource.pause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.e(LOG_TAG,"OnDestroy");
        //reset();
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.consume_button:
                if(!isSubscribed) {
                    //consumeTok();
                    //mTokSource.connectToStream();
                    //mTokSource.connectToStream("2_MX40NTAwOTE5Mn5-MTQxMzQxMDgwMzM5N35ibXB2QTVyK0IrTHBjVG5udFlJbVEweFZ-fg");
                }
                else
                {
                    //reset();
                }
                break;
        }

    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId)
    {
        Log.e(LOG_TAG,"Item Selected");
        mTokSource.connectToStream(mBroadcasts.get(itemPosition));
        return false;
    }

    /*
    private void reset()
    {
        if (mSession != null) {
            mSession.disconnect();

        }
        mSession = null;
        //mSubscriber = null;
        mConsumerView.removeAllViews();
        mCastButton.setText(NOT_CASTING_TEXT);
        isSubscribed = false;
        mLogoView.setVisibility(View.VISIBLE);
    }*/

    private void getAvailableStreams()
    {
        ParseHelper.getAvailableBroadcasts(new FindCallback<CastrBroadcast>() {
            @Override
            public void done(List<CastrBroadcast> castrBroadcasts, ParseException e) {
                if (e == null) {
                    Log.e(LOG_TAG, "We are good, there are this many parse objects: " + castrBroadcasts.size());
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
        Log.e(LOG_TAG,"Session Created");
        mCastButton.setText(CASTING_TEXT);
    }

    @Override
    public void isLive() {
        Log.e(LOG_TAG,"Is Live");
        mLogoView.setVisibility(View.INVISIBLE);
        mTokSource.consumeStream(mConsumerView);

    }

    @Override
    public void sessionTerminated() {
        Log.e(LOG_TAG,"Session Terminated");
    }
}
