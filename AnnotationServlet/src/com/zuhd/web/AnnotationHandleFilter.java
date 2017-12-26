package com.zuhd.web;

import com.zuhd.util.ScanClassUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by dell013 on 2017/12/14.
 */
public class AnnotationHandleFilter implements Filter
{
    private ServletContext m_servletContext = null;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {
        m_servletContext = filterConfig.getServletContext();
        Map<String, Class<?> > mapClasses= new HashMap<String, Class<?> >();
        String basePackage = filterConfig.getInitParameter("basePackage");

        if (basePackage.contains(","))
        {
            String[] packageNameAddr = basePackage.split(",");
            for (String packageName : packageNameAddr)
            {
                addServletClassToServletContext(packageName, mapClasses);
            }
        }
        else
        {
            addServletClassToServletContext(basePackage, mapClasses);
        }
    }

    private void addServletClassToServletContext(String packageName, Map<String, Class<?>> mapClasses)
    {
        Set<Class<?>> setClass = ScanClassUtil.getClasses(packageName);
        for (Class<?> clazz : setClass)
        {
            if (clazz.isAnnotationPresent(WebServlet.class))
            {
                WebServlet webServlet = clazz.getAnnotation(WebServlet.class);
                String value = webServlet.value();
                if (!value.equals(""))
                {
                    mapClasses.put(value, clazz);
                }

                String[] urlPatterns = webServlet.urlPatterns();
                for (String pattern : urlPatterns)
                {
                    mapClasses.put(pattern, clazz);
                }

                m_servletContext.setAttribute("servletClassMap", mapClasses);
                System.out.println("value=" + value);
                String targetClassName = value.substring(value.lastIndexOf("/") + 1);
                System.out.println("target=" + targetClassName);
            }
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException
    {
        HttpServletRequest req = (HttpServletRequest)servletRequest;
        HttpServletResponse resp = (HttpServletResponse)servletResponse;
        Map<String, Class<?>> mapClass = (Map<String, Class<?>>) m_servletContext.getAttribute("servletClassMap");
        String contextPath = req.getContextPath();
        String uri = req.getRequestURI();

        if (uri.indexOf("!") == -1)
        {
            String reqMethod = req.getMethod();
            String requestServletName = uri.substring(contextPath.length(), uri.lastIndexOf("."));
            Class<?> clazz = mapClass.get(requestServletName);
            if (null == clazz)
            {
                System.out.println("no such class!");
                filterChain.doFilter(req, resp);
                return;
            }
            Object obj = null;

            try
            {
                obj = clazz.newInstance();
            }
            catch (InstantiationException e)
            {
                e.printStackTrace();
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }


            Method targetMethod = null;
            if (reqMethod.equalsIgnoreCase("get"))
            {
                try
                {
                    targetMethod = clazz.getDeclaredMethod("doGet", HttpServletRequest.class, HttpServletResponse.class);
                }
                catch (NoSuchMethodException e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                try
                {
                    targetMethod = clazz.getDeclaredMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
                }
                catch (NoSuchMethodException e)
                {
                    e.printStackTrace();
                }
            }

            try
            {
                targetMethod.invoke(obj, req, resp);
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
            catch (InvocationTargetException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            String requestServletName = uri.substring(contextPath.length(), uri.lastIndexOf("!"));
            String invokeMethodName = uri.substring(uri.lastIndexOf("!") + 1, uri.lastIndexOf("."));

            Class<?> clazz = mapClass.get(requestServletName);
            if (null == clazz)
            {
                filterChain.doFilter(req, resp);
                return;
            }

            Object obj = null;
            try
            {
                obj = clazz.newInstance();
            }
            catch (InstantiationException e)
            {
                e.printStackTrace();
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
            Method[] methods = clazz.getMethods();
            WebServlet annotationInstance = clazz.getAnnotation(WebServlet.class);
            WebInitParam[] initParams = annotationInstance.initParams();
            Map<String, String> mapInitParams = new HashMap<String, String>();
            for (WebInitParam param : initParams)
            {
                mapInitParams.put(param.name(), param.value());
            }

            for (Method method : methods)
            {
                Class<?> retType = method.getReturnType();
                String methodName = method.getName();

                System.out.print(Modifier.toString(method.getModifiers()));
                System.out.print(" " + retType.getName() + " " + methodName + "(");

                Class<?>[] paramType = method.getParameterTypes();
                for (int j = 0; j < paramType.length; j++)
                {
                    if (j > 0)
                    {
                        System.out.print(",");
                    }

                    System.out.print(paramType[j].getName());
                }

                System.out.print(")");

                if (method.getName().equalsIgnoreCase("init"))
                {
                    try
                    {
                        method.invoke(obj, mapInitParams);
                    }
                    catch (IllegalAccessException e)
                    {
                        e.printStackTrace();
                    }
                    catch (InvocationTargetException e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            System.out.println("invoke method name:" + invokeMethodName);

            try
            {
                Method targetMethod = clazz.getDeclaredMethod(invokeMethodName, HttpServletRequest.class, HttpServletResponse.class);
                try
                {
                    targetMethod.invoke(obj, req, resp);
                }
                catch (IllegalAccessException e)
                {
                    e.printStackTrace();
                }
                catch (InvocationTargetException e)
                {
                    e.printStackTrace();
                }
            }
            catch (NoSuchMethodException e)
            {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void destroy()
    {

    }
}
