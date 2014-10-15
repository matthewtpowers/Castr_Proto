package com.castr.castr_prototype.streamsource;

import android.content.Context;
import android.util.Log;
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
import com.parse.FunctionCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * Created by Matthew on 10/15/2014.
 */
public class TokSource extends StreamSource implements Session.SessionListener, Publisher.PublisherListener, Session.StreamPropertiesListener{

    private static final String LOG_TAG = TokSource.class.getSimpleName();

    private Context mCtx;
    private SourceCallback mSourceCallback;

    //UI Elements
    private RelativeLayout mCanvasLayout;

    //Parse Elements
    private CastrBroadcast mBroadcastObj;

    //TokBox Elements
    private Session mSession;
    private Publisher mPublisher;

    //Publisher Name
    private String mPublisherName;


    /**
     * The constructor and this class are tightly coupled with Parse still
     *
     * @param ctx
     * @param sc
     */
    public TokSource(Context ctx, SourceCallback sc){
        mCtx = ctx;
        mSourceCallback = sc;
        mBroadcastObj = null;
        mSession = null;
        mPublisher = null;
        mPublisherName = ParseUser.getCurrentUser().getUsername();
     }


    /**
     * Create the TokBox session, then connect to Tok
     */
    @Override
    public void connect(int type, String title)
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
    public void publishStream(RelativeLayout layout){
        mCanvasLayout = layout;
        Log.i(LOG_TAG,"Session Connect ID: " + mSession.getSessionId());
        mPublisher = new Publisher(mCtx,mPublisherName);
        mPublisher.setPublisherListener(this);
        mPublisher.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE,BaseVideoRenderer.STYLE_VIDEO_FILL);
        //Attach the view
        layout.addView(mPublisher.getView());
        mSession.publish(mPublisher);

    }

    @Override
    public void consumeStream(RelativeLayout layout){

    }

    @Override
    public void pauseBroadcast()
    {
        if(mSession != null)
        {
            mSession.onPause();
        }
    }

    @Override
    public void resumeBroadcast()
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
    public void pauseConsumption()
    {

    }

    @Override
    public void resumeConsumption()
    {

    }

    @Override
    public void endConsumption()
    {

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
        mSession.setStreamPropertiesListener(this);
        mSession.connect(token);
    }


    /**
     * Once you are connected to the session you can publish or consume
     * @param session
     */
    @Override
    public void onConnected(Session session) {
        Log.i(LOG_TAG,"We are connected and about to publish the stream");
        //Signal the callback that we are connected and are ready to publish or consume
        mSourceCallback.sessionCreated();
    }

    @Override
    public void onDisconnected(Session session) {
        Log.e(LOG_TAG,"onDisconnected: " + session.getSessionId());
        mSourceCallback.sessionTerminated();
        endBroadcast();
    }

    @Override
    public void onStreamReceived(Session session, Stream stream) {
        Log.i(LOG_TAG,"onStreamReceived");
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
    public void onStreamHasAudioChanged(Session session, Stream stream, boolean b) {
        Log.i(LOG_TAG,"Stream Audio Has Changed");
    }

    @Override
    public void onStreamHasVideoChanged(Session session, Stream stream, boolean b) {
        Log.i(LOG_TAG,"Stream Video has Changed");
    }

    @Override
    public void onStreamVideoDimensionsChanged(Session session, Stream stream, int i, int i2) {
       Log.i(LOG_TAG,"Stream Video Dimensions Have Changed");
    }


}
