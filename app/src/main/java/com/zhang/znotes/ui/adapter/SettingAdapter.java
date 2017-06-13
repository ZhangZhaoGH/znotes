package com.zhang.znotes.ui.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhang.znotes.MyApplication;
import com.zhang.znotes.R;
import com.zhang.znotes.base.ListBaseAdapter;
import com.zhang.znotes.base.SuperViewHolder;
import com.zhang.znotes.utils.SPUtils;

/**
 * Created by zz on 2017/3/30.
 */

public class SettingAdapter extends ListBaseAdapter<String> {
    private boolean promptDel;
    private Context context;
    

    public SettingAdapter(Context context) {
        super(context);
        this.context=context;
        promptDel= (boolean) SPUtils.get(context,SPUtils.PROMPT_DEL,true);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_setting;
    }

    public interface onItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view , int position);
    }

    private onItemClickListener mOnItemClickListener;

    public void setmOnItemClickListener(onItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    public void onBindItemHolder(SuperViewHolder holder, final int position) {
        final LinearLayout menu=holder.getView(R.id.item_setting_menu);
        final ImageView imgview=holder.getView(R.id.imgview);
        if ("删除确认提示".equals(getDataList().get(position))){
            if (promptDel){
                imgview.setImageResource(R.mipmap.choose);
            }else {
                imgview.setImageResource(R.mipmap.no_choose);
            }

        }else {
            imgview.setImageResource(R.mipmap.go);
        }
        TextView textView=holder.getView(R.id.content);
        textView.setText(getDataList().get(position));

        if (mOnItemClickListener!=null){
            menu.setOnClickListener(new View.OnClickListener() {
                boolean isChange;
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(menu,position);
                    if ("删除确认提示".equals(getDataList().get(position))){
                        isChange=!isChange;
                        if (isChange){
                            imgview.setImageResource(R.mipmap.choose);
                            MyApplication.isShowDel=true;
                            SPUtils.put(context,SPUtils.PROMPT_DEL,true);
                        }else {
                            imgview.setImageResource(R.mipmap.no_choose);
                            MyApplication.isShowDel=false;
                            SPUtils.put(context,SPUtils.PROMPT_DEL,false);
                        }
                    }
                }
            });
        }

    }

}
