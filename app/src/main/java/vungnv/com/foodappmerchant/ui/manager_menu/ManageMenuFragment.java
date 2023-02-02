package vungnv.com.foodappmerchant.ui.manager_menu;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Objects;

import vungnv.com.foodappmerchant.R;
import vungnv.com.foodappmerchant.adapters.TabMenuAdapter;


public class ManageMenuFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private TabMenuAdapter tabMenuAdapter;


    public ManageMenuFragment() {
        // Required empty public constructor
    }

    public static ManageMenuFragment newInstance() {
        return new ManageMenuFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager2 = view.findViewById(R.id.viewPager2);
        tabLayout = view.findViewById(R.id.tabLayout);
        tabMenuAdapter = new TabMenuAdapter(requireActivity());
        viewPager2.setAdapter(tabMenuAdapter);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position){
                    case 0:
                        tab.setText("Đã kích hoạt");
                        break;
                    case 1:
                        tab.setText("Chưa kích hoạt");
                        break;
                }
            }
        });
        tabLayoutMediator.attach();
    }
}