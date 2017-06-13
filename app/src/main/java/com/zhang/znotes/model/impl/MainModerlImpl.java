package com.zhang.znotes.model.impl;


import com.zhang.znotes.MyApplication;
import com.zhang.znotes.R;
import com.zhang.znotes.base.BaseModel;
import com.zhang.znotes.bean.bmob.MyUser;
import com.zhang.znotes.bean.bmob.NoteBmob;
import com.zhang.znotes.bean.litepal.DelNotesBean;
import com.zhang.znotes.bean.litepal.NotesBean;
import com.zhang.znotes.model.IMainModel;
import com.zhang.znotes.service.UpdateInfoEvent;
import com.zhang.znotes.utils.LogUtil;
import com.zhang.znotes.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.litepal.crud.DataSupport;

import java.util.List;

import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by zz on 2017/3/25.
 */

public class MainModerlImpl implements IMainModel {
    @Override
    public void save(final NotesBean bean, String eamil, final AsyncCallback callback) {
        NoteBmob noteBmob = new NoteBmob();
        noteBmob.setContent(bean.getContent());
        noteBmob.setUserEmail(eamil);

        noteBmob.save(new SaveListener<String>() {
            @Override
            public void done(String objectId, BmobException e) {
                if (e == null) {
                    bean.setObjectId(objectId);
                    bean.update(bean.getId());
                    callback.onSuccess(MyApplication.getContext().getResources().getString(R.string.sava_success));
                } else {
                    callback.onError(MyApplication.getContext().getResources().getString(R.string.sava_error) + ": " + e.getMessage());
                    LogUtil.e("MainModerlImpl", "添加失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }

    @Override
    public void update(NotesBean bean, final AsyncCallback callback) {
        NoteBmob noteBmob = new NoteBmob();
        noteBmob.setContent(bean.getContent());
        noteBmob.update(bean.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    callback.onSuccess(MyApplication.getContext().getResources().getString(R.string.update_success));
                } else {
                    callback.onError(MyApplication.getContext().getResources().getString(R.string.update_error) + ": " + e.getMessage());
                    LogUtil.e("MainModerlImpl", "更新失败：" + e.getMessage() + "," + e.getErrorCode());
                }

            }
        });
    }

    @Override
    public void saveAll(List<BmobObject> inserNotes, final List<NotesBean> inserList, final AsyncCallback callback) {
        new BmobBatch().insertBatch(inserNotes).doBatch(new QueryListListener<BatchResult>() {

            @Override
            public void done(List<BatchResult> o, BmobException e) {
                if (e == null) {
                    int count = 0;
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < o.size(); i++) {
                        BatchResult result = o.get(i);
                        BmobException ex = result.getError();
                        NotesBean bean = inserList.get(i);
                        if (ex == null) {
                            LogUtil.e("MainModerlImpl", "第" + i + "个数据批量添加成功：" + result.getCreatedAt() + "," + result.getObjectId() + "," + result.getUpdatedAt());
                            bean.setObjectId(result.getObjectId());
                            bean.update(bean.getId());
                        } else {
                            count++;
                            stringBuilder = stringBuilder.append(ex.getMessage() + ",");
                            LogUtil.e("MainModerlImpl", "第" + i + "个数据批量添加失败：" + ex.getMessage() + "," + ex.getErrorCode());
                        }
                    }
                    if (count == 0) {
                        callback.onSuccess(MyApplication.getContext().getResources().getString(R.string.sava_all_success));
                    } else {
                        callback.onSuccess("有" + count + "个数据上传失败: " + stringBuilder.toString());
                    }
                } else {
                    if (9005 != e.getErrorCode()) {
                        // 9005 表示添加数据少于1条
                        callback.onError(MyApplication.getContext().getResources().getString(R.string.sava_error));
                        LogUtil.e("MainModerlImpl", "saveAll失败：" + e.getMessage() + "," + e.getErrorCode());
                    } else {
                        callback.onSuccess(MyApplication.getContext().getResources().getString(R.string.sava_all_success));
                    }
                }
            }
        });
    }

    @Override
    public void updateAll(List<BmobObject> updateNotes, final AsyncCallback callback) {
        if (updateNotes.size() == 0) {
            callback.onSuccess("");
            return;
        }
        new BmobBatch().updateBatch(updateNotes).doBatch(new QueryListListener<BatchResult>() {

            @Override
            public void done(List<BatchResult> o, BmobException e) {
                if (e == null) {
                    int count = 0;
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < o.size(); i++) {
                        BatchResult result = o.get(i);
                        BmobException ex = result.getError();
                        if (ex == null) {
                            LogUtil.e("MainModerlImpl", "第" + i + "个数据批量更新成功：" + result.getUpdatedAt());
                        } else {
                            count++;
                            stringBuilder = stringBuilder.append(ex.getMessage() + ",");
                            LogUtil.e("MainModerlImpl", "第" + i + "个数据批量更新失败：" + ex.getMessage() + "," + ex.getErrorCode());
                        }
                    }
                    if (count == 0) {
                        callback.onSuccess(MyApplication.getContext().getResources().getString(R.string.sava_all_success));
                    } else {
                        callback.onSuccess("有" + count + "个数据上传失败: " + stringBuilder.toString());
                    }
                } else {
                    callback.onError(MyApplication.getContext().getResources().getString(R.string.update_error));
                    LogUtil.e("MainModerlImpl", "updateAll失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }

    @Override
    public void del(int position, int id, AsyncCallback callback) {
        int delete = DataSupport.delete(NotesBean.class, id);
        //delete 删除个数
        if (1 == delete) {
            callback.onSuccess(position);
        } else {
            callback.onError(MyApplication.getContext().getResources().getString(R.string.del_error));
        }
    }

    @Override
    public void delAll(List<NotesBean> delList, AsyncCallback callback) {
        int count = 0;
        for (NotesBean note : delList) {
            DelNotesBean bean = new DelNotesBean();
            bean.setObjectId(note.getObjectId());
            bean.setContent(note.getContent());
            bean.setDate(note.getDate());
            bean.setTime(note.getTime());
            bean.save();
            int deleteNum = DataSupport.delete(NotesBean.class, note.getId());
            count += deleteNum;
        }

        if (count == delList.size()) {
            callback.onSuccess(MyApplication.getContext().getResources().getString(R.string.del_all_success));
        } else {
            int fail = delList.size() - count;
            callback.onError("有 " + fail + "条数据删除失败");
        }
    }


    @Override
    public void login(String account, String password, final AsyncCallback callback) {
        BmobUser user = new BmobUser();
        user.setUsername(account);
        user.setPassword(password);
        user.login(new SaveListener<MyUser>() {
            @Override
            public void done(MyUser userBean, BmobException e) {
                if (e == null) {
                    callback.onSuccess(MyApplication.getContext().getResources().getString(R.string.login_success));
                } else {
                    if (e.getErrorCode() == 101) {
                        callback.onError(MyApplication.getContext().getResources().getString(R.string.login_error_101));
                    } else {
                        callback.onError(MyApplication.getContext().getResources().getString(R.string.login_error) + ", " + e);
                    }
                    BmobUser.logOut();
                    EventBus.getDefault().post(new UpdateInfoEvent());
                    LogUtil.e("MainModerlImpl", "登录失败:" + e);
                }
            }
        });
    }

}
