package com.structure.fi.cryptoexchange.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class Util {
    public static Object deserializeXMLtoObject(String jsonSetting, Type type)
    {
        GsonBuilder builder = new GsonBuilder();
        builder.disableHtmlEscaping();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        return gson.fromJson(jsonSetting, type);
    }

    public static double findMedian(List<Double> list)
    {
        // First we sort the array
        Collections.sort(list);

        int n = list.size();

        // check for even case
        if (n % 2 != 0)
            return list.get(n / 2);

        return (list.get((n - 1) / 2) + list.get(n / 2)) / 2.0;
    }
}
