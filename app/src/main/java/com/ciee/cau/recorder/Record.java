package com.ciee.cau.recorder;

import java.util.Date;
import java.util.UUID;

/**
 * @author 陈明涛 Email:cmt96@foxmail.com
 * @version V1.0
 * @Description: 记录
 * @create 2021-04-02-16:24
 */
public class Record {
    /**
     * 记录的标题
     */
    private String mTitle;
    /**
     * 记录ID
     */
    private UUID mId;
    /**
     * 记录时间
     */
    private Date mDate;
    /**
     * 是否解决
     */
    private boolean mSolved;
    /**
     * 相关人员
     */
    private String mPerson;

    public Record() {
        this(UUID.randomUUID());
    }

    public Record(UUID id) {
        mId = id;
        mDate = new Date();
        mTitle = "new Record";
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public UUID getId() {
        return mId;
    }

    public void setId(UUID id) {
        mId = id;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    public String getPerson() {
        return mPerson;
    }

    public void setPerson(String person) {
        mPerson = person;
    }

    public String getPhotoFilename() {
        return "IMG_" + getId().toString() + ".jpg";
    }
}
