package com.u2tzjtne.libepub;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Parcelable;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.u2tzjtne.libepub.model.HighLight;
import com.u2tzjtne.libepub.model.HighlightImpl;
import com.u2tzjtne.libepub.model.locators.ReadLocator;
import com.u2tzjtne.libepub.model.sqlite.DbAdapter;
import com.u2tzjtne.libepub.network.QualifiedTypeConverterFactory;
import com.u2tzjtne.libepub.network.R2StreamerApi;
import com.u2tzjtne.libepub.ui.activity.ReadActivity;
import com.u2tzjtne.libepub.ui.base.OnSaveHighlight;
import com.u2tzjtne.libepub.ui.base.SaveReceivedHighlightTask;
import com.u2tzjtne.libepub.util.OnHighlightListener;
import com.u2tzjtne.libepub.util.ReadLocatorListener;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author avez raj
 * @date 9/13/2017
 */

public class EPUBReader {

    @SuppressLint("StaticFieldLeak")
    private static EPUBReader singleton = null;

    public static final String EXTRA_BOOK_ID = "com.u2tzjtne.libepub.extra.BOOK_ID";
    public static final String EXTRA_READ_LOCATOR = "com.u2tzjtne.libepub.extra.READ_LOCATOR";
    public static final String EXTRA_PORT_NUMBER = "com.u2tzjtne.libepub.extra.PORT_NUMBER";
    public static final String ACTION_SAVE_READ_LOCATOR = "com.u2tzjtne.libepub.action.SAVE_READ_LOCATOR";
    public static final String ACTION_CLOSE_EPUBREADER = "com.u2tzjtne.libepub.action.CLOSE_EPUBREADER";
    public static final String ACTION_EPUBREADER_CLOSED = "com.u2tzjtne.libepub.action.EPUBREADER_CLOSED";

    private Context context;
    private Config config;
    private boolean overrideConfig;
    private int portNumber = Constants.DEFAULT_PORT_NUMBER;
    private OnHighlightListener onHighlightListener;
    private ReadLocatorListener readLocatorListener;
    private OnClosedListener onClosedListener;
    private ReadLocator readLocator;

    @Nullable
    public Retrofit retrofit;
    @Nullable
    public R2StreamerApi r2StreamerApi;

    public interface OnClosedListener {
        /**
         * You may call {@link EPUBReader#clear()} in this method, if you wouldn't require to open
         * an epub again from the current activity.
         * Or you may call {@link EPUBReader#stop()} in this method, if you wouldn't require to open
         * an epub again from your application.
         */
        void onFolioReaderClosed();
    }

    private BroadcastReceiver highlightReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            HighlightImpl highlightImpl = intent.getParcelableExtra(HighlightImpl.INTENT);
            HighLight.HighLightAction action = (HighLight.HighLightAction)
                    intent.getSerializableExtra(HighLight.HighLightAction.class.getName());
            if (onHighlightListener != null && highlightImpl != null && action != null) {
                onHighlightListener.onHighlight(highlightImpl, action);
            }
        }
    };

    private BroadcastReceiver readLocatorReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            ReadLocator readLocator =
                    (ReadLocator) intent.getSerializableExtra(EPUBReader.EXTRA_READ_LOCATOR);
            if (readLocatorListener != null) {
                readLocatorListener.saveReadLocator(readLocator);
            }
        }
    };

    private BroadcastReceiver closedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (onClosedListener != null) {
                onClosedListener.onFolioReaderClosed();
            }
        }
    };

    public static EPUBReader get() {

        if (singleton == null) {
            synchronized (EPUBReader.class) {
                if (singleton == null) {
                    if (AppContext.get() == null) {
                        throw new IllegalStateException("-> context == null");
                    }
                    singleton = new EPUBReader(AppContext.get());
                }
            }
        }
        return singleton;
    }

    private EPUBReader() {
    }

    private EPUBReader(Context context) {
        this.context = context;
        DbAdapter.initialize(context);

        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
        localBroadcastManager.registerReceiver(highlightReceiver,
                new IntentFilter(HighlightImpl.BROADCAST_EVENT));
        localBroadcastManager.registerReceiver(readLocatorReceiver,
                new IntentFilter(ACTION_SAVE_READ_LOCATOR));
        localBroadcastManager.registerReceiver(closedReceiver,
                new IntentFilter(ACTION_EPUBREADER_CLOSED));
    }

    public EPUBReader openBook(String assetOrSdcardPath) {
        Intent intent = getIntentFromUrl(assetOrSdcardPath, 0);
        context.startActivity(intent);
        return singleton;
    }

    public EPUBReader openBook(int rawId) {
        Intent intent = getIntentFromUrl(null, rawId);
        context.startActivity(intent);
        return singleton;
    }

    public EPUBReader openBook(String assetOrSdcardPath, String bookId) {
        Intent intent = getIntentFromUrl(assetOrSdcardPath, 0);
        intent.putExtra(EXTRA_BOOK_ID, bookId);
        context.startActivity(intent);
        return singleton;
    }

    public EPUBReader openBook(int rawId, String bookId) {
        Intent intent = getIntentFromUrl(null, rawId);
        intent.putExtra(EXTRA_BOOK_ID, bookId);
        context.startActivity(intent);
        return singleton;
    }

    private Intent getIntentFromUrl(String assetOrSdcardPath, int rawId) {

        Intent intent = new Intent(context, ReadActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Config.INTENT_CONFIG, config);
        intent.putExtra(Config.EXTRA_OVERRIDE_CONFIG, overrideConfig);
        intent.putExtra(EXTRA_PORT_NUMBER, portNumber);
        intent.putExtra(ReadActivity.EXTRA_READ_LOCATOR, (Parcelable) readLocator);

        if (rawId != 0) {
            intent.putExtra(ReadActivity.INTENT_EPUB_SOURCE_PATH, rawId);
            intent.putExtra(ReadActivity.INTENT_EPUB_SOURCE_TYPE,
                    ReadActivity.EpubSourceType.RAW);
        } else if (assetOrSdcardPath.contains(Constants.ASSET)) {
            intent.putExtra(ReadActivity.INTENT_EPUB_SOURCE_PATH, assetOrSdcardPath);
            intent.putExtra(ReadActivity.INTENT_EPUB_SOURCE_TYPE,
                    ReadActivity.EpubSourceType.ASSETS);
        } else {
            intent.putExtra(ReadActivity.INTENT_EPUB_SOURCE_PATH, assetOrSdcardPath);
            intent.putExtra(ReadActivity.INTENT_EPUB_SOURCE_TYPE,
                    ReadActivity.EpubSourceType.SD_CARD);
        }

        return intent;
    }

    /**
     * Pass your configuration and choose to override it every time or just for first execution.
     *
     * @param config         custom configuration.
     * @param overrideConfig true will override the config, false will use either this
     *                       config if it is null in application context or will fetch previously
     *                       saved one while execution.
     */
    public EPUBReader setConfig(Config config, boolean overrideConfig) {
        this.config = config;
        this.overrideConfig = overrideConfig;
        return singleton;
    }

    public EPUBReader setPortNumber(int portNumber) {
        this.portNumber = portNumber;
        return singleton;
    }

    public static void initRetrofit(String streamerUrl) {

        if (singleton == null || singleton.retrofit != null) {
            return;
        }

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)
                .build();

        singleton.retrofit = new Retrofit.Builder()
                .baseUrl(streamerUrl)
                .addConverterFactory(new QualifiedTypeConverterFactory(
                        JacksonConverterFactory.create(),
                        GsonConverterFactory.create()))
                .client(client)
                .build();

        singleton.r2StreamerApi = singleton.retrofit.create(R2StreamerApi.class);
    }

    public EPUBReader setOnHighlightListener(OnHighlightListener onHighlightListener) {
        this.onHighlightListener = onHighlightListener;
        return singleton;
    }

    public EPUBReader setReadLocatorListener(ReadLocatorListener readLocatorListener) {
        this.readLocatorListener = readLocatorListener;
        return singleton;
    }

    public EPUBReader setOnClosedListener(OnClosedListener onClosedListener) {
        this.onClosedListener = onClosedListener;
        return singleton;
    }

    public EPUBReader setReadLocator(ReadLocator readLocator) {
        this.readLocator = readLocator;
        return singleton;
    }

    public void saveReceivedHighLights(List<HighLight> highlights,
                                       OnSaveHighlight onSaveHighlight) {
        new SaveReceivedHighlightTask(onSaveHighlight, highlights).execute();
    }

    /**
     * Closes all the activities related to EPUBReader.
     * After closing all the activities of EPUBReader, callback can be received in
     * {@link OnClosedListener#onFolioReaderClosed()} if implemented.
     * Developer is still bound to call {@link #clear()} or {@link #stop()}
     * for clean up if required.
     */
    public void close() {
        Intent intent = new Intent(EPUBReader.ACTION_CLOSE_EPUBREADER);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    /**
     * Nullifies readLocator and listeners.
     * This method ideally should be used in onDestroy() of Activity or Fragment.
     * Use this method if you want to use EPUBReader singleton instance again in the application,
     * else use {@link #stop()} which destruct the EPUBReader singleton instance.
     */
    public static synchronized void clear() {

        if (singleton != null) {
            singleton.readLocator = null;
            singleton.onHighlightListener = null;
            singleton.readLocatorListener = null;
            singleton.onClosedListener = null;
        }
    }

    /**
     * Destructs the EPUBReader singleton instance.
     * Use this method only if you are sure that you won't need to use
     * EPUBReader singleton instance again in application, else use {@link #clear()}.
     */
    public static synchronized void stop() {

        if (singleton != null) {
            DbAdapter.terminate();
            singleton.unregisterListeners();
            singleton = null;
        }
    }

    private void unregisterListeners() {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
        localBroadcastManager.unregisterReceiver(highlightReceiver);
        localBroadcastManager.unregisterReceiver(readLocatorReceiver);
        localBroadcastManager.unregisterReceiver(closedReceiver);
    }
}
