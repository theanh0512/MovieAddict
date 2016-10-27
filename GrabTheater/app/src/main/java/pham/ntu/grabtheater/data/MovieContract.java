package pham.ntu.grabtheater.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Pham on 16/10/2016.
 */

public class MovieContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "pham.ntu.grabtheater";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIE = "movie";
    public static final String PATH_VIDEO = "video";
    public static final String PATH_NOWPLAYING = "nowplaying";
    public static final String PATH_POPULAR = "popular";
    public static final String PATH_TOPRATED = "toprated";

    /*
        Inner class that defines the contents of the Movie table
     */
    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String TABLE_NAME = "movie";

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_IS_ADULT = "is_adult";
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";
        public static final String COLUMN_GENRE_IDS = "genre_ids";
        public static final String COLUMN_ORIGINAL_LANGUAGE = "original_language";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_HAS_VIDEO = "has_video";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_VOTE_COUNT = "vote_count";

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class VideoEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_VIDEO).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VIDEO;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VIDEO;

        public static final String TABLE_NAME = "video";
        // Column with the foreign key into the movie table.
        public static final String COLUMN_MOVIE_KEY = "movie_id";

        public static final String COLUMN_VIDEO_ID = "video_id";
        public static final String COLUMN_ISO = "iso_639_1";
        public static final String COLUMN_VIDEO_KEY = "video_key";
        public static final String COLUMN__VIDEO_NAME = "video_name";
        public static final String COLUMN_VIDEO_SITE = "video_site";
        public static final String COLUMN_VIDEO_SIZE = "video_size";
        public static final String COLUMN_VIDEO_TYPE = "video_type";

        public static Uri buildVideoUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static int getMovieIDFromUri(Uri uri) {
            return Integer.parseInt(uri.getPathSegments().get(1));
        }
    }

    public static final class NơwPlayingEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_NOWPLAYING).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NOWPLAYING;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NOWPLAYING;

        public static final String TABLE_NAME = "nowplaying";
        // Column with the foreign key into the movie table.
        public static final String COLUMN_MOVIE_KEY = "movie_id";

        public static final String COLUMN_PAGE_NUMBER = "page_number";
        public static final String COLUMN_POSITION = "position";

        public static Uri buildNowPlayingUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildNowPlayingPageAndPosition(int page, int pos) {
            return CONTENT_URI.buildUpon().appendQueryParameter(COLUMN_PAGE_NUMBER, String.valueOf(page))
                    .appendQueryParameter(COLUMN_POSITION, String.valueOf(pos)).build();
        }

        public static Uri buildNowPlayingPage(int page) {
            return CONTENT_URI.buildUpon().appendQueryParameter(COLUMN_PAGE_NUMBER, String.valueOf(page)).build();
        }

        public static int getPageNumberFromUri(Uri uri) {
            String pageNumber = uri.getQueryParameter(COLUMN_PAGE_NUMBER);
            if (null != pageNumber && pageNumber.length() > 0) return Integer.parseInt(pageNumber);
            else return 0;
        }
    }

    public static final class PopularEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_POPULAR).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_POPULAR;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_POPULAR;

        public static final String TABLE_NAME = "popular";
        // Column with the foreign key into the movie table.
        public static final String COLUMN_MOVIE_KEY = "movie_id";

        public static final String COLUMN_PAGE_NUMBER = "page_number";
        public static final String COLUMN_POSITION = "position";

        public static Uri buildPopularUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildPopularPage(int page) {
            return CONTENT_URI.buildUpon().appendQueryParameter(COLUMN_PAGE_NUMBER, String.valueOf(page)).build();
        }

        public static int getPageNumberFromUri(Uri uri) {
            String pageNumber = uri.getQueryParameter(COLUMN_PAGE_NUMBER);
            if (null != pageNumber && pageNumber.length() > 0) return Integer.parseInt(pageNumber);
            else return 0;
        }
    }

    public static final class TopRatedEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TOPRATED).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TOPRATED;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TOPRATED;

        public static final String TABLE_NAME = "toprated";
        // Column with the foreign key into the movie table.
        public static final String COLUMN_MOVIE_KEY = "movie_id";

        public static final String COLUMN_PAGE_NUMBER = "page_number";
        public static final String COLUMN_POSITION = "position";

        public static Uri buildTopRatedUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildTopRatedPage(int page) {
            return CONTENT_URI.buildUpon().appendQueryParameter(COLUMN_PAGE_NUMBER, String.valueOf(page)).build();
        }

        public static int getPageNumberFromUri(Uri uri) {
            String pageNumber = uri.getQueryParameter(COLUMN_PAGE_NUMBER);
            if (null != pageNumber && pageNumber.length() > 0) return Integer.parseInt(pageNumber);
            else return 0;
        }
    }

}
