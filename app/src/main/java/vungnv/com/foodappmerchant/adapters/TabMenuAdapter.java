package vungnv.com.foodappmerchant.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import vungnv.com.foodappmerchant.ui.manager_menu.ManageMenuActivatedFragment;
import vungnv.com.foodappmerchant.ui.manager_menu.ManageMenuNoActivatedFragment;

public class TabMenuAdapter extends FragmentStateAdapter {
    public TabMenuAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = null;
        switch (position){
            case 0:
                fragment = ManageMenuActivatedFragment.newInstance();
                break;
            case 1:
                fragment = ManageMenuNoActivatedFragment.newInstance();
                break;
        }
        assert fragment != null;
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }

}
