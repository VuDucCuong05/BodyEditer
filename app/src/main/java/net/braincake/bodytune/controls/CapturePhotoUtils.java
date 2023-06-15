package net.braincake.bodytune.controls;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

public class CapturePhotoUtils {

    public interface PhotoLoadResponse {
        void loadResponse(Bitmap bitmap, int i, int i2);
    }

    private static String saveImage(Bitmap bitmap, String str, ContentResolver contentResolver) {
        Uri uri;
        FileNotFoundException e;
        OutputStream outputStream = null;
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("_display_name", str);
            contentValues.put("mime_type", "image/png");
            contentValues.put("relative_path", "DCIM/");
            uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            try {
                outputStream = contentResolver.openOutputStream(uri);
            } catch (FileNotFoundException e2) {
                e = e2;
            }
        } catch (Exception xxe) {

            uri = null;
            xxe.printStackTrace();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);


            try {
                outputStream.flush();
                outputStream.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            return uri.toString();
        }
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        try {
            outputStream.flush();
            outputStream.close();
        } catch (IOException e4) {
            e4.printStackTrace();
        }
        return uri.toString();
    }

    public static String insertImage(ContentResolver var0, Bitmap var1, String var2, String var3) {
        if (Build.VERSION.SDK_INT >= 29) {
            return saveImage(var1, var2, var0);
        } else {
            ContentValues var4 = new ContentValues();
            var4.put("title", var2);
            var4.put("description", var3);
            var4.put("mime_type", "image/png");
            var4.put("date_added", System.currentTimeMillis() / 1000L);
            var4.put("datetaken", System.currentTimeMillis());
            var4.put("date_modified", System.currentTimeMillis() / 1000L);
            var3 = null;

            Uri var19;
            label121: {
                label127: {
                    Uri var20;
                    label128: {
                        try {
                            var20 = var0.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, var4);
                        } catch (Exception var17) {
                            var20 = null;
                            break label128;
                        }


                        if (var1 != null) {
                            label129: {
                                OutputStream var21;
                                try {
                                    var21 = var0.openOutputStream(var20);
                                } catch (Exception var16) {

                                    break label129;
                                }

                                try {
                                    var1.compress(Bitmap.CompressFormat.PNG, 100, var21);
                                } finally {
                                    try {
                                        var21.close();
                                    } catch (Exception var13) {

                                        break label129;
                                    }
                                }

                                long var5 = ContentUris.parseId(var20);
                                storeThumbnail(var0, MediaStore.Images.Thumbnails.getThumbnail(var0, var5, 1, (BitmapFactory.Options)null), var5, 3);
                                var19 = var20;
                                break label121;
                            }
                        } else {
                            try {
                                var0.delete(var20, (String)null, (String[])null);
                                break label127;
                            } catch (Exception var15) {

                            }
                        }
                    }

                    var19 = var20;
                    if (var20 == null) {
                        break label121;
                    }

                    var0.delete(var20, (String)null, (String[])null);
                }

                var19 = null;
            }

            String var18 = var3;
            if (var19 != null) {
                var18 = var19.toString();
            }

            return var18;
        }
    }



    private static Bitmap storeThumbnail(ContentResolver contentResolver, Bitmap bitmap, long j, int i) {
        Matrix matrix = new Matrix();
        matrix.setScale(50.0f / ((float) bitmap.getWidth()), 50.0f / ((float) bitmap.getHeight()));
        Bitmap createBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        ContentValues contentValues = new ContentValues(4);
        contentValues.put("kind", Integer.valueOf(i));
        contentValues.put("image_id", Integer.valueOf((int) j));
        contentValues.put("height", Integer.valueOf(createBitmap.getHeight()));
        contentValues.put("width", Integer.valueOf(createBitmap.getWidth()));
        try {
            OutputStream openOutputStream = contentResolver.openOutputStream(contentResolver.insert(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, contentValues));
            createBitmap.compress(Bitmap.CompressFormat.JPEG, 100, openOutputStream);
            openOutputStream.close();
            return createBitmap;
        } catch (IOException unused) {
            return null;
        }
    }

    public static void getBitmapFromDisk(final int i, final int i2, final String str, final PhotoLoadResponse photoLoadResponse, final Activity activity) {
        new Thread(new Runnable() {

            public void run() {
                try {
                    File file = new File(activity.getFilesDir(), str);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inMutable = true;
                    Bitmap decodeStream = BitmapFactory.decodeStream(new FileInputStream(file), null, options);
                    if (decodeStream == null) {
                        activity.runOnUiThread(new Runnable() {

                            public void run() {
                                photoLoadResponse.loadResponse(null, i, i2);
                            }
                        });
                        return;
                    }
                    if (!decodeStream.isMutable()) {
                        Bitmap copy = decodeStream.copy(Bitmap.Config.ARGB_8888, true);
                        decodeStream.recycle();
                        decodeStream = copy;
                    }
                    Bitmap finalDecodeStream = decodeStream;
                    activity.runOnUiThread(new Runnable() {

                        public void run() {
                            photoLoadResponse.loadResponse(finalDecodeStream, i, i2);
                        }
                    });
                } catch (FileNotFoundException e) {
                    activity.runOnUiThread(new Runnable() {

                        public void run() {
                            photoLoadResponse.loadResponse(null, i, i2);
                        }
                    });
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
