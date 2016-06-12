package com.bilalbaloch.countries;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @author bilalbaloch
 */
public class NetworkManager {

    private static NetworkManager instance = null;
    private boolean connectivity = false;
    private static final boolean wifiOnly = false;
    private int mLastNetworkType=-1;
    private Context context;

    private NetworkManager(final Context c) {
        context = c;
    }

    public static final synchronized NetworkManager getInstance() {
        return instance;
    }

    public static final synchronized void create(final Context c) {
        if(instance == null) {
            instance = new NetworkManager(c);
        }
    }

    public static final boolean isInstanciated() {
        return instance != null;
    }

    public void updateNetworkReachability() {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo eventInfo = cm.getActiveNetworkInfo();
        if (eventInfo == null || eventInfo.getState() == NetworkInfo.State.DISCONNECTED) {
            connectivity = false;
        } else if (eventInfo.getState() == NetworkInfo.State.CONNECTED){
            if (wifiOnly){
                if (eventInfo.getType()==ConnectivityManager.TYPE_WIFI) {
                    connectivity = true;
                }else {
                    connectivity = false;
                }
            }else{
                int curtype = eventInfo.getType();
                if (curtype!=mLastNetworkType){
                    connectivity = false;
                }
                connectivity = true;
                mLastNetworkType=curtype;
            }
        }
    }

    public void connectivityChanged(ConnectivityManager cm, boolean noConnectivity) {
        updateNetworkReachability();
    }

    public boolean isWifiConnected() {
        return connectivity;
    }

}
