package net.arvin.pictureselector.uis.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import net.arvin.pictureselector.entities.ImageEntity;
import net.arvin.pictureselector.uis.fragments.ScaleImageFragment;

import java.util.List;

/**
 * created by arvin on 16/9/3 17:13
 * email：1035407623@qq.com
 */
public class ReviewAdapter extends FragmentStatePagerAdapter {
    private List<ImageEntity> mItems;

    public ReviewAdapter(FragmentManager fm, List<ImageEntity> items) {
        super(fm);
        this.mItems = items;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Fragment getItem(int position) {
        return new ScaleImageFragment(mItems.get(position));
    }
}
