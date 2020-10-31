package com.web.mundo.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {

    public static final String DIR_PATH = "/book/chapter/{0}";
    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);

    public static void saveContentToFile(String content, String filePath) throws IOException {

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(filePath);
            fileOutputStream.write(content.getBytes());
            fileOutputStream.flush();
        } catch (IOException e) {
            LOGGER.error("储存文件出错了,filePath:{},", filePath, e);
        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
        }
    }


    public static synchronized  void chechFileDir(String filePath){
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdir();
        }
    }

}
