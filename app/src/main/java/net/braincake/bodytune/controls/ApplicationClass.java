package net.braincake.bodytune.controls;

import android.app.Application;
import android.graphics.Color;

public class ApplicationClass extends Application {
    public static final int[] listOfSkinColor = {Color.rgb(65, 49, 32), Color.rgb(90, 29, 29), Color.rgb(126, 79, 28), Color.rgb(147, 122, 96), Color.rgb(142, 0, 0), Color.rgb(127, 39, 0), Color.rgb(105, 47, 81), Color.rgb(156, 73, 88), Color.rgb(215, 161, 105), Color.rgb(219, 125, 69), Color.rgb(246, 189, 123), Color.rgb(242, 207, 169), Color.rgb(255, 209, 198), Color.rgb(255, 241, 226), Color.rgb(244, 246, 225), Color.rgb(255, 232, 240), Color.rgb(246, 225, 237), Color.rgb(247, 183, 183), Color.rgb(184, 157, 160), Color.rgb(196, 137, 169), Color.rgb(120, 112, 130), Color.rgb(104, 125, 159), Color.rgb(212, 222, 241), Color.rgb(225, 246, 240), Color.rgb(128, 219, 160), Color.rgb(167, 179, 140), Color.rgb(117, 118, 70), Color.rgb(189, 172, 153), Color.rgb(255, 150, 0), Color.rgb(246, 227, 123)};

    public void onCreate() {
        super.onCreate();
        new Thread(new Runnable() {

            public final void run() {
                ApplicationClass.this.lambda$onCreate$0$ApplicationClass();
            }
        }).start();
        iniFirebase();
    }

    public void lambda$onCreate$0$ApplicationClass() {
     }

    private void iniFirebase() {

    }
}
