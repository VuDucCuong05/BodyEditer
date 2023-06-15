package com.gos.body.editor.photo.sticker.event;

import android.view.MotionEvent;

import com.gos.body.editor.photo.sticker.StickerView;

public interface StickerIconEvent {
    void onActionDown(StickerView stickerView, MotionEvent motionEvent);

    void onActionMove(StickerView stickerView, MotionEvent motionEvent);

    void onActionUp(StickerView stickerView, MotionEvent motionEvent);
}
