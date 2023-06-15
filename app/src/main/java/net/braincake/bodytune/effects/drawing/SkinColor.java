package net.braincake.bodytune.effects.drawing;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageSoftLightBlendFilter;

import net.braincake.bodytune.activity.EditPhotoActivity;
import net.braincake.bodytune.R;
import net.braincake.bodytune.adapters.MainMenuAdapter;
import net.braincake.bodytune.adapters.SkinColorListAdapter;
import net.braincake.bodytune.controls.ApplicationClass;
import net.braincake.bodytune.controls.CapturePhotoUtils;
import net.braincake.bodytune.controls.MenuInfo;
import net.braincake.bodytune.controls.ScaleImage;
import net.braincake.bodytune.controls.StartPointSeekBar;

public class SkinColor implements ScaleImage.TouchInterface, View.OnClickListener, CapturePhotoUtils.PhotoLoadResponse, EditPhotoActivity.BackPressed {
    private int alpha;
    private Bitmap colorBitmap;
    private GPUImage gpuImage;
    private final Handler handler = new Handler();
    private boolean isDrawing;
    private boolean isMoved;
    private float lastX;
    private float lastY;
    private EditPhotoActivity mActivity;
    private ConstraintLayout mBottomUtils;
    private FrameLayout mCancelButton;
    private Canvas mCanvas;
    private Paint mChangeColorPaint;
    private SkinColorListAdapter mColorAdapter;
    private Canvas mColorCanvas;
    private SkinColorListAdapter.OnItemClickListener mColorClickListener = new SkinColorListAdapter.OnItemClickListener() {

        @SuppressLint("WrongConstant")
        @Override
        public void onItemClick(final int i) {
            SkinColor.this.mActivity.mLoading.setVisibility(0);
            SkinColor.this.mActivity.isBlocked = true;
            SkinColor.this.mSelectedColor.drawColor(ApplicationClass.listOfSkinColor[i], PorterDuff.Mode.SRC_IN);
            SkinColor.this.selectedColor.invalidate();
            new Thread(new Runnable() {

                public void run() {
                    SkinColor.this.mColorCanvas.drawColor(ApplicationClass.listOfSkinColor[i]);
                    SkinColor.this.mEffectCanvas.drawBitmap(SkinColor.this.gpuImage.getBitmapWithFilterApplied(), 0.0f, 0.0f, (Paint) null);
                    SkinColor.this.handler.post(SkinColor.this.runnable);
                }
            }).start();
        }
    };
    private Bitmap mCurrentBitmap;
    private Paint mCurrentPaint;
    private Paint mDecodePaint;
    private FrameLayout mDoneButton;
    private Paint mDrawPaint;
    private Bitmap mEffect;
    private Canvas mEffectCanvas;
    private Paint mEncodePaint;
    private Paint mEraserPaint;
    private int mIdCurrent;
    private int mIdLast;
    private int mIdRequisite;
    private BitmapDrawable[] mLayers = new BitmapDrawable[2];
    private MainMenuAdapter.OnItemClickListener mMenuHomeClickListener = new MainMenuAdapter.OnItemClickListener() {


        @Override
        public void onItemClick(int i) {
            SkinColor skinColor = SkinColor.this;
            skinColor.mCurrentPaint = i == 0 ? skinColor.mDrawPaint : skinColor.mEraserPaint;
        }
    };
    private List<MenuInfo> mMenuInfo;
    private Bitmap mOriginalBitmap;
    private ConstraintLayout mParent;
    private ScaleImage mScaleImage;
    private StartPointSeekBar mSeekBar;
    private Canvas mSelectedColor;
    private Paint mSimplePaint = new Paint();
    private int maxSizeOfBitmap;
    private RecyclerView recyclerView;
    private Runnable runnable;
    private View selectedColor;

    public SkinColor(Bitmap bitmap, EditPhotoActivity editPhotoActivity, ScaleImage scaleImage) {
        this.mOriginalBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        this.mActivity = editPhotoActivity;
        this.mScaleImage = scaleImage;
        preCreate();
    }

    @SuppressLint("WrongConstant")
    private void preCreate() {
        this.mActivity.mLoading.setVisibility(0);
        Paint paint = new Paint();
        this.mChangeColorPaint = paint;
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        Bitmap createBitmap = Bitmap.createBitmap(this.mOriginalBitmap.getWidth(), this.mOriginalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        this.colorBitmap = createBitmap;
        if (!createBitmap.isMutable()) {
            Bitmap copy = this.colorBitmap.copy(Bitmap.Config.ARGB_8888, true);
            this.colorBitmap.recycle();
            this.colorBitmap = copy;
        }
        this.mColorCanvas = new Canvas(this.colorBitmap);
        GPUImage gPUImage = new GPUImage(this.mActivity);
        this.gpuImage = gPUImage;
        gPUImage.setImage(this.mOriginalBitmap);
        GPUImageSoftLightBlendFilter gPUImageSoftLightBlendFilter = new GPUImageSoftLightBlendFilter();
        gPUImageSoftLightBlendFilter.setBitmap(this.colorBitmap);
        this.gpuImage.setFilter(gPUImageSoftLightBlendFilter);
        this.runnable = new Runnable() {

            public void run() {
                SkinColor.this.mActivity.mLoading.setVisibility(8);
                SkinColor.this.mActivity.isBlocked = false;
                SkinColor.this.mCanvas.drawBitmap(SkinColor.this.mEffect, 0.0f, 0.0f, SkinColor.this.mChangeColorPaint);
                SkinColor.this.mLayers[1].invalidateSelf();
            }
        };
        new Thread(new Runnable() {
            public void run() {
                SkinColor.this.mColorCanvas.drawColor(ApplicationClass.listOfSkinColor[0]);
                SkinColor skinColor = SkinColor.this;
                skinColor.mEffect = skinColor.gpuImage.getBitmapWithFilterApplied();
                if (!SkinColor.this.mEffect.isMutable()) {
                    Bitmap copy = SkinColor.this.mEffect.copy(Bitmap.Config.ARGB_8888, true);
                    SkinColor.this.mEffect.recycle();
                    SkinColor.this.mEffect = copy;
                }
                SkinColor.this.mEffectCanvas = new Canvas(SkinColor.this.mEffect);
                SkinColor.this.handler.post(new Runnable() {

                    public void run() {
                        SkinColor.this.mActivity.mLoading.setVisibility(8);
                        SkinColor.this.mActivity.isBlocked = false;
                        SkinColor.this.onCreate();
                    }
                });
            }
        }).start();
    }

    @SuppressLint("WrongConstant")
    private void onCreate() {
        this.mParent = this.mActivity.findViewById(R.id.page);
        this.mBottomUtils = this.mActivity.findViewById(R.id.mBottomUtils);
        this.mCancelButton = this.mActivity.findViewById(R.id.mCancelButton);
        this.mDoneButton = this.mActivity.findViewById(R.id.mDoneButton);
        ArrayList arrayList = new ArrayList();
        this.mMenuInfo = arrayList;
        arrayList.add(new MenuInfo(R.drawable.skin_draw, this.mActivity.getString(R.string.draw)));
        this.mMenuInfo.add(new MenuInfo(R.drawable.skin_erase, this.mActivity.getString(R.string.erase)));
        MainMenuAdapter mainMenuAdapter = new MainMenuAdapter(this.mMenuInfo, this.mActivity);
        mainMenuAdapter.setOnItemClickListener(this.mMenuHomeClickListener);
        mainMenuAdapter.setSelectedMode(true);
        this.mActivity.mMenuHome.setAdapter(mainMenuAdapter);
        View inflate = View.inflate(this.mActivity, R.layout.item_skincolor, null);
        this.selectedColor = inflate;
        ImageView imageView = inflate.findViewById(R.id.colorPlace);
        Bitmap copy = ((BitmapDrawable) imageView.getDrawable()).getBitmap().copy(Bitmap.Config.ARGB_8888, true);
        this.mSelectedColor = new Canvas(copy);
        imageView.setImageBitmap(copy);
        this.mSelectedColor.drawColor(ApplicationClass.listOfSkinColor[0], PorterDuff.Mode.SRC_IN);
        this.selectedColor.setId(R.id.selectedColor);
        RecyclerView recyclerView2 = new RecyclerView(this.mActivity);
        this.recyclerView = recyclerView2;
        recyclerView2.setLayoutManager(new LinearLayoutManager(this.mActivity, 1, false));
        SkinColorListAdapter skinColorListAdapter = new SkinColorListAdapter(this.mActivity);
        this.mColorAdapter = skinColorListAdapter;
        skinColorListAdapter.setOnItemClickListener(this.mColorClickListener);
        this.recyclerView.setAdapter(this.mColorAdapter);
        this.recyclerView.setOverScrollMode(2);
        this.mSeekBar = new StartPointSeekBar(this.mActivity);
        Paint paint = new Paint(1);
        this.mDrawPaint = paint;
        paint.setShader(new BitmapShader(this.mEffect, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT));
        this.mDrawPaint.setStyle(Paint.Style.STROKE);
        this.mDrawPaint.setStrokeJoin(Paint.Join.ROUND);
        this.mDrawPaint.setStrokeCap(Paint.Cap.ROUND);
        Paint paint2 = new Paint(1);
        this.mEraserPaint = paint2;
        paint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        this.mEraserPaint.setStyle(Paint.Style.STROKE);
        this.mEraserPaint.setStrokeJoin(Paint.Join.ROUND);
        this.mEraserPaint.setStrokeCap(Paint.Cap.ROUND);
        Paint paint3 = new Paint();
        this.mDecodePaint = paint3;
        paint3.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        this.mDecodePaint.setColorFilter(new ColorMatrixColorFilter(new ColorMatrix(new float[]{0.0f, 0.0f, 0.0f, 0.0f, 255.0f, 0.0f, 0.0f, 0.0f, 0.0f, 255.0f, 0.0f, 0.0f, 0.0f, 0.0f, 255.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f})));
        Paint paint4 = new Paint();
        this.mEncodePaint = paint4;
        paint4.setColorFilter(new ColorMatrixColorFilter(new ColorMatrix(new float[]{0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 255.0f})));
        this.mCurrentPaint = this.mDrawPaint;
        Bitmap createBitmap = Bitmap.createBitmap(this.mOriginalBitmap.getWidth(), this.mOriginalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        this.mCurrentBitmap = createBitmap;
        if (!createBitmap.isMutable()) {
            Bitmap copy2 = this.mCurrentBitmap.copy(Bitmap.Config.ARGB_8888, true);
            this.mCurrentBitmap.recycle();
            this.mCurrentBitmap = copy2;
        }
        this.maxSizeOfBitmap = Math.max(this.mOriginalBitmap.getWidth(), this.mOriginalBitmap.getHeight());
        this.mCanvas = new Canvas(this.mCurrentBitmap);
        this.mLayers[0] = new BitmapDrawable(this.mActivity.getResources(), this.mOriginalBitmap);
        this.mLayers[1] = new BitmapDrawable(this.mActivity.getResources(), this.mCurrentBitmap);
        this.mScaleImage.setImageDrawable(new LayerDrawable(this.mLayers));
        this.mScaleImage.setOnTouchInterface(this);
        this.mActivity.mUndoButton.setOnClickListener(this);
        this.mActivity.mRedoButton.setOnClickListener(this);
        this.mCancelButton.setOnClickListener(this);
        this.mDoneButton.setOnClickListener(this);
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(Math.round(((float) (this.mBottomUtils.getWidth() - (((ImageView) this.mActivity.findViewById(R.id.arrowImage)).getDrawable().getIntrinsicWidth() * 2))) * 0.75f), -2);
        layoutParams.leftToLeft = 0;
        layoutParams.rightToRight = 0;
        layoutParams.topToTop = 0;
        layoutParams.bottomToBottom = 0;
        this.mBottomUtils.addView(this.mSeekBar, 1, layoutParams);
        ConstraintLayout.LayoutParams layoutParams2 = new ConstraintLayout.LayoutParams(-2, -2);
        layoutParams2.bottomToTop = this.mBottomUtils.getId();
        layoutParams2.rightToRight = this.mBottomUtils.getId();
        this.mParent.addView(this.selectedColor, 2, layoutParams2);
        ConstraintLayout.LayoutParams layoutParams3 = new ConstraintLayout.LayoutParams(-2, 0);
        layoutParams3.bottomToTop = this.selectedColor.getId();
        layoutParams3.topToTop = 0;
        layoutParams3.rightToRight = this.mBottomUtils.getId();
        layoutParams3.bottomMargin = copy.getHeight() / 3;
        this.mParent.addView(this.recyclerView, 3, layoutParams3);
        this.recyclerView.setVerticalFadingEdgeEnabled(true);
        this.recyclerView.setFadingEdgeLength(layoutParams3.bottomMargin);
        this.alpha = 255;
        this.mSeekBar.setProgress(100.0d);
        this.mSeekBar.setOnSeekBarChangeListener(new StartPointSeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStartTrackingTouch(StartPointSeekBar startPointSeekBar) {
            }

            @Override
            public void onStopTrackingTouch(StartPointSeekBar startPointSeekBar) {
            }

            @Override
            public void onOnSeekBarValueChange(StartPointSeekBar startPointSeekBar, long j) {
                SkinColor.this.alpha = Math.round(((float) j) * 2.55f);
                SkinColor.this.mLayers[1].setAlpha(SkinColor.this.alpha);
            }
        });
        this.mActivity.mBefore.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View view, MotionEvent motionEvent) {

                int action = motionEvent.getAction();
                if (action == 0) {
                    SkinColor.this.mLayers[1].setAlpha(0);
                } else if (action == 1 || action == 3) {
                    SkinColor.this.mLayers[1].setAlpha(SkinColor.this.alpha);
                }
                return true;
            }
        });
        this.mActivity.mShare.setOnClickListener(null);
        this.mActivity.mBack.setOnClickListener(null);
        this.mActivity.mTopUtils.setVisibility(8);
        this.mLayers[1].setAlpha(255);
        this.mActivity.findViewById(R.id.saveCloseContainer).setVisibility(0);
        this.mActivity.isBlocked = false;
        this.mActivity.mLoading.setVisibility(8);
        ((TextView) this.mActivity.findViewById(R.id.nameOfTool)).setText(this.mActivity.getResources().getString(R.string.skin_color));
        this.mActivity.sendEvent("Skin Color - open");
    }

    @SuppressLint("WrongConstant")
    private void close(boolean z) {
        for (int i = 0; i <= this.mIdLast; i++) {
            EditPhotoActivity editPhotoActivity = this.mActivity;
            editPhotoActivity.deleteFile("tool_" + i + ".jpg");
        }
        this.mIdCurrent = -1;
        this.mCurrentBitmap.recycle();
        this.mEffect.recycle();
        this.colorBitmap.recycle();
        this.mParent.removeView(this.recyclerView);
        this.mParent.removeView(this.selectedColor);
        this.mColorAdapter.setOnItemClickListener(null);
        this.mScaleImage.setOnTouchInterface(null);
        this.mActivity.mUndoButton.setOnClickListener(this.mActivity);
        this.mActivity.mRedoButton.setOnClickListener(this.mActivity);
        this.mCancelButton.setOnClickListener(null);
        this.mDoneButton.setOnClickListener(null);
        this.mSeekBar.setOnSeekBarChangeListener(null);
        this.mBottomUtils.removeView(this.mSeekBar);
        this.mActivity.mShare.setOnClickListener(this.mActivity);
        this.mActivity.mBack.setOnClickListener(this.mActivity);
        this.mActivity.mBefore.setOnTouchListener(this.mActivity);
        this.mScaleImage.setImageBitmap(this.mActivity.mCurrentBitmap);
        this.mActivity.mTopUtils.setVisibility(0);
        this.mActivity.findViewById(R.id.saveCloseContainer).setVisibility(8);
        this.mActivity.mMenuHome.setAdapter(this.mActivity.mAdapter);
        this.mMenuInfo.clear();
        if (z) {
            this.mActivity.sendEvent("Skin Color - V");
            return;
        }
        this.mActivity.sendEvent("Tool - X");
        this.mActivity.sendEvent("Skin Color - X");
    }

    private void save() {
        this.mSimplePaint.setAlpha(this.alpha);
        this.mActivity.mCurrentBitmap.recycle();
        this.mActivity.mCurrentBitmap = this.mOriginalBitmap;
        new Canvas(this.mOriginalBitmap).drawBitmap(this.mCurrentBitmap, 0.0f, 0.0f, this.mSimplePaint);
        this.mActivity.addMainState();
    }

    @SuppressLint("WrongConstant")
    @Override
    public void touch(int i, float f, float f2, float f3) {
        if (i == 0) {
            float f4 = ((float) this.maxSizeOfBitmap) / (f3 * 20.0f);
            if (f4 != this.mCurrentPaint.getStrokeWidth()) {
                this.mCurrentPaint.setStrokeWidth(f4);
                this.mCurrentPaint.setMaskFilter(new BlurMaskFilter(f4 / 2.0f, BlurMaskFilter.Blur.SOLID));
            }
            this.mBottomUtils.setVisibility(4);
            this.recyclerView.setVisibility(4);
            this.selectedColor.setVisibility(4);
            this.lastX = f;
            this.lastY = f2;
            this.isDrawing = true;
            this.isMoved = false;
        } else if (i != 1) {
            if (i == 2) {
                if (this.isMoved) {
                    int i2 = this.mIdCurrent + 1;
                    this.mIdCurrent = i2;
                    while (i2 <= this.mIdLast) {
                        this.mActivity.deleteFile("tool_" + i2 + ".jpg");
                        i2++;
                    }
                    int i3 = this.mIdCurrent;
                    this.mIdLast = i3;
                    this.mIdRequisite = i3;
                    Bitmap createBitmap = Bitmap.createBitmap(this.mOriginalBitmap.getWidth(), this.mOriginalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
                    if (!createBitmap.isMutable()) {
                        createBitmap.recycle();
                        createBitmap = this.mOriginalBitmap.copy(Bitmap.Config.ARGB_8888, true);
                    }
                    final String str = "tool_" + this.mIdCurrent + ".jpg";
                    final Handler handler2 = new Handler();
                    Bitmap finalCreateBitmap = createBitmap;
                    Bitmap finalCreateBitmap1 = createBitmap;
                    new Thread(new Runnable() {

                        public void run() {
                            try {
                                new Canvas(finalCreateBitmap1).drawBitmap(SkinColor.this.mCurrentBitmap, 0.0f, 0.0f, SkinColor.this.mEncodePaint);
                                FileOutputStream openFileOutput = SkinColor.this.mActivity.openFileOutput(str, 0);
                                finalCreateBitmap1.compress(Bitmap.CompressFormat.JPEG, 100, openFileOutput);
                                openFileOutput.close();
                                if (SkinColor.this.mIdCurrent == -1) {
                                    SkinColor.this.mActivity.deleteFile(str);
                                }
                            } catch (Exception e) {
                                Log.d("My", "Error (save Bitmap): " + e.getMessage());
                            }
                            handler2.post(new Runnable() {

                                public void run() {
                                    finalCreateBitmap.recycle();
                                }
                            });
                        }
                    }).start();
                }
                this.mBottomUtils.setVisibility(0);
                this.recyclerView.setVisibility(0);
                this.selectedColor.setVisibility(0);
                this.isDrawing = false;
            }
        } else if (this.isDrawing) {
            this.isMoved = true;
            this.mCanvas.drawLine(this.lastX, this.lastY, f, f2, this.mCurrentPaint);
            this.lastX = f;
            this.lastY = f2;
            this.mLayers[1].invalidateSelf();
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mCancelButton:
                close(false);
                return;
            case R.id.mDoneButton:
                save();
                return;
            case R.id.mRedoButton:
                int i = this.mIdRequisite;
                if (i < this.mIdLast) {
                    int i2 = i + 1;
                    this.mIdRequisite = i2;
                    CapturePhotoUtils.getBitmapFromDisk(i, i2, "tool_" + this.mIdRequisite + ".jpg", this, this.mActivity);
                    this.mActivity.sendEvent("Tool - Forward");
                    this.mActivity.sendEvent("Skin Color - Forward");
                    return;
                }
                return;
            case R.id.mUndoButton:
                if (this.mIdRequisite >= 1) {
                    this.mActivity.sendEvent("Tool - Back");
                    this.mActivity.sendEvent("Skin Color- Back");
                    int i3 = this.mIdRequisite;
                    if (i3 > 1) {
                        int i4 = i3 - 1;
                        this.mIdRequisite = i4;
                        CapturePhotoUtils.getBitmapFromDisk(i3, i4, "tool_" + this.mIdRequisite + ".jpg", this, this.mActivity);
                        return;
                    }
                    this.mIdRequisite = 0;
                    this.mIdCurrent = 0;
                    this.mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
                    this.mLayers[1].invalidateSelf();
                    return;
                }
                return;
            default:
                return;
        }
    }

    @Override
    public void loadResponse(Bitmap bitmap, int i, int i2) {
        if (bitmap != null) {
            if ((i2 > i && this.mIdCurrent < i2) || (i2 < i && i2 < this.mIdCurrent)) {
                this.mCanvas.drawBitmap(this.mEffect, 0.0f, 0.0f, this.mSimplePaint);
                this.mCanvas.drawBitmap(bitmap, 0.0f, 0.0f, this.mDecodePaint);
                this.mLayers[1].invalidateSelf();
                this.mIdCurrent = i2;
                this.mIdRequisite = i2;
            }
            bitmap.recycle();
            return;
        }
        this.mIdRequisite = i;
    }

    @Override
    public void onBackPressed(boolean z) {
        close(z);
    }
}
