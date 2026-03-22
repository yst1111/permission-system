package com.permission.mapper;

import com.permission.entity.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;
import java.util.Map;

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
    
    @Select("<script>" +
            "SELECT DISTINCT m.id, m.menu_name as menuName, m.menu_code as menuCode, m.path, m.component, m.parent_id as parentId, m.icon, m.sort_order as sortOrder " +
            "FROM sys_menu m " +
            "INNER JOIN sys_role_menu rm ON m.id = rm.menu_id " +
            "WHERE rm.role_id IN " +
            "<foreach collection='roleIds' item='roleId' open='(' separator=',' close=')'>" +
            "#{roleId}" +
            "</foreach>" +
            " AND m.status = 1 ORDER BY m.sort_order" +
            "</script>")
    List<Map<String, Object>> selectMenusByRoleIds(@Param("roleIds") List<Long> roleIds);
}
