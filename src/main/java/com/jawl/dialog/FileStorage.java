package com.jawl.dialog;

import android.os.Environment;

import java.io.File;
import java.util.UUID;

/**
 * Created by Fsh on 2016/12/28.
 */

public class FileStorage {

    /**
     * 创建文件
     */
    private File cropIconDir;
    private File iconDir;

    public FileStorage() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File external = Environment.getExternalStorageDirectory();
            String rootDir = "/" + "cameraFile";
            cropIconDir = new File(external, rootDir);
            if (!cropIconDir.exists()) {
                cropIconDir.mkdirs();

            }
            iconDir = new File(external, rootDir + "/icon");
            if (!iconDir.exists()) {
                iconDir.mkdirs();

            }
        }
    }

    public File createCropFile() {
        String fileName = "";
        if (cropIconDir != null) {
            fileName = UUID.randomUUID().toString() + ".png";
        }
        return new File(cropIconDir, fileName);
    }

    public File createIconFile() {
        String fileName = "";
        if (iconDir != null) {
            fileName = UUID.randomUUID().toString() + ".png";
        }
        return new File(iconDir, fileName);
    }

}
