package com.zuhd.controller;

import com.zuhd.web.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by dell013 on 2017/12/26.
 */
@Controller
public class LoginController
{
    @RequestMapping("login/handle")
    public View loginHandle()
    {
        ViewData data = new ViewData();
        HttpServletRequest request = WebContext.requestHolder.get();
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username.equals("hello") &&
                password.equals("000000"))
        {
            data.put("msg", "welcome " + username);
            return new View("/index.jsp");
        }
        else
        {
            data.put("msg", "login error");
            return new View("/Login.jsp");
        }
    }

    @RequestMapping("ajaxLogin/handle")
    public void ajaxLoginHandle()
    {
        HttpServletRequest request = WebContext.requestHolder.get();
        HttpServletResponse response = WebContext.responseHolder.get();
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username.equals("hello") &&
                password.equals("000000"))
        {
            request.getSession().setAttribute("username", username);
            try
            {
                response.getWriter().write("success!");
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
                response.getWriter().write("failed!");
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
