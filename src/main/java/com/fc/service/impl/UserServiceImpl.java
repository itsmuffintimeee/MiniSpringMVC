package com.fc.service.impl;

import com.fc.annotation.MyService;
import com.fc.service.UserService;

/**
 * Service层接口实现类
 *
 * @author juice
 */
@MyService("UserServiceImpl")
public class UserServiceImpl implements UserService {
    @Override
    public String getNameAndAge(String username, int age) {
        return "username：" + username + " -----> age：" + age;
    }
}
