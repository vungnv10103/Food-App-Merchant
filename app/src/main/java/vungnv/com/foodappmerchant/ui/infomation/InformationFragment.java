package vungnv.com.foodappmerchant.ui.infomation;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import vungnv.com.foodappmerchant.R;

public class InformationFragment extends Fragment {


    public InformationFragment() {
        // Required empty public constructor
    }
    public static InformationFragment newInstance() {
        return new InformationFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_infomation, container, false);
    }
}