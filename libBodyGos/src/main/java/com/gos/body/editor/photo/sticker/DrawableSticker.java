package com.gos.body.editor.photo.sticker;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

public class DrawableSticker extends Sticker {
    private Drawable drawable;
    private Rect realBounds;

    public DrawableSticker(Drawable drawable2) {
        this.drawable = drawable2;
        realBounds = new Rect(0, 0, getWidth(), getHeight());
    }

    @NonNull
    public Drawable getDrawable() {
        return this.drawable;
    }

    public DrawableSticker setDrawable(@NonNull Drawable drawable2) {
        this.drawable =null;

        this.drawable = drawable2;
        return this;
    }

    public void draw(@NonNull Canvas canvas) {

        canvas.save();
        canvas.concat(getMatrix());
        this.drawable.clearColorFilter();
        this.drawable.setBounds(this.realBounds);

        this.drawable.draw(canvas);

        canvas.restore();
        canvas = null;
    }

    @NonNull
    public DrawableSticker setAlpha(@IntRange(from = 0, to = 255) int i) {
        this.drawable.setAlpha(i);
        return this;
    }

    public int getAlpha() {
        return this.drawable.getAlpha();
    }

    public int getWidth() {
        return this.drawable.getIntrinsicWidth();
    }

    public int getHeight() {
        return this.drawable.getIntrinsicHeight();
    }

    public void release() {
        super.release();
        if (this.drawable != null) {
            this.drawable = null;
        }
    }
}
