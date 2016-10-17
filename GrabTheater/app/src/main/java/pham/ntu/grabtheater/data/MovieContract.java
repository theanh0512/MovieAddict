package pham.ntu.grabtheater.data;

import android.provider.BaseColumns;

/**
 * Created by Pham on 16/10/2016.
 */

public class MovieContract {

    /*
        Inner class that defines the contents of the Movie table
     */
    public static final class MovieEntry implements BaseColumns {

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
    }

    /* Inner class that defines the contents of the weather table */
    public static final class VideoEntry implements BaseColumns {

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
    }
}
