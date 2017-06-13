package com.zhang.znotes.bean.bmob;

import java.util.Date;

import cn.bmob.v3.BmobObject;

/**
 * Created by zz on 2017/3/24.
 */

public class NoteBmob extends BmobObject {
    private String userEmail;

    private String content;

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
