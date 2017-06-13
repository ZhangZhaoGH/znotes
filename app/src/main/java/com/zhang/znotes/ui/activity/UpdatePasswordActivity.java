package com.zhang.znotes.ui.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
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
import com.zhang.znotes.utils.KeyBoardUtils;
import com.zhang.znotes.utils.ToastUtils;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class UpdatePasswordActivity extends BaseActivity {
    private EditText oldPass;
    private EditText newPass;
    private Toolbar mToolbar;
    private ShapeLoadingDialog dialog;
    private Context mContext;


    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        return R.layout.activity_update_password;
    }

    @Override
    public void setPresenter() {

    }

    @Override
    public void initView(View view) {
        mContext=this;
        dialog=new ShapeLoadingDialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setLoadingText(getResources().getString(R.string.loading));
        mToolbar= (Toolbar) view.findViewById(R.id.toolbar);
        mToolbar.setTitle(getString(R.string.action_update_password));
        setSupportActionBar(mToolbar);

        oldPass = (EditText) view.findViewById(R.id.password);
        newPass = (EditText) view.findViewById(R.id.new_password);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                KeyBoardUtils.closeKeybord(oldPass,mContext);
            }
        });

        Button signIn = (Button)view.findViewById(R.id.commit);
        signIn.setOnClickListener(this);
    }

    @Override
    public void widgetClick(View v) {
        String oldPW=oldPass.getText().toString();
        final String newPW=newPass.getText().toString();
        if (TextUtils.isEmpty(oldPW)){
            ToastUtils.showToast(getString(R.string.prompt_old_password));
            return;
        }
        if (TextUtils.isEmpty(newPW)){
            ToastUtils.showToast(getString(R.string.prompt_new_password));
            return;
        }

        if (newPW.length()<6){
            ToastUtils.showToast(getResources().getString(R.string.prompt_password_length));
            return;
        }

        BmobUser.updateCurrentUserPassword(oldPW, newPW, new UpdateListener() {

            @Override
            public void done(BmobException e) {
                if(e==null){
                    BmobUser bmobUser = BmobUser.getCurrentUser(MyUser.class);
                    MyUser newUser = new MyUser();
                    newUser.setPassword(newPW);
                    newUser.update(bmobUser.getObjectId(),new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e==null){
                                LogUtil("保存成功");
                            }else{
                                LogUtil("更新用户信息失败:" + e.getMessage());
                            }
                        }
                    });
                    ToastUtils.showToast(getString(R.string.prompt_password_update_success));
                    finish();
                }else{
                    if (e.getErrorCode() == 210){
                        ToastUtils.showToast(getString(R.string.prompt_password_update_error)+": 旧密码错误");
                    }else {
                        ToastUtils.showToast(getString(R.string.prompt_password_update_error)+": "+e.getMessage());
                    }
                }
            }

        });
    }
}
