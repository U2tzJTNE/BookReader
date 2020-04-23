package com.u2tzjtne.libtxt.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.u2tzjtne.libtxt.Config;
import com.u2tzjtne.libtxt.R;

/**
 * @author Administrator
 * @date 2016/8/30 0030
 */
public class PageModeDialog extends Dialog implements View.OnClickListener {

    private TextView tvSimulation;
    private TextView textView;
    private TextView tvSlide;
    private TextView tvNone;

    private Config config;
    private PageModeListener pageModeListener;

    private PageModeDialog(Context context, boolean flag, OnCancelListener listener) {
        super(context, flag, listener);
    }

    public PageModeDialog(Context context) {
        this(context, R.style.setting_dialog);
    }

    public PageModeDialog(Context context, int themeResId) {
        super(context, themeResId);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setGravity(Gravity.BOTTOM);
        setContentView(R.layout.dialog_pagemode);
        initView();
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = d.getWidth();
        getWindow().setAttributes(p);

        config = Config.getInstance();
        selectPageMode(config.getPageMode());
    }

    private void initView() {
        tvSimulation = findViewById(R.id.tv_simulation);
        textView = findViewById(R.id.tv_cover);
        tvSlide = findViewById(R.id.tv_slide);
        tvNone = findViewById(R.id.tv_none);

        textView.setOnClickListener(this);
        tvSlide.setOnClickListener(this);
        tvNone.setOnClickListener(this);
        tvSimulation.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tv_simulation) {
            selectPageMode(Config.PAGE_MODE_SIMULATION);
            setPageMode(Config.PAGE_MODE_SIMULATION);
        } else if (id == R.id.tv_cover) {
            selectPageMode(Config.PAGE_MODE_COVER);
            setPageMode(Config.PAGE_MODE_COVER);
        } else if (id == R.id.tv_slide) {
            selectPageMode(Config.PAGE_MODE_SLIDE);
            setPageMode(Config.PAGE_MODE_SLIDE);
        } else if (id == R.id.tv_none) {
            selectPageMode(Config.PAGE_MODE_NONE);
            setPageMode(Config.PAGE_MODE_NONE);
        }
    }

    /**
     * 设置翻页
     *
     * @param pageMode
     */
    public void setPageMode(int pageMode) {
        config.setPageMode(pageMode);
        if (pageModeListener != null) {
            pageModeListener.changePageMode(pageMode);
        }
    }

    /**
     * 选择怕翻页
     *
     * @param pageMode
     */
    private void selectPageMode(int pageMode) {
        if (pageMode == Config.PAGE_MODE_SIMULATION) {
            setTextViewSelect(tvSimulation, true);
            setTextViewSelect(textView, false);
            setTextViewSelect(tvSlide, false);
            setTextViewSelect(tvNone, false);
        } else if (pageMode == Config.PAGE_MODE_COVER) {
            setTextViewSelect(tvSimulation, false);
            setTextViewSelect(textView, true);
            setTextViewSelect(tvSlide, false);
            setTextViewSelect(tvNone, false);
        } else if (pageMode == Config.PAGE_MODE_SLIDE) {
            setTextViewSelect(tvSimulation, false);
            setTextViewSelect(textView, false);
            setTextViewSelect(tvSlide, true);
            setTextViewSelect(tvNone, false);
        } else if (pageMode == Config.PAGE_MODE_NONE) {
            setTextViewSelect(tvSimulation, false);
            setTextViewSelect(textView, false);
            setTextViewSelect(tvSlide, false);
            setTextViewSelect(tvNone, true);
        }
    }

    /**
     * 设置按钮选择的背景
     *
     * @param textView
     * @param isSelect
     */
    private void setTextViewSelect(TextView textView, Boolean isSelect) {
        if (isSelect) {
            textView.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_select_bg));
            textView.setTextColor(getContext().getResources().getColor(R.color.read_dialog_button_select));
        } else {
            textView.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_bg));
            textView.setTextColor(getContext().getResources().getColor(R.color.white));
        }
    }

    public void setPageModeListener(PageModeListener pageModeListener) {
        this.pageModeListener = pageModeListener;
    }

    public interface PageModeListener {
        void changePageMode(int pageMode);
    }
}
