package com.zhang.znotes.ui.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mingle.widget.ShapeLoadingDialog;
import com.zhang.znotes.R;
import com.zhang.znotes.base.BaseActivity;
import com.zhang.znotes.utils.KeyBoardUtils;
import com.zhang.znotes.utils.ToastUtils;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class GetPasswordActivity extends BaseActivity {
    private EditText emailEt;
    private Button sendBtn;
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
        return R.layout.activity_get_password;
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
        Toolbar mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        mToolbar.setTitle(getResources().getString(R.string.action_get_password));
        setSupportActionBar(mToolbar);
        emailEt= (EditText) view.findViewById(R.id.email);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                KeyBoardUtils.closeKeybord(emailEt,mContext);
            }
        });
        sendBtn= (Button) view.findViewById(R.id.send);
        sendBtn.setOnClickListener(this);
    }

    @Override
    public void widgetClick(View v) {
        switch (v.getId()){
            case R.id.send:
                final String email=emailEt.getText().toString();
                if (!email.contains("@")){
                    ToastUtils.showToast(getString(R.string.prompt_email));
                    return;
                }
                dialog.show();
                BmobUser.resetPasswordByEmail(email, new UpdateListener() {

                    @Override
                    public void done(BmobException e) {
                        if(e==null){
                            ToastUtils.showToast("重置密码请求成功，请到" + email + "邮箱进行密码重置操作");
                            finish();
                        }else{
                            ToastUtils.showToast("失败:" + e.getMessage());
                        }
                        dialog.dismiss();
                    }
                });
                break;
            default:
                break;
        }
    }
}
