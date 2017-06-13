package com.zhang.znotes.view;

import com.zhang.znotes.base.BaseView;

/**
 * Created by zz on 2017/3/21.
 */

public interface ILoginView extends BaseView {

    void loginSuccess(String successMessage);

    void loginError(String errorMessage);
}
