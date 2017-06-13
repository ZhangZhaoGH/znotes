package com.zhang.znotes.presenter;

import com.zhang.znotes.base.BasePresenter;
import com.zhang.znotes.bean.litepal.NotesBean;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by zz on 2017/3/25.
 */

public interface ICloudNotesPresenter extends BasePresenter {

    void delAll(List<BmobObject> inserNotes);

    void del(int position,String id);




}
