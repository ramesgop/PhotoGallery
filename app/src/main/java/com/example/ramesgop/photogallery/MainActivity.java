package com.example.ramesgop.photogallery;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MainActivity";
    private ProgressDialog progressDialog;
    private RecyclerView photosList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Button photosbutton = findViewById(R.id.getPhotos);
        photosbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new GetPhotos().execute();
            }
        });

        photosList = this.findViewById(R.id.list_photos);
        photosList.setHasFixedSize(true);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        photosList.setLayoutManager(layoutManager);
    }

    private class GetPhotos extends AsyncTask<Void, Void, ArrayList<Photo>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage(getString(R.string.waiting));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected ArrayList<Photo> doInBackground(Void... arg0) {
            ArrayList<Photo> photos = new ArrayList<>();

            // Creating service handler class instance
            ServiceHandler serviceHandler = new ServiceHandler();

            // Making a request to ISSUES_URL and getting response
            String jsonStr = serviceHandler.makeServiceCall();

            Log.d(LOG_TAG, "Response " + " = " + jsonStr);
            if(jsonStr != null)
            {
                try
                {
                    JSONObject result = new JSONObject(jsonStr);
                    //Parse and add the json objects
                    JSONArray jsonArrayObj = result.getJSONObject("photos").getJSONArray("photo");



                    for(int i=0; i<jsonArrayObj.length(); i++)
                    {
                        JSONObject jsonObject = jsonArrayObj.getJSONObject(i);
                        String id = jsonObject.getString("id");
                        String farm = jsonObject.getString("farm");
                        String server = jsonObject.getString("server");
                        String secret = jsonObject.getString("secret");

                        Photo photo = new Photo(id, farm,server,secret);
                        photos.add(photo);

                        //Log.d(LOG_TAG, "Object " + i + " "+ photo.toString());
                    }


                }
                catch(JSONException e)
                {
                    Log.e(LOG_TAG, "Exception while parsing issues json object");
                }
            }

            return photos;
        }

        @Override
        protected void onPostExecute(ArrayList<Photo> result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (progressDialog.isShowing()){
                progressDialog.dismiss();
            }

            photosList.setAdapter(new PhotosAdapter(result));
        }

    }
}
