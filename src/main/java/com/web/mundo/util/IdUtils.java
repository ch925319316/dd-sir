package com.web.mundo.util;

import java.util.UUID;

public class IdUtils {



    public static String getUUID(){
        String uuid = UUID.randomUUID().toString();
        return uuid.replace("-", "");
    }


}
