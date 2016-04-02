package pham.ntu.grabtheater;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Administrator PC on 3/31/2016.
 */
public class PagerAdapter extends FragmentStatePagerAdapter {
    Fragment currentFragment;
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs){
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                TabNowShowingFragment tabNowShowing = new TabNowShowingFragment();
                currentFragment = tabNowShowing;
                return tabNowShowing;
            case 1:
                TabFavouritesFragment tabFavourites = new TabFavouritesFragment();
                currentFragment = tabFavourites;
                return tabFavourites;
            default:
                return null;
        }
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    public Fragment getCurrentFragment() {
        return currentFragment;
    }
}
