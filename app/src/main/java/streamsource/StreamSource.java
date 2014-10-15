package streamsource;

import android.content.Context;
import android.widget.RelativeLayout;

import com.castr.castr_prototype.config.GenericConstants;
import com.castr.castr_prototype.model.CastrBroadcast;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;

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

    public interface SourceCallback{
        abstract void sessionCreated();
        abstract void isLive();
        abstract void sessionTerminated();
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

    public abstract void connect(int type, String title);

    //Making the assumption that you always are going to need a view to render the video on
    public abstract void publishStream(RelativeLayout layout);

    //Making the assumption that you always are going to need a view to render the video on
    public abstract void consumeStream(RelativeLayout layout);

    //Broadcasts will need to be handled when there is an onPause event
    public abstract void pauseBroadcast();

    //Similarly for onResume
    public abstract void resumeBroadcast();

    //Ability to end a broadcast
    public abstract void endBroadcast();

    //Ability to Pause Consumption
    public abstract void pauseConsumption();

    //Resume consumption
    public abstract void resumeConsumption();

    //End consumption
    public abstract void endConsumption();

}
