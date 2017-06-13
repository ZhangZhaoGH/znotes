package com.zhang.znotes.view;

import com.zhang.znotes.base.BaseView;

/**
 * Created by zz on 2017/3/23.
 */

public interface IUserInfoView extends BaseView {


    void updateSuccess(String successMessage);

    void updateError(String errorMessage);

    void updateBackgroundSuccess(String successMessage);

    void updateBackgroundError(String errorMessage);

    void logOutSuccess(String successMessage);


}
