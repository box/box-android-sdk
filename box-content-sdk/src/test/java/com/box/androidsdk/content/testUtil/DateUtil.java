package com.box.androidsdk.content.testUtil;


import org.junit.Assert;

import java.util.Date;

public class DateUtil {
    public static void assertSameDateSecondPrecision(Date expectedDate, Date date) {
        long expectedDateTruncated = expectedDate.getTime() / 1000;
        long dateTruncated = date.getTime() / 1000;
        Assert.assertEquals(expectedDateTruncated, dateTruncated);
    }
}
