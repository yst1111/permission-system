package com.permission.mapper;

import com.permission.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface RoleMapper {
    
    List<Role> selectRoleList(Role role);
    
    Role selectRoleById(Long id);
    
    Role selectRoleByCode(String roleCode);
    
    int insertRole(Role role);
    
    int updateRole(Role role);
    
    int deleteRoleById(Long id);
    
    int batchDeleteRoles(@Param("ids") List<Long> ids);
    
    List<Long> selectMenuIdsByRoleId(Long roleId);
    
    int insertRoleMenu(@Param("roleId") Long roleId, @Param("menuIds") List<Long> menuIds);
    
    int deleteRoleMenuByRoleId(Long roleId);
}
