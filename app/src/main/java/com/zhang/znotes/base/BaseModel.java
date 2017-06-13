package com.zhang.znotes.base;

/**
 * Created by zz on 2017/3/20.
 * MVP 基类Model
 */

public interface BaseModel {

     interface AsyncCallback {

        void onSuccess(Object success);

        void onError(Object error);

    }
}
