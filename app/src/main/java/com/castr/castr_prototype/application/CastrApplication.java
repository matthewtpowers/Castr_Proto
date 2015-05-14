package com.castr.castr_prototype.application;

import android.app.Application;

import com.castr.castr_prototype.streamsource.TokSource;

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
