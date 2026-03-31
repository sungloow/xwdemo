package com.citycheckin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citycheckin.common.UserContext;
import com.citycheckin.entity.SysMenu;
import com.citycheckin.entity.SysRoleMenu;
import com.citycheckin.mapper.SysMenuMapper;
import com.citycheckin.mapper.SysRoleMenuMapper;
import com.citycheckin.service.SysMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    private final SysRoleMenuMapper sysRoleMenuMapper;

    @Override
    public List<SysMenu> getMenuTree() {
        List<SysMenu> allMenus = list(new LambdaQueryWrapper<SysMenu>()
                .orderByAsc(SysMenu::getSortOrder));
        return buildTree(allMenus, 0);
    }

    @Override
    public List<SysMenu> getMenuTreeByUserId(Integer userId) {
        List<SysMenu> menus;
        if (UserContext.isSuperAdmin()) {
            menus = list(new LambdaQueryWrapper<SysMenu>()
                    .eq(SysMenu::getIsShow, 1)
                    .orderByAsc(SysMenu::getSortOrder));
        } else {
            menus = baseMapper.selectMenusByUserId(userId);
            menus = menus.stream().filter(m -> Integer.valueOf(1).equals(m.getIsShow())).collect(Collectors.toList());
        }
        return buildTree(menus, 0);
    }

    private List<SysMenu> buildTree(List<SysMenu> all, Integer pid) {
        List<SysMenu> tree = new ArrayList<>();
        for (SysMenu menu : all) {
            if (pid.equals(menu.getPid())) {
                menu.setChildren(buildTree(all, menu.getId()));
                tree.add(menu);
            }
        }
        return tree;
    }

    @Override
    public void addMenu(SysMenu menu) {
        menu.setCreator(UserContext.getUsername());
        menu.setCreateTime(LocalDateTime.now());
        menu.setUpdateTime(LocalDateTime.now());
        save(menu);
    }

    @Override
    public void updateMenu(SysMenu menu) {
        menu.setUpdater(UserContext.getUsername());
        menu.setUpdateTime(LocalDateTime.now());
        updateById(menu);
    }

    @Override
    public void deleteMenu(Integer id) {
        removeById(id);
        // 同步删除子菜单及角色菜单关联
        List<SysMenu> children = list(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getPid, id));
        children.forEach(child -> deleteMenu(child.getId()));
        sysRoleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getMId, id));
    }
}
