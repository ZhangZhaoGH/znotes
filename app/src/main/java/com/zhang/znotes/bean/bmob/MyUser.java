package com.zhang.znotes.bean.bmob;

import java.util.Date;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;

/**
 * Created by zz on 2017/3/22.
 */

public class MyUser extends BmobUser {
    /**
     * true 是男 false 是女
     */
    private Boolean sex;
    private String headImg;
    private String bgImg;
    private String userPassword;

    public String getBgImg() {
        return bgImg;
    }

    public void setBgImg(String bgImg) {
        this.bgImg = bgImg;
    }

    private Integer age;

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    @Override
    public String toString() {
        return "MyUser{" +
                "sex=" + sex +
                ", headImg='" + headImg + '\'' +
                ", age=" + age +
                '}';
    }

    public Boolean getSex() {
        return sex;
    }

    public void setSex(Boolean sex) {
        this.sex = sex;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
