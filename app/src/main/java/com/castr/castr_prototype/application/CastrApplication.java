package com.castr.castr_prototype.application;

import android.app.Application;

import com.castr.castr_prototype.config.GenericConstants;
import com.castr.castr_prototype.model.CastrBroadcast;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;

import streamsource.TokSource;

/**
 * Created by Matthew on 10/14/2014.
 */
public class CastrApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        TokSource.init(this);
    }
}
