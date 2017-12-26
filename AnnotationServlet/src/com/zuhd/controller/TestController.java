package com.zuhd.controller;

import com.zuhd.web.Controller;
import com.zuhd.web.RequestMapping;
import com.zuhd.web.View;

/**
 * Created by dell013 on 2017/12/26.
 */
@Controller
public class TestController
{
    @RequestMapping("main/index")
    public View forward1()
    {
        return new View("/index.jsp");
    }

    @RequestMapping("main/login")
    public View forward2()
    {
        return new View("/Login.jsp");
    }
}
