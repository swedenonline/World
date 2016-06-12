package com.bilalbaloch.countries.Helper;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bilalbaloch.countries.R;

/**
 * @author bilalbaloch
 */
public class CustomToast {

    private Context context;
    private Activity activity;
    private final int duration = Toast.LENGTH_SHORT;

    public CustomToast(Activity act, Context ctx) {
        this.activity = act;
        this.context = ctx;
    }

    public void displayMessage(String msg, int duration) {
        displayCustomToast(msg.toString(), duration);
    }

    public void displayMessage(int msg, int duration) {
        displayCustomToast(Integer.toString(msg).toString(),duration);
    }

    public void displayMessage(String msg) {
        displayCustomToast(msg.toString(), duration);
    }

    public void displayMessage(int msg) {
        displayCustomToast(Integer.toString(msg).toString(), duration);
    }

    public void displayCustomToast(final String message, final int duration) {
        String tempMsg = message;
        if(tempMsg.equalsIgnoreCase("")) tempMsg = "-Empty Message-";
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast, (ViewGroup) activity.findViewById(R.id.toastRoot));

        TextView toastText = (TextView) layout.findViewById(R.id.toastMessage);
        toastText.setText(tempMsg);

        final Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER|Gravity.BOTTOM, 0, 0);
        toast.setDuration(duration);
        toast.setView(layout);
        toast.show();
    }
}
