package com.zhang.znotes.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.picasso.Picasso;
import com.zhang.znotes.MyApplication;
import com.zhang.znotes.R;
import com.zhang.znotes.bean.bmob.MyUser;
import com.zhang.znotes.bean.bmob.NoteBmob;
import com.zhang.znotes.bean.litepal.DelNotesBean;
import com.zhang.znotes.presenter.impl.MainPresenterImpl;
import com.zhang.znotes.service.UpdateInfoEvent;
import com.zhang.znotes.ui.view.DividerItemDecoration;
import com.zhang.znotes.ui.adapter.SwipeMenuAdapter;
import com.zhang.znotes.base.BaseActivity;
import com.zhang.znotes.bean.litepal.NotesBean;
import com.zhang.znotes.utils.CommonUtil;
import com.zhang.znotes.utils.SPUtils;
import com.zhang.znotes.utils.ToastUtils;
import com.zhang.znotes.view.IMainView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;
import cn.sharesdk.framework.ShareSDK;
import de.hdodenhof.circleimageview.CircleImageView;
import skin.support.SkinCompatManager;
import skin.support.utils.SkinPreference;

public class MainActivity extends BaseActivity implements IMainView {
    private Context mContext;
    private EditText mEt;
    private RecyclerView mRecyclerView;
    private long firstTime;
    private DrawerLayout mDrawerLayout;
    private List<NotesBean> mList;
    private Toolbar mToolbar;
    private List<NotesBean> etList;
    private CircleImageView headImg;
    private TextView username;
    private SwipeMenuAdapter menuAdapter;
    private MyUser user;
    private ImageView syn_iv;
    private Animation animation;

    private MainPresenterImpl presenter;
    private ImageView commit, cancleTitle, allChoose;
    private List<BmobObject> inserNotes;
    private List<BmobObject> updateNotes;
    private List<NotesBean> inserList;
    private FloatingActionButton fab;
    private RelativeLayout titleRl;
    private LinearLayout commitRl;
    private TextView chooseTitle;
    private Animation topInAnim, bottomInAnim;
    private Animation topOutAnim, bottomOutAnim;
    private int chooseNum = 0;
    //删除的集合
    private List<NotesBean> delList;
    private ImageView upload;
    private View headView;
    private ImageView bg_iv;
    public MaterialDialog dialog;


    @Override
    public void doBusiness(Context mContext) {
        if (user != null) {
            presenter.login(user.getUsername(), user.getUserPassword());
        }
    }

    @Override
    public void initParms(Bundle parms) {
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void setPresenter() {
        presenter = new MainPresenterImpl(this);
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void initView(View view) {
        EventBus.getDefault().register(this);
        inserNotes = new ArrayList<BmobObject>();
        updateNotes = new ArrayList<BmobObject>();
        inserList = new ArrayList<NotesBean>();
        delList = new ArrayList<>();
        mContext = this;
        mList = new ArrayList<>();
        etList = new ArrayList<>();
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.mipmap.menu);
        mToolbar.setTitle("");// 标题的文字需在setSupportActionBar之前，不然会无效
        setSupportActionBar(mToolbar);
        syn_iv = (ImageView) view.findViewById(R.id.syn_iv);
        syn_iv.setImageResource(R.mipmap.syn);
        syn_iv.setOnClickListener(this);
        syn_iv.setVisibility(View.VISIBLE);
        animation = AnimationUtils.loadAnimation(this, R.anim.roraterepeat);
        mEt = (EditText) view.findViewById(R.id.main_edit);
        mEt.addTextChangedListener(textwatcher);
        mEt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    mEt.setCursorVisible(true);// 再次点击显示光标
                }
                return false;
            }
        });
        //全选布局
        titleRl = (RelativeLayout) view.findViewById(R.id.choose_title_rl);
        commitRl = (LinearLayout) view.findViewById(R.id.commit_ll);
        chooseTitle = (TextView) view.findViewById(R.id.check_title);
        cancleTitle = (ImageView) view.findViewById(R.id.cancle_title);
        allChoose = (ImageView) view.findViewById(R.id.all_choose_iv);
        allChoose.setOnClickListener(this);
        cancleTitle.setOnClickListener(this);
        commit = (ImageView) view.findViewById(R.id.commit_iv);
        commit.setOnClickListener(this);
        upload = (ImageView) view.findViewById(R.id.upload_iv);
        upload.setVisibility(View.VISIBLE);
        upload.setOnClickListener(this);

        topInAnim = AnimationUtils.loadAnimation(this, R.anim.alpha_top_in);
        bottomInAnim = AnimationUtils.loadAnimation(this, R.anim.alpha_bottom_in);
        topOutAnim = AnimationUtils.loadAnimation(this, R.anim.alpha_top_out);
        bottomOutAnim = AnimationUtils.loadAnimation(this, R.anim.alpha_bottom_out);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.main_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mDrawerLayout = (DrawerLayout) view.findViewById(R.id.drawer);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open,
                R.string.drawer_close);
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        NavigationView navView = (NavigationView) view.findViewById(R.id.nav_view);
        navView.setItemIconTintList(null);//让控件显示本身的颜色
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_cloud_notes:
                        //云笔记
                        if (user == null) {
                            CommonUtil.showLogInDialog(mContext);
                        } else {
                            startActivity(CloudNotesActivity.class);
                        }
                        break;
                    case R.id.nav_recycle_bin:
                        //回收站
                        startActivity(RecycleBinActivity.class);
                        break;
                    case R.id.nav_setting:
                        //设置
                        startActivity(SettingActivity.class);
                        break;
                    case R.id.nav_log_out:
                        //退出

                        if (user == null) {
                            ToastUtils.showToast("请先登录");
                            break;
                        }

                        CommonUtil.showDialog(mContext, getString(R.string.prompt_log_out), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                BmobUser.logOut();
                                EventBus.getDefault().post(new UpdateInfoEvent());
                                ToastUtils.showToast("注销成功");
                            }
                        });
                        break;
                }
                mDrawerLayout.closeDrawers();
                return true;
            }
        });

        headView = navView.getHeaderView(0);

        headView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BmobUser user = BmobUser.getCurrentUser(MyUser.class);
                if (null != user) {
                    startActivity(UserInfoActivity.class);
                } else {
                    startActivity(LoginActivity.class);
                }
                mDrawerLayout.closeDrawers();
            }
        });
        headImg = (CircleImageView) headView.findViewById(R.id.icon_image);
        username = (TextView) headView.findViewById(R.id.usename);
        bg_iv=(ImageView)headView.findViewById(R.id.bg_iv);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(this);


        user = BmobUser.getCurrentUser(MyUser.class);
        if (user != null) {
            username.setText(user.getUsername());
            if (!TextUtils.isEmpty(user.getHeadImg())) {
                Picasso.with(mContext).load(user.getHeadImg()).resize(300, 300).centerCrop().placeholder(R.mipmap.image_loading).error(R.mipmap.image_error).into(headImg);
            }
            if (!TextUtils.isEmpty(user.getBgImg())){
                Picasso.with(mContext).load(user.getBgImg()).resize(600,600).centerCrop().into(bg_iv);
            }
        }

        MyApplication.isShowDel= (boolean) SPUtils.get(mContext,SPUtils.PROMPT_DEL,true);

    }


    @Override
    protected void onResume() {
        super.onResume();
        getNotes();
        initData();
        mEt.setText("");
        mEt.setCursorVisible(false);// 再次点击显示光标
    }

    private void getNotes() {
        mList.clear();
        mList = DataSupport.select("date", "content", "time", "objectId").order("date desc").find(NotesBean.class);
    }


    @Subscribe
    public void onEventMainThread(UpdateInfoEvent event) {
        user = BmobUser.getCurrentUser(MyUser.class);
        if (null != user) {
            username.setText(user.getUsername());
            if (!TextUtils.isEmpty(user.getHeadImg())) {
                Picasso.with(mContext).load(user.getHeadImg()).resize(300, 300).centerCrop().placeholder(R.mipmap.image_loading).error(R.mipmap.image_error).into(headImg);
            }
            if (!TextUtils.isEmpty(user.getBgImg())){
                Picasso.with(mContext).load(user.getBgImg()).resize(600,600).centerCrop().into(bg_iv);
            }
        } else {
            username.setText(getString(R.string.prompt_header));
            headImg.setImageResource(R.mipmap.default_head);
            bg_iv.setImageResource(R.color.color_blue);
        }
    }

    private void initData() {
        if (menuAdapter == null) {
            menuAdapter = new SwipeMenuAdapter(mContext, 1);
            menuAdapter.setDataList(mList);
            menuAdapter.setmOnItemClickListener(new SwipeMenuAdapter.AdapterItemClick() {
                @Override
                public void onItemClck(View view, int position) {
                    if (titleRl.getVisibility() == View.VISIBLE) {
                        //批量选项显示
                        int objId = mList.get(position).getId();
                        boolean isContain = false; // false 不包含 true则包含
                        int index = 0;
                        for (int i = 0; i < delList.size(); i++) {
                            if (delList.get(i).getId() == objId) {
                                isContain = true;
                                index = i;
                            }
                        }
                        if (isContain) {
                            chooseNum--;
                            delList.remove(delList.get(index));
                        } else {
                            chooseNum++;
                            delList.add(mList.get(position));
                        }

                        chooseTitle.setText("已选择 " + chooseNum + " 项");
                        menuAdapter.notifyItemChanged(position, position);
                    }

                    if (titleRl.getVisibility() == View.GONE) {
                        //批量选项隐藏
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("bean", mList.get(position));
                        startActivity(CreatNoteActivity.class, bundle);
                    }
                }

                @Override
                public void onItemLongClck(View view, int position) {

                }
            });
            menuAdapter.setOnSwipeListener(new SwipeMenuAdapter.onSwipeListener() {
                @Override
                public void onDel(int position) {
                    showDia(position);
                }

                @Override
                public void onSyn(int position) {
                    startSyn(position);
                }
            });

            mRecyclerView.setAdapter(menuAdapter);
        } else {
            menuAdapter.setDataList(mList);
            menuAdapter.notifyDataSetChanged();
        }
    }

    private void startSyn(final int position) {
        if (user == null) {
            CommonUtil.showLogInDialog(mContext);
            return;
        }
        NotesBean bean = mList.get(position);
        if (TextUtils.isEmpty(bean.getObjectId())) {
            presenter.save(bean, user.getEmail());
        } else {
            //更新数据
            presenter.update(bean);
        }

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mEt.setCursorVisible(false);
        return super.onTouchEvent(event);

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (titleRl.getVisibility() == View.VISIBLE){
                    hideOption();
                    return true;
                }
                long secondTime = System.currentTimeMillis();
                if (secondTime - firstTime > 2000) {
                    firstTime = secondTime;
                    ToastUtils.showSnackMessage(mToolbar, getResources().getString(R.string.click_again_log_out));
                    return true;
                } else {
                    finish();
                }
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    /**
     * 隐藏选项卡
     */
    private void hideOption() {
        titleRl.startAnimation(topOutAnim);
        commitRl.startAnimation(bottomOutAnim);
        fab.setVisibility(View.VISIBLE);
        titleRl.setVisibility(View.GONE);
        commitRl.setVisibility(View.GONE);
        if (menuAdapter != null) {
            chooseNum = 0;
            delList.clear();
            menuAdapter.setChooseType(4);
            chooseTitle.setText("已选择 0 项");
        }
    }

    @Override
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
//                dialog = new MaterialDialog.Builder(this)
//                        .title("提示")
//                        .content("加载中")
//                        .positiveText("确定")
//                        .negativeText("取消")
//                        .positiveColor(Color.parseColor("#00BCD4"))
//                        .negativeColor(Color.parseColor("#ff4a57"))
//                        .onPositive(new MaterialDialog.SingleButtonCallback() {
//                            @Override
//                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                                ToastUtils.showToast("确定的监听");
//                            }
//                        })
//                        .cancelable(false)
//                        .show();

                //创建写的日记页面
                startActivity(CreatNoteActivity.class);
                break;
            case R.id.syn_iv:
                fab.setVisibility(View.GONE);
                titleRl.setVisibility(View.VISIBLE);
                commitRl.setVisibility(View.VISIBLE);
                titleRl.startAnimation(topInAnim);
                commitRl.startAnimation(bottomInAnim);
                if (menuAdapter != null) {
                    menuAdapter.setChooseType(1);
                }

                break;
            case R.id.cancle_title:
                hideOption();
                break;
            case R.id.upload_iv:
                //上传
                if (user == null) {
                    CommonUtil.showLogInDialog(mContext);
                    return;
                }

                if (delList.size() < 1) {
                    ToastUtils.showToast("最少需要选择1个");
                    return;
                }

                CommonUtil.showDialog(mContext, getResources().getString(R.string.is_confirm_all_update), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        synAll();
                    }
                });

                break;
            case R.id.commit_iv:
                //删除
                if (delList.size() < 1) {
                    ToastUtils.showToast("最少需要选择1个");
                    return;
                }

                if (MyApplication.isShowDel) {
                    CommonUtil.showDialog(mContext, getResources().getString(R.string.is_confirm_del), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            presenter.delAll(delList);
                        }
                    });
                } else {
                    //确定删除
                    presenter.delAll(delList);
                }

                break;
            case R.id.all_choose_iv:
                //全选
                ToastUtils.showToast("一次最多只能选50");
                if (allChoose.getDrawable().getCurrent().getConstantState()
                        == ContextCompat.getDrawable(mContext, R.mipmap.all_choose).getConstantState()) {
                    allChoose.setImageResource(R.mipmap.no_all_choose);
                } else {
                    allChoose.setImageResource(R.mipmap.all_choose);
                }

                break;
            default:
                break;
        }
    }


    @Override
    public void delAllSuccess(String successMessage) {
        hideOption();
        getNotes();
        initData();
        ToastUtils.showToast(successMessage);
    }

    @Override
    public void delAllError(String errorMessage) {
        hideOption();
        getNotes();
        initData();
        ToastUtils.showToast(errorMessage);
    }

    private void synAll() {
        CommonUtil.showDialog(mContext, getResources().getString(R.string.is_confirm_all_update), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mList.size() == 0) {
                    ToastUtils.showToast(getResources().getString(R.string.update_without_data));
                    return;
                }
                inserNotes.clear();
                inserList.clear();
                updateNotes.clear();
                for (NotesBean bean : mList) {
                    NoteBmob note = new NoteBmob();
                    note.setContent(bean.getContent());
                    note.setUserEmail(user.getEmail());
                    if (TextUtils.isEmpty(bean.getObjectId())) {
                        inserNotes.add(note);
                        inserList.add(bean);
                    } else {
                        note.setObjectId(bean.getObjectId());
                        updateNotes.add(note);
                    }
                }

                presenter.saveAll(inserNotes, inserList);
                presenter.updateAll(updateNotes);
            }
        });
    }


    private TextWatcher textwatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            String content = mEt.getText().toString();
            if (!TextUtils.isEmpty(content)) {
                etList = DataSupport.select("date", "content", "time", "objectId").order("date desc").find(NotesBean.class);
                mList.clear();
                for (NotesBean bean : etList) {
                    if (bean.getContent().contains(content)) {
                        mList.add(bean);
                    }
                }
                initData();
            } else {
                mList = DataSupport.select("date", "content", "time", "objectId").order("date desc").find(NotesBean.class);
                initData();
            }

        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {

        }

        @Override
        public void afterTextChanged(Editable arg0) {

        }
    };

    @Override
    public void updateSuccess(String successMessage) {
        ToastUtils.showToast(successMessage);
    }

    @Override
    public void updateError(String errorMessage) {
        ToastUtils.showToast(errorMessage);
    }

    @Override
    public void saveSuccess(String successMessage) {
        ToastUtils.showToast(successMessage);
    }

    @Override
    public void saveError(String errorMessage) {
        ToastUtils.showToast(errorMessage);
    }

    @Override
    public void updateAllResult(String successMessage) {
        ToastUtils.showToast(successMessage);
    }

    private void showDia(final int position) {
        if (MyApplication.isShowDel) {
            CommonUtil.showDialog(mContext, getResources().getString(R.string.is_confirm_del), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //把删除的类保存到回收站类里
                    NotesBean notesBean = mList.get(position);
                    DelNotesBean bean = new DelNotesBean();
                    bean.setObjectId(notesBean.getObjectId());
                    bean.setContent(notesBean.getContent());
                    bean.setDate(notesBean.getDate());
                    bean.setTime(notesBean.getTime());
                    bean.save();

                    presenter.del(position, mList.get(position).getId());
                }
            });
        } else {
            NotesBean notesBean = mList.get(position);
            DelNotesBean bean = new DelNotesBean();
            bean.setObjectId(notesBean.getObjectId());
            bean.setContent(notesBean.getContent());
            bean.setDate(notesBean.getDate());
            bean.setTime(notesBean.getTime());
            bean.save();

            presenter.del(position, mList.get(position).getId());
        }

    }

    @Override
    public void delSuccess(int position) {
        mList.clear();
        mList = DataSupport.select("date", "content", "time", "objectId").order("date desc").find(NotesBean.class);
        ToastUtils.showToast(getResources().getString(R.string.del_success));
        menuAdapter.getDataList().remove(position);
        menuAdapter.notifyItemRemoved(position);
        if (position != menuAdapter.getDataList().size()) {
            menuAdapter.notifyItemRangeChanged(position, menuAdapter.getDataList().size() - position);
        }
    }

    @Override
    public void delError(String errorMessage) {
        ToastUtils.showToast(errorMessage);
    }

    @Override
    public void loginSuccess(String successMessage) {
//        ToastUtils.showToast(successMessage);
        LogUtil(successMessage);
    }

    @Override
    public void loginError(String errorMessage) {
        ToastUtils.showToast(errorMessage);
    }

    @Override
    public void showLoading() {
        syn_iv.startAnimation(animation);
    }

    @Override
    public void hideLoading() {
        animation.cancel();
    }
}
