package com.bilalbaloch.countries.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import com.bilalbaloch.countries.NetworkManager;

/**
 * Created by baloch on 11/06/16.
 */

/**
 *
 * Intercept network state changes.
 *
 */
public class NetworkReceiver extends BroadcastReceiver {

    private static final String TAG = "NetworkReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {

        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        Boolean lNoConnectivity =
                intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY,false);

        if (NetworkManager.isInstanciated()) {
            NetworkManager.getInstance().connectivityChanged(cm,lNoConnectivity);
        }else
            Log.i(TAG , ">>> NetworkManager is not instanciated...");
    }

}
