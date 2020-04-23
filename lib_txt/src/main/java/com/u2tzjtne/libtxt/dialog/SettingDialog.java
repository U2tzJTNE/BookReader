package com.u2tzjtne.libtxt.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import com.u2tzjtne.libtxt.Config;
import com.u2tzjtne.libtxt.R;
import com.u2tzjtne.libtxt.util.DisplayUtils;
import com.u2tzjtne.libtxt.view.CircleImageView;

import java.util.Objects;

/**
 * @author Administrator
 * @date 2016/7/26 0026
 */
public class SettingDialog extends Dialog implements View.OnClickListener {

    private SeekBar sbBrightness;
    private TextView tvXitong;
    private TextView tvSize;
    private TextView tvQihei;
    private TextView tvDefault;
    private CircleImageView ivBgDefault;
    private CircleImageView ivBg1;
    private CircleImageView ivBg2;
    private CircleImageView ivBg3;
    private CircleImageView ivBg4;
    private TextView tvFzxinghei;
    private TextView tvFzkatong;
    private TextView tvBysong;


    private Config config;
    private Boolean isSystem;
    private SettingListener mSettingListener;
    private int FONT_SIZE_MIN;
    private int FONT_SIZE_MAX;
    private int currentFontSize;

    public SettingDialog(Context context) {
        this(context, R.style.setting_dialog);
    }

    public SettingDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getWindow()).setGravity(Gravity.BOTTOM);
        setContentView(R.layout.dialog_setting);
        initView();
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = d.getWidth();
        getWindow().setAttributes(p);

        FONT_SIZE_MIN = (int) getContext().getResources().getDimension(R.dimen.reading_min_text_size);
        FONT_SIZE_MAX = (int) getContext().getResources().getDimension(R.dimen.reading_max_text_size);

        config = Config.getInstance();

        //初始化亮度
        isSystem = config.isSystemLight();
        setTextViewSelect(tvXitong, isSystem);
        setBrightness(config.getLight());

        //初始化字体大小
        currentFontSize = (int) config.getFontSize();
        tvSize.setText(currentFontSize + "");

        //初始化字体
        tvDefault.setTypeface(config.getTypeface(Config.FONTTYPE_DEFAULT));
        tvQihei.setTypeface(config.getTypeface(Config.FONTTYPE_QIHEI));
        tvFzkatong.setTypeface(config.getTypeface(Config.FONTTYPE_FZKATONG));
        tvBysong.setTypeface(config.getTypeface(Config.FONTTYPE_BYSONG));
        selectTypeface(config.getTypefacePath());

        selectBg(config.getBookBgType());

        sbBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress > 10) {
                    changeBright(false, progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void initView() {
        findViewById(R.id.tv_dark).setOnClickListener(this);
        sbBrightness = findViewById(R.id.sb_brightness);
        findViewById(R.id.tv_bright).setOnClickListener(this);
        tvXitong = findViewById(R.id.tv_xitong);
        findViewById(R.id.tv_subtract).setOnClickListener(this);
        tvSize = findViewById(R.id.tv_size);
        findViewById(R.id.tv_add).setOnClickListener(this);
        tvQihei = findViewById(R.id.tv_qihei);
        tvDefault = findViewById(R.id.tv_default);
        ivBgDefault = findViewById(R.id.iv_bg_default);
        ivBg1 = findViewById(R.id.iv_bg_1);
        ivBg2 = findViewById(R.id.iv_bg_2);
        ivBg3 = findViewById(R.id.iv_bg_3);
        ivBg4 = findViewById(R.id.iv_bg_4);
        findViewById(R.id.tv_size_default).setOnClickListener(this);
        tvFzxinghei = findViewById(R.id.tv_fzxinghei);
        tvFzkatong = findViewById(R.id.tv_fzkatong);
        tvBysong = findViewById(R.id.tv_bysong);

        tvXitong.setOnClickListener(this);
        tvQihei.setOnClickListener(this);
        tvFzxinghei.setOnClickListener(this);
        tvFzkatong.setOnClickListener(this);
        tvDefault.setOnClickListener(this);
        ivBgDefault.setOnClickListener(this);
        ivBg1.setOnClickListener(this);
        ivBg2.setOnClickListener(this);
        ivBg3.setOnClickListener(this);
        ivBg4.setOnClickListener(this);
    }

    /**
     * 选择背景
     *
     * @param type
     */
    private void selectBg(int type) {
        switch (type) {
            default:
            case Config.BOOK_BG_DEFAULT:
                ivBgDefault.setBorderWidth(DisplayUtils.dp2px(getContext(), 2));
                ivBg1.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
                ivBg2.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
                ivBg3.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
                ivBg4.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
                break;
            case Config.BOOK_BG_1:
                ivBgDefault.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
                ivBg1.setBorderWidth(DisplayUtils.dp2px(getContext(), 2));
                ivBg2.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
                ivBg3.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
                ivBg4.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
                break;
            case Config.BOOK_BG_2:
                ivBgDefault.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
                ivBg1.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
                ivBg2.setBorderWidth(DisplayUtils.dp2px(getContext(), 2));
                ivBg3.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
                ivBg4.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
                break;
            case Config.BOOK_BG_3:
                ivBgDefault.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
                ivBg1.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
                ivBg2.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
                ivBg3.setBorderWidth(DisplayUtils.dp2px(getContext(), 2));
                ivBg4.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
                break;
            case Config.BOOK_BG_4:
                ivBgDefault.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
                ivBg1.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
                ivBg2.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
                ivBg3.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
                ivBg4.setBorderWidth(DisplayUtils.dp2px(getContext(), 2));
                break;
        }
    }

    /**
     * 设置字体
     * @param type
     */
    public void setBookBg(int type) {
        config.setBookBg(type);
        if (mSettingListener != null) {
            mSettingListener.changeBookBg(type);
        }
    }

    /**
     * 选择字体
     *
     * @param typeface
     */
    private void selectTypeface(String typeface) {
        switch (typeface) {
            default:
            case Config.FONTTYPE_DEFAULT:
                setTextViewSelect(tvDefault, true);
                setTextViewSelect(tvQihei, false);
                setTextViewSelect(tvFzxinghei, false);
                setTextViewSelect(tvFzkatong, false);
                setTextViewSelect(tvBysong, false);
                break;
            case Config.FONTTYPE_QIHEI:
                setTextViewSelect(tvDefault, false);
                setTextViewSelect(tvQihei, true);
                setTextViewSelect(tvFzxinghei, false);
                setTextViewSelect(tvFzkatong, false);
                setTextViewSelect(tvBysong, false);
                break;
            case Config.FONTTYPE_FZXINGHEI:
                setTextViewSelect(tvDefault, false);
                setTextViewSelect(tvQihei, false);
                setTextViewSelect(tvFzxinghei, true);
                setTextViewSelect(tvFzkatong, false);
                setTextViewSelect(tvBysong, false);
                break;
            case Config.FONTTYPE_FZKATONG:
                setTextViewSelect(tvDefault, false);
                setTextViewSelect(tvQihei, false);
                setTextViewSelect(tvFzxinghei, false);
                setTextViewSelect(tvFzkatong, true);
                setTextViewSelect(tvBysong, false);
                break;
            case Config.FONTTYPE_BYSONG:
                setTextViewSelect(tvDefault, false);
                setTextViewSelect(tvQihei, false);
                setTextViewSelect(tvFzxinghei, false);
                setTextViewSelect(tvFzkatong, false);
                setTextViewSelect(tvBysong, true);
                break;
        }
    }

    //设置字体
    public void setTypeface(String typeface) {
        config.setTypeface(typeface);
        Typeface tFace = config.getTypeface(typeface);
        if (mSettingListener != null) {
            mSettingListener.changeTypeFace(tFace);
        }
    }

    /**
     * 设置亮度
     *
     * @param brightness
     */
    public void setBrightness(float brightness) {
        sbBrightness.setProgress((int) (brightness * 100));
    }

    //设置按钮选择的背景
    private void setTextViewSelect(TextView textView, Boolean isSelect) {
        if (isSelect) {
            textView.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_select_bg));
            textView.setTextColor(getContext().getResources().getColor(R.color.read_dialog_button_select));
        } else {
            textView.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_bg));
            textView.setTextColor(getContext().getResources().getColor(R.color.white));
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tv_xitong) {
            isSystem = !isSystem;
            changeBright(isSystem, sbBrightness.getProgress());
        } else if (id == R.id.tv_subtract) {
            subtractFontSize();
        } else if (id == R.id.tv_add) {
            addFontSize();
        } else if (id == R.id.tv_size_default) {
            defaultFontSize();
        } else if (id == R.id.tv_qihei) {
            selectTypeface(Config.FONTTYPE_QIHEI);
            setTypeface(Config.FONTTYPE_QIHEI);
        } else if (id == R.id.tv_fzxinghei) {
            selectTypeface(Config.FONTTYPE_FZXINGHEI);
            setTypeface(Config.FONTTYPE_FZXINGHEI);
        } else if (id == R.id.tv_fzkatong) {
            selectTypeface(Config.FONTTYPE_FZKATONG);
            setTypeface(Config.FONTTYPE_FZKATONG);
        } else if (id == R.id.tv_bysong) {
            selectTypeface(Config.FONTTYPE_BYSONG);
            setTypeface(Config.FONTTYPE_BYSONG);
        } else if (id == R.id.tv_default) {
            selectTypeface(Config.FONTTYPE_DEFAULT);
            setTypeface(Config.FONTTYPE_DEFAULT);
        } else if (id == R.id.iv_bg_default) {
            setBookBg(Config.BOOK_BG_DEFAULT);
            selectBg(Config.BOOK_BG_DEFAULT);
        } else if (id == R.id.iv_bg_1) {
            setBookBg(Config.BOOK_BG_1);
            selectBg(Config.BOOK_BG_1);
        } else if (id == R.id.iv_bg_2) {
            setBookBg(Config.BOOK_BG_2);
            selectBg(Config.BOOK_BG_2);
        } else if (id == R.id.iv_bg_3) {
            setBookBg(Config.BOOK_BG_3);
            selectBg(Config.BOOK_BG_3);
        } else if (id == R.id.iv_bg_4) {
            setBookBg(Config.BOOK_BG_4);
            selectBg(Config.BOOK_BG_4);
        }
    }

    //变大书本字体
    private void addFontSize() {
        if (currentFontSize < FONT_SIZE_MAX) {
            currentFontSize += 1;
            tvSize.setText(currentFontSize + "");
            config.setFontSize(currentFontSize);
            if (mSettingListener != null) {
                mSettingListener.changeFontSize(currentFontSize);
            }
        }
    }

    private void defaultFontSize() {
        currentFontSize = (int) getContext().getResources().getDimension(R.dimen.reading_default_text_size);
        tvSize.setText(currentFontSize + "");
        config.setFontSize(currentFontSize);
        if (mSettingListener != null) {
            mSettingListener.changeFontSize(currentFontSize);
        }
    }

    //变小书本字体
    private void subtractFontSize() {
        if (currentFontSize > FONT_SIZE_MIN) {
            currentFontSize -= 1;
            tvSize.setText(currentFontSize + "");
            config.setFontSize(currentFontSize);
            if (mSettingListener != null) {
                mSettingListener.changeFontSize(currentFontSize);
            }
        }
    }

    //改变亮度
    public void changeBright(Boolean isSystem, int brightness) {
        float light = (float) (brightness / 100.0);
        setTextViewSelect(tvXitong, isSystem);
        config.setSystemLight(isSystem);
        config.setLight(light);
        if (mSettingListener != null) {
            mSettingListener.changeSystemBright(isSystem, light);
        }
    }

    public void setSettingListener(SettingListener settingListener) {
        this.mSettingListener = settingListener;
    }

    public interface SettingListener {
        void changeSystemBright(Boolean isSystem, float brightness);

        void changeFontSize(int fontSize);

        void changeTypeFace(Typeface typeface);

        void changeBookBg(int type);
    }
}