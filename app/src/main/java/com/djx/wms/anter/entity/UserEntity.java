package com.djx.wms.anter.entity;

import com.djx.wms.anter.entity.RoleInfor;

/**
 * Created by pc on 2016/3/10.
 */


public class UserEntity
{
    public int UserID;
    public String UserName;
    public String LoginName;
    public String UserPass;
    public Boolean isUse;
    public String lastTime;
    public String ClientID;
    public static int loginState;
    public String EnterID;
    public String EntCode;
    public String WarehouseIDs;
    public String Roles;

    public RoleInfor roleInfor;
    public UserEntity userEntity = null;

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public String getUserName() {
        return UserName;
    }

    public String WarehouseIDs() {
        return WarehouseIDs;
    }



    public void setUserName(String userName) {
        UserName = userName;
    }

    public void setLoginName(String loginName) { LoginName = loginName;}


    public String getUserPass() {
        return UserPass;
    }

    public void setUserPass(String userPass) {
        UserPass = userPass;
    }

    public Boolean getIsUse() {
        return isUse;
    }

    public void setIsUse(Boolean isUse) {
        this.isUse = isUse;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public String getClientID() {
        return ClientID;
    }

    public void setClientID(String clientID) {
        ClientID = clientID;
    }

    public int getLoginState() {
        return loginState;
    }

    public void setLoginState(int loginState) {
        this.loginState = loginState;
    }

    public String getEnterID() {
        return EnterID;
    }

    public void setEnterID(String enterID) {
        EnterID = enterID;
    }

    public String getEntCode() {
        return EntCode;
    }

    public void setEntCode(String entCode) {
        EntCode = entCode;
    }

    public RoleInfor getRoleInfor() {
        return roleInfor;
    }

    public void setRoleInfor(RoleInfor roleInfor) {
        this.roleInfor = roleInfor;
    }
}