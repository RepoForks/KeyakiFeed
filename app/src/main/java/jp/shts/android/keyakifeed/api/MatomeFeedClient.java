package jp.shts.android.keyakifeed.api;

import com.loopj.android.http.AsyncHttpClient;

import jp.shts.android.keyakifeed.entities.FeedItemList;

public class MatomeFeedClient {

    private static final String TAG = MatomeFeedClient.class.getSimpleName();

    private static final String[] URL_LIST = {
            "http://keyaki46.2chblog.jp/index.rdf",
            "http://torizaka46.2chblog.jp/index.rdf",
            "http://keyakizakamatome.blog.jp/index.rdf",
            "http://keyakizaka1.blog.jp/index.rdf",
    };

    private static AsyncHttpClient client = new AsyncHttpClient();

    /**
     * http://square.github.io/otto/
     * use ThreadEnforcer
     */
    public static class GetMatomeFeedCallback {
        public FeedItemList feedItemList;
        public Exception e;
        public GetMatomeFeedCallback(FeedItemList feedItemList, Exception e) {
            this.feedItemList = feedItemList;
            this.e = e;
            // 読み込み完了時にソートしておく
            if (!hasError()) { feedItemList.sort(); }
        }
        public boolean hasError() { return e != null || feedItemList == null || feedItemList.isEmpty(); }
    }

    public static void get() {
        get(URL_LIST);
    }

    private static void get(String[] urls) {
        final MatomeFeedClientResponseHandler responseHandler
                = new MatomeFeedClientResponseHandler(urls.length);
        for (String url : urls) {
            client.get(url, responseHandler);
        }
    }

}