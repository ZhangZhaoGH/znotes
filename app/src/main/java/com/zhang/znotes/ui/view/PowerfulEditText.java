package com.zhang.znotes.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

import com.zhang.znotes.R;

/**
 * Created by zz on 2017/4/19.
 */

public class PowerfulEditText extends EditText {

    /**
     * 普通类型
     */
    private static final int TYPE_NORMAL = -1;
    /**
     * 带清除功能的类型
     */
    private static final int TYPE_CAN_CLEAR = 0;
    /**
     * 带密码查看功能的类型
     */
    private static final int TYPE_CAN_WATCH_PWD = 1;
    private TypedArray typedArray;

    private int funcType;
    private Drawable mDrawableRight;
    private Drawable mEyeDrawableRight;
    private Drawable leftDrawable;
    private Context mContext;
    private boolean eyeOpen;
    private int leftWidth, leftHeight;
    private int rightWidth;
    private int rightHeight;
    /**
     * 关闭查看密码图标的资源id
     */
    private int eyeCloseResourseId;
    /**
     * 开启查看密码图标的资源id
     */
    private int eyeOpenResourseId;

    public PowerfulEditText(Context context) {
        this(context, null);
    }

    public PowerfulEditText(Context context, AttributeSet attrs) {
//        super(context, attrs);
        //不加这个很多属性无法在xml里定义
        this(context, attrs, R.attr.editTextStyle);
    }

    public PowerfulEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        typedArray = context.obtainStyledAttributes(attrs, R.styleable.PowerfulEditText);
        funcType = typedArray.getInt(R.styleable.PowerfulEditText_funcType, TYPE_NORMAL);
        eyeCloseResourseId = typedArray.getResourceId(R.styleable.PowerfulEditText_eyeClose, R.mipmap.eye_close);
        eyeOpenResourseId = typedArray.getResourceId(R.styleable.PowerfulEditText_eyeOpen, R.mipmap.eye_open);

        init();
    }

    /**
     * 自己的逻辑操作，判断是什么类型等等
     */
    private void init() {

        //获取editText的右侧图标，默认位置是左上右下，对应0,1,2,3;
        mDrawableRight = getCompoundDrawables()[2];
        leftDrawable = getCompoundDrawables()[0];
        if (mDrawableRight == null) {
            //没有设置默认右侧图标
            if (funcType == TYPE_CAN_CLEAR) {
                //选择的是带右侧清除功能，设置默认右侧图标
                mDrawableRight = ContextCompat.getDrawable(mContext, R.mipmap.edit_clear);
            }
            if (funcType == TYPE_CAN_WATCH_PWD) {
                mEyeDrawableRight = ContextCompat.getDrawable(mContext, eyeOpenResourseId);
                mDrawableRight = ContextCompat.getDrawable(mContext, eyeCloseResourseId);
            }
        }
        if (leftDrawable != null) {
            leftWidth = typedArray.getInt(R.styleable.PowerfulEditText_liftDrawableWidth, leftDrawable.getIntrinsicWidth());
            leftHeight = typedArray.getInt(R.styleable.PowerfulEditText_liftDrawableHeight, leftDrawable.getIntrinsicHeight());
            leftDrawable.setBounds(0, 0, leftWidth, leftHeight);
        }

        if (mDrawableRight != null) {
            rightWidth = typedArray.getInt(R.styleable.PowerfulEditText_liftDrawableWidth, mDrawableRight.getIntrinsicWidth());
            rightHeight = typedArray.getInt(R.styleable.PowerfulEditText_liftDrawableHeight, mDrawableRight.getIntrinsicHeight());
            mDrawableRight.setBounds(0, 0, rightWidth, rightHeight);
            if (mEyeDrawableRight != null) {
                mEyeDrawableRight.setBounds(0, 0, rightWidth, rightHeight);
            }

            if (funcType == TYPE_CAN_CLEAR) {
                String content = getText().toString().trim();
                if (!TextUtils.isEmpty(content)) {
                    //如果是清除功能，则一开始隐藏图标，否则显示
                    setRightIconVisible(true);
                    setSelection(content.length());
                } else {
                    //隐藏右侧图标
                    setRightIconVisible(false);
                }
            } else {
                setRightIconVisible(true);
            }

        }

        typedArray.recycle();


        //设置输入框内容发生改变的监听
        addTextChangedListener(new TextWatcher() {
            /**
             * 发生改变之前
             */
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                if (textListener != null) {
                    textListener.beforeTextChanged(s, start, count, after);
                }

            }

            /**
             * 输入框内容发生改变的回调
             */
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //根据文本内容的长度来判断是否显示隐藏删除图标
                if (funcType == 0) {
                    setRightIconVisible(s.length() > 0);
                }

                if (textListener != null) {
                    textListener.onTextChanged(s, start, before, count);
                }
            }

            /**
             * 发生改变之后
             */
            @Override
            public void afterTextChanged(Editable s) {
                if (textListener != null) {
                    textListener.afterTextChanged(s);
                }

            }
        });

    }

    /**
     * 因为不能直接给editText设置监听事件，通过重写onTouchEvent判断手指的点击区域
     * 是否是在右侧图标的位置来执行清除操作
     * 当我们按下的位置是在
     * 控件的总宽度 - 图标的宽度 - 图标到控件右边的宽度
     * 控件的总宽度 - 图标到控件右边的宽度
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (getCompoundDrawables()[2] != null) {
                boolean isTouched = event.getX() > (getWidth() - getTotalPaddingRight()) && event.getX() < (getWidth() - getPaddingRight());

                if (isTouched) {
                    //是在删除控件范围内
                    if (onRightClickListener == null) {
                        if (funcType == TYPE_CAN_CLEAR) {
                            //没有设置右侧图标监听，并且模式是清除模式，则清除文本
                            setText("");
                            setRightIconVisible(false);
                        }
                        if (funcType == TYPE_CAN_WATCH_PWD) {
                            //查看密码模式
                            if (eyeOpen) {
                                //改成明文
                                setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                                eyeOpen = false;
                            } else {
                                //变成密文模式
                                setTransformationMethod(PasswordTransformationMethod.getInstance());
                                eyeOpen = true;
                            }
                            //切换图标
                            switchWatchPwdIcon();

                        }
                    } else {
                        //这里是有右侧图标监听
                        onRightClickListener.Click(this);

                    }
                }
            }
        }

        return super.onTouchEvent(event);
    }

    /**
     * 切换查看密码的图标
     */
    private void switchWatchPwdIcon() {
        if (eyeOpen) {
            //开启查看
            setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], mEyeDrawableRight, getCompoundDrawables()[3]);
        } else {
            //关闭查看
            setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], mDrawableRight, getCompoundDrawables()[3]);
        }

    }

    private OnRightClickListener onRightClickListener;

    public interface OnRightClickListener {
        void Click(EditText editText);
    }

    private TextListener textListener;

    /**
     * 输入框文本变化的回调，如果需要多进行一次监听操作，则设置此监听代替textWatcher
     */
    public interface TextListener {
        void beforeTextChanged(CharSequence s, int start, int count, int after);

        void onTextChanged(CharSequence s, int start, int before, int count);

        void afterTextChanged(Editable s);
    }

    /**
     * 设置右侧图标的显示与隐藏
     *
     * @param type true为显示图标
     */
    private void setRightIconVisible(boolean type) {
        Drawable drawableRight = type ? mDrawableRight : null;
        //重新设置editText图标
        setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], drawableRight, getCompoundDrawables()[3]);


    }
}
