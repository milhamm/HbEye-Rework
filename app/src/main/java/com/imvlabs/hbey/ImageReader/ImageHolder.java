package com.imvlabs.hbey.ImageReader;

/**
 * An static class to help carrying big image
 */
public class ImageHolder {
    static byte[] image;
    public static void SetImage(byte[] bitmap) {
        image = bitmap;
    }
    public static byte[] getImage() {
        return image;
    }
}
