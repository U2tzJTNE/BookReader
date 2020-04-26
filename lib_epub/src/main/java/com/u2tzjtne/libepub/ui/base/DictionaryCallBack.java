package com.u2tzjtne.libepub.ui.base;

import com.u2tzjtne.libepub.model.dictionary.Dictionary;

/**
 * @author gautam chibde on 4/7/17.
 */

public interface DictionaryCallBack extends BaseMvpView {

    void onDictionaryDataReceived(Dictionary dictionary);

    //TODO
    void playMedia(String url);
}
