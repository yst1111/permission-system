package com.permission.service;

import com.permission.entity.Menu;
import java.util.List;

public interface MenuService {
    
    List<Menu> getMenuList(Menu menu);
    
    Menu getMenuById(Long id);
    
    Menu getMenuByCode(String menuCode);
    
    List<Menu> getMenuTree();
    
    List<Menu> getChildrenByParentId(Long parentId);
    
    int addMenu(Menu menu);
    
    int updateMenu(Menu menu);
    
    int deleteMenu(Long id);
    
    int batchDeleteMenus(List<Long> ids);
}
