package net.braincake.bodytune.controls;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import androidx.recyclerview.widget.ItemTouchHelper;
import net.braincake.bodytune.R;

public class StartPointSeekBar extends View {
    private static final int DEFAULT_BACKGROUND_COLOR = Color.parseColor("#66ffffff");
    private static final int DEFAULT_BORDER_COLOR = Color.parseColor("#33121212");
    private static final float DEFAULT_MAX_VALUE = 100.0f;
    private static final float DEFAULT_MIN_VALUE = 0.0f;
    private static final int DEFAULT_RANGE_COLOR = Color.parseColor("#F7252E");
    private static final Paint paint = new Paint(1);
    private static final Paint strokePaint = new Paint(1);
    private double absoluteMaxValue;
    private double absoluteMinValue;
    private final int defaultBackgroundColor;
    private final int defaultRangeColor;
    private final float lineHeight;
    private OnSeekBarChangeListener listener;
    private double normalizedThumbValue;
    private final float padding;
    private long progress;
    RectF rect;
    private final float thumbHalfHeight;
    private final float thumbHalfWidth;
    private final Bitmap thumbImage;

    public interface OnSeekBarChangeListener {
        void onOnSeekBarValueChange(StartPointSeekBar startPointSeekBar, long j);

        void onStartTrackingTouch(StartPointSeekBar startPointSeekBar);

        void onStopTrackingTouch(StartPointSeekBar startPointSeekBar);
    }

    public void setOnSeekBarChangeListener(OnSeekBarChangeListener onSeekBarChangeListener) {
        this.listener = onSeekBarChangeListener;
    }

    public StartPointSeekBar(Context context) {
        this(context, null);
    }

    public StartPointSeekBar(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public StartPointSeekBar(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.normalizedThumbValue = 0.0d;
        this.rect = new RectF();
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.ActionBar, i, 0);
        Drawable drawable = obtainStyledAttributes.getDrawable(7);
        drawable = drawable == null ? getResources().getDrawable(R.drawable.seekbar_thumb) : drawable;
        float intrinsicHeight = ((float) drawable.getIntrinsicHeight()) / ((float) drawable.getIntrinsicWidth());
        @SuppressLint("ResourceType") int dimension = (int) obtainStyledAttributes.getDimension(3, (float) drawable.getIntrinsicHeight());
        int i2 = (int) (((float) dimension) / intrinsicHeight);
        this.thumbImage = Bitmap.createBitmap(i2, dimension, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(this.thumbImage);
        drawable.setBounds(0, 0, i2, dimension);
        drawable.draw(canvas);
        this.absoluteMinValue = (double) obtainStyledAttributes.getFloat(5, 0.0f);
        this.absoluteMaxValue = (double) obtainStyledAttributes.getFloat(4, DEFAULT_MAX_VALUE);
        double valueToNormalized = valueToNormalized((double) obtainStyledAttributes.getFloat(6, (float) this.absoluteMinValue));
        this.normalizedThumbValue = valueToNormalized;
        this.progress = Math.round(normalizedToValue(valueToNormalized));
        this.defaultBackgroundColor = obtainStyledAttributes.getColor(0, DEFAULT_BACKGROUND_COLOR);
        this.defaultRangeColor = obtainStyledAttributes.getColor(1, DEFAULT_RANGE_COLOR);
        int color = obtainStyledAttributes.getColor(2, DEFAULT_BORDER_COLOR);
        obtainStyledAttributes.recycle();
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(1.0f);
        strokePaint.setColor(color);
        this.thumbHalfWidth = ((float) this.thumbImage.getWidth()) * 0.5f;
        float height = ((float) this.thumbImage.getHeight()) * 0.5f;
        this.thumbHalfHeight = height;
        this.lineHeight = height * 0.45f;
        this.padding = this.thumbHalfWidth;
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    public void setAbsoluteMinMaxValue(double d, double d2) {
        this.absoluteMinValue = d;
        this.absoluteMaxValue = d2;
    }

    public synchronized void onMeasure(int i, int i2) {
        int i3 = ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION;
        if (MeasureSpec.getMode(i) != 0) {
            i3 = MeasureSpec.getSize(i);
        }
        int height = this.thumbImage.getHeight();
        if (MeasureSpec.getMode(i2) != 0) {
            height = Math.min(height, MeasureSpec.getSize(i2));
        }
        setMeasuredDimension(i3, height);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!isEnabled()) {
            return false;
        }
        int action = motionEvent.getAction() & 255;
        if (action == 0) {
            trackTouchEvent(motionEvent);
            attemptClaimDrag();
            this.progress = Math.round(normalizedToValue(this.normalizedThumbValue));
            OnSeekBarChangeListener onSeekBarChangeListener = this.listener;
            if (onSeekBarChangeListener != null) {
                onSeekBarChangeListener.onStartTrackingTouch(this);
                this.listener.onOnSeekBarValueChange(this, this.progress);
            }
        } else if (action == 1) {
            trackTouchEvent(motionEvent);
            OnSeekBarChangeListener onSeekBarChangeListener2 = this.listener;
            if (onSeekBarChangeListener2 != null) {
                onSeekBarChangeListener2.onStopTrackingTouch(this);
            }
        } else if (action == 2) {
            trackTouchEvent(motionEvent);
            sendMoveAction();
        } else if (action == 3) {
            OnSeekBarChangeListener onSeekBarChangeListener3 = this.listener;
            if (onSeekBarChangeListener3 != null) {
                onSeekBarChangeListener3.onStopTrackingTouch(this);
            }
        } else if (action == 5) {
            trackTouchEvent(motionEvent);
            sendMoveAction();
        } else if (action == 6) {
            trackTouchEvent(motionEvent);
            sendMoveAction();
        }
        return true;
    }

    public long getProgress() {
        return this.progress;
    }

    private void sendMoveAction() {
        long round = Math.round(normalizedToValue(this.normalizedThumbValue));
        if (round != this.progress) {
            this.progress = round;
            OnSeekBarChangeListener onSeekBarChangeListener = this.listener;
            if (onSeekBarChangeListener != null) {
                onSeekBarChangeListener.onOnSeekBarValueChange(this, round);
            }
        }
    }

    private void attemptClaimDrag() {
        if (getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
    }

    private void trackTouchEvent(MotionEvent motionEvent) {
        setNormalizedValue(screenToNormalized(motionEvent.getX(motionEvent.getPointerCount() - 1)));
    }

    private double screenToNormalized(float f) {
        float width = (float) getWidth();
        float f2 = this.padding;
        if (width <= f2 * 2.0f) {
            return 0.0d;
        }
        return Math.min(1.0d, Math.max(0.0d, (double) ((f - f2) / (width - (f2 * 2.0f)))));
    }

    private double normalizedToValue(double d) {
        double d2 = this.absoluteMinValue;
        return d2 + (d * (this.absoluteMaxValue - d2));
    }

    private double valueToNormalized(double d) {
        double d2 = this.absoluteMaxValue;
        double d3 = this.absoluteMinValue;
        if (0.0d == d2 - d3) {
            return 0.0d;
        }
        return (d - d3) / (d2 - d3);
    }

    private void setNormalizedValue(double d) {
        this.normalizedThumbValue = Math.max(0.0d, d);
        invalidate();
    }

    public void setProgress(double d) {
        double valueToNormalized = valueToNormalized(d);
        if (valueToNormalized > this.absoluteMaxValue || valueToNormalized < this.absoluteMinValue) {
            throw new IllegalArgumentException("Value should be in the middle of max and min value");
        }
        this.normalizedThumbValue = valueToNormalized;
        invalidate();
    }

    private float normalizedToScreen(double d) {
        return (float) (((double) this.padding) + (d * ((double) (((float) getWidth()) - (this.padding * 2.0f)))));
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.rect.top = (((float) getHeight()) - this.lineHeight) * 0.5f;
        this.rect.bottom = (((float) getHeight()) + this.lineHeight) * 0.5f;
        this.rect.left = this.padding;
        this.rect.right = ((float) getWidth()) - this.padding;
        paint.setColor(this.defaultBackgroundColor);
        RectF rectF = this.rect;
        float f = this.lineHeight;
        canvas.drawRoundRect(rectF, f, f, paint);
        if (normalizedToScreen(valueToNormalized(0.0d)) < normalizedToScreen(this.normalizedThumbValue)) {
            this.rect.left = normalizedToScreen(valueToNormalized(0.0d));
            this.rect.right = normalizedToScreen(this.normalizedThumbValue);
        } else {
            this.rect.right = normalizedToScreen(valueToNormalized(0.0d));
            this.rect.left = normalizedToScreen(this.normalizedThumbValue);
        }
        paint.setColor(this.defaultRangeColor);
        if (this.absoluteMinValue < 0.0d) {
            canvas.drawRect(this.rect, paint);
        } else {
            RectF rectF2 = this.rect;
            float f2 = this.lineHeight;
            canvas.drawRoundRect(rectF2, f2, f2, paint);
        }
        this.rect.left = this.padding;
        this.rect.right = ((float) getWidth()) - this.padding;
        RectF rectF3 = this.rect;
        float f3 = this.lineHeight;
        canvas.drawRoundRect(rectF3, f3, f3, strokePaint);
        drawThumb(normalizedToScreen(this.normalizedThumbValue), canvas);
    }

    private void drawThumb(float f, Canvas canvas) {
        canvas.drawBitmap(this.thumbImage, f - this.thumbHalfWidth, (((float) getHeight()) * 0.5f) - this.thumbHalfHeight, paint);
    }
}
