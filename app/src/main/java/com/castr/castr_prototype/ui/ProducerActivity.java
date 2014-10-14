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
import android.widget.Toast;

import com.castr.castr_prototype.R;
import com.castr.castr_prototype.config.GenericConstants;
import com.castr.castr_prototype.util.ParseHelper;
import com.opentok.android.BaseVideoRenderer;
import com.opentok.android.Connection;
import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.parse.FunctionCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

public class ProducerActivity extends Activity implements Session.ConnectionListener, Session.SignalListener, Session.StreamPropertiesListener, Session.SessionListener, Publisher.PublisherListener,
        View.OnClickListener {

    private static final String LOG_TAG = ProducerActivity.class.getSimpleName();

    private static final String PUBLISHER_NAME = "applico_publisher";
    private static final String NOT_CASTING_TEXT = "Start Casting!";
    private static final String CASTING_TEXT = "Stop Casting..";

    //UI Related Elements
    private RelativeLayout mPubView;
    private Button mCastButton;
    private ImageView mLogoView;

    //TokBox Elements
    private Session mSession;
    private Publisher mPublisher;

    //Boolean for if we are live
    private boolean isCasting = false;

    private ParseObject mBroadcastObj;

    //TODO move the connection activity off of the main thread.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producer);
        mPubView = (RelativeLayout)findViewById(R.id.publisherview);
        mCastButton = (Button)findViewById(R.id.cast_button);
        mCastButton.setOnClickListener(this);
        mLogoView = (ImageView)findViewById(R.id.logo);

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
        Log.e(LOG_TAG,"On Resume");
        if(mSession != null)
        {
            mSession.onResume();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.e(LOG_TAG,"On Pause");
        if(mSession != null)
        {
            mSession.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy()
    {
        Log.e(LOG_TAG,"On Destroy");
        reset();

        super.onDestroy();
        finish();
    }


    @Override
    public void onConnectionCreated(Session session, Connection connection) {
        Log.e(LOG_TAG,"Connection Listener Connect ID: " + session.getSessionId());
        Log.e(LOG_TAG,"Connection Listener Creation Time: " + connection.getCreationTime());
        Log.e(LOG_TAG,"Connection Listener Connection ID: " + connection.getConnectionId());
    }

    //Connection Listener Method
    @Override
    public void onConnectionDestroyed(Session session, Connection connection) {
        Log.e(LOG_TAG,"Connection Destroyed Connect ID: " + session.getSessionId());
        Log.e(LOG_TAG,"Connection Destroyed Creation Time: " + connection.getCreationTime());
        Log.e(LOG_TAG,"Connection Destroyed Connection ID: " + connection.getConnectionId());
    }

    //Signal Listener Method
    @Override
    public void onSignalReceived(Session session, String s, String s2, Connection connection) {
        Log.e(LOG_TAG,"Signal Received");
    }

    //Stream Listener Method
    @Override
    public void onStreamHasAudioChanged(Session session, Stream stream, boolean b) {
        Log.e(LOG_TAG,"Stream Audio Changed");
    }

    //Stream Listener Method
    @Override
    public void onStreamHasVideoChanged(Session session, Stream stream, boolean b) {
        Log.e(LOG_TAG,"Stream Video Changed");
    }

    //Stream Listener Method
    @Override
    public void onStreamVideoDimensionsChanged(Session session, Stream stream, int i, int i2) {
        Log.e(LOG_TAG,"Stream Video Dimensions Changed");
    }

    //Session Listener Method
    @Override
    public void onConnected(Session session) {
         Log.e(LOG_TAG,"Session Connect ID: " + session.getSessionId());
        mPublisher = null;
        mPublisher = new Publisher(this,PUBLISHER_NAME);
        mPublisher.setPublisherListener(this);
        mPublisher.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE,BaseVideoRenderer.STYLE_VIDEO_FILL);
        //Attach the view
        mPubView.addView(mPublisher.getView());
        mSession.publish(mPublisher);



    }

    //Session Listener Method
    @Override
    public void onDisconnected(Session session) {
        Log.e(LOG_TAG,"Session Disconnect ID: " + session.getSessionId());
        reset();
    }

    //Session Listener Method
    @Override
    public void onStreamReceived(Session session, Stream stream) {
        Log.e(LOG_TAG,"Session ID Received: " + session.getSessionId());
        Log.e(LOG_TAG,"Stream ID Recieved: " + stream.getStreamId());
        Log.e(LOG_TAG,"Stream Name: " + stream.getName());

    }

    //Session Listener Method
    @Override
    public void onStreamDropped(Session session, Stream stream) {
        Log.e(LOG_TAG,"Session ID Dropped: " + session.getSessionId());
        Log.e(LOG_TAG,"Stream ID Dropped: " + stream.getStreamId());
        Log.e(LOG_TAG,"Stream Name: " + stream.getName());

    }

    //Session Listener Method
    @Override
    public void onError(Session session, OpentokError opentokError) {
        Log.e(LOG_TAG,"Session ID Error: " + session.getSessionId());
        Log.e(LOG_TAG,"Open Tok Error Code: " + opentokError.getErrorCode().getErrorCode());
        Log.e(LOG_TAG, "Open Tok Error Message: " + opentokError.getMessage());
        Toast toast = Toast.makeText(this, "Session Error: " + opentokError.getMessage(),
                Toast.LENGTH_SHORT);
        toast.show();
        isCasting = false;
        reset();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId())
        {

            case R.id.cast_button:
                if(!isCasting) {
                    Log.e(LOG_TAG,"Connect to Tok");
                    //connectToTok();
                    createSession();
                }
                else
                {
                    Log.e(LOG_TAG,"Disconnect from Tok");
                    disconnectFromTok();
                }
                break;
        }
    }

    @Override
    public void onStreamCreated(PublisherKit publisherKit, Stream stream) {
           Log.e(LOG_TAG,"Stream created");
           mCastButton.setText(CASTING_TEXT);
           isCasting = true;
           mLogoView.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {
           Log.e(LOG_TAG,"Stream Destroyed");
           reset();
    }

    @Override
    public void onError(PublisherKit publisherKit, OpentokError opentokError) {
            Log.e(LOG_TAG,"Stream Error");
            reset();
    }



    private void connectToTok()
    {

        mSession = null;
        //Setup the session with the API key and Session ID
        //TODO move to parse
        Log.e(LOG_TAG,"Session ID: " + mBroadcastObj.get("sessionId"));
        final String sessionId = mBroadcastObj.getString(ParseHelper.BROADCAST_SESSION_ID_KEY);
        Log.e(LOG_TAG,"Object ID: " + mBroadcastObj.getObjectId());
        //Get the token by passing in the OBJECT_ID from parse - not the session ID.  The objectID is uniquely created by parse.
        ParseHelper.getAccessToken(mBroadcastObj.getObjectId(), new FunctionCallback<Object>() {
                    @Override
                    public void done(Object o, ParseException e) {

                        if(e == null)
                        {
                            String token = o.toString();
                            Log.e(LOG_TAG,"Token: " + token);
                            if(token != null) {
                                connectToSession(token, sessionId);
                            }
                            else
                            {
                                Log.e(LOG_TAG,"There is an issue generating TokBox tokens");
                            }
                        }
                        else
                        {
                            Log.e(LOG_TAG,"Exception: " + e.getLocalizedMessage());

                        }
                    }
        });





    }

    //Connect to parse and get the session ID
    private void createSession()
    {
        mBroadcastObj = ParseHelper.createBroadcast(1,"Android Rules", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null){
                    Log.e(LOG_TAG,"No exception");
                    connectToTok();
                }
                else
                {
                    Log.e(LOG_TAG,"Exception with Parse");
                    Log.e(LOG_TAG,"Exception: " + e.getLocalizedMessage());
                }
            }
        });
    }

    //After creating the session, the session needs to be connected to, needs error handling
    private void connectToSession(String token, String sessionId)
    {
        mSession = new Session(this, GenericConstants.TOK_API_KEY, sessionId);
        mSession.setConnectionListener(this);
        mSession.setSessionListener(this);
        mSession.setSignalListener(this);
        mSession.setStreamPropertiesListener(this);
        mSession.connect(token);
    }



    private void disconnectFromTok()
    {
        if(mSession != null)
        {
            mSession.disconnect();
            reset();
            mPubView.removeAllViews();
        }
    }

    public void reset()
    {
        if(mSession != null)
        {
            mSession.disconnect();

        }
        isCasting = false;
        mSession = null;
        mPublisher = null;
        mLogoView.setVisibility(View.VISIBLE);
        mCastButton.setText(NOT_CASTING_TEXT);
        if(mBroadcastObj != null)
        {
            ParseHelper.endBroadcast(mBroadcastObj);
            Log.e(LOG_TAG,"Ended Broadcast");
        }


    }

}
