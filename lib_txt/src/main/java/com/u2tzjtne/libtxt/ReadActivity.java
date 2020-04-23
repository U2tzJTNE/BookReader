package com.u2tzjtne.libtxt;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.SQLException;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.AppBarLayout;
import com.u2tzjtne.libtxt.base.BaseActivity;
import com.u2tzjtne.libtxt.db.BookList;
import com.u2tzjtne.libtxt.db.BookMarks;
import com.u2tzjtne.libtxt.dialog.PageModeDialog;
import com.u2tzjtne.libtxt.dialog.SettingDialog;
import com.u2tzjtne.libtxt.util.BrightnessUtil;
import com.u2tzjtne.libtxt.util.PageFactory;
import com.u2tzjtne.libtxt.view.PageWidget;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author Administrator
 * @date 2016/7/15 0015
 */
public class ReadActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "ReadActivity";
    private final static String EXTRA_BOOK = "bookList";
    private final static int MESSAGE_CHANGE_PROGRESS = 1;

    private PageWidget bookPage;
    private TextView tvProgress;
    private RelativeLayout rlProgress;
    private SeekBar sbProgress;
    private TextView tvDayOrNight;
    private RelativeLayout rlBottom;
    private RelativeLayout rlReadBottom;
    private Toolbar toolbar;
    private AppBarLayout appbar;

    private Config config;
    private PageFactory pageFactory;
    /**
     * popwindow是否显示
     */
    private Boolean isShow = false;
    private SettingDialog mSettingDialog;
    private PageModeDialog mPageModeDialog;
    private Boolean mDayOrNight;
    /**
     * 接收电池信息更新的广播
     */
    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Objects.equals(intent.getAction(), Intent.ACTION_BATTERY_CHANGED)) {
                Log.e(TAG, Intent.ACTION_BATTERY_CHANGED);
                int level = intent.getIntExtra("level", 0);
                pageFactory.updateBattery(level);
            } else if (Objects.equals(intent.getAction(), Intent.ACTION_TIME_TICK)) {
                Log.e(TAG, Intent.ACTION_TIME_TICK);
                pageFactory.updateTime();
            }
        }
    };

    @Override
    public int getLayoutRes() {
        return R.layout.activity_read;
    }

    @Override
    protected void initData() {
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.return_button);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        config = Config.getInstance();
        pageFactory = PageFactory.getInstance();

        IntentFilter mfilter = new IntentFilter();
        mfilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        mfilter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(myReceiver, mfilter);

        mSettingDialog = new SettingDialog(this);
        mPageModeDialog = new PageModeDialog(this);
        //获取屏幕宽高
        WindowManager manage = getWindowManager();
        Display display = manage.getDefaultDisplay();
        Point displaySize = new Point();
        display.getSize(displaySize);
        //保持屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //隐藏
        hideSystemUI();
        //改变屏幕亮度
        if (!config.isSystemLight()) {
            BrightnessUtil.setBrightness(this, config.getLight());
        }
        //获取intent中的携带的信息
        Intent intent = getIntent();
        BookList bookList = (BookList) intent.getSerializableExtra(EXTRA_BOOK);

        bookPage.setPageMode(config.getPageMode());
        pageFactory.setPageWidget(bookPage);

        try {
            if (bookList != null) {
                pageFactory.openBook(bookList);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "打开电子书失败", Toast.LENGTH_SHORT).show();
        }

        initDayOrNight();
    }

    @Override
    protected void initView() {
        bookPage = findViewById(R.id.bookpage);
        tvProgress = findViewById(R.id.tv_progress);
        rlProgress = findViewById(R.id.rl_progress);
        findViewById(R.id.tv_pre).setOnClickListener(this);
        sbProgress = findViewById(R.id.sb_progress);
        findViewById(R.id.tv_next).setOnClickListener(this);
        findViewById(R.id.tv_directory).setOnClickListener(this);
        tvDayOrNight = findViewById(R.id.tv_dayornight);
        findViewById(R.id.tv_pagemode).setOnClickListener(this);
        findViewById(R.id.tv_setting).setOnClickListener(this);
        rlBottom = findViewById(R.id.rl_bottom);
        rlReadBottom = findViewById(R.id.rl_read_bottom);
        toolbar = findViewById(R.id.toolbar);
        appbar = findViewById(R.id.appbar);
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tv_pre) {
            pageFactory.preChapter();
        } else if (id == R.id.tv_next) {
            pageFactory.nextChapter();
        } else if (id == R.id.tv_directory) {
            Intent intent = new Intent(ReadActivity.this, MarkActivity.class);
            startActivity(intent);
        } else if (id == R.id.tv_dayornight) {
            changeDayOrNight();
        } else if (id == R.id.tv_pagemode) {
            hideReadSetting();
            mPageModeDialog.show();
        } else if (id == R.id.tv_setting) {
            hideReadSetting();
            mSettingDialog.show();
        }
    }

    @Override
    protected void initListener() {
        sbProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            float pro;

            // 触发操作，拖动
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                pro = (float) (progress / 10000.0);
                showProgress(pro);
            }

            // 表示进度条刚开始拖动，开始拖动时候触发的操作
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            // 停止拖动时候
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                pageFactory.changeProgress(pro);
            }
        });

        mPageModeDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                hideSystemUI();
            }
        });

        mPageModeDialog.setPageModeListener(new PageModeDialog.PageModeListener() {
            @Override
            public void changePageMode(int pageMode) {
                bookPage.setPageMode(pageMode);
            }
        });

        mSettingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                hideSystemUI();
            }
        });

        mSettingDialog.setSettingListener(new SettingDialog.SettingListener() {
            @Override
            public void changeSystemBright(Boolean isSystem, float brightness) {
                if (!isSystem) {
                    BrightnessUtil.setBrightness(ReadActivity.this, brightness);
                } else {
                    int bh = BrightnessUtil.getScreenBrightness(ReadActivity.this);
                    BrightnessUtil.setBrightness(ReadActivity.this, bh);
                }
            }

            @Override
            public void changeFontSize(int fontSize) {
                pageFactory.changeFontSize(fontSize);
            }

            @Override
            public void changeTypeFace(Typeface typeface) {
                pageFactory.changeTypeface(typeface);
            }

            @Override
            public void changeBookBg(int type) {
                pageFactory.changeBookBg(type);
            }
        });

        pageFactory.setPageEvent(new PageFactory.PageEvent() {
            @Override
            public void changeProgress(float progress) {
                Message message = new Message();
                message.what = MESSAGE_CHANGE_PROGRESS;
                message.obj = progress;
                mHandler.sendMessage(message);
            }
        });

        bookPage.setTouchListener(new PageWidget.TouchListener() {
            @Override
            public void center() {
                if (isShow) {
                    hideReadSetting();
                } else {
                    showReadSetting();
                }
            }

            @Override
            public Boolean prePage() {
                if (isShow) {
                    return false;
                }

                pageFactory.prePage();
                return !pageFactory.isfirstPage();
            }

            @Override
            public Boolean nextPage() {
                Log.e("setTouchListener", "nextPage");
                if (isShow) {
                    return false;
                }

                pageFactory.nextPage();
                return !pageFactory.islastPage();
            }

            @Override
            public void cancel() {
                pageFactory.cancelPage();
            }
        });

    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MESSAGE_CHANGE_PROGRESS) {
                float progress = (float) msg.obj;
                setSeekBarProgress(progress);
            }
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        if (!isShow) {
            hideSystemUI();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pageFactory.clear();
        bookPage = null;
        unregisterReceiver(myReceiver);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isShow) {
                hideReadSetting();
                return true;
            }
            if (mSettingDialog.isShowing()) {
                mSettingDialog.hide();
                return true;
            }
            if (mPageModeDialog.isShowing()) {
                mPageModeDialog.hide();
                return true;
            }
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.read, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_add_bookmark) {
            if (pageFactory.getCurrentPage() != null) {
                List<BookMarks> bookMarksList = DataSupport.where("bookpath = ? and begin = ?", pageFactory.getBookPath(), pageFactory.getCurrentPage().getBegin() + "").find(BookMarks.class);

                if (!bookMarksList.isEmpty()) {
                    Toast.makeText(ReadActivity.this, "该书签已存在", Toast.LENGTH_SHORT).show();
                } else {
                    BookMarks bookMarks = new BookMarks();
                    StringBuilder word = new StringBuilder();
                    for (String line : pageFactory.getCurrentPage().getLines()) {
                        word.append(line);
                    }
                    try {
                        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm ss");
                        String time = sf.format(new Date());
                        bookMarks.setTime(time);
                        bookMarks.setBegin(pageFactory.getCurrentPage().getBegin());
                        bookMarks.setText(word.toString());
                        bookMarks.setBookPath(pageFactory.getBookPath());
                        bookMarks.save();

                        Toast.makeText(ReadActivity.this, "书签添加成功", Toast.LENGTH_SHORT).show();
                    } catch (SQLException e) {
                        Toast.makeText(ReadActivity.this, "该书签已存在", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(ReadActivity.this, "添加书签失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }


    public static boolean openBook(final BookList bookList, Activity context) {
        if (bookList == null) {
            throw new NullPointerException("BookList can not be null");
        }

        Intent intent = new Intent(context, ReadActivity.class);
        intent.putExtra(EXTRA_BOOK, bookList);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        context.startActivity(intent);
        return true;
    }

    /**
     * 隐藏菜单。沉浸式阅读
     */
    private void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }

    private void showSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }

    /**
     * 显示书本进度
     *
     * @param progress
     */
    public void showProgress(float progress) {
        if (rlProgress.getVisibility() != View.VISIBLE) {
            rlProgress.setVisibility(View.VISIBLE);
        }
        setProgress(progress);
    }

    public void initDayOrNight() {
        mDayOrNight = config.getDayOrNight();
        if (mDayOrNight) {
            tvDayOrNight.setText(getResources().getString(R.string.read_setting_day));
        } else {
            tvDayOrNight.setText(getResources().getString(R.string.read_setting_night));
        }
    }

    /**
     * 改变显示模式
     */
    public void changeDayOrNight() {
        if (mDayOrNight) {
            mDayOrNight = false;
            tvDayOrNight.setText(getResources().getString(R.string.read_setting_night));
        } else {
            mDayOrNight = true;
            tvDayOrNight.setText(getResources().getString(R.string.read_setting_day));
        }
        config.setDayOrNight(mDayOrNight);
        pageFactory.setDayOrNight(mDayOrNight);
    }

    private void setProgress(float progress) {
        DecimalFormat decimalFormat = new DecimalFormat("00.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
        String p = decimalFormat.format(progress * 100.0);//format 返回的是字符串
        tvProgress.setText(p + "%");
    }

    public void setSeekBarProgress(float progress) {
        sbProgress.setProgress((int) (progress * 10000));
    }

    private void showReadSetting() {
        isShow = true;
        rlProgress.setVisibility(View.GONE);

        showSystemUI();

        Animation topAnim = AnimationUtils.loadAnimation(this, R.anim.dialog_top_enter);
        rlBottom.startAnimation(topAnim);
        appbar.startAnimation(topAnim);
        rlBottom.setVisibility(View.VISIBLE);
        appbar.setVisibility(View.VISIBLE);

    }

    private void hideReadSetting() {
        isShow = false;
        Animation bottomAnim = AnimationUtils.loadAnimation(this, R.anim.dialog_exit);
        Animation topAnim = AnimationUtils.loadAnimation(this, R.anim.dialog_top_exit);
        if (rlBottom.getVisibility() == View.VISIBLE) {
            rlBottom.startAnimation(topAnim);
        }
        if (appbar.getVisibility() == View.VISIBLE) {
            appbar.startAnimation(topAnim);
        }
        if (rlReadBottom.getVisibility() == View.VISIBLE) {
            rlReadBottom.startAnimation(topAnim);
        }
        rlBottom.setVisibility(View.GONE);
        rlReadBottom.setVisibility(View.GONE);
        appbar.setVisibility(View.GONE);
        hideSystemUI();
    }
}
