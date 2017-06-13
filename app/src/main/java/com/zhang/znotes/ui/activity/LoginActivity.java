package com.zhang.znotes.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mingle.widget.ShapeLoadingDialog;
import com.zhang.znotes.R;
import com.zhang.znotes.base.BaseActivity;
import com.zhang.znotes.bean.bmob.MyUser;
import com.zhang.znotes.presenter.impl.LoginPresenterImpl;
import com.zhang.znotes.service.UpdateInfoEvent;
import com.zhang.znotes.utils.KeyBoardUtils;
import com.zhang.znotes.utils.ToastUtils;
import com.zhang.znotes.view.ILoginView;

import org.greenrobot.eventbus.EventBus;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 2017年3月15日17:06:01 by zz
 */
public class LoginActivity extends BaseActivity implements ILoginView {

    private EditText mEmailView;
    private EditText mPasswordView;
    private Toolbar mToolbar;
    private LoginPresenterImpl presenter;
    private ShapeLoadingDialog dialog;
    private String account,password;
    private Context mContext;


    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        return R.layout.activity_login;
    }

    @Override
    public void setPresenter() {
        presenter=new LoginPresenterImpl(this);
    }

    @Override
    public void initView(View view) {
        mContext=this;
        dialog=new ShapeLoadingDialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setLoadingText(getResources().getString(R.string.loading));
        mToolbar= (Toolbar) view.findViewById(R.id.toolbar);
        mToolbar.setTitle(getResources().getString(R.string.action_sign_in));
        TextView toolbar_right = (TextView) view.findViewById(R.id.toolbar_right);
        toolbar_right.setText(getResources().getString(R.string.prompt_forget_password));
        toolbar_right.setOnClickListener(this);
        setSupportActionBar(mToolbar);

        mEmailView = (EditText) view.findViewById(R.id.email);
        mPasswordView = (EditText) view.findViewById(R.id.password);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                KeyBoardUtils.closeKeybord(mEmailView,mContext);
            }
        });

        Button signIn = (Button)view.findViewById(R.id.btn_sign_in);
        Button register = (Button)view.findViewById(R.id.btn_register);
        signIn.setOnClickListener(this);
        register.setOnClickListener(this);
    }


    @Override
    public void widgetClick(View v) {
        switch (v.getId()){
            case R.id.toolbar_right:
                startActivity(GetPasswordActivity.class);
            break;
            case R.id.btn_sign_in:
                 account=mEmailView.getText().toString();
                 password=mPasswordView.getText().toString();
                if (TextUtils.isEmpty(account)){
                    ToastUtils.showToast(getResources().getString(R.string.prompt_username));
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    ToastUtils.showToast(getResources().getString(R.string.prompt_password));
                    return;
                }
                presenter.login(account,password);
                break;
            case R.id.btn_register:
                startActivity(RegisterActivity.class);
                break;
            default:
                break;
        }
    }


    @Override
    public void showLoading() {
        dialog.show();
    }

    @Override
    public void hideLoading() {
        dialog.dismiss();
    }

    @Override
    public void loginSuccess(String successMessage) {
        ToastUtils.showToast(successMessage);
        BmobUser user=BmobUser.getCurrentUser(MyUser.class);
        MyUser myUser = new MyUser();
        myUser.setUserPassword(password);
        myUser.update(user.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    LogUtil("保存成功");
                } else {
                    LogUtil("失败:" + e.getMessage());
                }
                EventBus.getDefault().post(new UpdateInfoEvent());
                startActivity(UserInfoActivity.class);
                finish();
            }
        });

    }

    @Override
    public void loginError(String errorMessage) {
        ToastUtils.showToast(errorMessage);
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }
}

