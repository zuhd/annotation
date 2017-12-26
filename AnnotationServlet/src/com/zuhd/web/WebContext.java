package com.zuhd.web;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by dell013 on 2017/12/25.
 */
public class WebContext
{
    public static ThreadLocal<HttpServletRequest> requestHolder = new ThreadLocal<HttpServletRequest>();
    public static ThreadLocal<HttpServletResponse> responseHolder = new ThreadLocal<HttpServletResponse>();

    public HttpServletRequest getRequest()
    {
        return requestHolder.get();
    }

    public HttpSession getSession()
    {
        return getRequest().getSession();
    }

    public ServletContext getServletContext()
    {
        return getRequest().getServletContext();
    }

    public HttpServletResponse getResponse()
    {
        return responseHolder.get();
    }
}
