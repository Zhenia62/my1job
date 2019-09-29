package com.example.m1j.MainMenuActivity.relateToFragment_OnBack;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class BackPressImplimentation implements OnBackPressListener {

    private Fragment parentFragment;

    public BackPressImplimentation(Fragment parentFragment) {
        this.parentFragment = parentFragment;
    }

    @Override
    public boolean onBackPressed() {

        if (parentFragment == null) return false;

        int childCount = parentFragment.getChildFragmentManager().getBackStackEntryCount();

        if (childCount == 0) {
            return false;
        } else {
            FragmentManager childFragmentManager = parentFragment.getChildFragmentManager();
            OnBackPressListener childFragment = (OnBackPressListener) childFragmentManager.getFragments().get(0);


            if (!childFragment.onBackPressed()) {
                childFragmentManager.popBackStackImmediate();

            }

            return true;
        }
    }
}