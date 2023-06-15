package com.gos.body.editor.photo.sticker.event;

import android.view.MotionEvent;

import com.gos.body.editor.photo.sticker.StickerView;

public abstract class AbstractFlipEvent implements StickerIconEvent {

    public abstract int getFlipDirection();

    public void onActionDown(StickerView stickerView, MotionEvent motionEvent) {
    }

    public void onActionMove(StickerView stickerView, MotionEvent motionEvent) {
    }

    public void onActionUp(StickerView stickerView, MotionEvent motionEvent) {
        stickerView.flipCurrentSticker(getFlipDirection());
    }
}
