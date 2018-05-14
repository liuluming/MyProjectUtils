package com.my51c.see51.yzxvoip;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.my51c.see51.common.AppData;
import com.my51c.see51.yzxvoip.model.TelListsInfo;
import com.yzxtcp.tools.CustomLog;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xhb
 * @Title TelListsInfoDBManager
 * @Description 电话列表信息表数据库操作类
 * @Company yunzhixun
 * @date 2015-12-14 上午10:08:15
 */
public class TelListsInfoDBManager extends AbsDBManager implements IDBManager<TelListsInfo> {
    public static final String TAG = TelListsInfoDBManager.class.getSimpleName();
    private static TelListsInfoDBManager instance;

    private TelListsInfoDBManager(Context context) {
        super(context);
    }

    public static TelListsInfoDBManager getInstance() {
        if (instance == null) {
            CustomLog.v("MainAppliction:" + AppData.getInstance());
            instance = new TelListsInfoDBManager(AppData.getInstance());
        }
        return instance;
    }

    @Override
    public List<TelListsInfo> getAll() {
        List<TelListsInfo> users = new ArrayList<TelListsInfo>();
        Cursor cursor = null;
        try {
            String sql = "select * from " + YzxDbHelper.TABLE_TEL_Lists_INFO;
            cursor = getInstance().sqliteDB().rawQuery(sql, null);
            while (cursor.moveToNext()) {
                TelListsInfo user = new TelListsInfo();
                user.setId(cursor.getInt(cursor.getColumnIndex(TelListsInfoColumn._ID)));
                user.setGravator(cursor.getString(cursor.getColumnIndex(TelListsInfoColumn._GRAVATOR)));
                user.setDialFlag(cursor.getInt(cursor.getColumnIndex(TelListsInfoColumn._DIALFLAG)));
                user.setName(cursor.getString(cursor.getColumnIndex(TelListsInfoColumn._NAME)));
                user.setTelMode(cursor.getInt(cursor.getColumnIndex(TelListsInfoColumn._TELMODE)));
                user.setTime(cursor.getString(cursor.getColumnIndex(TelListsInfoColumn._TIME)));
                user.setTelephone(cursor.getString(cursor.getColumnIndex(TelListsInfoColumn._TELEPHONE)));
                user.setTelAdress(cursor.getString(cursor.getColumnIndex(TelListsInfoColumn._TELADRESS)));
                users.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return users;
    }

    public List<TelListsInfo> getAll(String loginPhone) {
        //置顶用户
        List<TelListsInfo> isTopUsers = new ArrayList<TelListsInfo>();
        //非置顶用户
        List<TelListsInfo> isNoTopUsers = new ArrayList<TelListsInfo>();
        Cursor cursor = null;
        try {
            String isTopSql = "select * from " + YzxDbHelper.TABLE_TEL_Lists_INFO + " where _loginphone=? and _istop = 1 order by " + TelListsInfoColumn._TIME + " DESC";
            cursor = getInstance().sqliteDB().rawQuery(isTopSql, new String[]{loginPhone});
            while (cursor.moveToNext()) {
                TelListsInfo user = new TelListsInfo();
                user.setId(cursor.getInt(cursor.getColumnIndex(TelListsInfoColumn._ID)));
                user.setGravator(cursor.getString(cursor.getColumnIndex(TelListsInfoColumn._GRAVATOR)));
                user.setDialFlag(cursor.getInt(cursor.getColumnIndex(TelListsInfoColumn._DIALFLAG)));
                user.setName(cursor.getString(cursor.getColumnIndex(TelListsInfoColumn._NAME)));
                user.setTelMode(cursor.getInt(cursor.getColumnIndex(TelListsInfoColumn._TELMODE)));
                user.setTime(cursor.getString(cursor.getColumnIndex(TelListsInfoColumn._TIME)));
                user.setTelephone(cursor.getString(cursor.getColumnIndex(TelListsInfoColumn._TELEPHONE)));
                user.setLoginPhone(cursor.getString(cursor.getColumnIndex(TelListsInfoColumn._LOGINPHONE)));
                user.setIsTop(cursor.getInt(cursor.getColumnIndex(TelListsInfoColumn._ISTOP)));
                user.setTelAdress(cursor.getString(cursor.getColumnIndex(TelListsInfoColumn._TELADRESS)));
                isTopUsers.add(user);
            }
            String isNoTopSql = "select * from " + YzxDbHelper.TABLE_TEL_Lists_INFO + " where _loginphone=? and _istop = 0 order by " + TelListsInfoColumn._TIME + " DESC";
            cursor = getInstance().sqliteDB().rawQuery(isNoTopSql, new String[]{loginPhone});
            while (cursor.moveToNext()) {
                TelListsInfo user = new TelListsInfo();
                user.setId(cursor.getInt(cursor.getColumnIndex(TelListsInfoColumn._ID)));
                user.setGravator(cursor.getString(cursor.getColumnIndex(TelListsInfoColumn._GRAVATOR)));
                user.setDialFlag(cursor.getInt(cursor.getColumnIndex(TelListsInfoColumn._DIALFLAG)));
                user.setName(cursor.getString(cursor.getColumnIndex(TelListsInfoColumn._NAME)));
                user.setTelMode(cursor.getInt(cursor.getColumnIndex(TelListsInfoColumn._TELMODE)));
                user.setTime(cursor.getString(cursor.getColumnIndex(TelListsInfoColumn._TIME)));
                user.setTelephone(cursor.getString(cursor.getColumnIndex(TelListsInfoColumn._TELEPHONE)));
                user.setLoginPhone(cursor.getString(cursor.getColumnIndex(TelListsInfoColumn._LOGINPHONE)));
                user.setIsTop(cursor.getInt(cursor.getColumnIndex(TelListsInfoColumn._ISTOP)));
                user.setTelAdress(cursor.getString(cursor.getColumnIndex(TelListsInfoColumn._TELADRESS)));
                isNoTopUsers.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        List<TelListsInfo> users = new ArrayList<TelListsInfo>();
        users.addAll(isTopUsers);
        users.addAll(isNoTopUsers);
        return users;
    }


    @Override
    public TelListsInfo getById(String id) {
        return null;
    }

    /**
     * 删除电话记录
     *
     * @return
     */
    public int deleteTelListInfo(TelListsInfo info) {
        return sqliteDB().delete(YzxDbHelper.TABLE_TEL_Lists_INFO, TelListsInfoColumn._ID + " = ?", new String[]{info.getId() + ""});
    }

    @Override
    public int deleteById(String telPhone) {
        int result = sqliteDB().delete(YzxDbHelper.TABLE_TEL_Lists_INFO, "_TELEPHONE=?", new String[]{telPhone});
        return result;
    }

    @Override
    public int insert(TelListsInfo t) {
        if (isTop(t.getTelephone())) {
            Log.i(TAG, "this TelListsInfo is top");
            t.setIsTop(1);
        } else {
            Log.i(TAG, "this TelListsInfo is not top");
            t.setIsTop(0);
        }
        deleteById(t.getTelephone());
        ContentValues values = null;
        int result = 0;
        try {
            values = new ContentValues();
            values.put(TelListsInfoColumn._GRAVATOR, t.getGravator());
            values.put(TelListsInfoColumn._NAME, t.getName());
            values.put(TelListsInfoColumn._TELMODE, t.getTelMode());
            values.put(TelListsInfoColumn._DIALFLAG, t.getDialFlag());
            values.put(TelListsInfoColumn._TIME, t.getTime());
            values.put(TelListsInfoColumn._TELEPHONE, t.getTelephone());
            values.put(TelListsInfoColumn._LOGINPHONE, t.getLoginPhone());
            values.put(TelListsInfoColumn._ISTOP, t.getIsTop());
            values.put(TelListsInfoColumn._TELADRESS, t.getTelAdress());
            result = (int) sqliteDB().insert(YzxDbHelper.TABLE_TEL_Lists_INFO, null, values);

            //如果记录多于100条，则删除较早的
            List<TelListsInfo> users = getAll(t.getLoginPhone());
            if (users.size() > 100) {
                int id = users.get(users.size() - 1).getId();
                sqliteDB().delete(YzxDbHelper.TABLE_TEL_Lists_INFO, TelListsInfoColumn._ID + "=?", new String[]{id + ""});
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (values != null) {
                values.clear();
                values = null;
            }
        }
        return result;
    }

    @Override
    public int update(TelListsInfo t) {
        if (t == null) {
            throw new IllegalArgumentException(
                    "update TelListsInfo t == null ? ");
        }
        ContentValues values = null;
        try {
            values = new ContentValues();
            values.put(TelListsInfoColumn._GRAVATOR, t.getGravator());
            values.put(TelListsInfoColumn._NAME, t.getName());
            values.put(TelListsInfoColumn._TELMODE, t.getTelMode());
            values.put(TelListsInfoColumn._DIALFLAG, t.getDialFlag());
            values.put(TelListsInfoColumn._TIME, t.getTime());
            values.put(TelListsInfoColumn._TELEPHONE, t.getTelephone());
            values.put(TelListsInfoColumn._LOGINPHONE, t.getLoginPhone());
            values.put(TelListsInfoColumn._ISTOP, t.getIsTop());
            values.put(TelListsInfoColumn._TELADRESS, t.getTelAdress());
            Log.i(TAG, "update TelListsInfo--------");
            return getInstance().sqliteDB().update(YzxDbHelper.TABLE_TEL_Lists_INFO,
                    values, "_loginphone = ? and _id = ?", new String[]{t.getLoginPhone(), t.getId() + ""});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 判断某个号码是否是置顶
     *
     * @param phone
     * @return
     */
    private boolean isTop(String phone) {
        Cursor cursor = null;
        try {
            String isTopSql = "select * from " + YzxDbHelper.TABLE_TEL_Lists_INFO + " where _telephone=? and _istop = 1";
            cursor = getInstance().sqliteDB().rawQuery(isTopSql, new String[]{phone});
            return cursor.getCount() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }

        return false;
    }

}
  
