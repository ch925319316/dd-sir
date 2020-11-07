package com.web.mundo.util;


import com.jayway.jsonpath.JsonPath;

import java.util.LinkedHashMap;
import java.util.List;

public class JsonPathUtils {


    public static String readString(String content, String jsonPath) {
        try {
            Object val = JsonPath.read(content, jsonPath);
            if (val != null) {
                return val.toString();
            }
        } catch (Exception e) {
        }
        return null;
    }

    public static String readString(Object object, String jsonPath) {
        try {
            Object val = JsonPath.read(object, jsonPath);
            if (val != null) {
                return val.toString();
            }
        } catch (Exception e) {
        }
        return null;
    }


    public static List<LinkedHashMap> readListMap(String content, String jsonPath) {
        try {
            List<LinkedHashMap> lists  = JsonPath.read(content, jsonPath);
                return lists;
        } catch (Exception e) {
        }
        return null;
    }

    public static LinkedHashMap readMap(String content, String jsonPath) {
        try {
            LinkedHashMap map  = JsonPath.read(content, jsonPath);
                return map;
        } catch (Exception e) {
        }
        return null;
    }


}
