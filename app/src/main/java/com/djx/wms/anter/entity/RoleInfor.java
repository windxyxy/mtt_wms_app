package com.djx.wms.anter.entity;

import java.util.List;

/**
 * Created by pc on 2016/3/10.
 */
public class RoleInfor {
    public int RoleID;
    public String RoleName;

    public List<String> ModuleID;
    public List<String> ModuleFullName;
    public List<String> ManuModuleID;
    public List<String> ManuModuleFullName;
    public List<String> ButtonName;
    public List<String> ShopID;
    public List<String> WarehouseID;
    public Boolean isSuper;

    public int getRoleID() {
        return RoleID;
    }

    public Boolean getisSuper() {
        return isSuper;
    }

    public void setRoleID(int roleID) {
        RoleID = roleID;
    }

    public String getRoleName() {
        return RoleName;
    }

    public void setRoleName(String roleName) {
        RoleName = roleName;
    }

    public List<String> getModuleID() {
        return ModuleID;
    }

    public void setModuleID(List<String> moduleID) {
        ModuleID = moduleID;
    }

    public List<String> getModuleFullName() {
        return ModuleFullName;
    }

    public void setModuleFullName(List<String> moduleFullName) {
        ModuleFullName = moduleFullName;
    }

    public List<String> getManuModuleID() {
        return ManuModuleID;
    }

    public void setManuModuleID(List<String> manuModuleID) {
        ManuModuleID = manuModuleID;
    }

    public List<String> getManuModuleFullName() {
        return ManuModuleFullName;
    }

    public void setManuModuleFullName(List<String> manuModuleFullName) {
        ManuModuleFullName = manuModuleFullName;
    }

    public List<String> getButtonName() {
        return ButtonName;
    }

    public void setButtonName(List<String> buttonName) {
        ButtonName = buttonName;
    }

    public List<String> getShopID() {
        return ShopID;
    }

    public void setShopID(List<String> shopID) {
        ShopID = shopID;
    }

    public List<String> getWarehouseID() {
        return WarehouseID;
    }

    public void setWarehouseID(List<String> warehouseID) {
        WarehouseID = warehouseID;
    }


}
