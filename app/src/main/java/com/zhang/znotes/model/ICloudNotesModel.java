package com.zhang.znotes.model;

import com.zhang.znotes.base.BaseModel;
import com.zhang.znotes.bean.litepal.NotesBean;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by zz on 2017/3/25.
 */

public interface ICloudNotesModel extends BaseModel {


    void delAll(List<BmobObject> delNotes, AsyncCallback callback);

    void del(String id, AsyncCallback callback);


}
