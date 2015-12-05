package jp.shts.android.keyakifeed.models;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Date;
import java.util.List;

import jp.shts.android.keyakifeed.models.eventbus.BusHolder;

@ParseClassName("Entry")
public class Entry extends ParseObject {

    private static final String TAG = Entry.class.getSimpleName();

    public static ParseQuery<Entry> getQuery(int limit, int skip) {
        ParseQuery<Entry> query = ParseQuery.getQuery(Entry.class);
        query.orderByDescending("published");
        query.setLimit(limit);
        query.setSkip(skip);
        return query;
    }

    public static void all(ParseQuery<Entry> query) {
        query.findInBackground(new FindCallback<Entry>() {
            @Override
            public void done(List<Entry> entries, ParseException e) {
                BusHolder.get().post(new GetEntriesCallback.All(entries, e));
            }
        });
    }

    public static void next(ParseQuery<Entry> query) {
        query.findInBackground(new FindCallback<Entry>() {
            @Override
            public void done(List<Entry> entries, ParseException e) {
                BusHolder.get().post(new GetEntriesCallback.Next(entries, e));
            }
        });
    }

    /**
     * For event bus callbacks
     */
    public static class GetEntriesCallback {
        final public List<Entry> entries;
        final public ParseException e;
        GetEntriesCallback(List<Entry> entries, ParseException e) {
            this.entries = entries;
            this.e = e;
        }
        public static class All extends GetEntriesCallback {
            All(List<Entry> entries, ParseException e) {
                super(entries, e);
            }
        }
        public static class Next extends GetEntriesCallback {
            Next(List<Entry> entries, ParseException e) {
                super(entries, e);
            }
        }
    }

    public String getAuthor() {
        return getString("author");
    }

    public String getAuthorId() {
        return getString("author_id");
    }

    public String getAuthorImageUrl() {
        return getString("author_image_url");
    }

    public String getBody() {
        return getString("body");
    }

    public String getDay() {
        return getString("day");
    }

    public List<String> getImageUrlList() {
        return getList("image_url_list");
    }

    public Date getPublishedDate() {
        return getDate("published");
    }

    public String getTitle() {
        return getString("title");
    }

    public String getWeek() {
        return getString("week");
    }

    public String getYearMonth() {
        return getString("yearmonth");
    }
}
