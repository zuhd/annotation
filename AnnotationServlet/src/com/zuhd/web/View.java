package com.zuhd.web;

/**
 * Created by dell013 on 2017/12/25.
 */
public class View
{
    private String url;

    private String dispatchAction = DispatchActionConstant.FORWARD;

    public View(String url)
    {
        this.url = url;
    }

    public View(String url, String name, Object value)
    {
        this.url = url;
        ViewData data = new ViewData();
        data.put(name, value);
    }

    public View(String url, String dispatchAction, String name, Object value)
    {
        this.url = url;
        this.dispatchAction = dispatchAction;
        ViewData data = new ViewData();
        data.put(name, value);
    }

    public String getUrl()
    {
        return this.url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getDispatchAction()
    {
        return this.dispatchAction;
    }

    public void setDispatchAction(String dispatchAction)
    {
        this.dispatchAction = dispatchAction;
    }
}
