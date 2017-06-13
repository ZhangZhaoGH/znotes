package com.zhang.znotes.presenter.impl;

import android.content.Context;

import com.zhang.znotes.base.BaseModel;
import com.zhang.znotes.model.impl.UserModelImpl;
import com.zhang.znotes.presenter.IRegisterPresenter;
import com.zhang.znotes.view.IRegisterView;

/**
 * Created by Administrator on 2017/3/20.
 */

public class RegisterPresenterImpl implements IRegisterPresenter {
    private UserModelImpl model;
    private IRegisterView registerView;

    public RegisterPresenterImpl(Context context,IRegisterView registerView) {
        this.registerView = registerView;
        model=new UserModelImpl();
    }

    @Override
    public void onRegister(String username,String account, String password) {
        registerView.showLoading();
        model.register(username,account, password, new BaseModel.AsyncCallback() {
            @Override
            public void onSuccess(Object success) {
                String result= (String) success;
                registerView.registSuccess(result);
                registerView.hideLoading();
            }

            @Override
            public void onError(Object error) {
                String result= (String) error;
                registerView.registError(result);
                registerView.hideLoading();
            }
        });

    }

    @Override
    public void onDestroy() {
        registerView=null;
    }
}
