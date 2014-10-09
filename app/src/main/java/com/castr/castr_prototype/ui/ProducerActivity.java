package com.castr.castr_prototype.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.castr.castr_prototype.R;
import com.opentok.android.Connection;
import com.opentok.android.OpentokError;
import com.opentok.android.Session;
import com.opentok.android.Stream;

public class ProducerActivity extends Activity implements Session.ConnectionListener, Session.SignalListener, Session.StreamPropertiesListener, Session.SessionListener {

    private static final String LOG_TAG = ProducerActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producer);
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


    //Connection Listener Method
    @Override
    public void onConnectionCreated(Session session, Connection connection) {

    }

    //Connection Listener Method
    @Override
    public void onConnectionDestroyed(Session session, Connection connection) {

    }

    //Signal Listener Method
    @Override
    public void onSignalReceived(Session session, String s, String s2, Connection connection) {

    }

    //Stream Listener Method
    @Override
    public void onStreamHasAudioChanged(Session session, Stream stream, boolean b) {

    }

    //Stream Listener Method
    @Override
    public void onStreamHasVideoChanged(Session session, Stream stream, boolean b) {

    }

    //Stream Listener Method
    @Override
    public void onStreamVideoDimensionsChanged(Session session, Stream stream, int i, int i2) {

    }

    //Session Listener Method
    @Override
    public void onConnected(Session session) {

    }

    //Session Listener Method
    @Override
    public void onDisconnected(Session session) {

    }

    //Session Listener Method
    @Override
    public void onStreamReceived(Session session, Stream stream) {

    }

    //Session Listener Method
    @Override
    public void onStreamDropped(Session session, Stream stream) {

    }

    //Session Listener Method
    @Override
    public void onError(Session session, OpentokError opentokError) {

    }
}
