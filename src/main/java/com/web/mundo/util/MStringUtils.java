package com.web.mundo.util;

import org.apache.commons.lang3.StringUtils;

public class MStringUtils {


    public static String trim(String value){
        if (StringUtils.isBlank(value)) {
            return value;
        }
        return value.trim();

    }

}
