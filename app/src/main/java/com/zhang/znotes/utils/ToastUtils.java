package com.zhang.znotes.utils;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

import com.zhang.znotes.MyApplication;
import com.zhang.znotes.R;

/**
  * Taost 显示的工具类
  * @author zhangzhao
  *
  */
public class ToastUtils {
	/** 上下文. */
    private static Context mContext = null;
    
    /** 显示Toast. */
    public static final int SHOW_TOAST = 0;

   	
   public static void showToast(String text){
	   Toast.makeText(MyApplication.getContext(), text, Toast.LENGTH_SHORT).show();
   }
	public static void showSnackMessage(View v, String text) {
		if (v == null) {
			showToast(text);
			return;
		}
		showSnackMessage(v, text, null, null, Snackbar.LENGTH_SHORT);
	}

	private static void showSnackMessage(View v, String msg, Snackbar.Callback callback,
										 View.OnClickListener listener, int length) {
		Snackbar snack = Snackbar.make(v, msg, length);
		snack.getView().setBackgroundColor(MyApplication.getContext().getResources().getColor(R.color.color_blue));
		snack.setActionTextColor(Color.WHITE);
		snack.setCallback(callback);
		snack.setAction(msg, listener);
		snack.show();
	}

	public static void showSnackMessage(View v, String msg,  int length ,String actionMsg ,View.OnClickListener listener) {
		Snackbar snack = Snackbar.make(v, msg, length);
		snack.getView().setBackgroundColor(MyApplication.getContext().getResources().getColor(R.color.color_blue));
		snack.setActionTextColor(Color.WHITE);
		snack.setAction(actionMsg, listener);
		snack.show();
	}


 }
