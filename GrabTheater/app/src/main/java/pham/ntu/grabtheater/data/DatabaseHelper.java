package pham.ntu.grabtheater.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import pham.ntu.grabtheater.data.MovieContract.MovieEntry;
import pham.ntu.grabtheater.data.MovieContract.VideoEntry;

/**
 * Created by Pham on 16/10/2016.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    static final String DATABASE_NAME = "movie.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieEntry.COLUMN_MOVIE_ID + " INTEGER UNIQUE NOT NULL, " +
                MovieEntry.COLUMN_IS_ADULT + " INTEGER, " +
                MovieEntry.COLUMN_BACKDROP_PATH + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_GENRE_IDS + " TEXT " +
                MovieEntry.COLUMN_ORIGINAL_LANGUAGE + " TEXT " +
                MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT " +
                MovieEntry.COLUMN_OVERVIEW + " TEXT " +
                MovieEntry.COLUMN_RELEASE_DATE + " TEXT " +
                MovieEntry.COLUMN_POSTER_PATH + " TEXT " +
                MovieEntry.COLUMN_POPULARITY + " REAL " +
                MovieEntry.COLUMN_TITLE + " TEXT " +
                MovieEntry.COLUMN_HAS_VIDEO + " INTEGER " +
                MovieEntry.COLUMN_VOTE_AVERAGE + " REAL " +
                MovieEntry.COLUMN_VOTE_COUNT + " INTEGER " +
                " );";

        final String SQL_CREATE_VIDEO_TABLE = "CREATE TABLE " + VideoEntry.TABLE_NAME + " (" +
                VideoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                //id associated with movie
                VideoEntry.COLUMN_MOVIE_KEY + " INTEGER NOT NULL, " +
                VideoEntry.COLUMN_VIDEO_ID + " TEXT NOT NULL, " +
                VideoEntry.COLUMN_ISO + " TEXT NOT NULL, " +
                VideoEntry.COLUMN_VIDEO_KEY + " TEXT NOT NULL," +
                VideoEntry.COLUMN__VIDEO_NAME + " TEXT NOT NULL, " +
                VideoEntry.COLUMN_VIDEO_SITE + " TEXT NOT NULL, " +
                VideoEntry.COLUMN_VIDEO_SIZE + " INT NOT NULL, " +
                VideoEntry.COLUMN_VIDEO_TYPE + " TEXT NOT NULL, " +

                // Set up the location column as a foreign key to location table.
                " FOREIGN KEY (" + VideoEntry.COLUMN_MOVIE_KEY + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry._ID + ");";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_VIDEO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + VideoEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
