package com.u2tzjtne.libtxt.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
/**
 *
 * @author Administrator
 * @date 2016/8/31 0031
 */
public abstract class BaseFragment extends Fragment {

    private View rootView;

    /**
     * 初始化布局
     */
    protected abstract int getLayoutRes();

    protected abstract void initData(View view);
    protected abstract void initView(View view);

    protected abstract void initListener();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutRes(), container, false);
        rootView = view;
        initView(view);
        initData(view);
        initListener();
        return view;
    }

    public View getRootView() {
        return rootView;
    }
}
