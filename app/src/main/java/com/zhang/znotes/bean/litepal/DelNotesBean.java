package com.zhang.znotes.bean.litepal;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by zz on 2017/3/29.
 * 笔记本删除类
 */

public class DelNotesBean extends DataSupport implements Serializable {
    private int id;
    private String time;
    private Date date;
    private String objectId;
    private String content;

    @Override
    public String toString() {
        return "DelNotesBean{" +
                "id=" + id +
                ", time='" + time + '\'' +
                ", date=" + date +
                ", objectId='" + objectId + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
