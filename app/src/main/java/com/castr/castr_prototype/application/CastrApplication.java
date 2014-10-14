package com.castr.castr_prototype.application;

import android.app.Application;

import com.castr.castr_prototype.config.GenericConstants;
import com.castr.castr_prototype.model.CastrBroadcast;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by Matthew on 10/14/2014.
 */
public class CastrApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //Register Subclasses
        ParseObject.registerSubclass(CastrBroadcast.class);
        //Setup parse
        Parse.initialize(this, GenericConstants.PARSE_APP_ID, GenericConstants.PARSE_CLIENT_ID);

        //TODO - replace this with real users at some point
        //This is an annonymous user
        ParseUser.enableAutomaticUser();
        ParseUser.getCurrentUser().increment("RunCount");
        ParseUser.getCurrentUser().saveInBackground();
    }
}
