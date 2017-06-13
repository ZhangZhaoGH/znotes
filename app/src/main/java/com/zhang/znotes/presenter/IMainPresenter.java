package com.zhang.znotes.presenter;

import com.zhang.znotes.base.BaseModel;
import com.zhang.znotes.base.BasePresenter;
import com.zhang.znotes.bean.litepal.NotesBean;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by zz on 2017/3/25.
 */

public interface IMainPresenter extends BasePresenter {

    void save(NotesBean bean, String eamil);

    void update(NotesBean bean);

    void saveAll(List<BmobObject> inserNotes, List<NotesBean> inserList);

    void updateAll(List<BmobObject> updateNotes);

    void del(int position,int id);

    void delAll(List<NotesBean> delList);

    void login(String account, String password);




}
