package com.liangxun.yuejiula.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.liangxun.yuejiula.entity.Record;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;


// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table RECORD.
*/
public class RecordDao extends AbstractDao<Record, String> {

    public static final String TABLENAME = "RECORD";

    /**
     * Properties of entity Record.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property RecordId = new Property(0, String.class, "recordId", true, "RECORD_ID");
        public final static Property RecordType = new Property(1, String.class, "recordType", false, "RECORD_TYPE");
        public final static Property RecordCont = new Property(2, String.class, "recordCont", false, "RECORD_CONT");
        public final static Property RecordPicUrl = new Property(3, String.class, "recordPicUrl", false, "RECORD_PIC_URL");
        public final static Property RecordVoice = new Property(4, String.class, "recordVoice", false, "RECORD_VOICE");
        public final static Property RecordVideo = new Property(5, String.class, "recordVideo", false, "RECORD_VIDEO");
        public final static Property RecordEmpId = new Property(6, String.class, "recordEmpId", false, "RECORD_EMP_ID");
        public final static Property RecordSchoolId = new Property(7, String.class, "recordSchoolId", false, "RECORD_SCHOOL_ID");
        public final static Property RecordIsDel = new Property(8, String.class, "recordIsDel", false, "RECORD_IS_DEL");
        public final static Property DateLine = new Property(9, String.class, "dateLine", false, "DATE_LINE");
        public final static Property School_record_mood_id = new Property(10, String.class, "school_record_mood_id", false, "SCHOOL_RECORD_MOOD_ID");
        public final static Property School_record_mood_name = new Property(11, String.class, "school_record_mood_name", false, "SCHOOL_RECORD_MOOD_NAME");
        public final static Property Money = new Property(12, String.class, "money", false, "MONEY");
        public final static Property Is_paimai = new Property(13, String.class, "is_paimai", false, "IS_PAIMAI");
        public final static Property EmpName = new Property(14, String.class, "empName", false, "EMP_NAME");
        public final static Property EmpCover = new Property(15, String.class, "empCover", false, "EMP_COVER");
        public final static Property LevelName = new Property(16, String.class, "levelName", false, "LEVEL_NAME");
        public final static Property PlNum = new Property(17, String.class, "plNum", false, "PL_NUM");
        public final static Property ZanNum = new Property(18, String.class, "zanNum", false, "ZAN_NUM");
        public final static Property LevelCount = new Property(19, String.class, "levelCount", false, "LEVEL_COUNT");
        public final static Property SchoolName = new Property(20, String.class, "schoolName", false, "SCHOOL_NAME");
    };

    private DaoSession daoSession;


    public RecordDao(DaoConfig config) {
        super(config);
    }
    
    public RecordDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'RECORD' (" + //
                "'RECORD_ID' TEXT PRIMARY KEY NOT NULL ," + // 0: recordId
                "'RECORD_TYPE' TEXT," + // 1: recordType
                "'RECORD_CONT' TEXT," + // 2: recordCont
                "'RECORD_PIC_URL' TEXT," + // 3: recordPicUrl
                "'RECORD_VOICE' TEXT," + // 4: recordVoice
                "'RECORD_VIDEO' TEXT," + // 5: recordVideo
                "'RECORD_EMP_ID' TEXT," + // 6: recordEmpId
                "'RECORD_SCHOOL_ID' TEXT," + // 7: recordSchoolId
                "'RECORD_IS_DEL' TEXT," + // 8: recordIsDel
                "'DATE_LINE' TEXT," + // 9: dateLine
                "'SCHOOL_RECORD_MOOD_ID' TEXT," + // 10: school_record_mood_id
                "'SCHOOL_RECORD_MOOD_NAME' TEXT," + // 11: school_record_mood_name
                "'MONEY' TEXT," + // 12: money
                "'IS_PAIMAI' TEXT," + // 13: is_paimai
                "'EMP_NAME' TEXT," + // 14: empName
                "'EMP_COVER' TEXT," + // 15: empCover
                "'LEVEL_NAME' TEXT," + // 16: levelName
                "'PL_NUM' TEXT," + // 17: plNum
                "'ZAN_NUM' TEXT," + // 18: zanNum
                "'LEVEL_COUNT' TEXT," + // 19: levelCount
                "'SCHOOL_NAME' TEXT);"); // 20: schoolName
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'RECORD'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Record entity) {
        stmt.clearBindings();
        stmt.bindString(1, entity.getRecordId());
 
        String recordType = entity.getRecordType();
        if (recordType != null) {
            stmt.bindString(2, recordType);
        }
 
        String recordCont = entity.getRecordCont();
        if (recordCont != null) {
            stmt.bindString(3, recordCont);
        }
 
        String recordPicUrl = entity.getRecordPicUrl();
        if (recordPicUrl != null) {
            stmt.bindString(4, recordPicUrl);
        }
 
        String recordVoice = entity.getRecordVoice();
        if (recordVoice != null) {
            stmt.bindString(5, recordVoice);
        }
 
        String recordVideo = entity.getRecordVideo();
        if (recordVideo != null) {
            stmt.bindString(6, recordVideo);
        }
 
        String recordEmpId = entity.getRecordEmpId();
        if (recordEmpId != null) {
            stmt.bindString(7, recordEmpId);
        }
 
        String recordSchoolId = entity.getRecordSchoolId();
        if (recordSchoolId != null) {
            stmt.bindString(8, recordSchoolId);
        }
 
        String recordIsDel = entity.getRecordIsDel();
        if (recordIsDel != null) {
            stmt.bindString(9, recordIsDel);
        }
 
        String dateLine = entity.getDateLine();
        if (dateLine != null) {
            stmt.bindString(10, dateLine);
        }
 
        String school_record_mood_id = entity.getSchool_record_mood_id();
        if (school_record_mood_id != null) {
            stmt.bindString(11, school_record_mood_id);
        }
 
        String school_record_mood_name = entity.getSchool_record_mood_name();
        if (school_record_mood_name != null) {
            stmt.bindString(12, school_record_mood_name);
        }
 
        String money = entity.getMoney();
        if (money != null) {
            stmt.bindString(13, money);
        }
 
        String is_paimai = entity.getIs_paimai();
        if (is_paimai != null) {
            stmt.bindString(14, is_paimai);
        }
 
        String empName = entity.getEmpName();
        if (empName != null) {
            stmt.bindString(15, empName);
        }
 
        String empCover = entity.getEmpCover();
        if (empCover != null) {
            stmt.bindString(16, empCover);
        }
 
        String levelName = entity.getLevelName();
        if (levelName != null) {
            stmt.bindString(17, levelName);
        }
 
        String plNum = entity.getPlNum();
        if (plNum != null) {
            stmt.bindString(18, plNum);
        }
 
        String zanNum = entity.getZanNum();
        if (zanNum != null) {
            stmt.bindString(19, zanNum);
        }
 
        String levelCount = entity.getLevelCount();
        if (levelCount != null) {
            stmt.bindString(20, levelCount);
        }
 
        String schoolName = entity.getSchoolName();
        if (schoolName != null) {
            stmt.bindString(21, schoolName);
        }
    }

    @Override
    protected void attachEntity(Record entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    /** @inheritdoc */
    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.getString(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Record readEntity(Cursor cursor, int offset) {
        Record entity = new Record( //
            cursor.getString(offset + 0), // recordId
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // recordType
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // recordCont
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // recordPicUrl
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // recordVoice
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // recordVideo
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // recordEmpId
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // recordSchoolId
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // recordIsDel
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // dateLine
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // school_record_mood_id
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // school_record_mood_name
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // money
            cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13), // is_paimai
            cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14), // empName
            cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15), // empCover
            cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16), // levelName
            cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17), // plNum
            cursor.isNull(offset + 18) ? null : cursor.getString(offset + 18), // zanNum
            cursor.isNull(offset + 19) ? null : cursor.getString(offset + 19), // levelCount
            cursor.isNull(offset + 20) ? null : cursor.getString(offset + 20) // schoolName
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Record entity, int offset) {
        entity.setRecordId(cursor.getString(offset + 0));
        entity.setRecordType(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setRecordCont(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setRecordPicUrl(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setRecordVoice(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setRecordVideo(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setRecordEmpId(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setRecordSchoolId(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setRecordIsDel(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setDateLine(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setSchool_record_mood_id(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setSchool_record_mood_name(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setMoney(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setIs_paimai(cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13));
        entity.setEmpName(cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14));
        entity.setEmpCover(cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15));
        entity.setLevelName(cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16));
        entity.setPlNum(cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17));
        entity.setZanNum(cursor.isNull(offset + 18) ? null : cursor.getString(offset + 18));
        entity.setLevelCount(cursor.isNull(offset + 19) ? null : cursor.getString(offset + 19));
        entity.setSchoolName(cursor.isNull(offset + 20) ? null : cursor.getString(offset + 20));
     }
    
    /** @inheritdoc */
    @Override
    protected String updateKeyAfterInsert(Record entity, long rowId) {
        return entity.getRecordId();
    }
    
    /** @inheritdoc */
    @Override
    public String getKey(Record entity) {
        if(entity != null) {
            return entity.getRecordId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
