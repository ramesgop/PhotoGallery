package com.example.ramesgop.photogallery;

import java.util.ArrayList;

interface IPhotoSearchCallback {

    void onPhotosAvailable(ArrayList<Photo> result);
}
