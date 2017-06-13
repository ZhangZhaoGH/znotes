package com.zhang.znotes.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mingle.widget.ShapeLoadingDialog;
import com.zhang.znotes.R;
import com.zhang.znotes.base.BaseActivity;
import com.zhang.znotes.presenter.impl.RegisterPresenterImpl;
import com.zhang.znotes.utils.ToastUtils;
import com.zhang.znotes.view.IRegisterView;

public class RegisterActivity extends BaseActivity implements IRegisterView {

    private EditText etAccount,etPassword,etPasswordAgain,et_email;
    private RegisterPresenterImpl presenter;
    private Context mContext;
    private ShapeLoadingDialog dialog;

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        return R.layout.activity_register;
    }

    @Override
    public void setPresenter() {
        presenter=new RegisterPresenterImpl(mContext,this);
    }

    @Override
    public void initView(View view) {
        mContext=this;
        Toolbar mToolbar= (Toolbar) view.findViewById(R.id.toolbar);
        mToolbar.setTitle(getResources().getString(R.string.action_register));
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        et_email= (EditText) view.findViewById(R.id.et_email);
        etAccount= (EditText) view.findViewById(R.id.et_account);
        etPassword= (EditText) view.findViewById(R.id.et_password);
        etPasswordAgain= (EditText) view.findViewById(R.id.et_password_again);
        Button submit= (Button) view.findViewById(R.id.btn_submit);
        submit.setOnClickListener(this);
        dialog=new ShapeLoadingDialog(this);
        dialog.setLoadingText(getResources().getString(R.string.loading));
        dialog.setCanceledOnTouchOutside(false);

    }


    @Override
    public void widgetClick(View v) {
        switch (v.getId()){
            case R.id.btn_submit:
                //提交注册
                String account=etAccount.getText().toString();
                String email=et_email.getText().toString();
                String password=etPassword.getText().toString();
                String password2=etPasswordAgain.getText().toString();
                if (TextUtils.isEmpty(account)){
                    ToastUtils.showToast(getResources().getString(R.string.prompt_username));
                    return;
                }
                if (TextUtils.isEmpty(email)){
                    ToastUtils.showToast(getResources().getString(R.string.prompt_email));
                    return;
                }
                if (!email.contains("@")){
                    ToastUtils.showToast(getResources().getString(R.string.prompt_username));
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    ToastUtils.showToast(getResources().getString(R.string.prompt_password));
                    return;
                }
                if (password.length()<6){
                    ToastUtils.showToast(getResources().getString(R.string.prompt_password_length));
                    return;
                }
                if (TextUtils.isEmpty(password2)){
                    ToastUtils.showToast(getResources().getString(R.string.prompt_password_again));
                    return;
                }
                if (!password.equals(password2)){
                    ToastUtils.showToast(getResources().getString(R.string.prompt_password_confirm));
                    return;
                }

                presenter.onRegister(account,email,password);
                break;
            default:
                break;
        }
    }


    @Override
    public void registSuccess(String successMessage) {
        ToastUtils.showToast(successMessage);
        finish();
    }

    @Override
    public void registError(String errorMessage) {
        ToastUtils.showToast(errorMessage);
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
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }
}
