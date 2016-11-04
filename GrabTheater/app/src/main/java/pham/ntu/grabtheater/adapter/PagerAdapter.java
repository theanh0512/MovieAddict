package pham.ntu.grabtheater.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import pham.ntu.grabtheater.TabFavouritesFragment;
import pham.ntu.grabtheater.TabNowShowingFragment;

/**
 * Created by Administrator PC on 3/31/2016.
 */
public class PagerAdapter extends FragmentStatePagerAdapter {
    Fragment currentFragment;
    int mNumOfTabs;
    Context mContext;
    SparseArray<Fragment> registeredFragments = new SparseArray<>();

    public PagerAdapter(FragmentManager fm, int NumOfTabs, Context context) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        mContext = context;
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
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
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
