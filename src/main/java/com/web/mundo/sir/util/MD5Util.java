package com.web.mundo.sir.util;

import java.security.MessageDigest;

public class MD5Util {
    public static String getMD5(String str) {
        try {
            return bytesToHex(MessageDigest.getInstance("MD5").digest(str.getBytes("UTF-8")));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String bytesToHex(byte[] paramArrayOfByte)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      int i = 0;
      int m = paramArrayOfByte.length;
      while (i < m)
      {
        int k = paramArrayOfByte[i];
        int j = k;
        if (k < 0) {
          j = k + 256;
        }
        if (j < 16) {
          localStringBuilder.append("0");
        }
        localStringBuilder.append(Integer.toHexString(j));
        i += 1;
      }
      return localStringBuilder.toString();
    }
}