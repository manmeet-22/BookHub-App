package com.androidexample.bookhub;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static com.androidexample.bookhub.MainActivity.LOG_TAG;

/**
 * Helper methods related to requesting and receiving books data from GPA.
 */
public final class QueryUtils {
    /**
     * Return a list of {@link Books} objects that has been built up from
     * parsing the given JSON response.
     */
    private static final String TAG_TITLE = "title";
    private static final String TAG_AUTHORS = "authors";

    private static List<Books> extractFeatureFromJson(String booksJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(booksJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding books to
        List<Books> books = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(booksJSON);

            // Extract the JSONArray associated with the key called "features",
            // which represents a list of features (or books).
            JSONArray booksArray = baseJsonResponse.getJSONArray("items");

            // For each book in the bookArray, create an {@link Books} object
            for (int i = 0; i < booksArray.length(); i++) {

                // Get a single books at position i within the list of books
                JSONObject currentBook = booksArray.getJSONObject(i);

                // For a given book, extract the JSONObject associated with the
                // key called "properties", which represents a list of all properties
                // for that book.
                String title, authors, date, url, imageUrl = null;

                JSONObject BookDetails = currentBook.getJSONObject("volumeInfo");
                if (BookDetails.has("authors")) {
                    authors = (BookDetails.getString("authors"));
                    authors = authors.replace("[\"", "");
                    authors = authors.replace("\"]", "");
                    authors = authors.replace("\",\"", " ,");
                } else {
                    authors = "No Authors Found";
                }
                title = BookDetails.getString("title").trim();
                date = BookDetails.getString("publishedDate");
                url = BookDetails.getString("infoLink");
                imageUrl = "https://www.prozis.com/themes/prozis/imgs/no-image.jpg";

                if (BookDetails.has("imageLinks")) {
                    JSONObject imageJsonResponse = BookDetails.getJSONObject("imageLinks");
                    imageUrl = imageJsonResponse.getString("smallThumbnail");
                }

                Log.v(TAG_TITLE, title);
                Log.v(TAG_AUTHORS, authors);

                Books book = new Books(title, authors, date, url, imageUrl);

                // Add the new {@link Books} to the list of books.
                books.add(book);
            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the book JSON results", e);

            e.printStackTrace();
        }
        // Return the list of books
        return books;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
                return jsonResponse;

            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the book JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }
    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    public static List<Books> fetchBooksData(String requestURL) {

        Log.i(LOG_TAG, "fetchBookData called");
       /*try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        URL url = createUrl(requestURL);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making HTTP request.", e);
        }
        List<Books> books = extractFeatureFromJson(jsonResponse);
        return books;
    }
}