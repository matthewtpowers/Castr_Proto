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
import com.castr.castr_prototype.config.GenericConstants;
import com.castr.castr_prototype.model.CastrBroadcast;
import com.castr.castr_prototype.util.ParseHelper;
import com.opentok.android.BaseVideoRenderer;
import com.opentok.android.Connection;
import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;
import com.opentok.android.SubscriberKit;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

public class ConsumerActivity extends Activity implements View.OnClickListener, Session.ConnectionListener, Session.SignalListener, Session.StreamPropertiesListener, Session.SessionListener, SubscriberKit.SubscriberListener,
        Subscriber.VideoListener, ActionBar.OnNavigationListener {

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
    private Subscriber mSubscriber;

    private boolean isSubscribed = false;


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
        Log.e(LOG_TAG, "On Resume");
        if (mSession != null) {
            mSession.onResume();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.e(LOG_TAG, "On Pause");
        if (mSession != null) {
            mSession.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.e(LOG_TAG,"OnDestroy");
        reset();
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.consume_button:
                if(!isSubscribed) {
                    consumeTok();
                }
                else
                {
                    reset();
                }
                break;
        }

    }

    @Override
    public void onConnectionCreated(Session session, Connection connection) {
        Log.e(LOG_TAG, "Connection Listener Connect ID: " + session.getSessionId());
        Log.e(LOG_TAG, "Connection Listener Creation Time: " + connection.getCreationTime());
        Log.e(LOG_TAG, "Connection Listener Connection ID: " + connection.getConnectionId());
    }

    @Override
    public void onConnectionDestroyed(Session session, Connection connection) {
        Log.e(LOG_TAG, "Connection Destroyed Connect ID: " + session.getSessionId());
        Log.e(LOG_TAG, "Connection Destroyed Creation Time: " + connection.getCreationTime());
        Log.e(LOG_TAG, "Connection Destroyed Connection ID: " + connection.getConnectionId());
    }

    @Override
    public void onConnected(Session session) {
        Log.e(LOG_TAG, "Session Connect ID: " + session.getSessionId());
    }

    @Override
    public void onDisconnected(Session session) {
        Log.e(LOG_TAG, "Session Disconnect ID: " + session.getSessionId());
        //reset();
    }


    //A stream is received when a stream is published to a session
    @Override
    public void onStreamReceived(Session session, Stream stream) {
        Log.e(LOG_TAG, "Session ID Received: " + session.getSessionId());
        Log.e(LOG_TAG, "Stream ID Recieved: " + stream.getStreamId());
        Log.e(LOG_TAG, "Stream Name: " + stream.getName());

        //This is where the stream code would go
        mSubscriber = new Subscriber(this, stream);
        mSubscriber.setSubscriberListener(this);
        mSubscriber.setVideoListener(this);
        mSession.subscribe(mSubscriber);
        mCastButton.setText(CASTING_TEXT);
        isSubscribed = true;
    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {
        Log.e(LOG_TAG, "Session ID Dropped: " + session.getSessionId());
        Log.e(LOG_TAG, "Stream ID Dropped: " + stream.getStreamId());
        Log.e(LOG_TAG, "Stream Name: " + stream.getName());
    }

    @Override
    public void onError(Session session, OpentokError opentokError) {
        Log.e(LOG_TAG, "Session ID Error: " + session.getSessionId());
        Log.e(LOG_TAG, "Open Tok Error Code: " + opentokError.getErrorCode().getErrorCode());
        Log.e(LOG_TAG, "Open Tok Error Message: " + opentokError.getMessage());
        Toast toast = Toast.makeText(this, "Session Error: " + opentokError.getMessage(),
                Toast.LENGTH_SHORT);
        toast.show();
        //isCasting = false;
        //reset();
    }

    @Override
    public void onSignalReceived(Session session, String s, String s2, Connection connection) {
        Log.e(LOG_TAG, "Signal Received");
    }

    @Override
    public void onStreamHasAudioChanged(Session session, Stream stream, boolean b) {
        Log.e(LOG_TAG, "Stream Audio Changed");
    }

    @Override
    public void onStreamHasVideoChanged(Session session, Stream stream, boolean b) {
        Log.e(LOG_TAG, "Stream Video Changed");
    }

    @Override
    public void onStreamVideoDimensionsChanged(Session session, Stream stream, int i, int i2) {
        Log.e(LOG_TAG, "Stream Video Dimensions Changed");
    }

    @Override
    public void onConnected(SubscriberKit subscriberKit) {
        Log.e(LOG_TAG, "Subscriber Connected");
    }

    @Override
    public void onDisconnected(SubscriberKit subscriberKit) {
        Log.e(LOG_TAG, "Subscriber Disconnected");

    }

    @Override
    public void onError(SubscriberKit subscriberKit, OpentokError opentokError) {
        Log.e(LOG_TAG, "Subscriber Error");
    }

    @Override
    public void onVideoDataReceived(SubscriberKit subscriberKit) {
        Log.e(LOG_TAG, "Video Data Recieved");
        mLogoView.setVisibility(View.INVISIBLE);
        //We want to inflate the view
        mConsumerView.addView(mSubscriber.getView());
        subscriberKit.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE,BaseVideoRenderer.STYLE_VIDEO_FILL);

    }

    @Override
    public void onVideoDisabled(SubscriberKit subscriberKit, String s) {
        Log.e(LOG_TAG, "Video Disabled");
    }

    @Override
    public void onVideoEnabled(SubscriberKit subscriberKit, String s) {
        Log.e(LOG_TAG, "Video Enabled");
    }

    @Override
    public void onVideoDisableWarning(SubscriberKit subscriberKit) {
        Log.e(LOG_TAG, "Video Disabled Warning");
    }

    @Override
    public void onVideoDisableWarningLifted(SubscriberKit subscriberKit) {
        Log.e(LOG_TAG, "Video Disabled Warning Lifted");
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId)
    {
        Log.e(LOG_TAG,"Item Selected");
        return false;
    }


    private void consumeTok() {
        mSession = null;
        //Setup the session with the API key and Session ID
        //TODO move to parse
        //mSession = new Session(this, GenericConstants.TOK_API_KEY, GenericConstants.TOK_SESSION_ID);
        mSession.setConnectionListener(this);
        mSession.setSessionListener(this);
        mSession.setSignalListener(this);
        mSession.setStreamPropertiesListener(this);
        mSession.connect(GenericConstants.TOK_TOKEN);
    }

    private void reset()
    {
        if (mSession != null) {
            mSession.disconnect();

        }
        mSession = null;
        mSubscriber = null;
        mConsumerView.removeAllViews();
        mCastButton.setText(NOT_CASTING_TEXT);
        isSubscribed = false;
        mLogoView.setVisibility(View.VISIBLE);
    }

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
}
