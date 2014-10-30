package com.castr.castr_prototype.streamsource;

import android.content.Context;
import android.util.Log;
import android.widget.RelativeLayout;

import com.castr.castr_prototype.config.WowzaConstants;
import com.castr.castr_prototype.model.CastrBroadcast;
import com.castr.castr_prototype.util.ParseHelper;

import net.majorkernelpanic.streaming.Session;
import net.majorkernelpanic.streaming.SessionBuilder;
import net.majorkernelpanic.streaming.audio.AudioQuality;
import net.majorkernelpanic.streaming.gl.SurfaceView;
import net.majorkernelpanic.streaming.rtsp.RtspClient;


/**
 * Created by Matthew on 10/27/2014.
 * This handles a Wowza source for streaming media
 */
public class WowzaSource extends StreamSource implements Session.Callback, RtspClient.Callback {

    private static final String LOG_TAG = WowzaSource.class.getSimpleName();

    private Context mCtx;
    private SourceCallback mSourceCallback;


    //Audio Quality
    private int mSampleQuality = 8000;
    private int mBitRate = 16000;

    //Video Encoding
    private int mVideoEncoding = SessionBuilder.VIDEO_H264;

    //TODO add this to the TokSource
    //Orientation for Surface View
    private int mLandscapeOrientation = 0;
    private int mPortraitOrientation = 1;

    // Rtsp session
    private Session mSession;
    private static RtspClient mClient;

    //Parse Elements
    private CastrBroadcast mBroadcastObj;

    //UI Elements - this is the canvas we are rendering on
    private RelativeLayout mCanvasLayout;
    private SurfaceView mSurfaceView;

    //TODO this needs to change and the stream will be associated with a userID from parse or sessionID
    private String mStreamPath = "/live/android_test";

    public WowzaSource(Context ctx, SourceCallback sc)
    {
        mCtx = ctx;
        mSurfaceView = null;
        mSession = null;
        mBroadcastObj = null;
        mSourceCallback = sc;


    }

    @Override
    public void resume() {
        toggleStreaming();
    }

    @Override
    public void pause() {

        toggleStreaming();
    }

    @Override
    public void connectToPublish(int type, String title) {
        //TODO get the broadcast object from Parse
        mBroadcastObj = null;
        mSourceCallback.sessionCreated();

    }

    @Override
    public void publishStream(RelativeLayout layout, String name) {

            Log.e(LOG_TAG,"Child Count: " + layout.getChildCount());

            //TODO this needs to be more elegant than forcing this to look for a surface view
            mSurfaceView = (SurfaceView)layout.getChildAt(0);

            mSession = SessionBuilder.getInstance()
                    .setContext(mCtx)
                    .setAudioEncoder(SessionBuilder.AUDIO_NONE)
                    .setAudioQuality(new AudioQuality(mSampleQuality, mBitRate))
                    .setVideoEncoder(mVideoEncoding)
                    .setSurfaceView(mSurfaceView).setPreviewOrientation(mPortraitOrientation)
                    .setCallback(this).build();

            // Configures the RTSP client
            mClient = new RtspClient();
            mClient.setSession(mSession);
            mClient.setCallback(this);

            mSurfaceView.setAspectRatioMode(SurfaceView.ASPECT_RATIO_PREVIEW);

            mClient.setCredentials(WowzaConstants.WOWZA_UN, WowzaConstants.WOWZA_PASS);
            mClient.setServerAddress(WowzaConstants.WOWZA_IP_ADDR, WowzaConstants.WOWZA_PORT);
            //TODO - change to real broadcast object if we end up routing parse through this
            //Log.i(LOG_TAG,"Path: " + mBroadcastObj.getString(CastrBroadcast.BROADCAST_SESSION_ID_KEY));
            mClient.setStreamPath(mStreamPath);
            // Start camera preview
            mSession.startPreview();
            // Start video stream
            mClient.startStream();

    }

    @Override
    public void endBroadcast() {
        if(mSession != null)
        {
            mSession.stop();
            mSession.release();
            mSession = null;
        }

        if(mClient != null)
        {
            mClient.stopStream();
            mClient.release();
            mClient = null;
        }
        if(mBroadcastObj != null)
        {
            ParseHelper.endBroadcast(mBroadcastObj);
        }
    }

    @Override
    public void connectToStream(CastrBroadcast cast) {

        //TODO - this may end up being a simple HTTP call
    }

    @Override
    public void consumeStream(RelativeLayout layout) {
        //TODO - this may end up being a simple HTTP call
    }

    @Override
    public void endConsumption() {

        //TODO this may just be as simple as cleaning up the HTTP session.
    }

    /*
    Begin of Session Callback code.
     */

    @Override
    public void onBitrateUpdate(long bitrate) {
        Log.i(LOG_TAG,"Bitrate Update: " + bitrate);

    }

    @Override
    public void onSessionError(int reason, int streamType, Exception e) {
        Log.e(LOG_TAG,"onError");
        Log.e(LOG_TAG, "Reason: " + reason);
        Log.e(LOG_TAG,"Stream Type: " + streamType);
        Log.e(LOG_TAG,"Exception: " + e.getMessage());
        handleWowzaError(reason, e.getMessage());
    }

    @Override
    public void onPreviewStarted() {
        Log.i(LOG_TAG,"Preview Started");

    }

    @Override
    public void onSessionConfigured() {
        Log.i(LOG_TAG, "We are connected and about to publish/subscribe the stream");
        //Signal the callback that we are connected and are ready to publish or consume
        mSourceCallback.sessionCreated();

    }

    @Override
    public void onSessionStarted() {
        Log.i(LOG_TAG,"The Session is Live");
        //Signal the callback that we are connected and are ready to publish or consume
        mSourceCallback.isLive();

    }

    @Override
    public void onSessionStopped() {
        Log.e(LOG_TAG,"onStreamDropped");
        mSourceCallback.sessionTerminated();

    }

    @Override
    public void onRtspUpdate(int message, Exception exception) {
        Log.i(LOG_TAG,"Msg: " + message);
        Log.i(LOG_TAG,"Exception: " + exception.getMessage());
        handleRTSPError(message, exception.getMessage());
    }

    /**
     * Toggle streaming on onPause and onResume by default
     */
    private void toggleStreaming()
    {
        if(mClient != null && mSession != null) {
            if (!mClient.isStreaming()) {
                // Start camera preview
                mSession.startPreview();

                // Start video stream
                mClient.startStream();
            } else {
                // already streaming, stop streaming
                // stop camera preview
                mSession.stopPreview();

                // stop streaming
                mClient.stopStream();
            }
        }
    }


    /**
     * Handle the Wowza Session Errors
     * @param reason
     * @param msg
     */
    private void handleWowzaError(int reason, String msg)
    {
        //TODO need to make sure some of the error code ints don't overlap

        Log.e(LOG_TAG,"Error with Wowza or RTSP Client");

        switch (reason) {

            case Session.ERROR_CAMERA_ALREADY_IN_USE:
                mSourceCallback.onError(ERROR_CAMERA,msg);
                break;
            case Session.ERROR_CAMERA_HAS_NO_FLASH:
                mSourceCallback.onError(ERROR_CAMERA,msg);
                break;
            case Session.ERROR_INVALID_SURFACE:
                mSourceCallback.onError(ERROR_OTHER,msg);
                break;
            case Session.ERROR_STORAGE_NOT_READY:
                mSourceCallback.onError(ERROR_OTHER,msg);
                break;
            case Session.ERROR_CONFIGURATION_NOT_SUPPORTED:
                mSourceCallback.onError(ERROR_AUTH,msg);
                break;
            case Session.ERROR_UNKNOWN_HOST:
                mSourceCallback.onError(ERROR_CONNECTING,msg);
                break;
            case Session.ERROR_OTHER:
                mSourceCallback.onError(ERROR_OTHER,msg);
                break;
        }
    }

    private void handleRTSPError(int reason, String msg)
    {
        switch (reason) {
            case RtspClient.ERROR_CONNECTION_FAILED:
                mSourceCallback.onError(ERROR_CONNECTING,msg);
                break;
            case RtspClient.ERROR_CONNECTION_LOST:
                mSourceCallback.onError(ERROR_CONNECTING,msg);
                break;
            case RtspClient.ERROR_WRONG_CREDENTIALS:
                mSourceCallback.onError(ERROR_AUTH,msg);
                break;
        }

    }




}
