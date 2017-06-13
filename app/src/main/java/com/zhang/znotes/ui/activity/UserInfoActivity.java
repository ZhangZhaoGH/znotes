package com.zhang.znotes.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.mingle.widget.ShapeLoadingDialog;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.zhang.znotes.MyApplication;
import com.zhang.znotes.R;
import com.zhang.znotes.base.BaseActivity;
import com.zhang.znotes.bean.bmob.MyUser;
import com.zhang.znotes.presenter.IUserInfoPresenter;
import com.zhang.znotes.presenter.impl.UserInfoPresenterImpl;
import com.zhang.znotes.service.UpdateInfoEvent;
import com.zhang.znotes.utils.KeyBoardUtils;
import com.zhang.znotes.utils.ToastUtils;
import com.zhang.znotes.view.IUserInfoView;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserInfoActivity extends BaseActivity implements IUserInfoView {
    private CollapsingToolbarLayout toolbarLayout;
    private ImageView bg;
    private CircleImageView head;
    private Context mContext;
    private Button log_out, save;
    private IUserInfoPresenter presenter;
    private static final int PHOTO_REQUEST_CAREMA = 1;
    private static final int PHOTO_REQUEST_GALLERY = 2;
    private static final String PHOTO_FILE_NAME = "headImg.jpg";
    private static final String PHOTO_BG_NAME = "bg.jpg";
    private File tempFile, bgFile;
    private ShapeLoadingDialog dialog;
    private MyUser user;
    private EditText usernameEt, emailEt, telEt;
    private LinearLayout upload_ll;
    private RadioButton boy;
    private String type;

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        return R.layout.activity_user_info;
    }

    @Override
    public void setPresenter() {
        presenter = new UserInfoPresenterImpl(this);
    }

    @Override
    public void initView(View view) {
        mContext = this;
        emailEt = (EditText) view.findViewById(R.id.email);
        usernameEt = (EditText) view.findViewById(R.id.username);
        telEt = (EditText) view.findViewById(R.id.tel);
        toolbarLayout = (CollapsingToolbarLayout) view.findViewById(R.id.toolbar_layout);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                KeyBoardUtils.closeKeybord(emailEt, mContext);
            }
        });
        bg = (ImageView) view.findViewById(R.id.img);
        bg.setOnClickListener(this);
        head = (CircleImageView) view.findViewById(R.id.head);
        head.setOnClickListener(this);
        head.setImageResource(R.mipmap.fanghuonv);
        dialog = new ShapeLoadingDialog(this);
        dialog.setLoadingText("上传过程中...");
        dialog.setCanceledOnTouchOutside(false);
        log_out = (Button) view.findViewById(R.id.log_out);
        log_out.setOnClickListener(this);

        user = BmobUser.getCurrentUser(MyUser.class);
        toolbarLayout.setTitle(user.getUsername());
        String headImg = user.getHeadImg();
        if (!TextUtils.isEmpty(headImg)) {
            Picasso.with(mContext).load(headImg).resize(300, 300).centerCrop().placeholder(R.mipmap.image_error).into(head);
        }else {
            head.setImageResource(R.mipmap.default_head);
        }
        if (!TextUtils.isEmpty(user.getBgImg())) {
            Picasso.with(mContext).load(user.getBgImg()).resize(600, 600).centerCrop().placeholder(R.color.color_blue).into(bg);
        }

        if (!TextUtils.isEmpty(user.getMobilePhoneNumber())) {
            telEt.setText(user.getMobilePhoneNumber());
        }
        emailEt.setText(user.getEmail());
        usernameEt.setText(user.getUsername());
        telEt.setOnClickListener(this);
        emailEt.setOnClickListener(this);
        usernameEt.setOnClickListener(this);
        upload_ll = (LinearLayout) view.findViewById(R.id.upload_ll);
        upload_ll.setOnClickListener(this);

        emailEt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    emailEt.setCursorVisible(true);
                }
                return false;
            }
        });
        usernameEt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    usernameEt.setCursorVisible(true);
                }
                return false;
            }
        });
        telEt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    telEt.setCursorVisible(true);
                }
                return false;
            }
        });

        boy = (RadioButton) view.findViewById(R.id.boy);
        RadioButton girl = (RadioButton) view.findViewById(R.id.girl);
        if (user.getSex() != null){
            if (user.getSex()) {
                boy.setChecked(true);
            } else {
                girl.setChecked(true);
            }
        }
        save = (Button) view.findViewById(R.id.save);
        save.setOnClickListener(this);
    }

    @Override
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.img:
                type = MyApplication.BGIMG;
                changeHeadIcon(getString(R.string.choose_bg_img));
                break;
            case R.id.head:
                type = MyApplication.HEAD;
                changeHeadIcon(getString(R.string.choose_head_img));
                break;
            case R.id.log_out:
                presenter.logOut();
                break;
            case R.id.upload_ll:
                startActivity(UpdatePasswordActivity.class);
                break;
            case R.id.save:
                updateInfo();
                break;
            default:
                break;
        }
    }

    private void updateInfo() {
        MyUser newUser = new MyUser();
        if (!TextUtils.isEmpty(emailEt.getText().toString())) {
            newUser.setEmail(emailEt.getText().toString());
        }
        if (!TextUtils.isEmpty(usernameEt.getText().toString())) {
            newUser.setUsername(usernameEt.getText().toString());
        }
        if (!TextUtils.isEmpty(telEt.getText().toString())) {
            newUser.setMobilePhoneNumber(telEt.getText().toString());
        }
        newUser.setSex(boy.isChecked());
        newUser.update(user.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    ToastUtils.showToast(getString(R.string.update_success));
                    finish();
                } else {
                    ToastUtils.showToast(getString(R.string.update_error) + " :" + e.getMessage());
                    LogUtil(getString(R.string.update_error) + " :" + e.getMessage());
                }
            }
        });
    }

    /**
     * 更换头像选项
     */
    private void changeHeadIcon(String title) {
        presenter.changeHeadIcon(mContext, title, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
                } else {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        Uri uri = null;
                        if (type.equals(MyApplication.HEAD)) {
                            tempFile = new File(Environment.getExternalStorageDirectory(), PHOTO_FILE_NAME);
                            uri = Uri.fromFile(tempFile);
                        }
                        if (type.equals(MyApplication.BGIMG)) {
                            bgFile = new File(Environment.getExternalStorageDirectory(), PHOTO_BG_NAME);
                            uri = Uri.fromFile(bgFile);
                        }
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                        startActivityForResult(intent, PHOTO_REQUEST_CAREMA);
                    } else {
                        ToastUtils.showToast("未找到存储卡，无法存储照片！");
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHOTO_REQUEST_GALLERY) {
            if (data != null) {
                Uri uri = data.getData();
                if (type.equals(MyApplication.HEAD)) {
                    Picasso.with(mContext).load(uri).resize(300, 300).centerCrop().placeholder(R.mipmap.image_loading).into(head);
                    presenter.updateHead(uri.toString());
                }
                if (type.equals(MyApplication.BGIMG)) {
                    Picasso.with(mContext).load(uri).resize(600, 600).centerCrop().placeholder(R.mipmap.image_loading).into(bg);
                    presenter.updateBg(uri.toString());
                }
            }

        } else if (requestCode == PHOTO_REQUEST_CAREMA) {
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                if (type.equals(MyApplication.HEAD)) {
                    if (tempFile != null) {
                        Uri uri = Uri.fromFile(tempFile);
                        LogUtil("uri=" + uri);
                        Picasso.with(mContext).load(uri).memoryPolicy(MemoryPolicy.NO_CACHE).resize(300, 300).centerCrop().placeholder(R.mipmap.image_loading).into(head);
                        presenter.updateHead(uri.toString());
                    } else {
                        ToastUtils.showToast("上传的文件路径出错");
                    }
                }
                if (type.equals(MyApplication.BGIMG)) {
                    if (bgFile != null) {
                        Uri uri = Uri.fromFile(bgFile);
                        LogUtil("uri=" + uri);
                        Picasso.with(mContext).load(uri).memoryPolicy(MemoryPolicy.NO_CACHE).resize(600, 600).centerCrop().placeholder(R.mipmap.image_loading).into(bg);
                        presenter.updateBg(uri.toString());
                    } else {
                        ToastUtils.showToast("上传的文件路径出错");
                    }
                }

            } else {
                ToastUtils.showToast("未找到存储卡，无法存储照片！");
            }
        }
    }


    @Override
    public void logOutSuccess(String successMessage) {
        ToastUtils.showToast(successMessage);
        EventBus.getDefault().post(new UpdateInfoEvent());
        finish();
    }


    @Override
    public void updateSuccess(String successMessage) {
        ToastUtils.showToast(successMessage);
        EventBus.getDefault().post(new UpdateInfoEvent());
    }

    @Override
    public void updateError(String errorMessage) {
        ToastUtils.showToast(errorMessage);
    }

    @Override
    public void updateBackgroundSuccess(String successMessage) {
        ToastUtils.showToast(successMessage);
        EventBus.getDefault().post(new UpdateInfoEvent());
    }

    @Override
    public void updateBackgroundError(String errorMessage) {
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
}
