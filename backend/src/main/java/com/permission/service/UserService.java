package com.permission.service;

import com.permission.entity.User;
import java.util.List;

public interface UserService {
    List<User> getUserList(User user);
    User getUserById(Long id);
    User getUserByUsername(String username);
    int addUser(User user);
    int updateUser(User user);
    int deleteUser(Long id);
    List<Long> getUserRoleIds(Long userId);
    int resetPassword(Long userId);
    int batchDeleteUsers(List<Long> userIds);
} 