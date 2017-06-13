package com.zhang.znotes.presenter.impl;

import com.zhang.znotes.base.BaseModel;
import com.zhang.znotes.model.impl.CloudNotesModelImpl;
import com.zhang.znotes.presenter.ICloudNotesPresenter;
import com.zhang.znotes.view.ICloudNotesView;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by zz on 2017/3/26.
 */

public class CloudNotesPresenterImpl implements ICloudNotesPresenter{
    private CloudNotesModelImpl model;
    private ICloudNotesView mView;

    public CloudNotesPresenterImpl(ICloudNotesView mView) {
        this.mView = mView;
        model=new CloudNotesModelImpl();
    }

    @Override
    public void onDestroy() {
        mView=null;
    }

    @Override
    public void delAll(final List<BmobObject> inserNotes) {
        mView.showLoading();
        model.delAll(inserNotes, new BaseModel.AsyncCallback() {
            @Override
            public void onSuccess(Object success) {
                String msg= (String) success;
                mView.delAllSuccess(inserNotes,msg);
                mView.hideLoading();
            }

            @Override
            public void onError(Object error) {
                String msg= (String) error;
                mView.delAllError(inserNotes,msg);
                mView.hideLoading();
            }
        });
    }

    @Override
    public void del(final int position, String id) {
        mView.showLoading();
        model.del(id, new BaseModel.AsyncCallback() {
            @Override
            public void onSuccess(Object success) {

                mView.delSuccess(position);
                mView.hideLoading();
            }

            @Override
            public void onError(Object error) {
                String msg= (String) error;
                mView.delError(msg);
                mView.hideLoading();
            }
        });
    }
}
