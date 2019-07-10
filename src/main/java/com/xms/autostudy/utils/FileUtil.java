package com.xms.autostudy.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by xumengsi on 2019-07-10 10:24
 */
public class FileUtil {

    private static final Logger log = LoggerFactory.getLogger(FileUtil.class);

    public static String file2Base64(File file) {
        Base64 b64 = new Base64();
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[(int)file.length()];
            fileInputStream.read(buffer);
            fileInputStream.close();
            return b64.encodeToString(buffer);
        } catch (Exception e) {
            log.error("文件转base64失败");
            e.printStackTrace();
        }
        return null;
    }
}
