package com.imvlabs.hbey.Helper;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import io.realm.RealmConfiguration;

/**
 * A static class for helping tools
 */
public class Utilities {
    public static String convertDateToString(Date date){
        return new SimpleDateFormat("MM/dd/yyyy", Locale.US).format(date);
    }

    public static String getNormalStatus(boolean isMale, double hbLevel){
        if (String.valueOf(hbLevel).equals("NaN")){
            return "NaN";
        } else return ((isMale && (hbLevel>=14 && hbLevel<=18)) || (!isMale && (hbLevel>=12 && hbLevel<=16))) ? "Normal":"Anemia";
    }

    public static String formatRawHbLevel(double hbLevel) {
        return new DecimalFormat("##.###").format(hbLevel);
    }

    public static RealmConfiguration getRealmConfig(Context context) {
        return new RealmConfiguration.Builder()
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build();
    }

    static String preference = "setting";
    static String sharingAllowedKey = "sharing";
    static String historySavedKey = "history";
    public static void setUserAllowSharing(Context context, boolean isAllowed){
        SharedPreferences preferences = context.getSharedPreferences(preference, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(sharingAllowedKey, isAllowed);
        editor.apply();
    }

    public static boolean isUserAllowSharing(Context context){
        SharedPreferences preferences = context.getSharedPreferences(preference, Context.MODE_PRIVATE);
        return preferences.getBoolean(sharingAllowedKey, false);
    }

    public static void setHistoryNumber(Context context, int maxNumber){
        SharedPreferences preferences = context.getSharedPreferences(preference, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(historySavedKey, maxNumber);
        editor.apply();
    }

    public static int getHistoryNumber(Context context){
        SharedPreferences preferences = context.getSharedPreferences(preference, Context.MODE_PRIVATE);
        return preferences.getInt(historySavedKey, 50);
    }
}
