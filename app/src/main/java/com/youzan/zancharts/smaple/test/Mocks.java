package com.youzan.zancharts.smaple.test;

import android.graphics.Color;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.youzan.zancharts.ChartItem;
import com.youzan.zancharts.Line;
import com.youzan.zancharts.PieChartItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by liangfei on 7/13/16.
 */

public class Mocks {
    public static List<ChartItem> summary() {
        return new Gson().fromJson(SUMMARY, new TypeToken<List<ChartItem>>() {
        }.getType());
    }

    public static List<Line> fanLines() {
        final Line newFanLine = new Line();
        newFanLine.color = Color.BLUE;
        newFanLine.label = "新增粉丝数";
        newFanLine.items = new ArrayList<>(10);

        final Line escapedFanLine = new Line();
        escapedFanLine.color = Color.GREEN;
        escapedFanLine.label = "跑路粉丝数";
        escapedFanLine.items = new ArrayList<>(10);

        newFanLine.items = summary().subList(0, 6);
        escapedFanLine.items = summary().subList(6, 12);

        return new ArrayList<Line>() {
            {
                add(newFanLine);
                add(escapedFanLine);
            }
        };
    }

    public static List<PieChartItem> fans() {
        return new ArrayList<PieChartItem>() {
            {
                add(new PieChartItem("one", "one", "1", Color.BLACK, "人数"));
                add(new PieChartItem("two", "two", "2", Color.RED, "人数"));
                add(new PieChartItem("three", "three", "3", Color.BLUE, "人数"));
            }
        };
    }

    private static final String SUMMARY = "[\n" +
            "{\n" +
            "\"key\": \"20160613\",\n" +
            "\"title\": \"2016-06-13\",\n" +
            "\"value\": \"841668.8\"\n" +
            "},\n" +
            "{\n" +
            "\"key\": \"20160614\",\n" +
            "\"title\": \"2016-06-14\",\n" +
            "\"value\": \"610159.45\"\n" +
            "},\n" +
            "{\n" +
            "\"key\": \"20160615\",\n" +
            "\"title\": \"2016-06-15\",\n" +
            "\"value\": \"669198.62\"\n" +
            "},\n" +
            "{\n" +
            "\"key\": \"20160616\",\n" +
            "\"title\": \"2016-06-16\",\n" +
            "\"value\": \"703032.1\"\n" +
            "},\n" +
            "{\n" +
            "\"key\": \"20160617\",\n" +
            "\"title\": \"2016-06-17\",\n" +
            "\"value\": \"1141277.35\"\n" +
            "},\n" +
            "{\n" +
            "\"key\": \"20160618\",\n" +
            "\"title\": \"2016-06-18\",\n" +
            "\"value\": \"702866.52\"\n" +
            "},\n" +
            "{\n" +
            "\"key\": \"20160619\",\n" +
            "\"title\": \"2016-06-19\",\n" +
            "\"value\": \"570508.85\"\n" +
            "},\n" +
            "{\n" +
            "\"key\": \"20160620\",\n" +
            "\"title\": \"2016-06-20\",\n" +
            "\"value\": \"507547.15\"\n" +
            "},\n" +
            "{\n" +
            "\"key\": \"20160621\",\n" +
            "\"title\": \"2016-06-21\",\n" +
            "\"value\": \"474940.3\"\n" +
            "},\n" +
            "{\n" +
            "\"key\": \"20160622\",\n" +
            "\"title\": \"2016-06-22\",\n" +
            "\"value\": \"437807.2\"\n" +
            "},\n" +
            "{\n" +
            "\"key\": \"20160623\",\n" +
            "\"title\": \"2016-06-23\",\n" +
            "\"value\": \"397390.15\"\n" +
            "},\n" +
            "{\n" +
            "\"key\": \"20160624\",\n" +
            "\"title\": \"2016-06-24\",\n" +
            "\"value\": \"780202.33\"\n" +
            "},\n" +
            "{\n" +
            "\"key\": \"20160625\",\n" +
            "\"title\": \"2016-06-25\",\n" +
            "\"value\": \"472123.7\"\n" +
            "},\n" +
            "{\n" +
            "\"key\": \"20160626\",\n" +
            "\"title\": \"2016-06-26\",\n" +
            "\"value\": \"624285.1\"\n" +
            "},\n" +
            "{\n" +
            "\"key\": \"20160627\",\n" +
            "\"title\": \"2016-06-27\",\n" +
            "\"value\": \"1107880.3\"\n" +
            "},\n" +
            "{\n" +
            "\"key\": \"20160628\",\n" +
            "\"title\": \"2016-06-28\",\n" +
            "\"value\": \"480767.9\"\n" +
            "},\n" +
            "{\n" +
            "\"key\": \"20160629\",\n" +
            "\"title\": \"2016-06-29\",\n" +
            "\"value\": \"562938.4\"\n" +
            "},\n" +
            "{\n" +
            "\"key\": \"20160630\",\n" +
            "\"title\": \"2016-06-30\",\n" +
            "\"value\": \"772194.48\"\n" +
            "},\n" +
            "{\n" +
            "\"key\": \"20160701\",\n" +
            "\"title\": \"2016-07-01\",\n" +
            "\"value\": \"886447.3\"\n" +
            "},\n" +
            "{\n" +
            "\"key\": \"20160702\",\n" +
            "\"title\": \"2016-07-02\",\n" +
            "\"value\": \"603225.95\"\n" +
            "},\n" +
            "{\n" +
            "\"key\": \"20160703\",\n" +
            "\"title\": \"2016-07-03\",\n" +
            "\"value\": \"598214.9\"\n" +
            "},\n" +
            "{\n" +
            "\"key\": \"20160704\",\n" +
            "\"title\": \"2016-07-04\",\n" +
            "\"value\": \"484821.3\"\n" +
            "},\n" +
            "{\n" +
            "\"key\": \"20160705\",\n" +
            "\"title\": \"2016-07-05\",\n" +
            "\"value\": \"526150.25\"\n" +
            "},\n" +
            "{\n" +
            "\"key\": \"20160706\",\n" +
            "\"title\": \"2016-07-06\",\n" +
            "\"value\": \"486345.9\"\n" +
            "},\n" +
            "{\n" +
            "\"key\": \"20160707\",\n" +
            "\"title\": \"2016-07-07\",\n" +
            "\"value\": \"474582.35\"\n" +
            "},\n" +
            "{\n" +
            "\"key\": \"20160708\",\n" +
            "\"title\": \"2016-07-08\",\n" +
            "\"value\": \"690130.9\"\n" +
            "},\n" +
            "{\n" +
            "\"key\": \"20160709\",\n" +
            "\"title\": \"2016-07-09\",\n" +
            "\"value\": \"527821.7\"\n" +
            "},\n" +
            "{\n" +
            "\"key\": \"20160710\",\n" +
            "\"title\": \"2016-07-10\",\n" +
            "\"value\": \"504681.55\"\n" +
            "},\n" +
            "{\n" +
            "\"key\": \"20160711\",\n" +
            "\"title\": \"2016-07-11\",\n" +
            "\"value\": \"416149.3\"\n" +
            "},\n" +
            "{\n" +
            "\"key\": \"20160712\",\n" +
            "\"title\": \"2016-07-12\",\n" +
            "\"value\": \"0.0\"\n" +
            "}\n" +
            "]";
}
