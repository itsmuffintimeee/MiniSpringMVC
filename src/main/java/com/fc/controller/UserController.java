package com.fc.controller;

import com.alibaba.fastjson.JSONObject;
import com.fc.annotation.MyAutowired;
import com.fc.annotation.MyController;
import com.fc.annotation.MyRequestMapping;
import com.fc.annotation.MyRequestParam;
import com.fc.service.UserService;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * 控制器
 *
 * @author juice
 */
@MyController
@MyRequestMapping("user")
public class UserController {

    @MyAutowired("UserServiceImpl")
    private UserService userService;

    @MyRequestMapping("getJson")
    public void getNameAndAge(@MyRequestParam("username") String username,
                              @MyRequestParam("age") Integer age,
                              HttpServletResponse response) {
        // 声明文本输出流
        PrintWriter writer = null;
        try {
            // 调用service层对象的方法
            String nameAndAge = userService.getNameAndAge(username, age);
            // 实例化JSON转换器
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("nameAndAge", nameAndAge);
            // 实例化文本输出流
            writer = response.getWriter();
            // 转换为JSON字符串
            String json = jsonObject.toJSONString();
            // 执行写操作
            writer.print(json);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != writer) {
                // 刷新输出流
                writer.flush();
                // 关闭输出流
                writer.close();
            }
        }
    }

}
