package com.zhang.znotes.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhang.znotes.R;
import com.zhang.znotes.base.BaseActivity;
import com.zhang.znotes.bean.bmob.MyUser;
import com.zhang.znotes.bean.bmob.NoteBmob;
import com.zhang.znotes.bean.litepal.NotesBean;
import com.zhang.znotes.utils.CommonUtil;
import com.zhang.znotes.utils.DateKit;
import com.zhang.znotes.utils.SPUtils;
import com.zhang.znotes.utils.ToastUtils;

import java.util.Date;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class CreatNoteActivity extends BaseActivity {
    private Context mContext;
    private TextView creat_time;
    private EditText creat_et;
    private NotesBean notesBean, bean;
    private Toolbar mToolbar;
    private InputMethodManager imm;
    private ImageView syn_iv;
    private Animation animation;
    private ImageView share;
    private ScrollView scorll;

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void initParms(Bundle parms) {
        if (parms != null) {
            bean = (NotesBean) parms.getSerializable("bean");
        }
    }


    @Override
    public int bindLayout() {
        return R.layout.activity_creat_note;
    }

    @Override
    public void setPresenter() {

    }

    @Override
    public void initView(View view) {
        ShareSDK.initSDK(this);
        mContext = this;
        int editSize = (int) SPUtils.get(mContext, "editSize", 16);
        scorll = (ScrollView) view.findViewById(R.id.scrollView);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        notesBean = new NotesBean();
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        mToolbar.setTitle(getResources().getString(R.string.edit));
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
            }
        });

        syn_iv = (ImageView) view.findViewById(R.id.syn_iv);
        syn_iv.setImageResource(R.mipmap.update);
        syn_iv.setOnClickListener(this);
        syn_iv.setVisibility(View.VISIBLE);
        share = (ImageView) view.findViewById(R.id.share_iv);
        share.setVisibility(View.VISIBLE);
        share.setOnClickListener(this);
        creat_time = (TextView) view.findViewById(R.id.creat_time);
        creat_et = (EditText) view.findViewById(R.id.creat_et);
        creat_et.setTextSize(TypedValue.COMPLEX_UNIT_SP, editSize);
        animation = AnimationUtils.loadAnimation(mContext, R.anim.roraterepeat);
        if (bean == null) {
            String time = DateKit.getCurrentTime();
            creat_time.setText(time);
        } else {
            creat_time.setText(bean.getTime());
            creat_et.setText(bean.getContent());
            creat_et.setSelection(bean.getContent().length());
        }

    }


    @Override
    protected void onPause() {
        super.onPause();
        Date date = new Date(System.currentTimeMillis());
        String time = DateKit.getCurrentTime();
        String content = creat_et.getText().toString();
        if (!TextUtils.isEmpty(content)) {
            if (bean == null) {
                notesBean.setDate(date);
                notesBean.setTime(time);
                notesBean.setContent(content);
                notesBean.save();
            }
            if (bean != null) {
                if (!bean.getContent().equals(content)) {
                    bean.setDate(date);
                    bean.setTime(time);
                    bean.setContent(content);
                    LogUtil("id=" + bean.getId());
                    if (bean.getId() == 0) {
                        bean.save();
                        LogUtil("save");
                    } else {
                        LogUtil("update");
                        bean.update(bean.getId());
                    }
                }
            }

        }
    }

    @Override
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.syn_iv:
                synNote();
                break;
            case R.id.share_iv:
                String content = creat_et.getText().toString();
                if (TextUtils.isEmpty(content)) {
                    ToastUtils.showToast("请输入分享文本内容");
                    return;
                }
                showShare(content);
                break;
            default:
                break;
        }
    }


    private void showShare(String content) {
//        ShareSDK.initSDK(mContext);
//        OnekeyShare share = new OnekeyShare();
//        share.disableSSOWhenAuthorize();
//        share.setText("text");
//        // text是分享文本，所有平台都需要这个字段
//        share.setTitle("title");
//        // url仅在微信（包括好友和朋友圈）中使用
//        share.setUrl("http://sweetystory.com/");
//        share.setTitleUrl("http://sweetystory.com/");
//        share.setImageUrl("http://sweetystory.com/Public/ttwebsite/theme1/style/img/special-1.jpg");
//        share.show(mContext);

        String fname = CommonUtil.savePic(CommonUtil.compressImage(CommonUtil.getBitmapByView(scorll)));
        OnekeyShare oks = new OnekeyShare();
        // 关闭sso授权
        oks.disableSSOWhenAuthorize();
        // 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法
//        oks.setTitle("我的学车历程");
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
//        oks.setTitleUrl(linkurl);
        // text是分享文本，所有平台都需要这个字段
//        oks.setText(content1);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath(fname);//确保SDcard下面存在此张图片
//        oks.setImageUrl(imageurl);
        // url仅在微信（包括好友和朋友圈）中使用
//        oks.setUrl(linkurl);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
//        oks.setComment("快来看看我的学车历程吧~");
        // site是分享此内容的网站名称，仅在QQ空间使用
//        oks.setSite(getResources().getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
//        oks.setSiteUrl(linkurl);
        // 启动分享GUI
        // 参考代码配置章节，设置分享参数
        oks.show(this);

//        OnekeyShare oks = new OnekeyShare();
//        //关闭sso授权
//        oks.disableSSOWhenAuthorize();
//        // title标题，印象笔记、邮箱、信息、微信、人人网、QQ和QQ空间使用
////        oks.setTitle("标题");
//        // titleUrl是标题的网络链接，仅在Linked-in,QQ和QQ空间使用
////        oks.setTitleUrl("http://sharesdk.cn");
//        // text是分享文本，所有平台都需要这个字段
//        oks.setText(content);
//        //分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
//        oks.setImageUrl(fname);
//        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
//        // url仅在微信（包括好友和朋友圈）中使用
////        oks.setUrl("http://sharesdk.cn");
//        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
////        oks.setComment("我是测试评论文本");
//        // site是分享此内容的网站名称，仅在QQ空间使用
////        oks.setSite("ShareSDK");
//        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
////        oks.setSiteUrl("http://sharesdk.cn");
//
//// 启动分享GUI
//        oks.show(this);
    }


    private void synNote() {


        MyUser user = BmobUser.getCurrentUser(MyUser.class);
        if (user == null) {
            CommonUtil.showLogInDialog(mContext);
            return;
        }
        syn_iv.startAnimation(animation);
        syn_iv.setImageResource(R.mipmap.syn);
        if (bean == null) {
            syn(user, notesBean);
        } else {
            syn(user, bean);
        }
    }

    private void syn(MyUser user, final NotesBean notesBean) {
        NoteBmob noteBmob = new NoteBmob();
        noteBmob.setContent(creat_et.getText().toString());
        noteBmob.setUserEmail(user.getEmail());

        if (TextUtils.isEmpty(notesBean.getObjectId())) {
            //添加数据
            noteBmob.save(new SaveListener<String>() {
                @Override
                public void done(String objectId, BmobException e) {
                    if (e == null) {
                        notesBean.setObjectId(objectId);
                        LogUtil("添加" + notesBean.toString());
                        ToastUtils.showToast(getResources().getString(R.string.sava_success));
                    } else {
                        ToastUtils.showToast(getResources().getString(R.string.sava_error) + ": " + e.getMessage());
                        Log.e("zz", "添加失败：" + e.getMessage() + "," + e.getErrorCode());
                    }
                    animation.cancel();
                    syn_iv.setImageResource(R.mipmap.update);

                }
            });
        } else {
            //更新数据
            noteBmob.update(notesBean.getObjectId(), new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        ToastUtils.showToast(getResources().getString(R.string.sava_success));
                        LogUtil("更新" + notesBean.toString());
                    } else {
                        ToastUtils.showToast(getResources().getString(R.string.sava_error) + ": " + e.getMessage());
                        Log.e("zz", "更新失败：" + e.getMessage() + "," + e.getErrorCode());
                    }
                    animation.cancel();
                    syn_iv.setImageResource(R.mipmap.update);
                }
            });
        }


    }
}
