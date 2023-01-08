package vungnv.com.foodappmerchant.ui.account;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import vungnv.com.foodappmerchant.R;

public class MangerAccountFragment extends Fragment {

    public MangerAccountFragment() {

    }
    public static MangerAccountFragment newInstance() {
        return new MangerAccountFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manger_account, container, false);
    }
}