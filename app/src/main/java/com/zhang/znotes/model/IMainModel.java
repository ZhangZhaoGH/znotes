package com.zhang.znotes.model;

import com.zhang.znotes.base.BaseModel;
import com.zhang.znotes.bean.litepal.NotesBean;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by zz on 2017/3/25.
 */

public interface IMainModel extends BaseModel {

    void save(NotesBean bean,String eamil,AsyncCallback callback);

    void update(NotesBean bean,AsyncCallback callback);

    void saveAll(List<BmobObject> inserNotes,List<NotesBean> inserList,AsyncCallback callback);

    void updateAll(List<BmobObject> updateNotes,AsyncCallback callback);

    void del(int position,int id,AsyncCallback callback);

    void delAll(List<NotesBean> delList,AsyncCallback callback);

    void login(String account, String password,AsyncCallback callback);

}
