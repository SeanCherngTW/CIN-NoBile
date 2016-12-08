package com.cin.linyuehlii.nobile;

import java.io.Serializable;

/**
 * Created by 大芳鎖定你 on 2016/7/20.
 */
public class BMission implements Serializable {
    String MissionAppName;
    long MissionAppTime;

    public BMission(String MissionAppName, long MissionAppTime) {
        this.MissionAppName = MissionAppName;
        this.MissionAppTime = MissionAppTime;
    }

    public String getMissionAppName() {
        return MissionAppName;
    }

    public void setMissionAppName(String missionAppName) {
        MissionAppName = missionAppName;
    }

    public long getMissionAppTime() {
        return MissionAppTime;
    }

    public void setMissionAppTime(long missionAppTime) {
        MissionAppTime = missionAppTime;
    }

    public String getMissionAppTimeFormatted() {
        String Minute = String.valueOf(MissionAppTime % 60000);
        String Hour = String.valueOf(MissionAppTime / 3600000);
        return Hour +" hr " + Minute + " min ";
    }
}
