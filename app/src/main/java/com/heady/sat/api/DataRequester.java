package com.heady.sat.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.heady.sat.db.DBHandler;
import com.heady.sat.network.IOManager;
import com.heady.sat.util.Constants;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * Created by admin_vserv on 5/19/2018.
 */

public class DataRequester {

    public void requestData(Context context)
    {
        try
        {
            String jsonData = new IOManager().execute(Constants.Urls.BASE_URL).get();

            if(jsonData != null && !TextUtils.isEmpty(jsonData))
            {
                DBHandler dbHandler = DBHandler.getInstance(context);

                dbHandler.deletealltable();
                dbHandler.createTables();
                dbHandler.insertCategoryData(jsonData);
                dbHandler.insertRankingData(jsonData);
            }
        }
        catch (Exception ex)
        {

        }
    }


}
