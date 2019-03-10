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
3. GetPhotosTask deals with making a request and returning the result back to the MainActivity
4. GetPhotosTask instance is queued on the executor in mainActivity so that network call will happen in another thread.
5. RecylclerView is used for displaying the Images in a GridLayout (using GridLayoutManager).

## Authors

* **Ramesh G**