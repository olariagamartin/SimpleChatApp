package com.themarto.mychatapp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    public static byte[] compressImage (Context context, Uri imagePath) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 25, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public static String getChatRoomId (String senderId, String receiverId) {
        if (senderId.compareTo(receiverId) < 0 ) {
            return senderId + receiverId;
        } else {
            return receiverId + senderId;
        }
    }

    public static String getDate (long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        return sdf.format(new Date(millis));
    }
}
