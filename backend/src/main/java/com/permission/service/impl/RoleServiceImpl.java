package com.permission.service.impl;

import com.permission.entity.Role;
import com.permission.mapper.RoleMapper;
import com.permission.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public List<Role> getRoleList(Role role) {
        return roleMapper.selectRoleList(role);
    }

    @Override
    public Role getRoleById(Long id) {
        return roleMapper.selectRoleById(id);
    }

    @Override
    public Role getRoleByCode(String roleCode) {
        return roleMapper.selectRoleByCode(roleCode);
    }

    @Override
    public int addRole(Role role) {
        return roleMapper.insertRole(role);
    }

    @Override
    public int updateRole(Role role) {
        return roleMapper.updateRole(role);
    }

    @Override
    public int deleteRole(Long id) {
        return roleMapper.deleteRoleById(id);
    }

    @Override
    public int batchDeleteRoles(List<Long> ids) {
        int count = 0;
        for (Long id : ids) {
            count += roleMapper.deleteRoleById(id);
        }
        return count;
    }

    @Override
    public List<Long> getRoleMenuIds(Long roleId) {
        return roleMapper.selectMenuIdsByRoleId(roleId);
    }

    @Override
    public int assignMenus(Long roleId, List<Long> menuIds) {
        // 先删除原有菜单
        roleMapper.deleteRoleMenuByRoleId(roleId);
        // 再分配新菜单
        if (menuIds != null && !menuIds.isEmpty()) {
            return roleMapper.insertRoleMenu(roleId, menuIds);
        }
        return 1;
    }
}
