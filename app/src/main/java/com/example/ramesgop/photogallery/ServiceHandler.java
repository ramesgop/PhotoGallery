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
    private static final String REQUEST_URL = "https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=3e7cc266ae2b0e0d78e279ce8e361736&format=json&nojsoncallback=1&safe_search=1&per_page=20&text=kittens";
    private static final String REQUEST_URL_FORMAT = "https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=3e7cc266ae2b0e0d78e279ce8e361736&format=json&nojsoncallback=1&safe_search=1&per_page=%1$s&page=%2$s&text=%3$s";

    public final static int GET = 1;
    public final static int POST = 2;

    public ServiceHandler() {
    }

    /**
     * Making service call
     * */
    public String makeServiceCall() {
        return this.makeServiceCall(REQUEST_URL);
    }

    public String makeServiceCall(String text, int resultsPerPage, int page) {
        return this.makeServiceCall(String.format(REQUEST_URL_FORMAT, Integer.toString(resultsPerPage), Integer.toString(page), text));
    }

    /**
     * Making service call
     * @url - requestUrl to make request
     * @method - http request method
     * @params - http request params
     * */
    public String makeServiceCall(String requestUrl) {

        StringBuilder content = new StringBuilder();

        try {
            URL resourceUrl, base, next;
            HttpURLConnection urlConnection;
            String location;

            int code =0;

            resourceUrl = new URL(requestUrl);
            urlConnection = (HttpURLConnection) resourceUrl.openConnection();
            urlConnection.setInstanceFollowRedirects(false);
            code = urlConnection.getResponseCode();

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
