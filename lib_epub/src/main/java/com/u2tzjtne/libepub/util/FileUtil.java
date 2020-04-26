package com.u2tzjtne.libepub.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import com.u2tzjtne.libepub.Constants;
import com.u2tzjtne.libepub.ui.activity.ReadActivity;

import java.io.*;

/**
 * Created by Mahavir on 12/15/16.
 */

public class FileUtil {
    private static final String TAG = FileUtil.class.getSimpleName();
    private static final String FOLIO_READER_ROOT = "libepub";

    public static String saveEpubFileAndLoadLazyBook(final Context context,
                                                     ReadActivity.EpubSourceType epubSourceType,
                                                     String epubFilePath,
                                                     int epubRawId, String epubFileName) {
        String filePath;
        InputStream epubInputStream;
        boolean isFolderAvailable;
        try {
            isFolderAvailable = isFolderAvailable(epubFileName);
            filePath = getFolioEpubFilePath(epubSourceType, epubFilePath, epubFileName);

            if (!isFolderAvailable) {
                if (epubSourceType.equals(ReadActivity.EpubSourceType.RAW)) {
                    epubInputStream = context.getResources().openRawResource(epubRawId);
                    saveTempEpubFile(filePath, epubFileName, epubInputStream);
                } else if (epubSourceType.equals(ReadActivity.EpubSourceType.ASSETS)) {
                    AssetManager assetManager = context.getAssets();
                    epubFilePath = epubFilePath.replaceAll(Constants.ASSET, "");
                    epubInputStream = assetManager.open(epubFilePath);
                    saveTempEpubFile(filePath, epubFileName, epubInputStream);
                } else {
                    filePath = epubFilePath;
                }
            }
            return filePath;
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }

        return null;
    }

    public static String getFolioEpubFolderPath(String epubFileName) {
        return Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/" + FOLIO_READER_ROOT + "/" + epubFileName;
    }

    public static String getFolioEpubFilePath(ReadActivity.EpubSourceType sourceType, String epubFilePath, String epubFileName) {
        if (ReadActivity.EpubSourceType.SD_CARD.equals(sourceType)) {
            return epubFilePath;
        } else {
            return getFolioEpubFolderPath(epubFileName) + "/" + epubFileName + ".epub";
        }
    }

    private static boolean isFolderAvailable(String epubFileName) {
        File file = new File(getFolioEpubFolderPath(epubFileName));
        return file.isDirectory();
    }

    public static String getEpubFilename(Context context, ReadActivity.EpubSourceType epubSourceType,
                                         String epubFilePath, int epubRawId) {
        String epubFileName;
        if (epubSourceType.equals(ReadActivity.EpubSourceType.RAW)) {
            Resources res = context.getResources();
            epubFileName = res.getResourceEntryName(epubRawId);
        } else {
            String[] temp = epubFilePath.split("/");
            epubFileName = temp[temp.length - 1];
            int fileMaxIndex = epubFileName.length();
            epubFileName = epubFileName.substring(0, fileMaxIndex - 5);
        }

        return epubFileName;
    }

    public static Boolean saveTempEpubFile(String filePath, String fileName, InputStream inputStream) {
        OutputStream outputStream = null;
        File file = new File(filePath);
        try {
            if (!file.exists()) {
                File folder = new File(getFolioEpubFolderPath(fileName));
                folder.mkdirs();

                outputStream = new FileOutputStream(file);
                int read = 0;
                byte[] bytes = new byte[inputStream.available()];

                while ((read = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }
            } else {
                return true;
            }
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return false;
    }

    public static String getExtensionUppercase(String path) {
        if (TextUtils.isEmpty(path))
            return null;
        int lastIndexOfDot = path.lastIndexOf('.');
        if (lastIndexOfDot == -1)
            return null;
        return path.substring(lastIndexOfDot + 1).toUpperCase();
    }
}
