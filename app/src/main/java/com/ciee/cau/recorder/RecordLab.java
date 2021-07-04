package com.ciee.cau.recorder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ciee.cau.recorder.database.RecordBaseHelper;
import com.ciee.cau.recorder.database.RecordCursorWrapper;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.ciee.cau.recorder.database.RecordDbSchema.*;

/**
 * 管理所有记录
 * @author ChenMingTao email:cmt96@foxmail.com
 * @create 2021-04-22-16:14
 */
public class RecordLab {
    private static RecordLab sRecordLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    /**
     * 静态工厂方法，获取该单例的对象
     */
    public static RecordLab get(Context context) {
        if (sRecordLab == null) {
            synchronized (RecordLab.class) {
                if (sRecordLab == null) {
                    sRecordLab = new RecordLab(context);
                }
            }
        }
        return sRecordLab;
    }

    private RecordLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new RecordBaseHelper(mContext).getWritableDatabase(); // 获取数据库
    }

    /**
     * 向数据库中添加数据
     */
    public void addRecord(Record r) {
        if (r.getId() == null) {
            r.setId(UUID.randomUUID());
        }
        if (r.getTitle() == null) {
            r.setTitle("new Record");
        }
        if (r.getDate() == null) {
            r.setDate(new Date());
        }
        ContentValues values = getContentValues(r);
        mDatabase.insert(RecordTable.NAME, null, values); // 参数：数据表名，null，要写入的数据
    }

    /**
     * 更新数据库中的数据
     */
    public void updateRecord(Record record) {
        String uuidString = record.getId().toString();
        ContentValues values = getContentValues(record);

        mDatabase.update(RecordTable.NAME, values, RecordTable.Cols.UUID + " = ?", new String[]{ uuidString}); // 参数：表名，更新数据，where语句
    }

    /**
     * 通过UUID 获取数据库中的某个Record
     * @param id 每个Record的UUID
     * @return Record
     */
    public Record getRecord(UUID id) {

        try (RecordCursorWrapper cursor = queryRecords(RecordTable.Cols.UUID + " = ?", new String[]{id.toString()})) {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getRecord();
        }
    }

    /**
     * 获取数据库中全部的Record
     * @return List<Record>
     */
    public List<Record> getRecords() {
        List<Record> records = new ArrayList<>();

        try (RecordCursorWrapper cursor = queryRecords(null, null)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                records.add(cursor.getRecord());
                cursor.moveToNext();
            }
        }

        return records;
    }

    /**
     * 从数据库获取全部Records
     * @return CursorWrapper
     */
    private RecordCursorWrapper queryRecords(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                RecordTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        ); // 查询数据库

        return new RecordCursorWrapper(cursor);
    }

    /**
     * 把Record转换为ContentValues
     */
    private static ContentValues getContentValues(Record record) {
        ContentValues values = new ContentValues(); // SQLite存数据时的专用键值对
        values.put(RecordTable.Cols.UUID, record.getId().toString());
        values.put(RecordTable.Cols.TITLE, record.getTitle());
        values.put(RecordTable.Cols.DATE, record.getDate().getTime());
        values.put(RecordTable.Cols.SOLVED, record.isSolved() ? 1 : 0);
        values.put(RecordTable.Cols.SUSPECT, record.getPerson());

        return values;
    }

    /**
     * 获取图片
     */
    public File getPhotoFile(Record record) {
        File fileDir = mContext.getFilesDir();
        return new File(fileDir, record.getPhotoFilename());
    }
}
