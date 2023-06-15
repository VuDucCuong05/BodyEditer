package com.gos.body.editor.photo.beauty;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.gos.body.editor.photo.R;
import com.gos.body.editor.photo.SharePreferenceUtil;
import com.gos.body.editor.photo.interfaces.OnSaveBitmap;
import com.gos.body.editor.photo.sticker.BeautySticker;
import com.gos.body.editor.photo.sticker.BitmapStickerIcon;
import com.gos.body.editor.photo.sticker.Sticker;
import com.gos.body.editor.photo.sticker.StickerView;
import com.gos.body.editor.photo.sticker.event.ZoomIconEvent;
import com.gos.body.editor.photo.utils.SystemUtil;
import com.gos.body.editor.photo.views.DegreeSeekBar;
import com.gos.body.editor.photo.views.PhotoEditorView;

import org.wysaid.nativePort.CGEDeformFilterWrapper;
import org.wysaid.nativePort.CGEImageHandler;
import org.wysaid.texUtils.TextureRenderer;
import org.wysaid.view.ImageGLSurfaceView;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class BeautyDialog extends DialogFragment {
    static final int BOOB = 0;
    static final int FACE = 3;
    static final int HIP_1 = 2;
    static final int MAGIC = 4;
    static final int WAIST = 1;
    private Bitmap bitmap;
    private ImageView boobs;
    private ImageView compare;

    public int currentType = 7;
    private ImageView face;

    public ImageGLSurfaceView glSurfaceView;

    public SeekBar intensitySmooth;
    private DegreeSeekBar intensityTwoDirection;

    public RelativeLayout loadingView;
    private List<Retouch> lstRetouch;

    public CGEDeformFilterWrapper mDeformWrapper;
    private float mTouchRadiusForWaist;
    private LinearLayout magicLayout;
    private static final String TAG = "BeautyDialog";

    public OnBeautySave onBeautySave;
    View.OnClickListener onClickListener = view -> {
        int id = view.getId();
        if (id == R.id.resetWaist) {
            BeautyDialog.this.glSurfaceView.flush(true, () -> {
                if (BeautyDialog.this.mDeformWrapper != null) {
                    BeautyDialog.this.mDeformWrapper.restore();
                    BeautyDialog.this.glSurfaceView.requestRender();
                }
            });
            return;
        } else if (id == R.id.wrapBoobs) {
            BeautyDialog.this.showAdjustBoobs();
            return;
        } else if (id == R.id.wrapFace) {
            BeautyDialog.this.showAdjustFace();
            return;
        } else if (id == R.id.wrapHip) {
            BeautyDialog.this.showAdjustHipOne();
            return;
        } else if (id == R.id.wrapWaist) {
            BeautyDialog.this.showWaist();
            return;
        }
        return;
    };
    SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            if (BeautyDialog.this.intensitySmooth.getProgress() == 0) {
                BeautyDialog.this.glSurfaceView.setFilterWithConfig("");
                return;
            }
            ImageGLSurfaceView access$100 = BeautyDialog.this.glSurfaceView;
            access$100.setFilterWithConfig(MessageFormat.format("@beautify face 1 {0} 640", BeautyDialog.this.intensitySmooth.getProgress() + ""));
        }
    };

    public PhotoEditorView photoEditorView;
    private RelativeLayout resetWaist;
    private ImageView seat;

    public float startX;

    public float startY;
    private TextView tvBoobs;
    private TextView tvFace;
    private TextView tvSeat;
    private TextView tvWaise;
    private ViewGroup viewGroup;
    private ImageView waise;
    private RelativeLayout wrapBoobs;
    private RelativeLayout wrapFace;
    private RelativeLayout wrapHip;
    private RelativeLayout wrapWaist;

    public interface OnBeautySave {
        void onBeautySave(Bitmap bitmap);
    }

    public void setBitmap(Bitmap bitmap2) {
        this.bitmap = bitmap2;
    }

    public void showWaist() {
        Log.d(TAG, "showWaist: ");
        if (SharePreferenceUtil.isFirstAdjustWaise(getContext())) {
            View inflate = LayoutInflater.from(getContext()).inflate(R.layout.waise_instruction, this.viewGroup, false);
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setCancelable(false);
            builder.setView(inflate);
            final AlertDialog create = builder.create();
            inflate.findViewById(R.id.btnDone).setOnClickListener(view -> BeautyDialog.lambda$showWaist$0(BeautyDialog.this, create, view));
            create.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            create.show();
        }
        saveCurrentState();
        selectFunction(1);
        this.magicLayout.setVisibility(View.GONE);
        this.photoEditorView.setHandlingSticker(null);
        this.photoEditorView.setDrawCirclePoint(true);
        this.resetWaist.setVisibility(View.VISIBLE);
        this.intensityTwoDirection.setVisibility(View.GONE);
        this.currentType = 3;
        this.intensityTwoDirection.setCurrentDegrees(0);
        this.mTouchRadiusForWaist = (float) SystemUtil.dpToPx(getContext(), 20);
        this.photoEditorView.setCircleRadius((int) this.mTouchRadiusForWaist);
        this.photoEditorView.getStickers().clear();
    }

    public static void lambda$showWaist$0(BeautyDialog beautyDialog, AlertDialog alertDialog, View view) {
        alertDialog.dismiss();
        SharePreferenceUtil.setFirstAdjustWaist(beautyDialog.getContext(), false);
    }

    private void saveCurrentState() {
        new SaveCurrentState().execute();
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);

    }

    public void showAdjustBoobs() {
        Log.d(TAG, "showAdjustBoobs: ");
        if (SharePreferenceUtil.isFirstAdjustBoob(getContext())) {
            View inflate = LayoutInflater.from(getContext()).inflate(R.layout.boobs_instruction, this.viewGroup, false);
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setCancelable(false);
            builder.setView(inflate);
            final AlertDialog create = builder.create();
            inflate.findViewById(R.id.btnDone).setOnClickListener(view -> {
                create.dismiss();
                SharePreferenceUtil.setFirstAdjustBoob(BeautyDialog.this.getContext(), false);
            });
            create.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            create.show();
        }
        saveCurrentState();
        this.intensityTwoDirection.setVisibility(View.VISIBLE);
        this.intensityTwoDirection.setDegreeRange(-30, 30);
        this.resetWaist.setVisibility(View.GONE);
        this.photoEditorView.setDrawCirclePoint(false);
        selectFunction(0);
        this.magicLayout.setVisibility(View.GONE);
        this.currentType = 7;
        this.intensityTwoDirection.setCurrentDegrees(0);
        this.photoEditorView.getStickers().clear();
        this.photoEditorView.addSticker(new BeautySticker(getContext(), 0, ContextCompat.getDrawable(getContext(), R.drawable.circle_beauty)));
        this.photoEditorView.addSticker(new BeautySticker(getContext(), 1, ContextCompat.getDrawable(getContext(), R.drawable.circle_beauty)));
    }

    public void showAdjustFace() {
        Log.d(TAG, "showAdjustFace: ");
        if (SharePreferenceUtil.isFirstAdjusFace(getContext())) {
            View inflate = LayoutInflater.from(getContext()).inflate(R.layout.chin_instruction, this.viewGroup, false);
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setCancelable(false);
            builder.setView(inflate);
            final AlertDialog create = builder.create();
            inflate.findViewById(R.id.btnDone).setOnClickListener(view -> BeautyDialog.lambda$showAdjustFace$1(BeautyDialog.this, create, view));
            create.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            create.show();
        }
        saveCurrentState();
        this.intensityTwoDirection.setVisibility(View.VISIBLE);
        this.intensityTwoDirection.setDegreeRange(-15, 15);
        this.resetWaist.setVisibility(View.GONE);

        this.photoEditorView.setDrawCirclePoint(false);
        selectFunction(3);
        this.currentType = 4;
        this.magicLayout.setVisibility(View.GONE);
        this.intensityTwoDirection.setCurrentDegrees(0);
        this.photoEditorView.getStickers().clear();
        this.photoEditorView.addSticker(new BeautySticker(getContext(), 4, getResources().getDrawable(R.drawable.chin)));
        Log.d(TAG, "showAdjustFace: currentType " + currentType);
    }

    public static void lambda$showAdjustFace$1(BeautyDialog beautyDialog, AlertDialog alertDialog, View view) {
        alertDialog.dismiss();
        SharePreferenceUtil.setFirstAdjustFace(beautyDialog.getContext(), false);
    }

    public void showAdjustHipOne() {
        Log.d(TAG, "showAdjustHipOne: ");
        if (SharePreferenceUtil.isFirstAdjusHip(getContext())) {
            View inflate = LayoutInflater.from(getContext()).inflate(R.layout.hip_instruction, this.viewGroup, false);
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setCancelable(false);
            builder.setView(inflate);
            final AlertDialog create = builder.create();
            inflate.findViewById(R.id.btnDone).setOnClickListener(view -> BeautyDialog.lambda$showAdjustHipOne$2(BeautyDialog.this, create, view));
            create.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            create.show();
        }
        saveCurrentState();
        this.intensityTwoDirection.setVisibility(View.VISIBLE);
        this.intensityTwoDirection.setDegreeRange(-30, 30);
        this.resetWaist.setVisibility(View.GONE);
        this.photoEditorView.setDrawCirclePoint(false);
        selectFunction(2);
        this.intensityTwoDirection.setCurrentDegrees(0);
        this.currentType = 9;
        this.photoEditorView.getStickers().clear();
        this.photoEditorView.addSticker(new BeautySticker(getContext(), 2, getResources().getDrawable(R.drawable.hip_1)));
    }

    public static void lambda$showAdjustHipOne$2(BeautyDialog beautyDialog, AlertDialog alertDialog, View view) {
        alertDialog.dismiss();
        SharePreferenceUtil.setFirstAdjustHip(beautyDialog.getContext(), false);
    }

    public void hideAllFunction() {
        this.intensityTwoDirection.setVisibility(View.GONE);
        this.resetWaist.setVisibility(View.GONE);
        this.magicLayout.setVisibility(View.GONE);
        this.loadingView.setVisibility(View.GONE);
    }

    public void setOnBeautySave(OnBeautySave onBeautySave2) {
        this.onBeautySave = onBeautySave2;
    }

    public static BeautyDialog show(@NonNull AppCompatActivity appCompatActivity, Bitmap bitmap2, OnBeautySave onBeautySave2) {
        BeautyDialog beautyDialog = new BeautyDialog();
        beautyDialog.setBitmap(bitmap2);
        beautyDialog.setOnBeautySave(onBeautySave2);
        beautyDialog.show(appCompatActivity.getSupportFragmentManager(), "BeautyDialog");
        return beautyDialog;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setRetainInstance(true);
    }

    @SuppressLint("ResourceType")
    @Nullable
    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup2, @Nullable Bundle bundle) {
        getDialog().getWindow().requestFeature(1);
        getDialog().getWindow().setFlags(1024, 1024);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));
        getDialog().getWindow().setDimAmount(0.0f);
        View inflate = layoutInflater.inflate(R.layout.beauty_layout, viewGroup2, false);
        this.intensityTwoDirection = inflate.findViewById(R.id.intensityTwoDirection);
        this.intensityTwoDirection.setDegreeRange(-20, 20);
        this.photoEditorView = inflate.findViewById(R.id.photoEditorView);


        this.glSurfaceView = this.photoEditorView.getGLSurfaceView();
        this.loadingView = inflate.findViewById(R.id.loadingView);
        this.boobs = inflate.findViewById(R.id.boobs);
        this.wrapBoobs = inflate.findViewById(R.id.wrapBoobs);
        this.wrapBoobs.setOnClickListener(this.onClickListener);
        this.tvBoobs = inflate.findViewById(R.id.tvBoobs);
        this.waise = inflate.findViewById(R.id.waist);
        this.wrapWaist = inflate.findViewById(R.id.wrapWaist);
        this.wrapWaist.setOnClickListener(this.onClickListener);
        this.tvWaise = inflate.findViewById(R.id.tvWaist);
        this.resetWaist = inflate.findViewById(R.id.resetWaist);
        this.resetWaist.setOnClickListener(this.onClickListener);
        this.seat = inflate.findViewById(R.id.seat);
        this.wrapHip = inflate.findViewById(R.id.wrapHip);
        this.wrapHip.setOnClickListener(this.onClickListener);
        this.tvSeat = inflate.findViewById(R.id.tvSeat);
        this.face = inflate.findViewById(R.id.face);
        this.wrapFace = inflate.findViewById(R.id.wrapFace);
        this.wrapFace.setOnClickListener(this.onClickListener);
        this.tvFace = inflate.findViewById(R.id.tvFace);
        this.magicLayout = inflate.findViewById(R.id.magicLayout);
        this.viewGroup = inflate.findViewById(16908290);
        this.intensitySmooth = inflate.findViewById(R.id.intensitySmooth);
        this.intensitySmooth.setOnSeekBarChangeListener(this.onSeekBarChangeListener);
        this.lstRetouch = new ArrayList();
//        this.lstRetouch.add(new Retouch(this.boobs, this.tvBoobs, Utils.getResourceIdFromAttr(getContext(),R.attr.icBoob), Utils.getResourceIdFromAttr(getContext(),R.attr.icBoobSelected)));
//        this.lstRetouch.add(new Retouch(this.waise, this.tvWaise, Utils.getResourceIdFromAttr(getContext(),R.attr.icWaist), Utils.getResourceIdFromAttr(getContext(),R.attr.icWaistSelected)));
//        this.lstRetouch.add(new Retouch(this.seat, this.tvSeat, Utils.getResourceIdFromAttr(getContext(),R.attr.icHip), Utils.getResourceIdFromAttr(getContext(),R.attr.icHipSelected)));
//        this.lstRetouch.add(new Retouch(this.face, this.tvFace, Utils.getResourceIdFromAttr(getContext(),R.attr.icFace), Utils.getResourceIdFromAttr(getContext(),R.attr.icFaceSelected)));
        this.intensityTwoDirection.setScrollingListener(new DegreeSeekBar.ScrollingListener() {
            public void onScrollStart() {
                Log.d(TAG, "onScrollStart: ");
                Iterator<Sticker> it = BeautyDialog.this.photoEditorView.getStickers().iterator();
                while (it.hasNext()) {
                    ((BeautySticker) it.next()).updateRadius();
                }
            }

            public void onScroll(final int scrollValue) {
                TextureRenderer.Viewport renderViewport = BeautyDialog.this.glSurfaceView.getRenderViewport();
                final float width = (float) renderViewport.width;
                final float height = (float) renderViewport.height;
                Log.d(TAG, "onScroll: currentType " + currentType);
                if (BeautyDialog.this.currentType == 7) {
                    Log.d(TAG, "onScroll: BeautyDialog.this.currentType == 7");
                    BeautyDialog.this.glSurfaceView.lazyFlush(true, new Runnable() {


                        public final void run() {
                            onScrollCustom(scrollValue, width, height);
                        }
                    });
                } else if (BeautyDialog.this.currentType == 9) {
                    Log.d(TAG, "onScroll: BeautyDialog.this.currentType == 9");
                    BeautyDialog.this.glSurfaceView.lazyFlush(true, () -> {
                        if (BeautyDialog.this.mDeformWrapper != null) {
                            BeautyDialog.this.mDeformWrapper.restore();
                            Iterator<Sticker> it = BeautyDialog.this.photoEditorView.getStickers().iterator();
                            while (it.hasNext()) {
                                BeautySticker beautySticker = (BeautySticker) it.next();
                                PointF mappedCenterPoint2 = beautySticker.getMappedCenterPoint2();
                                RectF mappedBound = beautySticker.getMappedBound();
                                for (int i = 0; i < Math.abs(scrollValue); i++) {
                                    Log.e("klhjfasdjhfafe", width + "      s    " + height);
                                    if (scrollValue > 0) {
                                        BeautyDialog.this.mDeformWrapper.forwardDeform(mappedBound.right - 20, mappedCenterPoint2.y, mappedBound.right + 20, mappedCenterPoint2.y, width, height, (float) beautySticker.getRadius(), 0.01f);
                                        BeautyDialog.this.mDeformWrapper.forwardDeform(mappedBound.left + 20, mappedCenterPoint2.y, mappedBound.left - 20, mappedCenterPoint2.y, width, height, (float) beautySticker.getRadius(), 0.01f);
                                    } else {
                                        BeautyDialog.this.mDeformWrapper.forwardDeform(mappedBound.right + 20, mappedCenterPoint2.y, mappedBound.right - 20, mappedCenterPoint2.y, width, height, (float) beautySticker.getRadius(), 0.01f);
                                        BeautyDialog.this.mDeformWrapper.forwardDeform(mappedBound.left - 20, mappedCenterPoint2.y, mappedBound.left + 20, mappedCenterPoint2.y, width, height, (float) beautySticker.getRadius(), 0.01f);
                                    }
                                }
                            }
                        }
                    });
                } else if (BeautyDialog.this.currentType == 4) {
                    Log.d(TAG, "onScroll: BeautyDialog.this.currentType == 4");
                    BeautyDialog.this.glSurfaceView.lazyFlush(true, () -> {
                        if (BeautyDialog.this.mDeformWrapper != null) {
                            BeautyDialog.this.mDeformWrapper.restore();
                            Iterator<Sticker> it2 = BeautyDialog.this.photoEditorView.getStickers().iterator();
                            while (it2.hasNext()) {
                                BeautySticker beautySticker = (BeautySticker) it2.next();
                                PointF mappedCenterPoint2 = beautySticker.getMappedCenterPoint2();
                                RectF mappedBound = beautySticker.getMappedBound();
                                int radius = beautySticker.getRadius() / 2;
                                float f = (mappedBound.left + mappedCenterPoint2.x) / 2.0f;
                                float f2 = mappedBound.left + ((f - mappedBound.left) / 2.0f);
                                float f3 = (mappedBound.bottom + mappedBound.top) / 2.0f;
                                float f4 = mappedBound.top + ((f3 - mappedBound.top) / 2.0f);
                                float f5 = (mappedBound.right + mappedCenterPoint2.x) / 2.0f;
                                float f6 = mappedBound.right - ((mappedBound.right - f5) / 2.0f);
                                float f7 = (mappedBound.bottom + mappedBound.top) / 2.0f;
                                float f8 = mappedBound.top + ((f7 - mappedBound.top) / 2.0f);
                                int i = 0;
                                while (i < Math.abs(scrollValue)) {
                                    Log.d(TAG, "run2: i " + i);
                                    if (scrollValue < 0) {
                                        BeautyDialog.this.mDeformWrapper.forwardDeform(mappedBound.right, mappedBound.top, mappedBound.right - radius, mappedBound.top, width, height, (float) beautySticker.getRadius(), 0.002f);
                                        BeautyDialog.this.mDeformWrapper.forwardDeform(f6, f8, f6 - radius, f8, width, height, (float) beautySticker.getRadius(), 0.005f);
                                        BeautyDialog.this.mDeformWrapper.forwardDeform(f5, f7, f5 - radius, f7, width, height, (float) beautySticker.getRadius(), 0.007f);
                                        BeautyDialog.this.mDeformWrapper.forwardDeform(mappedBound.left, mappedBound.top, mappedBound.left + radius, mappedBound.top, width, height, (float) beautySticker.getRadius(), 0.002f);
                                        BeautyDialog.this.mDeformWrapper.forwardDeform(f2, f4, f2 + radius, f4, width, height, (float) beautySticker.getRadius(), 0.005f);
                                        BeautyDialog.this.mDeformWrapper.forwardDeform(f, f3, f + radius, f3, width, height, (float) beautySticker.getRadius(), 0.007f);
                                    } else {
                                        BeautyDialog.this.mDeformWrapper.forwardDeform(mappedBound.right, mappedBound.top, mappedBound.right + radius, mappedBound.top, width, height, (float) beautySticker.getRadius(), 0.002f);
                                        BeautyDialog.this.mDeformWrapper.forwardDeform(f6, f8, f6 + radius, f8, width, height, (float) beautySticker.getRadius(), 0.005f);
                                        BeautyDialog.this.mDeformWrapper.forwardDeform(f5, f7, f5 + radius, f7, width, height, (float) beautySticker.getRadius(), 0.007f);
                                        BeautyDialog.this.mDeformWrapper.forwardDeform(mappedBound.left + radius, mappedBound.top, mappedBound.left, mappedBound.top, width, height, (float) beautySticker.getRadius(), 0.002f);
                                        BeautyDialog.this.mDeformWrapper.forwardDeform(f2, f4, f2 - radius, f4, width, height, (float) beautySticker.getRadius(), 0.005f);
                                        BeautyDialog.this.mDeformWrapper.forwardDeform(f, f3, f - radius, f3, width, height, (float) beautySticker.getRadius(), 0.007f);
                                    }
                                    i++;
                                }
                            }
                        }
                    });
                }
            }

            public void onScrollCustom(int i, float f, float f2) {
                Log.e("dflpadfa", f + "   s    " + f2);
                Log.d(TAG, "onScrollCustom: ");
                if (BeautyDialog.this.mDeformWrapper != null) {
                    BeautyDialog.this.mDeformWrapper.restore();
                    for (Sticker next : BeautyDialog.this.photoEditorView.getStickers()) {
                        PointF mappedCenterPoint2 = ((BeautySticker) next).getMappedCenterPoint2();

                        RectF mappedBound = next.getMappedBound();
                        Log.e("hsdfgsr", mappedBound.left + "         s       " + mappedBound.right + "       s        " + (mappedBound.right - mappedBound.left) + "   s    " + next.getWidth());

                        for (int i2 = 0; i2 < Math.abs(i); i2++) {
                            if (i > 0) {
                                BeautyDialog.this.mDeformWrapper.bloatDeform(mappedCenterPoint2.x, mappedCenterPoint2.y, f, f2, (float) (mappedBound.right - mappedBound.left), 0.03f);
                            } else if (i < 0) {
                                BeautyDialog.this.mDeformWrapper.wrinkleDeform(mappedCenterPoint2.x, mappedCenterPoint2.y, f, f2, (float) (mappedBound.right - mappedBound.left), 0.03f);
                            }
                        }
                    }
                }
            }

            public void onScrollEnd() {
                BeautyDialog.this.glSurfaceView.requestRender();
            }
        });
        BitmapStickerIcon bitmapStickerIcon = new BitmapStickerIcon(ContextCompat.getDrawable(getContext(), R.drawable.sticker_ic_scale_white_18dp), 3, BitmapStickerIcon.ZOOM);
        bitmapStickerIcon.setIconEvent(new ZoomIconEvent());
        BitmapStickerIcon bitmapStickerIcon2 = new BitmapStickerIcon(ContextCompat.getDrawable(getContext(), R.drawable.sticker_ic_scale_white_2_18dp), 2, BitmapStickerIcon.ZOOM);
        bitmapStickerIcon2.setIconEvent(new ZoomIconEvent());
        this.photoEditorView.setIcons(Arrays.asList(bitmapStickerIcon, bitmapStickerIcon2));
        this.photoEditorView.setBackgroundColor(ViewCompat.MEASURED_STATE_MASK);
        this.photoEditorView.setLocked(false);
        this.photoEditorView.setConstrained(true);
        this.photoEditorView.setOnStickerOperationListener(new StickerView.OnStickerOperationListener() {
            public void onStickerAdded(@NonNull Sticker sticker) {
            }

            public void onStickerClicked(@NonNull Sticker sticker) {
            }

            public void onStickerDoubleTapped(@NonNull Sticker sticker) {
            }

            public void onStickerDragFinished(@NonNull Sticker sticker) {
            }

            public void onStickerFlipped(@NonNull Sticker sticker) {
            }

            public void onStickerTouchOutside() {
            }

            public void onStickerTouchedDown(@NonNull Sticker sticker) {
            }

            public void onStickerZoomFinished(@NonNull Sticker sticker) {
            }

            public void onTouchUpForBeauty(float x, float y) {
            }

            public void onStickerDeleted(@NonNull Sticker sticker) {
                BeautyDialog.this.loadingView.setVisibility(View.GONE);
            }

            public void onTouchDownForBeauty(float x, float y) {
                Log.d(TAG, "onTouchDownForBeauty: f " + x + "f2" + y);
                BeautyDialog.this.startX = x;
                BeautyDialog.this.startY = y;
            }

            public void onTouchDragForBeauty(final float x, final float y) {
                Log.d(TAG, "onTouchDragForBeauty: f " + x + " f2 " + y);
                final TextureRenderer.Viewport renderViewport = BeautyDialog.this.glSurfaceView.getRenderViewport();

                BeautyDialog.this.glSurfaceView.lazyFlush(true, () -> {
                    Log.d(TAG, "onTouchDragForBeauty run: startX " + BeautyDialog.this.startX + " x " + x + " startY " + BeautyDialog.this.startY + " y " + y);
                    Log.d(TAG, "onTouchDragForBeauty run: width " + renderViewport.width + " height " + renderViewport.height);
                    onTouchDragForBeautyCustom(BeautyDialog.this.startX, BeautyDialog.this.startY, x, y, renderViewport.width, renderViewport.height);
                    BeautyDialog.this.startX = x;
                    BeautyDialog.this.startY = y;
                });

            }

            public void onTouchDragForBeautyCustom(float startX, float startY, float endX, float endY, float w, float h) {

                if (mDeformWrapper != null) {
                    mDeformWrapper.forwardDeform(startX, startY, endX, endY, w, h, 200.0f, 0.02f);
                }
            }
        });
        this.compare = inflate.findViewById(R.id.compare);
        this.compare.setOnTouchListener((view, motionEvent) -> BeautyDialog.onCreateViewCustom(BeautyDialog.this, view, motionEvent));
        inflate.findViewById(R.id.imgSave).setOnClickListener(view -> new SaveCurrentState(true).execute());
        inflate.findViewById(R.id.imgClose).setOnClickListener(view -> BeautyDialog.this.dismiss());
        this.photoEditorView.setImageSource(this.bitmap, () -> BeautyDialog.onCreateViewCustomSeven(BeautyDialog.this));
        if (Build.VERSION.SDK_INT > 23) {
            this.photoEditorView.post(new Runnable() {
                public final void run() {
                    BeautyDialog.onCreateViewEight(BeautyDialog.this);
                }
            });
        }
        hideAllFunction();
        return inflate;
    }

    public static boolean onCreateViewCustom(BeautyDialog beautyDialog, View view, MotionEvent motionEvent) {
        Log.d(TAG, "onCreateViewCustom: motionEvent.getActionMasked()" + motionEvent.getActionMasked());
        switch (motionEvent.getActionMasked()) {
            case 0:
                beautyDialog.photoEditorView.getGLSurfaceView().setAlpha(0.0f);
                return true;
            case 1:
                beautyDialog.photoEditorView.getGLSurfaceView().setAlpha(1.0f);
                return false;
            default:
                return true;
        }
    }

    public static void onCreateViewCustomSeven(final BeautyDialog beautyDialog) {
        Log.d(TAG, "onCreateViewCustomSeven: ");
        beautyDialog.glSurfaceView.setImageBitmap(beautyDialog.bitmap);
        beautyDialog.glSurfaceView.queueEvent(new Runnable() {
            public final void run() {
                BeautyDialog.checkNullSix(beautyDialog);
            }
        });
    }


    public static void checkNullSix(BeautyDialog beautyDialog) {
        Log.d(TAG, "checkNullSix: ");
        float width = (float) beautyDialog.bitmap.getWidth();
        float height = (float) beautyDialog.bitmap.getHeight();
        float min = Math.min(((float) beautyDialog.glSurfaceView.getRenderViewport().width) / width, ((float) beautyDialog.glSurfaceView.getRenderViewport().height) / height);
        if (min < 1.0f) {
            width *= min;
            height *= min;
        }
        beautyDialog.mDeformWrapper = CGEDeformFilterWrapper.create((int) width, (int) height, 10.0f);
        beautyDialog.mDeformWrapper.setUndoSteps(ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION);
        if (beautyDialog.mDeformWrapper != null) {
            CGEImageHandler imageHandler = beautyDialog.glSurfaceView.getImageHandler();
            imageHandler.setFilterWithAddres(beautyDialog.mDeformWrapper.getNativeAddress());
            imageHandler.processFilters();
        }
    }

    public static void onCreateViewEight(BeautyDialog beautyDialog) {
        Log.d(TAG, "onCreateViewEight: ");
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(beautyDialog.glSurfaceView.getRenderViewport().width, beautyDialog.glSurfaceView.getRenderViewport().height);
        layoutParams.addRule(13);
        beautyDialog.photoEditorView.setLayoutParams(layoutParams);
    }

    private void selectFunction(int i) {
        Log.d(TAG, "selectFunction: i " + i);
        for (int i2 = 0; i2 < this.lstRetouch.size(); i2++) {
            if (i2 == i) {
                Retouch retouch = this.lstRetouch.get(i2);
                retouch.imageView.setImageResource(retouch.drawableSelected);
                retouch.textView.setTextColor(getContext().getResources().getColor(R.color.red));
            } else {
                Retouch retouch2 = this.lstRetouch.get(i2);
                retouch2.imageView.setImageResource(retouch2.drawable);
                retouch2.textView.setTextColor(getContext().getResources().getColor(R.color.white));
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public void onDestroyView() {
        super.onDestroyView();
    }

    public void onPause() {
        super.onPause();
    }

    public void onResume() {
        super.onResume();
    }

    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(-1, -1);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(ViewCompat.MEASURED_STATE_MASK));
        }
    }

    class Retouch {
        int drawable;
        int drawableSelected;
        ImageView imageView;
        TextView textView;

        Retouch(ImageView imageView2, TextView textView2, int i, int i2) {
            this.drawable = i;
            this.drawableSelected = i2;
            this.imageView = imageView2;
            this.textView = textView2;
        }
    }

    class SaveCurrentState extends AsyncTask<Void, Void, Bitmap> {
        boolean isCloseDialog;

        SaveCurrentState() {
        }

        SaveCurrentState(boolean z) {
            this.isCloseDialog = z;
        }


        public void onPreExecute() {
            BeautyDialog.this.getDialog().getWindow().setFlags(16, 16);
            BeautyDialog.this.loadingView.setVisibility(View.VISIBLE);
        }

        public Bitmap doInBackground(Void... voidArr) {
            final Bitmap[] bitmapArr = {null};
            BeautyDialog.this.photoEditorView.saveGLSurfaceViewAsBitmap(new OnSaveBitmap() {
                public void onFailure(Exception exc) {
                }

                public void onBitmapReady(Bitmap bitmap) {
                    bitmapArr[0] = bitmap;
                }
            });
            while (bitmapArr[0] == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return bitmapArr[0];
        }


        public void onPostExecute(Bitmap bitmap) {
            BeautyDialog.this.photoEditorView.setImageSource(bitmap);
            BeautyDialog.this.loadingView.setVisibility(View.GONE);
            try {
                BeautyDialog.this.getDialog().getWindow().clearFlags(16);
            } catch (Exception unused) {
            }
            BeautyDialog.this.glSurfaceView.flush(true, () -> {

                if (mDeformWrapper != null) {
                    mDeformWrapper.restore();
                    glSurfaceView.requestRender();
                }
            });
            if (this.isCloseDialog) {
                BeautyDialog.this.onBeautySave.onBeautySave(bitmap);
                BeautyDialog.this.dismiss();
            }
        }

    }

    public void onDestroy() {
        super.onDestroy();
//        if (this.mDeformWrapper != null) {
//            this.mDeformWrapper.release(true);
//            this.mDeformWrapper = null;
//        }
//        this.glSurfaceView.release();
//        this.glSurfaceView.onPause();
    }
}
