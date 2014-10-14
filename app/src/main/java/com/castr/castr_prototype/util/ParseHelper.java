package com.castr.castr_prototype.util;

import android.util.Log;

import com.castr.castr_prototype.model.CastrBroadcast;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Matthew on 10/13/2014.
 * Intended to build the objects for Castr
 */
public class ParseHelper {

    private static final String LOG_TAG = ParseHelper.class.getSimpleName();

    //Cloud Function Method Names
    public static final String METHOD_BROADCAST_TOKEN = "getBroadcastToken";
    public static final String METHOD_RANDOM_CAST = "getRandomCast";

    //Cloud Method Fields
    public static final String FIELD_BROADCAST_ID = "broadcast";


    /**
     * Create the broadcast object with a call back
     * The broadcast object has an objectID which is really the broadcast ID that
     * we use on the backend to generate the token that we would need to create the session
     *
     * @param type
     * @param title
     * @param sc
     * @return
     */
    public static CastrBroadcast createBroadcast(int type, String title, SaveCallback sc) {
        CastrBroadcast obj = new CastrBroadcast();
        obj.setBroadcastType(type);
        obj.setOwner(ParseUser.getCurrentUser());
        obj.setTitle(title);
        obj.saveInBackground(sc);
        return obj;
    }

    /**
     * End the broadcast in Parse, this should be part of a reset method
     * @param obj
     */
    public static void endBroadcast(CastrBroadcast obj)
    {
        obj.setEndDate(new Date());
        obj.saveInBackground();
    }

    public static void getAvailableBroadcasts(FindCallback<CastrBroadcast> callback)
    {
        ParseQuery<CastrBroadcast> query = ParseQuery.getQuery(CastrBroadcast.BROADCAST_KEY);
        query.whereDoesNotExist(CastrBroadcast.BROADCAST_END_DATE_KEY);
        query.findInBackground(callback);
    }

    public static void getAccessToken(String id, FunctionCallback<Object> callback)
    {
        //Create a map that represents the json
        Map<String, String> fields = new HashMap<String,String>();
        //A broadcast ID is really the object Id on the record
        fields.put(FIELD_BROADCAST_ID,id);
        ParseCloud.callFunctionInBackground(METHOD_BROADCAST_TOKEN,fields,callback);
    }
}

