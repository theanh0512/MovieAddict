package pham.ntu.grabtheater;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;

import pham.ntu.grabtheater.data.DatabaseHelper;
import pham.ntu.grabtheater.data.MovieContract;
import pham.ntu.grabtheater.utils.PollingCheck;

/*
    Students: These are functions and some test data to make it easier to test your database and
    Content Provider.  Note that you'll want your WeatherContract class to exactly match the one
    in our solution to use these as-given.
 */
public class TestUtilities extends AndroidTestCase {
    static final String TEST_LOCATION = "99705";
    static final long TEST_DATE = 1419033600L;  // December 20th, 2014

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    //    /*
//        Students: Use this to create some default weather values for your database tests.
//     */
    static ContentValues createNowPlayingValues(long testMovieRowId) {
        ContentValues nowPlayingValues = new ContentValues();
        nowPlayingValues.put(MovieContract.NơwPlayingEntry.COLUMN_MOVIE_KEY, 123);
        nowPlayingValues.put(MovieContract.NơwPlayingEntry.COLUMN_POSITION, 0);
        nowPlayingValues.put(MovieContract.NơwPlayingEntry.COLUMN_PAGE_NUMBER, 1);

        return nowPlayingValues;
    }

    //
//    /*
//        Students: You can uncomment this helper function once you have finished creating the
//        LocationEntry part of the WeatherContract.
//     */
    static ContentValues createMovieValues() {
        // Create a new map of values, where column names are the keys
        ContentValues movieValues = new ContentValues();
        int[] arr = {1, 2, 3, 4};
        String arrString = GetDataTask.convertArrayToString(arr);
        movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, 123);
        movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, "test");
        movieValues.put(MovieContract.MovieEntry.COLUMN_IS_ADULT, 1);
        movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, "test");
        movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, "test");
        movieValues.put(MovieContract.MovieEntry.COLUMN_GENRE_IDS, arrString);
        movieValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, "test");
        movieValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE, "test");
        movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, "test");
        movieValues.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH, "test");
        movieValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, 22.22);
        movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, 1111);
        movieValues.put(MovieContract.MovieEntry.COLUMN_HAS_VIDEO, 1);
        movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, 11.11);

        return movieValues;
    }

    //
//    /*
//        Students: You can uncomment this function once you have finished creating the
//        LocationEntry part of the WeatherContract as well as the WeatherDbHelper.
//     */
    static long insertTestMovieValues(Context context) {
        // insert our test records into the database
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createMovieValues();

        long testMovieRowId;
        testMovieRowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert North Pole Location Values", testMovieRowId != -1);

        return testMovieRowId;
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }

    /*
        Students: The functions we provide inside of TestProvider use this utility class to test
        the ContentObserver callbacks using the PollingCheck class that we grabbed from the Android
        CTS tests.

        Note that this only tests that the onChange function is called; it does not test that the
        correct Uri is returned.
     */
    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }
}
