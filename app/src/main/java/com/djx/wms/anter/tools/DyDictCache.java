package com.djx.wms.anter.tools;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by gfgh on 2016/3/23.
 */
public class DyDictCache {
    public static  DyDictCache instance = new DyDictCache();

    cache  caches=new  cache();

    ///this by liuhan 这儿配置sql
    public void ConfigDateSource()
    {
        ///this by liuhan 注意:select 查询2列，第一列为combox value的值，第二列为combox text的值
        //by wangxk

        //品牌
        dateSource.put(caches.brand, " select DictValue as a,DictText as b from T_Config_Dictionary as a where isDelete = 0 and TypeID = 3");
        //仓库
        dateSource.put(caches.warehouse, "select indexID as a,whName as b from t_warehouse where isDelete = 0 order by sortPos");
        //物流公司
        dateSource.put(caches.logistics, "select indexID as a,logcName as b from t_logistics order by IndexID desc");
        //供应商
        dateSource.put(caches.supplier, "select indexId as a,suppName as b from t_supplier where isDelete = 0 order by posNum");
        //货主
        dateSource.put(caches.owner, "select indexId as a,ownerName as b from t_owner where isDelete = 0 order by indexId desc");
        //货品
        dateSource.put(caches.good, "select indexId as a,goodsAlias as b from t_goods order by indexId desc");

       /* #region*/
        //货品资料
        dateSource.put(caches.mark_good, "select indexId as a, flagName as b from t_mark where typeId = 1 order by orderPos");
        //销售订单
        dateSource.put(caches.mark_sel_order, "select indexId as a, flagName as b from t_mark where typeId = 2 order by orderPos");
        //客户档案
        dateSource.put(caches.mark_client, "select indexId as a, flagName as b from t_mark where typeId = 3 order by orderPos");
        //供货商
        dateSource.put(caches.mark_supplier, "select indexId as a, flagName as b from t_mark where typeId = 4 order by orderPos");
        //采购订单
        dateSource.put(caches.mark_purch_order, "select indexId as a, flagName as b from t_mark where typeId = 5 order by orderPos");
        //采购退货单
        dateSource.put(caches.mark_purch_return, "select indexId as a, flagName as b from t_mark where typeId = 6 order by orderPos");
        //出库单
        dateSource.put(caches.mark_out, "select indexId as a, flagName as b from t_mark where typeId = 7 order by orderPos");
        //入库单
        dateSource.put(caches.mark_in, "select indexId as a, flagName as b from t_mark where typeId = 8 order by orderPos");
        //销售退货单
        dateSource.put(caches.mark_sel_return, "select indexId as a, flagName as b from t_mark where typeId = 9 order by orderPos");
       /* #endregion*/
    }

    /// <summary>
    /// 获取动态缓存
    /// </summary>
    /// <param name="key">key 在 DyDictCacheKey 里面自己定义,同时在ConfigDateSource方法定义key对应的sql</param>
    /// <returns></returns>

    public List<Hashtable> GetDyDict(String key) {
        List<Hashtable> listHas = null;

        if (!CacheHas.containsKey(key))
        {
            if (dateSource.containsKey(key))
            {
                dateSource.get(key);
                listHas = Datarequest.GetDataArrayList(dateSource.get(key));
                CacheHas.put(key, ChangeHas(listHas));
                CacheHas.size();
            }
        }
        return CacheHas.get(key);
    }



    /// <summary>
    /// 刷新动态缓存
    /// </summary>
    /// <param name="key"></param>

    public void RefDyDict(String key) {
        List<Hashtable> listHas = null;

        if(key.equals("")){
            CacheHas.clear();
            return;
        }


        if (dateSource.containsKey(key))
        {
            listHas = Datarequest.GetDataArrayList(dateSource.get(key));
            CacheHas.put(key,ChangeHas(listHas));
            /*CacheHas.get(key) = ChangeHas(listHas);*/
        }


    }

    /// <summary>
    /// 转换数据格式
    /// </summary>
    /// <param name="oldHas"></param>
    /// <returns></returns>
    private List<Hashtable> ChangeHas(List<Hashtable> oldHas) {


        List<Hashtable> result = new ArrayList<>();

        int i = 1;
        if (oldHas != null && oldHas.size() != 0) {

            for (Hashtable has:oldHas) {
                Hashtable temp = new Hashtable();
                temp.put("DictValue", has.get("a"));
                temp.put("DictText", has.get("b"));
                result.add(temp);
            }


          /*      foreach (Hashtable has in oldHas) {
                Hashtable temp = new Hashtable();
                temp.Add("DictValue", has["a"]);
                temp.Add("DictText", has["b"]);
                result.Add(temp);

            }*/
        }
        return result;
    }

    /// <summary>
    /// 保存key对应的sql
    /// sql只返回两个字段，第一字段是值，第二个字段是显示文本
    /// </summary>
   /* public Hashtable  dateSource<String, String> = null;*/


    Hashtable<String,List<Hashtable>> CacheHas;

    public  Hashtable<String,String>dateSource=new Hashtable<String,String>();

    private DyDictCache() {
        dateSource = new Hashtable<String, String>();
        CacheHas = new Hashtable<String,List<Hashtable>>();

        ConfigDateSource();
    }
}

