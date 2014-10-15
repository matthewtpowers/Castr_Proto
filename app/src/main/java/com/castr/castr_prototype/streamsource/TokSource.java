package com.castr.castr_prototype.streamsource;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;

import com.castr.castr_prototype.config.GenericConstants;
import com.castr.castr_prototype.model.CastrBroadcast;
import com.castr.castr_prototype.util.ParseHelper;
import com.opentok.android.BaseVideoRenderer;
import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;
import com.opentok.android.SubscriberKit;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matthew on 10/15/2014.
 */
public class TokSource extends StreamSource implements Session.SessionListener, Publisher.PublisherListener, SubscriberKit.VideoListener{

    private static final String LOG_TAG = TokSource.class.getSimpleName();

    private Context mCtx;
    private SourceCallback mSourceCallback;

    //UI Elements - this is the canvas we are rendering on
    private RelativeLayout mCanvasLayout;

    //Parse Elements
    private CastrBroadcast mBroadcastObj;

    //TokBox Elements
    private Session mSession;
    private Publisher mPublisher;
    private Subscriber mSubscriber;


    /**
     * The constructor and this class are tightly coupled with Parse still
     *
     * @param ctx
     * @param sc
     */
    public TokSource(Context ctx, SourceCallback sc){
        mCtx = ctx;
        mSession = null;
        mSourceCallback = sc;
        mBroadcastObj = null;
        mPublisher = null;
        mSubscriber = null;
    }

    /*public TokSource(Context ctx, SubscriberCallback sc, CastrBroadcast obj)
    {
        mCtx = ctx;
        mSession = null;
        mSubscriberCallback = sc;
        mBroadcastObj = obj;
        mSourceCallback = mSubscriberCallback;
    }*/


    /**
     * Create the TokBox session, then connect to Tok
     */
    @Override
    public void connectToPublish(int type, String title)
    {
        mBroadcastObj = ParseHelper.createBroadcast(type, title, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    //Got the session info, connect to Tok
                    connectToTok();
                } else {
                    Log.e(LOG_TAG, "Exception with Parse");
                    Log.e(LOG_TAG, "Exception: " + e.getLocalizedMessage());
                }
            }
        });
    }

    /**
     * Publishing the stream should turn on your layout and allow the canvas to be drawn to
     * @param layout
     */
    @Override
    public void publishStream(RelativeLayout layout, String name){
        mCanvasLayout = layout;
        Log.i(LOG_TAG,"Session Connect ID: " + mSession.getSessionId());
        mPublisher = new Publisher(mCtx,name);
        mPublisher.setPublisherListener(this);
        mPublisher.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE,BaseVideoRenderer.STYLE_VIDEO_FILL);
        //Attach the view
        layout.addView(mPublisher.getView());
        mSession.publish(mPublisher);

    }


    @Override
    public void pause()
    {
        if(mSession != null)
        {
            mSession.onPause();
        }
    }

    @Override
    public void resume()
    {
        if(mSession != null)
        {
            mSession.onResume();
        }
    }

    @Override
    public void endBroadcast(){
        if(mSession != null)
        {
            mSession.disconnect();

        }
        mSession = null;
        mPublisher = null;
        if(mBroadcastObj != null)
        {
            ParseHelper.endBroadcast(mBroadcastObj);
        }
        if(mCanvasLayout != null) {
            mCanvasLayout.removeAllViews();
        }
    }

    @Override
    public void connectToStream(CastrBroadcast cast)
    {
        mBroadcastObj = cast;
        connectToTok();
    }

    @Override
    public void consumeStream(RelativeLayout layout){
        Log.e(LOG_TAG,"Consuming the Stream by setting the video listener and the layout");
        if(mSubscriber != null)
        {
            mCanvasLayout = layout;
            mSubscriber.setVideoListener(this);

        }
        /*mSubscriber = new Subscriber(this, stream);
        mSubscriber.setSubscriberListener(this);
        mSubscriber.setVideoListener(this);
        mSession.subscribe(mSubscriber);
        mCastButton.setText(CASTING_TEXT);
        */

    }

    @Override
    public void endConsumption()
    {
        if(mSession != null)
        {
            mSession.disconnect();

        }
        mSession = null;
        mSubscriber = null;
        if(mCanvasLayout != null) {
            mCanvasLayout.removeAllViews();
        }
    }


    /**
     * 1) Create a Session, through parse with valid user
     * 2) Get the token
     * 3) Connect to the session
     * 4) Setup the appropriate listeners
     */
    private void connectToTok(){
        //Setup the session with the API key and Session ID
        Log.i(LOG_TAG,"Session ID: " + mBroadcastObj.get("sessionId"));
        final String sessionId = mBroadcastObj.getString(CastrBroadcast.BROADCAST_SESSION_ID_KEY);
        Log.i(LOG_TAG,"Object ID: " + mBroadcastObj.getObjectId());
        //Get the token by passing in the OBJECT_ID from parse - not the session ID.  The objectID is uniquely created by parse.
        ParseHelper.getAccessToken(mBroadcastObj.getObjectId(), new FunctionCallback<Object>() {
            @Override
            public void done(Object o, ParseException e) {

                if(e == null)
                {
                    String token = o.toString();
                    Log.i(LOG_TAG,"Token: " + token);
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

    /**
     * After creating the session, the session needs to be connected to, needs error handling
     */
    private void connectToSession(String token, String sessionId)
    {
        mSession = new Session(mCtx, GenericConstants.TOK_API_KEY, sessionId);
        //The connection listener is irrelevant if you have the Session listener
        //mSession.setConnectionListener(this);
        mSession.setSessionListener(this);
        mSession.connect(token);
    }


    /**
     * Once you are connected to the session you can publish or consume
     * @param session
     */
    @Override
    public void onConnected(Session session) {
        Log.i(LOG_TAG,"We are connected and about to publish/subscribe the stream");
        //Signal the callback that we are connected and are ready to publish or consume
        mSourceCallback.sessionCreated();
    }

    @Override
    public void onDisconnected(Session session) {
        Log.e(LOG_TAG,"onDisconnected: " + session.getSessionId());
        mSourceCallback.sessionTerminated();
        endBroadcast();
    }

    /**
     * This is fired when ANOTHER client publishes a stream, we will use this for the subscriber to listen
     * @param session
     * @param stream
     */
    @Override
    public void onStreamReceived(Session session, Stream stream) {
        Log.e(LOG_TAG, "Session ID Received: " + session.getSessionId());
        Log.e(LOG_TAG, "Stream ID Recieved: " + stream.getStreamId());
        Log.e(LOG_TAG, "Stream Name: " + stream.getName());

        mSubscriber = new Subscriber(mCtx, stream);
        //mSubscriber.setVideoListener(this);
        mSession.subscribe(mSubscriber);
        mSourceCallback.isLive();
        //mCastButton.setText(CASTING_TEXT);
        //isSubscribed = true;
    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {
        Log.e(LOG_TAG,"onStreamDropped");
    }

    @Override
    public void onError(Session session, OpentokError opentokError) {
        Log.e(LOG_TAG,"onError");
    }

    @Override
    public void onStreamCreated(PublisherKit publisherKit, Stream stream) {
        Log.e(LOG_TAG,"onStreamCreated");
        mSourceCallback.isLive();
    }

    @Override
    public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {
        Log.e(LOG_TAG,"onStreamDestroyed");
    }

    @Override
    public void onError(PublisherKit publisherKit, OpentokError opentokError) {
        Log.e(LOG_TAG,"onError");
    }


    @Override
    public void onVideoDataReceived(SubscriberKit subscriberKit) {
        Log.e(LOG_TAG, "Video Data Recieved");
        mCanvasLayout.addView(mSubscriber.getView());
        subscriberKit.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE,BaseVideoRenderer.STYLE_VIDEO_FILL);
    }

    @Override
    public void onVideoDisabled(SubscriberKit subscriberKit, String s) {

    }

    @Override
    public void onVideoEnabled(SubscriberKit subscriberKit, String s) {

    }

    @Override
    public void onVideoDisableWarning(SubscriberKit subscriberKit) {

    }

    @Override
    public void onVideoDisableWarningLifted(SubscriberKit subscriberKit) {

    }
}
