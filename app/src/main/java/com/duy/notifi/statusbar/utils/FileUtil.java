package com.duy.notifi.statusbar.utils;

import android.util.Log;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by Duy on 04-Aug-17.
 */

public class FileUtil {
    private static final String TAG = "FileUtil";

    public static String readFile(String path) throws IOException {
        Log.d(TAG, "readFile() called with: path = [" + path + "]");

        RandomAccessFile randomAccessFile = new RandomAccessFile(path, "r");
        StringBuilder res = new StringBuilder();
        String line;
        while ((line = randomAccessFile.readLine()) != null) {
            res.append(line).append("\n");
        }
        randomAccessFile.close();
        return res.toString();
    }
}
