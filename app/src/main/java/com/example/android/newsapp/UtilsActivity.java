package com.example.android.newsapp;

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
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.ArrayList;

public class UtilsActivity {
    private static final String LOG_TAG = UtilsActivity.class.getSimpleName();


    /**
     * Create a private constructor because no one should ever create a {@link UtilsActivity} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name UtilsActivity (and an object instance of UtilsActivity is not needed).
     */
    private UtilsActivity() {
    }

    /**
     * Query the USGS dataset and return an {@link Article} object to represent a single article.
     */
    public static ArrayList<Article> fetchArticleData(String requestUrl) {
        Log.i(LOG_TAG, "fetchArticleDate()");
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        // Extract relevant fields from the JSON response and create an {@link Event} object

        // Return the {@link Event}
        return extractArticles(jsonResponse);
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
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
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the article JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
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
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link Article} objects that has been built up from
     * parsing a JSON response.
     *
     * @param jsonResponse By now, the jsonResponse should no longer be empty... if it is, something went wrong.
     */
    public static ArrayList<Article> extractArticles(String jsonResponse) {

        // Create an empty ArrayList that we can start adding articles to
        ArrayList<Article> articles = new ArrayList<>();
        int articleNumber = 0;
        try {

            // TODO: Parse the response given by the SAMPLE_JSON_RESPONSE string and
            // build up a list of Article objects with the corresponding data.
            JSONObject rootObject = new JSONObject(jsonResponse);
            JSONObject response = rootObject.getJSONObject("response");
            JSONArray articleArray = response.optJSONArray("results");

            for (int i = 0; i < articleArray.length(); i++) {
                JSONObject article = (JSONObject) articleArray.get(i);
                JSONObject field = article.getJSONObject("fields");
                JSONArray tags = article.getJSONArray("tags");
                JSONObject articleContributor = tags.getJSONObject(0);
                String sectionName = article.optString("sectionName", "");
                String articleTitle = article.getString("webTitle");
                String imageURL = field.getString("thumbnail");
                String time = article.getString("webPublicationDate");
                String url = article.getString("webUrl");
                String contributor = articleContributor.getString("webTitle");


                long convertedTime = convertTime(time);

                articles.add(new Article(sectionName, articleTitle, url, imageURL, contributor, convertedTime));
                articleNumber = i;
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(LOG_TAG, "Problem parsing the article JSON results on article " + articleNumber, e);
        }

        // Return the list of articles
        return articles;
    }

    private static long convertTime(String time) {
        if (time == null) return 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            OffsetDateTime odt = OffsetDateTime.parse(time);
            return odt.toInstant().toEpochMilli();
        }
        return 0;
    }


}
