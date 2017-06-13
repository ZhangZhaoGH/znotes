package com.zhang.znotes.ui.activity;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mingle.widget.ShapeLoadingDialog;
import com.zhang.znotes.R;
import com.zhang.znotes.base.BaseActivity;
import com.zhang.znotes.bean.bmob.NoteBmob;
import com.zhang.znotes.ui.adapter.SettingAdapter;
import com.zhang.znotes.ui.view.DividerItemDecoration;
import com.zhang.znotes.utils.CommonUtil;
import com.zhang.znotes.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class SettingActivity extends BaseActivity {
    private RecyclerView mRecyclerView;
    private Context mContext;
    private Toolbar mToolbar;
    private List<String> mList;
    private SettingAdapter adapter;


    @Override
    public void doBusiness(Context mContext) {
        initData();
    }

    private void initData() {
//        mList.add("主题");
//        mList.add("字体");
        mList.add("意见反馈");
        mList.add("检查更新");
        mList.add("删除确认提示");
//        mList.add("推荐ZNotes");
        if (adapter==null){
            adapter=new SettingAdapter(this);
            adapter.setDataList(mList);
            mRecyclerView.setAdapter(adapter);
            adapter.setmOnItemClickListener(new SettingAdapter.onItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {

                    switch (position){
                        case 0:
                            startActivity(FeedBackActivity.class);
                            break;
                        case 1:
                            getUpdateVersion();

                            break;
                        case 2:
                            break;
                        default:
                            break;
                    }
                }

                @Override
                public void onItemLongClick(View view, int position) {
                }
            });
        }else {
            adapter.notifyDataSetChanged();
        }

    }

    private void getUpdateVersion() {
        BmobQuery<NoteBmob> query = new BmobQuery<NoteBmob>();
        final String versionCode=CommonUtil.getVersionCode(mContext);
        final String versionName=CommonUtil.getVersionName(mContext);
        query.getObject("a36f5aa8a3", new QueryListener<NoteBmob>() {

            @Override
            public void done(NoteBmob object, BmobException e) {
                if(e==null){
                    String versionCodeOnLine=object.getContent();
                    if (versionCode.equals(versionCodeOnLine)){
                        ToastUtils.showToast("您当前的版本是"+versionName+" ,已经是最新版");
                    }else {
                        ToastUtils.showToast("您当前的版本是"+versionName+" ,为了更好的用户体验，请您更新最新版");
                    }
                }else{
                    ToastUtils.showToast("您当前的版本是"+versionName);
                    LogUtil("失败："+e.getMessage()+","+e.getErrorCode());
                }
            }

        });
    }

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        return R.layout.activity_setting;
    }

    @Override
    public void setPresenter() {

    }

    @Override
    public void initView(View view) {
        ShareSDK.initSDK(this);
        mContext=this;
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        mToolbar.setBackgroundColor(ContextCompat.getColor(mContext,R.color.title_bg));
        mToolbar.setTitle(getResources().getString(R.string.setting_title));
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mRecyclerView= (RecyclerView) view.findViewById(R.id.setting_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mList=new ArrayList<>();
    }

    @Override
    public void widgetClick(View v) {

    }

    private void showShare() {

        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // title标题，印象笔记、邮箱、信息、微信、人人网、QQ和QQ空间使用
        oks.setTitle("简洁、好用的笔记本");
        // titleUrl是标题的网络链接，仅在Linked-in,QQ和QQ空间使用
//        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
//        oks.setText(content);
        //分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
//        oks.setImageUrl(fname);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
//        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
//        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
//        oks.setSite("ShareSDK");
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
//        oks.setSiteUrl("http://sharesdk.cn");

// 启动分享GUI
        oks.show(this);
    }
}
