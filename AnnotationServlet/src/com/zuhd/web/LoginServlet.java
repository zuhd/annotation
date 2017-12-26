package com.zuhd.web;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Created by dell013 on 2017/12/19.
 */
@WebServlet(
        value="/servlet/LoginServlet",
        urlPatterns = {"/aaa/LoginServlet", "/bbb/LoginServlet"},
        initParams = {
                @WebInitParam(name="hello", value="111111"),
                @WebInitParam(name="world", value="222222")
        },
        name="LoginServlet",
        description = "Login Servlet"
)
public class LoginServlet
{
        public void loginHandle(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
        {
                String username = request.getParameter("username");
                String password = request.getParameter("password");

                if (username.equals("hello") && password.equals("000000"))
                {
                        request.getSession().setAttribute("username", username);
                        request.setAttribute("msg", "welcome: " + username);
                        request.getRequestDispatcher("/index.jsp").forward(request, response);
                }
                else
                {
                        request.setAttribute("msg", "login error!");
                        request.getRequestDispatcher("/Login.jsp").forward(request, response);
                }
        }

        public void init(Map<String, String> mapInitParam)
        {
                System.out.println("Init Param");
                System.out.println(mapInitParam.get("hello"));
                System.out.println(mapInitParam.get("world"));
        }
}
