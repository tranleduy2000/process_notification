/*
 * Copyright (C) 2013 Baidu Inc. All rights reserved.
 */
package com.duy.notifi.statusbar.data.status;

import android.content.Context;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * 监视CPU消耗
 * 
 * @author liuxinjian
 * @since 2013-8-22
 */
public class CpuInfo {

    /** CPU信息文件的路径 */
    public static final String CPUC_INFO_PATH = "/proc/cpuinfo";
    /** 状态信息文件的路径 */
    public static final String STAT_PATH = "/proc/stat";

    /** Context */
    private Context mContext;

    private long mProcessCpu;
    private long mIdleCpu;
    private long mTotalCpu;
    
    private long mProcessCpu2;
    private long mIdleCpu2;
    private long mTotalCpu2;

    private boolean mIsInitialStatics = true;
    private SimpleDateFormat sFormatterFile = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private long mTotalMemorySize;

    private long mInitialTraffic;
    private long mLastestTraffic;
    private long mTraffic;

    private String mProcessCpuRatio = "";
    private String mTotalCpuRatio = "";

    private int mPid;
    private String mUid;

    public CpuInfo(Context context, int pid, String uid) {
        mContext = context;
        mPid = pid;
        mUid = uid;
        mTotalMemorySize = MemoryUtils.getTotalMemory();
    }

    /**
     * read the status of CPU
     * 
     * @throws FileNotFoundException
     */
    private void readCpuStat() {
        String processPid = Integer.toString(mPid);
        String cpuStatPath = "/proc/" + processPid + "/stat";
        try {
            // monitor cpu stat of certain process
            RandomAccessFile processCpuInfo = new RandomAccessFile(cpuStatPath, "r");
            String line = "";
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.setLength(0);
            while ((line = processCpuInfo.readLine()) != null) {
                stringBuffer.append(line + "\n");
            }
            String[] tok = stringBuffer.toString().split(" ");
            mProcessCpu = Long.parseLong(tok[13]) + Long.parseLong(tok[14]);
            processCpuInfo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            // monitor total and idle cpu stat of certain process
            RandomAccessFile cpuInfo = new RandomAccessFile(STAT_PATH, "r");
            String[] toks = cpuInfo.readLine().split(" ");
            mIdleCpu = Long.parseLong(toks[5]);
            mTotalCpu = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4])
                    + Long.parseLong(toks[5]) + Long.parseLong(toks[6]) + Long.parseLong(toks[7])
                    + Long.parseLong(toks[8]);
            cpuInfo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * get CPU name
     * 
     * @return CPU name
     */
    public static String getCpuName() {
        try {
            RandomAccessFile cpu_stat = new RandomAccessFile(CPUC_INFO_PATH, "r");
            String[] cpu = cpu_stat.readLine().split(":"); // cpu信息的前一段是含有processor字符串，此处替换为不显示
            cpu_stat.close();
            return cpu[1];
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * reserve used ratio of process CPU and total CPU, meanwhile collect
     * network mTraffic
     * 
     * @return network mTraffic ,used ratio of process CPU and total CPU in
     *         certain interval
     */
    public ArrayList<String> getCpuRatioInfo() {
        DecimalFormat fomart = new DecimalFormat();
        fomart.setMaximumFractionDigits(2);
        fomart.setMinimumFractionDigits(2);

        readCpuStat();
        ArrayList<String> cpuUsedRatio = new ArrayList<String>();
        try {
            Calendar cal = Calendar.getInstance();
            String mDateTime2 = sFormatterFile.format(cal.getTime().getTime() + 8 * 60 * 60 * 1000);

            if (mIsInitialStatics == true) {
                mInitialTraffic = TrafficUtils.getTrafficInfo(mUid);
                mIsInitialStatics = false;
            } else {
                mLastestTraffic = TrafficUtils.getTrafficInfo(mUid);
                if (mInitialTraffic == -1) {
                    mTraffic = -1;
                } else {
                    mTraffic = (mLastestTraffic - mInitialTraffic + 1023) / MemoryUtils.KB;
                }
                mProcessCpuRatio = fomart.format(100 * ((double) (mProcessCpu - mProcessCpu2) / (double) (mTotalCpu - mTotalCpu2)));
                mTotalCpuRatio = fomart.format(100 * ((double) ((mTotalCpu - mIdleCpu) - (mTotalCpu2 - mIdleCpu2)) / (double) (mTotalCpu - mTotalCpu2)));
                
                long pidMemory = MemoryUtils.getPidMemorySize(mPid, mContext);
                String pMemory = fomart.format((double) pidMemory / MemoryUtils.KB);
                
                long freeMemory = MemoryUtils.getFreeMemorySize(mContext);
                String fMemory = fomart.format((double) freeMemory / MemoryUtils.KB);
                
                String percent = "统计出错";
                if (mTotalMemorySize != 0) {
                    percent = fomart.format(((double) pidMemory / (double) mTotalMemorySize) * 100);
                }

                // whether certain device supports mTraffic statics
//                if (mTraffic == -1) {
//                    MonitorService.bw.write(mDateTime2 + "," + pMemory + "," + percent + "," + fMemory + "," + mProcessCpuRatio + "," + mTotalCpuRatio + "," + "本程序或本设备不支持流量统计" + "\r\n");
//                } else {
//                    MonitorService.bw.write(mDateTime2 + "," + pMemory + "," + percent + "," + fMemory + "," + mProcessCpuRatio + "," + mTotalCpuRatio + "," + mTraffic + "\r\n");
//                }
            }
            mTotalCpu2 = mTotalCpu;
            mProcessCpu2 = mProcessCpu;
            mIdleCpu2 = mIdleCpu;
            cpuUsedRatio.add(mProcessCpuRatio);
            cpuUsedRatio.add(mTotalCpuRatio);
            cpuUsedRatio.add(String.valueOf(mTraffic));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cpuUsedRatio;
    }

    // TODO coming soon
    // public String cpuinfo() {
    // String sys_info = "";
    // String s;
    // try {
    // RandomAccessFile reader_stat = new RandomAccessFile("/proc/stat",
    // "r");
    // RandomAccessFile reader_info = new RandomAccessFile(
    // "/proc/cpuinfo", "r");
    // sys_info = reader_info.readLine(); // CPU型号
    // String load_info;
    // String cpu_stat = reader_stat.readLine(); // cpu行信息
    // String cpu0_stat = reader_stat.readLine(); // cpu0
    // String cpu1_stat = reader_stat.readLine(); // cpu1
    //
    // String[] tok = cpu_stat.split(" ");
    // String[] tok1 = cpu0_stat.split(" ");
    // String[] tok2 = cpu1_stat.split(" ");
    //
    // // 判断单核
    // if (tok[2].equals(tok1[1])) {
    // long idle_s1 = Long.parseLong(tok[5]);
    // long cpu_s1 = Long.parseLong(tok[2]) + Long.parseLong(tok[3])
    // + Long.parseLong(tok[4]) + Long.parseLong(tok[6])
    // + Long.parseLong(tok[5]) + Long.parseLong(tok[7])
    // + Long.parseLong(tok[8]);
    //
    // try {
    // Thread.sleep(1000);
    //
    // } catch (Exception e) {
    // }
    //
    // reader_stat.seek(0);
    //
    // load_info = reader_stat.readLine();
    //
    // reader_stat.close();
    //
    // tok = load_info.split(" ");
    // long idle_s2 = Long.parseLong(tok[5]);
    //
    // long cpu_s2 = Long.parseLong(tok[2]) + Long.parseLong(tok[3])
    // + Long.parseLong(tok[4]) + Long.parseLong(tok[6])
    // + Long.parseLong(tok[5]) + Long.parseLong(tok[7])
    // + Long.parseLong(tok[8]);
    //
    // return "CPU使用率为："
    // + (100 * ((cpu_s2 - idle_s2) - (cpu_s1 - idle_s1)) / (cpu_s2 - cpu_s1))
    // + "%";
    //
    // }
    //
    // // 双核情况
    // else if (tok2[0].equals("cpu1")) {
    // // 双核
    // reader_stat = new RandomAccessFile("/proc/stat", "r");
    // long[] idle_d1 = null;
    // long[] cpu_d1 = null;
    // long[] idle_d2 = null;
    // long[] cpu_d2 = null;
    // idle_d1[0] = Long.parseLong(tok1[4]); // cpu0空闲时间
    // cpu_d1[0] = Long.parseLong(tok1[2]) + Long.parseLong(tok1[3])
    // + Long.parseLong(tok1[4]) + Long.parseLong(tok1[6])
    // + Long.parseLong(tok1[5]) + Long.parseLong(tok1[7])
    // + Long.parseLong(tok1[1]); // cpu0非空闲时间
    // idle_d1[1] = Long.parseLong(tok2[4]);
    // cpu_d1[1] = Long.parseLong(tok2[2]) + Long.parseLong(tok2[3])
    // + Long.parseLong(tok2[4]) + Long.parseLong(tok2[6])
    // + Long.parseLong(tok2[5]) + Long.parseLong(tok2[7])
    // + Long.parseLong(tok2[1]);
    //
    // try {
    // Thread.sleep(1000);
    //
    // } catch (Exception e) {
    // }
    //
    // reader_stat.seek(0);
    //
    // cpu_stat = reader_stat.readLine(); // cpu行信息
    // cpu0_stat = reader_stat.readLine(); // cpu0
    // cpu1_stat = reader_stat.readLine();
    //
    // tok1 = cpu0_stat.split(" ");
    // tok2 = cpu1_stat.split(" ");
    //
    // idle_d2[0] = Long.parseLong(tok1[4]); // cpu0空闲时间
    // cpu_d2[0] = Long.parseLong(tok1[2]) + Long.parseLong(tok1[3])
    // + Long.parseLong(tok1[4]) + Long.parseLong(tok1[6])
    // + Long.parseLong(tok1[5]) + Long.parseLong(tok1[7])
    // + Long.parseLong(tok1[1]); // cpu0非空闲时间
    // idle_d2[1] = Long.parseLong(tok2[4]);
    // cpu_d2[1] = Long.parseLong(tok2[2]) + Long.parseLong(tok2[3])
    // + Long.parseLong(tok2[4]) + Long.parseLong(tok2[6])
    // + Long.parseLong(tok2[5]) + Long.parseLong(tok2[7])
    // + Long.parseLong(tok2[1]);
    //
    // reader_stat.close();
    // return "CPU1使用率为："
    // + (100 * ((cpu_d2[0] - idle_d2[0]) - (cpu_d1[0] - idle_d1[0])) /
    // (cpu_d2[0] - cpu_d1[0]))
    // + "%"
    // + "\n"
    // + "CPU2使用率为："
    // + (100 * ((cpu_d2[1] - idle_d2[1]) - (cpu_d1[1] - idle_d1[1])) /
    // (cpu_d2[1] - cpu_d1[1]))
    // + "%";
    // }
    // } catch (IOException ex) {
    // Log.e(TAG, ex.getMessage());
    //
    // }
    // return "0";
    // }
}
