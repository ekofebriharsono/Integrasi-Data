package com.example.root.uasinta;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static com.example.root.uasinta.UtilPreferences.RESULT;
import static com.example.root.uasinta.UtilPreferences.USER_IN_PREF;

public class SaveSharedPreference {

    static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Set the Login Status
     * @param context
     * @param loggedIn
     */

    public static void setUserIn(Context context, String loggedIn) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(USER_IN_PREF, loggedIn);
        editor.apply();
    }

    public static void setResult(Context context, HashSet<String> result) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putStringSet(RESULT, result);
        editor.apply();
    }

    /**
     * Get the Login Status
     * @param context
     * @return boolean: login status
     */

    public static String getUserIn(Context context) {
        return getPreferences(context).getString(USER_IN_PREF, null);
    }

    public static HashSet<String> getResult(Context context) {
        return (HashSet<String>) getPreferences(context).getStringSet(RESULT, null);
    }





}
