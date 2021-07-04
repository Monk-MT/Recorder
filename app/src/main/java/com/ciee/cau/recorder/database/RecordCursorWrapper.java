package com.ciee.cau.recorder.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.ciee.cau.recorder.Record;

import java.util.Date;
import java.util.UUID;

import static com.ciee.cau.recorder.database.RecordDbSchema.*;

/**
 * @author 陈明涛 Email:cmt96@foxmail.com
 * @version V1.0
 * @Description: 封装与处理查询数据库返回的Cursor
 * @Date 2021/4/28 15:32
 */
public class RecordCursorWrapper extends CursorWrapper {

    public RecordCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    /**
     * 处理Cursor
     * @return Record
     */
    public Record getRecord() {
        String uuidString = getString(getColumnIndex(RecordTable.Cols.UUID));
        String title = getString(getColumnIndex(RecordTable.Cols.TITLE));
        long date = getLong(getColumnIndex(RecordTable.Cols.DATE));
        int isSolved = getInt(getColumnIndex(RecordTable.Cols.SOLVED));
        String suspect = getString(getColumnIndex(RecordTable.Cols.SUSPECT));

        Record record = new Record(UUID.fromString(uuidString));
        record.setTitle(title);
        record.setDate(new Date(date));
        record.setSolved(isSolved != 0);
        record.setPerson(suspect);

        return record;
    }
}
