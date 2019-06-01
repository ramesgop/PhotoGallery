package com.example.ramesgop.photogallery;

public class Photo {

    private String farm;
    private String server;
    private String id;
    private String secret;

    private static final String PHOTO_URL = "https://farm%1$s.static.flickr.com/%2$s/%3$s_%4$s.jpg";

    public Photo (String id, String farm, String server, String secret) {
        this.id = id;
        this.farm = farm;
        this.server = server;
        this.secret = secret;
    }

    public String getPhotoUrl() {
        return  String.format(PHOTO_URL, farm, server, id, secret);

    }


}
