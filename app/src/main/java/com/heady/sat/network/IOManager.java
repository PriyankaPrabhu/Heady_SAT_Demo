package com.heady.sat.network;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by admin_vserv on 5/19/2018.
 */

public class IOManager extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... params) {

        String stringUrl = params[0];
        String result = null;
        String inputLine;
        try {

            URL myUrl = new URL(stringUrl);
            //Create a connection
            HttpURLConnection connection = (HttpURLConnection)
                    myUrl.openConnection();

            //Set methods and timeouts
            connection.setRequestMethod("GET");
            connection.setReadTimeout(20000);
            connection.setConnectTimeout(20000);

            //Connect to our url
            connection.connect();

            //Create a new InputStreamReader
            InputStreamReader streamReader = new
                    InputStreamReader(connection.getInputStream());
            //Create a new buffered reader and String Builder
            BufferedReader reader = new BufferedReader(streamReader);
            StringBuilder stringBuilder = new StringBuilder();
            //Check if the line we are reading is not null
            while((inputLine = reader.readLine()) != null){
                stringBuilder.append(inputLine);
            }
            //Close our InputStream and Buffered reader
            reader.close();
            streamReader.close();
            //Set our result equal to our stringBuilder
            result = stringBuilder.toString();

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result){
        super.onPostExecute(result);
    }
}
