package com.zhang.znotes.presenter.impl;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.zhang.znotes.R;
import com.zhang.znotes.base.BaseModel;
import com.zhang.znotes.bean.litepal.NotesBean;
import com.zhang.znotes.model.IMainModel;
import com.zhang.znotes.model.impl.MainModerlImpl;
import com.zhang.znotes.presenter.IMainPresenter;
import com.zhang.znotes.utils.ToastUtils;
import com.zhang.znotes.view.IMainView;

import org.litepal.crud.DataSupport;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by zz on 2017/3/25.
 */

public class MainPresenterImpl implements IMainPresenter{
    private IMainModel model;
    private IMainView mainView;
    private static final int  UPDATE_NOTES=1;
    private int count=0;
    private StringBuilder stringBuilder=new StringBuilder();
    private Handler hander=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case UPDATE_NOTES:
                    count++;
                    Log.e("MainPresenterImpl","count="+count);
                    String message= (String) msg.obj;
                    if (!stringBuilder.toString().equals(message)){
                        stringBuilder.append(message);
                    }
                    if (count == 2){
                        count=0;
                        mainView.updateAllResult(stringBuilder.toString());
                        mainView.hideLoading();
                        stringBuilder=stringBuilder.delete(0,stringBuilder.length());
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public MainPresenterImpl(IMainView mainView) {
        this.mainView = mainView;
        model=new MainModerlImpl();
    }

    @Override
    public void onDestroy() {
        mainView=null;
    }

    @Override
    public void save(NotesBean bean, String eamil) {
        mainView.showLoading();
        model.save(bean, eamil, new BaseModel.AsyncCallback() {
            @Override
            public void onSuccess(Object success) {
                String msg= (String) success;
                mainView.saveSuccess(msg);
                mainView.hideLoading();
            }

            @Override
            public void onError(Object error) {
                String msg= (String) error;
                mainView.saveError(msg);
                mainView.hideLoading();
            }
        });
    }

    @Override
    public void update(NotesBean bean) {
        mainView.showLoading();
        model.update(bean, new BaseModel.AsyncCallback() {
            @Override
            public void onSuccess(Object success) {
                String msg= (String) success;
                mainView.updateSuccess(msg);
                mainView.hideLoading();
            }

            @Override
            public void onError(Object error) {
                String msg= (String) error;
                mainView.updateError(msg);
                mainView.hideLoading();
            }
        });
    }

    @Override
    public void saveAll(List<BmobObject> inserNotes, List<NotesBean> inserList) {
        mainView.showLoading();
        model.saveAll(inserNotes, inserList, new BaseModel.AsyncCallback() {
            @Override
            public void onSuccess(Object success) {
                String msg= (String) success;
                Message message = hander.obtainMessage();
                message.obj=msg;
                message.what=UPDATE_NOTES;
                hander.sendMessage(message);
            }

            @Override
            public void onError(Object error) {
                String msg= (String) error;
                Message message = hander.obtainMessage();
                message.obj=msg;
                message.what=UPDATE_NOTES;
                hander.sendMessage(message);
            }
        });
    }

    @Override
    public void updateAll(List<BmobObject> updateNotes) {
        mainView.showLoading();
        model.updateAll(updateNotes, new BaseModel.AsyncCallback() {
            @Override
            public void onSuccess(Object success) {
                String msg= (String) success;
                Message message = hander.obtainMessage();
                message.obj=msg;
                message.what=UPDATE_NOTES;
                hander.sendMessage(message);
            }

            @Override
            public void onError(Object error) {
                String msg= (String) error;
                Message message = hander.obtainMessage();
                message.obj=msg;
                message.what=UPDATE_NOTES;
                hander.sendMessage(message);
            }
        });
    }

    @Override
    public void del(int position,int id) {
        mainView.showLoading();
        model.del(position,id, new BaseModel.AsyncCallback() {
            @Override
            public void onSuccess(Object success) {
                int msg= (int) success;
                mainView.delSuccess(msg);
                mainView.hideLoading();
            }

            @Override
            public void onError(Object error) {
                String msg= (String) error;
                mainView.delError(msg);
                mainView.hideLoading();
            }
        });
    }

    @Override
    public void delAll(List<NotesBean> delList) {
        model.delAll(delList, new BaseModel.AsyncCallback() {
            @Override
            public void onSuccess(Object success) {
                String msg= (String) success;
                mainView.delAllSuccess(msg);
            }

            @Override
            public void onError(Object error) {
                String msg= (String) error;
                mainView.delAllError(msg);
            }
        });
    }

    @Override
    public void login(String account, String password) {
        mainView.showLoading();
        model.login(account, password, new BaseModel.AsyncCallback() {
            @Override
            public void onSuccess(Object success) {
                String msg= (String) success;
                mainView.loginSuccess(msg);
                mainView.hideLoading();
            }

            @Override
            public void onError(Object error) {
                String msg= (String) error;
                mainView.loginError(msg);
                mainView.hideLoading();
            }
        });
    }
}
