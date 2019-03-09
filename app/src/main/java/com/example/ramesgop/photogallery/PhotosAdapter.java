package com.example.ramesgop.photogallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.InputStream;
import java.util.ArrayList;

public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.PhotosViewHolder> {
    private final String LOG_TAG = "PhotosAdapter";
    private ArrayList<Photo> mPhotos;

    public static class PhotosViewHolder extends RecyclerView.ViewHolder{

        public ImageView image;

        public PhotosViewHolder(ImageView imageView) {
            super(imageView);
            image = imageView;
        }

    }

    public PhotosAdapter(ArrayList<Photo> photos) {
        mPhotos = photos;
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

        new ImageLoader(viewHolder.image).execute(currentPhoto.getPhotoUrl());
    }

    @Override
    public int getItemCount() {
        return mPhotos != null ? mPhotos.size() : 0;
    }

    private class ImageLoader extends AsyncTask<String, Void, Bitmap> {
        private ImageView photoItem;

        public ImageLoader(ImageView photoItem) {
            this.photoItem = photoItem;
        }

        @Override
        protected Bitmap doInBackground(String... url) {
            String photoUrl = url[0];
            Bitmap image = null;
            try {
                InputStream in = new java.net.URL(photoUrl).openStream();
                image = BitmapFactory.decodeStream(in);
            }
            catch (Exception e) {
                Log.e(LOG_TAG, "Exception downloading photo");
            }
            return image;
        }

        @Override
        protected void onPostExecute(Bitmap image) {
            this.photoItem.setImageBitmap(image);
        }
    }

}
