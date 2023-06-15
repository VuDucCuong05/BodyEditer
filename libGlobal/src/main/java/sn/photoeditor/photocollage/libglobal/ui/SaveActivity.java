package sn.photoeditor.photocollage.libglobal.ui;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;

import java.io.File;

import sn.photoeditor.photocollage.libglobal.BuildConfig;
import sn.photoeditor.photocollage.libglobal.R;


public class SaveActivity extends AppCompatActivity implements View.OnClickListener {
    public static String PATH_SAVE = "path_save_image";
    private ImageView imgShare;
    public static final String AUTHORITY = "sn.paint.art.color.photo.editor" + ".provider_paths";
    private TextView tvPathShare;
    private ImageView imgBackShare, imgHome;
    private LinearLayout llShareFacebook, llShareInsta, llShareTwitter, llShareMessage, llShareMore;
    private String pathSaveImage = "/storage/emulated/0/SNImage/20211215_214608.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);
        initViews();
    }

    private void initViews() {
        imgShare = findViewById(R.id.img_share);
        tvPathShare = findViewById(R.id.tv_path_save);
        imgBackShare = findViewById(R.id.img_back);
        imgBackShare.setOnClickListener(this);
        imgHome = findViewById(R.id.img_home);
        imgHome.setOnClickListener(this);
        llShareFacebook = findViewById(R.id.ll_share_facebook);
        llShareFacebook.setOnClickListener(this);
        llShareInsta = findViewById(R.id.ll_share_insta);
        llShareInsta.setOnClickListener(this);
        llShareMessage = findViewById(R.id.ll_share_message);
        llShareMessage.setOnClickListener(this);
        llShareMore = findViewById(R.id.ll_share_more);
        llShareMore.setOnClickListener(this);
        llShareTwitter = findViewById(R.id.ll_share_twitter);
        llShareTwitter.setOnClickListener(this);
        pathSaveImage = getIntent().getStringExtra(PATH_SAVE);
        Glide.with(this).load(pathSaveImage).into(imgShare);
        tvPathShare.setText("(" + getResources().getString(R.string.photo_saved_at) + " " + pathSaveImage + ")");
        Log.e("pathFiles", pathSaveImage + "       s");
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.img_home) {//   startActivities(new Intents(SaveActivity.this,MainActivity.class));
        } else if (id == R.id.img_back) {
            finish();
        } else if (id == R.id.ll_share_facebook) {
            Log.e("aidfjklhefd", pathSaveImage + "      s");
            shareFacebook(this, pathSaveImage);
            shareToApp(this, pathSaveImage, "com.facebook.katana");
        } else if (id == R.id.ll_share_insta) {
            shareToApp(this, pathSaveImage, "com.instagram.android");
        } else if (id == R.id.ll_share_message) {
            shareToApp(this, pathSaveImage, "com.facebook.orca");
        } else if (id == R.id.ll_share_twitter) {
            shareToApp(this, pathSaveImage, "com.twitter.android");
        } else if (id == R.id.ll_share_more) {
            shareImage(pathSaveImage, this);
        }
    }

    public static void shareTwitter(Activity activity, String pathImage) {

        try {

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setPackage("com.twitter.android");
            StringBuilder stringBuilder = new StringBuilder(activity.getString(R.string.makebeautys_createby) + activity.getString(R.string.app_names));
            stringBuilder.append(activity.getString(R.string.makebeautys_google_link) + activity.getPackageName());
            stringBuilder.append(" : ");
            intent.putExtra(Intent.EXTRA_TEXT, stringBuilder.toString());
            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(pathImage));
            intent.setType("image/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            activity.startActivity(Intent.createChooser(intent, "Share Photo to Twitter"));
            return;
        } catch (ActivityNotFoundException ex) {
            ex.printStackTrace();
            Toast.makeText(activity, "Please install Twitter to use this function!", Toast.LENGTH_LONG).show();
        }
    }

    public static void shareInstagram(Activity activity, String pathImage) {

        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(pathImage));
            intent.putExtra(Intent.EXTRA_TEXT, "Some text you would like to share...");
            intent.setPackage("com.instagram.android");
            activity.startActivity(Intent.createChooser(intent, "Share photo to Instagram"));
            return;
        } catch (ActivityNotFoundException ex) {
            ex.printStackTrace();
            Toast.makeText(activity, "Please install Instagram app to use this function!", Toast.LENGTH_LONG).show();
        }
    }

    public static void shareMessages(Activity activity, String pathImage) {

        try {

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setPackage("com.facebook.orca");
            StringBuilder stringBuilder = new StringBuilder(activity.getString(R.string.makebeautys_createby) + activity.getString(R.string.app_names));
            stringBuilder.append(activity.getString(R.string.makebeautys_google_link) + activity.getPackageName());
            stringBuilder.append(" : ");
            intent.putExtra(Intent.EXTRA_TEXT, stringBuilder.toString());
            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(pathImage));
            intent.setType("image/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            activity.startActivity(Intent.createChooser(intent, "Share photo to Facebook Messenger"));
            return;
        } catch (ActivityNotFoundException ex) {
            ex.printStackTrace();
            Toast.makeText(activity, "Please install FB Messenger to use this function!", Toast.LENGTH_LONG).show();
        }
    }


    public static void shareFacebook(Activity activity, String pathImage) {

        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(pathImage));
            intent.putExtra(Intent.EXTRA_TEXT, "Some text you would like to share...");
            intent.setPackage("com.facebook.katana");
            activity.startActivity(Intent.createChooser(intent, "Share photo to Facebook"));
            return;
        } catch (ActivityNotFoundException ex) {
            ex.printStackTrace();
            Toast.makeText(activity, "Please install Facebook app to use this function!", Toast.LENGTH_LONG).show();
        }
    }

    public void disableExposure() {
        if (Build.VERSION.SDK_INT >= 24)
            try {
                StrictMode.class.getMethod("disableDeathOnFileUriExposure", new Class[0]).invoke(null);
                return;
            } catch (Exception localException) {
                localException.printStackTrace();
            }
    }

    public void shareToApp(Activity activity, String pathImage, String packetName) {
        disableExposure();
        try {

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setPackage(packetName);
            StringBuilder stringBuilder = new StringBuilder(activity.getString(R.string.makebeautys_createby) + activity.getString(R.string.app_names));
            stringBuilder.append(activity.getString(R.string.makebeautys_google_link) + activity.getPackageName());
            stringBuilder.append(" : ");
            intent.putExtra(Intent.EXTRA_TEXT, stringBuilder.toString());
            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + pathImage));
            intent.setType("image/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            activity.startActivity(Intent.createChooser(intent, "Share Image"));
            return;
        } catch (ActivityNotFoundException ex) {
            ex.printStackTrace();
            Toast.makeText(activity, "Please install Line to use this function!", Toast.LENGTH_LONG).show();
        }
    }

    public static void shareImage(String path, Activity activity) {
        MediaScannerConnection.scanFile(activity, new String[]{path},
                null, (path1, uri) -> {
                    Intent shareIntent = new Intent(
                            Intent.ACTION_SEND);
                    shareIntent.setType("image/*");
                    shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    shareIntent
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                    activity.startActivity(Intent.createChooser(shareIntent,
                            activity.getResources().getString(R.string.share_image)));

                });
    }


}