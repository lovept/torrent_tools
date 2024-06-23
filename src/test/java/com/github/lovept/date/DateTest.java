package com.github.lovept.date;

import org.junit.Test;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author lovept
 * @date 2024/5/31 15:05
 * @description TODO
 */
public class DateTest {
    @Test
    public void t1() {
        long epochSecond = Instant.now().getEpochSecond();
        System.out.println(epochSecond);
    }

    @Test
    public void t2() {
        String date = "2021-01-22 18:21:46+0800";
        // 转换为时间戳
        Instant instant = OffsetDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssZ")).toInstant();
        long epochSecond = instant.getEpochSecond();
        System.out.println(epochSecond);
    }
}
