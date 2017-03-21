package com.androidexample.bookhub;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static com.androidexample.bookhub.R.id.editText;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Books>> {
    public static final String LOG_TAG = MainActivity.class.getName();

    private static final int BOOK_LOADER_ID = 1;
    private static String GPA_REQUEST_URL;
    public View coordinatorLayoutView;
    public ListView booksListView;
    public String inputQuery;
    public ProgressBar mProgressBar;
    public ImageView noNet;
    public ImageView noResult;
    public EditText input;
    public Snackbar snackbar;
    public View sbView;
    public ImageView intro;
    public TextView introText;
    public ConnectivityManager connectivityManager;
    public NetworkInfo networkInfo;
    private BooksAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intro = (ImageView) findViewById(R.id.intro_image);
        introText = (TextView)findViewById(R.id.intro_text);
        coordinatorLayoutView = findViewById(R.id.snackbar);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        booksListView = (ListView) findViewById(R.id.list);
        noNet = (ImageView) findViewById(R.id.no_net_image);
        noResult = (ImageView) findViewById(R.id.no_result);
        input = (EditText) findViewById(editText);
        mProgressBar.setVisibility(View.GONE);
        mProgressBar.getIndeterminateDrawable().setColorFilter(Color.rgb(238, 238, 238), PorterDuff.Mode.MULTIPLY);
        noNet.setVisibility(View.GONE);
        noResult.setVisibility(View.GONE);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        introText.setTypeface(typeface);

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();
        Log.v("mainActivity", "Connectivity check");
        if (networkInfo != null && networkInfo.isConnected()) {
            Log.i(LOG_TAG, "Net Is connected");
        } else {
            intro.setVisibility(View.GONE);
            introText.setVisibility(View.GONE);
            noResult.setVisibility(View.GONE);
            noNet.setVisibility(View.VISIBLE);
            coordinatorLayoutView.setVisibility(View.VISIBLE);
            snackbar = Snackbar.make(coordinatorLayoutView, R.string.snackbar_text_noInternet, Snackbar.LENGTH_LONG).setAction(R.string.snackbar_action_setting, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Settings.ACTION_SETTINGS);
                    startActivity(intent);
                }
            });
            sbView = snackbar.getView();
            sbView.setBackgroundColor(Color.rgb(27, 39, 50));
            snackbar.setActionTextColor(Color.rgb(239, 83, 80));
            snackbar.show();
            connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }

        input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch();
                    return true;
                }
                return false;
            }
        });
    }

    private void performSearch() {
        intro.setVisibility(View.GONE);
        introText.setVisibility(View.GONE);
        noNet.setVisibility(View.GONE);
        noResult.setVisibility(View.GONE);
        booksListView.setVisibility(View.VISIBLE);

        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }

        inputQuery = input.getText().toString().trim();
        if (TextUtils.isEmpty(inputQuery)) {
            input.setError(getString(R.string.input_error));

        } else {

            inputQuery = inputQuery.replace(" ", "+");
            Log.i(LOG_TAG, "Input:" + inputQuery);
            GPA_REQUEST_URL = "https://www.googleapis.com/books/v1/volumes?q=" + inputQuery + "&orderBy=newest&maxResults=20";
            Log.v("URL:", GPA_REQUEST_URL);
            //new BooksAsyncTask().execute(GPA_REQUEST_URL);

            // Create a new adapter that takes an empty list of books as input
            mAdapter = new BooksAdapter(MainActivity.this, new ArrayList<Books>());

            // Set the adapter on the {@link ListView}
            // so the list can be populated in the user interface
            booksListView.setAdapter(mAdapter);
           /* BooksAsyncTask task = new BooksAsyncTask();
            task.execute(GPA_REQUEST_URL);

           */ // Set an item click listener on the ListView, which sends an intent to a web browser
            // to open a website with more information about the selected book.

            booksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    // Find the current book that was clicked on
                    Books currentBook = mAdapter.getItem(position);

                    // Convert the String URL into a URI object (to pass into the Intent constructor)
                    Uri booksUri = Uri.parse(currentBook.getUrl());

                    // Create a new intent to view the book URI
                    Intent websiteIntent = new Intent(Intent.ACTION_VIEW, booksUri);

                    // Send the intent to launch a new activity
                    startActivity(websiteIntent);
                }
            });
            connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            networkInfo = connectivityManager.getActiveNetworkInfo();
            Log.v("mainActivity", "Connectivity check");
            if (networkInfo != null && networkInfo.isConnected()) {

                Log.i(LOG_TAG, "If network is there");
                booksListView.setVisibility(View.VISIBLE);
                noNet.setVisibility(View.GONE);
                noResult.setVisibility(View.GONE);
                // Get a reference to the LoaderManager, in order to interact with loaders.
                LoaderManager loaderManager = getLoaderManager();
                Log.i(LOG_TAG, "Calling init loader");

                // Initialize the loader. Pass in the int ID constant defined above and pass in null for
                // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
                // because this activity implements the LoaderCallbacks interface).
                loaderManager.initLoader(BOOK_LOADER_ID, null, MainActivity.this);
                loaderManager.restartLoader(BOOK_LOADER_ID, null, MainActivity.this);
            } else {
                mProgressBar.setVisibility(View.GONE);
                booksListView.setVisibility(View.GONE);
                noResult.setVisibility(View.GONE);
                noNet.setVisibility(View.VISIBLE);
                snackbar = Snackbar.make(coordinatorLayoutView, R.string.snackbar_text_noInternet, Snackbar.LENGTH_LONG).setAction(R.string.snackbar_action_setting, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Settings.ACTION_SETTINGS);
                        startActivity(intent);
                    }
                });
                sbView = snackbar.getView();
                sbView.setBackgroundColor(Color.rgb(27, 39, 50));
                snackbar.setActionTextColor(Color.rgb(239, 83, 80));
                snackbar.show();
                Log.i(LOG_TAG, "No Internet");
            }
        }
    }
/*
    private class BooksAsyncTask extends AsyncTask<String, Void, List<Books>> {

        */

    /**
     * This method runs on a background thread and performs the network request.
     * We should not update the UI from a background thread, so we return a list of
     * {@link Books}s as the result.
     *//*
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Toast.makeText(MainActivity.this, "Please Wait", Toast.LENGTH_SHORT).show();
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Books> doInBackground(String... urls) {
            // Don't perform the request if there are no URLs, or the first URL is null.

            if (urls.length < 1 || urls[0] == null) {
                return null;
            }

            List<Books> result = QueryUtils.fetchBooksData(urls[0]);
            return result;
        }

        @Override
        protected void onPostExecute(List<Books> data) {
            // Clear the adapter of previous book data
            mProgressBar.setVisibility(View.GONE);
            mAdapter.clear();
            Log.v("main", "On post Execute");
            // If there is a valid list of {@link book}s, then add them to the adapter's
            // data set. This will trigger the ListView to update.
            if (data != null && !data.isEmpty()) {
                mAdapter.addAll(data);
                snackbar = Snackbar.make(coordinatorLayoutView, R.string.snackbar_text, Snackbar.LENGTH_LONG).setAction(R.string.snackbar_action, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        coordinatorLayoutView.setVisibility(View.GONE);
                    }
                });
                sbView = snackbar.getView();
                sbView.setBackgroundColor(Color.rgb(27, 39, 50));
                snackbar.setActionTextColor(Color.rgb(255, 241, 118));
                snackbar.show();

                coordinatorLayoutView.setVisibility(View.VISIBLE);
            }


        }
    }*/
    @Override
    public Loader<List<Books>> onCreateLoader(int id, Bundle args) {
        Log.i(LOG_TAG, "onCreateLoader called");
        noResult.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        return new BooksLoader(this, GPA_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<Books>> loader, List<Books> books) {
        Log.i(LOG_TAG, "onLoadFinished called");

        mAdapter.clear();
        if (books != null && !books.isEmpty()) {
            mAdapter.addAll(books);
            snackbar = Snackbar.make(coordinatorLayoutView, R.string.snackbar_text, Snackbar.LENGTH_LONG).setAction(R.string.snackbar_action, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    coordinatorLayoutView.setVisibility(View.GONE);
                }
            });
            sbView = snackbar.getView();
            sbView.setBackgroundColor(Color.rgb(27, 39, 50));
            snackbar.setActionTextColor(Color.rgb(255, 241, 118));
            snackbar.show();

            coordinatorLayoutView.setVisibility(View.VISIBLE);

        } else {
            noResult.setVisibility(View.VISIBLE);
            snackbar = Snackbar.make(coordinatorLayoutView, R.string.snackbar_text_noResult, Snackbar.LENGTH_LONG).setAction(R.string.snackbar_action, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    coordinatorLayoutView.setVisibility(View.GONE);
                }
            });
            sbView = snackbar.getView();
            sbView.setBackgroundColor(Color.rgb(27, 39, 50));
            snackbar.setActionTextColor(Color.rgb(255, 241, 118));
            snackbar.show();
        }
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<List<Books>> loader) {
        Log.i(LOG_TAG, "onLoadReset called");
        mAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent homeIntent = new Intent(this, MainActivity.class);
            startActivity(homeIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} // Activity end