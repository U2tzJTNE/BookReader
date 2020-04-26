package com.u2tzjtne.libepub.ui.activity;

import android.graphics.Rect;
import com.u2tzjtne.libepub.Config;
import com.u2tzjtne.libepub.model.DisplayUnit;
import com.u2tzjtne.libepub.model.locators.ReadLocator;

import java.lang.ref.WeakReference;

public interface ReadActivityCallback {

    int getCurrentChapterIndex();

    ReadLocator getEntryReadLocator();

    boolean goToChapter(String href);

    Config.Direction getDirection();

    void onDirectionChange(Config.Direction newDirection);

    void storeLastReadLocator(ReadLocator lastReadLocator);

    void toggleSystemUI();

    void setDayMode();

    void setNightMode();

    int getTopDistraction(final DisplayUnit unit);

    int getBottomDistraction(final DisplayUnit unit);

    Rect getViewportRect(final DisplayUnit unit);

    WeakReference<ReadActivity> getActivity();

    String getStreamerUrl();
}
