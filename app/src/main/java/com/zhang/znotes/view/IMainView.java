package com.zhang.znotes.view;

import com.zhang.znotes.base.BaseView;

/**
 * Created by zz on 2017/3/25.
 */

public interface IMainView extends BaseView {

    void updateSuccess(String successMessage);

    void updateError(String errorMessage);

    void saveSuccess(String successMessage);

    void saveError(String errorMessage);

    void updateAllResult(String successMessage);

    void delSuccess(int position);

    void delError(String errorMessage);

    void delAllSuccess(String successMessage);

    void delAllError(String errorMessage);

    void loginSuccess(String successMessage);

    void loginError(String errorMessage);


}
