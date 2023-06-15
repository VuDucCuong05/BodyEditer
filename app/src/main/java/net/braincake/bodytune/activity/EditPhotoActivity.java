package net.braincake.bodytune.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.exifinterface.media.ExifInterface;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.braincake.bodytune.R;
import net.braincake.bodytune.adapters.MainMenuAdapter;
import net.braincake.bodytune.controls.CapturePhotoUtils;
import net.braincake.bodytune.controls.MenuInfo;
import net.braincake.bodytune.controls.ScaleImage;
import net.braincake.bodytune.controls.StartPointSeekBar;
import net.braincake.bodytune.effects.drawing.SkinColor;
import net.braincake.bodytune.effects.mesh.Enhance;
import net.braincake.bodytune.effects.mesh.Hips;
import net.braincake.bodytune.effects.mesh.Refine;
import net.braincake.bodytune.effects.mesh.Waist;
import net.braincake.bodytune.effects.resize.Height;

import org.wysaid.nativePort.CGEDeformFilterWrapper;
import org.wysaid.nativePort.CGEImageHandler;
import org.wysaid.view.ImageGLSurfaceView;

public class EditPhotoActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener, CapturePhotoUtils.PhotoLoadResponse {

    private ImageView imgTest;
    static final int SAVED_PHOTO = 326;
    final float MAX_LENGTH_OF_IMAGE = 1500.0f;
    private Bundle bundle;

    Handler handler = new Handler();
    public ImageGLSurfaceView glSurfaceView;
    public boolean isBlocked = true;
    public MainMenuAdapter mAdapter;
    public ImageView mBack;
    public FrameLayout mBefore;
    Canvas mCanvas;
    public Bitmap mCurrentBitmap;
    BackPressed mCurrentInterface;
    public int mIdCurrent;
    public int mIdLast;
    public int mIdRequisite;
    String mImagePath;
    public FrameLayout mLoading;
    public RecyclerView mMenuHome;

    private MainMenuAdapter.OnItemClickListener mMenuHomeClickListener = new MainMenuAdapter.OnItemClickListener() {

        @Override
        public void onItemClick(int i) {
            EditPhotoActivity.this.isBlocked = true;
            EditPhotoActivity.this.sendEvent("Tool - open");
            if (i == 0) {
                type = 3;

                mScaleImage.setVisibility(View.VISIBLE);
                Bitmap bitmap = mCurrentBitmap;
                EditPhotoActivity editPhotoActivity2 = EditPhotoActivity.this;
                mCurrentInterface = new Refine(bitmap, editPhotoActivity2, editPhotoActivity2.mScaleImage);
            } else if (i == 1) {
                type = 4;

                mScaleImage.setVisibility(View.VISIBLE);
                Bitmap bitmap2 = mCurrentBitmap;
                EditPhotoActivity editPhotoActivity4 = EditPhotoActivity.this;
                mCurrentInterface = new Enhance(bitmap2, editPhotoActivity4, editPhotoActivity4.mScaleImage);
            } else if (i == 2) {
                type = 5;

                mScaleImage.setVisibility(View.VISIBLE);
                Bitmap bitmap3 = mCurrentBitmap;
                EditPhotoActivity editPhotoActivity6 = EditPhotoActivity.this;
                mCurrentInterface = new Height(bitmap3, editPhotoActivity6, editPhotoActivity6.mScaleImage);
            } else if (i == 3) {
                type = 6;

                mScaleImage.setVisibility(View.VISIBLE);
                Bitmap bitmap4 = mCurrentBitmap;
                EditPhotoActivity editPhotoActivity8 = EditPhotoActivity.this;
                mCurrentInterface = new Waist(bitmap4, editPhotoActivity8, editPhotoActivity8.mScaleImage);
            } else if (i == 4) {
                type = 7;

                mScaleImage.setVisibility(View.VISIBLE);
                Bitmap bitmap5 = mCurrentBitmap;
                EditPhotoActivity editPhotoActivity10 = EditPhotoActivity.this;
                mCurrentInterface = new Hips(bitmap5, editPhotoActivity10, editPhotoActivity10.mScaleImage);
            } else if (i == 5) {
                type = 8;
                mScaleImage.setVisibility(View.VISIBLE);
                EditPhotoActivity editPhotoActivity11 = EditPhotoActivity.this;
                Bitmap bitmap6 = editPhotoActivity11.mCurrentBitmap;
                EditPhotoActivity editPhotoActivity12 = EditPhotoActivity.this;
                editPhotoActivity11.mCurrentInterface = new SkinColor(bitmap6, editPhotoActivity12, editPhotoActivity12.mScaleImage);
            } else if (i == 6) {
                type = 1;
                //  createChestAndFace();
            } else if (i == 7) {
                type = 2;
                //  createChestAndFace();
            }
        }
    };
    List<MenuInfo> mMenuInfo;
    Bitmap mOriginalBitmap;
    public ImageView mRedoButton;
    ScaleImage mScaleImage;
    public ImageView mShare;
    public ConstraintLayout mTopUtils;
    public ImageView mUndoButton;
    BroadcastReceiver startSaveReceiver = new BroadcastReceiver() {

        public void onReceive(final Context context, Intent intent) {
            new Thread(new Runnable() {


                public void run() {
                    EditPhotoActivity.this.uri = CapturePhotoUtils.insertImage(EditPhotoActivity.this.getContentResolver(), EditPhotoActivity.this.mCurrentBitmap, "BodyTune_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + "_" + ((int) (Math.random() * 100.0d)), "");
                    Intent intent = new Intent("photoWasSaved");
                    intent.putExtra("uri", EditPhotoActivity.this.uri);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }
            }).start();
        }
    };
    String uri;


    public interface BackPressed {
        void onBackPressed(boolean z);
    }

    @Override
    public void onCreate(Bundle bundle2) {
        super.onCreate(bundle2);
        setContentView(R.layout.activity_edit_photo);
        setColorStatusBar();
        this.bundle = new Bundle();
        ArrayList arrayList = new ArrayList();
        this.mMenuInfo = arrayList;

        arrayList.add(new MenuInfo(R.drawable.main_menu_icon_refine, getString(R.string.refine)));
        this.mMenuInfo.add(new MenuInfo(R.drawable.enhance_big, getString(R.string.enhance)));
        this.mMenuInfo.add(new MenuInfo(R.drawable.main_menu_icon_height, getString(R.string.height)));
        this.mMenuInfo.add(new MenuInfo(R.drawable.main_menu_icon_waist, getString(R.string.waist)));
        this.mMenuInfo.add(new MenuInfo(R.drawable.main_menu_icon_hips, getString(R.string.hips)));
        this.mMenuInfo.add(new MenuInfo(R.drawable.main_menu_icon_skin_color, getString(R.string.skin_color)));
//        this.mMenuInfo.add(new MenuInfo(R.drawable.ic_chest, getString(R.string.chest)));
//        this.mMenuInfo.add(new MenuInfo(R.drawable.ic_face, getString(R.string.face)));
        LocalBroadcastManager.getInstance(this).registerReceiver(this.startSaveReceiver, new IntentFilter("startSaveBitmap"));
        if (bundle2 == null) {
            String[] list = getFilesDir().list();
            for (String str : list) {
                if (str.endsWith(".jpg") || str.endsWith(".png")) {
                    deleteFile(str);
                }
            }
            String stringExtra = getIntent().getStringExtra("path");
            this.mImagePath = stringExtra;

            new Thread(new Runnable() {
                public void run() {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inMutable = true;
                    try {
                        int attributeInt = new ExifInterface(new File(EditPhotoActivity.this.mImagePath).getAbsolutePath()).getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                        int i = 0;
                        if (attributeInt == 3) {
                            i = 180;
                        } else if (attributeInt == 6) {
                            i = 90;
                        } else if (attributeInt == 8) {
                            i = 270;
                        }
                        Bitmap decodeFile = BitmapFactory.decodeFile(EditPhotoActivity.this.mImagePath, options);
                        if (decodeFile == null) {
                            EditPhotoActivity.this.finish();
                            return;
                        }
                        int width = decodeFile.getWidth();
                        int height = decodeFile.getHeight();
                        if (((float) Math.max(width, height)) > 1500.0f) {
                            float max = 1500.0f / ((float) Math.max(width, height));
                            width = (int) (((float) width) * max);
                            height = (int) (((float) height) * max);
                            EditPhotoActivity.this.mOriginalBitmap = Bitmap.createScaledBitmap(decodeFile, width, height, true);
                            decodeFile.recycle();
                        } else {
                            EditPhotoActivity.this.mOriginalBitmap = decodeFile;
                        }
                        if (i != 0) {
                            Matrix matrix = new Matrix();
                            matrix.postRotate((float) i);
                            Bitmap createBitmap = Bitmap.createBitmap(EditPhotoActivity.this.mOriginalBitmap, 0, 0, width, height, matrix, true);
                            EditPhotoActivity.this.mOriginalBitmap.recycle();
                            EditPhotoActivity.this.mOriginalBitmap = createBitmap;
                        }
                        if (EditPhotoActivity.this.mOriginalBitmap == null) {
                            EditPhotoActivity.this.finish();
                            return;
                        }
                        if (!EditPhotoActivity.this.mOriginalBitmap.isMutable()) {
                            Bitmap copy = EditPhotoActivity.this.mOriginalBitmap.copy(Bitmap.Config.ARGB_8888, true);
                            EditPhotoActivity.this.mOriginalBitmap.recycle();
                            EditPhotoActivity.this.mOriginalBitmap = copy;
                        }
                        File file = new File(EditPhotoActivity.this.mImagePath);
                        if (file.getParentFile().equals(EditPhotoActivity.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)) && file.delete()) {
                            EditPhotoActivity.this.sendEvent("Camera - Tap");
                        }
                        EditPhotoActivity editPhotoActivity = EditPhotoActivity.this;
                        editPhotoActivity.mCurrentBitmap = editPhotoActivity.mOriginalBitmap.copy(Bitmap.Config.ARGB_8888, true);
                        new Thread(new Runnable() {

                            public void run() {
                                try {
                                    FileOutputStream openFileOutput = EditPhotoActivity.this.openFileOutput("original.png", 0);
                                    EditPhotoActivity.this.mOriginalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, openFileOutput);
                                    openFileOutput.close();
                                } catch (Exception e) {
                                    Log.d("My", "Error (save Original): " + e.getMessage());
                                }
                            }
                        }).start();
                        EditPhotoActivity.this.handler.post(new Runnable() {

                            public final void run() {
                                lambda$run$0$MainActivity$3();
                            }
                        });
                    } catch (IOException | OutOfMemoryError e) {

                        EditPhotoActivity.this.finish();
                    }
                }

                public void lambda$run$0$MainActivity$3() {
                    EditPhotoActivity.this.onCreated();
                }
            }).start();
            return;
        }

        this.mIdLast = bundle2.getInt("mIdLast");
        int i = bundle2.getInt("mIdCurrent");
        this.mIdCurrent = i;
        this.mIdRequisite = i;
        new Thread(new Runnable() {
            public final void run() {
                EditPhotoActivity.this.lambda$onCreate$0$MainActivity();
            }
        }).start();


    }

    public void lambda$onCreate$0$MainActivity() {
        boolean z = true;
        int i = 0;
        while (z) {
            try {
                StringBuilder sb = new StringBuilder();
                sb.append("tool_");
                i++;
                sb.append(i);
                sb.append(".jpg");
                if (!deleteFile(sb.toString())) {
                    if (!deleteFile("tool_" + i + ".png")) {
                        z = false;
                    }
                }
                z = true;
            } catch (Exception e) {
                e.printStackTrace();
                this.handler.post(new Runnable() {

                    public void run() {
                        EditPhotoActivity.this.finish();
                    }
                });
                return;
            }
        }
        Bitmap decodeStream = null;
        try {
            decodeStream = BitmapFactory.decodeStream(new FileInputStream(new File(getFilesDir(), "original.png")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap finalDecodeStream = decodeStream;
        this.handler.post(new Runnable() {
            public void run() {
                EditPhotoActivity.this.mOriginalBitmap = finalDecodeStream;
                try {
                    File filesDir = EditPhotoActivity.this.getFilesDir();
                    File file = new File(filesDir, "main_" + EditPhotoActivity.this.mIdCurrent + ".png");
                    EditPhotoActivity.this.mCurrentBitmap = BitmapFactory.decodeStream(new FileInputStream(file));
                    if (EditPhotoActivity.this.mCurrentBitmap == null) {
                        EditPhotoActivity.this.mCurrentBitmap = EditPhotoActivity.this.mOriginalBitmap.copy(Bitmap.Config.ARGB_8888, true);
                        EditPhotoActivity.this.mIdCurrent = 0;
                        EditPhotoActivity.this.mIdRequisite = 0;
                    } else if (!EditPhotoActivity.this.mCurrentBitmap.isMutable()) {
                        Bitmap copy = EditPhotoActivity.this.mCurrentBitmap.copy(Bitmap.Config.ARGB_8888, true);
                        EditPhotoActivity.this.mCurrentBitmap.recycle();
                        EditPhotoActivity.this.mCurrentBitmap = copy;
                    }
                } catch (FileNotFoundException e) {
                    EditPhotoActivity editPhotoActivity = EditPhotoActivity.this;
                    editPhotoActivity.mCurrentBitmap = editPhotoActivity.mOriginalBitmap.copy(Bitmap.Config.ARGB_8888, true);
                    EditPhotoActivity.this.mIdCurrent = 0;
                    EditPhotoActivity.this.mIdRequisite = 0;
                    e.printStackTrace();
                }
                EditPhotoActivity.this.onCreated();
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle bundle2) {
        super.onSaveInstanceState(bundle2);
        bundle2.putInt("mIdCurrent", this.mIdCurrent);
        bundle2.putInt("mIdLast", this.mIdLast);
    }

    @SuppressLint("WrongConstant")
    public void onCreated() {
        this.mScaleImage = (ScaleImage) findViewById(R.id.mScaleImage);
        this.mLoading = (FrameLayout) findViewById(R.id.loading);
        this.mTopUtils = (ConstraintLayout) findViewById(R.id.mTopUtils);
        this.mShare = (ImageView) findViewById(R.id.mShare);
        this.mBack = (ImageView) findViewById(R.id.mBack);
        this.mBefore = (FrameLayout) findViewById(R.id.mBefore);
        this.mMenuHome = (RecyclerView) findViewById(R.id.menuHome);
        this.mUndoButton = (ImageView) findViewById(R.id.mUndoButton);
        this.mRedoButton = (ImageView) findViewById(R.id.mRedoButton);
        this.mScaleImage.setImageBitmap(this.mCurrentBitmap);
        this.isBlocked = false;
        this.mLoading.setVisibility(8);
        this.mMenuHome.setLayoutManager(new LinearLayoutManager(this, 0, false));
        MainMenuAdapter mainMenuAdapter = new MainMenuAdapter(this.mMenuInfo, this);
        this.mAdapter = mainMenuAdapter;
        mainMenuAdapter.setOnItemClickListener(this.mMenuHomeClickListener);
        this.mMenuHome.setAdapter(this.mAdapter);
        this.mShare.setOnClickListener(this);
        this.mBack.setOnClickListener(this);
        this.mUndoButton.setOnClickListener(this);
        this.mRedoButton.setOnClickListener(this);
        sendEvent("Page - Edit zone");

    }

    @Override
    public void onResume() {
        super.onResume();
        findViewById(R.id.page).onCancelPendingInputEvents();

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    public void saveEffect(Bitmap bitmap) {
        this.mCurrentBitmap = this.mCurrentBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(this.mCurrentBitmap);
        this.mCanvas = canvas;
        canvas.drawBitmap(bitmap, 0.0f, 0.0f, (Paint) null);
        addMainState();
        mScaleImage.setImageBitmap(mCurrentBitmap);
    }

    public void addMainState() {
        sendEvent("Tool - V");
        int i = this.mIdCurrent + 1;
        this.mIdCurrent = i;
        if (i <= this.mIdLast) {
            while (i <= this.mIdLast) {
                deleteFile("main_" + i + ".png");
                i++;
            }
        }
        int i2 = this.mIdCurrent;
        this.mIdLast = i2;
        this.mIdRequisite = i2;
        final Bitmap copy = this.mCurrentBitmap.copy(Bitmap.Config.ARGB_8888, true);
        if (mCurrentInterface != null) {
            this.mCurrentInterface.onBackPressed(true);
        }
        final String str = "main_" + this.mIdCurrent + ".png";
        new Thread(new Runnable() {


            public void run() {
                try {
                    FileOutputStream openFileOutput = EditPhotoActivity.this.openFileOutput(str, 0);
                    copy.compress(Bitmap.CompressFormat.PNG, 100, openFileOutput);
                    openFileOutput.close();
                    copy.recycle();
                } catch (Exception e) {
                    Log.d("My", "Error (save Bitmap): " + e.getMessage());
                }
            }
        }).start();
    }

    public void sendEvent(String str) {
        this.bundle.putString(str, str);

    }

    @Override
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i2 == -1 && i == SAVED_PHOTO) {
            finish();
        }
    }

    @SuppressLint("WrongConstant")
    private void close(boolean z) {

        mScaleImage.setVisibility(View.VISIBLE);

        this.mIdCurrent = -1;
        if (z) {
            sendEvent("Enhance - V");
        } else {
            sendEvent("Tool - X");
            sendEvent("Enhance - X");
        }
        this.mMenuEnhance.setVisibility(View.VISIBLE);

        mShare.setOnClickListener(this);
        mBack.setOnClickListener(this);
        mBefore.setOnTouchListener(this);
        mMenuEnhance.setVisibility(View.GONE);

        findViewById(R.id.saveCloseContainer).setVisibility(View.GONE);
        mTopUtils.setVisibility(0);
        findViewById(R.id.menuHome).setVisibility(0);
    }

    public void onClick(View view) {
        if (!this.isBlocked) {
            switch (view.getId()) {
                case R.id.mDoneButton:
                    Toast.makeText(this, "save bitmap", Toast.LENGTH_SHORT).show();
                    return;
                case R.id.mCancelButton:
                    close(false);
                    return;
                case R.id.mBack:
                    if (!this.isBlocked) {
                        navigateToGallery();
                        return;
                    }
                    return;
                case R.id.mRedoButton:
                    int i = this.mIdRequisite;
                    if (i < this.mIdLast) {
                        int i2 = i + 1;
                        this.mIdRequisite = i2;
                        CapturePhotoUtils.getBitmapFromDisk(i, i2, "main_" + this.mIdRequisite + ".png", this, this);
                        sendEvent("Tool - Forward");
                        return;
                    }
                    return;
                case R.id.mShare:
                    sendEvent("Photo saved");
                    startActivityForResult(new Intent(this, ExportPhotoActivity.class), SAVED_PHOTO);
                    return;
                case R.id.mUndoButton:
                    int i3 = this.mIdRequisite;
                    if (i3 > 1) {
                        int i4 = i3 - 1;
                        this.mIdRequisite = i4;
                        CapturePhotoUtils.getBitmapFromDisk(i3, i4, "main_" + this.mIdRequisite + ".png", this, this);
                        sendEvent("Tool - Back");
                        return;
                    } else if (i3 == 1) {
                        this.mIdRequisite = 0;
                        this.mIdCurrent = 0;
                        this.mCurrentBitmap.recycle();
                        Bitmap copy = this.mOriginalBitmap.copy(Bitmap.Config.ARGB_8888, true);
                        this.mCurrentBitmap = copy;
                        this.mScaleImage.setImageBitmap(copy);
                        sendEvent("Tool - Back");
                        this.mScaleImage.resetToFitCenter();
                        return;
                    } else {
                        return;
                    }
                default:
                    return;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (int i = 0; i <= this.mIdLast; i++) {
            deleteFile("main_" + i + ".png");
        }
        deleteFile("original.png");
        Bitmap bitmap = this.mOriginalBitmap;
        if (bitmap != null) {
            bitmap.recycle();
        }
        Bitmap bitmap2 = this.mCurrentBitmap;
        if (bitmap2 != null) {
            bitmap2.recycle();
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(this.startSaveReceiver);
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onBackPressed() {
        if (!this.isBlocked) {
            if (this.mTopUtils.getVisibility() == 0) {
                super.onBackPressed();
                return;
            }
            BackPressed backPressed = this.mCurrentInterface;
            if (backPressed != null) {
                backPressed.onBackPressed(false);
            }
        }
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action == 0) {
            this.mScaleImage.setImageBitmap(this.mOriginalBitmap);
        } else if (action == 1 || action == 3) {
            this.mScaleImage.setImageBitmap(this.mCurrentBitmap);
        }
        return true;
    }

    @Override
    public void loadResponse(Bitmap bitmap, int i, int i2) {
        if (bitmap == null) {
            this.mIdRequisite = i;
        } else if ((i2 > i && this.mIdCurrent < i2) || (i2 < i && i2 < this.mIdCurrent)) {
            this.mCurrentBitmap.recycle();
            if (!bitmap.isMutable()) {
                this.mCurrentBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                bitmap.recycle();
            } else {
                this.mCurrentBitmap = bitmap;
            }
            this.mScaleImage.setImageBitmap(this.mCurrentBitmap);
            this.mIdCurrent = i2;
            this.mIdRequisite = i2;
            this.mScaleImage.resetToFitCenter();
        }
    }

    @SuppressLint("WrongConstant")
    private void navigateToGallery() {
        Intent intent = new Intent(this, CreateNewPhoto1Activity.class);
        intent.addFlags(67108864);
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i != 4) {
            return super.onKeyDown(i, keyEvent);
        }
        navigateToGallery();
        return true;
    }

    @SuppressLint("WrongConstant")
    private void setupNew() {


        mBottomUtils = findViewById(R.id.mBottomUtils);
        mCancelButton = findViewById(R.id.mCancelButton);
        mDoneButton = findViewById(R.id.mDoneButton);
        mParent = findViewById(R.id.page);
        mMenuEnhance = findViewById(R.id.seekbarWithTwoIcon);
        mSeekbar = findViewById(R.id.SWTI_seekbar);

        mUndoButton.setOnClickListener(this);
        mRedoButton.setOnClickListener(this);
        mCancelButton.setOnClickListener(this);
        mDoneButton.setOnClickListener(this);


        ((ImageView) findViewById(R.id.SWTI_1)).setImageResource(R.drawable.enhance_small);
        ((ImageView) findViewById(R.id.SWTI_2)).setImageResource(R.drawable.enhance_big);
        isBlocked = false;

        mTopUtils.setVisibility(0);
        mSeekbar.setAbsoluteMinMaxValue(-50, 50);
        mSeekbar.setProgress(0.0d);
        findViewById(R.id.saveCloseContainer).setVisibility(View.VISIBLE);
        mTopUtils.setVisibility(View.GONE);
        findViewById(R.id.menuHome).setVisibility(View.GONE);
        mMenuEnhance.setVisibility(0);


        mSeekbar.setAbsoluteMinMaxValue(-50, 50);
        mSeekbar.setProgress(0.0d);


        mSeekbar.setAbsoluteMinMaxValue(-50, 50);
        this.mSeekbar.setProgress(0.0d);

    }


    private int type = 1;
    private FrameLayout mDoneButton;
    private ConstraintLayout mParent;
    private LinearLayout mMenuEnhance;
    private StartPointSeekBar mSeekbar;
    private FrameLayout mCancelButton;
    private ConstraintLayout mBottomUtils;
    public float startX;
    public float startY;
    public CGEDeformFilterWrapper mDeformWrapper;

    public void checkNullSix() {

        float width = (float) mOriginalBitmap.getWidth();
        float height = (float) mOriginalBitmap.getHeight();
        float min = Math.min(((float) glSurfaceView.getRenderViewport().width) / width, ((float) glSurfaceView.getRenderViewport().height) / height);
        if (min < 1.0f) {
            width *= min;
            height *= min;
        }
        mDeformWrapper = CGEDeformFilterWrapper.create((int) width, (int) height, 10.0f);
        mDeformWrapper.setUndoSteps(ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION);
        if (mDeformWrapper != null) {
            CGEImageHandler imageHandler = glSurfaceView.getImageHandler();
            imageHandler.setFilterWithAddres(mDeformWrapper.getNativeAddress());
            imageHandler.processFilters();
        }
    }

    //
//    public void onScrollCustom(int i, float f, float f2) {
//
//        if (mDeformWrapper != null) {
//            mDeformWrapper.restore();
//            for (Sticker next : photoEditorView.getStickers()) {
//                PointF mappedCenterPoint2 = ((BeautySticker) next).getMappedCenterPoint2();
//                RectF mappedBound = next.getMappedBound();
//                for (int i2 = 0; i2 < Math.abs(i); i2++) {
//                    if (i > 0) {
//                        mDeformWrapper.bloatDeform(mappedCenterPoint2.x, mappedCenterPoint2.y, f, f2, (float) (mappedBound.right - mappedBound.left), 0.03f);
//                    } else if (i < 0) {
//                        mDeformWrapper.wrinkleDeform(mappedCenterPoint2.x, mappedCenterPoint2.y, f, f2, (float) (mappedBound.right - mappedBound.left), 0.03f);
//                    }
//                }
//            }
//        }
//    }
    public void setColorStatusBar() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        window.setNavigationBarColor(getResources().getColor(R.color.white));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }
}
