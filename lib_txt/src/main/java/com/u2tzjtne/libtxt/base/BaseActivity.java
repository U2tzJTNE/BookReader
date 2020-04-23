package com.u2tzjtne.libtxt.base;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 *
 * @author Administrator
 * @date 2016/7/8 0008
 */
public abstract class BaseActivity extends AppCompatActivity {
    /**
     * 初始化布局
     */
    public abstract int getLayoutRes();

    protected abstract void initData();
    protected abstract void initView();

    protected abstract void initListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutRes());
        initView();
        initData();
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
