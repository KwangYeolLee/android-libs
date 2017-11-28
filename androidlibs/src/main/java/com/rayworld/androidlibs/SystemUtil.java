package com.rayworld.androidlibs;

import android.content.Context;
import android.telephony.TelephonyManager;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * Created by 이광열 on 2016-09-23.
 */

public class SystemUtil {

    public static String getLineNumber(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        return telephonyManager.getLine1Number();
    }
}
