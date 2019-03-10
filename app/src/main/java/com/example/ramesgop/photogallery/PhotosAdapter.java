package com.example.ramesgop.photogallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.PhotosViewHolder> {
    private final String LOG_TAG = "PhotosAdapter";
    private ArrayList<Photo> mPhotos;
    private ExecutorService mExecutorService;
    private static final int NUM_THREADS = 5;

    public static class PhotosViewHolder extends RecyclerView.ViewHolder{

        public ImageView image;

        public PhotosViewHolder(ImageView imageView) {
            super(imageView);
            image = imageView;
        }

    }

    public PhotosAdapter(ArrayList<Photo> photos) {
        mPhotos = photos;
        mExecutorService = Executors.newFixedThreadPool(NUM_THREADS);
    }

    @Override
    public PhotosAdapter.PhotosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ImageView imageView = (ImageView) LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_item, parent, false);
        PhotosViewHolder viewHolder = new PhotosViewHolder(imageView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PhotosViewHolder viewHolder, int position) {
        Photo currentPhoto = mPhotos.get(position);
        viewHolder.image.setImageBitmap(null);
        mExecutorService.submit(new ImageDownloader(viewHolder.image, currentPhoto.getPhotoUrl()));
    }

    @Override
    public int getItemCount() {
        return mPhotos != null ? mPhotos.size() : 0;
    }

    private class ImageDownloader implements Runnable {

        private ImageView iv;
        private String url;


        public ImageDownloader(ImageView imageView, String imageUrl) {
            iv = imageView;
            url = imageUrl;
        }

        @Override
        public void run() {
            Bitmap image = null;
            try {
                InputStream in = new java.net.URL(url).openStream();
                image = BitmapFactory.decodeStream(in);
            }
            catch (Exception e) {
                Log.e(LOG_TAG, "Exception downloading photo");
            }
            final Bitmap imageBitmap = image;
            iv.post(new Runnable() {
                @Override
                public void run() {
                    iv.setImageBitmap(imageBitmap);
                }
            });
        }
    }

    public void addPhotos(ArrayList<Photo> photos) {
        mPhotos.addAll(photos);
    }

    public void clearDataSet() {
        mPhotos.clear();
    }
}
