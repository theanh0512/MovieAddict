package pham.ntu.grabtheater;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity implements TabNowShowingFragment.OnFragmentInteractionListener, TabFavouritesFragment.OnFragmentInteractionListener, TabNowShowingFragment.ItemsListClickHandler {

    public static String additionalUrl = "now_playing";
    boolean dualPane;
    View detailFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        detailFrame = findViewById(R.id.detail_frame);
        dualPane = (detailFrame != null && detailFrame.getVisibility() == View.VISIBLE);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        MainActivity.additionalUrl = preferences.getString(getString(R.string.pref_sort_types_key), getString(R.string.pref_sort_types_nowplaying));
        if (!dualPane) {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);

            if (MainActivity.additionalUrl.equals(getString(R.string.pref_sort_types_nowplaying)))
                tabLayout.addTab(tabLayout.newTab().setText(R.string.pref_sort_types_label_nowplaying));
            else if (MainActivity.additionalUrl.equals(getString(R.string.pref_sort_types_popular)))
                tabLayout.addTab(tabLayout.newTab().setText(R.string.pref_sort_types_label_popular));
            else
                tabLayout.addTab(tabLayout.newTab().setText(R.string.pref_sort_types_label_toprated));
            tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.title_tab_favourites)));
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

            final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
            final PagerAdapter adapter = new PagerAdapter
                    (getSupportFragmentManager(), tabLayout.getTabCount());
            viewPager.setAdapter(adapter);
            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    viewPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

//    //Check whether there is an internet connection
//    public boolean isOnline() {
//        ConnectivityManager cm =
//                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo netInfo = cm.getActiveNetworkInfo();
//        return netInfo != null && netInfo.isConnectedOrConnecting();
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onHandleItemClick(int position) {
        Movie movie = TabNowShowingFragment.moviesList.get(position);
        Bundle bundle = new Bundle();
        bundle.putParcelable("Movie", movie);
        if (dualPane) {
            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(bundle);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.detail_frame, detailFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class).putExtra("Bundle", bundle);
            startActivity(intent);
        }
    }
}
