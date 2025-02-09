package com.fc.service;

/**
 * Service层接口
 *
 * @author juice
 */
public interface UserService {

    /**
     * 获取用户名和年龄
     *
     * @param username 用户名
     * @param age      年龄
     * @return 用户名和年龄
     */
    String getNameAndAge(String username, int age);
}
