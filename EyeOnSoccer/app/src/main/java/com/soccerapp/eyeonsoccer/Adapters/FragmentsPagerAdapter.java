package com.soccerapp.eyeonsoccer.Adapters;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.soccerapp.eyeonsoccer.Classes.Constants;
import com.soccerapp.eyeonsoccer.Fragments.NewsFragment;
import com.soccerapp.eyeonsoccer.Fragments.TableFragment;
import com.soccerapp.eyeonsoccer.Fragments.WatchFragment;
import com.soccerapp.eyeonsoccer.Fragments.ScheduleFragment;


/**
 * Created by Asad on 17/10/2015.
 */
public class FragmentsPagerAdapter extends FragmentStatePagerAdapter {


    public FragmentsPagerAdapter(android.support.v4.app.FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case Constants.TABLE_FRAGMENT_INDEX:
                return new TableFragment();
            case Constants.SCHEDULE_FRAGMENT_INDEX:
                return new ScheduleFragment();
            case Constants.NEWS_FRAGMENT_INDEX:
                return new NewsFragment();
            case Constants.WATCH_FRAGMENT_INDEX:
                return new WatchFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return Constants.NUMBER_OF_FRAGMENTS;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0)
            return "Table";
        if (position == 1)
            return "Table";
        if (position == 2)
            return "Hoe";
        if (position == 3)
            return "Cat";
        return null;
    }
}
