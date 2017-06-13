package com.zhang.znotes.base;

/**
 * Created by zz on 2017/3/20.
 * MVP 基类view
 */

public interface BaseView<T> {


    /**
     * show loading message
     */
    void showLoading();

    /**
     * hide loading
     */
    void hideLoading();

}
