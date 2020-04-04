package com.programmingintelugu.mybuddies.Utilities;

import android.app.Activity;
import android.content.Context;
import android.text.format.DateFormat;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Utility {
    String TAG = "Utility";
    Context context;
    Activity activity;

    public Utility(Context context){
        this.context = context;
        activity = (Activity) context;

    }

    public String getDateTimeStamp() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(new Date());
    }
}
