package com.citycheckin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.citycheckin.entity.SysMenu;

import java.util.List;

public interface SysMenuService extends IService<SysMenu> {

    List<SysMenu> getMenuTree();

    List<SysMenu> getMenuTreeByUserId(Integer userId);

    void addMenu(SysMenu menu);

    void updateMenu(SysMenu menu);

    void deleteMenu(Integer id);
}
