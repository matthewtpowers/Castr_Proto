package com.castr.castr_prototype.streamsource;

import android.content.Context;
import android.widget.RelativeLayout;

import com.castr.castr_prototype.config.GenericConstants;
import com.castr.castr_prototype.model.CastrBroadcast;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by Matthew on 10/15/2014.
 *
 * There is a possibility that TokBox as we know it goes away, this is an effort to decouple
 * the activity from the implementation of the logic for connecting.  I would imagine
 * that if a third party service though does end up being needed it would still run through Parse
 *
 * TODO - at some point may want to decouple publishers and subscribers
 */
public abstract class StreamSource {

    /** Message for any non auth errors connecting to the service provider **/
    public static final int ERROR_CONNECTING = 1000;

    /** Message for any errors while in a broadcasting session **/
    public static final int ERROR_BROADCASTING = 1001;

    /** Message for any errors while in a data receiving session **/
    public static final int ERROR_CONSUMING = 1002;

    /** Message for any errors authenticating **/
    public static final int ERROR_AUTH = 1003;

    /** Message for issues with the camera **/
    public static final int ERROR_CAMERA = 1004;

    /** Message for any other errors **/
    public static final int ERROR_OTHER = 1005;




    /**
     * Callbacks for the publishers
     */
    public interface SourceCallback{
        abstract void sessionCreated();
        abstract void isLive();
        abstract void sessionTerminated();
        abstract void onError(int code, String msg);
        //TODO - add error handling
        /**
         * Proposed Error Codes
         * 1) Error connecting
         * 2) Error broadcasting
         * 3) Error consuming
         * 4) Error Authing
         * 5) Error Other
         *
         */
    }



    /**
     * This init implementation is going to need to be here. I don't envision tokbox getting
     * ripped out
     */
    public static void init(Context ctx)
    {
        //Register Subclasses
        ParseObject.registerSubclass(CastrBroadcast.class);
        //Setup parse
        Parse.initialize(ctx, GenericConstants.PARSE_APP_ID, GenericConstants.PARSE_CLIENT_ID);

        //TODO - replace this with real users at some point
        //This is an annonymous user
        ParseUser.enableAutomaticUser();
        ParseUser.getCurrentUser().increment("RunCount");
        ParseUser.getCurrentUser().saveInBackground();
    }

    /*
    Shared Methods
     */
    //Similarly for onResume
    public abstract void resume();

    //Will need to be handled when there is an onPause event
    public abstract void pause();

    public abstract void connectToPublish(int type, String title);

    /*
   Broadcast specific methods
    */

    //Making the assumption that you always are going to need a view to render the video on
    public abstract void publishStream(RelativeLayout layout, String name);


    //Ability to end a broadcast
    public abstract void endBroadcast();


    /*
    Consumption specific methods
     */
    //Connect logic is different than publishing
    public abstract void connectToStream(CastrBroadcast cast);

    //Making the assumption that you always are going to need a view to render the video on
    public abstract void consumeStream(RelativeLayout layout);

    //End consumption
    public abstract void endConsumption();


}
