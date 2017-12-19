package com.zuhd.web;

import javax.servlet.annotation.WebInitParam;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by dell013 on 2017/12/14.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface WebServlet
{
    String value();

    String[] urlPatterns() default{""};

    String description() default "";

    String displayName() default "";

    String name() default "";

    WebInitParam[] initParams() default{};
}
