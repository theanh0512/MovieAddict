package pham.ntu.grabtheater;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

import pham.ntu.grabtheater.data.MovieContract.MovieEntry;
import pham.ntu.grabtheater.data.MovieContract.NơwPlayingEntry;
import pham.ntu.grabtheater.data.MovieContract.PopularEntry;
import pham.ntu.grabtheater.data.MovieContract.TopRatedEntry;

/**
 * Created by Pham on 12/24/2015.
 */
class GetDataTask extends AsyncTask<String, Void, List<String>> {
    private final String LOG_TAG = GetDataTask.class.getSimpleName();
    private final Context mContext;
    //final String PREFIX_API_KEY = "?api_key=";
    private String result = "results";
    private String mUrlString = "http://api.themoviedb.org/3/movie/";
    private String page = "&page=";
    private boolean containPages = true;
    private String additionalUrl;
    private int pageNum;

    GetDataTask(Context context, String additionalUrl, boolean containPages) {
        mContext = context;
        this.additionalUrl = additionalUrl;
        this.containPages = containPages;
        if(!isOnline()) {
            displayNoInternetDialog();
        }
    }

    GetDataTask(Context context, String additionalUrl, boolean containPages, int pageNum) {
        mContext = context;
        this.additionalUrl = additionalUrl;
        this.containPages = containPages;
        this.pageNum = pageNum;
        if(!isOnline()) {
            displayNoInternetDialog();
        }
    }

    private void displayNoInternetDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle(mContext.getString(R.string.alert_title))
                    .setMessage(mContext.getString(R.string.alert_message))
                    .setCancelable(false)
                    .setNegativeButton(mContext.getString(R.string.alert_button), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        } catch (Exception e) {
            Log.d(MainActivity.class.getSimpleName(), "Show Dialog: " + e.getMessage());
        }
    }

    //Check whether there is an internet connection
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static String convertArrayToString(int[] array) {
        String strSeparator = "__,__";
        String str = "";
        if (array != null) {
            for (int i = 0; i < array.length; i++) {
                str = str + array[i];
                if (i < array.length - 1) {
                    str = str + strSeparator;
                }
            }
        }
        return str;
    }

    public static int[] convertStringToArray(String str) {
        String strSeparator = "__,__";
        String[] arr = str.split(strSeparator);
        int[] arrInt = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            arrInt[i] = Integer.parseInt(arr[i]);
        }
        return arrInt;
    }

    @Override
    protected List<String> doInBackground(String... strings) {
        mUrlString = mUrlString + additionalUrl + Config.PREFIX_API_KEY + Config.THE_MOVIE_DB_API_KEY;
        if (containPages) {
            page += pageNum;
            mUrlString += page;
        }
        List<String> jsonData = new ArrayList<>();
        URL url;
        try {
            url = new URL(mUrlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream is = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();

            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            result = sb.toString();

            //parse json data
            parseJsonData(jsonData);
            Log.i(LOG_TAG, "URL Queried: " + mUrlString);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return jsonData;
    }

    private void parseJsonData(List<String> jsonArray) {
        try {
            JSONObject jo = new JSONObject(result);
            if (containPages && Objects.equals(additionalUrl, MainActivity.additionalUrl)) {
                TabNowShowingFragment.totalPages = jo.getInt("total_pages");
            }
            JSONArray jArray = jo.getJSONArray("results");
            jsonArray.clear();

            if (Objects.equals(additionalUrl, MainActivity.additionalUrl))
                TabNowShowingFragment.moviesList.clear();
            if (additionalUrl.contains("similar")) DetailFragment.moviesList.clear();

            int len = jArray.length();
            Vector<ContentValues> cVVector = new Vector<>(len);
            Vector<ContentValues> cVVectorOrder = new Vector<>(len);
            if (jArray.length() == 1) len = 2;
            for (int i = 0; i < len; i++) {
                JSONObject json = jArray.getJSONObject(i);
                if (Objects.equals(additionalUrl, MainActivity.additionalUrl) || additionalUrl ==
                        DetailFragment.additionalUrl) {
                    String poster_path = json.getString("poster_path");
                    boolean adult = json.getBoolean("adult");
                    String overview = json.getString("overview");
                    String release_date = json.getString("release_date");
                    JSONArray genre = json.getJSONArray("genre_ids");
                    int[] genre_ids = null;
                    if (genre.length() != 0) {
                        genre_ids = new int[genre.length()];
                        for (int j = 0; j < genre.length(); j++) {
                            genre_ids[j] = genre.getInt(j);
                        }
                    }
                    String stringGenreIds = convertArrayToString(genre_ids);
                    int id = json.getInt("id");
                    String original_title = json.getString("original_title");
                    String original_language = json.getString("original_language");
                    String title = json.getString("title");
                    String backdrop_path = json.getString("backdrop_path");
                    double popularity = json.getDouble("popularity");
                    int vote_count = json.getInt("vote_count");
                    boolean video = json.getBoolean("video");
                    double vote_average = json.getDouble("vote_average");

                    ContentValues movieValues = new ContentValues();
                    movieValues.put(MovieEntry.COLUMN_MOVIE_ID, id);
                    movieValues.put(MovieEntry.COLUMN_POSTER_PATH, poster_path);
                    movieValues.put(MovieEntry.COLUMN_IS_ADULT, (adult = true) ? 1 : 0);
                    movieValues.put(MovieEntry.COLUMN_OVERVIEW, overview);
                    movieValues.put(MovieEntry.COLUMN_RELEASE_DATE, release_date);
                    movieValues.put(MovieEntry.COLUMN_GENRE_IDS, stringGenreIds);
                    movieValues.put(MovieEntry.COLUMN_ORIGINAL_TITLE, original_title);
                    movieValues.put(MovieEntry.COLUMN_ORIGINAL_LANGUAGE, original_language);
                    movieValues.put(MovieEntry.COLUMN_TITLE, title);
                    movieValues.put(MovieEntry.COLUMN_BACKDROP_PATH, backdrop_path);
                    movieValues.put(MovieEntry.COLUMN_POPULARITY, popularity);
                    movieValues.put(MovieEntry.COLUMN_VOTE_COUNT, vote_count);
                    movieValues.put(MovieEntry.COLUMN_HAS_VIDEO, (video = true) ? 1 : 0);
                    movieValues.put(MovieEntry.COLUMN_VOTE_AVERAGE, vote_average);

                    if (additionalUrl.contains("playing")) {
                        ContentValues nowPlayingValues = new ContentValues();
                        nowPlayingValues.put(NơwPlayingEntry.COLUMN_MOVIE_KEY, id);
                        nowPlayingValues.put(NơwPlayingEntry.COLUMN_POSITION, i);
                        nowPlayingValues.put(NơwPlayingEntry.COLUMN_PAGE_NUMBER, pageNum);

                        cVVectorOrder.add(nowPlayingValues);
                    } else if (additionalUrl.contains("popular")) {
                        ContentValues popularValues = new ContentValues();
                        popularValues.put(PopularEntry.COLUMN_MOVIE_KEY, id);
                        popularValues.put(PopularEntry.COLUMN_POSITION, i);
                        popularValues.put(PopularEntry.COLUMN_PAGE_NUMBER, pageNum);

                        cVVectorOrder.add(popularValues);
                    } else if (additionalUrl.contains("rated")) {
                        ContentValues topRatedValues = new ContentValues();
                        topRatedValues.put(TopRatedEntry.COLUMN_MOVIE_KEY, id);
                        topRatedValues.put(TopRatedEntry.COLUMN_POSITION, i);
                        topRatedValues.put(TopRatedEntry.COLUMN_PAGE_NUMBER, pageNum);

                        cVVectorOrder.add(topRatedValues);
                    }
                    cVVector.add(movieValues);

                    //jsonArray.add(s);
                    if (Objects.equals(additionalUrl, MainActivity.additionalUrl)) {
                        TabNowShowingFragment.moviesList.add(new Movie(adult, backdrop_path, genre_ids, id, original_language, original_title,
                                overview, release_date, poster_path, popularity, title, video, vote_average, vote_count));
                    } else {
                        DetailFragment.moviesList.add(new Movie(adult, backdrop_path, genre_ids, id, original_language, original_title,
                                overview, release_date, poster_path, popularity, title, video, vote_average, vote_count));
                    }
                } else {
                    String id = json.getString("id");
                    String iso_639_1 = json.getString("iso_639_1");
                    String key = json.getString("key");
                    String name = json.getString("name");
                    String site = json.getString("site");
                    int size = json.getInt("size");
                    String type = json.getString("type");
                    DetailFragment.trailersList.add(new Video(id, iso_639_1, key, name, site, size, type));
                }
            }

            //testing
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                mContext.getContentResolver().bulkInsert(MovieEntry.CONTENT_URI, cvArray);
            }
            if (cVVectorOrder.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVectorOrder.size()];
                cVVectorOrder.toArray(cvArray);
                if (additionalUrl.contains("playing"))
                    mContext.getContentResolver().bulkInsert(NơwPlayingEntry.CONTENT_URI, cvArray);
                else if (additionalUrl.contains("popular"))
                    mContext.getContentResolver().bulkInsert(PopularEntry.CONTENT_URI, cvArray);
                else if (additionalUrl.contains("rated"))
                    mContext.getContentResolver().bulkInsert(TopRatedEntry.CONTENT_URI, cvArray);
            }

            if (Objects.equals(additionalUrl, DetailFragment.additionalUrl))
                DetailFragment.mMovieImageAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            // TODO: handle exception
            Log.e("log_tag", "Error Parsing Data " + e.toString());
        }
    }

    @Override
    protected void onPostExecute(List<String> strings) {
//        if (Objects.equals(additionalUrl, MainActivity.additionalUrl))
//            TabNowShowingFragment.mMovieImageAdapterWithCursorAdapter.notifyDataSetChanged();
        if (Objects.equals(additionalUrl, DetailFragment.additionalUrl))
            DetailFragment.mMovieImageAdapter.notifyDataSetChanged();
    }
}
