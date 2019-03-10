package com.example.ramesgop.photogallery;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;

public class GetPhotosTask implements Runnable {

    private String searchText;
    private int page;
    private int resultsPerPage;
    private IPhotoSearchCallback mCallback;
    public static final String LOG_TAG = "GetPhotosTask";

    public GetPhotosTask(String searchText, int page, int resultsPerPage, IPhotoSearchCallback callback) {
        this.searchText = searchText;
        this.page = page;
        this.resultsPerPage = resultsPerPage;
        this.mCallback = callback;
    }

    @Override
    public void run() {
        ArrayList<Photo> photos = new ArrayList<>();
        ServiceHandler serviceHandler = new ServiceHandler();

        // Making a request to FLICKER_API_URL and getting response
        String jsonStr = serviceHandler.makeServiceCall(searchText, resultsPerPage, page);

        //Log.d(LOG_TAG, "Response " + " = " + jsonStr);
        photos = parsePhotos(jsonStr);

        mCallback.onPhotosAvailable(photos);
    }

    private ArrayList<Photo> parsePhotos(String jsonStr) {
        ArrayList<Photo> photos = new ArrayList<>();

        if(jsonStr != null)
        {
            try
            {
                JSONObject result = new JSONObject(jsonStr);
                //Parse and add the photo objects
                JSONArray jsonArrayObj = result.getJSONObject("photos").getJSONArray("photo");

                for(int i=0; i<jsonArrayObj.length(); i++)
                {
                    JSONObject jsonObject = jsonArrayObj.getJSONObject(i);

                    Photo photo = getPhoto(jsonObject);
                    photos.add(photo);
                }
            }
            catch(JSONException e)
            {
                Log.e(LOG_TAG, "Exception while parsing photos json object");
            }
        }

        return photos;
    }

    private Photo getPhoto(JSONObject jsonObject) {
        Photo photo = null;
        try {
            String id = jsonObject.getString("id");
            String farm = jsonObject.getString("farm");
            String server = jsonObject.getString("server");
            String secret = jsonObject.getString("secret");
            photo = new Photo(id, farm,server,secret);
        }
        catch (Exception e) {
            Log.e(LOG_TAG, "Exception while parsing photo");
        }
        return photo;
    }
}
