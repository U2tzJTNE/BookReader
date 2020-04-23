package com.u2tzjtne.libtxt;

import android.graphics.Color;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.astuetz.PagerSlidingTabStrip;
import com.u2tzjtne.libtxt.adapter.MyPagerAdapter;
import com.u2tzjtne.libtxt.base.BaseActivity;
import com.u2tzjtne.libtxt.util.FileUtils;
import com.u2tzjtne.libtxt.util.PageFactory;

import java.util.Objects;
/**
 * @author Administrator
 * @date 2016/1/6
 */
public class MarkActivity extends BaseActivity {

    private Toolbar toolbar;
    PagerSlidingTabStrip tabs;
    ViewPager pager;

    private Typeface typeface;
    private DisplayMetrics dm;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_mark;
    }

    @Override
    protected void initData() {
        PageFactory pageFactory = PageFactory.getInstance();
        Config config = Config.getInstance();
        dm = getResources().getDisplayMetrics();
        typeface = config.getTypeface();

        setSupportActionBar(toolbar);
        //设置导航图标
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(FileUtils.getFileName(pageFactory.getBookPath()));
        }

        setTabsValue();
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), pageFactory.getBookPath()));
        tabs.setViewPager(pager);
    }

    @Override
    protected void initView() {
        toolbar = findViewById(R.id.toolbar);
        tabs = findViewById(R.id.tabs);
        pager = findViewById(R.id.pager);
    }

    private void setTabsValue() {
        // 设置Tab是自动填充满屏幕的
        //所有初始化要在setViewPager方法之前
        tabs.setShouldExpand(true);
        // 设置Tab的分割线是透明的
        tabs.setDividerColor(Color.TRANSPARENT);
        // 设置Tab底部线的高度
        tabs.setUnderlineHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 1, dm));
        // 设置Tab Indicator的高度
        tabs.setIndicatorHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 2, dm));
        // 设置Tab标题文字的大小
        tabs.setTextSize((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 16, dm));
        //设置Tab标题文字的字体
        tabs.setTypeface(typeface, 0);
        // 设置Tab Indicator的颜色
        tabs.setIndicatorColor(getResources().getColor(R.color.colorAccent));
        // 取消点击Tab时的背景色
        tabs.setTabBackground(0);
    }

    @Override
    protected void initListener() {

    }
}
