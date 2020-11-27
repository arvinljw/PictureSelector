package net.arvin.selector.uis.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Created by arvinljw on 2020/7/22 16:20
 * Function：
 * Desc：
 */
public abstract class BaseFragment extends Fragment {
    protected View root;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(getLayoutId(), null);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    protected <T extends View> T findViewById(@IdRes int id) {
        return root.findViewById(id);
    }

    protected abstract int getLayoutId();

    protected abstract void init();
}
