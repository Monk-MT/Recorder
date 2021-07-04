package com.ciee.cau.recorder.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import static com.ciee.cau.recorder.database.RecordDbSchema.*;


/**
 * @author 陈明涛 Email:cmt96@foxmail.com
 * @version V1.0
 * @Description: 辅助操作数据库
 * @Date 2021/4/28 14:52
 */
public class RecordBaseHelper extends SQLiteOpenHelper {
    public static final int VERSION = 1;
    public static final String DATABASE_NAME = "recordBase.db";

    public RecordBaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override // 首次创建数据库调用
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + RecordTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                RecordTable.Cols.UUID + ", " +
                RecordTable.Cols.TITLE + ", " +
                RecordTable.Cols.DATE + ", " +
                RecordTable.Cols.SOLVED + ", " +
                RecordTable.Cols.SUSPECT + ")"
        );
    }

    @Override // 更新数据库
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
