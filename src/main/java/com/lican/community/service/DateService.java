package com.lican.community.service;

import java.util.Date;

public interface DateService {
    void recordUV(String ip);
    long calculateUV(Date startTime, Date endTIme);
    void recordDAU(int userId);
    long calculateDAU(Date startTime, Date endTime);
}
