package com.imvlabs.hbey.CameraHandler;

/**
 * Created by maaakbar on 10/24/15.
 */
public interface OnPictureReadyListener {
    void onPictureAvailable(byte[] data);
    void onPictureAvailable();
}