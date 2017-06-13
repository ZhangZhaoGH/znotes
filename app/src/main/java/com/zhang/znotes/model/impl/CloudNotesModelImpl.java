package com.zhang.znotes.model.impl;

import android.text.TextUtils;

import com.zhang.znotes.MyApplication;
import com.zhang.znotes.R;
import com.zhang.znotes.base.BaseModel;
import com.zhang.znotes.bean.bmob.NoteBmob;
import com.zhang.znotes.bean.litepal.NotesBean;
import com.zhang.znotes.model.ICloudNotesModel;
import com.zhang.znotes.utils.LogUtil;

import org.litepal.crud.DataSupport;

import java.util.List;

import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by zz on 2017/3/26.
 */

public class CloudNotesModelImpl implements ICloudNotesModel{
    @Override
    public void delAll(List<BmobObject> delNotes, final AsyncCallback callback) {
        List<NotesBean> mList = DataSupport.select("date", "content", "time", "objectId").order("date desc").find(NotesBean.class);
        for (BmobObject bmob : delNotes) {
            for (NotesBean bean:mList) {
                if (bmob.getObjectId().equals(bean.getObjectId())){
                    NotesBean bean1=new NotesBean();
                    bean1.setObjectId("");
                    bean1.update(bean.getId());
                }
            }
        }

        new BmobBatch().deleteBatch(delNotes).doBatch(new QueryListListener<BatchResult>() {

            @Override
            public void done(List<BatchResult> o, BmobException e) {
                if(e==null){
                    int count = 0;
                    StringBuilder stringBuilder = new StringBuilder();
                    for(int i=0;i<o.size();i++){
                        BatchResult result = o.get(i);
                        BmobException ex =result.getError();
                        if(ex==null){
                            LogUtil.e("第"+i+"个数据批量删除成功");
                        }else{
                            count++;
                            stringBuilder=stringBuilder.append(ex.getMessage()+",");
                            LogUtil.e("第"+i+"个数据批量删除失败："+ex.getMessage()+","+ex.getErrorCode());
                        }
                    }
                    if (count == 0){
                        callback.onSuccess(MyApplication.getContext().getResources().getString(R.string.del_all_success));
                    }else {
                        callback.onSuccess("有"+count+"个数据删除失败: "+stringBuilder.toString());
                    }
                }else{
                    callback.onError(MyApplication.getContext().getResources().getString(R.string.del_all_error) + "："+e.getMessage());
                    LogUtil.e("CloudNotesModelImpl","失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }

    @Override
    public void del(String id, final AsyncCallback callback) {
        List<NotesBean> mList = DataSupport.select("date", "content", "time", "objectId").order("date desc").find(NotesBean.class);
        for (NotesBean bean:mList) {
            if (id.equals(bean.getObjectId())){
                NotesBean bean1=new NotesBean();
                bean1.setObjectId("");
                bean1.update(bean.getId());
            }
        }

        NoteBmob noteBmob=new NoteBmob();
        noteBmob.setObjectId(id);
        noteBmob.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    callback.onSuccess(MyApplication.getContext().getResources().getString(R.string.del_success));
                }else{
                    callback.onError(MyApplication.getContext().getResources().getString(R.string.del_error) + "："+e.getMessage());
                    LogUtil.e("云删除","失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });

    }
}
