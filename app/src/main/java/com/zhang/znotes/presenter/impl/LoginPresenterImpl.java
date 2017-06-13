package com.zhang.znotes.presenter.impl;

import com.zhang.znotes.base.BaseModel;
import com.zhang.znotes.model.impl.UserModelImpl;
import com.zhang.znotes.presenter.ILoginPresenter;
import com.zhang.znotes.view.ILoginView;

/**
 * Created by zz on 2017/3/21.
 */

public class LoginPresenterImpl implements ILoginPresenter {
    private UserModelImpl model;
    private ILoginView loginView;

    public LoginPresenterImpl(ILoginView loginView) {
        this.loginView = loginView;
        model=new UserModelImpl();
    }

    @Override
    public void onDestroy() {
        loginView=null;
    }

    @Override
    public void login(String useraccount, String password) {
        loginView.showLoading();
        model.login(useraccount, password, new BaseModel.AsyncCallback() {
            @Override
            public void onSuccess(Object success) {
                String msg= (String) success;
                loginView.loginSuccess(msg);
                loginView.hideLoading();
            }

            @Override
            public void onError(Object error) {
                String msg= (String) error;
                loginView.loginError(msg);
                loginView.hideLoading();
            }
        });
    }
}
