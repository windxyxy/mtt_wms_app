package com.djx.wms.anter.entity;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by gfgh on 2016/3/16.
 */
public class SPEntity {
    public String getSPName() {
        return SPName;
    }

    public void setSPName(String SPName) {
        this.SPName = SPName;
    }

    public Hashtable getParamValue() {
        return ParamValue;
    }

    public void setParamValue(Hashtable paramValue) {
        ParamValue = paramValue;
    }

    public Hashtable getOutParam() {
        return OutParam;
    }

    public void setOutParam(Hashtable outParam) {
        OutParam = outParam;
    }

    public ArrayList<Hashtable> getListHt() {
        return ListHt;
    }

    public void setListHt(ArrayList<Hashtable> listHt) {
        ListHt = listHt;
    }

    /// <summary>
    /// 存储过程名
    /// *存储过程执行方法默认添加result参数，请在数据库存储过程做相应输出
    /// </summary>
    public String SPName;
    /// <summary>
    /// 所有参数（参数名-参数值）
    /// *凡是数据库存储过程有的参数，不管输入参数还是输出参数都必须出现在这里（result除外）
    /// *输出参数的值赋0，example：ParamValue.Add("example",0);
    /// </summary>
    public Hashtable ParamValue;
    /// <summary>
    /// 传出参数
    /// 数据库存储过程输出参数必须出现在这里（result除外）
    /// 添加时结构为（参数名-参数类型），example：OutParam.Add("example",SqlDbType.Int);
    /// 获取时结构为（参数名-参数值），example：OutParam["OutParam"];
    /// </summary>
    public Hashtable OutParam;
    /// <summary>
    /// 结果集
    /// </summary>
    public ArrayList<Hashtable> ListHt;
}
