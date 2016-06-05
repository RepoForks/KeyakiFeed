package jp.shts.android.keyakifeed.utils;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.shts.android.keyakifeed.models.eventbus.BusHolder;

public class SimpleImageDownloader extends ImageDownloader {

    private static final String TAG = SimpleImageDownloader.class.getSimpleName();

    public SimpleImageDownloader(Context context, String url) {
        super(context, new ArrayList<>(Arrays.asList(url)));
    }

    public SimpleImageDownloader(Context context, List<String> urls) {
        super(context, urls);
    }

    public static class Callback {
        public File file;
        Callback(File file) { this.file = file; }
    }

    @Override
    public void onResponse(Response response) {
        Log.e(TAG, "onResponse: ");
        if (response.result != Response.Result.SUCCESS) {
            Log.e(TAG, "failed to download image : response("
                    + response.toString() + ")");
        }
        BusHolder.get().post(new SimpleImageDownloader.Callback(response.file));
    }

}
