package com.zhang.znotes.ui.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhang.znotes.R;
import com.zhang.znotes.ui.view.SwipeMenuView;
import com.zhang.znotes.base.ListBaseAdapter;
import com.zhang.znotes.base.SuperViewHolder;
import com.zhang.znotes.bean.litepal.NotesBean;
import com.zhang.znotes.utils.DateKit;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by zz on 2017/3/1.
 */

public class SwipeMenuAdapter extends ListBaseAdapter<NotesBean>  {
    // 1是本地 2是云
    private int type;
    // 1是显示 3是没勾选 2是隐藏 4是隐藏并且设置图标没勾选
    private int chooseType;

    public SwipeMenuAdapter(Context context,int type) {
        super(context);
        this.type=type;
    }

    /**
     * 1是显示 3是没勾选 2是隐藏 4是隐藏并且设置图标没勾选
     * @param typeImg
     */
    public void setChooseType(int typeImg) {
        this.chooseType=typeImg;
        notifyDataSetChanged();
    }


    @Override
    public int getLayoutId() {
        return R.layout.item_main_notes;
    }


    @Override
    public void onBindItemHolder(final SuperViewHolder holder, final int position) {
        View contentView = holder.getView(R.id.item_main_menu);
        Button btnDelete = holder.getView(R.id.btnDelete);
        Button btn_syn=holder.getView(R.id.btn_syn);
        TextView summary = holder.getView(R.id.item_main_summary);
        TextView time =  holder.getView(R.id.item_main_time);
        final ImageView chooseTypeImg=holder.getView(R.id.choose_type_iv);

            //这句话关掉IOS阻塞式交互效果 并打开右滑退出
            ((SwipeMenuView)holder.itemView).setIos(false).setLeftSwipe(true);
        if (type == 2){
            btn_syn.setVisibility(View.GONE);
        }

        switch (chooseType){
            case 1:
                chooseTypeImg.setVisibility(View.VISIBLE);
                break;
            case 3:
                chooseTypeImg.setImageResource(R.mipmap.no_choose);
                break;
            case 2:
                chooseTypeImg.setVisibility(View.GONE);
                break;
            case 4:
                chooseTypeImg.setVisibility(View.GONE);
                chooseTypeImg.setImageResource(R.mipmap.no_choose);
                break;
            default:
                break;
        }


        NotesBean bean=getDataList().get(position);
        summary.setText(bean.getContent());
        Date date=bean.getDate();
        String times= DateKit.showTime(date);
        time.setText(times);
        if (mOnItemClickListener!=null){
            contentView.setOnClickListener(new View.OnClickListener() {
                boolean isChange;
                @Override
                public void onClick(View view) {
                    mOnItemClickListener.onItemClck(view,position);
                    if (View.VISIBLE == chooseTypeImg.getVisibility()){
                        isChange=!isChange;
                        if (isChange){
                            chooseTypeImg.setImageResource(R.mipmap.choose);
                        }else {
                            chooseTypeImg.setImageResource(R.mipmap.no_choose);
                        }
                    }

                }
            });
        }
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnSwipeListener!=null){
                    mOnSwipeListener.onDel(position);
                }
                ((SwipeMenuView) holder.itemView).smoothClose();
            }
        });

        btn_syn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnSwipeListener!=null){
                    mOnSwipeListener.onSyn(position);
                }
                ((SwipeMenuView) holder.itemView).smoothClose();
            }
        });

    }

    public interface AdapterItemClick   {
        void onItemClck(View view, int position);

        void onItemLongClck(View view, int position);
    }
    private AdapterItemClick mOnItemClickListener;
    public void setmOnItemClickListener(AdapterItemClick mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }


    /**
     * 和activity通信接口
     */
    public interface onSwipeListener{
        void onDel(int position);

        void onSyn(int position);
    }
    private onSwipeListener mOnSwipeListener;

    public void setOnSwipeListener(onSwipeListener mOnSwipeListener){
        this.mOnSwipeListener=mOnSwipeListener;
    }
}
