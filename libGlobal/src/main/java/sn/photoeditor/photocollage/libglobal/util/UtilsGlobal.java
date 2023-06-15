package sn.photoeditor.photocollage.libglobal.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.util.Locale;

import sn.photoeditor.photocollage.libglobal.R;

public class UtilsGlobal {
    public static Bitmap getBitmapFromUri(Uri uri, Context context) {
        try {
            ParcelFileDescriptor parcelFileDescriptor = context.getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
            return image;
        } catch (Exception e) {
            return null;
        }
    }

    public static String getSizeImage(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageInByte = stream.toByteArray();
        long lengthbmp = imageInByte.length;
        Locale locale = Locale.getDefault();
        return format(lengthbmp, locale);
    }

    public static String format(long value, Locale locale) {
        if (value < 1024) {
            return value + " B";
        }
        int z = (63 - Long.numberOfLeadingZeros(value)) / 10;
        return String.format(locale, "%.1f %siB", (double) value / (1L << (z * 10)), " KMGTPE".charAt(z));
    }
}
