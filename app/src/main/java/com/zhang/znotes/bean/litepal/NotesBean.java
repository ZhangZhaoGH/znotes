package com.zhang.znotes.bean.litepal;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.Date;

/**
 * 笔记本bean
 * Created by zz on 2017/2/26.
 */

public class NotesBean extends DataSupport implements Serializable {
    private int id;
    private String time;
    private Date date;
    private String objectId;
    private String content;

    @Override
    public String toString() {
        return "NotesBean{" +
                "id=" + id +
                ", time='" + time + '\'' +
                ", date=" + date +
                ", objectId='" + objectId + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
