package net.braincake.bodytune.effects.mesh;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
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
import java.util.ArrayList;
import java.util.List;

import net.braincake.bodytune.activity.EditPhotoActivity;
import net.braincake.bodytune.R;
import net.braincake.bodytune.controls.CapturePhotoUtils;
import net.braincake.bodytune.controls.ScaleImage;
import net.braincake.bodytune.controls.StartPointSeekBar;

public class Waist implements EditPhotoActivity.BackPressed, View.OnClickListener, View.OnTouchListener, ScaleImage.ScaleAndMoveInterface, CapturePhotoUtils.PhotoLoadResponse {
    private static final int MAX_ROW = 30;
    private static final int NUMBER_OF_COLUMN = 10;
    private int column;
    private List<WaistHistory> history = new ArrayList();
    private float initialHeight;
    private float initialTranslationX;
    private float initialTranslationY;
    private float initialWidth;
    private boolean isEditing;
    private float lastX;
    private float lastY;
    private EditPhotoActivity mActivity;
    private ImageView mBottomImage;
    private ConstraintLayout mBottomUtils;
    private FrameLayout mCancelButton;
    private Canvas mCanvas;
    private ImageView mCenterImage;
    private ConstraintLayout mControlLayout;
    private Bitmap mCurrentBitmap;
    private float[] mCurrentMesh;
    private FrameLayout mDoneButton;
    private int mHeight;
    private int mIdCurrent;
    private int mIdLast;
    private int mIdRequisite;
    private long mLastProgress;
    private ImageView mLeftImage;
    private float[] mMaxChangeMesh;
    private LinearLayout mMenuWaist;
    private int mMinHeight;
    private int mMinWidth;
    private int mNumberOfVerts;
    private Bitmap mOriginalBitmap;
    private Bitmap mOriginalSquare;
    private ConstraintLayout mParent;
    private ImageView mRightImage;
    private ScaleImage mScaleImage;
    private StartPointSeekBar mSeekbar;
    private ImageView mTopImage;
    private int mWidth;
    private float[] matrixValues = new float[9];
    private int row;
    private StartPointSeekBar.OnSeekBarChangeListener seekBarChangeListener = new StartPointSeekBar.OnSeekBarChangeListener() {

        @Override
        public void onOnSeekBarValueChange(StartPointSeekBar startPointSeekBar, long j) {
            if (Waist.this.isEditing) {
                long j2 = Waist.this.mLastProgress - j;
                Waist.this.mLastProgress = j;
                for (int i = 0; i < Waist.this.mNumberOfVerts; i += 2) {
                    float[] fArr = Waist.this.mCurrentMesh;
                    fArr[i] = fArr[i] + ((Waist.this.mMaxChangeMesh[i] * ((float) j2)) / 50.0f);
                }
                Bitmap createBitmap = Bitmap.createBitmap(Waist.this.mOriginalSquare.getWidth(), Waist.this.mOriginalSquare.getHeight(), Bitmap.Config.ARGB_8888);
                if (!createBitmap.isMutable()) {
                    Bitmap copy = createBitmap.copy(Bitmap.Config.ARGB_8888, true);
                    createBitmap.recycle();
                    createBitmap = copy;
                }
                new Canvas(createBitmap).drawBitmapMesh(Waist.this.mOriginalSquare, Waist.this.column, Waist.this.row, Waist.this.mCurrentMesh, 0, null, 0, null);
                Waist.this.mCanvas.drawBitmap(createBitmap, (float) Waist.this.xStart, (float) Waist.this.yStart, (Paint) null);
                createBitmap.recycle();
            }
        }

        @SuppressLint("WrongConstant")
        @Override
        public void onStartTrackingTouch(StartPointSeekBar startPointSeekBar) {
            float f;
            float f2;
            Waist.this.mControlLayout.setVisibility(4);
            if (!Waist.this.isEditing) {
                Waist.this.mLastProgress = 0;
                Waist.this.mScaleImage.getImageMatrix().getValues(Waist.this.matrixValues);
                Waist waist = Waist.this;
                int i = 0;
                waist.xStart = Math.round((waist.mControlLayout.getTranslationX() - Waist.this.matrixValues[2]) / Waist.this.matrixValues[0]);
                Waist waist2 = Waist.this;
                waist2.yStart = Math.round((waist2.mControlLayout.getTranslationY() - Waist.this.matrixValues[5]) / Waist.this.matrixValues[4]);
                int round = Math.round(((float) Waist.this.mControlLayout.getWidth()) / Waist.this.matrixValues[0]);
                int round2 = Math.round(((float) Waist.this.mControlLayout.getHeight()) / Waist.this.matrixValues[4]);
                float f3 = 2.0f;
                float f4 = ((float) round) / 2.0f;
                float f5 = ((float) round2) / 2.0f;
                if (Waist.this.xStart < 0) {
                    round += Waist.this.xStart;
                    f = ((float) Waist.this.xStart) + f4;
                    Waist.this.xStart = 0;
                } else {
                    f = f4;
                }
                if (Waist.this.yStart < 0) {
                    f2 = (float) (-Waist.this.yStart);
                    round2 += Waist.this.yStart;
                    Waist.this.yStart = 0;
                } else {
                    f2 = 0.0f;
                }
                int min = Math.min(round, Waist.this.mCurrentBitmap.getWidth() - Waist.this.xStart);
                int min2 = Math.min(round2, Waist.this.mCurrentBitmap.getHeight() - Waist.this.yStart);
                if (min >= 50 && min2 >= 50) {
                    Waist waist3 = Waist.this;
                    waist3.mOriginalSquare = Bitmap.createBitmap(waist3.mCurrentBitmap, Waist.this.xStart, Waist.this.yStart, min, min2);
                    Waist.this.column = 10;
                    float f6 = ((float) min) / ((float) Waist.this.column);
                    Waist.this.row = Math.min(min2 / 10, 30);
                    float f7 = ((float) min2) / ((float) Waist.this.row);
                    Waist waist4 = Waist.this;
                    waist4.mNumberOfVerts = (waist4.column + 1) * 2 * (Waist.this.row + 1);
                    Waist waist5 = Waist.this;
                    waist5.mCurrentMesh = new float[waist5.mNumberOfVerts];
                    Waist waist6 = Waist.this;
                    waist6.mMaxChangeMesh = new float[waist6.mNumberOfVerts];
                    while (i < Waist.this.mNumberOfVerts) {
                        int i2 = i / 2;
                        int i3 = i2 % (Waist.this.column + 1);
                        float f8 = ((float) i3) * f6;
                        float f9 = ((float) (i2 / (Waist.this.column + 1))) * f7;
                        Waist.this.mCurrentMesh[i] = f8;
                        Waist.this.mCurrentMesh[i + 1] = f9;
                        if (!(i3 == 0 || i3 == Waist.this.column)) {
                            Waist.this.mMaxChangeMesh[i] = ((((float) Math.sin((((double) (f9 + f2)) * 3.141592653589793d) / ((double) (f5 * f3)))) * f6) * (f8 - f)) / f4;
                        }
                        i += 2;
                        f3 = 2.0f;
                    }
                    Waist.this.isEditing = true;
                }
            }
        }

        @Override
        public void onStopTrackingTouch(StartPointSeekBar startPointSeekBar) {
            if (!Waist.this.isEditing) {
                Waist.this.mSeekbar.setProgress(0.0d);
            }
            Waist.this.mControlLayout.setVisibility(0);
        }
    };
    private int xStart;
    private int yStart;

    public Waist(Bitmap bitmap, EditPhotoActivity editPhotoActivity, ScaleImage scaleImage) {
        this.mOriginalBitmap = bitmap;
        this.mActivity = editPhotoActivity;
        this.mScaleImage = scaleImage;
        onCreate();
    }

    private void onCreate() {
        this.mBottomUtils = (ConstraintLayout) this.mActivity.findViewById(R.id.mBottomUtils);
        this.mCancelButton = (FrameLayout) this.mActivity.findViewById(R.id.mCancelButton);
        this.mDoneButton = (FrameLayout) this.mActivity.findViewById(R.id.mDoneButton);
        this.mParent = (ConstraintLayout) this.mActivity.findViewById(R.id.page);
        this.mMenuWaist = (LinearLayout) this.mActivity.findViewById(R.id.seekbarWithTwoIcon);
        this.mSeekbar = (StartPointSeekBar) this.mActivity.findViewById(R.id.SWTI_seekbar);
        mSeekbar.setAbsoluteMinMaxValue(-50, 50);
        ((ImageView) this.mActivity.findViewById(R.id.SWTI_1)).setImageResource(R.drawable.waist_left_icon);
        ((ImageView) this.mActivity.findViewById(R.id.SWTI_2)).setImageResource(R.drawable.waist_right_icon);
        this.mActivity.isBlocked = false;
        createControlLayout();
        this.mCurrentBitmap = this.mOriginalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        this.mCanvas = new Canvas(this.mCurrentBitmap);
        this.mActivity.mUndoButton.setOnClickListener(this);
        this.mActivity.mRedoButton.setOnClickListener(this);
        this.mCancelButton.setOnClickListener(this);
        this.mDoneButton.setOnClickListener(this);
        this.mActivity.mBefore.setOnTouchListener(new View.OnTouchListener() {
            /* class net.braincake.bodytune.effects.mesh.Waist.AnonymousClass2 */

            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if (action == 0) {
                    Waist.this.mScaleImage.setImageBitmap(Waist.this.mOriginalBitmap);
                } else if (action == 1 || action == 3) {
                    Waist.this.mScaleImage.setImageBitmap(Waist.this.mCurrentBitmap);
                }
                return true;
            }
        });
        ((TextView) this.mActivity.findViewById(R.id.nameOfTool)).setText(this.mActivity.getResources().getString(R.string.enhance));
        this.mSeekbar.setProgress(0.0d);
        this.mSeekbar.setOnSeekBarChangeListener(this.seekBarChangeListener);
        this.mMenuWaist.setVisibility(0);
        this.mScaleImage.setImageBitmap(this.mCurrentBitmap);
        this.mScaleImage.setOnScaleAndMoveInterface(this);
        this.mActivity.mShare.setOnClickListener(null);
        this.mActivity.mBack.setOnClickListener(null);
        this.mActivity.mTopUtils.setVisibility(8);
        this.mActivity.findViewById(R.id.menuHome).setVisibility(8);
        this.mActivity.findViewById(R.id.saveCloseContainer).setVisibility(0);
        this.mActivity.sendEvent("Waist - open");
    }

    private void createControlLayout() {
        this.mControlLayout = new ConstraintLayout(this.mActivity);
        ImageView imageView = new ImageView(this.mActivity);
        this.mTopImage = imageView;
        imageView.setId(R.id.mTopImage);
        this.mTopImage.setImageResource(R.drawable.transform_up);
        ImageView imageView2 = new ImageView(this.mActivity);
        this.mBottomImage = imageView2;
        imageView2.setId(R.id.mBottomImage);
        this.mBottomImage.setImageResource(R.drawable.transform_down);
        ImageView imageView3 = new ImageView(this.mActivity);
        this.mLeftImage = imageView3;
        imageView3.setId(R.id.mLeftImage);
        this.mLeftImage.setImageResource(R.drawable.transform_left);
        ImageView imageView4 = new ImageView(this.mActivity);
        this.mRightImage = imageView4;
        imageView4.setId(R.id.mRightImage);
        this.mRightImage.setImageResource(R.drawable.transform_right);
        ImageView imageView5 = new ImageView(this.mActivity);
        this.mCenterImage = imageView5;
        imageView5.setId(R.id.mCenterImage);
        this.mCenterImage.setImageResource(R.drawable.transform_move);
        FrameLayout frameLayout = new FrameLayout(this.mActivity);
        frameLayout.setId(R.id.centerLine);
        frameLayout.setBackgroundResource(R.drawable.transform_line_center);
        FrameLayout frameLayout2 = new FrameLayout(this.mActivity);
        frameLayout2.setId(R.id.lineLeft);
        frameLayout2.setBackgroundResource(R.drawable.transform_line_left);
        FrameLayout frameLayout3 = new FrameLayout(this.mActivity);
        frameLayout3.setId(R.id.lineRight);
        frameLayout3.setBackgroundResource(R.drawable.transform_line_right);
        int intrinsicHeight = this.mTopImage.getDrawable().getIntrinsicHeight();
        this.mMinHeight = intrinsicHeight * 4;
        this.mMinWidth = this.mLeftImage.getDrawable().getIntrinsicWidth() * 3;
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(-2, -2);
        layoutParams.leftToLeft = 0;
        layoutParams.rightToRight = 0;
        layoutParams.topToTop = 0;
        layoutParams.bottomToBottom = 0;
        this.mCenterImage.setLayoutParams(layoutParams);
        ConstraintLayout.LayoutParams layoutParams2 = new ConstraintLayout.LayoutParams(-2, -2);
        layoutParams2.leftToLeft = 0;
        layoutParams2.rightToRight = 0;
        layoutParams2.topToTop = 0;
        this.mTopImage.setLayoutParams(layoutParams2);
        ConstraintLayout.LayoutParams layoutParams3 = new ConstraintLayout.LayoutParams(-2, -2);
        layoutParams3.leftToLeft = 0;
        layoutParams3.rightToRight = 0;
        layoutParams3.bottomToBottom = 0;
        this.mBottomImage.setLayoutParams(layoutParams3);
        ConstraintLayout.LayoutParams layoutParams4 = new ConstraintLayout.LayoutParams(-2, -2);
        layoutParams4.leftToRight = frameLayout2.getId();
        layoutParams4.rightToRight = frameLayout2.getId();
        layoutParams4.topToTop = frameLayout.getId();
        layoutParams4.bottomToBottom = frameLayout.getId();
        this.mLeftImage.setLayoutParams(layoutParams4);
        ConstraintLayout.LayoutParams layoutParams5 = new ConstraintLayout.LayoutParams(-2, -2);
        layoutParams5.leftToLeft = frameLayout3.getId();
        layoutParams5.rightToLeft = frameLayout3.getId();
        layoutParams5.topToTop = frameLayout.getId();
        layoutParams5.bottomToBottom = frameLayout.getId();
        this.mRightImage.setLayoutParams(layoutParams5);
        ConstraintLayout.LayoutParams layoutParams6 = new ConstraintLayout.LayoutParams(-2, 0);
        layoutParams6.leftToLeft = 0;
        layoutParams6.rightToRight = 0;
        layoutParams6.topToTop = 0;
        layoutParams6.bottomToBottom = 0;
        int i = intrinsicHeight / 2;
        layoutParams6.topMargin = i;
        layoutParams6.bottomMargin = i;
        frameLayout.setLayoutParams(layoutParams6);
        ConstraintLayout.LayoutParams layoutParams7 = new ConstraintLayout.LayoutParams(-2, 0);
        layoutParams7.topToTop = frameLayout.getId();
        layoutParams7.bottomToBottom = frameLayout.getId();
        frameLayout2.setLayoutParams(layoutParams7);
        ConstraintLayout.LayoutParams layoutParams8 = new ConstraintLayout.LayoutParams(-2, 0);
        layoutParams8.topToTop = frameLayout.getId();
        layoutParams8.bottomToBottom = frameLayout.getId();
        layoutParams8.rightToRight = 0;
        frameLayout3.setLayoutParams(layoutParams8);
        this.mControlLayout.addView(frameLayout2);
        this.mControlLayout.addView(frameLayout3);
        this.mControlLayout.addView(frameLayout);
        this.mControlLayout.addView(this.mTopImage);
        this.mControlLayout.addView(this.mRightImage);
        this.mControlLayout.addView(this.mBottomImage);
        this.mControlLayout.addView(this.mLeftImage);
        this.mControlLayout.addView(this.mCenterImage);
        ConstraintLayout.LayoutParams layoutParams9 = new ConstraintLayout.LayoutParams(this.mMinWidth, this.mMinHeight);
        this.mControlLayout.setLayoutParams(layoutParams9);
        this.mParent.addView(this.mControlLayout, 1);
        this.mWidth = this.mScaleImage.getWidth();
        this.mHeight = this.mScaleImage.getHeight();
        this.mControlLayout.setTranslationX(((float) (this.mWidth - layoutParams9.width)) / 2.0f);
        this.mControlLayout.setTranslationY(((float) (this.mHeight - layoutParams9.height)) / 2.0f);
        this.mTopImage.setOnTouchListener(this);
        this.mBottomImage.setOnTouchListener(this);
        this.mLeftImage.setOnTouchListener(this);
        this.mRightImage.setOnTouchListener(this);
        this.mCenterImage.setOnTouchListener(this);
    }

    @SuppressLint("WrongConstant")
    private void close(boolean z) {
        for (int i = 0; i <= this.mIdLast; i++) {
            EditPhotoActivity editPhotoActivity = this.mActivity;
            editPhotoActivity.deleteFile("tool_" + i + ".png");
        }
        this.mIdCurrent = -1;
        Bitmap bitmap = this.mOriginalSquare;
        if (bitmap != null && !bitmap.isRecycled()) {
            this.mOriginalSquare.recycle();
        }
        if (z) {
            this.mActivity.sendEvent("Waist - V");
        } else {
            this.mActivity.sendEvent("Tool - X");
            this.mActivity.sendEvent("Waist - X");
        }
        this.mCurrentBitmap.recycle();
        this.mControlLayout.removeAllViews();
        this.mParent.removeView(this.mControlLayout);
        this.history.clear();
        this.mTopImage.setOnTouchListener(null);
        this.mBottomImage.setOnTouchListener(null);
        this.mLeftImage.setOnTouchListener(null);
        this.mRightImage.setOnTouchListener(null);
        this.mCenterImage.setOnTouchListener(null);
        this.mMenuWaist.setVisibility(8);
        this.mActivity.mUndoButton.setOnClickListener(this.mActivity);
        this.mActivity.mRedoButton.setOnClickListener(this.mActivity);
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
                this.mActivity.saveEffect(this.mCurrentBitmap);
                return;
            case R.id.mRedoButton:
                int i = this.mIdRequisite;
                if (i == this.mIdCurrent && i < this.mIdLast) {
                    this.mActivity.sendEvent("Tool - Forward");
                    this.mActivity.sendEvent("Waist - Forward");
                    if (!this.isEditing) {
                        int i2 = this.mIdRequisite;
                        int i3 = i2 + 1;
                        this.mIdRequisite = i3;
                        CapturePhotoUtils.getBitmapFromDisk(i2, i3, "tool_" + this.mIdRequisite + ".png", this, this.mActivity);
                        return;
                    }
                    saveState();
                    return;
                }
                return;
            case R.id.mUndoButton:
                saveState();
                int i4 = this.mIdRequisite;
                if (i4 == this.mIdCurrent && i4 > 0) {
                    int i5 = i4 - 1;
                    this.mIdRequisite = i5;
                    CapturePhotoUtils.getBitmapFromDisk(i4, i5, "tool_" + (this.mIdRequisite + 1) + ".png", this, this.mActivity);
                    this.mActivity.sendEvent("Tool - Back");
                    this.mActivity.sendEvent("Waist - Back");
                    return;
                }
                return;
            default:
                return;
        }
    }

    private void saveState() {
        if (this.isEditing) {
            this.isEditing = false;
            if (this.mSeekbar.getProgress() != 0) {
                int i = this.mIdCurrent + 1;
                this.mIdCurrent = i;
                while (i <= this.mIdLast) {
                    this.mActivity.deleteFile("tool_" + i + ".png");
                    List<WaistHistory> list = this.history;
                    list.remove(list.size() - 1);
                    i++;
                }
                int i2 = this.mIdCurrent;
                this.mIdLast = i2;
                this.mIdRequisite = i2;
                final Bitmap copy = this.mOriginalSquare.copy(Bitmap.Config.ARGB_8888, true);
                this.mOriginalSquare.recycle();
                this.history.add(new WaistHistory((float[]) this.mCurrentMesh.clone(), (float) this.xStart, (float) this.yStart, this.column, this.row));
                this.mSeekbar.setProgress(0.0d);
                final String str = "tool_" + this.mIdCurrent + ".png";
                final Handler handler = new Handler();
                new Thread(new Runnable() {

                    public void run() {
                        try {
                            FileOutputStream openFileOutput = Waist.this.mActivity.openFileOutput(str, 0);
                            copy.compress(Bitmap.CompressFormat.PNG, 100, openFileOutput);
                            openFileOutput.close();
                            if (Waist.this.mIdCurrent == -1) {
                                Waist.this.mActivity.deleteFile(str);
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

    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0) {
            this.lastX = motionEvent.getRawX();
            this.lastY = motionEvent.getRawY();
            this.initialTranslationX = this.mControlLayout.getTranslationX();
            this.initialTranslationY = this.mControlLayout.getTranslationY();
            this.initialWidth = (float) this.mControlLayout.getWidth();
            this.initialHeight = (float) this.mControlLayout.getHeight();
            startWorkWithControl();
            return true;
        } else if (motionEvent.getAction() != 2) {
            if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                this.mBottomUtils.setVisibility(0);
                this.mSeekbar.setEnabled(true);
            }
            return true;
        } else {
            switch (view.getId()) {
                case R.id.mBottomImage:
                    int rawY = (int) ((this.initialHeight + motionEvent.getRawY()) - this.lastY);
                    if (rawY >= this.mMinHeight && ((float) rawY) <= ((float) this.mHeight) - this.initialTranslationY) {
                        this.mControlLayout.getLayoutParams().height = rawY;
                        this.mControlLayout.requestLayout();
                        break;
                    }
                case R.id.mCenterImage:
                    float rawX = (this.initialTranslationX + motionEvent.getRawX()) - this.lastX;
                    float rawY2 = (this.initialTranslationY + motionEvent.getRawY()) - this.lastY;
                    if (rawX >= 0.0f && rawX <= ((float) this.mWidth) - this.initialWidth) {
                        this.mControlLayout.setTranslationX(rawX);
                    }
                    if (rawY2 >= 0.0f && rawY2 <= ((float) this.mHeight) - this.initialHeight) {
                        this.mControlLayout.setTranslationY(rawY2);
                        break;
                    }
                case R.id.mLeftImage:
                    float rawX2 = motionEvent.getRawX() - this.lastX;
                    float f = this.initialWidth;
                    int i = (int) (f - rawX2);
                    if (i >= this.mMinWidth && ((float) i) <= f + this.initialTranslationX) {
                        this.mControlLayout.getLayoutParams().width = i;
                        this.mControlLayout.setTranslationX(this.initialTranslationX + rawX2);
                        this.mControlLayout.requestLayout();
                        break;
                    }
                case R.id.mRightImage:
                    int rawX3 = (int) ((this.initialWidth + motionEvent.getRawX()) - this.lastX);
                    if (rawX3 >= this.mMinWidth && ((float) rawX3) <= ((float) this.mWidth) - this.initialTranslationX) {
                        this.mControlLayout.getLayoutParams().width = rawX3;
                        this.mControlLayout.requestLayout();
                        break;
                    }
                case R.id.mTopImage:
                    float rawY3 = motionEvent.getRawY() - this.lastY;
                    float f2 = this.initialHeight;
                    int i2 = (int) (f2 - rawY3);
                    if (i2 >= this.mMinHeight && ((float) i2) <= this.initialTranslationY + f2) {
                        this.mControlLayout.getLayoutParams().height = i2;
                        this.mControlLayout.setTranslationY(this.initialTranslationY + rawY3);
                        this.mControlLayout.requestLayout();
                        break;
                    }
            }
            return true;
        }
    }

    private void startWorkWithControl() {
        this.mBottomUtils.setVisibility(8);
        this.mSeekbar.setEnabled(false);
        saveState();
    }

    @Override
    public void move(float f, float f2, float f3, float f4) {
        saveState();
    }

    @Override
    public void loadResponse(Bitmap bitmap, int i, int i2) {
        if (bitmap != null) {
            if (i2 > i && this.mIdCurrent < i2) {
                Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
                if (!createBitmap.isMutable()) {
                    Bitmap copy = createBitmap.copy(Bitmap.Config.ARGB_8888, true);
                    createBitmap.recycle();
                    createBitmap = copy;
                }
                Canvas canvas = new Canvas(createBitmap);
                WaistHistory waistHistory = this.history.get(i2 - 1);
                canvas.drawBitmapMesh(bitmap, waistHistory.column, waistHistory.row, waistHistory.currentMesh, 0, null, 0, null);
                this.mCanvas.drawBitmap(createBitmap, waistHistory.x, waistHistory.y, (Paint) null);
                createBitmap.recycle();
                this.mIdCurrent = i2;
                this.mIdRequisite = i2;
            } else if (i2 < i && i2 < this.mIdCurrent) {
                this.mCanvas.drawBitmap(bitmap, this.history.get(i2).x, this.history.get(i2).y, (Paint) null);
                this.mIdCurrent = i2;
                this.mIdRequisite = i2;
            }
            this.mScaleImage.invalidate();
            bitmap.recycle();
            return;
        }
        this.mIdRequisite = i;
    }

    public class WaistHistory {
        int column;
        float[] currentMesh;
        int row;
        float x;
        float y;

        WaistHistory(float[] fArr, float f, float f2, int i, int i2) {
            this.currentMesh = fArr;
            this.x = f;
            this.y = f2;
            this.column = i;
            this.row = i2;
        }
    }
}
