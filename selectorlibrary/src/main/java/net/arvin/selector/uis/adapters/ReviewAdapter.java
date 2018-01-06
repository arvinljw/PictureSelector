package net.arvin.selector.uis.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import net.arvin.selector.entities.FileEntity;
import net.arvin.selector.uis.fragments.ImageFragment;

import java.util.List;

/**
 * Created by arvinljw on 17/12/27 15:30
 * Function：
 * Desc：
 */
public class ReviewAdapter extends FragmentStatePagerAdapter {
    private List<FileEntity> mItems;

    public ReviewAdapter(FragmentManager fm, List<FileEntity> items) {
        super(fm);
        this.mItems = items;
    }

    @Override
    public Fragment getItem(int position) {
        return new ImageFragment(mItems.get(position));
    }

    @Override
    public int getCount() {
        return mItems.size();
    }
}
