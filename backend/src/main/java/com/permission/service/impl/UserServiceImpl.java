package com.permission.service.impl;

import com.permission.entity.User;
import com.permission.mapper.UserMapper;
import com.permission.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.List;

/**
 * 用户服务实现类
 * 
 * @author system
 * @since 2024-01-01
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<User> getUserList(User user) {
        return userMapper.selectUserList(user);
    }

    @Override
    public User getUserById(Long id) {
        return userMapper.selectUserById(id);
    }

    @Override
    public User getUserByUsername(String username) {
        return userMapper.selectUserByUsername(username);
    }

    @Override
    public int addUser(User user) {
        // 加密密码
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        }
        return userMapper.insertUser(user);
    }

    @Override
    public int updateUser(User user) {
        // 如果密码不为空，则加密密码
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        }
        return userMapper.updateUser(user);
    }

    @Override
    public int deleteUser(Long id) {
        return userMapper.deleteUserById(id);
    }

    @Override
    public List<Long> getUserRoleIds(Long userId) {
        return userMapper.selectUserRoleIds(userId);
    }

    @Override
    public int resetPassword(Long userId) {
        // 重置为默认密码：123456
        String defaultPassword = DigestUtils.md5DigestAsHex("123456".getBytes());
        return userMapper.updatePassword(userId, defaultPassword);
    }

    @Override
    public int batchDeleteUsers(List<Long> userIds) {
        int count = 0;
        for (Long userId : userIds) {
            count += userMapper.deleteUserById(userId);
        }
        return count;
    }
} 