package com.zhang.znotes.model;

import android.content.Context;
import android.content.DialogInterface;

import com.zhang.znotes.base.BaseModel;

/**
 * Created by zz on 2017/3/20.
 */

public interface IUserModel extends BaseModel {

    void register(String username, String account, String password,AsyncCallback callback);

    void login(String account, String password,AsyncCallback callback);

    void updateHead(String path, AsyncCallback callback);

    void updateBg(String path, AsyncCallback callback);

    void changeHeadIcon(Context mContext,String titile, DialogInterface.OnClickListener listen);

    void logOut( AsyncCallback callback);



}
