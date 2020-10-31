package com.web.mundo.sir.util;

public class StringUtil {
    public static boolean isEmpty(CharSequence charSequence) {
        return charSequence == null || charSequence.length() == 0;
    }
}