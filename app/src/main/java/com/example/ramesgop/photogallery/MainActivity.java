package com.example.ramesgop.photogallery;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements IPhotoSearchCallback {

    private static final String LOG_TAG = "MainActivity";
    private static final int RESULTS_PER_PAGE = 30;
    private ProgressDialog progressDialog;
    private RecyclerView photosList;
    private PhotosAdapter photosAdapter;
    private GridLayoutManager layoutManager;
    private int pagesFetched = 0;
    private boolean isFetchingImages = false;
    private ExecutorService mExecutor;
    private String searchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mExecutor = Executors.newSingleThreadExecutor();

        final EditText searchEditText = this.findViewById(R.id.searchText);

        final Button photosbutton = findViewById(R.id.getPhotos);
        photosbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startFetching the results
                pagesFetched = 1;
                //new GetPhotos().execute(pagesFetched);
                searchText = searchEditText.getText().toString();
                searchEditText.clearFocus();
                InputMethodManager imm = (InputMethodManager)getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);

                showProgressDialog();
                photosAdapter.clearDataSet();
                mExecutor.submit(new GetPhotosTask(searchText, pagesFetched, RESULTS_PER_PAGE, MainActivity.this ));
            }
        });

        photosList = this.findViewById(R.id.list_photos);
        photosList.setHasFixedSize(true);

        layoutManager = new GridLayoutManager(this, 3);
        photosList.setLayoutManager(layoutManager);

        photosAdapter = new PhotosAdapter(new ArrayList<Photo>());
        photosList.setAdapter(photosAdapter);

        photosList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if ((visibleItemCount + firstVisibleItemPosition) +  6 >= totalItemCount && !isFetchingImages) {
                    loadNextPage();
                }
            }
        });
    }

    private void loadNextPage() {
        pagesFetched++;
        //new GetPhotos().execute(pagesFetched);
        mExecutor.submit(new GetPhotosTask(searchText, pagesFetched, RESULTS_PER_PAGE, this));
    }

    @Override
    public void onPhotosAvailable(final ArrayList<Photo> result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                int count = photosAdapter.getItemCount();
                photosAdapter.addPhotos(result);
                photosAdapter.notifyItemRangeInserted(count, RESULTS_PER_PAGE);

                isFetchingImages = false;
            }
        });
    }

    private void showProgressDialog() {
        // Showing progress dialog
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage(getString(R.string.waiting));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        isFetchingImages = true;
    }

    private class GetPhotos extends AsyncTask<Integer, Void, ArrayList<Photo>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            showProgressDialog();
        }

        @Override
        protected ArrayList<Photo> doInBackground(Integer... arg0) {
            ArrayList<Photo> photos = new ArrayList<>();
            int page = arg0[0];

            // Creating service handler class instance
            ServiceHandler serviceHandler = new ServiceHandler();

            // Making a request to FLICKER_API_URL and getting response
            String jsonStr = serviceHandler.makeServiceCall("kittens", RESULTS_PER_PAGE, page);

            //Log.d(LOG_TAG, "Response " + " = " + jsonStr);
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
                        String id = jsonObject.getString("id");
                        String farm = jsonObject.getString("farm");
                        String server = jsonObject.getString("server");
                        String secret = jsonObject.getString("secret");

                        Photo photo = new Photo(id, farm,server,secret);
                        photos.add(photo);
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
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            int count = photosAdapter.getItemCount();
            photosAdapter.addPhotos(result);
            photosAdapter.notifyItemRangeInserted(count, RESULTS_PER_PAGE);

            isFetchingImages = false;
        }

    }

    private class GetPhotosTask implements Runnable {

        private String searchText;
        private int page;
        private int resultsPerPage;
        private IPhotoSearchCallback mCallback;

        private GetPhotosTask(String serchText, int page, int resultsPerPage, IPhotoSearchCallback callback) {
            this.searchText = serchText;
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
                        String id = jsonObject.getString("id");
                        String farm = jsonObject.getString("farm");
                        String server = jsonObject.getString("server");
                        String secret = jsonObject.getString("secret");

                        Photo photo = new Photo(id, farm,server,secret);
                        photos.add(photo);
                    }
                }
                catch(JSONException e)
                {
                    Log.e(LOG_TAG, "Exception while parsing issues json object");
                }
            }

            mCallback.onPhotosAvailable(photos);
        }
    }
}
