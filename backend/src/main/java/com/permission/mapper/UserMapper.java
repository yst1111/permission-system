package com.permission.mapper;

import com.permission.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface UserMapper {
    List<User> selectUserList(User user);
    User selectUserById(Long id);
    User selectUserByUsername(String username);
    int insertUser(User user);
    int updateUser(User user);
    int deleteUserById(Long id);
    List<Long> selectUserRoleIds(Long userId);
    int updatePassword(@Param("userId") Long userId, @Param("password") String password);
} 