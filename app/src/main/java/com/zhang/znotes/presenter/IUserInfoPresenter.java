package com.zhang.znotes.presenter;

import android.content.Context;
import android.content.DialogInterface;

import com.zhang.znotes.base.BaseModel;
import com.zhang.znotes.base.BasePresenter;

/**
 * Created by zz on 2017/3/23.
 */

public interface IUserInfoPresenter extends BasePresenter {

    void changeHeadIcon(Context mContext,String title, DialogInterface.OnClickListener listen);

    void logOut();

    void updateHead(String path);

    void updateBg(String path);


}
