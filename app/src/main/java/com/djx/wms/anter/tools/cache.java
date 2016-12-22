package com.djx.wms.anter.tools;

/**
 * Created by gfgh on 2016/3/23.
 */
public  class cache{
    public static  cache instance = new cache();
    /// <summary>
    /// 仓库
    /// </summary>
    public  String warehouse = "warehouse";
    /// <summary>
    /// 物流公司
    /// </summary>
    public String logistics = "logistics";
    /// <summary>
    /// 供应商
    /// </summary>
    public String supplier = "supplier";
    /// <summary>
    /// 货主
    /// </summary>
    public String owner = "owner";
    /// <summary>
    /// 货品
    /// </summary>
    public String good = "good";

     /*标记缓存*/
    /// <summary>
    /// 标记_货品资料
    /// </summary>
    public String mark_good = "mark_good";
    /// <summary>
    /// 标记_销售订单
    /// </summary>
    public String mark_sel_order = "mark_sel_order";
    /// <summary>
    /// 标记_客户档案
    /// </summary>
    public String mark_client = "mark_client";
    /// <summary>
    /// 标记_供货商
    /// </summary>
    public String mark_supplier = "mark_supplier";
    /// <summary>
    /// 标记_采购订单
    /// </summary>
    public String mark_purch_order = "mark_purch_order";
    /// <summary>
    /// 标记_采购退货单
    /// </summary>
    public String mark_purch_return = "mark_purch_return";
    /// <summary>
    /// 标记_出库单
    /// </summary>
    public String mark_out = "mark_out";
    /// <summary>
    /// 标记_入库单
    /// </summary>
    public String mark_in = "mark_in";
    /// <summary>
    /// 标记_销售退货单
    /// </summary>
    public String mark_sel_return = "mark_sel_return";

    /// <summary>
    /// 品牌
    /// </summary>
    public String brand="brand";
}



