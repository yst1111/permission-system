package com.permission.service;

import com.permission.entity.Role;
import java.util.List;

public interface RoleService {
    
    List<Role> getRoleList(Role role);
    
    Role getRoleById(Long id);
    
    Role getRoleByCode(String roleCode);
    
    int addRole(Role role);
    
    int updateRole(Role role);
    
    int deleteRole(Long id);
    
    int batchDeleteRoles(List<Long> ids);
    
    List<Long> getRoleMenuIds(Long roleId);
    
    int assignMenus(Long roleId, List<Long> menuIds);
}
