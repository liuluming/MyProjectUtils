package com.my51c.see51.yzxvoip;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.yzxtcp.tools.CustomLog;

/**
 * @author zhuqian
 * @Title AbsDBManager
 * @Description 抽象的DBManager
 * @Company yunzhixun
 * @date 2015-12-2 下午2:29:11
 */
public abstract class AbsDBManager {

    public static final String TAG = AbsDBManager.class.getSimpleName();
    private static SQLiteDatabase yzxDb;
    private static YzxDbHelper yzxDbHelper;
    private final DBObserver dbObserver = new DBObserver();

    public AbsDBManager(Context context) {
        CustomLog.v("version:" + YZXContents.getVersionCode());
        openDb(context, YZXContents.getVersionCode());
    }

    private void openDb(Context context, int versionCode) {
        if (yzxDbHelper == null) {
            yzxDbHelper = new YzxDbHelper(context, this, versionCode);
        }
        if (yzxDb == null) {
            CustomLog.v("yzxDbHelper:" + yzxDbHelper);
            yzxDb = yzxDbHelper.getWritableDatabase();
        }
    }

    private void open(boolean isReadOnly) {
        if (yzxDb == null) {
            if (isReadOnly) {
                yzxDb = yzxDbHelper.getReadableDatabase();
            } else {
                yzxDb = yzxDbHelper.getWritableDatabase();
            }
        }
    }

    public void destroy() {
        try {
            if (yzxDbHelper != null) {
                yzxDbHelper.close();
            }
            closeDb();
        } catch (Exception e) {
            CustomLog.d(e.toString());
        }
    }

    public final void reopen() {
        closeDb();
        open(false);
        CustomLog.d("----reopen this db----");
    }

    private void closeDb() {
        if (yzxDb != null) {
            yzxDb.close();
            yzxDb = null;
        }
    }

    protected final SQLiteDatabase sqliteDB() {
        open(false);
        return yzxDb;
    }

    public void addObserver(OnDbChangeListener observer) {
        dbObserver.addObserver(observer);
    }

    public void removeObserver(OnDbChangeListener observer) {
        dbObserver.removeObserver(observer);
    }

    protected void notify(String notifyId) {
        dbObserver.notify(notifyId);
    }

    protected void clear() {
        dbObserver.clear();
    }

    public static class YzxDbHelper extends SQLiteOpenHelper {

        public static final String DATABASE_NAME = "yunzhixun.db";

        public static final String TABLE_USER = "user";
        public static final String TABLE_USER_SETTINGS = "user_settings";
        public static final String TABLE_CONVERSATION_BG = "conversation_bg";
        public static final String TABLE_DRAFT_MSG = "draft_msg";
        public static final String TABLE_TEL_Lists_INFO = "tel_lists_info";
        public static final String TABLE_TEL_USER_INFO = "tel_users_info";

        private AbsDBManager dbManager;

        public YzxDbHelper(Context context, AbsDBManager dbManager, int version) {
            this(context, DATABASE_NAME, null, version, dbManager);
        }

        public YzxDbHelper(Context context, String name, CursorFactory factory,
                           int version, AbsDBManager dbManager) {
            super(context, name, factory, version);
            this.dbManager = dbManager;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            createTables(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //新的版本号来自于AndroidManifest.xml中的“versionCode”
            if (oldVersion < 7) {
                //公众号表增加公众号服务状态字段
                db.execSQL("alter table " + TABLE_USER + "  add " + UserInfoColumn._VERICODE + " TEXT");
                oldVersion++;
            }
        }

        public void createTables(SQLiteDatabase db) {
            //创建用户信息表
            createUserTable(db);
            //创建用户设置表
            createUserSettingsTable(db);
            //创建会话背景表
            createConversationBgTable(db);
            //创建草稿表
            createDraftMsgTable(db);
            // 创建电话消息表
            createTelListsInfoTable(db);
            // 创建电话用户信息表
            createTelUserInfoTable(db);
        }

        private void createDraftMsgTable(SQLiteDatabase db) {
            String sql = "CREATE TABLE IF NOT EXISTS " +
                    TABLE_DRAFT_MSG
                    + " ("
                    + DraftMsgColume._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + DraftMsgColume._ACCOUNT + " TEXT, "
                    + DraftMsgColume._TARGETID + " TEXT, "
                    + DraftMsgColume._DRAFTMSG + " TEXT"
                    + ")";
            CustomLog.d("execute createDraftMsgTable sql = " + sql);
            db.execSQL(sql);
        }

        private void createConversationBgTable(SQLiteDatabase db) {
            String sql = "CREATE TABLE IF NOT EXISTS " +
                    TABLE_CONVERSATION_BG
                    + " ("
                    + ConversationBgColume._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + ConversationBgColume._ACCOUNT + " TEXT, "
                    + ConversationBgColume._TARGETID + " TEXT, "
                    + ConversationBgColume._BGPATH + " TEXT"
                    + ")";
            CustomLog.d("execute createConversationBgTable sql = " + sql);
            db.execSQL(sql);
        }

        private void createUserSettingsTable(SQLiteDatabase db) {
            String sql = "CREATE TABLE IF NOT EXISTS " +
                    TABLE_USER_SETTINGS
                    + " ("
                    + UserSettingColume._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + UserSettingColume._ACCOUNT + " TEXT, "
                    + UserSettingColume._ASADDRESSANDPORT + " TEXT, "
                    + UserSettingColume._TCPADDRESSANDPORT + " TEXT, "
                    + UserSettingColume._TOKEN + " TEXT, "
                    + UserSettingColume._MSGNOTIFY + " INTEGER DEFAULT 1, "
                    + UserSettingColume._MSGVITOR + " INTEGER DEFAULT 1, "
                    + UserSettingColume._MSGVOICE + " INTEGER DEFAULT 1"
                    + ")";
            CustomLog.d("execute createUserSettingsTable sql = " + sql);
            db.execSQL(sql);
        }

        private void createUserTable(SQLiteDatabase db) {
            String sql = "CREATE TABLE IF NOT EXISTS " +
                    TABLE_USER
                    + " ("
                    + UserInfoColumn._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + UserInfoColumn._ACCOUNT + " TEXT, "
                    + UserInfoColumn._NAME + " TEXT, "
                    + UserInfoColumn._VERICODE + " TEXT,"
                    + UserInfoColumn._LOGINSTATUS + " INTEGER DEFAULT 1, "
                    + UserInfoColumn._DEFAULTLOGIN + " INTEGER DEFAULT 1"
                    + ")";
            CustomLog.d("execute createUserTable sql = " + sql);
            db.execSQL(sql);
        }

        /**
         * @param db
         * @return void    返回类型
         * @Description 创建电话列表信息表
         * @date 2015-12-11 上午11:34:42
         * @author xhb
         */
        private void createTelListsInfoTable(SQLiteDatabase db) {
            String sql = "CREATE TABLE IF NOT EXISTS " +
                    TABLE_TEL_Lists_INFO
                    + " ("
                    + TelListsInfoColumn._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + TelListsInfoColumn._GRAVATOR + " TEXT, "
                    + TelListsInfoColumn._NAME + " TEXT, "
                    + TelListsInfoColumn._DIALFLAG + " INTEGER, "
                    + TelListsInfoColumn._TELMODE + " TEXT, "
                    + TelListsInfoColumn._TIME + " TEXT, "
                    + TelListsInfoColumn._TELEPHONE + " TEXT, "
                    + TelListsInfoColumn._LOGINPHONE + " TEXT,"
                    + TelListsInfoColumn._ISTOP + " TEXT,"
                    + TelListsInfoColumn._TELADRESS + " TEXT"
                    + ")";
            CustomLog.d("execute create tel_lists_info sql = " + sql);
            db.execSQL(sql);
        }

        /**
         * @param db
         * @return void    返回类型
         * @Description 创建电话用户信息表
         * @date 2015-12-11 下午5:11:49
         * @author xhb
         */
        private void createTelUserInfoTable(SQLiteDatabase db) {
            String sql = "CREATE TABLE IF NOT EXISTS " +
                    TABLE_TEL_USER_INFO
                    + " ("
                    + TelUserInfoColumn._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + TelUserInfoColumn._GRAVATOR + " TEXT, "
                    + TelUserInfoColumn._NAME + " TEXT, "
                    + TelUserInfoColumn._TELEPHONE + " TEXT, "
                    + TelUserInfoColumn._DIALFLAG + " INTEGER, "
                    + TelUserInfoColumn._TIME + " TEXT, "
                    + TelUserInfoColumn._TELMODE + " INTEGER, "
                    + TelUserInfoColumn._DIALMESSAGE + " TEXT, "
                    + TelUserInfoColumn._LOGINPHONE + " TEXT, "
                    + TelListsInfoColumn._TELADRESS + " TEXT"
                    + ")";
            CustomLog.d("execute create tel_user_info sql = " + sql);
            db.execSQL(sql);
        }
    }

    /**
     * @author zhuqian
     * @Title BaseColumn
     * @Description 基础的表列字段
     * @Company yunzhixun
     * @date 2015-12-2 下午2:32:00
     */
    public static class BaseColumn {
        public static final String _ID = "_id";
    }

    /**
     * @author xhb
     * @Title TelephoneInfoColumn
     * @Description 音视频电话列表信息
     * @Company yunzhixun
     * @date 2015-12-11 下午5:06:49
     */
    public static class TelListsInfoColumn extends BaseColumn {
        /**
         * 头像
         */
        public static final String _GRAVATOR = "_gravator";
        /**
         * 昵称
         */
        public static final String _NAME = "_name";
        /**
         * 呼入呼出标志
         */
        public static final String _DIALFLAG = "_dialflag";
        /**
         * 电话方式
         */
        public static final String _TELMODE = "_telmode";
        /**
         * 时间
         */
        public static final String _TIME = "_time";
        /**
         * 电话号码
         */
        public static final String _TELEPHONE = "_telephone";
        /**
         * 当前登录的手机号码
         */
        public static final String _LOGINPHONE = "_loginphone";
        /**
         * 是否置顶
         */
        public static final String _ISTOP = "_istop";
        /**
         * 当前手机号码归属地
         */
        public static final String _TELADRESS = "_teladress";
    }

    /**
     * @author xhb
     * @Title TelephoneUserInfoColumn
     * @Description 音视频电话用户信息
     * @Company yunzhixun
     * @date 2015-12-11 下午5:08:40
     */
    public static class TelUserInfoColumn extends BaseColumn {
        /**
         * 头像
         */
        public static final String _GRAVATOR = "_gravator";
        /**
         * 昵称
         */
        public static final String _NAME = "_name";
        /**
         * 电话号码
         */
        public static final String _TELEPHONE = "_telephone";
        /**
         * 呼入呼出标志
         */
        public static final String _DIALFLAG = "_dialflag";
        /**
         * 时间
         */
        public static final String _TIME = "_time";
        /**
         * 电话方式
         */
        public static final String _TELMODE = "_telmode";
        /**
         * 呼叫信息
         */
        public static final String _DIALMESSAGE = "_dialmessage";
        /**
         * 当前登录的手机号码
         */
        public static final String _LOGINPHONE = "_loginphone";
        /**
         * 当前手机号码归属地
         */
        public static final String _TELADRESS = "_teladress";
    }

    /**
     * @author zhuqian
     * @Title UserInfoColumn
     * @Description 用户表列字段
     * @Company yunzhixun
     * @date 2015-12-2 下午2:33:28
     */
    public static class UserInfoColumn extends BaseColumn {
        public static final String _ACCOUNT = "_account";
        public static final String _NAME = "_name";
        public static final String _VERICODE = "_veriCode";
        public static final String _LOGINSTATUS = "_loginStatus";
        public static final String _DEFAULTLOGIN = "_defaultLogin";
    }

    /**
     * @author zhuqian
     * @Title UserSettingCloume
     * @Description 用户设置表列字段
     * @Company yunzhixun
     * @date 2015-12-2 下午2:51:37
     */
    public static class UserSettingColume extends BaseColumn {
        public static final String _ACCOUNT = "_account";
        public static final String _MSGNOTIFY = "_msgNotify";
        public static final String _MSGVOICE = "_msgVoice";
        public static final String _MSGVITOR = "_msgVitor";
        public static final String _TOKEN = "_token";
        public static final String _ASADDRESSANDPORT = "_asAddressAndPort";
        public static final String _TCPADDRESSANDPORT = "_tcpAddressAndPort";
    }

    /**
     * @author zhuqian
     * @Title ConversationBgCloume
     * @Description 会话背景表列字段
     * @Company yunzhixun
     * @date 2015-12-2 下午2:53:20
     */
    public static class ConversationBgColume extends BaseColumn {
        public static final String _ACCOUNT = "_account";
        public static final String _TARGETID = "_targetId";
        public static final String _BGPATH = "_bgPath";
    }

    /**
     * @author zhuqian
     * @Title DraftMsgColume
     * @Description 草稿表列字段
     * @Company yunzhixun
     * @date 2015-12-2 下午2:54:35
     */
    public static class DraftMsgColume extends BaseColumn {
        public static final String _ACCOUNT = "_account";
        public static final String _TARGETID = "_targetId";
        public static final String _DRAFTMSG = "_draftMsg";
    }
}
