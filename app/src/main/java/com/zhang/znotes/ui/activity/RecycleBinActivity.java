package com.zhang.znotes.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.zhang.znotes.bean.bmob.NoteBmob;
import com.zhang.znotes.bean.litepal.DelNotesBean;
import com.zhang.znotes.bean.litepal.NotesBean;
import com.zhang.znotes.presenter.impl.CloudNotesPresenterImpl;
import com.zhang.znotes.ui.adapter.SwipeMenuAdapter;
import com.zhang.znotes.ui.view.DividerItemDecoration;
import com.zhang.znotes.utils.CommonUtil;
import com.zhang.znotes.utils.KeyBoardUtils;
import com.zhang.znotes.utils.ToastUtils;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;

public class RecycleBinActivity extends BaseActivity {
    private EditText mEt;
    private RecyclerView mRecyclerView;
    private Context mContext;
    //展示adaper用的集合
    private List<NotesBean> mList;
    //获取所有的删除信息集合
    private List<DelNotesBean> delNotesList;
    //搜索比对的集合
    private List<NotesBean> etList;
    //删除的集合
    private List<Integer> delList;
    private SwipeMenuAdapter menuAdapter;
    private Toolbar mToolbar;
    private RelativeLayout titleRl;
    private LinearLayout commitRl;
    private TextView chooseTitle;
    private ImageView syn,commit,cancleTitle,allChoose;
    private Animation topInAnim, bottomInAnim;
    private Animation topOutAnim, bottomOutAnim;
    private int chooseNum = 0;

    @Override
    public void doBusiness(Context mContext) {
        getNotes();
        setMenuAdapter();
    }

    private void getNotes() {
        delNotesList.clear();
        mList.clear();
        etList.clear();
        delNotesList = DataSupport.select("date", "content", "time", "objectId", "id").order("date desc").find(DelNotesBean.class);
        for (DelNotesBean bean : delNotesList) {
            NotesBean note = new NotesBean();
            note.setObjectId(bean.getObjectId());
            note.setDate(bean.getDate());
            note.setContent(bean.getContent());
            note.setTime(bean.getTime());
//            note.setId(bean.getId());
            mList.add(note);
            etList.add(note);
        }
        LogUtil("集合size=" + mList.size() + ",delList=" + delList.size());
        LogUtil("集合size=" + mList.toString());
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
    }

    @Override
    public void initView(View view) {
        mContext = this;
        etList = new ArrayList<>();
        mList = new ArrayList<>();
        delNotesList = new ArrayList<>();
        delList = new ArrayList<>();
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        mToolbar.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_oragne));
        mToolbar.setTitle(getResources().getString(R.string.recycle_title));
        setSupportActionBar(mToolbar);
        syn = (ImageView) view.findViewById(R.id.syn_iv);
        syn.setImageResource(R.mipmap.del);
        syn.setOnClickListener(this);
        syn.setVisibility(View.VISIBLE);
        mEt = (EditText) view.findViewById(R.id.search_et);
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
                KeyBoardUtils.closeKeybord(mEt, mContext);
            }
        });
        mRecyclerView = (RecyclerView) view.findViewById(R.id.search_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        titleRl = (RelativeLayout) view.findViewById(R.id.choose_title_rl);
        commitRl = (LinearLayout) view.findViewById(R.id.commit_ll);
        chooseTitle= (TextView) view.findViewById(R.id.check_title);
        cancleTitle= (ImageView) view.findViewById(R.id.cancle_title);
        allChoose= (ImageView) view.findViewById(R.id.all_choose_iv);
        allChoose.setOnClickListener(this);
        cancleTitle.setOnClickListener(this);
        commit= (ImageView) view.findViewById(R.id.commit_iv);
        commit.setOnClickListener(this);

        topInAnim = AnimationUtils.loadAnimation(this, R.anim.alpha_top_in);
        bottomInAnim = AnimationUtils.loadAnimation(this, R.anim.alpha_bottom_in);
        topOutAnim = AnimationUtils.loadAnimation(this, R.anim.alpha_top_out);
        bottomOutAnim = AnimationUtils.loadAnimation(this, R.anim.alpha_bottom_out);
    }

    @Override
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.syn_iv:
                //"点击删除图标"
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
            case R.id.commit_iv:
                if (delList.size() < 1) {
                    ToastUtils.showToast("最少需要选择1个");
                    return;
                }
                if (MyApplication.isShowDel){
                    CommonUtil.showDialog(mContext, getResources().getString(R.string.is_confirm_recycle_del), new DialogInterface.OnClickListener() {
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

    private void delAll() {
        int count = 0;
        for (Integer id : delList) {
            int deleteNum = DataSupport.delete(DelNotesBean.class, id);
            count += deleteNum;
        }
        if (count == delList.size()){
            ToastUtils.showToast(getResources().getString(R.string.del_all_success));
        }else {
            int fail=delList.size() - count;
            ToastUtils.showToast("有 "+fail +"条数据删除失败");
        }

        hideOption();
        getNotes();
        setMenuAdapter();
    }

    private void setMenuAdapter() {
        if (menuAdapter == null) {
            menuAdapter = new SwipeMenuAdapter(mContext, 2);
            menuAdapter.setDataList(mList);
            menuAdapter.setmOnItemClickListener(new SwipeMenuAdapter.AdapterItemClick() {
                @Override
                public void onItemClck(View view, int position) {

                    if (titleRl.getVisibility() == View.VISIBLE) {
                        //批量选项显示
                        int objId = delNotesList.get(position).getId();

                        boolean isContain = false; // false 不包含 true则包含
                        for (int i = 0; i < delList.size(); i++) {
                            if (delList.get(i)==objId){
                                isContain=true;
                            }
                        }
                        if (isContain){
                            chooseNum--;
                            delList.remove((Integer) objId);
                        }else {
                            chooseNum++;
                            delList.add(objId);
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
                }
            });

            mRecyclerView.setAdapter(menuAdapter);
        } else {
            menuAdapter.setDataList(mList);
            menuAdapter.notifyDataSetChanged();
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

    private void showDia(final int position) {
        if (MyApplication.isShowDel){
            CommonUtil.showDialog(mContext, getResources().getString(R.string.is_confirm_recycle_del), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    int delete = DataSupport.delete(DelNotesBean.class, delNotesList.get(position).getId());
                    //delete 删除个数
                    LogUtil(delNotesList.get(position).getContent());
                    if (1 == delete) {
                        getNotes();
                        ToastUtils.showToast(getResources().getString(R.string.del_success));
                        menuAdapter.getDataList().remove(position);
                        menuAdapter.notifyItemRemoved(position);
                        if (position != menuAdapter.getDataList().size()) {
                            menuAdapter.notifyItemRangeChanged(position, menuAdapter.getDataList().size() - position);
                        }
                    } else {
                        ToastUtils.showToast(getResources().getString(R.string.del_error));
                    }
                }
            });
        }else {
            int delete = DataSupport.delete(DelNotesBean.class, delNotesList.get(position).getId());
            LogUtil(mList.get(position).getContent());
            if (1 == delete) {
                getNotes();
                ToastUtils.showToast(getResources().getString(R.string.del_success));
                menuAdapter.getDataList().remove(position);
                menuAdapter.notifyItemRemoved(position);
                if (position != menuAdapter.getDataList().size()) {
                    menuAdapter.notifyItemRangeChanged(position, menuAdapter.getDataList().size() - position);
                }
            } else {
                ToastUtils.showToast(getResources().getString(R.string.del_error));
            }
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
        if (menuAdapter != null) {
            chooseNum = 0;
            delList.clear();
            menuAdapter.setChooseType(4);
            chooseTitle.setText("已选择 0 项");
        }
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
                setMenuAdapter();
            } else {
                for (NotesBean bean : etList) {
                    mList.add(bean);
                }
                setMenuAdapter();
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
}
