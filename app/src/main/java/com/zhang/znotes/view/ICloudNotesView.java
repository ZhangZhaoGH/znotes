package com.zhang.znotes.view;

import com.zhang.znotes.base.BaseView;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by zz on 2017/3/25.
 */

public interface ICloudNotesView extends BaseView {


    void delAllSuccess(List<BmobObject> inserNotes,String successMessage);

    void delSuccess(int position);

    void delError(String errorMessage);


    void delAllError(List<BmobObject> inserNotes,String errorMessage);

}
