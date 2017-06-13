package com.zhang.znotes.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.zhang.znotes.R;
import com.zhang.znotes.ui.view.DividerItemDecoration;
import com.zhang.znotes.ui.adapter.SwipeMenuAdapter;
import com.zhang.znotes.base.BaseActivity;
import com.zhang.znotes.bean.litepal.NotesBean;
import com.zhang.znotes.utils.ToastUtils;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class ScrollingActivity extends BaseActivity {
    private RecyclerView mRecyclerView;
    private SwipeMenuAdapter menuAdapter;
    private List<NotesBean> mList;
    private CollapsingToolbarLayout toolbar_layout;
    private BottomSheetBehavior<View> behavior;
    private BottomSheetDialog bsDialog;

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        return R.layout.activity_scrolling;
    }

    @Override
    public void setPresenter() {

    }

    @Override
    public void initView(View view) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//                    bsDialog.show();
                int state = behavior.getState();
                if (state == BottomSheetBehavior.STATE_EXPANDED){
                    behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }else {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
        });
        mRecyclerView = (RecyclerView) view.findViewById(R.id.main_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mList = new ArrayList<>();
        mList = DataSupport.select("date", "content", "time").order("date desc").find(NotesBean.class);
        LogUtil(mList.size() + "=size");
        if (menuAdapter == null) {
            menuAdapter = new SwipeMenuAdapter(this,1);
            menuAdapter.setDataList(mList);
            menuAdapter.setmOnItemClickListener(new SwipeMenuAdapter.AdapterItemClick() {
                @Override
                public void onItemClck(View view, int position) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("bean", mList.get(position));
                    startActivity(CreatNoteActivity.class, bundle);
                }

                @Override
                public void onItemLongClck(View view, int position) {

                }
            });
            menuAdapter.setOnSwipeListener(new SwipeMenuAdapter.onSwipeListener() {
                @Override
                public void onDel(final int position) {
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

        toolbar_layout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        AppBarLayout app_bar = (AppBarLayout) findViewById(R.id.app_bar);
        app_bar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset == 0) {
                    if (state != CollapsingToolbarLayoutState.EXPANDED) {
                        state = CollapsingToolbarLayoutState.EXPANDED;//修改状态标记为展开
//                        toolbar_layout.setTitle("EXPANDED");//设置title为EXPANDED
                    }

                } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                    if (state != CollapsingToolbarLayoutState.COLLAPSED) {
                        toolbar_layout.setTitle("防火女");//设置title不显示
//                        playButton.setVisibility(View.VISIBLE);//隐藏播放按钮
                        state = CollapsingToolbarLayoutState.COLLAPSED;//修改状态标记为折叠
                    }
                } else {
                    if (state != CollapsingToolbarLayoutState.INTERNEDIATE) {
                        if (state == CollapsingToolbarLayoutState.COLLAPSED) {
//                            playButton.setVisibility(View.GONE);//由折叠变为中间状态时隐藏播放按钮
                        }
//                        toolbar_layout.setTitle("INTERNEDIATE");//设置title为INTERNEDIATE
                        state = CollapsingToolbarLayoutState.INTERNEDIATE;//修改状态标记为中间
                    }
                }
            }
        });

        View bottomSheet = view.findViewById(R.id.bottom_sheet);
        behavior = BottomSheetBehavior.from(bottomSheet);


//        bsDialog = new BottomSheetDialog(this);
//        bsDialog.setContentView(R.layout.include_bottom_sheet_layout);
//        bsDialog.show();
    }

    private CollapsingToolbarLayoutState state;

    private enum CollapsingToolbarLayoutState {
        EXPANDED,
        COLLAPSED,
        INTERNEDIATE
    }


    @Override
    public void widgetClick(View v) {

    }

    /**
     * 显示删除对话框
     *
     * @param position
     */
    private void showDia(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("确定删除吗?");
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int delete = DataSupport.delete(NotesBean.class, mList.get(position).getId());
                //delete 删除个数
                if (1 == delete) {
                    mList.clear();
                    mList = DataSupport.select("date", "content", "time").order("date desc").find(NotesBean.class);
                    ToastUtils.showToast("删除成功");
                }
                menuAdapter.remove(position);
                menuAdapter.notifyItemRemoved(position);
                if (position != menuAdapter.getDataList().size()) {
                    // 如果移除的是最后一个，忽略
                    menuAdapter.notifyItemRangeChanged(position, menuAdapter.getDataList().size() - 1);
                }

            }
        });
        builder.show();
    }
}
