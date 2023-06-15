package net.braincake.bodytune.effects.mesh;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.FileOutputStream;

import net.braincake.bodytune.activity.EditPhotoActivity;
import net.braincake.bodytune.R;
import net.braincake.bodytune.controls.CapturePhotoUtils;
import net.braincake.bodytune.controls.ScaleImage;
import net.braincake.bodytune.controls.StartPointSeekBar;

public class Enhance implements EditPhotoActivity.BackPressed, View.OnClickListener, CapturePhotoUtils.PhotoLoadResponse, View.OnTouchListener, ScaleImage.ScaleAndMoveInterface {
    private final int ONE_DP = Math.round(Resources.getSystem().getDisplayMetrics().density);
    private float currentCenterX;
    private float currentCenterY;
    private float firstX;
    private float firstY;
    private boolean isEditing;
    private int lastCurrentSize;
    private float lastY;
    private ConstraintLayout.LayoutParams layoutParams;
    private EditPhotoActivity mActivity;
    private ConstraintLayout mBottomUtils;
    private FrameLayout mCancelButton;
    private Canvas mCanvas;
    private ImageView mCircleImage;
    private ConstraintLayout mCircleLayout;

    private Bitmap mCurrentBitmap;
    private int mCurrentSize;
    private FrameLayout mDoneButton;
    private float[] mFinalPoint;
    private int mIdCurrent;
    private int mIdLast;
    private int mIdRequisite;
    private float[] mIntermediatePoint;
    private int mMaxSize;
    private LinearLayout mMenuEnhance;
    private int mMinSize;
    private int mNumberOfColumns;
    private int mNumberOfVerts;
    private Bitmap mOriginalBitmap;
    private Bitmap mOriginalSquare;
    private ConstraintLayout mParent;
    private ImageView mResizeImage;
    private ScaleImage mScaleImage;
    private StartPointSeekBar mSeekbar;
    private float[] matrixValues = new float[9];
    private StartPointSeekBar.OnSeekBarChangeListener seekBarChangeListener = new StartPointSeekBar.OnSeekBarChangeListener() {

        @Override
        public void onOnSeekBarValueChange(StartPointSeekBar startPointSeekBar, long j) {
            if (Enhance.this.isEditing) {
                Log.e("valueasdf", "122");
                for (int i = 0; i < Enhance.this.mNumberOfVerts; i += 2) {
                    int i2 = i / 2;
                    float f = ((float) (i2 % (Enhance.this.mNumberOfColumns + 1))) * Enhance.this.step;
                    float f2 = ((float) (i2 / (Enhance.this.mNumberOfColumns + 1))) * Enhance.this.step;
                    float f3 = ((float) j) / 75.0f;
                    mFinalPoint[i] = f + (Enhance.this.mIntermediatePoint[i] * f3);
                    int i3 = i + 1;
                    mFinalPoint[i3] = f2 + (Enhance.this.mIntermediatePoint[i3] * f3);
                }
                Bitmap createBitmap = Bitmap.createBitmap(Enhance.this.mOriginalSquare.getWidth(), Enhance.this.mOriginalSquare.getHeight(), Bitmap.Config.ARGB_8888);
                if (!createBitmap.isMutable()) {
                    createBitmap.recycle();
                    createBitmap = Enhance.this.mOriginalSquare.copy(Bitmap.Config.ARGB_8888, true);
                }
                new Canvas(createBitmap).drawBitmapMesh(Enhance.this.mOriginalSquare, Enhance.this.mNumberOfColumns, Enhance.this.mNumberOfColumns, Enhance.this.mFinalPoint, 0, null, 0, null);
                mCanvas.drawBitmap(createBitmap, (float) Enhance.this.xLeft, (float) Enhance.this.yTop, (Paint) null);
                createBitmap.recycle();
                mScaleImage.invalidate();
            }
        }

        @Override
        public void onStartTrackingTouch(StartPointSeekBar startPointSeekBar) {
            Log.e("valueasdf", "123");
            mResizeImage.setOnTouchListener(null);
            mCircleLayout.setOnTouchListener(null);
            if (!Enhance.this.isEditing) {
                mScaleImage.getImageMatrix().getValues(Enhance.this.matrixValues);
                Enhance enhance = Enhance.this;
                enhance.xLeft = (int) (((enhance.mCircleLayout.getTranslationX() - Enhance.this.matrixValues[2]) - ((float) Enhance.this.mScaleImage.getPaddingLeft())) / Enhance.this.matrixValues[0]);

                enhance.yTop = (int) (((enhance.mCircleLayout.getTranslationY() - Enhance.this.matrixValues[5]) - ((float) Enhance.this.mScaleImage.getPaddingTop())) / Enhance.this.matrixValues[4]);
                int translationX = (int) ((((Enhance.this.mCircleLayout.getTranslationX() + ((float) Enhance.this.mCurrentSize)) - Enhance.this.matrixValues[2]) - ((float) Enhance.this.mScaleImage.getPaddingLeft())) / Enhance.this.matrixValues[0]);
                int translationY = (int) ((((Enhance.this.mCircleLayout.getTranslationY() + ((float) Enhance.this.mCurrentSize)) - Enhance.this.matrixValues[5]) - ((float) Enhance.this.mScaleImage.getPaddingTop())) / Enhance.this.matrixValues[4]);
                if (translationX >= 1 && translationY >= 1 && Enhance.this.xLeft < Enhance.this.mOriginalBitmap.getWidth() && Enhance.this.yTop < Enhance.this.mOriginalBitmap.getHeight()) {
                    isEditing = true;

                    enhance.mOriginalSquare = Bitmap.createBitmap(translationX - xLeft, translationY - Enhance.this.yTop, Bitmap.Config.ARGB_8888);
                    new Canvas(Enhance.this.mOriginalSquare).drawBitmap(Enhance.this.mCurrentBitmap, (float) (-Enhance.this.xLeft), (float) (-Enhance.this.yTop), (Paint) null);

                    enhance.mNumberOfColumns = Math.min((int) (((float) (translationX - xLeft)) / 5.0f), 10);
                    enhance.mNumberOfVerts = (mNumberOfColumns + 1) * (Enhance.this.mNumberOfColumns + 1) * 2;
                    enhance.mIntermediatePoint = new float[mNumberOfVerts];
                    enhance.mFinalPoint = new float[mNumberOfVerts];
                    enhance.step = ((float) mOriginalSquare.getWidth()) / ((float) Enhance.this.mNumberOfColumns);
                    float width = ((float) Enhance.this.mOriginalSquare.getWidth()) / 2.0f;
                    float width2 = ((float) Enhance.this.mOriginalSquare.getWidth()) / 2.0f;
                    for (int i = 0; i < Enhance.this.mNumberOfVerts; i += 2) {
                        int i2 = i / 2;
                        float f = ((float) (i2 % (Enhance.this.mNumberOfColumns + 1))) * Enhance.this.step;
                        float f2 = f - width2;
                        float f3 = (((float) (i2 / (Enhance.this.mNumberOfColumns + 1))) * Enhance.this.step) - width2;
                        float sqrt = (float) Math.sqrt(Math.pow((double) f2, 2.0d) + Math.pow((double) f3, 2.0d));
                        if (sqrt < width) {
                            float f4 = (width - sqrt) / width;
                            mIntermediatePoint[i] = f2 * f4;
                            mIntermediatePoint[i + 1] = f4 * f3;
                        } else {
                            mIntermediatePoint[i] = 0.0f;
                            mIntermediatePoint[i + 1] = 0.0f;
                        }
                    }
                }
            }
        }

        @Override
        public void onStopTrackingTouch(StartPointSeekBar startPointSeekBar) {
            Log.e("valueasdf", "124");
            mResizeImage.setOnTouchListener(Enhance.this);
            mCircleLayout.setOnTouchListener(Enhance.this);
            if (!Enhance.this.isEditing) {
                mSeekbar.setProgress(0.0d);
            }
        }
    };
    private float step;
    private int xLeft;
    private float xMax;
    private float yMax;
    private int yTop;

    public Enhance(Bitmap bitmap, EditPhotoActivity editPhotoActivity, ScaleImage scaleImage) {
        this.mOriginalBitmap = bitmap;
        this.mActivity = editPhotoActivity;
        this.mScaleImage = scaleImage;
        onCreate();
    }

    @SuppressLint("WrongConstant")
    private void onCreate() {
        this.mBottomUtils = (ConstraintLayout) this.mActivity.findViewById(R.id.mBottomUtils);
        this.mCancelButton = (FrameLayout) this.mActivity.findViewById(R.id.mCancelButton);
        this.mDoneButton = (FrameLayout) this.mActivity.findViewById(R.id.mDoneButton);
        this.mParent = (ConstraintLayout) this.mActivity.findViewById(R.id.page);
        this.mMenuEnhance = (LinearLayout) this.mActivity.findViewById(R.id.seekbarWithTwoIcon);
        this.mSeekbar = (StartPointSeekBar) this.mActivity.findViewById(R.id.SWTI_seekbar);
        ((ImageView) this.mActivity.findViewById(R.id.SWTI_1)).setImageResource(R.drawable.enhance_small);
        ((ImageView) this.mActivity.findViewById(R.id.SWTI_2)).setImageResource(R.drawable.enhance_big);
        this.mMaxSize = (int) (((float) Math.min(this.mOriginalBitmap.getHeight(), this.mOriginalBitmap.getWidth())) * this.mScaleImage.getCalculatedMinScale());
        this.xMax = (float) this.mScaleImage.getMeasuredWidth();
        this.yMax = (float) this.mScaleImage.getMeasuredHeight();
        this.mActivity.isBlocked = false;
        createCircle();
        this.mCircleLayout.setOnTouchListener(this);
        this.mResizeImage.setOnTouchListener(this);
        this.mCurrentBitmap = this.mOriginalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        this.mCanvas = new Canvas(this.mCurrentBitmap);
        this.mActivity.mUndoButton.setOnClickListener(this);
        this.mActivity.mRedoButton.setOnClickListener(this);
        this.mCancelButton.setOnClickListener(this);
        this.mDoneButton.setOnClickListener(this);
        this.mActivity.mBefore.setOnTouchListener(new View.OnTouchListener() {


            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if (action == 0) {
                    mScaleImage.setImageBitmap(Enhance.this.mOriginalBitmap);
                } else if (action == 1 || action == 3) {
                    mScaleImage.setImageBitmap(Enhance.this.mCurrentBitmap);
                }
                return true;
            }
        });
        ((TextView) this.mActivity.findViewById(R.id.nameOfTool)).setText(this.mActivity.getResources().getString(R.string.enhance));
        mSeekbar.setAbsoluteMinMaxValue(-50, 50);
        this.mSeekbar.setProgress(0.0d);

        this.mSeekbar.setOnSeekBarChangeListener(this.seekBarChangeListener);
        this.mMenuEnhance.setVisibility(0);
        this.mScaleImage.setImageBitmap(this.mCurrentBitmap);
        this.mScaleImage.setOnScaleAndMoveInterface(this);
        this.mActivity.mShare.setOnClickListener(null);
        this.mActivity.mBack.setOnClickListener(null);
        this.mActivity.mTopUtils.setVisibility(8);
        this.mActivity.findViewById(R.id.menuHome).setVisibility(8);
        this.mActivity.findViewById(R.id.saveCloseContainer).setVisibility(0);
        this.mActivity.sendEvent("Enhance - open");

    }

    private void createCircle() {
        ConstraintLayout.LayoutParams layoutParams2 = new ConstraintLayout.LayoutParams(-2, -2);
        ConstraintLayout constraintLayout = new ConstraintLayout(this.mActivity);
        this.mCircleLayout = constraintLayout;
        constraintLayout.setLayoutParams(layoutParams2);
        this.mCircleLayout.setBackgroundColor(0);
        Drawable drawable = this.mActivity.getResources().getDrawable(R.drawable.enhance_arrows_button);
        int min = (int) Math.min(((float) drawable.getIntrinsicWidth()) * 2.5f, (float) this.mMaxSize);
        this.mMinSize = min;
        int i = this.mMaxSize;
        if (min != i) {
            min = (int) (((float) (min + i)) * 0.25f);
        }
        this.mCurrentSize = min;
        ImageView imageView = new ImageView(this.mActivity);
        this.mCircleImage = imageView;
        imageView.setImageResource(R.drawable.circle);
        int i2 = this.mCurrentSize;
        ConstraintLayout.LayoutParams layoutParams3 = new ConstraintLayout.LayoutParams(i2, i2);
        this.layoutParams = layoutParams3;
        this.mCircleImage.setLayoutParams(layoutParams3);
        this.mCircleImage.setId(R.id.mCircleImage);
        ImageView imageView2 = new ImageView(this.mActivity);
        this.mResizeImage = imageView2;
        imageView2.setImageDrawable(drawable);
        ConstraintLayout.LayoutParams layoutParams4 = new ConstraintLayout.LayoutParams(-2, -2);
        this.layoutParams = layoutParams4;
        layoutParams4.circleAngle = 135.0f;
        this.layoutParams.circleConstraint = this.mCircleImage.getId();
        this.layoutParams.circleRadius = this.mCurrentSize / 2;
        this.mResizeImage.setLayoutParams(this.layoutParams);
        this.mResizeImage.setId(R.id.mResizeImage);
        this.mCircleLayout.addView(this.mCircleImage);
        this.mCircleLayout.addView(this.mResizeImage);
        this.mCircleLayout.setTranslationX((float) ((this.mScaleImage.getMeasuredWidth() - this.mCurrentSize) / 2));
        this.mCircleLayout.setTranslationY((float) ((this.mScaleImage.getMeasuredHeight() - this.mCurrentSize) / 2));
        this.mParent.addView(this.mCircleLayout, 1);
    }

    @SuppressLint("WrongConstant")
    private void close(boolean z) {
        for (int i = 0; i <= this.mIdLast; i++) {
            EditPhotoActivity editPhotoActivity = this.mActivity;
            editPhotoActivity.deleteFile("tool_" + i + ".png");
        }
        this.mIdCurrent = -1;
        if (z) {
            this.mActivity.sendEvent("Enhance - V");
        } else {
            this.mActivity.sendEvent("Tool - X");
            this.mActivity.sendEvent("Enhance - X");
        }
        this.mCurrentBitmap.recycle();
        this.mCircleLayout.removeAllViews();
        this.mParent.removeView(this.mCircleLayout);
        this.layoutParams.reset();
        Bitmap bitmap = this.mOriginalSquare;
        if (bitmap != null && !bitmap.isRecycled()) {
            this.mOriginalSquare.recycle();
        }
        this.mMenuEnhance.setVisibility(8);
        this.mActivity.mUndoButton.setOnClickListener(this.mActivity);
        this.mActivity.mRedoButton.setOnClickListener(this.mActivity);
        this.mCircleLayout.setOnTouchListener(null);
        this.mResizeImage.setOnTouchListener(null);
        this.mSeekbar.setOnSeekBarChangeListener(null);
        this.mScaleImage.setOnScaleAndMoveInterface(null);
        this.mCancelButton.setOnClickListener(null);
        this.mDoneButton.setOnClickListener(null);
        this.mActivity.mShare.setOnClickListener(this.mActivity);
        this.mActivity.mBack.setOnClickListener(this.mActivity);
        this.mActivity.mBefore.setOnTouchListener(this.mActivity);
        this.mScaleImage.setImageBitmap(this.mActivity.mCurrentBitmap);
        this.mActivity.mTopUtils.setVisibility(0);
        this.mActivity.findViewById(R.id.saveCloseContainer).setVisibility(8);
        this.mActivity.findViewById(R.id.menuHome).setVisibility(0);
    }

    @Override
    public void onBackPressed(boolean z) {
        close(z);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mCancelButton:
                close(false);
                return;
            case R.id.mDoneButton:
                if (this.isEditing) {
                    this.mOriginalSquare.recycle();
                }
                this.mActivity.saveEffect(this.mCurrentBitmap);
                return;
            case R.id.mRedoButton:
                if (this.mIdRequisite < this.mIdLast) {
                    saveState();
                    int i = this.mIdRequisite;
                    int i2 = i + 1;
                    this.mIdRequisite = i2;
                    CapturePhotoUtils.getBitmapFromDisk(i, i2, "tool_" + this.mIdRequisite + ".png", this, this.mActivity);
                    this.mActivity.sendEvent("Tool - Forward");
                    this.mActivity.sendEvent("Enhance - Forward");
                    return;
                }
                return;
            case R.id.mUndoButton:
                saveState();
                if (this.mIdRequisite >= 1) {
                    this.mActivity.sendEvent("Tool - Back");
                    this.mActivity.sendEvent("Enhance - Back");
                    int i3 = this.mIdRequisite;
                    if (i3 > 1) {
                        int i4 = i3 - 1;
                        this.mIdRequisite = i4;
                        CapturePhotoUtils.getBitmapFromDisk(i3, i4, "tool_" + this.mIdRequisite + ".png", this, this.mActivity);
                        return;
                    }
                    this.mIdRequisite = 0;
                    this.mIdCurrent = 0;
                    this.mCanvas.drawBitmap(this.mOriginalBitmap, 0.0f, 0.0f, (Paint) null);
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
                this.mCanvas.drawBitmap(bitmap, 0.0f, 0.0f, (Paint) null);
                this.mIdCurrent = i2;
                this.mIdRequisite = i2;
                this.mScaleImage.invalidate();
            }
            bitmap.recycle();
            return;
        }
        this.mIdRequisite = i;
    }

    private void saveState() {
        if (this.isEditing) {
            this.isEditing = false;
            this.mOriginalSquare.recycle();
            if (this.mSeekbar.getProgress() != 0) {
                this.mSeekbar.setProgress(0.0d);
                int i = this.mIdCurrent + 1;
                this.mIdCurrent = i;
                while (i <= this.mIdLast) {
                    this.mActivity.deleteFile("tool_" + i + ".png");
                    i++;
                }
                int i2 = this.mIdCurrent;
                this.mIdLast = i2;
                this.mIdRequisite = i2;
                final Bitmap copy = this.mCurrentBitmap.copy(Bitmap.Config.ARGB_8888, true);
                final String str = "tool_" + this.mIdCurrent + ".png";
                final Handler handler = new Handler();
                new Thread(new Runnable() {

                    public void run() {
                        try {
                            FileOutputStream openFileOutput = Enhance.this.mActivity.openFileOutput(str, 0);
                            copy.compress(Bitmap.CompressFormat.PNG, 100, openFileOutput);
                            openFileOutput.close();
                            if (Enhance.this.mIdCurrent == -1) {
                                mActivity.deleteFile(str);
                            }
                        } catch (Exception e) {
                            Log.d("My", "Error (save Bitmap): " + e.getMessage());
                        }
                        handler.post(new Runnable() {

                            public void run() {
                                copy.recycle();
                            }
                        });
                    }
                }).start();
            }
        }
    }

    private void startWorkWithCircle() {
        this.mBottomUtils.setVisibility(8);
        this.mSeekbar.setEnabled(false);
        saveState();
    }


    @SuppressLint("ResourceType")
    public boolean onTouch(View var1, MotionEvent var2) {
        int var3;
        float var6;
        if (var1.getId() == R.id.mResizeImage) {
            Log.e("statusPositionClick", "1");
            var3 = var2.getAction();
            float var5;
            if (var3 != 0) {
                if (var3 != 1) {
                    if (var3 == 2) {
                        float var4 = var2.getRawX();
                        var5 = var2.getRawY();
                        var6 = (float) Math.toDegrees(Math.atan2((double) (this.currentCenterY - var5), (double) (var4 - this.currentCenterX)));
                        var4 = (float) Math.sqrt(Math.pow((double) (var4 - this.currentCenterX), 2.0D) + Math.pow((double) (var5 - this.currentCenterY), 2.0D));
                        var6 = (float) ((double) var5 - (double) var4 * Math.sin(Math.toRadians((double) (var6 - 135.0F)))) - this.lastY;
                        var3 = this.mCurrentSize;
                        int var7;
                        if (var6 < 0.0F) {
                            var7 = Math.max((int) ((float) this.lastCurrentSize + var6 * 2.0F), this.mMinSize);
                            this.mCurrentSize = var7;
                            var3 = (var3 - var7) / 2;
                            ConstraintLayout var10 = this.mCircleLayout;
                            var6 = var10.getTranslationX();
                            var5 = (float) var3;
                            var10.setTranslationX(var6 + var5);
                            var10 = this.mCircleLayout;
                            var10.setTranslationY(var10.getTranslationY() + var5);
                        } else {
                            var7 = Math.min((int) ((float) this.lastCurrentSize + var6 * 2.0F), this.mMaxSize);
                            this.mCurrentSize = var7;
                            var3 = (var7 - var3) / 2;
                            var6 = this.mCircleLayout.getTranslationX();
                            var4 = (float) var3;
                            var5 = Math.max(var6 - var4, 0.0F);
                            var3 = this.mCurrentSize;
                            float var8 = (float) var3;
                            float var9 = this.xMax;
                            var6 = var5;
                            if (var8 + var5 > var9) {
                                var6 = var9 - (float) var3;
                            }

                            this.mCircleLayout.setTranslationX(var6);
                            var5 = Math.max(this.mCircleLayout.getTranslationY() - var4, 0.0F);
                            var3 = this.mCurrentSize;
                            var9 = (float) var3;
                            var4 = this.yMax;
                            var6 = var5;
                            if (var9 + var5 > var4) {
                                var6 = var4 - (float) var3;
                            }

                            this.mCircleLayout.setTranslationY(var6);
                        }

                        this.mCircleImage.getLayoutParams().width = this.mCurrentSize;
                        this.mCircleImage.getLayoutParams().height = this.mCurrentSize;
                        this.layoutParams.circleRadius = this.mCurrentSize / 2 - this.ONE_DP;
                        this.mCircleImage.requestLayout();
                        return true;
                    }

                    if (var3 != 3) {
                        return true;
                    }
                }

                this.mBottomUtils.setVisibility(0);
                this.mSeekbar.setEnabled(true);
            } else {
                this.startWorkWithCircle();
                var5 = var2.getRawX();
                this.lastY = var2.getRawY();
                this.currentCenterX = this.mCircleLayout.getTranslationX() + (float) this.mCurrentSize * 0.8535534F;
                var6 = this.mCircleLayout.getTranslationY() + (float) this.mCurrentSize * 0.8535534F;
                this.currentCenterY = var6;
                var6 = (float) Math.toDegrees(Math.atan2((double) (var6 - this.lastY), (double) (var5 - this.currentCenterX)));
                var5 = (float) Math.sqrt(Math.pow((double) (var5 - this.currentCenterX), 2.0D) + Math.pow((double) (this.lastY - this.currentCenterY), 2.0D));
                this.lastY = (float) ((double) this.lastY - (double) var5 * Math.sin(Math.toRadians((double) (var6 - 135.0F))));
                this.lastCurrentSize = this.mCurrentSize;
            }
        } else {
            Log.e("statusPositionClick", "2");
            var3 = var2.getAction();
            if (var3 != 0) {
                if (var3 != 1) {
                    if (var3 == 2) {
                        var6 = var2.getRawX() - this.firstX;
                        if (var6 >= 0.0F && (float) this.mCurrentSize + var6 <= this.xMax) {
                            this.mCircleLayout.setTranslationX(var6);
                        }

                        var6 = var2.getRawY() - this.firstY;
                        if (var6 >= 0.0F && (float) this.mCurrentSize + var6 <= this.yMax) {
                            this.mCircleLayout.setTranslationY(var6);
                        }

                        return true;
                    }

                    if (var3 != 3) {
                        return true;
                    }
                }

                this.mBottomUtils.setVisibility(0);
                this.mSeekbar.setEnabled(true);
            } else {
                this.startWorkWithCircle();
                this.firstX = var2.getRawX() - this.mCircleLayout.getTranslationX();
                this.firstY = var2.getRawY() - this.mCircleLayout.getTranslationY();
            }
        }

        return true;
    }

    @Override
    public void move(float f, float f2, float f3, float f4) {
        saveState();
    }
}
