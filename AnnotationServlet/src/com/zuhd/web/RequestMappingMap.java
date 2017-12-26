package com.zuhd.web;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dell013 on 2017/12/25.
 */
public class RequestMappingMap
{
    private static Map<String, Class<?>> requestMap = new HashMap<String, Class<?>>();

    public static Class<?> getClassName(String path)
    {
        return requestMap.get(path);
    }

    public static void put(String path, Class<?> clazz)
    {
        requestMap.put(path, clazz);
    }

    public static Map<String, Class<?>> getRequestMap()
    {
        return requestMap;
    }
}
