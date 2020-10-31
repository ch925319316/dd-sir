package com.web.mundo.sir.util;

public class TimeUtil {
    public static String getSecondTime() {
        return String.format("%010d", new Object[]{Long.valueOf(System.currentTimeMillis() / 1000)});
    }
}