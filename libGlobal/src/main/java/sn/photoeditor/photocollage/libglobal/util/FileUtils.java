package sn.photoeditor.photocollage.libglobal.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class FileUtils {
    public static String NAME_FOLDER = "SNImage";
    public static String PATH_FILE_SAVE = Environment.getExternalStorageDirectory().toString() + "/" + NAME_FOLDER + "/";


    private static final String TAG = "FileUtils";

    public static String getFolderName(String str) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), str);
        if (file.exists() || file.mkdirs()) {
            return file.getAbsolutePath();

        }
        return "";
    }

    private static boolean isSDAvailable() {
        return Environment.getExternalStorageState().equals("mounted");
    }


    public static File saveBitmapAsFile(Bitmap bitmap, int type) {
        String typeFile = "";
        if (type == 1) {
            typeFile = ".png";
        } else {
            typeFile = ".jpg";
        }
        FileOutputStream fileOutputStream;

        File file2 = new File(PATH_FILE_SAVE);
        if (!file2.exists()) {
            file2.mkdirs();
        }
        FileOutputStream fileOutputStream2 = null;
        try {
            File file3 = new File(PATH_FILE_SAVE + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date()) + typeFile);
            file3.createNewFile();
            fileOutputStream = new FileOutputStream(file3);
            try {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                fileOutputStream.flush();
                try {
                    fileOutputStream.close();
                } catch (IOException unused) {
                }
                return file3;
            } catch (Exception e) {
                e = e;
                try {
                    e.printStackTrace();
                    if (fileOutputStream != null) {
                        try {
                            fileOutputStream.close();
                        } catch (IOException unused2) {
                        }
                    }
                    return null;
                } catch (Throwable th) {
                    th = th;
                    fileOutputStream2 = fileOutputStream;
                    if (fileOutputStream2 != null) {
                    }
                    throw th;
                }
            }
        } catch (Exception e2) {
            e2.printStackTrace();
            fileOutputStream = null;

            if (fileOutputStream != null) {
            }
            return null;
        } catch (Throwable th2) {
            th2.printStackTrace();
            if (fileOutputStream2 != null) {
                try {
                    fileOutputStream2.close();
                } catch (IOException unused3) {
                    unused3.printStackTrace();
                }
            }
            return null;
        }
    }

    public static File getNewFile(Context context, String str) {
        String str2;
        String format = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
        if (isSDAvailable()) {
            str2 = getFolderName(str) + File.separator + format + ".jpg";
        } else {
            str2 = context.getFilesDir().getPath() + File.separator + format + ".jpg";
        }
        if (TextUtils.isEmpty(str2)) {
            return null;
        }
        return new File(str2);
    }


    public static Bitmap getResizedBitmap(Bitmap bitmap, int i, int i2) {
        Bitmap createBitmap = Bitmap.createBitmap(i, i2, Bitmap.Config.ARGB_8888);
        float width = ((float) i) / ((float) bitmap.getWidth());
        float height = ((float) i2) / ((float) bitmap.getHeight());
        Matrix matrix = new Matrix();
        matrix.setScale(width, height, 0.0f, 0.0f);
        Canvas canvas = new Canvas(createBitmap);
        canvas.setMatrix(matrix);
        canvas.drawBitmap(bitmap, 0.0f, 0.0f, new Paint(2));
        bitmap.recycle();
        return createBitmap;
    }

    public static boolean deleteFile(String path) {
        File file = new File(path);
        return file.delete();
    }

    public static long getRadiusBitmap(Bitmap bitmap, Bitmap bitmap1) {
        long size1 = getSizeBitmap(bitmap);
        long size2 = getSizeBitmap(bitmap1);

        long radius = ((long) ((float) size1 / (float) size2) * 100);
        int radiuss = (int) (radius);
        Log.e("hlhdgsdgr", size1 + "    s   " + size2 + "    s    " + radius + "   s   " + radiuss);
        return radiuss;
    }

    public static long getSizeBitmap(Bitmap bitmap) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageInByte = stream.toByteArray();

        return imageInByte.length;
    }

    public static String getSizeFileBitmap(Bitmap bitmap) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageInByte = stream.toByteArray();
        long lengthbmp = imageInByte.length;
        return getDynamicSpace(lengthbmp);
    }

    public static String getDynamicSpace(long diskSpaceUsed) {
        if (diskSpaceUsed <= 0) {
            return "0";
        }

        final String[] units = new String[]{"B", "KiB", "MiB", "GiB", "TiB"};
        int digitGroups = (int) (Math.log10(diskSpaceUsed) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(diskSpaceUsed / Math.pow(1024, digitGroups))
                + " " + units[digitGroups];
    }

    public static ArrayList<String> getAllFileMyImage(String path) {
        ArrayList<String> arrFile = new ArrayList<>();
        File directory = new File(path);
        File[] files = directory.listFiles();
        Log.d("Files", "Size: " + files.length);
        for (int i = 0; i < files.length; i++) {
            arrFile.add(files[i].getPath());
            Log.d("Files", "FileName:" + files[i].getName());
        }
        return arrFile;
    }
}
