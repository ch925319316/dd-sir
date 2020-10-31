package top.ichenhao.util;

public class TimeUtil {
    public static String getSecondTime() {
        return String.format("%010d", new Object[]{Long.valueOf(System.currentTimeMillis() / 1000)});
    }
}