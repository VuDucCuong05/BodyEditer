package net.braincake.bodytune.controls;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {
    private static final boolean NETWORK_STATUS_CONNECTED = true;
    public static boolean NETWORK_STATUS_NOT_CONNECTED;

    public static boolean getConnectivityStatusString(Context context) {
        NetworkInfo activeNetworkInfo;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        if (connectivityManager == null || (activeNetworkInfo = connectivityManager.getActiveNetworkInfo()) == null || (activeNetworkInfo.getType() != 1 && activeNetworkInfo.getType() != 0)) {
            return NETWORK_STATUS_NOT_CONNECTED;
        }
        return NETWORK_STATUS_CONNECTED;
    }
}
