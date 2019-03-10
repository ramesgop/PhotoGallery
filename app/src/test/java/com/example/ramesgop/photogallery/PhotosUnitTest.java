package com.example.ramesgop.photogallery;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class PhotosUnitTest {

    @Mock
    private JSONObject jsonObject;

    @Test
    public void photosUrl_isCorrect() {
        Photo photo = new Photo("23451156376","1", "578", "8983a8ebc7");

        assertEquals("http://farm1.static.flickr.com/578/23451156376_8983a8ebc7.jpg", photo.getPhotoUrl());
    }


}