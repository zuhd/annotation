package com.zuhd.util;

import java.lang.reflect.*;

/**
 * Created by dell013 on 2017/12/25.
 */
public class BeanUtil
{
    public static <T> T instanceClass(Class<T> clazz)
    {
        if (!clazz.isInterface())
        {
            try
            {
                return clazz.newInstance();
            }
            catch (InstantiationException e)
            {
                e.printStackTrace();
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static <T> T instanceClass(Constructor<T> ctor, Object... args) throws IllegalAccessException, InvocationTargetException, InstantiationException
    {
        makeAccessible(ctor);
        return ctor.newInstance(args);
    }

    public static Method findMethod(Class<?> clazz, String methodName, Class<?>... paramType)
    {
        try
        {
            return clazz.getMethod(methodName, paramType);
        }
        catch (NoSuchMethodException e)
        {
            e.printStackTrace();
            return findDeclaredMethod(clazz, methodName, paramType);
        }
    }

    public static Method findDeclaredMethod(Class<?> clazz, String methodName, Class<?>[] paramType)
    {
        try
        {
            return clazz.getDeclaredMethod(methodName, paramType);
        }
        catch (NoSuchMethodException e)
        {
            e.printStackTrace();
            if (clazz.getSuperclass() != null)
            {
                return findDeclaredMethod(clazz.getSuperclass(), methodName, paramType);
            }
            return null;
        }
    }

    public static Method[] findDeclaredMethods(Class<?> clazz)
    {
        return clazz.getDeclaredMethods();
    }

    public static void makeAccessible(Constructor<?> ctor)
    {
        if (Modifier.isPublic(ctor.getModifiers()) &&
                Modifier.isPublic(ctor.getDeclaringClass().getModifiers()))
        {
            return;
        }

        if (ctor.isAccessible())
        {
            return;
        }
        ctor.setAccessible(true);
    }

    public static Field[] findDeclaredFields(Class<?> clazz)
    {
        return clazz.getDeclaredFields();
    }
}
