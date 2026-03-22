package com.permission.service.impl;

import com.permission.entity.Menu;
import com.permission.mapper.MenuMapper;
import com.permission.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class MenuServiceImpl implements MenuService {

    @Autowired
    private MenuMapper menuMapper;

    @Override
    public List<Menu> getMenuList(Menu menu) {
        return menuMapper.selectMenuList(menu);
    }

    @Override
    public Menu getMenuById(Long id) {
        return menuMapper.selectMenuById(id);
    }

    @Override
    public Menu getMenuByCode(String menuCode) {
        return menuMapper.selectMenuByCode(menuCode);
    }

    @Override
    public List<Menu> getMenuTree() {
        return menuMapper.selectMenuTree();
    }

    @Override
    public List<Menu> getChildrenByParentId(Long parentId) {
        return menuMapper.selectChildrenByParentId(parentId);
    }

    @Override
    public int addMenu(Menu menu) {
        return menuMapper.insertMenu(menu);
    }

    @Override
    public int updateMenu(Menu menu) {
        return menuMapper.updateMenu(menu);
    }

    @Override
    public int deleteMenu(Long id) {
        // 先删除子菜单
        List<Menu> children = menuMapper.selectChildrenByParentId(id);
        if (children != null && !children.isEmpty()) {
            for (Menu child : children) {
                deleteMenu(child.getId());
            }
        }
        return menuMapper.deleteMenuById(id);
    }

    @Override
    public int batchDeleteMenus(List<Long> ids) {
        int count = 0;
        for (Long id : ids) {
            count += deleteMenu(id);
        }
        return count;
    }
}
