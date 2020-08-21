package com.hotpiecraft.olxapp.util;

import android.util.Base64;

public class Base64Custom {

    public static String encodeString(String string) {
        return Base64.encodeToString(string.getBytes(), Base64.NO_WRAP).replace("(\\n|\\r)", "");
    }

    public static String decodeString(String string) {
        return Base64.decode(string, Base64.NO_WRAP).toString();
    }
}
