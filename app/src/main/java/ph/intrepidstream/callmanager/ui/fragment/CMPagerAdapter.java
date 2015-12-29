package ph.intrepidstream.callmanager.ui.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jayzon on 2015/12/29.
 */
public class CMPagerAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> fragments;
    private List<String> fragmentTitles;

    public CMPagerAdapter(FragmentManager fm) {
        super(fm);

        fragments = new ArrayList<Fragment>();
        fragmentTitles = new ArrayList<String>();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentTitles.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    public void addFragment(Fragment fragment, String title)
    {
        fragments.add(fragment);
        fragmentTitles.add(title);
    }

}
