package com.zhang.znotes.model.impl;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;

import com.zhang.znotes.MyApplication;
import com.zhang.znotes.R;
import com.zhang.znotes.base.BaseModel;
import com.zhang.znotes.bean.bmob.MyUser;
import com.zhang.znotes.model.IUserModel;
import com.zhang.znotes.utils.LogUtil;
import com.zhang.znotes.utils.ToastUtils;

import java.io.File;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by zz on 2017/3/20.
 */

public class UserModelImpl implements IUserModel {


    @Override
    public void register(String username, String account, String password, final AsyncCallback callback) {
        BmobUser userBean = new BmobUser();
        userBean.setEmail(account);
        userBean.setUsername(username);
        userBean.setPassword(password);
        userBean.signUp(new SaveListener<MyUser>() {
            @Override
            public void done(MyUser s, BmobException e) {
                if (e == null) {
                    callback.onSuccess(MyApplication.getContext().getResources().getString(R.string.register_success));
                } else {
                    callback.onError(R.string.register_error + ", " + e);
                }
            }
        });


    }

    @Override
    public void login(String account, String password, final AsyncCallback callback) {
        BmobUser user = new BmobUser();
        user.setUsername(account);
        user.setPassword(password);
        user.login(new SaveListener<MyUser>() {
            @Override
            public void done(MyUser userBean, BmobException e) {
                if (e == null) {
                    callback.onSuccess(MyApplication.getContext().getResources().getString(R.string.login_success));
                    //通过BmobUser user = BmobUser.getCurrentUser()获取登录成功后的本地用户信息
                    //如果是自定义用户对象MyUser，可通过MyUser user = BmobUser.getCurrentUser(MyUser.class)获取自定义用户信息
                } else {
                    callback.onError(R.string.login_error + ", " + e);
                    LogUtil.e("login", " " + e);
                }
            }
        });

    }

    @Override
    public void updateHead(String path, final AsyncCallback callback) {
        MyUser user = BmobUser.getCurrentUser(MyUser.class);
        MyUser newUser = new MyUser();
        newUser.setHeadImg(path);
        newUser.update(user.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    callback.onSuccess(MyApplication.getContext().getResources().getString(R.string.update_success));
                } else {
                    callback.onError(MyApplication.getContext().getResources().getString(R.string.update_error) + " :" + e.getMessage());
                    LogUtil.e("更新失败:" + e.getMessage());
                }
            }
        });
    }

    @Override
    public void updateBg(String path, final AsyncCallback callback) {
        MyUser user = BmobUser.getCurrentUser(MyUser.class);
        MyUser newUser = new MyUser();
        newUser.setBgImg(path);
        newUser.update(user.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    callback.onSuccess(MyApplication.getContext().getResources().getString(R.string.update_success));
                } else {
                    callback.onError(MyApplication.getContext().getResources().getString(R.string.update_error) + " :" + e.getMessage());
                    LogUtil.e("更新失败:" + e.getMessage());
                }
            }
        });
    }

    @Override
    public void changeHeadIcon(final Context mContext, String title, DialogInterface.OnClickListener listen) {
        CharSequence[] items = {MyApplication.getContext().getResources().getString(R.string.album), MyApplication.getContext().getResources().getString(R.string.take_pictures)};
        AlertDialog dlg = new AlertDialog.Builder(mContext)
                .setTitle(title)
                .setItems(items, listen).create();
        dlg.show();
    }

    @Override
    public void logOut(AsyncCallback callback) {
        BmobUser.logOut();
        callback.onSuccess(MyApplication.getContext().getResources().getString(R.string.log_out_success));
    }


}
