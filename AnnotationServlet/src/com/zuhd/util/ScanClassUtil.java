package com.zuhd.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by dell013 on 2017/12/15.
 */
public class ScanClassUtil
{
    public static Set<Class<?> > getClasses(String pack)
    {
        Set<Class<?> > setClasses = new LinkedHashSet<Class<?> >();
        boolean recursive = true;
        String packageName = pack;
        String packageDirName = packageName.replace(".", "/");
        Enumeration<URL> dirs;
        try
        {
            String path = Thread.currentThread().getContextClassLoader().getResource(packageDirName).getPath();
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            while (dirs.hasMoreElements())
            {
                URL url = dirs.nextElement();
                String protocol = url.getProtocol();
                if ("file".equals(protocol))
                {
                    System.err.println("scan .class file");
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    findAndAddClassesInPackageByFile(packageName, filePath, recursive, setClasses);
                }
                else if ("jar".equals(protocol))
                {
                    System.err.println("scan .jar file");
                    JarFile jar;
                    jar = ((JarURLConnection)(url.openConnection())).getJarFile();
                    Enumeration<JarEntry> entries = jar.entries();

                    while (entries.hasMoreElements())
                    {
                        JarEntry entry = entries.nextElement();
                        String name = entry.getName();

                        if (name.charAt(0) == '/')
                        {
                            name = name.substring(1);
                        }

                        if (name.startsWith(packageDirName))
                        {
                            int idx = name.lastIndexOf('/');
                            if (idx != -1)
                            {
                                packageName = name.substring(0, idx).replace('/', '.');

                            }

                            if ((idx != -1) || recursive)
                            {
                                if (name.endsWith(".class") && !entry.isDirectory())
                                {
                                    String className = name.substring(packageName.length() + 1, name.length() - 6);
                                    try
                                    {
                                        setClasses.add(Class.forName(packageName + '.' + className));
                                    }
                                    catch (ClassNotFoundException e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return setClasses;
    }

    public static void findAndAddClassesInPackageByFile(String packageName, String packagePath, final boolean recursive, Set<Class<?>> setClasses)
    {
        File dir = new File(packagePath);
        if (!dir.exists() || !dir.isDirectory())
        {
            System.out.println("no file exists in" + packageName);
            return;
        }

        File[] dirFiles = dir.listFiles(new FileFilter()
        {
            @Override
            public boolean accept(File pathname)
            {
                return (recursive && pathname.isDirectory()) || pathname.getName().endsWith(".class");
            }
        });

        for (File file : dirFiles)
        {
            if (file.isDirectory())
            {
                findAndAddClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive, setClasses);
            }
            else
            {
                String className = file.getName().substring(0, file.getName().length() - 6);
                try
                {
                    setClasses.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + "." + className));
                }
                catch (ClassNotFoundException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

}
