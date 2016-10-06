package pham.ntu.grabtheater;

/**
 * Created by Administrator PC on 3/31/2016.
 */
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 12/24/2015.
 */
public class GetDataTask extends AsyncTask<String, Void, List<String>> {
    private final String LOG_TAG = GetDataTask.class.getSimpleName();
    //final String PREFIX_API_KEY = "?api_key=";
    String result = "results";
    String mUrlString = "http://api.themoviedb.org/3/movie/";
    String page = "&page=";
    boolean containPages = true;
    private final Context mcontext;
    String additionalUrl;
    int pageNum;

    public GetDataTask(Context context, String additionalUrl, boolean containPages) {
        mcontext = context;
        this.additionalUrl = additionalUrl;
        this.containPages = containPages;
    }

    public GetDataTask(Context context, String additionalUrl, boolean containPages,int pageNum) {
        mcontext = context;
        this.additionalUrl = additionalUrl;
        this.containPages = containPages;
        this.pageNum = pageNum;
    }


    @Override
    protected List<String> doInBackground(String... strings) {
        mUrlString = mUrlString+additionalUrl + Config.PREFIX_API_KEY + Config.THE_MOVIE_DB_API_KEY;
        if(containPages) {
            page += pageNum;
            mUrlString += page;
        }
        List<String> jsonData = new ArrayList<String>();
        URL url = null;
        try {
            url = new URL(mUrlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream is = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();

            String line = null;
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
            Log.i(LOG_TAG,"URL Queried: " + mUrlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return jsonData;
    }

    private void parseJsonData(List<String> jsonArray) {
        try {
            JSONObject jo = new JSONObject(result);
            if(containPages&&additionalUrl==MainActivity.additionalUrl){
                TabNowShowingFragment.totalPages = jo.getInt("total_pages");
            }
            JSONArray jArray = jo.getJSONArray("results");
            jsonArray.clear();

            if(additionalUrl==MainActivity.additionalUrl) TabNowShowingFragment.moviesList.clear();
            if(additionalUrl.contains("similar")) DetailActivity.DetailFragment.moviesList.clear();

            int len = jArray.length();
            if(jArray.length()==1) len=2;
            for (int i = 0; i < len-1; i++) {
                String s = "";
                JSONObject json = jArray.getJSONObject(i);
                if (additionalUrl == MainActivity.additionalUrl||additionalUrl==
                        DetailActivity.DetailFragment.additionalUrl) {
                    String poster_path = json.getString("poster_path");
                    boolean adult = json.getBoolean("adult");
                    String overview = json.getString("overview");
                    String release_date = json.getString("release_date");
                    JSONArray genre = json.getJSONArray("genre_ids");
                    int[] genre_ids = null;
                    if(genre.length()!=0) {
                        genre_ids = new int[genre.length()];
                        for (int j = 0; i < genre.length(); i++) {
                            genre_ids[i] = genre.getInt(i);
                        }
                    }
                    int id = json.getInt("id");
                    String original_title = json.getString("original_title");
                    String original_language = json.getString("original_language");
                    String title = json.getString("title");
                    String backdrop_path = json.getString("backdrop_path");
                    double popularity = json.getDouble("popularity");
                    int vote_count = json.getInt("vote_count");
                    boolean video = json.getBoolean("video");
                    double vote_average = json.getDouble("vote_average");
                    //jsonArray.add(s);
                    if(additionalUrl==MainActivity.additionalUrl) {
                        TabNowShowingFragment.moviesList.add(new Movie(adult, backdrop_path, genre_ids, id, original_language, original_title,
                                overview, release_date, poster_path, popularity, title, video, vote_average, vote_count));
                    }
                    else{
                        DetailActivity.DetailFragment.moviesList.add(new Movie(adult, backdrop_path, genre_ids, id, original_language, original_title,
                                overview, release_date, poster_path, popularity, title, video, vote_average, vote_count));
                    }
                }
                else{
                    String id = json.getString("id");
                    String iso_639_1 = json.getString("iso_639_1");
                    String key = json.getString("key");
                    String name = json.getString("name");
                    String site =  json.getString("site");
                    int size = json.getInt("size");
                    String type = json.getString("type");
                    DetailActivity.DetailFragment.trailersList.add(new Video(id,iso_639_1,key,name,site,size,type));
                }
            }

            if(additionalUrl==MainActivity.additionalUrl) TabNowShowingFragment.mMovieImageAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            // TODO: handle exception
            Log.e("log_tag", "Error Parsing Data " + e.toString());
        }
    }

    @Override
    protected void onPostExecute(List<String> strings) {
    }
}
