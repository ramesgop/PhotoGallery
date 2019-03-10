# PhotoGallery

Android app to search for photos on flickr and display them in a GridView

## Getting Started

Clone the repository and then build the project in Android Studio

### Prerequisites

AndroidStudio, Android SDK

## Features
1. Type any text to search for images and then click on Button "Get Photos" to fetch the image(s) from flickr
2. As you scroll down to the end, more images are fetched using the flickr API.
3. Images are shown as they are fetched.

## Code structure
1. MainActivity is the entry point for the application
2. ServiceHandler class deals with querying the flickr API and returning the results.
3. GetPhotosTask deals with making a request, parsing the result, constructing the Photo objects and returning the result back to the MainActivity
4. GetPhotosTask instance is queued on the executor in mainActivity so that network call will happen in another thread.
5. Photo class is used to hold each photo object.
6. RecyclerView is used for displaying the Images in a GridLayout (using GridLayoutManager). RecyclerView will attempt to reuse existing view instead of recreating.
7. PhotosAdapter (RecyclerView.Adapter) is the adapter used for displaying the photos in the RecyclerView. this also implements the ViewHolder pattern so that views are cached and we save on lookups.
8. Images in each photo_item (List item) are loaded in background thread. Using Executor to run the task for reading the image from the Photo Url and then displaying in the ImageView.
   Prefered Executor over AsyncTask as by default AsyncTasks run on the same thread so having multiple threads for downloading images will give better performance results in terms of faster loading.
9. ScrollListener is added to RecyclerView in MainActivity to detect when we scroll to end and fetch more pages.

## Sequence flow
1. On starting MainaActivity, you see an input editText and GetPhotos button. Enter any text and press GetPhotos to start fetching images.
2. For first fetch, showing a progressDialog while we fetch the results using the GetPhotosTask. Call is made using serviceHandler - params passed are page#, resultsPerPage, searchText.
   The JSON result is parsed in GetPhotosTask and result (ArrayList<Photo>) is passed to MainActivity in a callback.
3. In the MainActivity, add the results to the adapter and notify the change in data to Adapter so view can refresh.
4. If we detect a scroll near to the end of currently fetched items, make call to fetch more results. pagesFetched in MainActivity is used to keep track of results fetched.
   Results received are added to end of the PhotosAdapter.

## TODO
1. Add caching for Images (using LRUCache) so that we do not download same images again in case we scroll back.
2. Use interfaces for binding the different objects together. Currently directly using classes, move to Interfaces to abstract out the class implementation and dependency.
3. More testing.

## Authors

* **Ramesh G**