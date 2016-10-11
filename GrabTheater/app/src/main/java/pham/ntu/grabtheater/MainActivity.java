package pham.ntu.grabtheater;

import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.provider.SyncStateContract;
import android.support.v4.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements TabNowShowingFragment.OnFragmentInteractionListener, TabFavouritesFragment.OnFragmentInteractionListener {

    public static String additionalUrl = "now_playing";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(isOnline()) {
            setContentView(R.layout.activity_main);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            String additionalUrl = preferences.getString(getString(R.string.pref_sort_types_key), getString(R.string.pref_sort_types_nowplaying));
            MainActivity.additionalUrl = additionalUrl;

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
        else{
            try {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.alert_title))
                        .setMessage(getString(R.string.alert_message))
                        .setCancelable(false)
                        .setNegativeButton(getString(R.string.alert_button),new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                finish();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
            catch(Exception e)
            {
                Log.d(MainActivity.class.getSimpleName(), "Show Dialog: "+e.getMessage());
            }
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

    //Check whether there is an internet connection
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

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
}
