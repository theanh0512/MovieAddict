package pham.ntu.grabtheater.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by Pham on 19/10/2016.
 */

public class MovieProvider extends ContentProvider {
    static final int MOVIE = 100;
    static final int VIDEO_IN_MOVIE = 101;
    static final int VIDEO = 300;
    static final int NOW_PLAYING_WITH_PAGE_NUMBER = 401;
    static final int NOW_PLAYING = 400;
    static final int POPULAR = 202;
    static final int TOP_RATED = 203;
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final SQLiteQueryBuilder sVideoInMovieQueryBuilder;
    private static final SQLiteQueryBuilder sPopularQueryBuilder;
    private static final SQLiteQueryBuilder sTopRatedQueryBuilder;
    private static final SQLiteQueryBuilder sNowPlayingQueryBuilder;
    private static final String sMovieIDSelection =
            MovieContract.VideoEntry.TABLE_NAME +
                    "." + MovieContract.VideoEntry.COLUMN_MOVIE_KEY + " = ? ";
    private static final String sNowPlayingSelection =
            MovieContract.NơwPlayingEntry.TABLE_NAME +
                    "." + MovieContract.NơwPlayingEntry.COLUMN_PAGE_NUMBER + " = ? ";
    private static final String sPopularSelection =
            MovieContract.PopularEntry.TABLE_NAME +
                    "." + MovieContract.PopularEntry.COLUMN_PAGE_NUMBER + " = ? ";
    private static final String sTopRatedSelection =
            MovieContract.TopRatedEntry.TABLE_NAME +
                    "." + MovieContract.TopRatedEntry.COLUMN_PAGE_NUMBER + " = ? ";

    static {
        sVideoInMovieQueryBuilder = new SQLiteQueryBuilder();

        sVideoInMovieQueryBuilder.setTables(
                MovieContract.VideoEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.MovieEntry.TABLE_NAME +
                        " ON " + MovieContract.VideoEntry.TABLE_NAME +
                        "." + MovieContract.VideoEntry.COLUMN_MOVIE_KEY +
                        " = " + MovieContract.MovieEntry.TABLE_NAME +
                        "." + MovieContract.MovieEntry._ID);
    }

    static {
        sPopularQueryBuilder = new SQLiteQueryBuilder();

        sPopularQueryBuilder.setTables(
                MovieContract.MovieEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.PopularEntry.TABLE_NAME +
                        " ON " + MovieContract.PopularEntry.TABLE_NAME +
                        "." + MovieContract.PopularEntry.COLUMN_MOVIE_KEY +
                        " = " + MovieContract.MovieEntry.TABLE_NAME +
                        "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID);
    }

    static {
        sTopRatedQueryBuilder = new SQLiteQueryBuilder();

        sTopRatedQueryBuilder.setTables(
                MovieContract.MovieEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.TopRatedEntry.TABLE_NAME +
                        " ON " + MovieContract.TopRatedEntry.TABLE_NAME +
                        "." + MovieContract.TopRatedEntry.COLUMN_MOVIE_KEY +
                        " = " + MovieContract.MovieEntry.TABLE_NAME +
                        "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID);
    }

    static {
        sNowPlayingQueryBuilder = new SQLiteQueryBuilder();

        sNowPlayingQueryBuilder.setTables(
                MovieContract.MovieEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.NơwPlayingEntry.TABLE_NAME +
                        " ON " + MovieContract.NơwPlayingEntry.TABLE_NAME +
                        "." + MovieContract.NơwPlayingEntry.COLUMN_MOVIE_KEY +
                        " = " + MovieContract.MovieEntry.TABLE_NAME +
                        "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID);
    }

    private DatabaseHelper mHelper;

    static UriMatcher buildUriMatcher() {
        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIE);
        matcher.addURI(authority, MovieContract.PATH_VIDEO + "/*", VIDEO_IN_MOVIE);
        matcher.addURI(authority, MovieContract.PATH_VIDEO, VIDEO);
        //nowplaying/1/ -> page 1 items
        matcher.addURI(authority, MovieContract.PATH_NOWPLAYING + "/*", NOW_PLAYING_WITH_PAGE_NUMBER);
        matcher.addURI(authority, MovieContract.PATH_NOWPLAYING, NOW_PLAYING);
        matcher.addURI(authority, MovieContract.PATH_POPULAR, POPULAR);
        matcher.addURI(authority, MovieContract.PATH_TOPRATED, TOP_RATED);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mHelper = new DatabaseHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "video/*"
            case VIDEO_IN_MOVIE: {
                retCursor = getVideoInMovie(uri, projection, sortOrder);
                break;
            }
            // "video"
            case VIDEO: {
                retCursor = mHelper.getReadableDatabase().query(
                        MovieContract.VideoEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "movie"
            case MOVIE: {
                retCursor = mHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            // "nowplaying/*"
            case NOW_PLAYING_WITH_PAGE_NUMBER: {
                retCursor = getNowPlayingItemsInPage(uri, projection, sortOrder);
                break;
            }
            // "nowplaying?page_number=1"
            case NOW_PLAYING: {
                retCursor = getNowPlayingItemsInPage(uri, projection, sortOrder);
                break;
            }
            case POPULAR: {
                retCursor = getPopularItemsInPage(uri, projection, sortOrder);
                break;
            }
            case TOP_RATED: {
                retCursor = getTopRatedItemsInPage(uri, projection, sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    private Cursor getVideoInMovie(Uri uri, String[] projection, String sortOrder) {
        int movieID = MovieContract.VideoEntry.getMovieIDFromUri(uri);

        String[] selectionArgs = new String[]{Integer.toString(movieID)};
        String selection = sMovieIDSelection;

        return sVideoInMovieQueryBuilder.query(mHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getNowPlayingItemsInPage(Uri uri, String[] projection, String sortOrder) {
        int pageNumber = MovieContract.NơwPlayingEntry.getPageNumberFromUri(uri);

        String[] selectionArgs = new String[]{Integer.toString(pageNumber)};
        String selection = sNowPlayingSelection;

        return sNowPlayingQueryBuilder.query(mHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getPopularItemsInPage(Uri uri, String[] projection, String sortOrder) {
        int pageNumber = MovieContract.PopularEntry.getPageNumberFromUri(uri);

        String[] selectionArgs = new String[]{Integer.toString(pageNumber)};
        String selection = sPopularSelection;

        return sPopularQueryBuilder.query(mHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getTopRatedItemsInPage(Uri uri, String[] projection, String sortOrder) {
        int pageNumber = MovieContract.TopRatedEntry.getPageNumberFromUri(uri);

        String[] selectionArgs = new String[]{Integer.toString(pageNumber)};
        String selection = sTopRatedSelection;

        return sTopRatedQueryBuilder.query(mHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIE:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case VIDEO_IN_MOVIE:
                return MovieContract.VideoEntry.CONTENT_ITEM_TYPE;
            case VIDEO:
                return MovieContract.VideoEntry.CONTENT_TYPE;
            case NOW_PLAYING_WITH_PAGE_NUMBER:
                return MovieContract.NơwPlayingEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIE: {
                long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case VIDEO: {
                long _id = db.insert(MovieContract.VideoEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieContract.VideoEntry.buildVideoUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case NOW_PLAYING: {
                long _id = db.insert(MovieContract.NơwPlayingEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieContract.NơwPlayingEntry.buildNowPlayingUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        //to notify any registered observers
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIE:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insertWithOnConflict(MovieContract.MovieEntry.TABLE_NAME, null, value, SQLiteDatabase.CONFLICT_IGNORE);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case NOW_PLAYING:
                db.beginTransaction();
                int returnCount2 = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insertWithOnConflict(MovieContract.NơwPlayingEntry.TABLE_NAME, null, value, SQLiteDatabase.CONFLICT_REPLACE);
                        if (_id != -1) {
                            returnCount2++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount2;
            case POPULAR:
                db.beginTransaction();
                int returnCountPopular = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insertWithOnConflict(MovieContract.PopularEntry.TABLE_NAME, null, value, SQLiteDatabase.CONFLICT_REPLACE);
                        if (_id != -1) {
                            returnCountPopular++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCountPopular;
            case TOP_RATED:
                db.beginTransaction();
                int returnCountTopRated = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insertWithOnConflict(MovieContract.TopRatedEntry.TABLE_NAME, null, value, SQLiteDatabase.CONFLICT_REPLACE);
                        if (_id != -1) {
                            returnCountTopRated++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCountTopRated;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if (null == selection) selection = "1";
        switch (match) {
            case MOVIE:
                rowsDeleted = db.delete(
                        MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case VIDEO:
                rowsDeleted = db.delete(
                        MovieContract.VideoEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case NOW_PLAYING:
                rowsDeleted = db.delete(
                        MovieContract.NơwPlayingEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case POPULAR:
                rowsDeleted = db.delete(
                        MovieContract.PopularEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TOP_RATED:
                rowsDeleted = db.delete(
                        MovieContract.TopRatedEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MOVIE:
                rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case VIDEO:
                rowsUpdated = db.update(MovieContract.VideoEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case NOW_PLAYING:
                rowsUpdated = db.update(MovieContract.NơwPlayingEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case POPULAR:
                rowsUpdated = db.update(MovieContract.PopularEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case TOP_RATED:
                rowsUpdated = db.update(MovieContract.TopRatedEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
