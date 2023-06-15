package net.braincake.bodytune.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.Objects;

import net.braincake.bodytune.R;
import net.braincake.bodytune.controls.NetworkUtil;

public class ExportPhotoActivity extends AppCompatActivity implements View.OnClickListener {
    boolean isFirst = true;


    FrameLayout mCloseButton;
    FrameLayout mDotsButton;
    ImageView mEditFace;
    FrameLayout mFacebookButton;
    FrameLayout mInstagramButton;
    FrameLayout mLoading;
    ImageView mMakeAnother;
    FrameLayout mMessengerButton;
    ImageView mSavedImage;
    Uri mUri;
    FrameLayout mWhatsappButton;
    BroadcastReceiver photoSaved = new BroadcastReceiver() {
        /* class net.braincake.bodytune.activity.ExportPhotoActivity.AnonymousClass1 */

        @SuppressLint("WrongConstant")
        public void onReceive(Context context, Intent intent) {
            try {
                Uri parse = Uri.parse(intent.getStringExtra("uri"));
                ExportPhotoActivity.this.mUri = parse;
                ExportPhotoActivity.this.mSavedImage.setImageURI(parse);
                ExportPhotoActivity.this.mLoading.setVisibility(8);
            } catch (Exception unused) {
            }
        }
    };
    AlertDialog videoDialog;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_export_photo);
        if (bundle != null) {
            finish();
        }
        this.mCloseButton = (FrameLayout) findViewById(R.id.close);
        this.mSavedImage = (ImageView) findViewById(R.id.image);
        this.mMakeAnother = (ImageView) findViewById(R.id.makeAnother);
        this.mEditFace = (ImageView) findViewById(R.id.editFace);
        this.mLoading = (FrameLayout) findViewById(R.id.loading);
        this.mInstagramButton = (FrameLayout) findViewById(R.id.instagramButton);
        this.mFacebookButton = (FrameLayout) findViewById(R.id.facebookButton);
        this.mWhatsappButton = (FrameLayout) findViewById(R.id.whatsappButton);
        this.mMessengerButton = (FrameLayout) findViewById(R.id.messengerButton);
        this.mDotsButton = (FrameLayout) findViewById(R.id.dotsButton);
        this.mCloseButton.setOnClickListener(this);
        this.mMakeAnother.setOnClickListener(this);
        this.mEditFace.setOnClickListener(this);
        this.mInstagramButton.setOnClickListener(this);
        this.mFacebookButton.setOnClickListener(this);
        this.mWhatsappButton.setOnClickListener(this);
        this.mMessengerButton.setOnClickListener(this);
        this.mDotsButton.setOnClickListener(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(this.photoSaved, new IntentFilter("photoWasSaved"));
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("startSaveBitmap"));
        if (NetworkUtil.getConnectivityStatusString(this)) {

        }

        //showRating();
    }

    public void showRating() {
        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(), 0);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        if (sharedPreferences.getBoolean("showReview", true)) {
            showDialog(edit);
        }
        edit.putInt("numberOfSavedPhoto", sharedPreferences.getInt("numberOfSavedPhoto", 0) + 1);
        edit.apply();
    }

    public void showVideo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View inflate = View.inflate(this, R.layout.video_layout, null);
        builder.setView(inflate);
        final VideoView videoView = (VideoView) inflate.findViewById(R.id.videoView);
        ((FrameLayout) inflate.findViewById(R.id.download)).setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                ExportPhotoActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=net.braincake.pixl.pixl")));
            }
        });
        ((TextView) inflate.findViewById(R.id.close)).setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                ExportPhotoActivity.this.videoDialog.dismiss();
            }
        });
        videoView.setZOrderOnTop(true);
        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video_pixl));
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {


            public void onCompletion(MediaPlayer mediaPlayer) {
                videoView.start();
                Log.d("My", "Intrat");
            }
        });
        AlertDialog create = builder.create();
        this.videoDialog = create;
        create.setOnShowListener(new DialogInterface.OnShowListener() {


            public void onShow(DialogInterface dialogInterface) {
                videoView.start();
            }
        });
        this.videoDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {


            public void onDismiss(DialogInterface dialogInterface) {
                videoView.stopPlayback();
            }
        });
        this.videoDialog.show();
    }

    public void showDialog(final SharedPreferences.Editor editor) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View inflate = View.inflate(this, R.layout.review_layout, null);
        builder.setView(inflate);
        final AlertDialog create = builder.create();
        View.OnClickListener r4 = new View.OnClickListener() {

            public void onClick(View view) {
                int id = view.getId();
                if (id == R.id.cancelFeedbackButton) {
                    create.dismiss();
                } else if (id == R.id.feedbackButton) {
                    Intent intent = new Intent("android.intent.action.SENDTO");
                    intent.setData(Uri.parse("mailto:"));
                    intent.putExtra("android.intent.extra.EMAIL", new String[]{"fitnessappslab@gmail.com"});
                    intent.putExtra("android.intent.extra.SUBJECT", "Android BodyTune Review");
                    create.dismiss();
                    if (intent.resolveActivity(ExportPhotoActivity.this.getPackageManager()) != null) {
                        ExportPhotoActivity.this.startActivity(Intent.createChooser(intent, "Send Email"));
                    }
                } else if (id == R.id.starButton) {
                    editor.putBoolean("showReview", false);
                    editor.apply();
                    ExportPhotoActivity exportPhotoActivity = ExportPhotoActivity.this;
                    exportPhotoActivity.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + ExportPhotoActivity.this.getPackageName())));
                    create.dismiss();
                }
            }
        };
        ((ImageView) inflate.findViewById(R.id.starButton)).setOnClickListener(r4);
        ((ImageView) inflate.findViewById(R.id.feedbackButton)).setOnClickListener(r4);
        ((ImageView) inflate.findViewById(R.id.cancelFeedbackButton)).setOnClickListener(r4);
        ((Window) Objects.requireNonNull(create.getWindow())).setBackgroundDrawable(new ColorDrawable(0));
        create.show();
    }

    @Override
    public void onResume() {

        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        AlertDialog alertDialog = this.videoDialog;
        if (alertDialog != null && alertDialog.isShowing()) {
            this.videoDialog.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(this.photoSaved);
    }

    @SuppressLint("WrongConstant")
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.close:
                finish();
                return;
            case R.id.dotsButton:
            case R.id.facebookButton:
            case R.id.instagramButton:
            case R.id.messengerButton:
            case R.id.whatsappButton:
                if (this.mUri == null) {
                    Toast.makeText(this, "Saving is in progress.", 1).show();
                    return;
                }

                Intent intent = new Intent("android.intent.action.SEND");
                intent.setType("image/jpeg");
                intent.putExtra("android.intent.extra.STREAM", this.mUri);
                startActivity(Intent.createChooser(intent, "Share to"));
                return;
            case R.id.editFace:
                showVideo();
                return;
            case R.id.makeAnother:
                navigateToGallery();
                return;
            default:
                return;
        }
    }

    @SuppressLint("WrongConstant")
    private void navigateToGallery() {
        Intent intent = new Intent(this, CreateNewPhotoActivity.class);
        intent.addFlags(67108864);
        startActivity(intent);
    }
}
