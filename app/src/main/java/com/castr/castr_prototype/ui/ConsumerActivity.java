package com.castr.castr_prototype.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.castr.castr_prototype.R;
import com.castr.castr_prototype.config.GenericConstants;
import com.opentok.android.BaseVideoRenderer;
import com.opentok.android.Connection;
import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;
import com.opentok.android.SubscriberKit;

public class ConsumerActivity extends Activity implements View.OnClickListener, Session.ConnectionListener, Session.SignalListener, Session.StreamPropertiesListener, Session.SessionListener, SubscriberKit.SubscriberListener,
        Subscriber.VideoListener {

    private static final String LOG_TAG = ConsumerActivity.class.getSimpleName();

    //UI Related Elements
    private RelativeLayout mConsumerView;
    private Button mCastButton;

    //Tokbox Elements
    private Session mSession;
    private Subscriber mSubscriber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumer);
        mConsumerView = (RelativeLayout)findViewById(R.id.publisherview);
        mCastButton = (Button)findViewById(R.id.consume_button);
        mCastButton.setOnClickListener(this);
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

    @Override
    public void onClick(View view) {

        switch(view.getId())
        {
            case R.id.consume_button:
                consumeTok();
                break;
        }

    }

    private void consumeTok()
    {
        mSession = null;
        //Setup the session with the API key and Session ID
        //TODO move to parse
        mSession = new Session(this, GenericConstants.TOK_API_KEY, GenericConstants.TOK_SESSION_ID);
        mSession.setConnectionListener(this);
        mSession.setSessionListener(this);
        mSession.setSignalListener(this);
        mSession.setStreamPropertiesListener(this);
        mSession.connect(GenericConstants.TOK_TOKEN);
    }

    @Override
    public void onConnectionCreated(Session session, Connection connection) {
        Log.e(LOG_TAG,"Connection Listener Connect ID: " + session.getSessionId());
        Log.e(LOG_TAG,"Connection Listener Creation Time: " + connection.getCreationTime());
        Log.e(LOG_TAG,"Connection Listener Connection ID: " + connection.getConnectionId());
    }

    @Override
    public void onConnectionDestroyed(Session session, Connection connection) {
        Log.e(LOG_TAG,"Connection Destroyed Connect ID: " + session.getSessionId());
        Log.e(LOG_TAG,"Connection Destroyed Creation Time: " + connection.getCreationTime());
        Log.e(LOG_TAG,"Connection Destroyed Connection ID: " + connection.getConnectionId());
    }

    @Override
    public void onConnected(Session session) {
        Log.e(LOG_TAG,"Session Connect ID: " + session.getSessionId());
    }

    @Override
    public void onDisconnected(Session session) {
        Log.e(LOG_TAG,"Session Disconnect ID: " + session.getSessionId());
        //reset();
    }


    //A stream is received when a stream is published to a session
    @Override
    public void onStreamReceived(Session session, Stream stream) {
        Log.e(LOG_TAG,"Session ID Received: " + session.getSessionId());
        Log.e(LOG_TAG,"Stream ID Recieved: " + stream.getStreamId());
        Log.e(LOG_TAG,"Stream Name: " + stream.getName());

        //This is where the stream code would go
        mSubscriber = new Subscriber(this,stream);
        mSubscriber.setSubscriberListener(this);
        mSubscriber.setVideoListener(this);
    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {
        Log.e(LOG_TAG,"Session ID Dropped: " + session.getSessionId());
        Log.e(LOG_TAG,"Stream ID Dropped: " + stream.getStreamId());
        Log.e(LOG_TAG,"Stream Name: " + stream.getName());
    }

    @Override
    public void onError(Session session, OpentokError opentokError) {
        Log.e(LOG_TAG,"Session ID Error: " + session.getSessionId());
        Log.e(LOG_TAG,"Open Tok Error Code: " + opentokError.getErrorCode().getErrorCode());
        Log.e(LOG_TAG, "Open Tok Error Message: " + opentokError.getMessage());
        Toast toast = Toast.makeText(this, "Session Error: " + opentokError.getMessage(),
                Toast.LENGTH_SHORT);
        toast.show();
        //isCasting = false;
        //reset();
    }

    @Override
    public void onSignalReceived(Session session, String s, String s2, Connection connection) {
        Log.e(LOG_TAG,"Signal Received");
    }

    @Override
    public void onStreamHasAudioChanged(Session session, Stream stream, boolean b) {
        Log.e(LOG_TAG,"Stream Audio Changed");
    }

    @Override
    public void onStreamHasVideoChanged(Session session, Stream stream, boolean b) {
        Log.e(LOG_TAG,"Stream Video Changed");
    }

    @Override
    public void onStreamVideoDimensionsChanged(Session session, Stream stream, int i, int i2) {
        Log.e(LOG_TAG,"Stream Video Dimensions Changed");
    }

    @Override
    public void onConnected(SubscriberKit subscriberKit) {
        Log.e(LOG_TAG,"Subscriber Connected");
    }

    @Override
    public void onDisconnected(SubscriberKit subscriberKit) {
        Log.e(LOG_TAG,"Subscriber Disconnected");
    }

    @Override
    public void onError(SubscriberKit subscriberKit, OpentokError opentokError) {
        Log.e(LOG_TAG,"Subscriber Error");
    }

    @Override
    public void onVideoDataReceived(SubscriberKit subscriberKit) {
        Log.e(LOG_TAG,"Video Data Recieved");
    }

    @Override
    public void onVideoDisabled(SubscriberKit subscriberKit, String s) {
        Log.e(LOG_TAG,"Video Disabled");
    }

    @Override
    public void onVideoEnabled(SubscriberKit subscriberKit, String s) {
        Log.e(LOG_TAG,"Video Enabled");
    }

    @Override
    public void onVideoDisableWarning(SubscriberKit subscriberKit) {
        Log.e(LOG_TAG,"Video Disabled Warning");
    }

    @Override
    public void onVideoDisableWarningLifted(SubscriberKit subscriberKit) {
        Log.e(LOG_TAG,"Video Disabled Warning Lifted");
    }
}
