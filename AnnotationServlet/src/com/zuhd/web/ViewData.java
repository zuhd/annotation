package com.zuhd.web;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by dell013 on 2017/12/25.
 */
public class ViewData
{
    private HttpServletRequest request;

    public ViewData()
    {
        initRequest();
    }

    public void initRequest()
    {
        request = WebContext.requestHolder.get();
    }

    public void put(String name, Object value)
    {
       request.setAttribute(name, value);
    }
}
