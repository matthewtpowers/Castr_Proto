package com.castr.castr_prototype.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;


import com.castr.castr_prototype.R;
import com.castr.castr_prototype.streamsource.WowzaSource;
import net.majorkernelpanic.streaming.gl.SurfaceView;

//TODO this entire class is a TODO, this is not as well constructed as the Consumer and Producer Activity
//Need to rethink the surface view and how that is handled.
public class TestWowza extends Activity implements WowzaSource.SourceCallback  {

    // log tag
    public final static String LOG_TAG = TestWowza.class.getSimpleName();

    // surfaceview
    private static SurfaceView mSurfaceView;

    //Wowza Source
    private WowzaSource mWowzaSource;

    //RelativeLayout
    private RelativeLayout mCanvasLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);

        //mSurfaceView = (SurfaceView) findViewById(R.id.surface);

        //mSurfaceView.getHolder().addCallback(this);

        // Initialize RTSP client
        //initRtspClient();
        mCanvasLayout = (RelativeLayout)findViewById(R.id.surface_layout);
        mWowzaSource = new WowzaSource(this,this);
        mWowzaSource.connectToPublish(1, "Some Title");

    }

    @Override
    protected void onResume() {
        if(mWowzaSource != null)
        {
            mWowzaSource.resume();
        }
        super.onResume();
    }

    @Override
    protected void onPause(){
        if(mWowzaSource != null)
        {
            mWowzaSource.pause();
        }
        super.onPause();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mWowzaSource != null)
        {
            mWowzaSource.endBroadcast();
        }
        //mSurfaceView.getHolder().removeCallback(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public void sessionCreated() {
        Log.e(LOG_TAG,"Session Created");

            Log.e(LOG_TAG,"Publishing Stream");
        if(mCanvasLayout == null)
        {
            Log.e(LOG_TAG,"Canvas is null");
        }
            mWowzaSource.publishStream(mCanvasLayout,"some name");
            //mWowzaSource.publishStream(mSurfaceView,"Something");

    }

    @Override
    public void isLive() {
        Log.e(LOG_TAG,"Is Live");
    }

    @Override
    public void sessionTerminated() {
        Log.e(LOG_TAG,"Session Terminated");
    }

    @Override
    public void onError(int code, String msg) {
        Log.i("We got an issue: ",msg);
        //TODO need to implement error Logic
        switch(code)
        {
            case WowzaSource.ERROR_AUTH:
                break;
            case WowzaSource.ERROR_BROADCASTING:
                break;
            case WowzaSource.ERROR_CAMERA:
                break;
            case WowzaSource.ERROR_CONNECTING:
                break;
            case WowzaSource.ERROR_CONSUMING:
                break;
            case WowzaSource.ERROR_OTHER:
                break;

        }
    }


}
