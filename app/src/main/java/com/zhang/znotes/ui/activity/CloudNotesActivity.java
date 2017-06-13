package com.zhang.znotes.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
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

import com.mingle.widget.ShapeLoadingDialog;
import com.zhang.znotes.MyApplication;
import com.zhang.znotes.R;
import com.zhang.znotes.base.BaseActivity;
import com.zhang.znotes.bean.bmob.MyUser;
import com.zhang.znotes.bean.bmob.NoteBmob;
import com.zhang.znotes.bean.litepal.NotesBean;
import com.zhang.znotes.presenter.impl.CloudNotesPresenterImpl;
import com.zhang.znotes.ui.adapter.SwipeMenuAdapter;
import com.zhang.znotes.ui.view.DividerItemDecoration;
import com.zhang.znotes.utils.CommonUtil;
import com.zhang.znotes.utils.DateKit;
import com.zhang.znotes.utils.KeyBoardUtils;
import com.zhang.znotes.utils.LogUtil;
import com.zhang.znotes.utils.ToastUtils;
import com.zhang.znotes.view.ICloudNotesView;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class CloudNotesActivity extends BaseActivity implements ICloudNotesView{
    private EditText mEt;
    private RecyclerView mRecyclerView;
    private Context mContext;
    private Toolbar mToolbar;
    private List<NotesBean> mList;
    private List<NotesBean> etList;
    private List<BmobObject> bmobList;
    private SwipeMenuAdapter menuAdapter;
    private CloudNotesPresenterImpl presenter;
    private ImageView syn,commit,cancleTitle,allChoose;
    private ShapeLoadingDialog dialog;
    private RelativeLayout titleRl;
    private LinearLayout commitRl;
    private TextView chooseTitle;
    private Animation topInAnim,bottomInAnim;
    private Animation topOutAnim,bottomOutAnim;
    private int chooseNum = 0;


    @Override
    public void doBusiness(Context mContext) {
        getNotes();
    }

    private void getNotes() {
        mList.clear();
        etList.clear();
        dialog.show();
        MyUser user=BmobUser.getCurrentUser(MyUser.class);
        if (user != null) {
            BmobQuery<NoteBmob> query=new BmobQuery<>();
            query.addWhereEqualTo("userEmail",user.getEmail());
            query.setLimit(999);
            query.order("-updatedAt");
            query.findObjects(new FindListener<NoteBmob>() {
                @Override
                public void done(List<NoteBmob> list, BmobException e) {
                    if(e==null){
                        NotesBean bean;
                        for (NoteBmob bmob : list) {
                            //2017-03-25 22:51:16
                            String updatedAt = bmob.getUpdatedAt();
                            long mm=DateKit.strToMm(updatedAt);
                            Date date=new Date(mm);

                            String week= DateKit.strToWeek(updatedAt);
                            String[] split = updatedAt.split(" ");
                            String time=split[1].substring(0,split[1].length()-3);

                            bean=new NotesBean();
                            bean.setDate(date);
                            bean.setContent(bmob.getContent());
                            bean.setObjectId(bmob.getObjectId());
                            bean.setTime(split[0]+" "+week + " "+time);
                            mList.add(bean);
                            etList.add(bean);
                            LogUtil(bmob.getContent()+","+bmob.getUpdatedAt()+","+bmob.getCreatedAt());
                            LogUtil(bean.toString());
                        }
                        initData();
                        dialog.dismiss();
                    }else{
                        dialog.dismiss();
                        ToastUtils.showToast("查询失败："+e.getMessage());
                        LogUtil.e("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                    }
                }
            });
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (titleRl.getVisibility() == View.VISIBLE){
                    hideOption();
                    return true;
                }
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    private void initData() {
        if (menuAdapter == null) {
            menuAdapter = new SwipeMenuAdapter(mContext,2);
            menuAdapter.setDataList(mList);
            menuAdapter.setmOnItemClickListener(new SwipeMenuAdapter.AdapterItemClick() {
                @Override
                public void onItemClck(View view, int position) {

                    if (titleRl.getVisibility() == View.VISIBLE){
                        //批量选项显示
                        String objId = mList.get(position).getObjectId();
                        NoteBmob bmobObject=new NoteBmob();
                        bmobObject.setObjectId(objId);
                        LogUtil("objId="+objId);

                        if (bmobList.size()<1){
                            chooseNum++;
                            bmobList.add(bmobObject);
                            LogUtil("集合小于1 直接添加");
                        }else {
                            boolean isContain = false; // false 不包含 true则包含
                            int index = 0;
                            for (int i = 0; i < bmobList.size(); i++) {
                                if (bmobList.get(i).getObjectId().equals(objId)){
                                    isContain=true;
                                    index=i;
                                    LogUtil("包含 更改isContain状态="+isContain);
                                }
                            }
                            if (isContain){
                                chooseNum--;
                                LogUtil("移除 bmobObject="+bmobObject);
                                bmobList.remove(bmobList.get(index));
                            }else {
                                chooseNum++;
                                bmobList.add(bmobObject);
                                LogUtil("添加 bmobObject="+bmobObject);
                            }
                        }

                        chooseTitle.setText("已选择 "+chooseNum+ " 项");
                        menuAdapter.notifyItemChanged(position,position);
                    }

                    if (titleRl.getVisibility() == View.GONE){
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
                public void onDel( int position) {
                    showDia(position);
                }

                @Override
                public void onSyn(int position) {
                }
            });

            mRecyclerView.setAdapter(menuAdapter);
        } else {
            menuAdapter.setDataList(mList);
            menuAdapter.notifyDataSetChanged();
        }
    }

    private void showDia(final int position) {
        if (MyApplication.isShowDel){
            CommonUtil.showDialog(mContext, getResources().getString(R.string.is_confirm_cloud_del), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    presenter.del(position,mList.get(position).getObjectId());
                    LogUtil(mList.get(position).getContent());
                }
            });
        }else {
            presenter.del(position,mList.get(position).getObjectId());
        }

    }

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        return R.layout.activity_cloud_notes;
    }

    @Override
    public void setPresenter() {
        presenter=new CloudNotesPresenterImpl(this);
    }

    @Override
    public void initView(View view) {
        mContext=this;
        dialog=new ShapeLoadingDialog(this);
        dialog.setLoadingText(getResources().getString(R.string.loading));
        dialog.setCanceledOnTouchOutside(false);
        mList=new ArrayList<>();
        etList=new ArrayList<>();
        bmobList=new ArrayList<>();
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        mToolbar.setBackgroundColor(ContextCompat.getColor(mContext,R.color.color_dark_blue));
        mToolbar.setTitle(getResources().getString(R.string.cloud_title));
        setSupportActionBar(mToolbar);
        syn = (ImageView) view.findViewById(R.id.syn_iv);
        syn.setImageResource(R.mipmap.del);
        syn.setOnClickListener(this);
        syn.setVisibility(View.VISIBLE);
        mEt= (EditText) view.findViewById(R.id.search_et);
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
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                KeyBoardUtils.closeKeybord(mEt,mContext);
            }
        });
        mRecyclerView= (RecyclerView) view.findViewById(R.id.search_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        titleRl= (RelativeLayout) view.findViewById(R.id.choose_title_rl);
        commitRl= (LinearLayout) view.findViewById(R.id.commit_ll);
        chooseTitle= (TextView) view.findViewById(R.id.check_title);
        cancleTitle= (ImageView) view.findViewById(R.id.cancle_title);
        allChoose= (ImageView) view.findViewById(R.id.all_choose_iv);
        allChoose.setOnClickListener(this);
        cancleTitle.setOnClickListener(this);
        commit= (ImageView) view.findViewById(R.id.commit_iv);
        commit.setOnClickListener(this);

        topInAnim = AnimationUtils.loadAnimation(this,R.anim.alpha_top_in);
        bottomInAnim =AnimationUtils.loadAnimation(this,R.anim.alpha_bottom_in);
        topOutAnim = AnimationUtils.loadAnimation(this,R.anim.alpha_top_out);
        bottomOutAnim =AnimationUtils.loadAnimation(this,R.anim.alpha_bottom_out);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (titleRl.getVisibility() == View.VISIBLE){
            hideOption();
        }

    }

    @Override
    public void widgetClick(View v) {
        switch (v.getId()){
            case R.id.syn_iv:
                //"点击删除图标"
                titleRl.setVisibility(View.VISIBLE);
                commitRl.setVisibility(View.VISIBLE);
                titleRl.startAnimation(topInAnim);
                commitRl.startAnimation(bottomInAnim);
                if (menuAdapter!=null){
                    menuAdapter.setChooseType(1);
                }

                break;
            case R.id.cancle_title:
                hideOption();
                break;
            case R.id.commit_iv:
                if (bmobList.size() < 1){
                    ToastUtils.showToast("最少需要选择1个");
                    return;
                }
                if (MyApplication.isShowDel){
                    CommonUtil.showDialog(mContext,getResources().getString(R.string.is_confirm_cloud_del), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //确定删除
                            delAll();
                        }
                    });
                }else {
                    delAll();
                }


                break;
            case R.id.all_choose_iv:
                //全选
                ToastUtils.showToast("一次最多只能选50");
                if (allChoose.getDrawable().getCurrent().getConstantState()
                        == ContextCompat.getDrawable(mContext,R.mipmap.all_choose).getConstantState()){
                    allChoose.setImageResource(R.mipmap.no_all_choose);
                }else {
                    allChoose.setImageResource(R.mipmap.all_choose);
                }


                break;
            default:
                break;
        }
    }

    /**
     * 隐藏选项卡
     */
    private void hideOption() {
        titleRl.startAnimation(topOutAnim);
        commitRl.startAnimation(bottomOutAnim);
        titleRl.setVisibility(View.GONE);
        commitRl.setVisibility(View.GONE);
        if (menuAdapter != null){
            chooseNum=0;
            bmobList.clear();
            menuAdapter.setChooseType(4);
            chooseTitle.setText("已选择 0 项");
        }
    }

    private void delAll() {
        presenter.delAll(bmobList);
    }

    private TextWatcher textwatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            String content = mEt.getText().toString();
            mList.clear();
            if (!TextUtils.isEmpty(content)) {
                for (NotesBean bean : etList) {
                    if (bean.getContent().contains(content)) {
                        mList.add(bean);
                    }
                }
                initData();
            } else {
                for (NotesBean bean : etList) {
                    mList.add(bean);
                }
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
    public void showLoading() {
        dialog.show();
    }

    @Override
    public void hideLoading() {
        dialog.dismiss();
    }

    @Override
    public void delAllSuccess(List<BmobObject> inserNotes,String successMessage) {
        ToastUtils.showToast(successMessage);
        getNotes();
        hideOption();
    }

    @Override
    public void delSuccess(int position) {
        getNotes();
        ToastUtils.showToast(getResources().getString(R.string.del_success));
    }

    @Override
    public void delError(String errorMessage) {
        ToastUtils.showToast(errorMessage);
    }

    @Override
    public void delAllError(List<BmobObject> inserNotes,String errorMessage) {
        ToastUtils.showToast(errorMessage);
        hideOption();
    }
}
