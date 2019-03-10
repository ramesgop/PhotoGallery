package com.example.ramesgop.photogallery;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
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
                //start Fetching the results
                pagesFetched = 1;
                searchText = searchEditText.getText().toString();
                searchEditText.clearFocus();
                InputMethodManager imm = (InputMethodManager)getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);

                photosAdapter.clearDataSet();

                showProgressDialog();
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
        isFetchingImages = true;
        mExecutor.submit(new GetPhotosTask(searchText, pagesFetched, RESULTS_PER_PAGE, this));
    }

    @Override
    public void onPhotosAvailable(final ArrayList<Photo> result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null && progressDialog.isShowing()) {
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
}
