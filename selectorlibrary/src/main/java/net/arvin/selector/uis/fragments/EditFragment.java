package net.arvin.selector.uis.fragments;

import android.os.Bundle;

import net.arvin.selector.R;

import java.util.ArrayList;

/**
 * Created by arvinljw on 18/1/5 15:04
 * Function：
 * Desc：
 */
public class EditFragment extends BaseFragment {
    @Override
    protected int getLayout() {
        return R.layout.ps_fragment_edit;
    }

    @Override
    protected void init() {

    }

    @Override
    public void update(Bundle bundle) {

    }

    @Override
    protected ArrayList<String> getSelectedPictures() {
        return null;
    }

    @Override
    protected ArrayList<String> getSelectedVideos() {
        return null;
    }
}
