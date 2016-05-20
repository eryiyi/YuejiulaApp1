package com.liangxun.yuejiula.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import de.greenrobot.dao.query.QueryBuilder;

import java.util.List;

/**
 * Created by zhanghl on 2015/3/13.
 */
public class DBHelper {
    private static Context mContext;
    private static DBHelper instance;
    private static DaoMaster.DevOpenHelper helper;
    private ShoppingCartDao testDao;
    private static SQLiteDatabase db;
    private static DaoMaster daoMaster;
    private DBHelper(){}
    public static DBHelper getInstance(Context context){
        if (instance == null){
            instance = new DBHelper();
            if (mContext == null){
                mContext = context;
            }
            helper = new DaoMaster.DevOpenHelper(context, "pp_lx_db", null);
            db = helper.getWritableDatabase();
            daoMaster = new DaoMaster(db);
            instance.testDao = daoMaster.newSession().getShoppingCartDao();
        }
        return instance;
    }

    /**
     * 插入数据
     * @param test
     */
    public void addShoppingToTable(ShoppingCart test){
        testDao.insert(test);
    }

    //查询是否存在该商品
    public boolean isSaved(String ID)
    {
        QueryBuilder<ShoppingCart> qb = testDao.queryBuilder();
        qb.where(ShoppingCartDao.Properties.Goods_id.eq(ID));
        qb.buildCount().count();
        return qb.buildCount().count() > 0 ? true : false;
    }
    //批量插入数据
    public void saveTestList(List<ShoppingCart> tests){
        testDao.insertOrReplaceInTx(tests);
    }

    /**
     * 查询所有的购物车
     * @return
     */
    public List<ShoppingCart> getShoppingList(){
        return testDao.loadAll();
    }

    /**
     * 插入或是更新数据
     * @param test
     * @return
     */
    public long saveShopping(ShoppingCart test){
        return testDao.insertOrReplace(test);
    }

    /**
     * 更新数据
     * @param test
     */
    public void updateTest(ShoppingCart test){
        testDao.update(test);
    }

//    /**
//     * 获得所有收藏的题
//     * @return
//     */
//    public List<ShoppingCart> getFavourTest(){
//        QueryBuilder qb = testDao.queryBuilder();
//        qb.where(ShoppingCartDao.Properties.IsFavor.eq(true));
//        return qb.list();
//    }

    /**
     * 删除所有数据--购物车
     * */

    public void deleteShopping(){
        testDao.deleteAll();
    }
    /**
     * 删除数据根据goods_id
     * */

    public void deleteShoppingByGoodsId(String cartid){
        QueryBuilder qb = testDao.queryBuilder();
        qb.where(ShoppingCartDao.Properties.Cartid.eq(cartid));
        testDao.deleteByKey(cartid);//删除
    }
}
