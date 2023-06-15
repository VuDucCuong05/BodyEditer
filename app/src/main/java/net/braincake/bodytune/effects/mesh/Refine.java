package net.braincake.bodytune.effects.mesh;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import net.braincake.bodytune.activity.EditPhotoActivity;
import net.braincake.bodytune.R;
import net.braincake.bodytune.controls.ScaleImage;
import net.braincake.bodytune.controls.StartPointSeekBar;

public class Refine implements EditPhotoActivity.BackPressed, View.OnClickListener, ScaleImage.TouchInterface {
    private boolean isTouching;
    private float lastx;
    private float lasty;
    private EditPhotoActivity mActivity;
    private ConstraintLayout mBottomUtils;
    private FrameLayout mCancelButton;
    private Canvas mCanvas;
    private int mCircleRadius;
    private int mColums;
    private Bitmap mCurrentBitmap;
    private int mCurrentState = -1;
    private FrameLayout mDoneButton;
    private Paint mInterruptPaint;
    private Bitmap mLastBitmap;
    private StartPointSeekBar mMenuRefine;
    private Bitmap mOriginalBitmap;
    private Paint mPaint;
    private List<RefineHistory> mRefineHistory = new ArrayList();
    private int mRow;
    private ScaleImage mScaleImage;
    private int maxSize;
    private float stepX;
    private float stepY;
    private float[] verts;

    public Refine(Bitmap bitmap, EditPhotoActivity editPhotoActivity, ScaleImage scaleImage) {
        this.mOriginalBitmap = bitmap;
        this.mActivity = editPhotoActivity;
        this.mScaleImage = scaleImage;
        editPhotoActivity.mLoading.setVisibility(0);
        final Handler handler = new Handler();
        new Thread(new Runnable() {

            public void run() {
                Refine.this.createMesh();
                handler.post(new Runnable() {


                    public void run() {
                        Refine.this.mActivity.mLoading.setVisibility(8);
                        Refine.this.mActivity.isBlocked = false;
                        Refine.this.onCreate();
                    }
                });
            }
        }).start();
    }

    private void onCreate() {
        this.mBottomUtils = (ConstraintLayout) this.mActivity.findViewById(R.id.mBottomUtils);
        this.mCancelButton = (FrameLayout) this.mActivity.findViewById(R.id.mCancelButton);
        this.mDoneButton = (FrameLayout) this.mActivity.findViewById(R.id.mDoneButton);
        this.mMenuRefine = (StartPointSeekBar) this.mActivity.findViewById(R.id.menuRefine);
        Paint paint = new Paint();
        this.mPaint = paint;
        paint.setStrokeWidth(3.0f);
        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint.setFlags(1);
        this.mPaint.setColor(-1);
        Paint paint2 = new Paint();
        this.mInterruptPaint = paint2;
        paint2.setStrokeWidth(3.0f);
        this.mInterruptPaint.setStyle(Paint.Style.STROKE);
        this.mInterruptPaint.setFlags(1);
        this.mInterruptPaint.setColor(-1);
        this.mInterruptPaint.setPathEffect(new DashPathEffect(new float[]{15.0f, 10.0f}, 0.0f));
        this.mCurrentBitmap = this.mOriginalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Bitmap createBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        this.mLastBitmap = createBitmap;
        createBitmap.recycle();
        this.mCanvas = new Canvas(this.mCurrentBitmap);
        ((TextView) this.mActivity.findViewById(R.id.nameOfTool)).setText(this.mActivity.getResources().getString(R.string.refine));
        this.mScaleImage.setOnTouchInterface(this);
        this.mActivity.mUndoButton.setOnClickListener(this);
        this.mActivity.mRedoButton.setOnClickListener(this);
        this.mCancelButton.setOnClickListener(this);
        this.mDoneButton.setOnClickListener(this);

        this.mActivity.mBefore.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if (action == 0) {
                    Refine.this.mScaleImage.setImageBitmap(Refine.this.mOriginalBitmap);
                } else if (action == 1 || action == 3) {
                    Refine.this.mScaleImage.setImageBitmap(Refine.this.mCurrentBitmap);
                }
                return true;
            }
        });
        this.mMenuRefine.setProgress(50.0d);
        this.mMenuRefine.setOnSeekBarChangeListener(new StartPointSeekBar.OnSeekBarChangeListener() {


            @Override
            public void onOnSeekBarValueChange(StartPointSeekBar startPointSeekBar, long j) {
                Refine.this.mCanvas.drawBitmap(Refine.this.mLastBitmap, 0.0f, 0.0f, (Paint) null);
                Refine refine = Refine.this;
                refine.mCircleRadius = (int) (refine.stepX * 3.0f * ((((float) j) / 50.0f) + 1.0f));
                Refine.this.mCanvas.drawCircle((float) (Refine.this.mOriginalBitmap.getWidth() / 2), (float) (Refine.this.mOriginalBitmap.getHeight() / 2), (float) Refine.this.mCircleRadius, Refine.this.mPaint);
            }

            @Override
            public void onStartTrackingTouch(StartPointSeekBar startPointSeekBar) {
                if (Refine.this.mLastBitmap.isRecycled()) {
                    Refine refine = Refine.this;
                    refine.mLastBitmap = refine.mCurrentBitmap.copy(Bitmap.Config.ARGB_8888, true);
                }
                Refine.this.mScaleImage.setOnTouchInterface(null);
            }

            @Override
            public void onStopTrackingTouch(StartPointSeekBar startPointSeekBar) {
                Refine.this.mCanvas.drawBitmap(Refine.this.mLastBitmap, 0.0f, 0.0f, (Paint) null);
                if (!Refine.this.isTouching) {
                    Refine.this.mLastBitmap.recycle();
                    Refine.this.mBottomUtils.setVisibility(0);
                }
                Refine.this.mScaleImage.invalidate();
                Refine.this.mScaleImage.setOnTouchInterface(Refine.this);
            }
        });
        this.mMenuRefine.setVisibility(0);
        this.mScaleImage.setImageBitmap(this.mCurrentBitmap);
        this.mActivity.mShare.setOnClickListener(null);
        this.mActivity.mBack.setOnClickListener(null);
        this.mActivity.mTopUtils.setVisibility(8);
        this.mActivity.findViewById(R.id.menuHome).setVisibility(8);
        this.mActivity.findViewById(R.id.saveCloseContainer).setVisibility(0);
        this.mActivity.sendEvent("Refine - open");
    }

    private void close(boolean z) {
        this.mCurrentBitmap.recycle();
        this.mRefineHistory.clear();
        if (z) {
            this.mActivity.sendEvent("Refine - V");
        } else {
            this.mActivity.sendEvent("Tool - X");
            this.mActivity.sendEvent("Refine - X");
        }
        this.mScaleImage.setOnTouchInterface(null);
        this.mMenuRefine.setOnSeekBarChangeListener(null);
        this.mActivity.mUndoButton.setOnClickListener(this.mActivity);
        this.mActivity.mRedoButton.setOnClickListener(this.mActivity);
        this.mCancelButton.setOnClickListener(null);
        this.mDoneButton.setOnClickListener(null);
        this.mActivity.mShare.setOnClickListener(this.mActivity);
        this.mActivity.mBack.setOnClickListener(this.mActivity);
        this.mActivity.mBefore.setOnTouchListener(this.mActivity);
        this.mScaleImage.setImageBitmap(this.mActivity.mCurrentBitmap);
        this.mActivity.mTopUtils.setVisibility(0);
        this.mActivity.findViewById(R.id.saveCloseContainer).setVisibility(8);
        this.mActivity.findViewById(R.id.menuHome).setVisibility(0);
        this.mMenuRefine.setVisibility(8);
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
                if (this.mCurrentState + 1 < this.mRefineHistory.size()) {
                    int i = this.mCurrentState + 1;
                    this.mCurrentState = i;
                    RefineHistory refineHistory = this.mRefineHistory.get(i);
                    changeMesh(refineHistory.xLeft, refineHistory.yTop, refineHistory.xRight, refineHistory.yBottom, refineHistory.values, 1);
                    this.mActivity.sendEvent("Tool - Forward");
                    this.mActivity.sendEvent("Refine - Forward");
                    return;
                }
                return;
            case R.id.mUndoButton:
                if (this.mCurrentState > -1) {
                    this.mActivity.sendEvent("Tool - Back");
                    this.mActivity.sendEvent("Refine - Back");
                    RefineHistory refineHistory2 = this.mRefineHistory.get(this.mCurrentState);
                    changeMesh(refineHistory2.xLeft, refineHistory2.yTop, refineHistory2.xRight, refineHistory2.yBottom, refineHistory2.values, -1);
                    this.mCurrentState--;
                    return;
                }
                return;
            default:
                return;
        }
    }

    @Override
    public void onBackPressed(boolean z) {
        close(z);
    }

    @Override
    public void touch(int i, float f, float f2, float f3) {
        if (i == 0) {
            this.isTouching = true;
            this.lastx = f;
            this.lasty = f2;
            this.mLastBitmap = this.mCurrentBitmap.copy(Bitmap.Config.ARGB_8888, true);
            this.mCanvas.drawCircle(f, f2, (float) this.mCircleRadius, this.mPaint);
            this.mScaleImage.invalidate();
            this.mBottomUtils.setVisibility(4);
        } else if (i != 1) {
            if (i == 2) {
                this.mBottomUtils.setVisibility(0);
                if (!this.mLastBitmap.isRecycled()) {
                    this.mCanvas.drawBitmap(this.mLastBitmap, 0.0f, 0.0f, (Paint) null);
                    this.mLastBitmap.recycle();
                }
                if (this.isTouching && f != -1.0f) {
                    float degrees = (float) Math.toDegrees(Math.atan2((double) (this.lasty - f2), (double) (f - this.lastx)));
                    float sqrt = ((float) Math.sqrt(Math.pow((double) (this.lasty - f2), 2.0d) + Math.pow((double) (this.lastx - f), 2.0d))) / ((float) this.maxSize);
                    double d = (double) degrees;
                    float cos = this.stepX * sqrt * ((float) Math.cos(Math.toRadians(d)));
                    float sin = (-sqrt) * this.stepY * ((float) Math.sin(Math.toRadians(d)));
                    int max = Math.max((int) ((this.lastx - ((float) this.mCircleRadius)) / this.stepX), 0);
                    int min = Math.min(((int) ((this.lastx + ((float) this.mCircleRadius)) / this.stepX)) + 1, this.mColums);
                    int max2 = Math.max((int) ((this.lasty - ((float) this.mCircleRadius)) / this.stepY), 0);
                    int min2 = Math.min(((int) ((this.lasty + ((float) this.mCircleRadius)) / this.stepY)) + 1, this.mRow);
                    if (min - max <= 0 || min2 - max2 <= 0) {
                        this.isTouching = false;
                        return;
                    }
                    this.mCurrentState++;
                    while (this.mRefineHistory.size() > this.mCurrentState) {
                        List<RefineHistory> list = this.mRefineHistory;
                        list.remove(list.size() - 1);
                    }
                    changeMesh(max, max2, min, min2, cos, sin);
                }
                this.isTouching = false;
            }
        } else if (this.isTouching) {
            this.mCanvas.drawBitmap(this.mLastBitmap, 0.0f, 0.0f, (Paint) null);
            this.mCanvas.drawCircle(this.lastx, this.lasty, (float) this.mCircleRadius, this.mPaint);
            this.mCanvas.drawCircle(f, f2, (float) this.mCircleRadius, this.mPaint);
            this.mCanvas.drawLine(this.lastx, this.lasty, f, f2, this.mInterruptPaint);
            this.mScaleImage.invalidate();
        }
    }

    private void createMesh() {
        if (this.mOriginalBitmap.getWidth() > this.mOriginalBitmap.getHeight()) {
            this.mColums = 100;
            this.stepX = ((float) this.mOriginalBitmap.getWidth()) / ((float) this.mColums);
            this.mRow = (int) (((float) this.mOriginalBitmap.getHeight()) / this.stepX);
            this.stepY = ((float) this.mOriginalBitmap.getHeight()) / ((float) this.mRow);
        } else {
            this.mRow = 100;
            this.stepY = ((float) this.mOriginalBitmap.getHeight()) / ((float) this.mRow);
            this.mColums = (int) (((float) this.mOriginalBitmap.getWidth()) / this.stepY);
            this.stepX = ((float) this.mOriginalBitmap.getWidth()) / ((float) this.mColums);
        }
        this.mCircleRadius = (int) (this.stepX * 6.0f);
        this.maxSize = Math.max(this.mOriginalBitmap.getHeight(), this.mOriginalBitmap.getWidth()) / 2;
        int i = (this.mColums + 1) * (this.mRow + 1) * 2;
        this.verts = new float[i];
        for (int i2 = 0; i2 < i; i2 += 2) {
            float[] fArr = this.verts;
            int i3 = i2 / 2;
            int i4 = this.mColums;
            fArr[i2] = ((float) (i3 % (i4 + 1))) * this.stepX;
            fArr[i2 + 1] = ((float) (i3 / (i4 + 1))) * this.stepY;
        }
    }

    private void changeMesh(int i, int i2, int i3, int i4, float[][][] fArr, int i5) {
        for (int i6 = i2; i6 <= i4; i6++) {
            for (int i7 = i; i7 <= i3; i7++) {
                int i8 = (((this.mColums + 1) * i6) + i7) * 2;
                float[] fArr2 = this.verts;
                int i9 = i6 - i2;
                int i10 = i7 - i;
                float f = (float) i5;
                fArr2[i8] = fArr2[i8] + (fArr[i9][i10][0] * f);
                int i11 = i8 + 1;
                fArr2[i11] = fArr2[i11] + (fArr[i9][i10][1] * f);
            }
        }
        this.mCanvas.drawBitmapMesh(this.mOriginalBitmap, this.mColums, this.mRow, this.verts, 0, null, 0, null);
        this.mScaleImage.invalidate();
    }

    private void changeMesh(int i, int i2, int i3, int i4, float f, float f2) {
        int[] iArr = new int[3];
        iArr[2] = 2;
        iArr[1] = (i3 - i) + 1;
        iArr[0] = (i4 - i2) + 1;
        float[][][] fArr = (float[][][]) Array.newInstance(float.class, iArr);
        for (int i5 = i2; i5 <= i4; i5++) {
            for (int i6 = i; i6 <= i3; i6++) {
                int i7 = (((this.mColums + 1) * i5) + i6) * 2;
                float[] fArr2 = this.verts;
                float f3 = fArr2[i7];
                int i8 = i7 + 1;
                float f4 = fArr2[i8];
                float abs = Math.abs(this.lastx - f3);
                float abs2 = Math.abs(this.lasty - f4);
                float sqrt = (float) Math.sqrt((double) ((abs * abs) + (abs2 * abs2)));
                int i9 = this.mCircleRadius;
                if (sqrt < ((float) i9)) {
                    float f5 = (((float) i9) - sqrt) / ((float) i9);
                    if (i6 == 0 || i6 == this.mColums) {
                        float[] fArr3 = this.verts;
                        float f6 = f2 * f5;
                        fArr3[i8] = fArr3[i8] + f6;
                        fArr[i5 - i2][i6 - i][1] = f6;
                    } else if (i5 == 0 || i5 == this.mRow) {
                        float[] fArr4 = this.verts;
                        float f7 = f * f5;
                        fArr4[i7] = fArr4[i7] + f7;
                        fArr[i5 - i2][i6 - i][0] = f7;
                    } else {
                        float[] fArr5 = this.verts;
                        float f8 = f * f5;
                        fArr5[i7] = fArr5[i7] + f8;
                        float f9 = f2 * f5;
                        fArr5[i8] = fArr5[i8] + f9;
                        int i10 = i5 - i2;
                        int i11 = i6 - i;
                        fArr[i10][i11][0] = f8;
                        fArr[i10][i11][1] = f9;
                    }
                }
            }
        }
        this.mRefineHistory.add(new RefineHistory(i, i2, i3, i4, fArr));
        this.mCanvas.drawBitmapMesh(this.mOriginalBitmap, this.mColums, this.mRow, this.verts, 0, null, 0, null);
        this.mScaleImage.invalidate();
    }

    public class RefineHistory {
        float[][][] values;
        int xLeft;
        int xRight;
        int yBottom;
        int yTop;

        RefineHistory(int i, int i2, int i3, int i4, float[][][] fArr) {
            this.values = fArr;
            this.xLeft = i;
            this.xRight = i3;
            this.yBottom = i4;
            this.yTop = i2;
        }
    }
}
