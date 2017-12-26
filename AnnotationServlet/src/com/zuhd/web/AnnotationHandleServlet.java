package com.zuhd.web;

import com.zuhd.util.BeanUtil;
import com.zuhd.util.ScanClassUtil;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * Created by dell013 on 2017/12/25.
 */
public class AnnotationHandleServlet extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        this.execute(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        this.execute(req, resp);
    }

    private String pareRequestURI(HttpServletRequest request)
    {
        String path = request.getContextPath() + "/";
        String requestURI = request.getRequestURI();
        String midURL = requestURI.replaceFirst(path, "");
        String lastURL = midURL.substring(0, midURL.lastIndexOf("."));
        return lastURL;
    }

    private void execute(HttpServletRequest request, HttpServletResponse response)
    {
        WebContext.requestHolder.set(request);
        WebContext.responseHolder.set(response);
        String lastURL = pareRequestURI(request);
        Class<?> clazz = RequestMappingMap.getClassName(lastURL);

        Object classInstance = BeanUtil.instanceClass(clazz);
        Method[] methods = BeanUtil.findDeclaredMethods(clazz);
        Method method = null;
        for (Method m : methods)
        {
            if (m.isAnnotationPresent(RequestMapping.class))
            {
                String anoPath = m.getAnnotation(RequestMapping.class).value();
                if (anoPath != null &&
                        !"".equals(anoPath.trim()) &&
                        lastURL.equals(anoPath.trim()))
                {
                    method = m;
                    break;
                }
            }
        }

        if (method != null)
        {
            Object retObject = null;
            try
            {
                retObject = method.invoke(classInstance);
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
            catch (InvocationTargetException e)
            {
                e.printStackTrace();
            }
            if (retObject != null)
            {
                View view = (View)retObject;
                if (view.getDispatchAction().equals(DispatchActionConstant.FORWARD))
                {
                    try
                    {
                        request.getRequestDispatcher(view.getUrl()).forward(request, response);
                    }
                    catch (ServletException e)
                    {
                        e.printStackTrace();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                else if (view.getDispatchAction().equals(DispatchActionConstant.REDIRECT))
                {
                    try
                    {
                        response.sendRedirect(request.getContextPath() + view.getUrl());
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                {
                    try
                    {
                        request.getRequestDispatcher(view.getUrl()).forward(request, response);
                    }
                    catch (ServletException e)
                    {
                        e.printStackTrace();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException
    {
        super.init(config);

        String basePackage = config.getInitParameter("basePackage");
        if (basePackage.indexOf(",") > 0)
        {
            String[] packageNameArr = basePackage.split(",");
            for (String packageName : packageNameArr)
            {
                initRequestMappingMap(packageName);
            }
        }
        else
        {
            initRequestMappingMap(basePackage);
        }
    }

    private void initRequestMappingMap(String packageName)
    {
        Set<Class<?>> setClasses = ScanClassUtil.getClasses(packageName);

        for (Class<?> clazz : setClasses)
        {
            if (clazz.isAnnotationPresent(Controller.class))
            {
                Method[] methods = BeanUtil.findDeclaredMethods(clazz);

                for (Method m : methods)
                {
                    if (m.isAnnotationPresent(RequestMapping.class))
                    {
                        String anoPath = m.getAnnotation(RequestMapping.class).value();
                        if (anoPath != null && !"".equals(anoPath.trim()))
                        {
                            if (RequestMappingMap.getRequestMap().containsKey(anoPath))
                            {
                                throw new RuntimeException("anoPath exsits!");
                            }
                            RequestMappingMap.put(anoPath, clazz);
                        }
                    }
                }
            }
        }
    }
}
