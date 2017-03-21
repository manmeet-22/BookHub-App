package com.androidexample.bookhub;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

import static com.androidexample.bookhub.MainActivity.LOG_TAG;

/**
 * Created by MANI on 2/6/2017.
 */

public class BooksLoader extends AsyncTaskLoader<List<Books>> {
    /**
     * Query URL
     */
    private String mUrl;

    public BooksLoader(Context context, String url) {
        super(context);
        Log.i(LOG_TAG, "BooksLoader called");
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        Log.i(LOG_TAG, "onStartLoading called");
        forceLoad();
    }

    @Override
    public List<Books> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        List<Books> Books = QueryUtils.fetchBooksData(mUrl);
        return Books;
    }
}