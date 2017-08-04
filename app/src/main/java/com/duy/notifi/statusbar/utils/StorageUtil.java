package com.duy.notifi.statusbar.utils;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

/**
 * Created by Duy on 04-Aug-17.
 */

public class StorageUtil {
    private static final String TAG = "StorageUtil";

    public static Storage getInternalMemory() {
        StatFs statFs = new StatFs(Environment.getDataDirectory().getAbsolutePath());
        long blockSize = statFs.getBlockSizeLong();
        long totalSize = statFs.getBlockCountLong() * blockSize;
        long availableSize = statFs.getAvailableBlocksLong() * blockSize;
        long freeSize = statFs.getFreeBlocksLong() * blockSize;
        return new Storage(totalSize, freeSize, availableSize);
    }

    public static boolean hasExternalMemory() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    public static Storage getExternalMemory() {
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long blockSize = statFs.getBlockSizeLong();
        long totalSize = statFs.getBlockCountLong() * blockSize;
        long availableSize = statFs.getAvailableBlocksLong() * blockSize;
        long freeSize = statFs.getFreeBlocksLong() * blockSize;
        return new Storage(totalSize, freeSize, availableSize);
    }

    public static class Storage {
        private long total, free, available;

        public Storage(long total, long free, long available) {
            Log.d(TAG, "Storage() called with: total = [" + total + "], free = [" + free + "], available = [" + available + "]");

            this.total = total;
            this.free = free;
            this.available = available;
        }

        public long getTotal() {
            return total;
        }

        public long getFree() {
            return free;
        }

        public long getAvailable() {
            return available;
        }

        public int getPercent() {
            float res = ((float) (total - available) / total) * 100.0f;
            Log.d(TAG, "getPercent() returned: " + res);
            return (int) res;
        }
    }
}
