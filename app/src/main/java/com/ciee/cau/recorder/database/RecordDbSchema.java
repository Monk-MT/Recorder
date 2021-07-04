package com.ciee.cau.recorder.database;

/**
 * @author 陈明涛 Email:cmt96@foxmail.com
 * @version V1.0
 * @Description: 定义描述数据表元素的String常量
 * @Date 2021/4/28 14:47
 */
public class RecordDbSchema {
    public static final class RecordTable {
        public static final String NAME = "records";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String SOLVED = "solved";
            public static final String SUSPECT = "suspect";
        }
    }
}
