package com.web.mundo.sir.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class EncryptManager {
    private static final String TAG = "EncryptManager";
    private static volatile EncryptManager instance;
    private String appKey = "scb37537f85scxpcm59f7e318b9epa51";
    private Cipher cipher;
    private String encryptKey;
    private boolean isDebug = false;
    private byte[][] key_iv;
    private SecretKeySpec skeySpec;
    private static String p1 = "74029765cfeaf8dd791322dfd24691b4";
    private static String p2 = "0d27dfacef1338483561a46b246bf36d";

    private EncryptManager() {
        try {
            this.cipher = Cipher.getInstance("AES/CFB/NoPadding");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            this.cipher = null;
        } catch (NoSuchPaddingException e2) {
            e2.printStackTrace();
            this.cipher = null;
        }
    }

    public static EncryptManager getInstance() {
        if (instance == null) {
            synchronized (EncryptManager.class) {
                if (instance == null) {
                    instance = new EncryptManager();
                    instance.init(p1, p2);
                }
            }
        }
        return instance;
    }

    public void init(String str, String str2) {
        this.encryptKey = str;
        this.appKey = str2;
        try {
            this.key_iv = AesCfbUtil.EVP_BytesToKey(32, 16, (byte[]) null, str.getBytes("UTF-8"), 0);
            this.skeySpec = new SecretKeySpec(this.key_iv[0], "AES");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            this.skeySpec = null;
        } catch (Exception e2) {
            e2.printStackTrace();
            this.skeySpec = null;
        }
    }

    public String encrypt(String str) {
        logInfo(str);
        if (StringUtil.isEmpty(str) || this.cipher == null || this.skeySpec == null) {
            return null;
        }
        try {
            this.cipher.init(1, this.skeySpec);
            try {
                return AesCfbUtil.byte2hex(AesCfbUtil.byteMerger(this.cipher.getIV(), this.cipher.doFinal(str.getBytes("UTF-8"))));
            } catch (BadPaddingException e) {
                e.printStackTrace();
                return null;
            } catch (IllegalBlockSizeException e2) {
                e2.printStackTrace();
                return null;
            } catch (UnsupportedEncodingException e3) {
                e3.printStackTrace();
                return null;
            }
        } catch (InvalidKeyException e4) {
            e4.printStackTrace();
            return null;
        }
    }

    public String decrypt(String str) {
        logInfo(str);
        if (StringUtil.isEmpty(str) || this.cipher == null || this.skeySpec == null) {
            return null;
        }
        byte[] hex2byte = AesCfbUtil.hex2byte(str);
        byte[] copyOfRange = Arrays.copyOfRange(hex2byte, 0, 16);
        byte[] copyOfRange2 = Arrays.copyOfRange(hex2byte, 16, hex2byte.length);
        try {
            this.cipher.init(2, this.skeySpec, new IvParameterSpec(copyOfRange));
            String str2 = new String(this.cipher.doFinal(copyOfRange2), "UTF-8");
            logInfo(str2);
            return str2;
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            return null;
        } catch (InvalidKeyException e2) {
            e2.printStackTrace();
            return null;
        } catch (BadPaddingException e3) {
            e3.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e4) {
            e4.printStackTrace();
            return null;
        } catch (IllegalBlockSizeException e5) {
            e5.printStackTrace();
            return null;
        }
    }

    public String getRequestJson(String str){
        JSONObject jSONObject = new JSONObject();
        String secondTime = TimeUtil.getSecondTime();
        String encrypt = encrypt(str);
        String md5 = getMd5(AesCfbUtil.getSHA256StrJava("data=" + encrypt + "&timestamp=" + secondTime + this.appKey));
        jSONObject.put("timestamp", secondTime);
        jSONObject.put("data", encrypt);
        jSONObject.put("sign", md5);
        return jSONObject.toString();
    }

    public String getReusltJson(String str) {
        JSONObject jSONObject = new JSONObject(str);
        if (jSONObject == null) {
            return null;
        }
        String optString = jSONObject.optString("data");
        if (StringUtil.isEmpty(optString)) {
            return null;
        }
        return decrypt(optString);
    }

    private String getMd5(String str) {
        if (StringUtil.isEmpty(str)) {
            return null;
        }
        logInfo(str);
        return MD5Util.getMD5(str);
    }

    public boolean isCanEncrypt() {
        return !StringUtils.isEmpty(this.encryptKey) && !StringUtils.isEmpty(this.appKey);
    }

    private void logInfo(String str) {
//        PrintStream printStream = System.out;
//        printStream.print(TAG + " log=" + str);
    }
}