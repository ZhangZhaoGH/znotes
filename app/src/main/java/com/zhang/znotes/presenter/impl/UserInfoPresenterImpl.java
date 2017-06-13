package com.zhang.znotes.presenter.impl;

import android.content.Context;
import android.content.DialogInterface;

import com.zhang.znotes.MyApplication;
import com.zhang.znotes.base.BaseModel;
import com.zhang.znotes.model.impl.UserModelImpl;
import com.zhang.znotes.presenter.IUserInfoPresenter;
import com.zhang.znotes.view.IUserInfoView;

/**
 * Created by zz on 2017/3/23.
 */

public class UserInfoPresenterImpl implements IUserInfoPresenter {

    private UserModelImpl model;
    private IUserInfoView infoView;

    public UserInfoPresenterImpl(IUserInfoView infoView) {
        this.infoView = infoView;
        model = new UserModelImpl();
    }

    @Override
    public void changeHeadIcon(Context mContext, String titile, DialogInterface.OnClickListener listen) {
        model.changeHeadIcon(mContext, titile, listen);
    }

    @Override
    public void logOut() {
        infoView.showLoading();
        model.logOut(new BaseModel.AsyncCallback() {
            @Override
            public void onSuccess(Object success) {
                String msg = (String) success;
                infoView.logOutSuccess(msg);
                infoView.hideLoading();
            }

            @Override
            public void onError(Object error) {
            }
        });

    }

    @Override
    public void updateHead(String path) {
        infoView.showLoading();
        model.updateHead(path, new BaseModel.AsyncCallback() {
            @Override
            public void onSuccess(Object success) {
                String msg = (String) success;
                infoView.updateSuccess(msg);
                infoView.hideLoading();
            }

            @Override
            public void onError(Object error) {
                String msg = (String) error;
                infoView.updateError(msg);
                infoView.hideLoading();
            }
        });
    }

    @Override
    public void updateBg(String path) {
        infoView.showLoading();
        model.updateBg(path, new BaseModel.AsyncCallback() {
            @Override
            public void onSuccess(Object success) {
                String msg = (String) success;
                infoView.updateBackgroundSuccess(msg);
                infoView.hideLoading();
            }

            @Override
            public void onError(Object error) {
                String msg = (String) error;
                infoView.updateBackgroundError(msg);
                infoView.hideLoading();
            }
        });
    }

    @Override
    public void onDestroy() {
        infoView = null;
    }
}
