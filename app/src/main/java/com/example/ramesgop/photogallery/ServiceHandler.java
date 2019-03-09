package com.example.ramesgop.photogallery;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import javax.net.ssl.HttpsURLConnection;
import java.net.URL;
import java.util.List;

public class ServiceHandler {

    private static final String LOG_TAG = "HttpHandler";
    private static final String REQUEST_URL = "https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=3e7cc266ae2b0e0d78e279ce8e361736&format=json&nojsoncallback=1&safe_search=2&per_page=20&text=kittens";

    public final static int GET = 1;
    public final static int POST = 2;

    public ServiceHandler() {
    }

    /**
     * Making service call
     * @url - url to make request
     * @method - http request method
     * */
    public String makeServiceCall() {
        return this.makeServiceCall(REQUEST_URL, GET);
    }

    /**
     * Making service call
     * @url - requestUrl to make request
     * @method - http request method
     * @params - http request params
     * */
    public String makeServiceCall(String requestUrl, int method) {

        StringBuilder content = new StringBuilder();

        try {
            URL resourceUrl, base, next;
            HttpURLConnection urlConnection;
            String location;

            int code =0;

            while (true)
            {
                resourceUrl = new URL(requestUrl);
                urlConnection = (HttpURLConnection) resourceUrl.openConnection();
                urlConnection.setInstanceFollowRedirects(false);
                code = urlConnection.getResponseCode();
                //handle any http to https redirection
                switch (code)
                {
                    case HttpURLConnection.HTTP_MOVED_PERM:
                    case HttpURLConnection.HTTP_MOVED_TEMP:
                        location = urlConnection.getHeaderField("Location");
                        base     = new URL(requestUrl);
                        next     = new URL(base, location);
                        requestUrl = next.toExternalForm();
                        continue;
                }

                break;
            }

            if(code == HttpURLConnection.HTTP_OK) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    content.append(line + "\n");
                }
                bufferedReader.close();
            }

        }
        catch (IOException e)
        {
            Log.e(LOG_TAG, "Error connecting to the server" );
        }
        catch (Exception e) {
            Log.e(LOG_TAG, "Exception occured" );
            e.printStackTrace();
        }
        return content.toString();
    }
}
