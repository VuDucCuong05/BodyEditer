package com.gos.body.editor.photo.sticker.event;

import android.view.MotionEvent;

import com.gos.body.editor.photo.sticker.StickerView;


public class ZoomIconEvent implements StickerIconEvent {
    public void onActionDown(StickerView stickerView, MotionEvent motionEvent) {
    }

    public void onActionMove(StickerView stickerView, MotionEvent motionEvent) {
        stickerView.zoomAndRotateCurrentSticker(motionEvent);
    }

    public void onActionUp(StickerView stickerView, MotionEvent motionEvent) {
        if (stickerView.getOnStickerOperationListener() != null) {
            stickerView.getOnStickerOperationListener().onStickerZoomFinished(stickerView.getCurrentSticker());
        }
    }
}
