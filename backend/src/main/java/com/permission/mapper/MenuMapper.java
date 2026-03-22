package com.permission.mapper;

import com.permission.entity.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface MenuMapper {
    
    List<Menu> selectMenuList(Menu menu);
    
    Menu selectMenuById(Long id);
    
    Menu selectMenuByCode(String menuCode);
    
    List<Menu> selectMenuTree();
    
    List<Menu> selectChildrenByParentId(Long parentId);
    
    int insertMenu(Menu menu);
    
    int updateMenu(Menu menu);
    
    int deleteMenuById(Long id);
    
    int batchDeleteMenus(@Param("ids") List<Long> ids);
}
