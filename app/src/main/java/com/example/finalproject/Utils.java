package com.example.finalproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

/**
 * The Utils class provides utility methods for working with Bitmap images to help convert photos.
 */
public class Utils {
    /**
     * Converts a Bitmap image to a byte array.
     *
     * @param bitmap The Bitmap image to convert.
     * @return The byte array representing the Bitmap image.
     */
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    /**
     * Converts a byte array to a Bitmap image.
     *
     * @param imageBytes The byte array representing the image.
     * @return The Bitmap image.
     */
    public static Bitmap getImage(byte[] imageBytes) {
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }
}
