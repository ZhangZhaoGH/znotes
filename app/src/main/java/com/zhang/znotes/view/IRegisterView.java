package com.zhang.znotes.view;

import com.zhang.znotes.base.BaseView;

/**
 * Created by zz on 2017/3/20.
 */

public interface IRegisterView extends BaseView {

    /**
     * 注册成功
     */
    void registSuccess(String successMessage);

    /**
     * 注册失败提示
     * @param errorMessage
     */
    void registError(String errorMessage);

}
