package com.castr.castr_prototype.model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;

/**
 * Created by Matthew on 10/14/2014.
 * Parse class for broadcasts
 */
@ParseClassName(CastrBroadcast.BROADCAST_KEY)
public class CastrBroadcast extends ParseObject {

    //Broadcast Class
    public static final String BROADCAST_KEY = "Broadcast";
    public static final String BROADCAST_OBJ_ID_KEY = "objectId";
    public static final String BROADCAST_TYPE_KEY = "broadcastType";
    public static final String BROADCAST_END_DATE_KEY = "endedAt";
    public static final String BROADCAST_OWNER_KEY = "owner";
    public static final String BROADCAST_SESSION_ID_KEY = "sessionId";
    public static final String BROADCAST_TITLE_KEY = "title";
    public static final String BROADCAST_CAT_KEY = "createdAt";
    public static final String BROADCAST_UAT_KEY = "updatedAt";
    public static final String BROADCAST_ACL_KEY = "ACL";
    public static final String BROADCAST_TVIEWERS_KEY = "totalViewers";
    public static final String BROADCAST_WCOUNT_KEY = "watchersCount";
    public static final String BROADCAST_SS_IMAGE_KEY = "screenshotImageFile";

    public CastrBroadcast(){
        super();
    }

    public void setBroadcastType(Integer type){
        put(BROADCAST_TYPE_KEY, type);
    }

    public int getBroadcastType()
    {
        return getInt(BROADCAST_TYPE_KEY);
    }

    public void setEndDate(Date date)
    {
        put(BROADCAST_END_DATE_KEY, date);
    }

    public Date getEndDate()
    {
        return getDate(BROADCAST_END_DATE_KEY);
    }

    public void setOwner(ParseUser user)
    {
        put(BROADCAST_OWNER_KEY,user);
    }

    public ParseUser getOwner()
    {
        return getParseUser(BROADCAST_OWNER_KEY);
    }

    public void setSessionId(String sessionId)
    {
        put(BROADCAST_SESSION_ID_KEY, sessionId);
    }

    public String getSessionId()
    {
        return getString(BROADCAST_SESSION_ID_KEY);
    }

    public void setTitle(String title)
    {
        put(BROADCAST_TITLE_KEY, title);
    }

    public String getTitle()
    {
        return getString(BROADCAST_TITLE_KEY);
    }

    public void setCreatedDate(Date created)
    {
        put(BROADCAST_CAT_KEY,created);
    }

    public Date getCreatedDate()
    {
        return getDate(BROADCAST_CAT_KEY);
    }

    public void setUpdatedDate(Date updated)
    {
        put(BROADCAST_UAT_KEY, updated);
    }

    public Date getUpdatedDate()
    {
        return getDate(BROADCAST_UAT_KEY);
    }

    public void setTotalViewers(int number)
    {
        put(BROADCAST_TVIEWERS_KEY, number);
    }

    public int getTotalViewers()
    {
        return getInt(BROADCAST_TVIEWERS_KEY);
    }

    public void setTotalWatchers(int number)
    {
        put(BROADCAST_WCOUNT_KEY, number);
    }

    public int getTotalWatchers()
    {
        return getInt(BROADCAST_WCOUNT_KEY);
    }

    public void setImageFile (ParseFile file)
    {
        put(BROADCAST_SS_IMAGE_KEY, file);
    }

    public ParseFile getImageFile()
    {
        return getParseFile(BROADCAST_SS_IMAGE_KEY);
    }



}
