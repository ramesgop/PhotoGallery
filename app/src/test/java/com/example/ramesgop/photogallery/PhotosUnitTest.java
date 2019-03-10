package com.example.ramesgop.photogallery;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class PhotosUnitTest {
    @Test
    public void photosUrl_isCorrect() {
        Photo photo = new Photo("23451156376","1", "578", "8983a8ebc7");

        assertEquals("http://farm1.static.flickr.com/578/23451156376_8983a8ebc7.jpg", photo.getPhotoUrl());
    }

}